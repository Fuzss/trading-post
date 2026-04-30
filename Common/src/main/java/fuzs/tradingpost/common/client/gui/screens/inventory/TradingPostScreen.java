package fuzs.tradingpost.common.client.gui.screens.inventory;

import fuzs.puzzleslib.common.api.client.util.v1.SearchRegistryHelper;
import fuzs.puzzleslib.common.api.network.v4.MessageSender;
import fuzs.tradingpost.common.TradingPost;
import fuzs.tradingpost.common.client.TradingPostClient;
import fuzs.tradingpost.common.network.client.ServerboundClearSlotsMessage;
import fuzs.tradingpost.common.world.inventory.TradingPostMenu;
import fuzs.tradingpost.common.world.item.trading.TradingPostOffers;
import fuzs.tradingpost.common.world.level.block.entity.TradingPostBlockEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.Locale;
import java.util.Objects;

public class TradingPostScreen extends MerchantScreen {
    public static final Identifier MAGNIFYING_GLASS_LOCATION = TradingPost.id("container/villager/magnifying_glass");
    private static final Identifier OUT_OF_STOCK_SPRITE = Identifier.withDefaultNamespace(
            "container/villager/out_of_stock");
    private static final Identifier DISCOUNT_STRIKETHRUOGH_SPRITE = Identifier.withDefaultNamespace(
            "container/villager/discount_strikethrough");
    private static final Identifier CREATIVE_INVENTORY_LOCATION = Identifier.withDefaultNamespace(
            "textures/gui/container/creative_inventory/tab_item_search.png");
    public static final Component DEPRECATED_TRADE_COMPONENT = Component.translatable("merchant.deprecated");
    public static final Component MERCHANT_UNAVAILABLE_COMPONENT = Component.translatable("trading_post.trader_gone");

    private EditBox searchBox;
    private boolean ignoreTextInput;

    public TradingPostScreen(MerchantMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        for (MerchantScreen.TradeOfferButton tradeOfferButton : this.tradeOfferButtons) {
            tradeOfferButton.onPress = (Button button) -> {
                int shopItem = tradeOfferButton.getIndex() + this.scrollOff;
                MerchantOffers offers = this.getMenu().getOffers();
                this.shopItem = shopItem;
                this.getMenu().setSelectionHint(shopItem);
                this.getMenu().getTraders().setActiveOffer(offers.get(shopItem));
                this.getMenu().tryMoveItems(shopItem);
                // get real index when sending to server
                this.minecraft.getConnection()
                        .send(new ServerboundSelectTradePacket(offers instanceof TradingPostOffers ?
                                ((TradingPostOffers) offers).getOrigShopItem(shopItem) : shopItem));
            };
        }

        this.searchBox = new EditBox(this.font,
                this.leftPos + 13,
                this.topPos + 6,
                80,
                9,
                TradingPostBlockEntity.CONTAINER_COMPONENT);
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setTextColor(-1);
        this.addWidget(this.searchBox);
    }

