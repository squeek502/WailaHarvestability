package squeek.wailaharvestlevels.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ToolHelper
{
	
	public static void init()
	{
		
	}

	public static boolean hasToolTag(ItemStack itemStack)
	{
		return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("InfiTool");
	}

	public static NBTTagCompound getToolTag(ItemStack tool)
	{
		NBTTagCompound tag;
		tag = tool.getTagCompound().getCompoundTag("InfiTool");
		return tag;
	}

	public static int getPrimaryHarvestLevel(NBTTagCompound toolTag)
	{
		return toolTag.getInteger("HarvestLevel");
	}

	public static int getSecondaryHarvestLevel(NBTTagCompound toolTag)
	{
		return toolTag.getInteger("HarvestLevel2");
	}

}
