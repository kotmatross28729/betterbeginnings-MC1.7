package net.einsteinsci.betterbeginnings.items;

import net.einsteinsci.betterbeginnings.ModMain;
import net.minecraft.item.Item;

public class ItemBBCloth extends Item
{
	public ItemBBCloth()
	{
		super();

		setUnlocalizedName("cloth");
		setTextureName(ModMain.MODID + ":" + getUnlocalizedName().substring(5));

		setCreativeTab(ModMain.tabBetterBeginnings);
	}
}
