package net.einsteinsci.betterbeginnings.register.recipe;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.einsteinsci.betterbeginnings.register.recipe.elements.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class KilnRecipes
{
	private static final KilnRecipes SMELTINGBASE = new KilnRecipes();

	private Map<RecipeElement, ItemStack> smeltingList = Maps.newHashMap();
	private Map<ItemStack, Float> experienceList = Maps.newHashMap();

	private KilnRecipes()
	{
		// nothing here
	}

	public static void addRecipe(RecipeElement input, ItemStack output, float xp)
	{
	    smelting().putLists(input, output, xp);
	}
	
	public static void addRecipe(Item input, ItemStack output, float experience)
	{
		smelting().addLists(input, output, experience);
	}

	public void addLists(Item input, ItemStack itemStack, float experience)
	{
		putLists(new StackRecipeElement(new ItemStack(input)), itemStack, experience);
	}

	public static KilnRecipes smelting()
	{
		return SMELTINGBASE;
	}

	public void putLists(RecipeElement input, ItemStack itemStack2, float experience)
	{
		smeltingList.put(input, itemStack2);
		experienceList.put(itemStack2, experience);
	}

	public static void addRecipe(String input, ItemStack output, float experience)
	{
		smelting().putLists(new OreRecipeElement(input, 1), output, experience);
	}

	public static void addRecipe(Block input, ItemStack output, float experience)
	{
		smelting().addLists(Item.getItemFromBlock(input), output, experience);
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

		return entry.getValue();
	}
	
	private boolean canBeSmelted(ItemStack stack, RecipeElement oreRecipeElement)
	{
		return oreRecipeElement.matches(stack);
	}

	private boolean canBeSmelted(ItemStack stack, ItemStack stack2)
	{
		return stack2.getItem() == stack.getItem()
				&& (stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == stack
				.getItemDamage());
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

		return (Float)entry.getValue();
	}

	public static Map<RecipeElement, ItemStack> getSmeltingList()
	{
		return smelting().smeltingList;
	}

	public static Map<ItemStack, Float> getXPList()
	{
		return smelting().experienceList;
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
}
