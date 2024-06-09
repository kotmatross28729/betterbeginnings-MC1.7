package net.einsteinsci.betterbeginnings.event;

import net.einsteinsci.betterbeginnings.ModMain;
import net.einsteinsci.betterbeginnings.config.BBConfig;
import net.einsteinsci.betterbeginnings.items.ItemKnife;
import net.einsteinsci.betterbeginnings.util.ChatUtil;
import net.einsteinsci.betterbeginnings.util.LogUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

public class BlockBreakHelper
{
	// workaround for 1.7.10 version.
	public static class BlockPos
	{
		int x;
		int y;
		int z;

		public BlockPos(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public int getX()
		{
			return x;
		}
		public int getY()
		{
			return y;
		}
		public int getZ()
		{
			return z;
		}

		public void setX(int val)
		{
			x = val;
		}
		public void setY(int val)
		{
			y = val;
		}
		public void setZ(int val)
		{
			z = val;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof BlockPos)
			{
				BlockPos p = (BlockPos)obj;
				return p.x == x && p.y == y && p.z == z;
			}
			return false;
		}

		@Override
		public Object clone()
		{
			return new BlockPos(x, y, z);
		}

		@Override
		public String toString()
		{
			return "(" + x + ", " + y + ", " + z + ")";
		}
	}

	public static final Map<EntityPlayer, BlockPos> brokenOnce = new HashMap<>();

	// returns whether to cancel drops or not
	public static void handleBlockBreaking(BlockEvent.BreakEvent e)
	{
		if (!BBConfig.moduleBlockBreaking)
		{
			return;
		}

		if (e.getPlayer() instanceof FakePlayer)
		{
			return;
		}

		Block block = e.block;
		EntityPlayer player = e.getPlayer();
		ItemStack heldItemStack = player.getHeldItem();

		if (BBConfig.alwaysBreakable.contains(block))
		{
			LogUtil.logDebug("Skipped block-breaking for block '" + block.getUnlocalizedName() +
				"'. Block is marked as always breakable in config.");
			return;
		}

		if (player.capabilities.isCreativeMode)
		{
			return;
		}

		int neededHarvestLevel = block.getHarvestLevel(e.blockMetadata);
		String neededToolClass = block.getHarvestTool(e.blockMetadata);
		int usedHarvestLevel = 0;
		String usedToolClass = null;
		String stackName = heldItemStack != null ? heldItemStack.getDisplayName() : "NULL";

		boolean isConfigPickaxe = false, isConfigAxe = false;

		if (heldItemStack != null)
		{
			for (String toolClass : heldItemStack.getItem().getToolClasses(heldItemStack))
			{
				int hl = heldItemStack.getItem().getHarvestLevel(heldItemStack, toolClass);
				if (hl >= usedHarvestLevel)
				{
					usedHarvestLevel = hl;
					usedToolClass = toolClass;
				}
			}

			if (BBConfig.alsoPickaxes.containsKey(heldItemStack.getItem()))
			{
				usedToolClass = "pickaxe";
				usedHarvestLevel = BBConfig.alsoPickaxes.get(heldItemStack.getItem());
				isConfigPickaxe = true;
			}
			if (BBConfig.alsoAxes.containsKey(heldItemStack.getItem()))
			{
				usedToolClass = "axe";
				usedHarvestLevel = BBConfig.alsoAxes.get(heldItemStack.getItem());
				isConfigAxe = true;
			}
		}

		boolean cancel = false;

		if (heldItemStack != null)
		{
			if (heldItemStack.getItem() instanceof ItemKnife &&
				ItemKnife.getBreakable().contains(block))
			{
				return; // allows knife to do stuff.
			}
		}

		if (neededToolClass == null ||
			neededToolClass.equalsIgnoreCase("shovel") ||
			neededToolClass.equalsIgnoreCase("null"))
		{
			return;
		}

		if (neededToolClass.equalsIgnoreCase(usedToolClass))
		{
			if (usedHarvestLevel < neededHarvestLevel)
			{
				cancel = true;
			}
		}
		else if (neededToolClass.equalsIgnoreCase("pickaxe") && isConfigPickaxe)
		{
			LogUtil.logDebug("Item accepted as pickaxe from config: " + heldItemStack.toString());
		}
		else if (neededToolClass.equalsIgnoreCase("axe") && isConfigAxe)
		{
			LogUtil.logDebug("Item accepted as axe from config: " + heldItemStack.toString());
		}
		else
		{
			if (usedToolClass == null || usedToolClass.equalsIgnoreCase("null"))
			{
				if (e.world.difficultySetting != EnumDifficulty.PEACEFUL && !BBConfig.noDamageOnBadBreak)
				{
					player.attackEntityFrom(new DamageSourceFace(block), 6.0f);
				}

				if (!brokenOnce.containsKey(player) || brokenOnce.get(player) == null ||
					!brokenOnce.get(player).equals(new BlockPos(e.x, e.y, e.z)))
				{
					ChatUtil.sendModChatToPlayer(player, "Almost. Once more should do it."
					/* I18n.format("blockbreak.fail") */);
					brokenOnce.put(player, new BlockPos(e.x, e.y, e.z));

					// skip other notification
					e.setCanceled(true);
					return;
				}
				else
				{
					ChatUtil.sendModChatToPlayer(player, "Ouch! But at least it worked."
					/* I18n.format("blockbreak.success") */);
					brokenOnce.put(player, null);
				}

				LogUtil.log(Level.INFO, "Block break failed for " + stackName + " on " + block.getUnlocalizedName());
				LogUtil.log(Level.INFO, "  Required tool class: " + neededToolClass + ", supplied: " + usedToolClass);
				LogUtil.log(Level.INFO, "  Minimum harvest level: " + neededHarvestLevel + ", supplied: " +
					usedHarvestLevel);
			}
			else
			{
				cancel = true;
			}
		}

		if (cancel)
		{
			LogUtil.log(Level.INFO, "Block break failed for " + stackName + " on " + block.getUnlocalizedName());
			LogUtil.log(Level.INFO, "  Required tool class: " + neededToolClass + ", supplied: " + usedToolClass);
			LogUtil.log(Level.INFO, "  Minimum harvest level: " + neededHarvestLevel + ", supplied: " +
				usedHarvestLevel);

			ChatUtil.sendModChatToPlayer(player, "Wrong tool!" /* I18n.format("blockbreak.wrongtool") */);
			ChatUtil.sendModChatToPlayer(player, "Requires " + getToolLevelName(neededHarvestLevel) + " " +
					neededToolClass + "."
			/* I18n.format("blockbreak.wrongtool.message",
				getToolLevelName(neededHarvestLevel), neededToolClass) */);

			e.setCanceled(true);
		}
	}

	public static String getToolLevelName(int level)
	{
		switch (level)
		{
			case 0:
				return "flint or bone";
			case 1:
				return "stone";
			case 2:
				return "iron";
			case 3:
				return "diamond";
			default:
				return "[LEVEL " + level + "]";
		}
	}
}
