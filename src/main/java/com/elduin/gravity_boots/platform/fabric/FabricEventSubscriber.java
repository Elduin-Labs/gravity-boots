package com.elduin.gravity_boots.platform.fabric;

//? fabric {

import com.elduin.gravity_boots.item.ModItems;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class FabricEventSubscriber {

	public static void registerEvents() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
				.register(entries -> entries.accept(ModItems.GRAVITY_BOOTS));
	}
}
//?}
