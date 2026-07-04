package xyz.iwolfking.woldsvaults.api.data.level;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import net.minecraft.client.Minecraft;

public class ClientPersonalLevelCapData {
    private static PersonalLevelCapData.ResearchCapData researchCapData = new PersonalLevelCapData.ResearchCapData(20, 1);
    private static int globalLevelCap = 100;

    public static PersonalLevelCapData.ResearchCapData getResearchCapData() {
        return researchCapData;
    }


    public static int getGlobalLevelCap() {
        return globalLevelCap;
    }

    public static void setGlobalLevelCap(int globalLevelCap) {
        ClientPersonalLevelCapData.globalLevelCap = globalLevelCap;
    }

    public static void receiveMessage(PersonalLevelCapData.ResearchCapData newData) {
        researchCapData = newData;
    }

    // has the player reached the GLOBAL!!! level cap?
    public static boolean reachedCap() {
        if (Minecraft.getInstance().level != null) {

            return getGlobalLevelCap() <= VaultBarOverlay.vaultLevel;
        }
        return true;
    }
}
