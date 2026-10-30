package net.bandit.oathboundrelics.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.bandit.oathboundrelics.curio.items.TitansRemnantItem;
import net.bandit.oathboundrelics.registry.IngredientRegistry;
import net.bandit.oathboundrelics.util.TitansRemnantUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.Arrays;
import java.util.stream.Stream;

// Matches the base ingredient, but only Titan's Remnants that are still Dormant.
public record DormantRemnantIngredient(Ingredient base) implements ICustomIngredient {

    public static final MapCodec<DormantRemnantIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(DormantRemnantIngredient::base)
    ).apply(instance, DormantRemnantIngredient::new));

    @Override
    public boolean test(ItemStack stack) {
        return base.test(stack)
                && stack.getItem() instanceof TitansRemnantItem
                && TitansRemnantUtil.getStage(stack) == TitanRemnantStage.DORMANT;
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Arrays.stream(base.getItems());
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return IngredientRegistry.DORMANT_REMNANT.get();
    }
}
