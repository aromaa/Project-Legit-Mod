package net.goldtreeservers.projectlegitmod.net.communication;

import io.netty.channel.socket.DatagramPacket;

public interface IncomingPacket
{
	public void handle(DatagramPacket msg);
}
