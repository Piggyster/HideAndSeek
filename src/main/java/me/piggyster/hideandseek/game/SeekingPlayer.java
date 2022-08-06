package me.piggyster.hideandseek.game;

import me.piggyster.api.menu.item.SimpleItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SeekingPlayer {

   private static final SimpleItem SWORD = new SimpleItem(Material.NETHERITE_SWORD).setName("&cSeeker's Sword").addLore("&7Hit blocks that you think are players!");

    private Player player;

    public SeekingPlayer(Player player) {
        this.player = player;
        player.getInventory().clear();
        player.getInventory().setItem(0, SWORD.build());
    }


    public void remove() {
        player.getInventory().clear();
    }




}
