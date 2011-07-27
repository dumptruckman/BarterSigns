package com.dumptruckman.bartersigns.locale;

/**
 * @author dumptruckman
 */
public enum LanguagePath {
    NO_ITEM_IN_HAND("player.nohelditem"),
    SIGN_STOCK_SETUP("sign.stock.setup"),
    SIGN_STOCK_SET("sign.stock.set"),
    SIGN_STOCK_COLLECT_LEFTOVER("sign.stock.collect.leftover"),
    SIGN_PAYMENT_SETUP("sign.payment.setup"),
    SIGN_PAYMENT_SET("sign.payment.set"),
    SIGN_SETUP_UNFINISHED("sign.unfinishedsetup"),
    SIGN_READY_SIGN("sign.ready.sign"),
    SIGN_READY_MESSAGE("sign.ready.message"),
    SIGN_MENU_ADD_STOCK("sign.menu.addstock"),
    SIGN_MENU_REMOVE_STOCK("sign.menu.removestock"),
    SIGN_INSUFFICIENT_STOCK("sign.stock.insufficient"),
    PLAYER_INSUFFICIENT_AMOUNT("player.insufficientamount"),
    ;

    private String path;

    LanguagePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
