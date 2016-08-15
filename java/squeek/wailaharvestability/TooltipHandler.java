package squeek.wailaharvestability;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TooltipHandler {
    private static String[] modsSupported = new String[]{"tconstruct", "taiga"};

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tooltipEvent(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        if (item instanceof ItemTool && Config.HARVEST_LEVEL_TOOLTIP) {
            event.getToolTip().add(I18n.format("wailaharvestability.harvestlevel") + getHarvastName(((ItemTool) item).getToolMaterial().getHarvestLevel()));
        }
    }

    private String getHarvastName(int level) {
        if (I18n.hasKey("wailaharvestability.harvestlevel." + level)) {
            return I18n.format("wailaharvestability.harvestlevel." + level);
        }
        for (String mod : modsSupported) {
            if (Loader.isModLoaded(mod)) {
                if (I18n.hasKey("wailaharvestability.harvestlevel." + level + "." + mod)) {
                    return I18n.format("wailaharvestability.harvestlevel." + level + "." + mod);
                }
            }
        }
        return Integer.toString(level);
    }
}