package net.einsteinsci.betterbeginnings.inventory;

import cpw.mods.fml.common.FMLCommonHandler;
import net.einsteinsci.betterbeginnings.items.ItemKnife;
import net.einsteinsci.betterbeginnings.register.recipe.AdvancedRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class SlotAdvancedCrafting extends Slot
{
	/**
	 * The craft matrix inventory linked to this result slot.
	 */
	private final IInventory craftMatrix;
	/**
	 * The added materials necessary for advanced crafting.
	 */
	private final IInventory additionalMaterials;
	/**
	 * The player that is using the GUI where this slot resides.
	 */
	private EntityPlayer thePlayer;
	/**
	 * The number of items that have been crafted so far. Gets passed to
	 * ItemStack.onCrafting before being reset.
	 */
	private int amountCrafted;

	private ContainerDoubleWorkbench container;

	public SlotAdvancedCrafting(EntityPlayer player, ContainerDoubleWorkbench container_, IInventory matrix,
		IInventory resultInv, IInventory addedMats, int id, int x, int y)
	{
		super(resultInv, id, x, y);
		thePlayer = player;
		craftMatrix = matrix;
		additionalMaterials = addedMats;
		container = container_;
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes,
	 * not ore and wood. Typically increases an internal count then calls
	 * onCrafting(item).
	 */
	@Override
	protected void onCrafting(ItemStack stack, int resultCount)
	{
		amountCrafted += resultCount;
		onCrafting(stack);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes,
	 * not ore and wood.
	 */
	@Override
	protected void onCrafting(ItemStack stack)
	{
		stack.onCrafting(thePlayer.worldObj, thePlayer, amountCrafted);
		amountCrafted = 0;

		if (stack.getItem() == Item.getItemFromBlock(Blocks.crafting_table))
		{
			thePlayer.addStat(AchievementList.buildWorkBench, 1);
		}

		if (stack.getItem() instanceof ItemPickaxe)
		{
			thePlayer.addStat(AchievementList.buildPickaxe, 1);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.furnace))
		{
			thePlayer.addStat(AchievementList.buildFurnace, 1);
		}

		if (stack.getItem() instanceof ItemHoe)
		{
			thePlayer.addStat(AchievementList.buildHoe, 1);
		}

		if (stack.getItem() == Items.bread)
		{
			thePlayer.addStat(AchievementList.makeBread, 1);
		}

		if (stack.getItem() == Items.cake)
		{
			thePlayer.addStat(AchievementList.bakeCake, 1);
		}

		if (stack.getItem() instanceof ItemPickaxe
				&& ((ItemPickaxe)stack.getItem()).func_150913_i() != Item.ToolMaterial.WOOD)
		{
			thePlayer.addStat(AchievementList.buildBetterPickaxe, 1);
		}

		if (stack.getItem() instanceof ItemSword
				&& !((ItemSword)stack.getItem()).getToolMaterialName().equals("noobwood"))
		{
			thePlayer.addStat(AchievementList.buildSword, 1);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table))
		{
			thePlayer.addStat(AchievementList.enchantments, 1);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.bookshelf))
		{
			thePlayer.addStat(AchievementList.bookcase, 1);
		}
	}

	// onCraftingEvent?
	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack resultStack)
	{
		FMLCommonHandler.instance().firePlayerCraftingEvent(player, resultStack, craftMatrix);
		onCrafting(resultStack);

		// Decrease crafted materials
		for (int i = 0; i < craftMatrix.getSizeInventory(); ++i)
		{
			ItemStack ingredientStack = craftMatrix.getStackInSlot(i);

			if (ingredientStack != null)
			{
				if (ingredientStack.getItem() instanceof ItemKnife)
				{
					ingredientStack.damageItem(1, player);
				}
				else
				{
					craftMatrix.decrStackSize(i, 1);
				}

				if (ingredientStack.getItem().hasContainerItem(ingredientStack))
				{
					ItemStack containerStack = ingredientStack.getItem().getContainerItem(ingredientStack);

					if (containerStack != null)
					{
						boolean isDamageable = containerStack.isItemStackDamageable() ||
							containerStack.getItem() instanceof ItemKnife;

						if (isDamageable && containerStack.getItemDamage() > containerStack.getMaxDamage())
						{
							craftMatrix.setInventorySlotContents(i, null);
							MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, containerStack));
						}
						else
						{
							craftMatrix.setInventorySlotContents(i, containerStack);
						}
						continue;
					}

					boolean leavesGrid = ingredientStack.getItem().doesContainerItemLeaveCraftingGrid(ingredientStack);
					boolean addedToInventory = thePlayer.inventory.addItemStackToInventory(containerStack);
					if (!ingredientStack.getItem().doesContainerItemLeaveCraftingGrid(ingredientStack)
							|| !addedToInventory)
					{
						if (craftMatrix.getStackInSlot(i) == null)
						{
							craftMatrix.setInventorySlotContents(i, containerStack);
						}
						else
						{
							thePlayer.dropPlayerItemWithRandomChoice(containerStack, false);
						}
					}
				}
			}
		}

		// Decrease added materials
		for (int i = 0; i < additionalMaterials.getSizeInventory(); ++i)
		{
			ItemStack matStack = additionalMaterials.getStackInSlot(i);

			if (matStack != null)
			{
				int amount = 0;
				AdvancedRecipe advRecipe = container.getLastAdvancedRecipe();

				if (advRecipe != null)
				{
					amount = advRecipe.getNeededMaterialCount(matStack);
				}

				additionalMaterials.decrStackSize(i, amount);

				// Containers
				if (matStack.getItem().hasContainerItem(matStack))
				{
					ItemStack containerStack = matStack.getItem().getContainerItem(matStack);

					if (containerStack != null && containerStack.isItemStackDamageable()
							&& containerStack.getItemDamage() > containerStack.getMaxDamage())
					{
						MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, containerStack));
						continue;
					}

					if (!matStack.getItem().doesContainerItemLeaveCraftingGrid(matStack)
							|| !thePlayer.inventory.addItemStackToInventory(containerStack))
					{
						if (additionalMaterials.getStackInSlot(i) == null)
						{
							additionalMaterials.setInventorySlotContents(i, containerStack);
						}
						else
						{
							thePlayer.dropPlayerItemWithRandomChoice(containerStack, false);
						}
					}
				}
			}
		}
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for
	 * the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		// One way only.
		return false;
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of
	 * the second int arg. Returns the new stack.
	 */
	@Override
	public ItemStack decrStackSize(int amount)
	{
		if (getHasStack())
		{
			amountCrafted += Math.min(amount, getStack().stackSize);
		}

		return super.decrStackSize(amount);
	}
}


// BUFFER
