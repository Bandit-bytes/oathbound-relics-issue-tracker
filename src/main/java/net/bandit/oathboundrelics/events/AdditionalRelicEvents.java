package net.bandit.oathboundrelics.events;

import net.bandit.oathboundrelics.OathboundRelicsMod;
import net.bandit.oathboundrelics.config.OathboundConfig;
import net.bandit.oathboundrelics.registry.ItemRegistry;
import net.bandit.oathboundrelics.util.OathboundUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

// Effects for the second wave of relics: Knell, Chalice, Scourgeborne Amulet, Phial, Elixir, and Clot.
// The Key of the Sealed Abyss hooks Blood Toll directly in OathboundRelicEvents.
@EventBusSubscriber(modid = OathboundRelicsMod.MOD_ID)
public final class AdditionalRelicEvents {

    private static final String KNELL_COOLDOWN_TAG = OathboundRelicsMod.MOD_ID + "_knell_cooldown_until";
    private static final String CLOT_COOLDOWN_TAG = OathboundRelicsMod.MOD_ID + "_clot_cooldown_until";

    private static final ResourceLocation DUSK_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath(OathboundRelicsMod.MOD_ID, "elixir_of_eternal_dusk_speed");
    private static final ResourceLocation DUSK_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath(OathboundRelicsMod.MOD_ID, "elixir_of_eternal_dusk_damage");

    private AdditionalRelicEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        float amount = event.getAmount();
        if (amount <= 0.0F) {
            return;
        }

        if (OathboundConfig.enablePhialOfQuenchedEmbers()
                && event.getSource().is(DamageTypeTags.IS_FIRE)
                && OathboundUtil.hasCurio(player, ItemRegistry.PHIAL_OF_QUENCHED_EMBERS.get())) {
            amount *= (float) OathboundConfig.phialFireDamageMultiplier();
        }

        if (OathboundConfig.enableScourgeborneAmulet()
                && OathboundUtil.isBranded(player)
                && OathboundUtil.hasCurio(player, ItemRegistry.SCOURGEBORNE_AMULET.get())) {
            int harmful = 0;
            for (MobEffectInstance effect : player.getActiveEffects()) {
                if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                    harmful++;
                }
            }

            double reduction = Math.min(
                    OathboundConfig.scourgeborneMaxReduction(),
                    harmful * OathboundConfig.scourgeborneReductionPerEffect()
            );
            amount *= (float) (1.0D - reduction);
        }

        event.setAmount(amount);
    }

    @SubscribeEvent
    public static void onPlayerDamaged(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        if (event.getNewDamage() <= 0.0F || !player.isAlive()) {
            return;
        }

        long gameTime = player.level().getGameTime();

        if (OathboundConfig.enableKnellOfTheForsworn()
                && player.getHealth() <= player.getMaxHealth() * OathboundConfig.knellHealthThreshold()
                && gameTime >= player.getPersistentData().getLong(KNELL_COOLDOWN_TAG)
                && OathboundUtil.isBranded(player)
                && OathboundUtil.hasCurio(player, ItemRegistry.KNELL_OF_THE_FORSWORN.get())) {
            tollKnell(player);
            player.getPersistentData().putLong(KNELL_COOLDOWN_TAG, gameTime + OathboundConfig.knellCooldownTicks());
        }

        if (OathboundConfig.enableClotOfStubbornIchor()
                && player.getHealth() <= player.getMaxHealth() * OathboundConfig.clotHealthThreshold()
                && gameTime >= player.getPersistentData().getLong(CLOT_COOLDOWN_TAG)
                && OathboundUtil.hasCurio(player, ItemRegistry.CLOT_OF_STUBBORN_ICHOR.get())) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION,
                    OathboundConfig.clotRegenerationDurationTicks(),
                    OathboundConfig.clotRegenerationAmplifier(),
                    false,
                    true
            ));
            player.getPersistentData().putLong(CLOT_COOLDOWN_TAG, gameTime + OathboundConfig.clotCooldownTicks());
        }
    }

    private static void tollKnell(Player player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        double radius = OathboundConfig.knellRadius();
        double knockback = OathboundConfig.knellKnockback();

        for (Mob mob : level.getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(radius),
                mob -> mob.isAlive() && mob instanceof Enemy
        )) {
            double dx = player.getX() - mob.getX();
            double dz = player.getZ() - mob.getZ();
            if (knockback > 0.0D) {
                mob.knockback(knockback, dx, dz);
            }

            if (OathboundConfig.knellWeaknessDurationTicks() > 0) {
                mob.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS,
                        OathboundConfig.knellWeaknessDurationTicks(),
                        OathboundConfig.knellWeaknessAmplifier()
                ), player);
            }
            if (OathboundConfig.knellSlownessDurationTicks() > 0) {
                mob.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        OathboundConfig.knellSlownessDurationTicks(),
                        OathboundConfig.knellSlownessAmplifier()
                ), player);
            }
        }

        level.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.5F, 0.5F);
        level.playSound(null, player.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 0.8F, 0.7F);
        level.sendParticles(
                ParticleTypes.SCULK_SOUL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                24,
                radius * 0.4D,
                0.4D,
                radius * 0.4D,
                0.02D
        );
    }

    @SubscribeEvent
    public static void onPlayerDealsDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        // Melee only: the attacker must also be the direct source, not a projectile.
        if (event.getSource().getDirectEntity() != player || event.getEntity() == player) {
            return;
        }

        if (!OathboundConfig.enableChaliceOfTheBloodsworn()
                || event.getNewDamage() <= 0.0F
                || !OathboundUtil.isBranded(player)
                || !OathboundUtil.hasCurio(player, ItemRegistry.CHALICE_OF_THE_BLOODSWORN.get())) {
            return;
        }

        float heal = (float) Math.min(
                event.getNewDamage() * OathboundConfig.chaliceLifestealPercent(),
                OathboundConfig.chaliceMaxHealPerHit()
        );

        if (heal > 0.0F && player.getHealth() < player.getMaxHealth()) {
            player.heal(heal);
            player.causeFoodExhaustion((float) OathboundConfig.chaliceExhaustionPerHit());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || player.tickCount % 10 != 0) {
            return;
        }

        tickElixirOfEternalDusk(player);
    }

    private static void tickElixirOfEternalDusk(Player player) {
        boolean empowered = OathboundConfig.enableElixirOfEternalDusk()
                && OathboundUtil.hasCurio(player, ItemRegistry.ELIXIR_OF_ETERNAL_DUSK.get())
                && player.level().getMaxLocalRawBrightness(player.blockPosition()) <= OathboundConfig.elixirLightThreshold();

        applyDuskModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), DUSK_SPEED_ID,
                empowered ? OathboundConfig.elixirMovementSpeedBonus() : 0.0D);
        applyDuskModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), DUSK_DAMAGE_ID,
                empowered ? OathboundConfig.elixirAttackDamageBonus() : 0.0D);
    }

    private static void applyDuskModifier(AttributeInstance attribute, ResourceLocation id, double amount) {
        if (attribute == null) {
            return;
        }

        if (amount <= 0.0D) {
            attribute.removeModifier(id);
            return;
        }

        AttributeModifier existing = attribute.getModifier(id);
        if (existing == null || existing.amount() != amount) {
            attribute.addOrUpdateTransientModifier(new AttributeModifier(
                    id,
                    amount,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }
    }
}
