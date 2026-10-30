package net.bandit.oathboundrelics.registry;

import net.bandit.oathboundrelics.OathboundRelicsMod;
import net.bandit.oathboundrelics.items.DormantRemnantIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class IngredientRegistry {

    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, OathboundRelicsMod.MOD_ID);

    public static final DeferredHolder<IngredientType<?>, IngredientType<DormantRemnantIngredient>> DORMANT_REMNANT =
            INGREDIENT_TYPES.register("dormant_remnant", () -> new IngredientType<>(DormantRemnantIngredient.CODEC));

    private IngredientRegistry() {
    }

    public static void register(IEventBus modBus) {
        INGREDIENT_TYPES.register(modBus);
    }
}
