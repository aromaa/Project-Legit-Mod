package net.goldtreeservers.projectlegitmod.net.communication.tcp.incoming;

import io.netty.buffer.ByteBuf;
import net.goldtreeservers.projectlegitmod.net.ServerConnection;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StartUdpProtocolPluginMessage implements IMessage
{
	public StartUdpProtocolPluginMessage()
	{
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
	}

	public static class Handler implements IMessageHandler<StartUdpProtocolPluginMessage, IMessage>
	{
		@Override
		public IMessage onMessage(final StartUdpProtocolPluginMessage message, MessageContext ctx)
		{
			ServerConnection.getInstance().start();
			
			return null;
		}
	}
}
