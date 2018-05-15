package net.goldtreeservers.projectlegitmod.net.communication.udp.outgoing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.goldtreeservers.projectlegitmod.net.communication.OutgoingPacket;
import net.goldtreeservers.projectlegitmod.utils.ByteBufUtils;

public class ClientAuthenicateOutgoingPacket implements OutgoingPacket
{
	private static final byte HEADER_ID = 0;
	
	private String authenicationToken;
	
	public ClientAuthenicateOutgoingPacket(String authenicationToken)
	{
		this.authenicationToken = authenicationToken;
	}

	@Override
	public ByteBuf getBytes()
	{
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(ClientAuthenicateOutgoingPacket.HEADER_ID);
		
		ByteBufUtils.writeString(buf, this.authenicationToken);
		return buf;
	}
}
