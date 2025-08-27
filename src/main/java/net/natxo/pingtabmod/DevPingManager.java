package net.natxo.pingtabmod;

import net.fabricmc.loader.api.FabricLoader;

public class DevPingManager {
    private static int simulatedPing = -1;
    private static boolean enabled = false;
    
    static {
        // Solo activar en desarrollo
        enabled = FabricLoader.getInstance().isDevelopmentEnvironment();
    }
    
    public static boolean isEnabled() {
        return enabled && simulatedPing >= 0;
    }
    
    public static int getSimulatedPing() {
        return simulatedPing;
    }
    
    public static void setSimulatedPing(int ping) {
        if (enabled) {
            simulatedPing = ping;
            PingTabMod.LOGGER.info("Dev mode: Simulated ping set to " + ping + "ms");
        }
    }
    
    public static void disable() {
        simulatedPing = -1;
        if (enabled) {
            PingTabMod.LOGGER.info("Dev mode: Simulated ping disabled");
        }
    }
    
    public static int getPingForPlayer(int realPing) {
        if (isEnabled()) {
            return simulatedPing;
        }
        return realPing;
    }
}