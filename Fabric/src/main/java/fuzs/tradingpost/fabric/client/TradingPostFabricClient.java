package fuzs.tradingpost.fabric.client;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.tradingpost.common.TradingPost;
import fuzs.tradingpost.common.client.TradingPostClient;
import net.fabricmc.api.ClientModInitializer;

public class TradingPostFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(TradingPost.MOD_ID, TradingPostClient::new);
    }
}
