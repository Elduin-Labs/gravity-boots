package com.elduin.gravity_boots.gravity;

import net.minecraft.core.Direction;

/**
 * Per-player gravity state, mixed into {@code Player}.
 *
 * <p>Which way is "down" for this player right now, plus how long they have
 * been falling without finding anything to stand on.
 */
public interface GravityHolder {

	Direction gravityBoots$getGravity();

	void gravityBoots$setGravity(Direction direction);

	int gravityBoots$getUnsupportedTicks();

	void gravityBoots$setUnsupportedTicks(int ticks);
}
