package com.dumptruckman.actionmenu;

/**
 * @author dumptruckman
 */
public abstract class ActionMenuItem implements Runnable {

    private String text;

    public ActionMenuItem() {
        this("");
    }

    public ActionMenuItem(String text) {
        this.text = text;
    }

    public void update() {
        
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override public String toString() {
        return this.text;
    }
}