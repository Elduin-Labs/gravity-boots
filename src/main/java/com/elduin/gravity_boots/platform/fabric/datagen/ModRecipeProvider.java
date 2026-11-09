package com.elduin.gravity_boots.platform.fabric.datagen;

//? fabric {

import com.elduin.gravity_boots.GravityBoots;
import com.elduin.gravity_boots.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

	public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		return new IntRecipeProvider(provider, recipeOutput);
	}

	@Override
	public @NotNull String getName() {
		return GravityBoots.MOD_ID + ":recipe_provider";
	}

	static class IntRecipeProvider extends RecipeProvider {

		protected IntRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			super(provider, recipeOutput);
		}

		@Override
		public void buildRecipes() {
			final var itemLookup = registries.lookupOrThrow(Registries.ITEM);
			ShapelessRecipeBuilder.shapeless(itemLookup, RecipeCategory.COMBAT, ModItems.GRAVITY_BOOTS)
					.requires(Items.IRON_BOOTS)
					.requires(Items.ENDER_PEARL)
					.requires(Items.ENDER_PEARL)
					.requires(Items.SLIME_BALL)
					.unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
					.unlockedBy("has_iron_boots", has(Items.IRON_BOOTS))
					.save(output, "gravity_boots");
		}
	}
}
//?}
