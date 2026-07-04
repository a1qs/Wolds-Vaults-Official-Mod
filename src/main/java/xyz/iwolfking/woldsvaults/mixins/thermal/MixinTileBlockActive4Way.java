package xyz.iwolfking.woldsvaults.mixins.thermal;

import cofh.core.block.TileBlockActive;
import cofh.core.block.TileBlockActive4Way;
import cofh.lib.api.block.IWrenchable;
import cofh.thermal.expansion.block.entity.machine.MachineCrafterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import xyz.iwolfking.woldsvaults.api.lib.IPlayerOwnedBlock;

import java.util.function.Supplier;

@Mixin(value = TileBlockActive4Way.class, remap = false)
public class MixinTileBlockActive4Way extends TileBlockActive implements IWrenchable {

    public MixinTileBlockActive4Way(Properties builder, Class<?> tileClass, Supplier<BlockEntityType<?>> blockEntityType) {
        super(builder, tileClass, blockEntityType);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (!pLevel.isClientSide() && pPlacer instanceof Player) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MachineCrafterTile)
                ((IPlayerOwnedBlock)blockEntity).setPlacingPlayer((Player)pPlacer);
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }
}
