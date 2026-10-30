package net.bandit.oathboundrelics.blocks.blockentity;

import net.bandit.oathboundrelics.config.OathboundConfig;
import net.bandit.oathboundrelics.registry.BlockEntityRegistry;
import net.bandit.oathboundrelics.util.OathboundUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class RiteOfReposeBlockEntity extends BlockEntity implements AnimatedAltar {

    private static final int SCAN_INTERVAL_TICKS = 20;
    private static final int BLUE = 2;

    // Active while a Branded player is inside the altar's rest radius.
    private boolean active = false;

    // Client-only animation counters.
    private int clientActiveTicks = 0;
    private int clientClosingTicks = 9999;
    private boolean clientWasActive = false;

    public RiteOfReposeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.RITE_OF_REPOSE_BE.get(), pos, blockState);
    }

    public InteractionResult onUsed(Player player) {
        if (level == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        String key = OathboundConfig.enableRiteOfRepose()
                ? "message.oathboundrelics.repose.info"
                : "message.oathboundrelics.repose.disabled";

        player.displayClientMessage(
                Component.translatable(key, formatRadius(OathboundConfig.riteOfReposeRadius()))
                        .withStyle(ChatFormatting.AQUA),
                true
        );

        return InteractionResult.CONSUME;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RiteOfReposeBlockEntity altar) {
        if (level.isClientSide) {
            altar.clientTick();
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Stagger scans so many altars don't all check on the same tick.
        if (Math.floorMod(level.getGameTime() + pos.asLong(), SCAN_INTERVAL_TICKS) != 0) {
            return;
        }

        boolean shouldBeActive = OathboundConfig.enableRiteOfRepose() && altar.hasBrandedPlayerNearby(serverLevel);

        if (shouldBeActive != altar.active) {
            altar.active = shouldBeActive;
            altar.syncToClient();

            serverLevel.playSound(
                    null,
                    pos,
                    shouldBeActive ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE,
                    SoundSource.BLOCKS,
                    0.6F,
                    1.4F
            );
        }

        if (altar.active) {
            serverLevel.sendParticles(
                    ParticleTypes.END_ROD,
                    pos.getX() + 0.5D,
                    pos.getY() + 2.2D,
                    pos.getZ() + 0.5D,
                    2,
                    0.4D,
                    0.3D,
                    0.4D,
                    0.005D
            );
        }
    }

    private boolean hasBrandedPlayerNearby(ServerLevel level) {
        double radius = OathboundConfig.riteOfReposeRadius();
        double radiusSqr = radius * radius;
        double x = worldPosition.getX() + 0.5D;
        double y = worldPosition.getY() + 0.5D;
        double z = worldPosition.getZ() + 0.5D;

        for (Player player : level.players()) {
            if (player.isSpectator() || player.distanceToSqr(x, y, z) > radiusSqr) {
                continue;
            }
            if (OathboundUtil.isBranded(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True when a Rite of Repose sits within its rest radius of the given position.
     * Only looks at already-loaded chunks, so it never forces chunk loads.
     */
    public static boolean isWithinRepose(Level level, BlockPos pos) {
        double radius = OathboundConfig.riteOfReposeRadius();
        double radiusSqr = radius * radius;
        int reach = (int) Math.ceil(radius);

        int minChunkX = SectionPos.blockToSectionCoord(pos.getX() - reach);
        int maxChunkX = SectionPos.blockToSectionCoord(pos.getX() + reach);
        int minChunkZ = SectionPos.blockToSectionCoord(pos.getZ() - reach);
        int maxChunkZ = SectionPos.blockToSectionCoord(pos.getZ() + reach);

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }

                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (blockEntity instanceof RiteOfReposeBlockEntity
                            && blockEntity.getBlockPos().distSqr(pos) <= radiusSqr) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String formatRadius(double radius) {
        return radius == Math.floor(radius) ? String.valueOf((int) radius) : String.format("%.1f", radius);
    }

    private void clientTick() {
        if (active) {
            if (!clientWasActive) {
                clientActiveTicks = 0;
            }
            clientActiveTicks++;
            clientClosingTicks = 0;
        } else {
            if (clientWasActive) {
                clientClosingTicks = 0;
            } else if (clientClosingTicks < 1000) {
                clientClosingTicks++;
            }
        }
        clientWasActive = active;
    }

    private void syncToClient() {
        if (level instanceof ServerLevel serverLevel) {
            BlockState state = getBlockState();
            serverLevel.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public float getClientActiveTicks(float partialTick) {
        return Math.max(0.0F, clientActiveTicks + (active ? partialTick : 0.0F));
    }

    @Override
    public float getClientClosingTicks(float partialTick) {
        return Math.max(0.0F, clientClosingTicks + (!active && clientClosingTicks < 1000 ? partialTick : 0.0F));
    }

    @Override
    public int getClientRitualColorIndex() {
        return BLUE;
    }

    @Override
    public boolean usesWavePulse() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("Active", active);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.active = tag.getBoolean("Active");
    }
}
