package net.goldtreeservers.projectlegitmod.net;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import net.goldtreeservers.projectlegitmod.data.PlayerInput;
import net.goldtreeservers.projectlegitmod.data.PlayerInputData;
import net.goldtreeservers.projectlegitmod.net.communication.udp.outgoing.ClientInputOutgoingPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

public class ServerConnection
{
	@Getter private static ServerConnection instance = new ServerConnection();
	
	private UdpNetworkManager networkManager;
	private boolean enabled;
	
	private byte nextInputId;
	private Map<Byte, PlayerInputData> inputs;
	private Map<Byte, Vec3> positions;
	
	private byte lastAck;
	
	public ServerConnection()
	{
		this.inputs = new HashMap<Byte, PlayerInputData>();
		this.positions = new HashMap<Byte, Vec3>();
	}
	
	public void reset(UdpNetworkManager networkManager)
	{
		if (this.networkManager != null)
		{
			this.networkManager.shutdown();
		}
		
		this.networkManager = networkManager;
		this.enabled = false;
		
		this.nextInputId = 0;
		this.inputs.clear();
		this.positions.clear();
		
		this.lastAck = 0;
	}
	
	public void start()
	{
		this.enabled = true;
	}
	
	public boolean addInput(EnumSet<PlayerInput> input, Vec3 position, float yaw, float pitch)
	{
		//Don't override inputs, freeze player in the position
		if (this.inputs.containsKey(this.nextInputId))
		{
			return false;
		}
		
		byte id = this.nextInputId++;

		this.inputs.put(id, new PlayerInputData(id, input, yaw, pitch));
		this.positions.put(id, position);
		
		return true;
	}

	public void ack(final byte id, final Vec3 pos, final float yaw, final float pitch)
	{
		Minecraft.getMinecraft().addScheduledTask(new Runnable()
		{
			@Override
			public void run()
			{
				for(byte i = ServerConnection.this.lastAck; i != id; i++)
				{
					if (i == id)
					{
						break;
					}
					
					ServerConnection.this.inputs.remove(i);
				}
				
				ServerConnection.this.lastAck = id;
				
				Vec3 position = ServerConnection.this.positions.get(id);
				if (position != null)
				{
					if (position.distanceTo(pos) <= 0.1D)
					{
						return;
					}
					
					Minecraft.getMinecraft().thePlayer.setPositionAndRotation(pos.xCoord, pos.yCoord, pos.zCoord, yaw, pitch);
					
					for(int i = 1; i <= 255; i++)
					{
						PlayerInputData input = ServerConnection.this.inputs.get((byte)(id + i));
						if (input != null)
						{
							float forward = 0F;
							float strafe = 0F;
							
							if (input.getPlayerInput().contains(PlayerInput.FORWARD))
							{
								forward = 1F;
							}
							else if (input.getPlayerInput().contains(PlayerInput.BACKWARDS))
							{
								forward = -1F;
							}
							
							if (input.getPlayerInput().contains(PlayerInput.LEFT))
							{
								strafe = 1F;
							}
							else if (input.getPlayerInput().contains(PlayerInput.RIGHT))
							{
								strafe = -1F;
							}
							
							if (input.getPlayerInput().contains(PlayerInput.SNEAK))
							{
								forward *= 0.3F;
								strafe *= 0.3F;
							}
							
							Minecraft.getMinecraft().thePlayer.rotationYaw = input.getYaw();
							Minecraft.getMinecraft().thePlayer.rotationPitch = input.getPitch();
							
							Minecraft.getMinecraft().thePlayer.moveEntityWithHeading(strafe, forward);
						}
						else
						{
							break;
						}
					}
				}
				else
				{
					Minecraft.getMinecraft().thePlayer.setPositionAndRotation(pos.xCoord, pos.yCoord, pos.zCoord, yaw, pitch);
				}
			}
		});
	}
	
	public void tick()
	{
		if (this.inputs.size() > 0)
		{
			this.networkManager.sendPacketUnreliable(new ClientInputOutgoingPacket(this.inputs, this.lastAck));
		}
	}
	
	public boolean isActive()
	{
		return this.networkManager != null && this.enabled;
	}
}
