package xyz.iwolfking.woldsvaults.network.message;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.api.data.level.ClientPersonalLevelCapData;

import java.util.function.Supplier;

public class S2CSyncGlobalCap {
    private final int globalLevelCap;

    public S2CSyncGlobalCap(int pGlobalLevelCap) {
        globalLevelCap = pGlobalLevelCap;
    }

    public static void encode(S2CSyncGlobalCap message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.globalLevelCap);
    }

    public static S2CSyncGlobalCap decode(FriendlyByteBuf buffer) {
        return new S2CSyncGlobalCap(buffer.readInt());
    }


    public static void handle(S2CSyncGlobalCap message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientPersonalLevelCapData.setGlobalLevelCap(message.globalLevelCap);
            VaultBarOverlay.onVaultLevelChanged(VaultBarOverlay.vaultLevel); // Just to refresh the text.
        });
        context.setPacketHandled(true);
    }
}
