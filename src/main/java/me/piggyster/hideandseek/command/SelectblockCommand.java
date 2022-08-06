package me.piggyster.hideandseek.command;

import me.piggyster.api.command.Command;
import me.piggyster.hideandseek.HideAndSeekPlugin;
import me.piggyster.hideandseek.menu.SelectblockMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SelectblockCommand extends Command {

    private static final HideAndSeekPlugin plugin = HideAndSeekPlugin.getInstance();

    public SelectblockCommand() {
        super(plugin, "selectblock");
    }

    public void run(CommandSender sender, String[] args) {
        if(sender instanceof Player player) {
            SelectblockMenu menu = new SelectblockMenu(player);
            menu.open();
        }
    }
}
