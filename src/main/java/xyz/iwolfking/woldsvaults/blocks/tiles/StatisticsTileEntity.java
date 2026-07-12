package xyz.iwolfking.woldsvaults.blocks.tiles;

import iskallia.vault.init.ModGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.api.data.level.WorldExpansionData;
import xyz.iwolfking.woldsvaults.api.util.ComponentUtils;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public class StatisticsTileEntity extends BlockEntity {
    private static final int UPDATE_INTERVAL = 5 * 20; // 5 seconds

    private int globalLevelCap;
    private int toNextThreshold;
    private int totalContributions;
    private boolean reachedMaxLevelCap;


    public StatisticsTileEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.STATISTICS_TILE_ENTITY, pPos, pBlockState);
        updateData();
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (level.getGameTime() % UPDATE_INTERVAL == 0) {
            this.updateData();
        }
    }

    private void updateData() {
        if (level != null && level instanceof ServerLevel sLevel) {
            WorldExpansionData data = WorldExpansionData.get(sLevel);
            globalLevelCap = sLevel.getGameRules().getRule(ModGameRules.LEVEL_LOCK).get();
            toNextThreshold = data.totalContributionsForLevel(globalLevelCap + 1);
            totalContributions = data.getTotalContributions();

            if (globalLevelCap == 100) {
                reachedMaxLevelCap = true;
            }


            setChanged();
            sLevel.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
            sLevel.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        }
    }

    public int getGlobalLevelCap() {
        return globalLevelCap;
    }

    public int getToNextThreshold() {
        return toNextThreshold;
    }

    public int getTotalContributions() {
        return totalContributions;
    }

    public boolean hasReachedMaxLevelCap() {
        return reachedMaxLevelCap;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        globalLevelCap = tag.getInt("globalLevelCap");
        toNextThreshold = tag.getInt("toNextThreshold");
        totalContributions = tag.getInt("totalContributions");
        reachedMaxLevelCap = tag.getBoolean("reachedMaxLevelCap");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("globalLevelCap", globalLevelCap);
        tag.putInt("toNextThreshold", toNextThreshold);
        tag.putInt("totalContributions", totalContributions);
        tag.putBoolean("reachedMaxLevelCap", reachedMaxLevelCap);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


}