    @Override
    public void resize(int width, int height) {
        String inputValue = this.searchBox.getValue();
        super.resize(width, height);
        this.searchBox.setValue(inputValue);
        if (!this.searchBox.getValue().isEmpty()) {
            this.refreshSearchResults();
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        Component title = this.getDisplayTitle();
        guiGraphics.text(this.font,
                title,
                (49 + this.imageWidth / 2 - this.font.width(title) / 2),
                6,
                0XFF404040,
                false);
        guiGraphics.text(this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                0XFF404040,
                false);
    }

    private Component getDisplayTitle() {
        Component title = this.getMenu().getTraders().getDisplayName();
        if (title != null) {
            int traderLevel = this.menu.getTraderLevel();
            if (traderLevel > 0 && traderLevel <= 5 && this.menu.showProgressBar()) {
                return title.copy().append(" - ").append(Component.translatable("merchant.level." + traderLevel));
            } else {
                return title;
            }
        } else {
            return this.title;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTime) {

        MerchantOffers merchantoffers = this.getMenu().getOffers();
        this.setButtonsActive(merchantoffers);

        int originalScrollOff = this.scrollOff;
        Slot originalHoveredSlot = this.hoveredSlot;
        // set offers to empty to prevent MerchantScreen::render code from running, also disabled buttons drawing tooltips by changing scrollOff, as well as tooltip for hovered slot by removing hovered slot
        this.lock(true, merchantoffers.size(), null);
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTime);
        // reset everything so we can do this ourselves
        this.lock(false, originalScrollOff, originalHoveredSlot);

        if (!merchantoffers.isEmpty()) {

            // normally rendered as part of background, but skipped as offers are empty when it's called
            if (this.shopItem >= 0 && this.shopItem < merchantoffers.size()) {

                MerchantOffer merchantoffer = merchantoffers.get(this.shopItem);
                if (merchantoffer.isOutOfStock()) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                            OUT_OF_STOCK_SPRITE,
                            this.leftPos + 83 + 99,
                            this.topPos + 35,
                            0,
                            28,
                            21);
                }
            }

            int posX = this.leftPos + 5;
            int posY = this.topPos + 16 + 2;
            this.extractScroller(guiGraphics, this.leftPos, this.topPos, mouseX, mouseY, merchantoffers);

            for (int i = 0, merchantOffersSize = merchantoffers.size(); i < merchantOffersSize; i++) {

                if (merchantoffers.size() <= 7 || (i >= this.scrollOff && i < 7 + this.scrollOff)) {

                    MerchantOffer merchantoffer = merchantoffers.get(i);
                    // move this call here to render below red overlay
                    this.extractButtonArrows(guiGraphics, merchantoffer, this.leftPos, posY + 1);
                    if (!this.getMenu().getTraders().checkOffer(merchantoffer)) {

                        guiGraphics.fill(posX, posY, posX + 88, posY + 20, 822018048);
                    }

                    ItemStack baseCostA = merchantoffer.getBaseCostA();
                    ItemStack costA = merchantoffer.getCostA();
                    ItemStack costB = merchantoffer.getCostB();
                    ItemStack result = merchantoffer.getResult();

                    if (!this.getMenu().getTraders().checkOffer(merchantoffer)) {

                        guiGraphics.fill(posX, posY, posX + 88, posY + 20, 822083583);
                    }

                    this.extractCostA(guiGraphics, posX, posY, baseCostA, costA);

                    if (!costB.isEmpty()) {
                        guiGraphics.fakeItem(costB, posX + 35, posY + 1);
                        guiGraphics.itemDecorations(this.font, costB, posX + 35, posY + 1);
                    }

                    guiGraphics.fakeItem(result, posX + 68, posY + 1);
                    guiGraphics.itemDecorations(this.font, result, posX + 68, posY + 1);
                    posY += 20;
                }
            }

            MerchantOffer activeOffer = merchantoffers.get(this.shopItem);
            if (this.getMenu().showProgressBar()) {

                this.extractProgressBar(guiGraphics, this.leftPos, this.topPos, activeOffer);
            }

            if (activeOffer.isOutOfStock() && this.isHovering(186, 35, 22, 21, mouseX, mouseY) && this.getMenu()
                    .canRestock()) {

                guiGraphics.setTooltipForNextFrame(this.font, DEPRECATED_TRADE_COMPONENT, mouseX, mouseY);
            }

            posY = this.topPos + 16 + 2;
            for (int i = 0, merchantoffersSize = merchantoffers.size(); i < merchantoffersSize; i++) {

                if (merchantoffers.size() <= 7 || (i >= this.scrollOff && i < 7 + this.scrollOff)) {

                    MerchantOffer merchantoffer = merchantoffers.get(i);
                    if (!this.getMenu().getTraders().checkOffer(merchantoffer)) {

                        if (this.isHovering(posX, posY, 88, 19, mouseX + this.leftPos, mouseY + this.topPos)) {

                            guiGraphics.setTooltipForNextFrame(this.font,
                                    MERCHANT_UNAVAILABLE_COMPONENT,
                                    mouseX,
                                    mouseY);
                        }
                    }

                    posY += 20;
                }
            }
        }

