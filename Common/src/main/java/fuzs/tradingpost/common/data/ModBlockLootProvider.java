package fuzs.tradingpost.common.data;

import fuzs.puzzleslib.common.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.tradingpost.common.init.ModRegistry;

public class ModBlockLootProvider extends AbstractLootProvider.Blocks {
    
    public ModBlockLootProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addLootTables() {
        this.add(ModRegistry.TRADING_POST_BLOCK.value(), this::createNameableBlockEntityTable);
    }
}
