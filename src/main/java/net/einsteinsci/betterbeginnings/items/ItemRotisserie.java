package net.einsteinsci.betterbeginnings.items;

import net.einsteinsci.betterbeginnings.ModMain;

public class ItemRotisserie extends ItemPan
{
	public ItemRotisserie()
	{
		super();
		setUnlocalizedName("rotisserie");
		setMaxDamage(4);
		setMaxStackSize(1);
		setTextureName(ModMain.MODID + ":" + getUnlocalizedName().substring(5));
		setCreativeTab(ModMain.tabBetterBeginnings);
	}
}
