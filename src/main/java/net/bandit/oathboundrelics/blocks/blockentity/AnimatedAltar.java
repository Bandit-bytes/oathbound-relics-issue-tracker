package net.bandit.oathboundrelics.blocks.blockentity;

// Client-facing animation state shared by the wave-altar rites, so one renderer can draw any of them.
public interface AnimatedAltar {

    boolean isActive();

    float getClientActiveTicks(float partialTick);

    float getClientClosingTicks(float partialTick);

    // 0 = red, 1 = green, 2 = blue, 3 = purple
    int getClientRitualColorIndex();

    // Whether the active altar emits the periodic wave_start / wave_end pulse.
    default boolean usesWavePulse() {
        return true;
    }
}
