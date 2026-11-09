package com.elduin.gravity_boots.platform.fabric;

//? fabric {

import com.elduin.gravity_boots.GravityBoots;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		GravityBoots.onInitialize();
		FabricEventSubscriber.registerEvents();
	}
}
//?}
