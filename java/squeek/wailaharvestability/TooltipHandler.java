package squeek.wailaharvestability;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.wailaharvestability.helpers.StringHelper;
import squeek.wailaharvestability.helpers.ToolHelper;

public class TooltipHandler
{
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tooltipEvent(ItemTooltipEvent event)
    {
        Item item = event.getItemStack().getItem();
        if (item instanceof ItemTool && Config.HARVEST_LEVEL_TOOLTIP)
        {
            int harvestLevel = ToolHelper.getToolHarvestLevel(((ItemTool) item), event.getItemStack());
            String harvestName = StringHelper.getHarvestLevelName(harvestLevel);
            event.getToolTip().add(I18n.format("wailaharvestability.harvestlevel") + harvestName);
        }
    }
}