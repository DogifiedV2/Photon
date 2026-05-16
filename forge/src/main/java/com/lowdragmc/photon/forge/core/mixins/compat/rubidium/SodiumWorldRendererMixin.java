package com.lowdragmc.photon.forge.core.mixins.compat.rubidium;

import com.lowdragmc.photon.forge.core.rubidium.RubidiumParticleCullingCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Rubidium culls particles by Particle#getBoundingBox() before Photon emitters
 * can render their queued FX. During the particle render pass, let those boxes
 * pass Rubidium's chunk visibility check; Photon still performs its own cull
 * checks for emitter contents.
 */
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer", remap = false)
public abstract class SodiumWorldRendererMixin {
    @Inject(method = "isBoxVisible(DDDDDD)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void photon$allowPhotonParticleQueue(double minX, double minY, double minZ,
                                                 double maxX, double maxY, double maxZ,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if (RubidiumParticleCullingCompat.isParticleRenderActive()) {
            cir.setReturnValue(true);
        }
    }
}
