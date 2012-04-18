package com.dumptruckman.minecraft.bartersigns;

import com.dumptruckman.minecraft.actionmenu2.api.Menu;
import com.dumptruckman.minecraft.actionmenu2.api.MenuItem;
import com.dumptruckman.minecraft.actionmenu2.api.event.MenuItemEvent;
import com.dumptruckman.minecraft.actionmenu2.api.event.MenuItemListener;
import com.dumptruckman.minecraft.actionmenu2.impl.Menus;
import com.dumptruckman.minecraft.actionmenu2.impl.SimpleMenuItem;
import com.dumptruckman.minecraft.bartersigns.api.BarterSign;
import com.dumptruckman.minecraft.bartersigns.util.Language;
import com.dumptruckman.minecraft.pluginbase.config.AbstractYamlConfig;
import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.util.BlockLocation;
import com.dumptruckman.minecraft.pluginbase.util.Blocks;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.io.File;
import java.io.IOException;

class DefaultBarterSign extends AbstractYamlConfig<BarterSign> implements BarterSign {

    private BlockLocation location;
    private Menu menu;
    private MenuItem openShopMenuItem;

    DefaultBarterSign(BukkitPlugin plugin, File configFile, BlockLocation loc) throws IOException, IllegalArgumentException {
        super(plugin, false, configFile, BarterSign.class);
        init(plugin, loc);
    }

    private void init(BukkitPlugin plugin, BlockLocation loc) throws IllegalArgumentException {
        this.location = loc;
        Block block = Blocks.getBlockAt(location);
        if (block == null || !(block.getState() instanceof Sign)) {
            throw new IllegalArgumentException(plugin.getMessager().getMessage(Language.ERROR_LOCATION_NOT_SIGN, location));
        }
        initMenu(plugin, (Sign) block.getState());
    }

    private void initMenu(BukkitPlugin plugin, Sign sign) {
        menu = Menus.newMenu(plugin, sign);

        MenuItem item = new SimpleMenuItem();
        item.setText(plugin.getMessager().getMessage(Language.SIGN_HEADER_1, get(OWNER)));
        item.setSelectable(false);
        getMenu().getModel().add(item);
        item = new SimpleMenuItem();
        item.setText(plugin.getMessager().getMessage(Language.SIGN_HEADER_2, get(OWNER)));
        item.setSelectable(false);
        getMenu().getModel().add(item);
        item = new SimpleMenuItem();
        item.setText(plugin.getMessager().getMessage(Language.SIGN_HEADER_3, get(OWNER)));
        item.setSelectable(false);
        getMenu().getModel().add(item);

        openShopMenuItem = new SimpleMenuItem();
        openShopMenuItem.setText(plugin.getMessager().getMessage(Language.SIGN_OPEN_SHOP));
        openShopMenuItem.getMenuItemListeners().add(new OpenShopListener(this));
        getMenu().getModel().add(openShopMenuItem);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    public MenuItem getOpenShopMenuItem() {
        return openShopMenuItem;
    }

    @Override
    public BlockLocation getLocation() {
        return location;
    }

    public static class OpenShopListener implements MenuItemListener {

        private BarterSign bSign;

        public OpenShopListener(BarterSign bSign) {
            this.bSign = bSign;
        }

        @Override
        public void onAction(MenuItemEvent event) {
        }

        @Override
        public void onMenuItemChange(MenuItemEvent event) { }
    }
}
