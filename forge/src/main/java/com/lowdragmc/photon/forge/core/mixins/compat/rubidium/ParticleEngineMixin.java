package com.lowdragmc.photon.forge.core.mixins.compat.rubidium;

import com.lowdragmc.photon.forge.core.rubidium.RubidiumParticleCullingCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Marks the vanilla particle render pass so the Rubidium renderer mixin can skip
 * Rubidium's particle visibility filter for Photon queue wrapper particles.
 */
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
            at = @At("HEAD"), remap = false)
    private void photon$beginRubidiumParticleRender(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                                    LightTexture lightTexture, Camera camera, float partialTicks,
                                                    Frustum frustum, CallbackInfo ci) {
        RubidiumParticleCullingCompat.beginParticleRender();
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
            at = @At("RETURN"), remap = false)
    private void photon$endRubidiumParticleRender(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                                  LightTexture lightTexture, Camera camera, float partialTicks,
                                                  Frustum frustum, CallbackInfo ci) {
        RubidiumParticleCullingCompat.endParticleRender();
    }
}
