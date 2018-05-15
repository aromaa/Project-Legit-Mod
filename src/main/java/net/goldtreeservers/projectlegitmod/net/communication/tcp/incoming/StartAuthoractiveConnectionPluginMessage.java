package net.goldtreeservers.projectlegitmod.net.communication.tcp.incoming;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import net.goldtreeservers.projectlegitmod.net.ServerConnection;
import net.goldtreeservers.projectlegitmod.net.UdpNetworkManager;
import net.goldtreeservers.projectlegitmod.net.communication.udp.outgoing.ClientAuthenicateOutgoingPacket;
import net.goldtreeservers.projectlegitmod.utils.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StartAuthoractiveConnectionPluginMessage implements IMessage
{
	@Getter private int version;
	
	@Getter private InetAddress address;
	@Getter private int port;
	
	@Getter private String authenicationToken;
	
	public StartAuthoractiveConnectionPluginMessage()
	{
		
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.version = ByteBufUtils.readVarInt(buf);
		
		this.address = ByteBufUtils.readInetAddress(buf);
		this.port = buf.readUnsignedShort();
		
		this.authenicationToken = ByteBufUtils.readString(buf);
	}

	public static class Handler implements IMessageHandler<StartAuthoractiveConnectionPluginMessage, IMessage>
	{
		@Override
		public IMessage onMessage(final StartAuthoractiveConnectionPluginMessage message, final MessageContext ctx)
		{
			if (message.getVersion() == 0)
			{
				final UdpNetworkManager networkManager = new UdpNetworkManager(message.getAddress(), message.getPort());
				
				ServerConnection.getInstance().reset(networkManager);
				
				UdpNetworkManager oldNetworkManager = ctx.getClientHandler().getNetworkManager().channel().attr(UdpNetworkManager.ATTRIBUTE_KEY).getAndSet(networkManager);
				if (oldNetworkManager != null)
				{
					oldNetworkManager.shutdown();
				}
				
				//Very beautiful
				networkManager.start().addListener(new GenericFutureListener<ChannelFuture>()
				{
					@Override
					public void operationComplete(ChannelFuture future) throws Exception
					{
						Channel channel = future.channel();
						
						final ScheduledFuture<?> scheduleFuture = ctx.getClientHandler().getNetworkManager().channel().eventLoop().scheduleAtFixedRate(new Runnable()
						{
							@Override
							public void run()
							{
								networkManager.sendPacketUnreliable(new ClientAuthenicateOutgoingPacket(message.getAuthenicationToken()));
							}
						}, 0L, 250, TimeUnit.MILLISECONDS);
						
						final ScheduledFuture<?> cancelScheduleFuture = ctx.getClientHandler().getNetworkManager().channel().eventLoop().schedule(new Runnable()
						{
							@Override
							public void run()
							{
								scheduleFuture.cancel(false);
							}
						}, 10, TimeUnit.SECONDS);
						
						channel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>()
						{
							@Override
							public void operationComplete(ChannelFuture future) throws Exception
							{
								networkManager.shutdown();
								
								scheduleFuture.cancel(false);
								cancelScheduleFuture.cancel(false);
								
								Minecraft.getMinecraft().addScheduledTask(new Runnable()
								{
									@Override
									public void run()
									{
										ServerConnection.getInstance().reset(null);
									}
								});
							}
						});
					}
				});
			}
			
			return null;
		}
	}
}
