package xyz.iwolfking.woldsvaults.items.world;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xyz.iwolfking.woldsvaults.api.util.ComponentUtils;

import javax.annotation.Nullable;
import java.util.List;

public class PowerShardItem extends Item {
    private final double increase;

    public PowerShardItem(ResourceLocation id, Properties pProperties, double pIncrease) {
        super(pProperties);
        this.setRegistryName(id);
        this.increase = pIncrease;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(new TextComponent("Use on the ").withStyle(ChatFormatting.GRAY)
                .append(ComponentUtils.wavingComponent(new TextComponent("World Expander"), 0xF7C707, 0.05F, 0.4F))
                .append(new TextComponent(" to increase the World Border.").withStyle(ChatFormatting.GRAY))
        );
    }

    @Override
    public Component getName(ItemStack pStack) {
        return ComponentUtils.wavingComponent((MutableComponent) super.getName(pStack), 0xD31D8C, 0.01F, 0.4F);
    }


    public double getIncrease() {
        return increase;
    }
}
