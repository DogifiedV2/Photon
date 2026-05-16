package com.lowdragmc.photon.gui.editor;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.data.IProject;
import com.lowdragmc.lowdraglib.gui.editor.ui.*;
import com.lowdragmc.lowdraglib.gui.editor.ui.menu.ViewMenu;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.photon.Photon;
import com.lowdragmc.photon.client.fx.BlockEffect;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.lowdragmc.photon.client.fx.IEffect;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.lowdragmc.photon.client.gameobject.emitter.beam.BeamEmitter;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.lowdragmc.photon.client.gameobject.emitter.trail.TrailEmitter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@LDLRegister(name = "editor.fx", group = "editor")
@Environment(EnvType.CLIENT)
public class FXEditor extends Editor {
    public static final ConfigPanel.Tab BASIC = ConfigPanel.Tab.WIDGET;
    public static final ConfigPanel.Tab RESOURCE = ConfigPanel.Tab.RESOURCE;

    protected IEffect effect;

    public FXEditor(File workSpace) {
        super(workSpace);
    }

    public IEffect getEditorFX() {
        return effect;
    }

    @Override
    public void initEditorViews() {
        this.toolPanel = new ToolPanel(this);
        this.toolPanel.setSizeWidth(150);
        this.configPanel = new ConfigPanel(this, List.of(BASIC, RESOURCE));
        this.tabPages = new StringTabContainer(this);
        this.resourcePanel = new ResourcePanel(this);
        this.menuPanel = new MenuPanel(this);
        this.floatView = new WidgetGroup(0, 0, this.getSize().width, this.getSize().height);

        this.addWidget(this.tabPages);
        this.addWidget(this.toolPanel);
        this.addWidget(this.configPanel);
        this.addWidget(this.resourcePanel);
        this.addWidget(this.menuPanel);
        this.addWidget(this.floatView);
    }

    @Override
    public void loadProject(IProject project) {
        if (project == null || project instanceof FXProject) {
            super.loadProject(project);
        } else {
            throw new IllegalArgumentException("Invalid project type");
        }
    }

    /**
     * Temporary developer smoke harness entry point.
     * Creates the same visible state a human would make through:
     * File -> new FX project -> FX Object List -> add emitter -> particle -> select emitter.
     */
    public void openDevSmokeParticleProject() {
        var project = new FXProject().newEmptyProject();
        var emitters = createDevSmokeEmitters();
        project.getFx().getMainFX().objects().addAll(emitters);

        loadProject(project);
        runDevExportSmoke(project);

        if (!getTabPages().getTabGroups().isEmpty() && getTabPages().getTabGroups().get(0) instanceof ParticleScenePanel panel) {
            panel.onPanelSelected();
            if (!emitters.isEmpty() && panel.getFxObjectsList() != null) {
                panel.getFxObjectsList().setSelectedFX(emitters.get(0));
                panel.getFxObjectsList().updateList();
            }
            panel.restartEmitters();
        }
    }

    private void runDevExportSmoke(FXProject project) {
        if (!Boolean.getBoolean("photon.autoExportSmoke") &&
                !Boolean.parseBoolean(System.getenv().getOrDefault("PHOTON_AUTO_EXPORT_SMOKE", "false"))) {
            return;
        }

        var file = new File(LDLib.getLDLibDir(), "assets/photon/fx/codex_smoke.fx");
        if (file.getParentFile() != null && !file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
            Photon.LOGGER.warn("Photon dev smoke could not create FX export directory: {}", file.getParentFile());
            return;
        }

        var tag = new CompoundTag();
        tag.put("fx", project.getFx().serializeNBT());
        tag.putInt("_version", FXProject.VERSION);
        try {
            NbtIo.writeCompressed(tag, file);
            Photon.LOGGER.info("Photon dev smoke exported FX to {}", file.getAbsolutePath());
        } catch (IOException e) {
            Photon.LOGGER.error("Photon dev smoke failed to export FX to {}", file.getAbsolutePath(), e);
            return;
        }

        if (Boolean.getBoolean("photon.autoSpawnSmoke") ||
                Boolean.parseBoolean(System.getenv().getOrDefault("PHOTON_AUTO_SPAWN_SMOKE", "false"))) {
            Minecraft.getInstance().reloadResourcePacks().thenRun(() -> Minecraft.getInstance().execute(() -> {
                FXHelper.clearCache();
                var fx = FXHelper.getFX(new ResourceLocation("photon", "codex_smoke"));
                var minecraft = Minecraft.getInstance();
                if (fx != null && minecraft.level != null && minecraft.player != null) {
                    var effect = new BlockEffect(fx, minecraft.level, minecraft.player.blockPosition());
                    effect.setAllowMulti(true);
                    effect.start();
                    Photon.LOGGER.info("Photon dev smoke spawned exported FX at {}", minecraft.player.blockPosition());
                } else {
                    Photon.LOGGER.warn("Photon dev smoke could not reload/spawn exported FX. fx={} level={} player={}",
                            fx != null, minecraft.level != null, minecraft.player != null);
                }
            }));
        }
    }

    private List<IFXObject> createDevSmokeEmitters() {
        if (Boolean.getBoolean("photon.autoNoEmitter") ||
                Boolean.parseBoolean(System.getenv().getOrDefault("PHOTON_AUTO_NO_EMITTER", "false"))) {
            return List.of();
        }
        var requested = System.getProperty("photon.autoEmitters");
        if (requested == null || requested.isBlank()) {
            requested = System.getenv().getOrDefault("PHOTON_AUTO_EMITTERS", "particle");
        }

        var emitters = new ArrayList<IFXObject>();
        for (var rawName : requested.split(",")) {
            var name = rawName.trim().toLowerCase();
            if (name.isEmpty()) continue;
            IFXObject emitter = switch (name) {
                case "beam" -> new BeamEmitter();
                case "trail" -> new TrailEmitter();
                case "particle", "particles" -> new ParticleEmitter();
                default -> null;
            };
            if (emitter != null) {
                emitter.setName(name.equals("particles") ? "particle" : name);
                emitters.add(emitter);
            }
        }
        if (emitters.isEmpty()) {
            var fallback = new ParticleEmitter();
            fallback.setName("particle");
            emitters.add(fallback);
        }
        return emitters;
    }

}
