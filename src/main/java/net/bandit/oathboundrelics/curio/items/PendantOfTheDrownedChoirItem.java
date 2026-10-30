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

public class PendantOfTheDrownedChoirItem extends RelicCurioItem {

    public PendantOfTheDrownedChoirItem(Properties properties) {
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

        if (!OathboundConfig.enableDrownedChoirPendant()) {
            return modifiers;
        }

        if (OathboundConfig.drownedChoirOxygenBonus() > 0.0D) {
            modifiers.put(
                    Attributes.OXYGEN_BONUS,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(OathboundRelicsMod.MOD_ID, id.getPath() + "_oxygen"),
                            OathboundConfig.drownedChoirOxygenBonus(),
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        if (OathboundConfig.drownedChoirSubmergedMiningSpeedBonus() > 0.0D) {
            modifiers.put(
                    Attributes.SUBMERGED_MINING_SPEED,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(OathboundRelicsMod.MOD_ID, id.getPath() + "_submerged_mining"),
                            OathboundConfig.drownedChoirSubmergedMiningSpeedBonus(),
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        return modifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.empty());
        addFlavorTooltip(tooltip, "tooltip.oathboundrelics.pendant_of_the_drowned_choir.flavor");
        tooltip.add(Component.empty());
        addRelicEffectTooltip(tooltip, "tooltip.oathboundrelics.pendant_of_the_drowned_choir.desc_1");
        addRelicEffectTooltip(tooltip, "tooltip.oathboundrelics.pendant_of_the_drowned_choir.desc_2");
        addDetailTooltip(tooltip, "tooltip.oathboundrelics.pendant_of_the_drowned_choir.desc_3");
    }
}
