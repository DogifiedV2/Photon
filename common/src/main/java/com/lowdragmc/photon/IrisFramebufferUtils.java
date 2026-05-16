package com.lowdragmc.photon;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

/**
 * 1.18 experimental bridge stub for Photon 1.20 Iris/Oculus framebuffer helpers.
 * Shader-pack-specific framebuffer routing is deferred; default to Minecraft's main target.
 */
public class IrisFramebufferUtils {
    private static boolean renderingGUIScreen = false;

    public static int getIrisSolidFboId() {
        return Minecraft.getInstance().getMainRenderTarget().frameBufferId;
    }

    public static int getIrisTranslucentFboId() {
        return Minecraft.getInstance().getMainRenderTarget().frameBufferId;
    }

    public static int getIrisDepthTextureId() {
        return Minecraft.getInstance().getMainRenderTarget().getDepthTextureId();
    }

    public static int getIrisSolidTextureId() {
        return Minecraft.getInstance().getMainRenderTarget().getColorTextureId();
    }

    public static int getIrisTranslucentTextureId(boolean writeBuffer) {
        return Minecraft.getInstance().getMainRenderTarget().getColorTextureId();
    }

    public static Field getFboCachedField() {
        return null;
    }

    public static boolean isUsingShaderPack() {
        return false;
    }

    public static boolean isRenderingGUIScreen() {
        return renderingGUIScreen;
    }

    public static void setRenderingGUIScreen(boolean renderingGUIScreen) {
        IrisFramebufferUtils.renderingGUIScreen = renderingGUIScreen;
    }
}
