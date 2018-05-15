package net.goldtreeservers.projectlegitmod.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import net.goldtreeservers.projectlegitmod.net.communication.IncomingPacket;

public class UdpPacketHandler extends SimpleChannelInboundHandler<DatagramPacket>
{
	private UdpNetworkManager networkManager;

	public UdpPacketHandler(UdpNetworkManager networkManager)
	{
		this.networkManager = networkManager;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception
	{
		ByteBuf content = msg.content();
		
		byte packetType = content.readByte();
		if (packetType == 0) //Unreliable
		{
			this.handleCompletePacket(ctx, msg);
		}
		else
		{
			System.out.println("Unknown packet type: " + packetType);
		}
	}
	
	private void handleCompletePacket(ChannelHandlerContext ctx, DatagramPacket msg)
	{
		ByteBuf content = msg.content();
		
		byte packetId = content.readByte();

		IncomingPacket packet = this.networkManager.getPacketManager().getIncomingPacket(packetId);
		if (packet != null)
		{
			packet.handle(msg);
		}
		else
		{
			System.out.println("Unknown packet id: " + packetId);
		}
	}
}
