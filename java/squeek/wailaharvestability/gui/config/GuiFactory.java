package squeek.wailaharvestability.gui.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import squeek.wailaharvestability.Config;
import squeek.wailaharvestability.ModInfo;

public class GuiFactory extends DefaultGuiFactory
{
    public GuiFactory()
    {
        super(ModInfo.MODID, GuiConfig.getAbridgedConfigPath(Config.config.toString()));
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {
        return new GuiConfig(parentScreen, new ConfigElement(Config.config.getCategory(Config.CATEGORY_MAIN)).getChildElements(), modid, false, false, title);
    }
}