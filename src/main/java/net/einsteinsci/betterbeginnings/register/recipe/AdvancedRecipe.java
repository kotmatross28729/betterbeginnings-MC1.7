package net.einsteinsci.betterbeginnings.register.recipe;

import net.einsteinsci.betterbeginnings.inventory.InventoryWorkbenchAdditionalMaterials;
import net.einsteinsci.betterbeginnings.register.recipe.elements.RecipeElement;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class AdvancedRecipe
{
	/**
	 * How many horizontal slots this recipe is wide.
	 */
	public final int recipeWidth;
	/**
	 * How many vertical slots this recipe uses.
	 */
	public final int recipeHeight;
	/**
	 * Is a array of ItemStack that composes the recipe.
	 */
	public final RecipeElement[] recipeItems;
	/**
	 * Is the ItemStack that you get when craft the recipe.
	 */
	private ItemStack recipeOutput;

	// additional materials in the side slots
	private RecipeElement[] addedMaterials;

	// ...something...
	private boolean strangeFlag;

	public final boolean hideFromNEI;

	public AdvancedRecipe(int width, int height, RecipeElement[] items, ItemStack output,
	                      RecipeElement[] materials, boolean hide)
	{
		recipeWidth = width;
		recipeHeight = height;
		recipeItems = items;
		recipeOutput = output;
		addedMaterials = materials;
		hideFromNEI = hide;
	}

	// @Override
	public boolean matches(InventoryCrafting invCrafting, InventoryWorkbenchAdditionalMaterials materials,
	                       World world)
	{
		for (int i = 0; i <= 3 - recipeWidth; ++i)
		{
			for (int j = 0; j <= 3 - recipeHeight; ++j)
			{
				if (checkMatch(invCrafting, materials, i, j, true))
				{
					return true;
				}

				if (checkMatch(invCrafting, materials, i, j, false))
				{
					return true;
				}
			}
		}

		return false;
	}

	// Not sure what that fourth flag is...
	private boolean checkMatch(InventoryCrafting crafting, InventoryWorkbenchAdditionalMaterials materials, int width,
	                           int height, boolean flag4)
	{
		for (int k = 0; k < 3; ++k)
		{
			for (int l = 0; l < 3; ++l)
			{
				int i1 = k - width;
				int j1 = l - height;
				RecipeElement neededCraftingStack = null;

				if (i1 >= 0 && j1 >= 0 && i1 < recipeWidth && j1 < recipeHeight)
				{
					if (flag4)
					{
						neededCraftingStack = recipeItems[recipeWidth - i1 - 1 + j1 * recipeWidth];
					}
					else
					{
						neededCraftingStack = recipeItems[i1 + j1 * recipeWidth];
					}
				}

				ItemStack craftingStackInQuestion = crafting.getStackInRowAndColumn(k, l);

				if (craftingStackInQuestion != null || neededCraftingStack != null)
				{
					if (craftingStackInQuestion == null && neededCraftingStack != null ||
							craftingStackInQuestion != null && neededCraftingStack == null)
					{
						return false;
					}

					if (!neededCraftingStack.matches(craftingStackInQuestion))
					{
						return false;
					}
				}

				for (RecipeElement requiredMatStack : addedMaterials)
				{
					boolean foundIt = false;
					for (int i2 = 0; i2 < materials.getSizeInventory(); ++i2)
					{
						ItemStack testedMatStack = materials.getStackInSlot(i2);
						if (testedMatStack != null)
						{
							foundIt = requiredMatStack.matchesCheckSize(testedMatStack);
						}

						if (foundIt)
						{
							break;
						}
					}

					if (!foundIt)
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	public boolean matchesMostly(InventoryCrafting invCrafting, World world)
	{
		for (int i = 0; i <= 3 - recipeWidth; ++i)
		{
			for (int j = 0; j <= 3 - recipeHeight; ++j)
			{
				if (checkMatchMostly(invCrafting, i, j, true))
				{
					return true;
				}

				if (checkMatchMostly(invCrafting, i, j, false))
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean checkMatchMostly(InventoryCrafting crafting, int width, int height, boolean flag4)
	{
		for (int k = 0; k < 3; ++k)
		{
			for (int l = 0; l < 3; ++l)
			{
				int i1 = k - width;
				int j1 = l - height;
				RecipeElement neededCraftingStack = null;

				if (i1 >= 0 && j1 >= 0 && i1 < recipeWidth && j1 < recipeHeight)
				{
					if (flag4)
					{
						neededCraftingStack = recipeItems[recipeWidth - i1 - 1 + j1 * recipeWidth];
					}
					else
					{
						neededCraftingStack = recipeItems[i1 + j1 * recipeWidth];
					}
				}

				ItemStack craftingStackInQuestion = crafting.getStackInRowAndColumn(k, l);

				if (craftingStackInQuestion != null || neededCraftingStack != null)
				{
					// If one is null but not the other
					if (craftingStackInQuestion == null || neededCraftingStack == null)
					{
						return false;
					}

					if (!neededCraftingStack.matches(craftingStackInQuestion))
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	public int getNeededMaterialCount(ItemStack material)
	{
		for (RecipeElement stack : addedMaterials)
		{
			if (stack.matches(material))
			{
				return stack.getStackSize();
			}
		}

		return 0;
	}

	public RecipeElement[] getNeededMaterials()
	{
		return addedMaterials;
	}
	
	public RecipeElement[] getRecipeItems() 
	{
		return recipeItems;
	}

	public ItemStack getCraftingResult(InventoryCrafting crafting)
	{
		ItemStack itemstack = getRecipeOutput().copy();

		if (strangeFlag)
		{
			for (int i = 0; i < crafting.getSizeInventory(); ++i)
			{
				ItemStack itemstack1 = crafting.getStackInSlot(i);

				if (itemstack1 != null && itemstack1.hasTagCompound())
				{
					itemstack.setTagCompound((NBTTagCompound)itemstack1.stackTagCompound.copy());
				}
			}
		}

		return itemstack;
	}

	public ItemStack getRecipeOutput()
	{
		return recipeOutput;
	}

	public int getRecipeSize()
	{
		return recipeWidth * recipeHeight;
	}

	public RecipeElement[] getThreeByThree()
	{
		RecipeElement[] res = new RecipeElement[9];

		int y = 0, x = 0;
		int v = 0, u = 0;
		for (int i = 0; i < getRecipeSize(); i++)
		{
			RecipeElement ore = recipeItems[u + v * recipeWidth];

			if (ore != null)
			{
				res[x + y * 3] = ore;
			}

			u++;
			if (u >= recipeWidth)
			{
				u = 0;
				v++;
			}

			x = u;
			y = v;
		}

		return res;
	}

	public AdvancedRecipe func_92100_c()
	{
		strangeFlag = true;
		return this;
	}

	public boolean hasMaterial(ItemStack stack)
	{
		for (RecipeElement s : addedMaterials)
		{
			if (s.matches(stack))
			{
				return true;
			}
		}
		return false;
	}
}
