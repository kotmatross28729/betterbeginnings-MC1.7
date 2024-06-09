package net.einsteinsci.betterbeginnings.register.recipe;

import java.util.*;
import java.util.Map.Entry;

import net.einsteinsci.betterbeginnings.register.recipe.elements.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SmelterRecipeHandler
{
	private static final SmelterRecipeHandler SMELTINGBASE = new SmelterRecipeHandler();

	private Map experienceList = new HashMap();

	private List<SmelterRecipe> recipes = new ArrayList<>();

	private SmelterRecipeHandler()
	{
		// nothing here
	}

	public static SmelterRecipe addRecipe(RecipeElement input, ItemStack output, float experience, int boosters, int bonus, float chance)
	{
		return smelting().putLists(input, output, experience, boosters, bonus, chance);
	}

	public static void addRecipe(Item input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		smelting().addLists(input, output, experience, gravel, bonus, chance);
	}

	public void addLists(Item input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		putLists(new StackRecipeElement(new ItemStack(input)), output, experience, gravel, bonus, chance);
	}

	public static SmelterRecipeHandler smelting()
	{
		return SMELTINGBASE;
	}

	public SmelterRecipe putLists(RecipeElement input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		experienceList.put(output, Float.valueOf(experience));
		SmelterRecipe recipe = new SmelterRecipe(output, input, experience, gravel, bonus, chance);
		recipes.add(recipe);
		return recipe;
	}

	public static void addRecipe(Block input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		smelting().addLists(Item.getItemFromBlock(input), output, experience, gravel, bonus, chance);
	}

	public static void addRecipe(ItemStack input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		smelting().putLists(new StackRecipeElement(input), output, experience, gravel, bonus, chance);
	}

	public static void addRecipe(String oreDict, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		smelting().putLists(new OreRecipeElement(oreDict, 1), output, experience, gravel, bonus, chance);
	}
	
	public static void addRecipe(SmelterRecipe recipe, float experience)
	{
		smelting().recipes.add(recipe);
		smelting().experienceList.put(recipe.getInput(), experience);
	}

	public ItemStack getSmeltingResult(ItemStack input)
	{
		for (SmelterRecipe recipe : recipes)
		{
			if (recipe.getInput().matches(input))
			{
				return recipe.getOutput();
			}
		}

		return null;
	}

	public int getGravelCount(ItemStack stack)
	{
		for (SmelterRecipe recipe : recipes)
		{
			if (recipe.getInput() != null)
			{
				if (recipe.getInput().matches(stack))
				{
					return recipe.getGravel();
				}
			}
		}

		return -1;
	}

	public float giveExperience(ItemStack stack)
	{
		Iterator iterator = experienceList.entrySet().iterator();
		Entry entry;

		do
		{
			if (!iterator.hasNext())
			{
				return 0.0f;
			}

			entry = (Entry)iterator.next();
		} while (!canBeSmelted(stack, (ItemStack)entry.getKey()));

		if (stack.getItem().getSmeltingExperience(stack) != -1)
		{
			return stack.getItem().getSmeltingExperience(stack);
		}

		return ((Float)entry.getValue()).floatValue();
	}

	private boolean canBeSmelted(ItemStack stack, ItemStack stack2)
	{
		return stack2.getItem() == stack.getItem() &&
				(stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == stack
				.getItemDamage());
	}

	public int getBonus(ItemStack input)
	{
		for (SmelterRecipe recipe : recipes)
		{
			if (recipe.getInput().matches(input))
			{
				return recipe.getBonus();
			}
		}

		return 0;
	}

	public float getBonusChance(ItemStack input)
	{
		for (SmelterRecipe recipe : recipes)
		{
			if (recipe.getInput().matches(input))
			{
				return recipe.getBonusChance();
			}
		}

		return 0.0f;
	}

	public static List<SmelterRecipe> getRecipes()
	{
		return smelting().recipes;
	}
	
	public static Map getXPList()
	{
		return smelting().experienceList;
	}

	public static SmelterRecipe removeRecipe(RecipeElement input, ItemStack output)
	{
	    SmelterRecipe recipe;
	    for(Iterator<SmelterRecipe> iter = SmelterRecipeHandler.getRecipes().iterator(); iter.hasNext();)
	    {
		 recipe = iter.next();
		 if(recipe.getInput().equals(input) && ItemStack.areItemStacksEqual(recipe.getOutput(), output))
		 {
		     iter.remove();
		     return recipe;
		 }
	    }
	    return null;
	}
}
