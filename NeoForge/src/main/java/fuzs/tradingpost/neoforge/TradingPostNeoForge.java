package fuzs.tradingpost.neoforge;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.tradingpost.common.TradingPost;
import fuzs.tradingpost.common.data.ModBlockLootProvider;
import fuzs.tradingpost.common.data.ModBlockTagProvider;
import fuzs.tradingpost.common.data.ModEntityTypeTagProvider;
import fuzs.tradingpost.common.data.ModRecipeProvider;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(TradingPost.MOD_ID)
public class TradingPostNeoForge {

    public TradingPostNeoForge(ModContainer modContainer) {
        ModConstructor.construct(TradingPost.MOD_ID, TradingPost::new);
        DataProviderHelper.registerDataProviders(TradingPost.MOD_ID,
                ModBlockLootProvider::new,
                ModBlockTagProvider::new,
                ModEntityTypeTagProvider::new,
                ModRecipeProvider::new
        );
    }
}
