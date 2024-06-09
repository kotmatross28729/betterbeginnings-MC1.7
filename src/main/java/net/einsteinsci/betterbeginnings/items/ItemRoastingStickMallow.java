package net.einsteinsci.betterbeginnings.items;


import net.einsteinsci.betterbeginnings.ModMain;
import net.einsteinsci.betterbeginnings.register.RegisterItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemRoastingStickMallow extends Item
{
	boolean isCooked;

	public ItemRoastingStickMallow(boolean cooked)
	{
		if (cooked)
		{
			setUnlocalizedName("roastingStickCookedMallow");
		}
		else
		{
			setUnlocalizedName("roastingStickRawMallow");
		}
		isCooked = cooked;
		//shouldRotateAroundWhenRendering();
		setTextureName(ModMain.MODID + ":" + getUnlocalizedName().substring(5));
		setCreativeTab(ModMain.tabBetterBeginnings);
		setMaxStackSize(10);
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{

		if (isCooked)
		{
			player.inventory.addItemStackToInventory(new ItemStack(RegisterItems.marshmallowCooked));
			player.inventory.addItemStackToInventory(new ItemStack(RegisterItems.roastingStick));
			player.inventory.consumeInventoryItem(stack.getItem());
		}
		else
		{
			player.inventory.addItemStackToInventory(new ItemStack(RegisterItems.marshmallow));
			player.inventory.addItemStackToInventory(new ItemStack(RegisterItems.roastingStick));
			player.inventory.consumeInventoryItem(stack.getItem());
		}
		player.inventoryContainer.detectAndSendChanges();
		return stack;
	}
}
