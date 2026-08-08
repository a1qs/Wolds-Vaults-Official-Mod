package xyz.iwolfking.woldsvaults.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import xyz.iwolfking.woldsvaults.client.screens.LeaderboardScreen;
import xyz.iwolfking.woldsvaults.network.message.LeaderboardDataMessage;

public class ClientMessageHandler {
    public static void handle(LeaderboardDataMessage msg) {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(
                    new LeaderboardScreen(
                            new TextComponent("Leaderboard"),
                            msg.leaderboard
                    )
            );
        });
    }
}
