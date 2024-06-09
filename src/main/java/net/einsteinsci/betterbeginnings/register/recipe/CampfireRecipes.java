package net.einsteinsci.betterbeginnings.register.recipe;

import java.util.*;
import java.util.Map.Entry;

import net.einsteinsci.betterbeginnings.register.recipe.elements.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CampfireRecipes
{
	private static final CampfireRecipes SMELTINGBASE = new CampfireRecipes();

	private Map<RecipeElement, ItemStack> smeltingList = new HashMap<RecipeElement, ItemStack>();
	private Map<ItemStack, Float> experienceList = new HashMap<ItemStack, Float>();

	private CampfireRecipes()
	{
		// nothing here
	}
	
	public static void removeRecipe(RecipeElement input, ItemStack output)
	{
	    ItemStack result = smelting().smeltingList.get(input);
	    if(smelting().smeltingList.containsKey(input) && ItemStack.areItemStacksEqual(result, output)) return;
	    {
		smelting().experienceList.remove(result);
		smelting().smeltingList.remove(input);
	    }
	}
	
	public static void addRecipe(RecipeElement input, ItemStack output, float xp)
	{
	    smelting().addLists(input, output, xp);
	}
	
	public static void addRecipe(Item input, ItemStack output, float experience)
	{
		smelting().addLists(new StackRecipeElement(new ItemStack(input)), output, experience);
	}
	
	public static void addRecipe(String oreDictEntry, ItemStack output, float experience)
	{
		smelting().addLists(new OreRecipeElement(oreDictEntry, 1), output, experience);
	}

	public void addLists(RecipeElement input, ItemStack itemStack, float experience)
	{
		putLists(input, itemStack, experience);
	}

	public static CampfireRecipes smelting()
	{
		return SMELTINGBASE;
	}

	public void putLists(RecipeElement input, ItemStack itemStack2, float experience)
	{
		smeltingList.put(input, itemStack2);
		experienceList.put(itemStack2, experience);
	}

	public static void addRecipe(Block input, ItemStack output, float experience)
	{
		smelting().addLists(new StackRecipeElement(new ItemStack(Item.getItemFromBlock(input))), output, experience);
	}

	public static void addRecipe(ItemStack input, ItemStack output, float experience)
	{
		smelting().putLists(new StackRecipeElement(input), output, experience);
	}

	public ItemStack getSmeltingResult(ItemStack stack)
	{
		Iterator<Entry<RecipeElement, ItemStack>> iterator = smeltingList.entrySet().iterator();
		Entry<RecipeElement, ItemStack> entry;

		do
		{
			if (!iterator.hasNext())
			{
				return null;
			}

			entry = iterator.next();
		} while (!canBeSmelted(stack, entry.getKey()));

		return (ItemStack)entry.getValue();
	}

	private boolean canBeSmelted(ItemStack stack, RecipeElement ore)
	{
		return ore.matches(stack);
	}
	
	private boolean canBeSmelted(ItemStack stack, ItemStack stack2)
	{
		return stack2.getItem() == stack.getItem()
				&& (stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == stack
				.getItemDamage());
	}

	public float giveExperience(ItemStack stack)
	{
		Iterator<Entry<ItemStack, Float>> iterator = experienceList.entrySet().iterator();
		Entry<ItemStack, Float> entry;

		do
		{
			if (!iterator.hasNext())
			{
				return 0.0f;
			}

			entry = iterator.next();
		} while (!canBeSmelted(stack, (ItemStack)entry.getKey()));

		if (stack.getItem().getSmeltingExperience(stack) != -1)
		{
			return stack.getItem().getSmeltingExperience(stack);
		}

		return entry.getValue();
	}

	public static Map<RecipeElement, ItemStack> getSmeltingList()
	{
		return smelting().smeltingList;
	}
	
	public static Map<ItemStack, Float> getXPList()
	{
		return smelting().experienceList;
	}
}
