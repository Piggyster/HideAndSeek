package me.piggyster.hideandseek.menu;

import com.google.common.collect.Lists;
import me.piggyster.api.color.ColorAPI;
import me.piggyster.api.menu.PaginatedMenu;
import me.piggyster.api.menu.item.MenuItem;
import me.piggyster.api.menu.item.SimpleItem;
import me.piggyster.api.service.Service;
import me.piggyster.hideandseek.disguise.DisguiseType;
import me.piggyster.hideandseek.game.GameService;
import me.piggyster.hideandseek.game.HidingPlayer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectblockMenu extends PaginatedMenu {

    private static List<DisguiseType> TYPES = new ArrayList<>(Arrays.asList(DisguiseType.values()));

    private static GameService gameService;

    public SelectblockMenu(Player owner) {
        super(owner);
        if(gameService == null) {
            gameService = Service.grab(GameService.class);
        }
    }


    public List<Integer> getSlots() {
        return Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
    }

    public void draw() {
        super.draw();
        fillEmpty(new SimpleItem(Material.GRAY_STAINED_GLASS_PANE));
    }


    public List<MenuItem> getContent() {
        List<MenuItem> list = new ArrayList<>();
        for(DisguiseType type : TYPES) {
            MenuItem item;
            if(type.isBlock()) {
                item = new MenuItem(new SimpleItem(type.getMaterial()));
            } else {
                item = new MenuItem(new SimpleItem(Material.ARMOR_STAND));
            }
            item.setConsumer(event -> {
                if(player.hasPermission("event.disableblock") && event.getClick() == ClickType.RIGHT) {
                    TYPES.remove(type);
                    draw();
                    return;
                }


                if(!gameService.isHider(player)) {
                    player.sendMessage(ColorAPI.process("&cYou are not a hider."));
                    player.closeInventory();
                    return;
                }
                HidingPlayer hidingPlayer = gameService.getHider(player);
                if(!hidingPlayer.canChangeBlock()) {
                    player.sendMessage(ColorAPI.process("&cYou are still on cooldown!"));
                    player.closeInventory();
                    return;
                }
                hidingPlayer.setDisguiseType(type);
                hidingPlayer.removeDisguise();
                hidingPlayer.updateDisguise();
                player.closeInventory();
                player.sendMessage(ColorAPI.process("&aYou switched disguises."));
            });
            list.add(item);
        }
        return list;
    }

    public int getNextPageSlot() {
        return 17;
    }

    @Override
    public int getPreviousPageSlot() {
        return 0;
    }

    @Override
    public SimpleItem getNextPageItem() {
        return new SimpleItem(Material.ARROW).setName("&bNext Page");
    }

    @Override
    public SimpleItem getPreviousPageItem() {
        return new SimpleItem(Material.ARROW).setName("&bPrevious Page");
    }

    @Override
    public int getSize() {
        return 18;
    }

    @Override
    public String getTitle() {
        return "Select Disguise";
    }
}
