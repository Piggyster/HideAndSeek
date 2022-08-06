package me.piggyster.hideandseek.command;

import me.piggyster.api.color.ColorAPI;
import me.piggyster.api.command.Command;
import me.piggyster.api.service.Service;
import me.piggyster.hideandseek.HideAndSeekPlugin;
import me.piggyster.hideandseek.game.GameService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventCommand extends Command {

    private static final HideAndSeekPlugin plugin = HideAndSeekPlugin.getInstance();
    private GameService gameService;

    public EventCommand() {
        super(plugin, "event");
        gameService = Service.grab(GameService.class);
    }

    public void run(CommandSender sender, String[] args) {
        if(sender instanceof Player player) {
            if(args.length == 0) {
                player.sendMessage(ColorAPI.process("&aDo &2/event join &ato play Hide and Seek!"));
                return;
            } else if(args.length == 1 && args[0].equalsIgnoreCase("join")) {
                if(gameService.isStarted()) {
                    player.sendMessage(ColorAPI.process("&cThe event is already running!"));
                    return;
                } else if(gameService.isLimbo(player)) {
                    player.sendMessage(ColorAPI.process("&cYou have already been entered!"));
                    return;
                } else {
                    gameService.addLimbo(player, false);
                    player.sendMessage(ColorAPI.process("&aYou have been entered to the event!"));
                    return;
                }
            } else if(args.length == 1 && args[0].equalsIgnoreCase("start")) {
                if(!player.hasPermission("event.start")) {
                    player.sendMessage(ColorAPI.process("&cNo permission!"));
                    return;
                } else {
                    if(!gameService.isStarted()) {
                        gameService.start();
                        return;
                    } else {
                        player.sendMessage(ColorAPI.process("&cThe game has already begun."));
                        return;
                    }
                }
            } else if(args.length == 2 && args[0].equalsIgnoreCase("setseeker")) {
                if(!player.hasPermission("event.setseeker")) {
                    player.sendMessage(ColorAPI.process("&cNo permission!"));
                    return;
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target == null) {
                        player.sendMessage(ColorAPI.process("&cNot a valid player!"));
                        return;
                    }
                    gameService.addLimbo(target, true);
                    player.sendMessage(ColorAPI.process("&a" + target.getName() + " is now a seeker!"));
                    return;
                }
            } else if(args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                if(!player.hasPermission("event.stop")) {
                    player.sendMessage(ColorAPI.process("&cNo permission!"));
                    return;
                } else {
                    gameService.stop();
                    Bukkit.broadcastMessage(ColorAPI.process("&6&lHIDE AND SEEK &f&l- &eThe event has been stopped. Thanks for playing!"));
                    return;
                }
            }

        }
    }
}
