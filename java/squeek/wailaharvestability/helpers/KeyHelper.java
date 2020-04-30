package squeek.wailaharvestability.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

public class KeyHelper
{
	public static boolean isCtrlKeyDown()
	{
		// prioritize CONTROL, but allow OPTION as well on Mac (note: GuiScreen's isCtrlKeyDown only checks for the OPTION key on Mac)
		long handle = Minecraft.getInstance().getMainWindow().getHandle();
		boolean isCtrlKeyDown = InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_CONTROL) || InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_CONTROL);
		if (!isCtrlKeyDown && Minecraft.IS_RUNNING_ON_MAC)
			isCtrlKeyDown = InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_ALT) || InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_ALT);

		return isCtrlKeyDown;
	}

	public static boolean isShiftKeyDown()
	{
		long handle = Minecraft.getInstance().getMainWindow().getHandle();
		return InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_SHIFT);
	}
}