package net.goldtreeservers.projectlegitmod;

import java.util.EnumSet;

import net.goldtreeservers.projectlegitmod.data.PlayerInput;
import net.goldtreeservers.projectlegitmod.net.ServerConnection;
import net.goldtreeservers.projectlegitmod.net.communication.tcp.incoming.StartAuthoractiveConnectionPluginMessage;
import net.goldtreeservers.projectlegitmod.net.communication.tcp.incoming.StartUdpProtocolPluginMessage;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "projectlegitmod", version = "1.0", clientSideOnly = true)
public class ProjectLegitMod
{
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("ProjectLegitClient");
		network.registerMessage(StartAuthoractiveConnectionPluginMessage.Handler.class, StartAuthoractiveConnectionPluginMessage.class, 0, Side.CLIENT);
		network.registerMessage(StartUdpProtocolPluginMessage.Handler.class, StartUdpProtocolPluginMessage.class, 1, Side.CLIENT);
	
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
	{
		if (!ServerConnection.getInstance().isActive())
		{
			return;
		}
		
		if (event.phase != TickEvent.Phase.END)
		{
			return;
		}
		
		if (event.side != Side.CLIENT)
		{
			return;
		}
		
		if (event.player instanceof EntityPlayerSP)
		{
			EntityPlayerSP sp = (EntityPlayerSP)event.player;	
			MovementInput input = sp.movementInput;
			
			EnumSet<PlayerInput> inputs = EnumSet.noneOf(PlayerInput.class);
			if (input.moveForward != 0)
			{
				if (input.moveForward > 0)
				{
					inputs.add(PlayerInput.FORWARD);
				}
				else
				{
					inputs.add(PlayerInput.BACKWARDS);
				}
			}
			
			if (input.moveStrafe != 0)
			{
				if (input.moveStrafe > 0)
				{
					inputs.add(PlayerInput.LEFT);
				}
				else
				{
					inputs.add(PlayerInput.RIGHT);
				}
			}
			
			if (input.sneak)
			{
				inputs.add(PlayerInput.SNEAK);
			}
			
			if (input.jump)
			{
				inputs.add(PlayerInput.JUMP);
			}
			
			ServerConnection.getInstance().addInput(inputs, sp.getPositionVector(), sp.rotationYaw, sp.rotationPitch);
		}
	}
	
	@SubscribeEvent
	public void onClientTickEvent(TickEvent.ClientTickEvent event)
	{
		if (!ServerConnection.getInstance().isActive())
		{
			return;
		}
		
		ServerConnection.getInstance().tick();
	}
}
