package com.elduin.gravity_boots.mixin;

import com.elduin.gravity_boots.gravity.GravityLogic;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Standing on a wall means never touching the floor, which the server normally
 * reads as flying and kicks you for. While the boots are holding you to a
 * surface, keep resetting that counter.
 */
@Mixin(ServerGamePacketListenerImpl.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public abstract class ServerFloatingCheckMixin {

	@Shadow
	public ServerPlayer player;

	@Shadow
	private int aboveGroundTickCount;

	@Inject(method = "handleMovePlayer", at = @At("HEAD"))
	private void gravityBoots$allowWallWalking(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
		if (this.player != null
				&& GravityLogic.isWearingBoots(this.player)
				&& GravityLogic.hasCustomGravity(this.player)) {
			this.aboveGroundTickCount = 0;
		}
	}
}
