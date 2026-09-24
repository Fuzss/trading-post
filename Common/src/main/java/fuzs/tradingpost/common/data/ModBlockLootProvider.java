package fuzs.tradingpost.common.data;

import fuzs.puzzleslib.common.api.data.v3.loot.AbstractBlockLootSubProvider;
import fuzs.tradingpost.common.init.ModRegistry;
import net.minecraft.data.loot.LootTableSubProvider;

public class ModBlockLootProvider extends AbstractBlockLootSubProvider {

    public ModBlockLootProvider(LootTableSubProvider.Context context) {
        super(context);
    }

    @Override
    public void generate() {
        this.add(ModRegistry.TRADING_POST_BLOCK.value(), this::createNameableBlockEntityTable);
    }
}
