package com.lowdragmc.photon.client.emitter.data;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurable;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.SwitchWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import lombok.Getter;
import lombok.Setter;

/**
 * @author KilaBash
 * @date 2023/5/30
 * @implNote ToggleGroup
 */
public class ToggleGroup implements IConfigurable {

    @Getter
    @Setter
    @Persisted
    protected boolean enable;

    @Override
    public void buildConfigurator(ConfiguratorGroup father) {
        IConfigurable.super.buildConfigurator(father);
        if (!enable) {
            father.setCanCollapse(false);
            var name = father.getNameWidget();
            if (name != null) {
                name.setTextColor(ColorPattern.GRAY.color);
            }
        } else {
            father.setCanCollapse(true);
            var name = father.getNameWidget();
            if (name != null) {
                name.setTextColor(ColorPattern.WHITE.color);
            }
        }
        father.addWidget(new SwitchWidget(father.getLeftWidth() + 12, 1, 12, 12, (cd, pressed) -> {
            enable = pressed;
            if (!enable) {
                father.setCanCollapse(false);
                father.setCollapse(true);
                var name = father.getNameWidget();
                if (name != null) {
                    name.setTextColor(ColorPattern.GRAY.color);
                }
            } else {
                father.setCanCollapse(true);
                var name = father.getNameWidget();
                if (name != null) {
                    name.setTextColor(ColorPattern.WHITE.color);
                }
            }
        })
                .setPressed(enable)
                .setTexture(new GuiTextureGroup(ColorPattern.PANEL_DARK.rectTexture().setRadius(2), ColorPattern.LIGHT_GRAY.borderTexture(1).setRadius(2)),
                        new GuiTextureGroup(ColorPattern.PANEL_DARK.rectTexture().setRadius(2), ColorPattern.GREEN.borderTexture(1).setRadius(2),
                                new TextTexture("✓", ColorPattern.GREEN.color).setWidth(12).setDropShadow(false)))
                .setHoverTexture(new GuiTextureGroup(ColorPattern.PANEL_HOVER.rectTexture().setRadius(2), ColorPattern.WHITE.borderTexture(1).setRadius(2)))
                .setHoverTooltips("enable/disable"));
    }

}
