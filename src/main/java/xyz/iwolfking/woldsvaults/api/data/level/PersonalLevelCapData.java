package xyz.iwolfking.woldsvaults.api.data.level;

import iskallia.vault.init.ModGameRules;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import xyz.iwolfking.woldsvaults.init.ModNetwork;
import xyz.iwolfking.woldsvaults.network.message.S2CSyncLevelCapMessage;

import java.util.*;

public class PersonalLevelCapData extends SavedData {
    private static final String DATA_NAME = "woldsvaults_Personal_Levelcap";
    private static final ResearchCapData DEFAULT = new ResearchCapData(20, 1);

    protected Map<UUID, ResearchCapData> playerCap = new HashMap<>();

    private PersonalLevelCapData() {
    }

    private PersonalLevelCapData(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag pCompoundTag) {
        ListTag levelCapData = new ListTag();
        for (var entry : playerCap.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("player", entry.getKey());
            playerTag.putInt("currentCap", entry.getValue().currentCap());
            playerTag.putInt("researchCost", entry.getValue().researchCost());
            levelCapData.add(playerTag);
        }
        pCompoundTag.put("data", levelCapData);
        return pCompoundTag;
    }

    public void load(CompoundTag pCompoundTag) {
        this.playerCap.clear();
        ListTag data = pCompoundTag.getList("data", Tag.TAG_LIST);

        for (int i = 0; i < data.size(); i++) {
            CompoundTag levelCapData = data.getCompound(i);
            UUID playerId = levelCapData.getUUID("player");
            int currentCap = levelCapData.getInt("currentCap");
            int researchCost = levelCapData.getInt("researchCost");
            this.playerCap.put(playerId, new ResearchCapData(currentCap, researchCost));
        }
    }

    @Override
    public void setDirty(boolean pDirty) {
        super.setDirty(pDirty);
        if (pDirty) {
            MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
            if (srv != null) {
                srv.getPlayerList().getPlayers().forEach(this::syncTo);
            }
        }
    }

    public void syncTo(ServerPlayer player) {
        ModNetwork.CHANNEL.sendTo(this.getUpdatePacket(player.getUUID()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    private S2CSyncLevelCapMessage getUpdatePacket(UUID playerId) {
        return new S2CSyncLevelCapMessage(playerCap.getOrDefault(playerId, DEFAULT).currentCap(), playerCap.getOrDefault(playerId, DEFAULT).researchCost(), ServerLifecycleHooks.getCurrentServer().overworld().getGameRules().getRule(ModGameRules.LEVEL_LOCK).get());
    }


    public boolean reachedCap(UUID player) {
        return playerCap.getOrDefault(player, DEFAULT).currentCap() >= 100;
    }

    public int getPersonalCap(UUID player) {
        return playerCap.getOrDefault(player, DEFAULT).currentCap();
    }


    public void increaseLevelCap(ServerPlayer player) {
        PlayerVaultStatsData statsData = PlayerVaultStatsData.get(player.getLevel());
        PlayerVaultStats stats = statsData.getVaultStats(player);
        int currentPoints = stats.getUnspentKnowledgePoints();
        ResearchCapData capData = playerCap.getOrDefault(player.getUUID(), DEFAULT);
        if (currentPoints >= capData.researchCost) {
            statsData.spendKnowledgePoints(player, capData.researchCost);
            playerCap.put(player.getUUID(), new ResearchCapData(capData.currentCap + 1, computeCost(capData.currentCap + 1)));
            this.setDirty();
        }
    }

    public void resetPersonalCap(UUID player) {
        this.playerCap.put(player, DEFAULT);
        this.setDirty();
    }
    public void setPersonalCap(UUID player, int cap) {
        this.playerCap.put(player, new ResearchCapData(cap, computeCost(cap)));
        this.setDirty();
    }

    private static int computeCost(int currentCap) {
        return Math.max(1, (int) Math.round(0.72 * Math.pow(Math.E, 0.026 * currentCap))); // god is dead.
    }


    public static PersonalLevelCapData get(ServerLevel level) {
        return get(level.getServer());
    }

    public static PersonalLevelCapData get(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(PersonalLevelCapData::new, PersonalLevelCapData::new, DATA_NAME);
    }

    public record ResearchCapData(int currentCap, int researchCost) {}
}
