package xyz.iwolfking.woldsvaults.mixins.vaulthunters.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.gui.screen.player.legacy.widget.ResearchWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.ResourceBoundary;
import net.minecraft.client.gui.GuiComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(value = ResearchWidget.class, remap = false)
public class MixinResearchWidget {
    @Shadow @Final
    private boolean locked;
    @Shadow
    private boolean selected;
    @Shadow @Final
    private ResearchTree researchTree;
    @Shadow @Final
    private String researchName;
    @Shadow @Final
    private SkillStyle style;

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/screen/player/legacy/widget/ResearchWidget;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V"), remap = true)
    public void replaceRender(ResearchWidget instance, PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ResearchWidget thisInstance = (ResearchWidget) (Object) this;

        if (thisInstance.getResearchName().equals("Increase Level Cap")) {
            woldsVaults$renderUnique(thisInstance, matrixStack, mouseX, mouseY, partialTicks);
            return;
        }

        thisInstance.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    @Inject(method = "getClickableBounds", at = @At("HEAD"), cancellable = true)
    public void replaceClickableBounds(CallbackInfoReturnable<Rectangle> cir) {
        ResearchWidget thisInstance = (ResearchWidget) (Object) this;

        if (thisInstance.getResearchName().equals("Increase Level Cap")) {
            cir.setReturnValue(new Rectangle(thisInstance.x, thisInstance.y, 90, 90));
        }
    }

    @Unique
    private void woldsVaults$renderUnique(ResearchWidget instance, PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ResourceBoundary resourceBoundary = this.style.frameType.getResourceBoundary();
        matrixStack.pushPose();
        RenderSystem.setShaderTexture(0, resourceBoundary.getResource());
        matrixStack.scale(3.0F, 3.0F, 1.0F);
        int vOffset = this.locked ? 62 : (!selected && !instance.isMouseOver(mouseX, mouseY) ? (researchTree.getResearchesDone().contains(researchName) ? 31 : 0) : -31);
        instance.blit(matrixStack, instance.x / 3, instance.y / 3, resourceBoundary.getU(), resourceBoundary.getV() + vOffset, resourceBoundary.getWidth(), resourceBoundary.getHeight());
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.scale(3.0F, 3.0F, 1.0F);
        matrixStack.translate(-8.0F, -8.0F, 0.0F);
        ITextureAtlas atlas = (ITextureAtlas)ModTextureAtlases.RESEARCHES.get();
        RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
        GuiComponent.blit(matrixStack, (instance.x + 45) / 3, (instance.y + 45) / 3, 0, 16, 16, this.locked && this.style.inactiveIcon != null ? atlas.getSprite(this.style.inactiveIcon) : atlas.getSprite(this.style.icon));
        matrixStack.popPose();
    }

}
