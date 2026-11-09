package com.elduin.gravity_boots.platform.fabric;

//? fabric {

import com.elduin.gravity_boots.GravityBoots;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		GravityBoots.onInitializeClient();
	}

}
//?}
