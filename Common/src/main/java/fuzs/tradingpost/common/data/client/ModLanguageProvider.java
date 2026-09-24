package fuzs.tradingpost.common.data.client;

import fuzs.puzzleslib.common.api.client.data.v3.language.AbstractLanguageProvider;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.tradingpost.common.client.gui.screens.inventory.TradingPostScreen;
import fuzs.tradingpost.common.init.ModRegistry;
import fuzs.tradingpost.common.world.level.block.TradingPostBlock;
import fuzs.tradingpost.common.world.level.block.entity.TradingPostBlockEntity;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations() {
        this.add(ModRegistry.TRADING_POST_BLOCK.value(), "Trading Post");
        this.add(TradingPostBlockEntity.CONTAINER_COMPONENT, "Trading Post");
        this.add(TradingPostScreen.MERCHANT_UNAVAILABLE_COMPONENT, "The trader is no longer available.");
        this.add(TradingPostBlock.MISSING_MERCHANT_COMPONENT, "Couldn't find any available trader nearby");
    }
}
