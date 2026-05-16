package com.lowdragmc.photon.forge.core.rubidium;

/**
 * Render-thread marker used by Photon's optional Rubidium compatibility mixins.
 */
public final class RubidiumParticleCullingCompat {
    private static final ThreadLocal<Integer> PARTICLE_RENDER_DEPTH = ThreadLocal.withInitial(() -> 0);

    private RubidiumParticleCullingCompat() {
    }

    public static void beginParticleRender() {
        PARTICLE_RENDER_DEPTH.set(PARTICLE_RENDER_DEPTH.get() + 1);
    }

    public static void endParticleRender() {
        int depth = PARTICLE_RENDER_DEPTH.get() - 1;
        if (depth <= 0) {
            PARTICLE_RENDER_DEPTH.remove();
        } else {
            PARTICLE_RENDER_DEPTH.set(depth);
        }
    }

    public static boolean isParticleRenderActive() {
        return PARTICLE_RENDER_DEPTH.get() > 0;
    }
}
