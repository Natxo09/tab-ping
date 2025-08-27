package net.natxo.pingtabmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    
    @Shadow @Final private MinecraftClient client;
    @Shadow protected abstract List<PlayerListEntry> collectPlayerEntries();
    
    private static final int ENTRY_HEIGHT = 9;
    private static final int PADDING = 1;
    private static final int PING_WIDTH = 35; // Fixed width for ping display
    private static final int NAME_PING_GAP = 5; // Gap between name and ping
    
    /**
     * Completely override the rendering of player entries in the tab list
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void customRender(DrawContext context, int screenWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        // Check if we should render
        if (this.client.options.playerListKey.isPressed() && (!this.client.isInSingleplayer() || this.client.player.networkHandler.getListedPlayerListEntries().size() > 1)) {
            
            List<PlayerListEntry> entries = this.collectPlayerEntries();
            if (entries.isEmpty()) {
                ci.cancel();
                return;
            }
            
            // Calculate dimensions
            int maxNameWidth = 0;
            for (PlayerListEntry entry : entries) {
                Text displayName = entry.getDisplayName() != null ? entry.getDisplayName() : Text.literal(entry.getProfile().getName());
                maxNameWidth = Math.max(maxNameWidth, this.client.textRenderer.getWidth(displayName));
            }
            
            // Limit max name width to prevent extremely long names
            maxNameWidth = Math.min(maxNameWidth, 150);
            
            // Total width for each entry
            int entryWidth = maxNameWidth + NAME_PING_GAP + PING_WIDTH + (PADDING * 2);
            
            // Calculate number of columns and rows
            int maxRows = Math.min(entries.size(), 20); // Max 20 rows
            int columns = (entries.size() + maxRows - 1) / maxRows;
            
            // Calculate starting position (centered)
            int totalWidth = entryWidth * columns;
            int startX = (screenWidth - totalWidth) / 2;
            int startY = 10;
            
            // Background
            int backgroundHeight = maxRows * ENTRY_HEIGHT + PADDING * 2;
            context.fill(startX - PADDING, startY - PADDING, startX + totalWidth + PADDING, startY + backgroundHeight, 0x80000000);
            
            // Render entries
            int index = 0;
            for (PlayerListEntry entry : entries) {
                int column = index / maxRows;
                int row = index % maxRows;
                
                int x = startX + column * entryWidth;
                int y = startY + row * ENTRY_HEIGHT;
                
                renderCustomEntry(context, entry, x, y, maxNameWidth);
                index++;
            }
            
            ci.cancel();
        }
    }
    
    /**
     * Custom rendering for each player entry
     */
    private void renderCustomEntry(DrawContext context, PlayerListEntry entry, int x, int y, int maxNameWidth) {
        TextRenderer textRenderer = this.client.textRenderer;
        
        // Get display name
        Text displayName = entry.getDisplayName() != null ? entry.getDisplayName() : Text.literal(entry.getProfile().getName());
        
        // Truncate name if too long
        String nameString = displayName.getString();
        int nameWidth = textRenderer.getWidth(nameString);
        if (nameWidth > maxNameWidth) {
            // Truncate and add "..."
            while (nameWidth > maxNameWidth - textRenderer.getWidth("...") && nameString.length() > 0) {
                nameString = nameString.substring(0, nameString.length() - 1);
                nameWidth = textRenderer.getWidth(nameString);
            }
            nameString += "...";
        }
        
        // Draw player name
        context.drawText(textRenderer, nameString, x + PADDING, y, 0xFFFFFF, false);
        
        // Draw ping
        int pingX = x + maxNameWidth + NAME_PING_GAP + PADDING;
        drawPing(context, entry, pingX, y);
        
        // Draw gamemode indicator if spectator
        if (entry.getGameMode() == GameMode.SPECTATOR) {
            // Make the text slightly transparent for spectators
            context.drawText(textRenderer, nameString, x + PADDING, y, 0x7F7F7F, false);
        }
    }
    
    /**
     * Draw the ping value with color coding
     */
    private void drawPing(DrawContext context, PlayerListEntry entry, int x, int y) {
        TextRenderer textRenderer = this.client.textRenderer;
        
        // Get ping value
        int ping = net.natxo.pingtabmod.DevPingManager.getPingForPlayer(entry.getLatency());
        
        // Determine color
        Formatting pingColor;
        if (ping < 70) {
            pingColor = Formatting.GREEN;
        } else if (ping < 100) {
            pingColor = Formatting.YELLOW;
        } else if (ping < 200) {
            pingColor = Formatting.GOLD;
        } else {
            pingColor = Formatting.RED;
        }
        
        // Format ping text
        String pingText = ping > 999 ? "999+" : String.valueOf(ping);
        
        // Draw ping
        int color = pingColor.getColorValue() != null ? pingColor.getColorValue() : 0xFFFFFF;
        context.drawText(textRenderer, pingText, x, y, color, false);
    }
}