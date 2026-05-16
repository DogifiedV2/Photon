package com.lowdragmc.photon.client.gameobject.particle;

import com.lowdragmc.photon.client.gameobject.emitter.PhotonParticleRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import java.util.Random;

import java.util.function.Function;

public interface IParticle {

    PhotonParticleRenderType getRenderType();

    Random getRandom();

    boolean isRemoved();

    default boolean isAlive() {
        return !isRemoved();
    }

    float getT();

    float getT(float partialTicks);

    float getMemRandom(Object object);

    float getMemRandom(Object object, Function<Random, Float> randomFunc);

    void tick();

    void render(VertexConsumer buffer, Camera camera, float pPartialTicks);

}
