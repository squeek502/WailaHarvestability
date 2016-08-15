package squeek.wailaharvestability;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.wailaharvestability.helpers.StringHelper;

public class TooltipHandler {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tooltipEvent(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        if (item instanceof ItemTool && Config.HARVEST_LEVEL_TOOLTIP) {
            event.getToolTip().add(I18n.format("wailaharvestability.harvestlevel") + StringHelper.getHarvestLevelName(((ItemTool) item).getToolMaterial().getHarvestLevel()));
        }
    }
}