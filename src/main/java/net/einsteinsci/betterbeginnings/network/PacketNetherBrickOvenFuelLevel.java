package net.einsteinsci.betterbeginnings.network;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import net.einsteinsci.betterbeginnings.ModMain;
import net.einsteinsci.betterbeginnings.tileentity.TileEntityNetherBrickOven;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PacketNetherBrickOvenFuelLevel implements IMessage
{
	int xPos, yPos, zPos;

	FluidStack fluid;

	public static class PacketHandler implements IMessageHandler<PacketNetherBrickOvenFuelLevel, IMessage>
	{
		@Override
		public IMessage onMessage(PacketNetherBrickOvenFuelLevel message, MessageContext ctx)
		{
			EntityPlayer player = ModMain.proxy.getPlayerFromMessageContext(ctx);

			TileEntityNetherBrickOven oven = (TileEntityNetherBrickOven)player.worldObj.getTileEntity(
					message.xPos, message.yPos, message.zPos);
			oven.setFuelLevel(message.fluid);

			return null;
		}
	}

	public PacketNetherBrickOvenFuelLevel()
	{
		xPos = 0;
		yPos = 0;
		zPos = 0;

		fluid = null;
	}

	public PacketNetherBrickOvenFuelLevel(int x, int y, int z, FluidStack fuel)
	{
		xPos = x;
		yPos = y;
		zPos = z;

		fluid = fuel;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		xPos = buf.readInt();
		yPos = buf.readInt();
		zPos = buf.readInt();

		int fluidId = buf.readInt();
		int level = buf.readInt();

		if (level != 0)
		{
			fluid = new FluidStack(FluidRegistry.getFluid(fluidId), level);
		}
		else
		{
			fluid = null;
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(xPos);
		buf.writeInt(yPos);
		buf.writeInt(zPos);

		if (fluid != null)
		{
			buf.writeInt(fluid.getFluidID());
			buf.writeInt(fluid.amount);
		}
		else
		{
			buf.writeInt(0);
			buf.writeInt(0);
		}
	}
}
