package net.einsteinsci.betterbeginnings.inventory;

import cpw.mods.fml.common.FMLCommonHandler;
import net.einsteinsci.betterbeginnings.register.InfusionRepairUtil;
import net.einsteinsci.betterbeginnings.register.RegisterItems;
import net.einsteinsci.betterbeginnings.register.achievement.RegisterAchievements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

public class SlotInfusionRepairResult extends SlotCrafting
{
	IInventory inputSlots;

	public SlotInfusionRepairResult(EntityPlayer entityPlayer, IInventory inputs, IInventory output, int slotId, int xPos, int yPos)
	{
		super(entityPlayer, inputs, output, slotId, xPos, yPos);

		inputSlots = inputs;
	}

	public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack resultStack)
	{
		onCrafting(resultStack);

		InventoryInfusionRepair inputs = (InventoryInfusionRepair)inputSlots;

		if (InfusionRepairUtil.isDiffusionMode(inputs))
		{
			inputs.getStackInSlot(0).stackSize--;
			if (inputs.getStackInSlot(0).stackSize <= 0)
			{
				inputs.stackList[0] = null;
			}

			for (int i = 1; i < inputs.getSizeInventory(); i++)
			{
				ItemStack stack = inputs.getStackInSlot(i);
				if (stack == null)
				{
					continue;
				}

				if (stack.getItem() instanceof ItemBook)
				{
					stack.stackSize--;
					if (stack.stackSize <= 0)
					{
						inputs.stackList[i] = null;
					}
				}

				if (stack.getItem() instanceof ItemTool ||
					stack.getItem() instanceof ItemSword ||
					stack.getItem() instanceof ItemArmor)
				{
					NBTTagList enchList = stack.getEnchantmentTagList();
					if (enchList == null)
					{
						continue;
					}

					enchList.removeTag(0); // kill the first enchantment.
					if (enchList.tagCount() == 0)
					{
						stack.getTagCompound().removeTag("ench");
					}

					int dmgAmount = stack.getMaxDamage() / 5;
					int damagedMeta = stack.getItemDamage() + dmgAmount;
					damagedMeta = Math.min(damagedMeta, stack.getMaxDamage() - 1);
					stack.setItemDamage(damagedMeta);
				}
			}

			setBackgroundIcon(null);
		}
		else
		{
			ArrayList<ItemStack> required = InfusionRepairUtil.getRequiredStacks(inputs);

			for (ItemStack requiredStack : required)
			{
				for (int i = 0; i < inputSlots.getSizeInventory(); ++i)
				{
					if (requiredStack != null && inputSlots.getStackInSlot(i) != null)
					{
						if (requiredStack.getItem() == inputSlots.getStackInSlot(i).getItem() &&
							(requiredStack.getItemDamage() == OreDictionary.WILDCARD_VALUE ||
								requiredStack.getItemDamage() == inputSlots.getStackInSlot(i).getItemDamage()))
						{
							inputSlots.decrStackSize(i, requiredStack.stackSize);

							ItemStack itemstack1 = inputSlots.getStackInSlot(i);

							if (itemstack1 != null)
							{
								if (itemstack1.getItem().hasContainerItem(itemstack1))
								{
									ItemStack containerStack = itemstack1.getItem().getContainerItem(itemstack1);

									if (containerStack != null && containerStack.isItemStackDamageable() &&
										containerStack
											.getItemDamage() > containerStack.getMaxDamage())
									{
										MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(entityPlayer,
											containerStack));
										continue;
									}

									if (!itemstack1.getItem().doesContainerItemLeaveCraftingGrid(itemstack1) ||
										!entityPlayer.inventory.addItemStackToInventory(containerStack))
									{
										if (inputSlots.getStackInSlot(i) == null)
										{
											inputSlots.setInventorySlotContents(i, containerStack);
										}
										else
										{
											entityPlayer.dropPlayerItemWithRandomChoice(containerStack, false);
										}
									}
								}
							}

							break;
						}
					}
				}
			}

			if (!entityPlayer.capabilities.isCreativeMode)
			{
				entityPlayer.addExperienceLevel(-InfusionRepairUtil.getTakenLevels(inputs));
			}

			inputSlots.setInventorySlotContents(0, null);

			if (resultStack.getItem() == RegisterItems.noobWoodSword)
			{
				RegisterAchievements.achievementGet(entityPlayer, "repairNoobSword");
			}

			FMLCommonHandler.instance().firePlayerCraftingEvent(entityPlayer, resultStack, inputSlots);
		}
	}
}
