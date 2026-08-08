package xyz.iwolfking.woldsvaults.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.api.data.level.WorldExpansionData;
import xyz.iwolfking.woldsvaults.init.ModNetwork;

import java.util.function.Supplier;

public class LeaderboardRequestMessage {
    public LeaderboardRequestMessage() {}

    public static void encode(LeaderboardRequestMessage msg, FriendlyByteBuf buffer) {}

    public static LeaderboardRequestMessage decode(FriendlyByteBuf buffer) {
        return new LeaderboardRequestMessage();
    }

    public static void handle(LeaderboardRequestMessage ignoredMsg, Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayer player = contextSupplier.get().getSender();
        if (player != null) {
            WorldExpansionData data = WorldExpansionData.get(player.getLevel());

            ModNetwork.sendToClient(new LeaderboardDataMessage(data.getPlayerContributionsMap()), player);
        }
        contextSupplier.get().setPacketHandled(true);
    }
}
