package com.elduin.gravity_boots.gravity;

import com.elduin.gravity_boots.item.ModItems;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The whole mod, really.
 *
 * <p>A player wearing the boots keeps a "down" of their own — a {@link Direction}
 * that starts out as {@link Direction#DOWN} and changes whenever they walk off an
 * edge next to something solid. Walk off the side of an End island and the island's
 * wall becomes the new floor; walk to the bottom of that wall and the underside
 * becomes the floor after that. Walking back the way you came unwinds it.
 */
public final class GravityLogic {

	/** Pulled into the surface a little every tick, so you stay stuck to it. */
	private static final double STICK = 0.08D;
	/** Same as vanilla's fall acceleration, just aimed wherever "down" is. */
	private static final double FALL_ACCELERATION = 0.08D;
	private static final double TERMINAL_VELOCITY = 3.0D;
	private static final double JUMP_POWER = 0.42D;

	private static final double GROUND_FRICTION = 0.62D;
	private static final double AIR_FRICTION = 0.91D;
	/** Air control, as a fraction of walking speed. */
	private static final float AIR_CONTROL = 0.4F;

	/** How far off the surface counts as still standing on it. */
	private static final double SUPPORT_REACH = 0.32D;
	/**
	 * Don't grab a wall if there is ordinary floor this close underneath — otherwise
	 * hopping about next to a cliff would stick you to it.
	 */
	private static final double SAFE_GROUND_DROP = 2.5D;
	/** Ticks of freefall before we start looking for a wall to grab. */
	private static final int GRAB_DELAY = 3;
	/** Ticks of freefall with nothing found before we give up and fall normally. */
	private static final int RELEASE_DELAY = 8;

	private GravityLogic() {
	}

	public static boolean isWearingBoots(Player player) {
		return player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.GRAVITY_BOOTS);
	}

	/** True when this player's "down" is something other than the usual one. */
	public static boolean hasCustomGravity(Player player) {
		return gravityOf(player) != Direction.DOWN;
	}

	public static Direction gravityOf(Player player) {
		return ((GravityHolder) player).gravityBoots$getGravity();
	}

	private static void setGravity(Player player, Direction direction) {
		GravityHolder holder = (GravityHolder) player;
		holder.gravityBoots$setGravity(direction);
		holder.gravityBoots$setUnsupportedTicks(0);
	}

	// ------------------------------------------------------------------
	// Per-tick: work out what we are standing on
	// ------------------------------------------------------------------

	public static void tick(Player player) {
		GravityHolder holder = (GravityHolder) player;
		Direction gravity = holder.gravityBoots$getGravity();

		// No boots on, or deliberately letting go: fall the normal way.
		if (!isWearingBoots(player) || player.isShiftKeyDown() || player.getAbilities().flying
				|| player.isPassenger() || player.isSpectator()) {
			if (gravity != Direction.DOWN) {
				setGravity(player, Direction.DOWN);
			}
			return;
		}

		if (isSupported(player, gravity)) {
			holder.gravityBoots$setUnsupportedTicks(0);
			player.resetFallDistance();
			return;
		}

		int unsupported = holder.gravityBoots$getUnsupportedTicks() + 1;
		holder.gravityBoots$setUnsupportedTicks(unsupported);

		if (gravity != Direction.DOWN) {
			player.resetFallDistance();
		}

		if (unsupported < GRAB_DELAY) {
			return;
		}

		// Nothing underfoot. Is there a face to wrap onto? Try the four sides
		// first — that is the walk-off-an-edge case — then the far side, which is
		// how you come off the bottom of an island and end up on its underside.
		Direction grabbed = findSurface(player, gravity);
		if (grabbed != null) {
			setGravity(player, grabbed);
			player.resetFallDistance();
			return;
		}

		// Out in open air with nothing to hold. Go back to falling downwards so
		// you don't sail off sideways forever.
		if (unsupported >= RELEASE_DELAY && gravity != Direction.DOWN) {
			setGravity(player, Direction.DOWN);
		}
	}

	private static Direction findSurface(Player player, Direction gravity) {
		// Only grab a wall over a real drop; near the ground, land like normal.
		if (gravity == Direction.DOWN && hasSupportWithin(player, Direction.DOWN, SAFE_GROUND_DROP)) {
			return null;
		}

		for (Direction candidate : Direction.values()) {
			if (candidate.getAxis() == gravity.getAxis()) {
				continue;
			}
			if (isSupported(player, candidate)) {
				return candidate;
			}
		}

		Direction opposite = gravity.getOpposite();
		return isSupported(player, opposite) ? opposite : null;
	}

	/** Is there something solid right against the player's feet, that way? */
	public static boolean isSupported(Player player, Direction direction) {
		AABB box = player.getBoundingBox().deflate(0.02D).move(vec(direction).scale(SUPPORT_REACH));
		return !player.level().noCollision(player, box);
	}

	private static boolean hasSupportWithin(Player player, Direction direction, double distance) {
		for (double step = SUPPORT_REACH; step <= distance; step += 0.5D) {
			AABB box = player.getBoundingBox().deflate(0.02D).move(vec(direction).scale(step));
			if (!player.level().noCollision(player, box)) {
				return true;
			}
		}
		return false;
	}

	// ------------------------------------------------------------------
	// Per-tick: move
	// ------------------------------------------------------------------

	/**
	 * Stands in for vanilla movement while the player's "down" is sideways or
	 * upside down. Walking, jumping and falling all still happen — just along a
	 * different axis.
	 */
	public static void travel(Player player, Vec3 input) {
		Direction gravity = gravityOf(player);
		Vec3 down = vec(gravity);
		Vec3 up = down.scale(-1.0D);
		boolean supported = isSupported(player, gravity);

		// Build a set of directions that lie flat against the surface we're on,
		// starting from where the player is looking.
		Vec3 look = player.getLookAngle();
		Vec3 forward = look.subtract(down.scale(look.dot(down)));
		if (forward.lengthSqr() < 1.0E-4D) {
			// Looking straight along the gravity axis — pick any steady direction
			// so the controls don't spin.
			forward = anyPerpendicular(down);
		}
		forward = forward.normalize();
		Vec3 right = forward.cross(up).normalize();

		Vec3 wish = forward.scale(input.z).subtract(right.scale(input.x));
		if (wish.lengthSqr() > 1.0D) {
			wish = wish.normalize();
		}
		float speed = supported ? player.getSpeed() : player.getSpeed() * AIR_CONTROL;
		wish = wish.scale(speed);

		Vec3 velocity = player.getDeltaMovement();
		double along = velocity.dot(down);
		Vec3 planar = velocity.subtract(down.scale(along));
		planar = planar.scale(supported ? GROUND_FRICTION : AIR_FRICTION).add(wish);

		if (supported) {
			along = player.isJumping() ? -JUMP_POWER : STICK;
		} else {
			along = Math.min(along + FALL_ACCELERATION, TERMINAL_VELOCITY);
		}

		player.setDeltaMovement(planar.add(down.scale(along)));
		player.move(MoverType.SELF, player.getDeltaMovement());

		// Walking into a wall shouldn't leave speed banked up against it.
		if (player.horizontalCollision || player.verticalCollision) {
			Vec3 after = player.getDeltaMovement();
			double keep = after.dot(down);
			Vec3 flat = after.subtract(down.scale(keep));
			player.setDeltaMovement(flat.add(down.scale(keep)));
		}

		player.setOnGround(supported);
		player.resetFallDistance();
	}

	// ------------------------------------------------------------------

	private static Vec3 vec(Direction direction) {
		return new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
	}

	private static Vec3 anyPerpendicular(Vec3 axis) {
		return Math.abs(axis.y) < 0.9D ? new Vec3(0.0D, 1.0D, 0.0D) : new Vec3(1.0D, 0.0D, 0.0D);
	}
}
