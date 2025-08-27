package net.natxo.pingtabmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class PingTabModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Solo registrar comandos de desarrollo si estamos en modo desarrollo
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
                        context.getSource().sendFeedback(Text.literal("§e[DEV] Ping simulado establecido a " + ping + "ms"));
                        return 1;
                    }))
                .then(ClientCommandManager.literal("off")
                    .executes(context -> {
                        DevPingManager.disable();
                        context.getSource().sendFeedback(Text.literal("§e[DEV] Ping simulado desactivado"));
                        return 1;
                    }))
                .executes(context -> {
                    context.getSource().sendFeedback(Text.literal("§e[DEV] Uso: /devping <ms> o /devping off"));
                    return 1;
                }));
        });
    }
}
