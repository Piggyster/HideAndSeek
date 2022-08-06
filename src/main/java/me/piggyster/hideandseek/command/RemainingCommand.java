package me.piggyster.hideandseek.command;

import me.piggyster.api.command.Command;
import me.piggyster.hideandseek.HideAndSeekPlugin;
import me.piggyster.hideandseek.menu.RemainingMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RemainingCommand extends Command {

    private static final HideAndSeekPlugin plugin = HideAndSeekPlugin.getInstance();

    public RemainingCommand() {
        super(plugin, "remaining");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if(sender instanceof Player player && player.hasPermission("event.remaining")) {
            RemainingMenu menu = new RemainingMenu(player);
            menu.open();
        }
    }
}
