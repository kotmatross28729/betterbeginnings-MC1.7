package net.einsteinsci.betterbeginnings.register.recipe;

import java.util.*;

import net.einsteinsci.betterbeginnings.register.recipe.elements.RecipeElement;
import net.einsteinsci.betterbeginnings.tileentity.TileEntityBrickOven;
import net.einsteinsci.betterbeginnings.tileentity.TileEntityNetherBrickOven;
import net.minecraft.item.ItemStack;

public class BrickOvenShapelessRecipe implements IBrickOvenRecipe
{
	/**
	 * Is a List of ItemStack that composes the recipe.
	 */
	public final List<RecipeElement> recipeItems;
	/**
	 * Is the ItemStack that you get when craft the recipe.
	 */
	private final ItemStack recipeOutput;

	public BrickOvenShapelessRecipe(ItemStack output, List<RecipeElement> input)
	{
		recipeOutput = output;
		recipeItems = input;
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(TileEntityBrickOven oven)
	{
		ArrayList<RecipeElement> arraylist = new ArrayList<RecipeElement>(recipeItems);

		for (int col = 0; col < 3; ++col)
		{
			for (int row = 0; row < 3; ++row)
			{
				ItemStack itemstack = oven.getStackInRowAndColumn(row, col);

				if (itemstack != null)
				{
					boolean flag = false;
					Iterator iterator = arraylist.iterator();

					while (iterator.hasNext())
					{
						RecipeElement itemstack1 = (RecipeElement)iterator.next();

						if (itemstack1 != null && itemstack1.matches(itemstack))
						{
							flag = true;
							arraylist.remove(itemstack1);
							break;
						}
					}

					if (!flag)
					{
						return false;
					}
				}
			}
		}

		return arraylist.isEmpty();
	}

	@Override
	public boolean matches(TileEntityNetherBrickOven oven)
	{
		ArrayList<RecipeElement> arraylist = new ArrayList<RecipeElement>(recipeItems);

		for (int col = 0; col < 3; ++col)
		{
			for (int row = 0; row < 3; ++row)
			{
				ItemStack itemstack = oven.getStackInRowAndColumn(row, col);

				if (itemstack != null)
				{
					boolean flag = false;
					Iterator iterator = arraylist.iterator();

					while (iterator.hasNext())
					{
						RecipeElement itemstack1 = (RecipeElement)iterator.next();

						if (itemstack1.matches(itemstack))
						{
							flag = true;
							arraylist.remove(itemstack1);
							break;
						}
					}

					if (!flag)
					{
						return false;
					}
				}
			}
		}

		return arraylist.isEmpty();
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(TileEntityBrickOven oven)
	{
		return recipeOutput.copy();
	}

	@Override
	public ItemStack getCraftingResult(TileEntityNetherBrickOven oven)
	{
		return recipeOutput.copy();
	}

	/**
	 * Returns the size of the recipe area
	 */
	@Override
	public int getRecipeSize()
	{
		return recipeItems.size();
	}

	@Override
	public boolean contains(ItemStack stack)
	{
		for (RecipeElement ore : recipeItems)
		{
			if (ore.matches(stack))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return recipeOutput;
	}

	@Override
	public RecipeElement[] getInputs()
	{
		List<RecipeElement> buf = new ArrayList<>();
		for (RecipeElement ore : recipeItems)
		{
			if (ore != null)
			{
				buf.add(ore);
			}
		}

		return buf.toArray(new RecipeElement[0]);
	}
}
