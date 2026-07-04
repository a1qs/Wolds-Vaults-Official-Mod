package xyz.iwolfking.woldsvaults.api.data.level;

import iskallia.vault.init.ModGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldExpansionData extends SavedData {
    private static final String DATA_NAME = "woldsvaults_World_Expansion";
    private final Map<UUID, Integer> playerContributions = new HashMap<>();

    public static WorldExpansionData load(CompoundTag nbt) {
        WorldExpansionData data = new WorldExpansionData();
        ListTag playersList = nbt.getList("Players", 10); // 10 = CompoundTag type

        for (int i = 0; i < playersList.size(); i++) {
            CompoundTag playerTag = playersList.getCompound(i);
            UUID playerId = UUID.fromString(playerTag.getString("UUID"));
            int powerValue = playerTag.getInt("Contributions");
            data.playerContributions.put(playerId, powerValue);
        }

        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        ListTag playersList = new ListTag();

        for (Map.Entry<UUID, Integer> entry : playerContributions.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putString("UUID", entry.getKey().toString());
            playerTag.putInt("Contributions", entry.getValue());
            playersList.add(playerTag);
        }

        nbt.put("Players", playersList);
        return nbt;
    }


    public void setPlayerContributions(UUID playerId, int contributedGemstones) {
        playerContributions.put(playerId, contributedGemstones);
        this.setDirty();
    }

    public int getPlayerContributions(UUID playerId) {
        return playerContributions.getOrDefault(playerId, 0);
    }

    public void addContribution(UUID playerId, int contributed) {
        playerContributions.put(playerId, playerContributions.getOrDefault(playerId, 0) + contributed);
        this.setDirty();
    }


    public Map<UUID, Integer> getPlayerContributionsMap() {
        return playerContributions;
    }

    public void resetContributionsForPlayer(UUID playerId) {
        playerContributions.remove(playerId);
        this.setDirty();
    }


    @Override
    public void setDirty() {
        super.setDirty();
        boolean changed = processLevelCap();
        if (changed) {
            MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
            srv.getPlayerList().broadcastMessage(
                    new TextComponent("> The Level Cap has been increased to ").withStyle(ChatFormatting.YELLOW).append(new TextComponent("Level " + srv.getGameRules().getRule(ModGameRules.LEVEL_LOCK).get()))

                    , ChatType.CHAT, Util.NIL_UUID);
        }
    }

    public int getTotalContributions() {
        return playerContributions
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public boolean processLevelCap() {
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        int totalContributions = getTotalContributions();
        int currentLevelCap = srv.overworld().getGameRules().getRule(ModGameRules.LEVEL_LOCK).get();
        if (currentLevelCap >= 100) return false;

        int totalContributionsRequiredForLevel = totalContributionsForLevel(currentLevelCap + 1);

        if (totalContributions >= totalContributionsRequiredForLevel) {
            srv.getGameRules().getRule(ModGameRules.LEVEL_LOCK).set(currentLevelCap + 1, srv);

            processLevelCap(); // Check again in-case it surpassed multiple.
            return true;
        }
        return false;
    }


    // Returns total contributions required for a given level to be unlocked
    // E.g @param level = 10 -> Requires 43
    public int totalContributionsForLevel(int level) {
        int contributionsRequired = 0;

        for (int i = 10; i <= level; i++) {
            contributionsRequired = contributionsRequired + (int) Math.round(Math.pow(i, 0.6 * Math.E));
        }

        return contributionsRequired;
    }


    public static WorldExpansionData get(ServerLevel level) {
        return get(level.getServer());
    }

    public static WorldExpansionData get(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(WorldExpansionData::load, WorldExpansionData::new, DATA_NAME);
    }


}
