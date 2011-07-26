package com.dumptruckman.actionmenu;

/**
 * @author dumptruckman
 */
public abstract class ActionMenuItem implements Runnable {

    private String text;

    public ActionMenuItem() {
        text = "";
    }

    public ActionMenuItem(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override public String toString() {
        return this.text;
    }
}
