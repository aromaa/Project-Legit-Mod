package net.goldtreeservers.projectlegitmod.utils;

import java.util.EnumSet;

public class EnumSetUtils
{
	public static <E extends Enum<E>> int getFlag(EnumSet<E> enumSet)
	{
		int flag = 0;
		for(E input : enumSet)
		{
			flag |= 1 << input.ordinal();
		}
		
		return flag;
	}
	
	public static <E extends Enum<E>> EnumSet<E> getEnumSet(Class<E> enumClass, int flag)
	{
		EnumSet<E> enumSet = EnumSet.noneOf(enumClass);
		for(E value : enumClass.getEnumConstants())
		{
			if ((flag & (1 << value.ordinal())) != 0)
			{
				enumSet.add(value);
			}
		}
		
		return enumSet;
	}
}
