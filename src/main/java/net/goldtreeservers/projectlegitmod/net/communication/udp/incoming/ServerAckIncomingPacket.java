package net.goldtreeservers.projectlegitmod.net.communication.udp.incoming;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import net.goldtreeservers.projectlegitmod.net.ServerConnection;
import net.goldtreeservers.projectlegitmod.net.communication.IncomingPacket;
import net.goldtreeservers.projectlegitmod.utils.ByteBufUtils;
import net.minecraft.util.Vec3;

public class ServerAckIncomingPacket implements IncomingPacket
{
	@Override
	public void handle(DatagramPacket msg)
	{
		ByteBuf buf = msg.content();
		
		byte id = buf.readByte();
		
		Vec3 pos = ByteBufUtils.readVector(buf);
		float yaw = buf.readFloat();
		float pitch = buf.readFloat();
		
		ServerConnection.getInstance().ack(id, pos, yaw, pitch);
	}
}
