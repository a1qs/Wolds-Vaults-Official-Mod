package xyz.iwolfking.woldsvaults.mixins.vaulthunters.custom;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.ResearchesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.ResearchDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractDialog;
import iskallia.vault.init.ModSounds;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.PlayerReference;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.api.data.level.ClientPersonalLevelCapData;
import xyz.iwolfking.woldsvaults.api.data.level.PersonalLevelCapData;
import xyz.iwolfking.woldsvaults.init.ModNetwork;
import xyz.iwolfking.woldsvaults.network.message.C2SIncreaseLevelCapMessage;

import java.awt.*;
import java.util.List;

@Mixin(value = ResearchDialog.class, remap = false)
public abstract class MixinResearchDialog extends AbstractDialog<ResearchesElementContainerScreen> {

    @Shadow private String researchName;

    // dummy
    protected MixinResearchDialog(ResearchesElementContainerScreen skillTreeScreen) {
        super(skillTreeScreen);
    }

    @ModifyVariable(method = "renderHeading", at = @At("STORE"), name = "widgetBounds")
    private Rectangle modifyBounds(Rectangle widgetBounds) {
        if (woldsVaults$isLvlCapResearch(researchName)) {
            return new Rectangle(widgetBounds.x, widgetBounds.y, 30, 30);
        }
        return widgetBounds;
    }

    @Inject(method = "research", at = @At("HEAD"), cancellable = true)
    private void redirectResearches(CallbackInfo ci) {
        if (woldsVaults$isLvlCapResearch(researchName)) {
            int unspentPoints = VaultBarOverlay.unspentKnowledgePoints;
            if (ClientPersonalLevelCapData.getResearchCapData().researchCost() <= unspentPoints) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    minecraft.player.playSound(ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
                }
            }
            this.update();

            //noinspection InstantiationOfUtilityClass
            ModNetwork.CHANNEL.sendToServer(new C2SIncreaseLevelCapMessage());
            ci.cancel();
        }
    }

    @ModifyVariable(method = "update", at = @At("STORE"), name = "buttonText")
    private String modifyButtonText(String buttonText) {
        if (woldsVaults$isLvlCapResearch(researchName)) {
            PersonalLevelCapData.ResearchCapData data = ClientPersonalLevelCapData.getResearchCapData();
            return ClientPersonalLevelCapData.reachedCap() ? "You have reached the cap!" : "Research (" + data.researchCost() + ")";
        }
        return buttonText;
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void changeButtonActivity(CallbackInfo ci) {
        if (woldsVaults$isLvlCapResearch(researchName) && this.learnButton.active) {
            // The button should not be active if:
            // - The player has reached the global level cap
            if (ClientPersonalLevelCapData.getGlobalLevelCap() <= VaultBarOverlay.vaultLevel || VaultBarOverlay.vaultLevel >= 100) {
                this.learnButton.active = false;
            }
        }
    }

    @Redirect(method = "lambda$update$2", at = @At(value = "INVOKE", target = "Liskallia/vault/research/ResearchTree;getResearchShares()Ljava/util/List;"))
    private List<PlayerReference> redirectShares(ResearchTree instance) {
        if (woldsVaults$isLvlCapResearch(researchName)) {
            return List.of();
        }
        return instance.getResearchShares();
    }




    @Unique
    private static boolean woldsVaults$isLvlCapResearch(String researchName) {
        return researchName != null && researchName.equals("Increase Level Cap");
    }
}
