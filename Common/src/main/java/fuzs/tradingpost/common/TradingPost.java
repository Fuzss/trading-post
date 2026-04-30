package fuzs.tradingpost.common;

import fuzs.puzzleslib.common.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.common.api.core.v1.context.GameplayContentContext;
import fuzs.puzzleslib.common.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.common.api.event.v1.BuildCreativeModeTabContentsCallback;
import fuzs.tradingpost.common.config.ServerConfig;
import fuzs.tradingpost.common.init.ModRegistry;
import fuzs.tradingpost.common.network.ClientboundBuildOffersMessage;
import fuzs.tradingpost.common.network.ClientboundMerchantDataMessage;
import fuzs.tradingpost.common.network.ClientboundRemoveMerchantsMessage;
import fuzs.tradingpost.common.network.client.ServerboundClearSlotsMessage;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.apache.commons.lang3.math.Fraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingPost implements ModConstructor {
    public static final String MOD_ID = "tradingpost";
    public static final String MOD_NAME = "Trading Post";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerLoadingHandlers();
    }

    private static void registerLoadingHandlers() {
        BuildCreativeModeTabContentsCallback.buildCreativeModeTabContents(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register((CreativeModeTab creativeModeTab, CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) -> {
                    output.accept(ModRegistry.TRADING_POST_ITEM.value());
                });
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToClient(ClientboundMerchantDataMessage.class, ClientboundMerchantDataMessage.STREAM_CODEC);
        context.playToClient(ClientboundRemoveMerchantsMessage.class, ClientboundRemoveMerchantsMessage.STREAM_CODEC);
        context.playToClient(ClientboundBuildOffersMessage.class, ClientboundBuildOffersMessage.STREAM_CODEC);
        context.playToServer(ServerboundClearSlotsMessage.class, ServerboundClearSlotsMessage.STREAM_CODEC);
    }

    @Override
    public void onRegisterGameplayContent(GameplayContentContext context) {
        context.registerFuel(ModRegistry.TRADING_POST_BLOCK, Fraction.getFraction(3, 2));
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
