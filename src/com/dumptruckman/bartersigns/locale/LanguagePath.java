package com.dumptruckman.bartersigns.locale;

/**
 * @author dumptruckman
 */
public enum LanguagePath {
    NO_ITEM_IN_HAND("player.nohelditem"),
    SIGN_STOCK_SETUP("sign.stock.setup"),
    SIGN_STOCK_SET("sign.stock.set"),

    SIGN_COLLECT_LEFTOVER("sign.collect.leftover"),

    SIGN_PAYMENT_SETUP("sign.payment.setup"),
    //SIGN_PAYMENT_SET("sign.payment.set"),
    SIGN_PAYMENT_ADD("sign.payment.add"),
    SIGN_PAYMENT_ADDED("sign.payment.added"),
    SIGN_PAYMENT_REMOVE("sign.payment.remove"),
    SIGN_PAYMENT_REMOVED("sign.payment.removed"),
    SIGN_PAYMENT_CONTAINS("sign.payment.contains"),

    SIGN_SELLABLE_INCREASE("sign.sellable.increase"),
    SIGN_SELLABLE_DECREASE("sign.sellable.decrease"),
    SIGN_SELLABLE_MINIMUM("sign.sellable.minimum"),

    SIGN_MENU_COLLECT_REVENUE("sign.revenue.collect"),
    SIGN_REVENUE_EMPTY("sign.revenue.empty"),
    SIGN_SETUP_UNFINISHED("sign.unfinishedsetup"),
    SIGN_READY_SIGN("sign.ready.sign"),
    SIGN_READY_MESSAGE("sign.ready.message"),
    SIGN_MENU_ADD_STOCK("sign.stock.add"),
    SIGN_MENU_REMOVE_STOCK("sign.stock.remove"),
    SIGN_INSUFFICIENT_STOCK("sign.stock.insufficient"),
    PLAYER_INSUFFICIENT_AMOUNT("player.insufficientamount"),
    PLAYER_UNACCEPTABLE_ITEM("player.unacceptableitem"),
    SIGN_INFO("sign.info"),
    ;

    private String path;

    LanguagePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
