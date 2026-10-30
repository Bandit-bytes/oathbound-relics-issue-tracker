package net.bandit.oathboundrelics.curio.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ScourgeborneAmuletItem extends OathboundCurioItem {

    public ScourgeborneAmuletItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.empty());
        addFlavorTooltip(tooltip, "tooltip.oathboundrelics.scourgeborne_amulet.flavor");
        tooltip.add(Component.empty());
        addRelicEffectTooltip(tooltip, "tooltip.oathboundrelics.scourgeborne_amulet.desc_1");
        addRelicEffectTooltip(tooltip, "tooltip.oathboundrelics.scourgeborne_amulet.desc_2");
        addRelicEffectTooltip(tooltip, "tooltip.oathboundrelics.scourgeborne_amulet.desc_3");
    }
}
