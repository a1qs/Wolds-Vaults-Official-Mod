package xyz.iwolfking.woldsvaults.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.api.data.level.PersonalLevelCapData;

import java.util.function.Supplier;

@SuppressWarnings("InstantiationOfUtilityClass")
public class C2SIncreaseLevelCapMessage {
    public C2SIncreaseLevelCapMessage() {
    }

    public static void encode(C2SIncreaseLevelCapMessage ignoredMsg, FriendlyByteBuf ignoredBuffer) {
    }

    public static C2SIncreaseLevelCapMessage decode(FriendlyByteBuf ignored) {
        return new C2SIncreaseLevelCapMessage();
    }

    public static void handle(C2SIncreaseLevelCapMessage ignored, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {

                PersonalLevelCapData data = PersonalLevelCapData.get(player.getLevel());
                if (!data.reachedCap(player.getUUID())) {
                    data.increaseLevelCap(player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
