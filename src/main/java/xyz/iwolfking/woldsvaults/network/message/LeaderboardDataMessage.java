package xyz.iwolfking.woldsvaults.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.client.ClientMessageHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class LeaderboardDataMessage {
    public final Map<UUID, Integer> leaderboard;

    public LeaderboardDataMessage(Map<UUID, Integer> leaderboard) {
        this.leaderboard = leaderboard;
    }

    public static void encode(LeaderboardDataMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.leaderboard.size());
        msg.leaderboard.forEach((uuid, count) -> {
            buffer.writeUUID(uuid);
            buffer.writeInt(count);
        });
    }

    public static LeaderboardDataMessage decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        Map<UUID, Integer> leaderboard = new HashMap<>();
        for (int i = 0; i < size; i++) {
            UUID uuid = buffer.readUUID();
            int score = buffer.readInt();
            leaderboard.put(uuid, score);
        }

        return new LeaderboardDataMessage(leaderboard);
    }

    public static void handle(LeaderboardDataMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            if (FMLEnvironment.dist.isClient()) {
                ClientMessageHandler.handle(msg);
            }
        });

        contextSupplier.get().setPacketHandled(true);
    }
}
