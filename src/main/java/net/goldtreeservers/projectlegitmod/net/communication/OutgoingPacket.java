package net.goldtreeservers.projectlegitmod.net.communication;

import io.netty.buffer.ByteBuf;

public interface OutgoingPacket
{
	ByteBuf getBytes();
}
