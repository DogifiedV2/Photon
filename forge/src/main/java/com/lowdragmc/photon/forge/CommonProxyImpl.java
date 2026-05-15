package com.lowdragmc.photon.forge;

import com.lowdragmc.photon.Photon;
import com.lowdragmc.photon.PhotonCommonProxy;
import com.lowdragmc.photon.ServerCommands;
import com.lowdragmc.photon.command.FxLocationArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonProxyImpl {

    public CommonProxyImpl() {
        // used for forge events (ClientProxy + CommonProxy)
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        // register server commands
        MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
        // init common features
        PhotonCommonProxy.init();
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ArgumentTypes.register(Photon.MOD_ID + ":fx_location", FxLocationArgument.class, new EmptyArgumentSerializer<>(FxLocationArgument::new)));
    }

    public void registerCommand(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        ServerCommands.createServerCommands().forEach(dispatcher::register);
    }
}