        // move this out of if block above since search may update this
        for (int i = 0, offerButtonsLength = this.tradeOfferButtons.length; i < offerButtonsLength; i++) {

            MerchantScreen.TradeOfferButton tradeOfferButton = this.tradeOfferButtons[i];
            if (tradeOfferButton.active && tradeOfferButton.isHoveredOrFocused()) {

                tradeOfferButton.extractToolTip(guiGraphics, mouseX, mouseY);
            }

            tradeOfferButton.visible = i < this.getMenu().getOffers().size();
        }
    }

    private void extractCostA(GuiGraphicsExtractor guiGraphics, int posX, int posY, ItemStack baseCostA, ItemStack costA) {
        guiGraphics.fakeItem(costA, posX + 5, posY + 1);
        if (baseCostA.getCount() == costA.getCount()) {
            guiGraphics.itemDecorations(this.font, costA, posX + 5, posY + 1);
        } else {
            guiGraphics.itemDecorations(this.font,
                    baseCostA,
                    posX + 5,
                    posY + 1,
                    baseCostA.getCount() == 1 ? "1" : null);
            guiGraphics.itemDecorations(this.font, costA, posX + 5 + 14, posY + 1, costA.getCount() == 1 ? "1" : null);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    DISCOUNT_STRIKETHRUOGH_SPRITE,
                    posX + 5 + 7,
                    posY + 1 + 12,
                    9,
                    2);
        }
    }

    private void setButtonsActive(MerchantOffers merchantoffers) {
        if (!merchantoffers.isEmpty()) {
            for (int i = 0; i < merchantoffers.size(); i++) {
                if (merchantoffers.size() <= 7 || (i >= this.scrollOff && i < 7 + this.scrollOff)) {
                    MerchantOffer offer = merchantoffers.get(i);
                    this.tradeOfferButtons[i - this.scrollOff].active = this.getMenu().getTraders().checkOffer(offer);
                }
            }
        }
    }

    private void lock(boolean lockOffers, int newScrollOff, Slot newHoveredSlot) {
        this.getMenu().setOffersLocked(lockOffers);
        this.scrollOff = newScrollOff;
        this.hoveredSlot = newHoveredSlot;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                CREATIVE_INVENTORY_LOCATION,
                this.leftPos + 11,
                this.topPos + 4,
                80.0F,
                4.0F,
                90,
                12,
                256,
                256);
        this.searchBox.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                MAGNIFYING_GLASS_LOCATION,
                this.leftPos,
                this.topPos + 4,
                16,
                16);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        if (this.searchBox.mouseClicked(mouseButtonEvent, doubleClick)) {
            this.searchBox.setFocused(true);
            return true;
        } else {
            return super.mouseClicked(mouseButtonEvent, doubleClick);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        this.ignoreTextInput = false;
        String lastSearch = this.searchBox.getValue().trim();
        if (this.searchBox.keyPressed(keyEvent)) {
            if (!Objects.equals(this.searchBox.getValue().trim(), lastSearch)) {
                this.refreshSearchResults();
            }

            return true;
        } else if (this.searchBox.isFocused() && this.searchBox.isVisible() && !keyEvent.isEscape()) {
            return true;
        } else if (this.minecraft.options.keyChat.matches(keyEvent) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
        }

        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        String lastSearch = this.searchBox.getValue().trim();
        if (!this.ignoreTextInput && this.searchBox.charTyped(characterEvent)) {
            if (!Objects.equals(this.searchBox.getValue().trim(), lastSearch)) {
                this.refreshSearchResults();
            }

            return true;
        }

        return super.charTyped(characterEvent);
    }

    public void refreshSearchResults() {
        if (!(this.getMenu().getOffers() instanceof TradingPostOffers offers)) {
            return;
        }

        String query = this.searchBox.getValue().trim();
        if (query.isEmpty()) {
            offers.clearFilter();
        } else {
            SearchTree<MerchantOffer> searchTree = SearchRegistryHelper.getSearchTree(TradingPostClient.MERCHANT_OFFERS_SEARCH_TREE);
            offers.setFilter(searchTree.search(query.toLowerCase(Locale.ROOT)));
        }

        this.scrollOff = 0;
        this.shopItem = 0;
        this.getMenu().setSelectionHint(-1);
        this.getMenu().getTraders().setActiveOffer(null);
        this.getMenu().clearPaymentSlots();
        MessageSender.broadcast(new ServerboundClearSlotsMessage());
    }

    @Override
    public TradingPostMenu getMenu() {
        return (TradingPostMenu) super.getMenu();
    }
}
