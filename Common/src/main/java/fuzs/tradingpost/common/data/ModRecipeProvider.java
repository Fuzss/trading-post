package fuzs.tradingpost.common.data;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v3.recipes.AbstractRecipeProvider;
import fuzs.tradingpost.common.init.ModRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    @Override
    public void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ModRegistry.TRADING_POST_BLOCK.value())
                .define('#', ItemTags.PLANKS)
                .define('X', Items.EMERALD)
                .define('S', Items.STICK)
                .pattern(" X ")
                .pattern("###")
                .pattern("S S")
                .unlockedBy(getHasName(Items.EMERALD), this.has(Items.EMERALD))
                .save(this.output);
    }
}
