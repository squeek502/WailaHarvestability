package squeek.wailaharvestability.gui.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import squeek.wailaharvestability.Config;
import squeek.wailaharvestability.ModInfo;

public class GuiWailaHarvestabilityConfig extends GuiConfig {
    public GuiWailaHarvestabilityConfig(GuiScreen parentScreen)
    {
        super(parentScreen, new ConfigElement(Config.config.getCategory(Config.CATEGORY_MAIN)).getChildElements(), ModInfo.MODID, false, false, GuiConfig.getAbridgedConfigPath(Config.config.toString()));
    }
}