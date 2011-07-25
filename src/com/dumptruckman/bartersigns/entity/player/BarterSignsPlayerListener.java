package com.dumptruckman.bartersigns.entity.player;

import com.dumptruckman.actionmenu.ActionMenu;
import com.dumptruckman.actionmenu.DefaultActionMenu;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.sun.corba.se.impl.orb.ParserTable;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.ws.client.SenderException;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;


/**
 * @author dumptruckman
 */
public class BarterSignsPlayerListener extends PlayerListener {

    private BarterSignsPlugin plugin;

    public BarterSignsPlayerListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
        poop.addMenuItem(poop2);

        poop.addMenuItem(test);
        String test2 = "help";
        poop2.addMenuItem(test2);
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;

        test.setEvent(event);
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            poop.cycleMenu();
            poop.showMenu(event.getPlayer());
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            poop.doSelectedMenuItem(event.getPlayer());
        }
    }

    class Test implements Runnable {
            @Override
            public void run() {
                event.getPlayer().sendMessage("Hello");
            }

            PlayerInteractEvent event;
            public void setEvent(PlayerInteractEvent event) {
                this.event = event;
            }

            public String toString() {
                return "Say hello";
            }
        };

    Test test = new Test();
    ActionMenu poop = new DefaultActionMenu("Test");
    ActionMenu poop2 = new DefaultActionMenu("Tester");
}
