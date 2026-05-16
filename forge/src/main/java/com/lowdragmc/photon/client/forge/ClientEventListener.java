package com.lowdragmc.photon.client.forge;

import com.lowdragmc.photon.Photon;
import com.lowdragmc.photon.IrisFramebufferUtils;
import com.lowdragmc.photon.client.ClientCommands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * @author KilaBash
 * @date 2022/5/12
 * @implNote EventListener
 */
@Mod.EventBusSubscriber(modid = Photon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ClientEventListener {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        Photon.LOGGER.info("Registering Photon client commands");
        var dispatcher = event.getDispatcher();
        List<LiteralArgumentBuilder<CommandSourceStack>> commands = ClientCommands.createClientCommands();
        commands.forEach(command -> {
            Photon.LOGGER.info("Registering Photon client command: /{}", command.getLiteral());
            dispatcher.register(command);
        });
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientCommands.openPendingEditor();
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            IrisFramebufferUtils.setRenderingGUIScreen(true);
        }

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            IrisFramebufferUtils.setRenderingGUIScreen(false);
        }
    }

}
