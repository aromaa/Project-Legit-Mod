package net.goldtreeservers.projectlegitmod.net.communication.udp.outgoing;

import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.goldtreeservers.projectlegitmod.data.PlayerInputData;
import net.goldtreeservers.projectlegitmod.net.communication.OutgoingPacket;

public class ClientInputOutgoingPacket implements OutgoingPacket
{
	private static final byte HEADER_ID = 1;
	
	private Map<Byte, PlayerInputData> inputs;
	private byte lastAck;
	
	public ClientInputOutgoingPacket(Map<Byte, PlayerInputData> inputs, byte lastAck)
	{
		this.inputs = inputs;
		this.lastAck = lastAck;
	}
	
	@Override
	public ByteBuf getBytes()
	{
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(ClientInputOutgoingPacket.HEADER_ID);
		
		PlayerInputData oldInput = null;
		for(int i = 0; i <= 255; i++)
		{
			PlayerInputData input = this.inputs.get((byte)(this.lastAck + i));
			if (input != null)
			{
				input.toBytes(buf, oldInput);
				
				oldInput = input;
			}
			else if (oldInput != null)
			{
				break;
			}
		}
		
		return buf;
	}
}
