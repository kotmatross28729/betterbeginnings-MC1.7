package net.einsteinsci.betterbeginnings.crafttweaker;

import java.util.*;

import com.google.common.collect.Lists;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.einsteinsci.betterbeginnings.crafttweaker.util.*;
import net.einsteinsci.betterbeginnings.register.recipe.KilnRecipes;
import net.einsteinsci.betterbeginnings.register.recipe.KilnRecipeWrapper;
import net.einsteinsci.betterbeginnings.register.recipe.elements.RecipeElement;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterbeginnings.Kiln")
public class KilnTweaker
{
    private static final String NAME = "Kiln";
    
    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient input, float xp)
    {
	MineTweakerAPI.apply(new AddKilnRecipe(input, output, xp));
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient input)
    {
	addRecipe(output, input, 0.1F);
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output, IIngredient input)
    {
	MineTweakerAPI.apply(new RemoveKilnRecipe(input, output));
    }	

    @ZenMethod
    public static void removeOutput(IItemStack output)
    {
	MineTweakerAPI.apply(new RemoveKilnOutput(output));
    }

    private static class AddKilnRecipe extends AddRemoveAction
    {
	RecipeElement input; 
	ItemStack output; 
	float xp;

	private AddKilnRecipe(IIngredient input, IItemStack output, float xp)
	{
	    super(ActionType.ADD, NAME);
	    this.input = CraftTweakerUtil.convertToRecipeElement(input);
	    this.output = MineTweakerMC.getItemStack(output);
	    this.xp = xp;
	}

	@Override
	public void apply()
	{	    
	    KilnRecipes.addRecipe(input, output, xp);
	}

	@Override
	public void undo()
	{
	    KilnRecipes.removeRecipe(input, output);
	}

	@Override
	public String recipeToString()
	{
	    return input + " -> " + output;
	}
    }

    private static class RemoveKilnRecipe extends AddRemoveAction
    {
	RecipeElement input; 
	ItemStack output; 
	float xp;

	private RemoveKilnRecipe(IIngredient input, IItemStack output)
	{
	    super(ActionType.REMOVE, NAME);
	    this.input = CraftTweakerUtil.convertToRecipeElement(input);
	    this.output = MineTweakerMC.getItemStack(output);
	    this.xp = KilnRecipes.smelting().giveExperience(this.output);
	}

	@Override
	public void apply()
	{
	    KilnRecipes.removeRecipe(input, output);
	}

	@Override
	public void undo()
	{
	    KilnRecipes.addRecipe(input, output, xp);
	}

	@Override
	public String recipeToString()
	{
	    return input + " -> " + output;
	}
    }

    private static class RemoveKilnOutput extends RemoveOutputAction
    {
	ItemStack targetOutput; 

	List<KilnRecipeWrapper> removedRecipes = Lists.newArrayList();

	private RemoveKilnOutput(IItemStack output)
	{
	    super(NAME);
	    this.targetOutput = MineTweakerMC.getItemStack(output);
	}

	@Override
	public void apply()
	{   
	    Map.Entry<RecipeElement, ItemStack> recipe = null;
	    for(Iterator<Map.Entry<RecipeElement, ItemStack>> iter = KilnRecipes.getSmeltingList().entrySet().iterator(); iter.hasNext();)
	    {
		recipe = iter.next();
		if(targetOutput.isItemEqual(recipe.getValue()) && ItemStack.areItemStackTagsEqual(targetOutput, recipe.getValue()))
		{
		    KilnRecipeWrapper wrapper = new KilnRecipeWrapper(recipe.getKey(), recipe.getValue(), KilnRecipes.smelting().giveExperience(recipe.getValue()));
		    removedRecipes.add(wrapper);
		    iter.remove();
		}
	    }
	}

	@Override
	public void undo()
	{
	    for(KilnRecipeWrapper recipe : removedRecipes)
	    {
		recipe.add();
	    }
	}

	@Override
	public String recipeToString()
	{
	    return targetOutput.toString();
	}
    }
}
