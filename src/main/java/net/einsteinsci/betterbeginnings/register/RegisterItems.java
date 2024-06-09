package net.einsteinsci.betterbeginnings.register;

import cpw.mods.fml.common.registry.GameRegistry;
import net.einsteinsci.betterbeginnings.config.BBConfig;
import net.einsteinsci.betterbeginnings.items.*;
import net.einsteinsci.betterbeginnings.items.ItemBBCloth;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

public class RegisterItems
{
	public static final ToolMaterial noobWood = EnumHelper.addToolMaterial("noobwood", 0, 60, 2.0f, -4, 35);

	public static final Item noobWoodSword = new NoobWoodSword(noobWood);
	public static final Item flintKnife = new ItemKnifeFlint();
	public static final Item boneKnife = new ItemKnifeBone();
	public static final Item ironKnife = new ItemKnifeIron();
	public static final Item diamondKnife = new ItemKnifeDiamond();
	public static final Item goldKnife = new ItemKnifeGold();
	public static final Item flintHatchet = new ItemFlintHatchet();
	public static final Item bonePickaxe = new ItemBonePickaxe();
	public static final Item boneShard = new ItemBoneShard();
	public static final Item testItem = new ItemTestItem();
	public static final Item silk = new ItemSilk();
	public static final Item thread = new ItemThread();
	public static final Item cloth = new ItemBBCloth();
	public static final Item leatherStrip = new ItemLeatherStrip();
	public static final Item ironNugget = new ItemIronNugget();
	public static final Item charredMeat = new ItemCharredMeat();
	public static final Item fireBow = new ItemFireBow();
	public static final Item marshmallow = new ItemMarshmallow();
	public static final Item marshmallowCooked = new ItemMarshmallowCooked();
	public static final Item roastingStick = new ItemRoastingStick();
	public static final Item roastingStickRawMallow = new ItemRoastingStickMallow(false);
	public static final Item roastingStickCookedMallow = new ItemRoastingStickMallow(true);
	public static final Item twine = new ItemTwine();
	public static final Item rockHammer = new ItemRockHammer(ToolMaterial.IRON);
	public static final Item pan = new ItemPan();
	public static final Item rotisserie = new ItemRotisserie();

	public static void register()
	{
		RegisterHelper.registerItem(flintKnife);
		RegisterHelper.registerItem(boneKnife);
		RegisterHelper.registerItem(ironKnife);
		RegisterHelper.registerItem(goldKnife);
		RegisterHelper.registerItem(diamondKnife);

		RegisterHelper.registerItem(flintHatchet);
		RegisterHelper.registerItem(bonePickaxe);

		RegisterHelper.registerItem(boneShard);
		RegisterHelper.registerItem(testItem);
		RegisterHelper.registerItem(silk);
		RegisterHelper.registerItem(thread);
		RegisterHelper.registerItem(cloth);
		RegisterHelper.registerItem(twine);
		RegisterHelper.registerItem(leatherStrip);
		RegisterHelper.registerItem(ironNugget);
		RegisterHelper.registerItem(charredMeat);
		RegisterHelper.registerItem(fireBow);
		RegisterHelper.registerItem(rockHammer);
		RegisterHelper.registerItem(pan);
		RegisterHelper.registerItem(rotisserie);

		RegisterHelper.registerItem(marshmallow);
		RegisterHelper.registerItem(roastingStick);
		RegisterHelper.registerItem(marshmallowCooked);
		RegisterHelper.registerItem(roastingStickCookedMallow);
		RegisterHelper.registerItem(roastingStickRawMallow);

		RegisterHelper.registerItem(noobWoodSword);

		oreDictRegistry();
	}

	public static void oreDictRegistry()
	{
		OreDictionary.registerOre("nuggetIron", ironNugget);

		OreDictionary.registerOre("itemKnife", new ItemStack(flintKnife, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("itemKnife", new ItemStack(boneKnife, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("itemKnife", new ItemStack(ironKnife, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("itemKnife", new ItemStack(goldKnife, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("itemKnife", new ItemStack(diamondKnife, 1, OreDictionary.WILDCARD_VALUE));

		OreDictionary.registerOre("itemString", new ItemStack(Items.string));
		OreDictionary.registerOre("itemString", new ItemStack(thread));
		OreDictionary.registerOre("itemString", new ItemStack(twine));

		OreDictionary.registerOre("itemStringTough", new ItemStack(Items.string));
		OreDictionary.registerOre("itemStringTough", new ItemStack(twine));
	}

	public static void tweakVanilla()
	{
		((ItemFood)GameRegistry.findItem("minecraft", "beef")).setPotionEffect(17, 20, 0, 20);
		((ItemFood)GameRegistry.findItem("minecraft", "porkchop")).setPotionEffect(17, 25, 0, 25);
		((ItemFood)GameRegistry.findItem("minecraft", "fish")).setPotionEffect(17, 30, 1, 60); // Both fish types here

		if (BBConfig.makeStuffStackable)
		{
			GameRegistry.findItem("minecraft", "minecart").setMaxStackSize(16);
			GameRegistry.findItem("minecraft", "wooden_door").setMaxStackSize(16);
			GameRegistry.findItem("minecraft", "iron_door").setMaxStackSize(16);
			GameRegistry.findItem("minecraft", "potion").setMaxStackSize(16);
		}
	}
}
