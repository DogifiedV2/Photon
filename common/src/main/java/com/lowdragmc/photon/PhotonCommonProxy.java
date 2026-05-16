package com.lowdragmc.photon;

import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.photon.gui.FXEditorFactory;

public class PhotonCommonProxy {
    public static void init() {
        UIFactory.register(FXEditorFactory.INSTANCE);
        PhotonNetworking.init();
    }

}
