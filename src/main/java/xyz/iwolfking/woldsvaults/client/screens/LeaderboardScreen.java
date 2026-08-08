package xyz.iwolfking.woldsvaults.client.screens;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.NotNull;
import xyz.iwolfking.woldsvaults.api.util.ComponentUtils;

import java.util.*;

public class LeaderboardScreen extends Screen {
    private final Map<UUID, Integer> leaderboard;


    public LeaderboardScreen(Component title, Map<UUID, Integer> leaderboard) {
        super(title);
        this.leaderboard = leaderboard;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        int renderX = (int) (this.width * 0.85);
        int renderY = (int) (this.height * 0.6);
        int scale = Math.min(this.width, this.height) / 8;

        renderBackgroundAndTitle(pPoseStack);
        renderLeaderboard(pPoseStack, this.width / 2, 30);
        renderPlayerPosition(pPoseStack, renderX, renderY + 10);
        renderPlayerModel(pMouseX, pMouseY, renderX, renderY, scale);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    /* Actual render methods */
    private void renderLeaderboard(PoseStack pPoseStack, int x, int startY) {
        List<Map.Entry<UUID, Integer>> sortedLeaderboard = this.leaderboard.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
                .toList();

        List<Map.Entry<UUID, Integer>> top10 = new ArrayList<>(sortedLeaderboard.subList(0, Math.min(10, sortedLeaderboard.size())));

        // Ensure the list contains exactly 10 entries by padding it
        for (int i = top10.size(); i < 10; i++) {
            top10.add(new AbstractMap.SimpleEntry<>(null, -1));
        }

        int y = startY;
        int lineSpacing = 15;

        for (int placement = 1; placement <= top10.size(); placement++) {
            Map.Entry<UUID, Integer> entry = top10.get(placement - 1);

            String user = (entry.getKey() != null)
                    ? (entry.getKey().equals(this.minecraft.player.getUUID())
                       ? this.minecraft.player.getScoreboardName()
                       : getUsernameFromUUID(entry.getKey()))
                    : null;
            String contribution = entry.getValue() != -1 ? String.valueOf(entry.getValue()) : "0";


            if (user != null) {
                // Centered leaderboard text
                MutableComponent component = getPlacementComponent(placement, user, contribution);
                Minecraft.getInstance().font.drawShadow(pPoseStack, component, x - this.font.width(component) / 2.0f, y, 0xFFFFFF);
                y += lineSpacing;
            }


        }
    }


    private void renderPlayerPosition(PoseStack pPoseStack, int x, int y) {
        MutableComponent splitter = new TextComponent(" — Your Position — ");
        Minecraft.getInstance().font.drawShadow(pPoseStack, splitter, x - this.font.width(splitter) / 2.0f, y, 0xFFFFFF);

        UUID playerUUID = Minecraft.getInstance().player.getUUID();
        List<Map.Entry<UUID, Integer>> sortedLeaderboard = this.leaderboard.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
                .toList();

        int playerPlacement = -1;
        int playerContributions = 0;
        int lineSpacing = 15;
        int adjustedX;

        // Find player's position
        for (int i = 0; i < sortedLeaderboard.size(); i++) {
            if (sortedLeaderboard.get(i).getKey().equals(playerUUID)) {
                playerPlacement = i + 1; // Placement is index + 1
                playerContributions = sortedLeaderboard.get(i).getValue();
                break;
            }
        }

        MutableComponent component = playerPlacement != -1
                ? getPlacementComponent(playerPlacement, getUsernameFromUUID(playerUUID), String.valueOf(playerContributions))
                : getPlacementComponent(-999, getUsernameFromUUID(playerUUID), "???");

        adjustedX = adjustXToFit(component.getString(), x);
        Minecraft.getInstance().font.drawShadow(pPoseStack, component, adjustedX - this.font.width(component) / 2.0f, y + lineSpacing, 0xFFFFFF);
    }

    private void renderBackgroundAndTitle(PoseStack pPoseStack) {
        this.renderBackground(pPoseStack);
        drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    private void renderPlayerModel(int pMouseX, int pMouseY, int pRenderX, int pRenderY, int pScale) {
        float mouseXOffset = pRenderX - pMouseX;
        float mouseYOffset = pRenderY - (pMouseY + pScale);
        InventoryScreen.renderEntityInInventory(pRenderX, pRenderY, pScale, mouseXOffset, mouseYOffset, this.minecraft.player);
    }

    private int adjustXToFit(String text, int centerX) {
        int screenWidth = this.width;
        int textWidth = this.font.width(text);
        int leftEdge = centerX - textWidth / 2;
        int rightEdge = centerX + textWidth / 2;

        if (leftEdge < 0) {
            return centerX - leftEdge + 5; // Shift right to align left edge + 5 buffer
        } else if (rightEdge > screenWidth) {
            return centerX - (rightEdge - screenWidth) -5; // Shift left to align right edge - 5 buffer
        } else {
            return centerX; // No adjustment needed
        }
    }

    private static String getUsernameFromUUID(UUID uuid) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            for (PlayerInfo playerInfo : minecraft.getConnection().getOnlinePlayers()) {
                if (playerInfo.getProfile().getId().equals(uuid)) {
                    return playerInfo.getProfile().getName();
                }
            }
        }

        // Fallback: Attempt to resolve the profile using the session service
        if(uuid == null) return "Unknown";
        GameProfile profile = minecraft.getMinecraftSessionService().fillProfileProperties(new GameProfile(uuid, null), false);
        if (profile != null && profile.getName() != null) {
            return profile.getName();
        }

        return "Unknown"; // Fallback if no name is found
    }

    private static MutableComponent getPlacementComponent(int placement, String user, String contribution) {
        MutableComponent base = new TextComponent("#");
        MutableComponent placementComponent = switch (placement) {
            case 1 -> base.append(ComponentUtils.wavingComponent(new TextComponent("1"),0xfcba03, 0.05F, 0.2F));
            case 2 -> base.append(ComponentUtils.wavingComponent(new TextComponent("2"), 0xdedede,0.05F, 0.2F));
            case 3 -> base.append(ComponentUtils.wavingComponent(new TextComponent("3"), 0xCE8946, 0.05F, 0.2F));
            default -> base.append(new TextComponent(String.valueOf(placement)));
        };

        MutableComponent userComponent = switch (placement) {
            case 1 -> ComponentUtils.wavingComponent(new TextComponent(user),0xfcba03, 0.1F, 0.4F);
            case 2 -> ComponentUtils.wavingComponent(new TextComponent(user), 0xdedede,0.1F, 0.4F);
            case 3 -> ComponentUtils.wavingComponent(new TextComponent(user), 0xCE8946, 0.1F, 0.4F);
            default -> base.append(new TextComponent(user));
        };

        return placementComponent
                .append(new TextComponent(" — ").withStyle(ChatFormatting.DARK_GRAY))
                .append(userComponent)
                .append(new TextComponent(" — ").withStyle(ChatFormatting.DARK_GRAY))
                .append(new TextComponent(contribution).withStyle(ChatFormatting.YELLOW))
                .append(new TextComponent(" Contributions").withStyle(ChatFormatting.GRAY));
    }
}
