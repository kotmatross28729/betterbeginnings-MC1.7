package net.einsteinsci.betterbeginnings.crafttweaker;

import java.util.*;

import com.google.common.collect.Lists;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.einsteinsci.betterbeginnings.crafttweaker.util.*;
import net.einsteinsci.betterbeginnings.register.recipe.*;
import net.einsteinsci.betterbeginnings.register.recipe.elements.RecipeElement;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterbeginnings.Campfire")
public class CampfireTweaker
{
    private static final String NAME = "Smelter";
    
    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient input, float xp)
    {
	MineTweakerAPI.apply(new AddCampfireRecipe(input, output, xp));
    }
    
    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient input)
    {
	addRecipe(output, input, 0.2F);
    }

    @ZenMethod
    public static void addPanRecipe(IItemStack output, IIngredient input, float xp)
    {
	MineTweakerAPI.apply(new AddCampfireRecipe(input, output, xp).setPan(true));
    }

    @ZenMethod
    public static void addPanRecipe(IItemStack output, IIngredient input)
    {
	addPanRecipe(output, input, 0.3F);
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output, IIngredient input)
    {
	MineTweakerAPI.apply(new RemoveCampfireRecipe(input, output));
    }	

    @ZenMethod
    public static void removePanRecipe(IItemStack output, IIngredient input)
    {
	MineTweakerAPI.apply(new RemoveCampfireRecipe(input, output).setPan(true));
    }

    @ZenMethod
    public static void removeOutput(IItemStack output)
    {
	MineTweakerAPI.apply(new RemoveCampfireOutput(output));
    }

    private static class AddCampfireRecipe extends AddRemoveAction
    {
	RecipeElement input; 
	ItemStack output; 
	float xp;
	
	boolean isPan;

	private AddCampfireRecipe(IIngredient input, IItemStack output, float xp)
	{
	    super(ActionType.ADD, NAME);
	    this.input = CraftTweakerUtil.convertToRecipeElement(input);
	    this.output = MineTweakerMC.getItemStack(output);
	    this.xp = xp;
	}

	public AddCampfireRecipe setPan(boolean isPan)
	{
	    this.isPan = isPan;
	    return this;
	}

	@Override
	public void apply()
	{
	    if(isPan)
	    {
		CampfirePanRecipes.addRecipe(input, output, xp);
	    }
	    else
	    {
		CampfireRecipes.addRecipe(input, output, xp);
	    }
	}

	@Override
	public void undo()
	{
	    
	    if(isPan)
	    {
		CampfirePanRecipes.removeRecipe(input, output);
	    }
	    else
	    {
		CampfireRecipes.removeRecipe(input, output);
	    }
	}

	@Override
	public String recipeToString()
	{
	    return input + " -> " + output;
	}
    }
    
    private static class RemoveCampfireRecipe extends AddRemoveAction
    {
	RecipeElement input; 
	ItemStack output; 
	float xp;
	
	boolean isPan;

	private RemoveCampfireRecipe(IIngredient input, IItemStack output)
	{
	    super(ActionType.REMOVE, NAME);
	    this.input = CraftTweakerUtil.convertToRecipeElement(input);
	    this.output = MineTweakerMC.getItemStack(output);
	}

	public RemoveCampfireRecipe setPan(boolean isPan)
	{
	    this.isPan = isPan;
	    return this;
	}

	@Override
	public void apply()
	{
	    if(isPan)
	    {
		CampfirePanRecipes.removeRecipe(input, output);
		xp = CampfirePanRecipes.smelting().giveExperience(output);
	    }
	    else
	    {
		CampfireRecipes.removeRecipe(input, output);
		xp = CampfireRecipes.smelting().giveExperience(output);
	    }
	}

	@Override
	public void undo()
	{
	    if(isPan)
	    {
		CampfirePanRecipes.addRecipe(input, output, xp);
	    }
	    else
	    {
		CampfireRecipes.addRecipe(input, output, xp);
	    }
	}

	@Override
	public String recipeToString()
	{
	    return input + " -> " + output;
	}
    }
    
    private static class RemoveCampfireOutput extends RemoveOutputAction
    {
	ItemStack targetOutput; 
	
	List<CampfireRecipeWrapper> removedRecipes = Lists.newArrayList();

	private RemoveCampfireOutput(IItemStack output)
	{
	    super(NAME);
	    this.targetOutput = MineTweakerMC.getItemStack(output);
	}

	@Override
	public void apply()
	{   
	    Map.Entry<RecipeElement, ItemStack> recipe = null;
	    for(Iterator<Map.Entry<RecipeElement, ItemStack>> iter = CampfireRecipes.getSmeltingList().entrySet().iterator(); iter.hasNext();)
	    {
		recipe = iter.next();
		if(targetOutput.isItemEqual(recipe.getValue()) && ItemStack.areItemStackTagsEqual(targetOutput, recipe.getValue()))
		{
		    CampfireRecipeWrapper wrapper = new CampfireRecipeWrapper(recipe.getKey(), recipe.getValue(), CampfireRecipes.smelting().giveExperience(recipe.getValue()), false); 
		    removedRecipes.add(wrapper);
		    iter.remove();
		}
	    }
	    for(Iterator<Map.Entry<RecipeElement, ItemStack>> iter = CampfirePanRecipes.getSmeltingList().entrySet().iterator(); iter.hasNext();)
	    {
		recipe = iter.next();
		if(targetOutput.isItemEqual(recipe.getValue()) && ItemStack.areItemStackTagsEqual(targetOutput, recipe.getValue()))
		{
		    CampfireRecipeWrapper wrapper = new CampfireRecipeWrapper(recipe.getKey(), recipe.getValue(), CampfirePanRecipes.smelting().giveExperience(recipe.getValue()), true);
		    removedRecipes.add(wrapper);
		    iter.remove();
		}
	    }
	}
	
	@Override
	public void undo()
	{
	    for(CampfireRecipeWrapper recipe : removedRecipes)
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
