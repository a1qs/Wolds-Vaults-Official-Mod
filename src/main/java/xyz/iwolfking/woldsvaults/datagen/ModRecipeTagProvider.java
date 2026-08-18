package xyz.iwolfking.woldsvaults.datagen;

import com.simibubi.create.AllTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.WoldsVaults;

public class ModRecipeTagProvider extends TagsProvider<RecipeSerializer<?>> {
    public ModRecipeTagProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, Registry.RECIPE_SERIALIZER, WoldsVaults.MOD_ID, existingFileHelper);
    }


    @Override
    protected void addTags() {
    }

    @Override
    public String getName() {
        return "Wold's Recipe Tag Provider";
    }
}
