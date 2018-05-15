package net.goldtreeservers.projectlegitmod.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import net.goldtreeservers.projectlegitmod.net.communication.OutgoingPacket;
import net.goldtreeservers.projectlegitmod.utils.NettyUtils;

public class UdpNetworkManager
{
	public static AttributeKey<UdpNetworkManager> ATTRIBUTE_KEY = AttributeKey.valueOf("UDP-NETWORK-MANAGER");
	
	private EventLoopGroup bossGroup;
	
	@Getter private UdpPacketManager packetManager;
	
	private InetSocketAddress target;
	
	@Getter private Channel channel;
	
	public UdpNetworkManager(InetAddress address, int port)
	{
		this.bossGroup = NettyUtils.createEventLoopGroup();
		
		this.packetManager = new UdpPacketManager();
		
		this.target = new InetSocketAddress(address, port);
	}
	
	public ChannelFuture start()
	{
		Bootstrap boostrap = new Bootstrap()
				.group(this.bossGroup)
				.channel(NettyUtils.getDatagramChannel())
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(new UdpPacketHandler(this));
		
		return boostrap.bind(0).addListener(new GenericFutureListener<ChannelFuture>()
		{
			@Override
			public void operationComplete(ChannelFuture future) throws Exception
			{
				UdpNetworkManager.this.channel = future.channel();
			}
		});
	}
	
	public void sendPacketUnreliable(OutgoingPacket packet)
	{
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(0); //Packet type
		buf.writeBytes(packet.getBytes());
		
		this.channel.writeAndFlush(new DatagramPacket(buf, this.target));
	}
	
	public void shutdown()
	{
		this.channel.close();
		this.channel = null;
	}
}
