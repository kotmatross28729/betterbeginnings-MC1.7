package net.einsteinsci.betterbeginnings.register.recipe;

import net.einsteinsci.betterbeginnings.tileentity.TileEntityNetherBrickOven;
import net.minecraft.item.ItemStack;

public class NetherBrickOvenRecipeHandler extends BrickOvenRecipeHandler
{
	private static final NetherBrickOvenRecipeHandler INSTANCE = new NetherBrickOvenRecipeHandler();

	public NetherBrickOvenRecipeHandler()
	{
		super();
	}

	public static NetherBrickOvenRecipeHandler instance()
	{
		return INSTANCE;
	}

	public ItemStack findMatchingRecipe(TileEntityNetherBrickOven oven)
	{
		int i = 0;
		ItemStack itemstack = null;
		ItemStack itemstack1 = null;
		int j;

		for (j = TileEntityNetherBrickOven.INPUTSTART; j < oven.getSizeInventory(); ++j)
		{
			ItemStack itemstack2 = oven.getStackInSlot(j);
			// System.out.println("\nBrickOvenRecipeHandler.findMatchingRecipeResult(): j = " + j);

			if (itemstack2 != null)
			{
				if (i == 0)
				{
					itemstack = itemstack2;
				}

				if (i == 1)
				{
					itemstack1 = itemstack2;
				}

				++i;
			}
		}

		for (IBrickOvenRecipe recipe : BrickOvenRecipeHandler.instance().recipes)
		{
			// IBrickOvenRecipe recipe = (IBrickOvenRecipe)recipes.get(j);

			if (recipe.matches(oven))
			{
				return recipe.getCraftingResult(oven);
			}
		}

		return null;
	}
}
