package com.dumptruckman.bartersigns.locale;

/**
 * @author dumptruckman
 */
public enum LanguagePath {
    NO_ITEM_IN_HAND("player.nohelditem"),
    SIGN_HELP("sign.help"),
    PLAYER_HELP("player.help"),
    SIGN_STOCK_SETUP("sign.stock.setup"),
    SIGN_STOCK_SET("sign.stock.set"),
    SIGN_STOCK_LIMIT("sign.stock.limit"),

    SIGN_COLLECT_LEFTOVER("sign.collect.leftover"),

    SIGN_PAYMENT_ADD("sign.payment.add"),
    SIGN_PAYMENT_ADDED("sign.payment.added"),
    SIGN_PAYMENT_REMOVE("sign.payment.remove"),
    SIGN_PAYMENT_REMOVED("sign.payment.removed"),

    SIGN_SELLABLE_INCREASE("sign.sellable.increase"),
    SIGN_SELLABLE_DECREASE("sign.sellable.decrease"),
    SIGN_SELLABLE_MINIMUM("sign.sellable.minimum"),

    SIGN_REVENUE_COLLECT("sign.revenue.collect"),
    SIGN_REVENUE_COLLECTED("sign.revenue.collected"),
    SIGN_REVENUE_EMPTY("sign.revenue.empty"),
    SIGN_SETUP_UNFINISHED("sign.unfinishedsetup"),
    SIGN_READY_SIGN("sign.ready.sign"),
    SIGN_READY_MESSAGE("sign.ready.message"),
    SIGN_MENU_ADD_STOCK("sign.stock.add"),
    SIGN_MENU_REMOVE_STOCK("sign.stock.remove"),
    SIGN_INSUFFICIENT_STOCK("sign.stock.insufficient"),
    PLAYER_INSUFFICIENT_AMOUNT("player.insufficientamount"),
    PLAYER_UNACCEPTABLE_ITEM("player.unacceptableitem"),
    PLAYER_PURCHASE("player.purchased"),
    SIGN_INFO("sign.info"),
    OWNER_MESSAGE("sign.owner"),
    REMOVE_SIGN("sign.remove"),
    NO_PERMISSION("player.nopermission"),
    SIGN_PURCHASE("sign.purchase"),;

    private String path;

    LanguagePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
