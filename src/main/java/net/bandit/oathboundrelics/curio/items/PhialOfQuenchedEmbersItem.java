package net.bandit.oathboundrelics.curio.items;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.bandit.oathboundrelics.OathboundRelicsMod;
import net.bandit.oathboundrelics.config.OathboundConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

// Fire damage reduction lives in AdditionalRelicEvents; burn time is shortened here via the Burning Time attribute.
public class PhialOfQuenchedEmbersItem extends RelicCurioItem {

    public PhialOfQuenchedEmbersItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            ResourceLocation id,
            ItemStack stack
    ) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = LinkedHashMultimap.create();
        modifiers.putAll(super.getAttributeModifiers(slotContext, id, stack));

        if (OathboundConfig.enablePhialOfQuenchedEmbers() && OathboundConfig.phialBurningTimeReduction() > 0.0D) {
            modifiers.put(
                    Attributes.BURNING_TIME,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(OathboundRelicsMod.MOD_ID, id.getPath() + "_burning_time"),
                            -OathboundConfig.phialBurningTimeReduction(),
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        }

        return modifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.empty());
        addFlavorTooltip(tooltip, "tooltip.oathboundrelics.phial_of_quenched_embers.flavor");
        tooltip.add(Component.empty());
        addRelicEffectTooltip(tooltip, "tooltip.oathboundrelics.phial_of_quenched_embers.desc_1");
        addRelicEffectTooltip(tooltip, "tooltip.oathboundrelics.phial_of_quenched_embers.desc_2");
        addDetailTooltip(tooltip, "tooltip.oathboundrelics.phial_of_quenched_embers.desc_3");
    }
}
