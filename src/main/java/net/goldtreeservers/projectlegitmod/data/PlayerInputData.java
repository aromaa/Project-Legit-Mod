package net.goldtreeservers.projectlegitmod.data;

import java.util.EnumSet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.projectlegitmod.utils.EnumSetUtils;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PlayerInputData
{
	private final byte id;
	
	private EnumSet<PlayerInput> playerInput;
	private float yaw; //Change to short..?
	private float pitch; //Change to short..? Has smaller range space so it could be actualyl worth it

	public void toBytes(ByteBuf buf, PlayerInputData oldInput)
	{
		int inputFlag = this.getInputFlag();
		if (oldInput == null)
		{
			buf.writeByte(this.id);
			buf.writeShort(inputFlag);
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
		}
		else
		{
			ByteBuf temp = Unpooled.buffer(10);
			
			EnumSet<State> state = EnumSet.noneOf(State.class);
			if (inputFlag != oldInput.getInputFlag())
			{
				temp.writeShort(inputFlag);
				
				state.add(State.INPUT_CHANGED);
			}
			
			if (this.yaw != oldInput.yaw)
			{
				temp.writeFloat(this.yaw);
				
				state.add(State.YAW_CHANGED);
			}
			
			if (this.pitch != oldInput.pitch)
			{
				temp.writeFloat(this.pitch);
				
				state.add(State.PITCH_CHANGED);
			}
			
			buf.writeByte(EnumSetUtils.getFlag(state));
			buf.writeBytes(temp);
		}
	}
	
	public int getInputFlag()
	{
		return EnumSetUtils.getFlag(this.playerInput);
	}
	
	private static enum State
	{
		INPUT_CHANGED,
		YAW_CHANGED,
		PITCH_CHANGED,
	}
}
