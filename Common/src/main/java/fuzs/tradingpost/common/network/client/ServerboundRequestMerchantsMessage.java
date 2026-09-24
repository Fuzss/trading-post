package fuzs.tradingpost.common.network.client;

import fuzs.puzzleslib.common.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.common.api.network.v4.message.WritableMessage;
import fuzs.puzzleslib.common.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.tradingpost.common.world.inventory.TradingPostMenu;
import fuzs.tradingpost.common.world.item.trading.MerchantCollection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class ServerboundRequestMerchantsMessage implements ServerboundPlayMessage, WritableMessage<RegistryFriendlyByteBuf> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestMerchantsMessage> STREAM_CODEC = WritableMessage.streamCodec(
            ServerboundRequestMerchantsMessage::new);

    public ServerboundRequestMerchantsMessage() {
        // NO-OP
    }

    private ServerboundRequestMerchantsMessage(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        // NO-OP
    }

    @Override
    public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        // NO-OP
    }

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu instanceof TradingPostMenu menu) {
                    MerchantCollection traders = menu.getTraders();
                    int oldSize = traders.getOffers().size();
                    traders.buildOffers(traders.getIdToOfferCountMap());
                    if (traders.getOffers().size() != oldSize) {
                        traders.sendMerchantData(context.player(), menu.containerId);
                    }
                }
            }
        };
    }
}
