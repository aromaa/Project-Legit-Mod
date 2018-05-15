package net.goldtreeservers.projectlegitmod.net;

import java.util.HashMap;
import java.util.Map;

import net.goldtreeservers.projectlegitmod.net.communication.IncomingPacket;
import net.goldtreeservers.projectlegitmod.net.communication.udp.incoming.ServerAckIncomingPacket;

public class UdpPacketManager
{
	private Map<Byte, IncomingPacket> incomingPackets;
	
	public UdpPacketManager()
	{
		this.incomingPackets = new HashMap<Byte, IncomingPacket>();
		this.incomingPackets.put((byte) 0, new ServerAckIncomingPacket());
	}
	
	public IncomingPacket getIncomingPacket(byte packetId)
	{
		return this.incomingPackets.get(packetId);
	}
}
