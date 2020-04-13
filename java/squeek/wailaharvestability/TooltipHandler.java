package squeek.wailaharvestability;

import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import squeek.wailaharvestability.helpers.StringHelper;
import squeek.wailaharvestability.helpers.ToolHelper;

@Mod.EventBusSubscriber(modid = ModInfo.MODID, value = Dist.CLIENT)
public class TooltipHandler
{
    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event)
    {
        Item item = event.getItemStack().getItem();
        if (item instanceof ToolItem && Config.MAIN.harvestLevelTooltip.get())
        {
            int harvestLevel = ToolHelper.getToolHarvestLevel(((ToolItem) item), event.getItemStack());
            String harvestName = StringHelper.getHarvestLevelName(harvestLevel);
            event.getToolTip().add(new TranslationTextComponent("wailaharvestability.harvestlevel").appendText(harvestName));
        }
    }
}