package xyz.iwolfking.woldsvaults.network.message;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.iwolfking.woldsvaults.api.data.level.ClientPersonalLevelCapData;
import xyz.iwolfking.woldsvaults.api.data.level.PersonalLevelCapData;

import java.util.function.Supplier;

public class S2CSyncLevelCapMessage {
    private final int currentCap;
    private final int researchCost;
    private final int globalLevelCap;

    public S2CSyncLevelCapMessage(int pCurrentCap, int pResearchCost, int pGlobalLevelCap) {
        currentCap = pCurrentCap;
        researchCost = pResearchCost;
        globalLevelCap = pGlobalLevelCap;
    }

    public static void encode(S2CSyncLevelCapMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.currentCap);
        buffer.writeInt(message.researchCost);
        buffer.writeInt(message.globalLevelCap);
    }

    public static S2CSyncLevelCapMessage decode(FriendlyByteBuf buffer) {
        return new S2CSyncLevelCapMessage(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }


    public static void handle(S2CSyncLevelCapMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientPersonalLevelCapData.receiveMessage(new PersonalLevelCapData.ResearchCapData(message.currentCap, message.researchCost));
            ClientPersonalLevelCapData.setGlobalLevelCap(message.globalLevelCap);
            VaultBarOverlay.onVaultLevelChanged(VaultBarOverlay.vaultLevel); // Just to refresh the text.
        });
        context.setPacketHandled(true);
    }
}
