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
            var modular = new ModularUI(IUIHolder.EMPTY, player).widget(new FXEditor(LDLib.getLDLibDir()));
            modular.initWidgets();
            ModularUIGuiContainer gui = new ModularUIGuiContainer(modular, player.containerMenu.containerId);
            minecraft.setScreen(gui);
            player.containerMenu = gui.getMenu();
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
