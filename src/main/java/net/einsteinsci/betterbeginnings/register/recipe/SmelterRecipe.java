package net.einsteinsci.betterbeginnings.register.recipe;

import net.einsteinsci.betterbeginnings.register.recipe.elements.RecipeElement;
import net.minecraft.item.ItemStack;

public class SmelterRecipe
{
	private ItemStack outputStack;
	private RecipeElement input;
	private float experienceGiven;
	private int gravelNeeded;

	private int bonusIfEnder;
	private float bonusChance;

	public SmelterRecipe(ItemStack output, RecipeElement input, float experience, int gravel, int bonus, float chance)
	{
		outputStack = output;
		this.input = input;
		experienceGiven = experience;
		gravelNeeded = gravel;
		bonusIfEnder = bonus;
		bonusChance = chance;
	}

	public ItemStack getOutput()
	{
		return outputStack;
	}

	public RecipeElement getInput()
	{
		return input;
	}

	public float getExperience()
	{
		return experienceGiven;
	}

	public int getGravel()
	{
		return gravelNeeded;
	}

	public int getBonus()
	{
		return bonusIfEnder;
	}

	public float getBonusChance()
	{
		return bonusChance;
	}
}
