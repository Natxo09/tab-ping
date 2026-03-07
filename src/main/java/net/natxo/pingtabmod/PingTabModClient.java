package net.natxo.pingtabmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class PingTabModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            registerDevCommands();
        }
    }

    private void registerDevCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("devping")
                .then(ClientCommandManager.argument("milliseconds", IntegerArgumentType.integer(0, 9999))
                    .executes(context -> {
                        int ping = IntegerArgumentType.getInteger(context, "milliseconds");
                        DevPingManager.setSimulatedPing(ping);
                        context.getSource().sendFeedback(Component.literal("\u00A7e[DEV] Simulated ping set to " + ping + "ms"));
                        return 1;
                    }))
                .then(ClientCommandManager.literal("off")
                    .executes(context -> {
                        DevPingManager.disable();
                        context.getSource().sendFeedback(Component.literal("\u00A7e[DEV] Simulated ping disabled"));
                        return 1;
                    }))
                .executes(context -> {
                    context.getSource().sendFeedback(Component.literal("\u00A7e[DEV] Usage: /devping <ms> or /devping off"));
                    return 1;
                }));
        });
    }
}
