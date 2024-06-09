package net.einsteinsci.betterbeginnings.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.einsteinsci.betterbeginnings.ModMain;
import net.einsteinsci.betterbeginnings.tileentity.TileEntityCampfire;
import net.minecraft.entity.player.EntityPlayer;

public class PacketCampfireState implements IMessage
{
	int xPos, yPos, zPos;

	byte state;

	public static class PacketHandler implements IMessageHandler<PacketCampfireState, IMessage>
	{
		@Override
		public IMessage onMessage(PacketCampfireState msg, MessageContext ctx)
		{
			EntityPlayer player = ModMain.proxy.getPlayerFromMessageContext(ctx);

			TileEntityCampfire campfire = (TileEntityCampfire)player.worldObj
				.getTileEntity(msg.xPos, msg.yPos, msg.zPos);

			if (campfire != null)
			{
				campfire.campfireState = msg.state;
			}

			return null;
		}
	}

	public PacketCampfireState()
	{
		xPos = 0;
		yPos = 0;
		zPos = 0;

		state = 0;
	}

	public PacketCampfireState(int x, int y, int z, byte _state)
	{
		xPos = x;
		yPos = y;
		zPos = z;

		state = _state;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		xPos = buf.readInt();
		yPos = buf.readInt();
		zPos = buf.readInt();

		state = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(xPos);
		buf.writeInt(yPos);
		buf.writeInt(zPos);

		buf.writeByte(state);
	}
}
