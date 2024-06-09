package net.einsteinsci.betterbeginnings.crafttweaker;

import minetweaker.MineTweakerAPI;
public class MineTweakerCompat 
{
	public static void register() 
	{
		MineTweakerAPI.registerClass(KilnTweaker.class);
		MineTweakerAPI.registerClass(OvenTweaker.class);
		MineTweakerAPI.registerClass(SmelterTweaker.class);
		MineTweakerAPI.registerClass(AdvancedCraftingTweaker.class);
		MineTweakerAPI.registerClass(CampfireTweaker.class);
		//MineTweakerAPI.registerClass(InfusionRepairTweaker.class);
		//MineTweakerAPI.registerClass(RockHammerTweaker.class);
	}
}
