package com.lowdragmc.photon.gui;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.photon.Photon;
import com.lowdragmc.photon.gui.editor.FXEditor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class FXEditorFactory extends UIFactory<FXEditorFactory> implements IUIHolder {
    public static final FXEditorFactory INSTANCE = new FXEditorFactory();

    private FXEditorFactory() {
        super(Photon.id("editor"));
    }

    @Override
    protected ModularUI createUITemplate(FXEditorFactory holder, Player entityPlayer) {
        return createUI(entityPlayer);
    }

    @Override
    protected FXEditorFactory readHolderFromSyncData(FriendlyByteBuf syncData) {
        return this;
    }

    @Override
    protected void writeHolderToSyncData(FriendlyByteBuf syncData, FXEditorFactory holder) {
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(this, entityPlayer)
                .widget(new FXEditor(LDLib.getLDLibDir()));
    }

    @Override
    public boolean isInvalid() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return LDLib.isRemote();
    }

    @Override
    public void markAsDirty() {
    }
}
