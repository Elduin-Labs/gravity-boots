package com.elduin.gravity_boots.item;

import com.elduin.gravity_boots.GravityBoots;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;

public final class ModItems {

	public static final ResourceKey<Item> GRAVITY_BOOTS_KEY =
			ResourceKey.create(Registries.ITEM, GravityBoots.id("gravity_boots"));

	/**
	 * Iron-tier boots. Everything they actually do lives in
	 * {@link com.elduin.gravity_boots.gravity.GravityLogic} — the item itself is
	 * just the thing you have to be wearing for it to kick in.
	 */
	public static final Item GRAVITY_BOOTS = new Item(new Item.Properties()
			.humanoidArmor(ArmorMaterials.IRON, ArmorType.BOOTS)
			.rarity(Rarity.RARE)
			.setId(GRAVITY_BOOTS_KEY));

	private ModItems() {
	}

	public static void register() {
		Registry.register(BuiltInRegistries.ITEM, GRAVITY_BOOTS_KEY, GRAVITY_BOOTS);
	}
}
