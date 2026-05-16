package com.lowdragmc.photon.client;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.photon.Photon;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.photon.client.gameobject.FXObject;
import com.lowdragmc.photon.client.gameobject.emitter.PhotonParticleRenderType;
import com.lowdragmc.photon.client.fx.BlockEffect;
import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.lowdragmc.photon.core.mixins.accessor.ParticleEngineAccessor;
import com.lowdragmc.photon.gui.editor.FXEditor;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;

import static com.lowdragmc.lowdraglib.client.ClientCommands.createLiteral;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote ClientCommands
 */
@Environment(EnvType.CLIENT)
public class ClientCommands {
    private static boolean pendingEditorOpen;
    private static boolean autoWorldLoadAttempted;
    private static boolean autoEditorOpenQueued;
    private static boolean autoWindowResizeAttempted;
    private static int autoOpenTicks;

    private static boolean isAutoOpenEditorEnabled() {
        return Boolean.getBoolean("photon.autoOpenEditor") ||
                Boolean.parseBoolean(System.getenv().getOrDefault("PHOTON_AUTO_OPEN_EDITOR", "false"));
    }

    private static String getAutoLoadWorldName() {
        var property = System.getProperty("photon.autoLoadWorld");
        if (property != null && !property.isBlank()) {
            return property;
        }
        var env = System.getenv("PHOTON_AUTO_LOAD_WORLD");
        if (env != null && !env.isBlank()) {
            return env;
        }
        return "New World";
    }

    @Environment(EnvType.CLIENT)
    public static void runDevAutoOpenHarness() {
        if (!isAutoOpenEditorEnabled() || autoEditorOpenQueued) return;

        var minecraft = Minecraft.getInstance();
        if (!autoWindowResizeAttempted && minecraft.getWindow() != null) {
            autoWindowResizeAttempted = true;
            var width = Integer.getInteger("photon.autoWindowWidth", 1600);
            var height = Integer.getInteger("photon.autoWindowHeight", 900);
            var x = Integer.getInteger("photon.autoWindowX", 40);
            var y = Integer.getInteger("photon.autoWindowY", 80);
            Photon.LOGGER.info("Photon dev auto-open harness moving window to {},{} and resizing to {}x{}", x, y, width, height);
            minecraft.getWindow().setWindowed(width, height);
            GLFW.glfwSetWindowPos(minecraft.getWindow().getWindow(), x, y);
            minecraft.resizeDisplay();
        }
        if (minecraft.level == null || minecraft.player == null) {
            if (!autoWorldLoadAttempted && ++autoOpenTicks > 40) {
                autoWorldLoadAttempted = true;
                var worldName = getAutoLoadWorldName();
                File worldFolder = new File(new File(minecraft.gameDirectory, "saves"), worldName);
                if (worldFolder.isDirectory()) {
                    Photon.LOGGER.info("Photon dev auto-open harness loading singleplayer world '{}'", worldName);
                    minecraft.loadLevel(worldName);
                } else {
                    Photon.LOGGER.warn("Photon dev auto-open harness could not load '{}': {} is not a directory",
                            worldName, worldFolder.getAbsolutePath());
                }
            }
            return;
        }

        autoEditorOpenQueued = true;
        pendingEditorOpen = true;
        Photon.LOGGER.info("Photon dev auto-open harness queued editor open");
    }

    @Environment(EnvType.CLIENT)
    public static void openPendingEditor() {
        if (!pendingEditorOpen) return;
        pendingEditorOpen = false;
        var minecraft = Minecraft.getInstance();
        try {
            var player = minecraft.player;
            if (player == null) {
                Photon.LOGGER.warn("Cannot open Photon editor: local player is null on client tick");
                return;
            }
            Photon.LOGGER.info("Opening Photon editor screen on client tick. previousScreen={} workspace={}",
                    minecraft.screen == null ? "null" : minecraft.screen.getClass().getName(),
                    LDLib.getLDLibDir().getAbsolutePath());
            var editor = new FXEditor(LDLib.getLDLibDir());
            var modular = new ModularUI(IUIHolder.EMPTY, player).widget(editor);
            modular.initWidgets();
            ModularUIGuiContainer gui = new ModularUIGuiContainer(modular, player.containerMenu.containerId);
            minecraft.setScreen(gui);
            player.containerMenu = gui.getMenu();
            if (isAutoOpenEditorEnabled()) {
                Photon.LOGGER.info("Photon dev auto-open harness creating smoke FX project");
                editor.openDevSmokeParticleProject();
            }
            player.displayClientMessage(new TextComponent("Photon editor screen opened"), false);
            Photon.LOGGER.info("Photon editor screen opened: currentScreen={}",
                    minecraft.screen == null ? "null" : minecraft.screen.getClass().getName());
        } catch (Throwable throwable) {
            Photon.LOGGER.error("Failed to open Photon editor screen", throwable);
            if (minecraft.player != null) {
                minecraft.player.displayClientMessage(new TextComponent("Photon editor failed to open: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage()), false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <S> List<LiteralArgumentBuilder<S>> createClientCommands() {
        return List.of(
                (LiteralArgumentBuilder<S>) createLiteral("photon_editor").executes(context -> {
                    Photon.LOGGER.info("/photon_editor command executed; deferring editor open until next client tick");
                    var minecraft = Minecraft.getInstance();
                    var entityPlayer = minecraft.player;
                    pendingEditorOpen = true;
                    if (entityPlayer != null) {
                        entityPlayer.displayClientMessage(new TextComponent("Photon editor command executed; opening editor next tick..."), false);
                    } else {
                        Photon.LOGGER.warn("/photon_editor command executed before a local player was available");
                    }
                    return 1;
                }),
                (LiteralArgumentBuilder<S>) createLiteral("photon_client")
                        .then(createLiteral("clear_particles")
                                .executes(context -> {
                                    if (Minecraft.getInstance().particleEngine instanceof ParticleEngineAccessor accessor) {
                                        accessor.getParticles().entrySet().removeIf(entry ->
                                                entry.getKey() instanceof PhotonParticleRenderType ||
                                                entry.getKey() == FXObject.NO_RENDER_RENDER_TYPE);
                                    }
                                    EntityEffect.CACHE.clear();
                                    BlockEffect.CACHE.clear();
                                    return 1;
                                }))
                        .then(createLiteral("clear_client_fx_cache")
                                .executes(context -> {
                                    if (Minecraft.getInstance().player != null) {
                                        Minecraft.getInstance().player.displayClientMessage(new TextComponent("clear client cache fx: " + FXHelper.clearCache()), false);
                                    } else {
                                        FXHelper.clearCache();
                                    }
                                    return 1;
                                }))
        );
    }
}
