package com.elduin.gravity_boots.mixin;

import com.elduin.gravity_boots.gravity.GravityHolder;
import com.elduin.gravity_boots.gravity.GravityLogic;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public abstract class PlayerGravityMixin implements GravityHolder {

	@Unique
	private Direction gravityBoots$gravity = Direction.DOWN;

	@Unique
	private int gravityBoots$unsupportedTicks;

	@Override
	public Direction gravityBoots$getGravity() {
		return this.gravityBoots$gravity;
	}

	@Override
	public void gravityBoots$setGravity(Direction direction) {
		this.gravityBoots$gravity = direction;
	}

	@Override
	public int gravityBoots$getUnsupportedTicks() {
		return this.gravityBoots$unsupportedTicks;
	}

	@Override
	public void gravityBoots$setUnsupportedTicks(int ticks) {
		this.gravityBoots$unsupportedTicks = ticks;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void gravityBoots$updateGravity(CallbackInfo ci) {
		GravityLogic.tick((Player) (Object) this);
	}

	@Inject(method = "travel", at = @At("HEAD"), cancellable = true)
	private void gravityBoots$travel(Vec3 input, CallbackInfo ci) {
		Player self = (Player) (Object) this;
		if (!GravityLogic.hasCustomGravity(self) || !GravityLogic.isWearingBoots(self)) {
			return;
		}
		GravityLogic.travel(self, input);
		ci.cancel();
	}
}
