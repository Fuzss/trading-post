package fuzs.tradingpost.neoforge;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import fuzs.tradingpost.common.TradingPost;
import fuzs.tradingpost.common.data.ModBlockLootProvider;
import fuzs.tradingpost.common.data.ModBlockTagProvider;
import fuzs.tradingpost.common.data.ModEntityTypeTagProvider;
import fuzs.tradingpost.common.data.ModRecipeProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(TradingPost.MOD_ID)
public class TradingPostNeoForge {

    public TradingPostNeoForge(ModContainer modContainer) {
        ModConstructor.construct(TradingPost.MOD_ID, TradingPost::new);
        DataProviderBuilder.of(TradingPost.MOD_ID)
                .addLootProvider(ModBlockLootProvider::new, LootContextParamSets.BLOCK)
                .addProvider(ModBlockTagProvider::new, ModEntityTypeTagProvider::new)
                .addRecipeProvider(ModRecipeProvider::new);
    }
}
