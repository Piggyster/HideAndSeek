package me.piggyster.hideandseek;

import me.piggyster.api.service.Service;
import me.piggyster.hideandseek.command.EventCommand;
import me.piggyster.hideandseek.command.RemainingCommand;
import me.piggyster.hideandseek.command.SelectblockCommand;
import me.piggyster.hideandseek.game.GameService;
import me.piggyster.hideandseek.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class HideAndSeekPlugin extends JavaPlugin {

    private static HideAndSeekPlugin instance;

    public void onEnable() {
        instance = this;
        Service.provide(GameService.class, new GameService());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        new EventCommand();
        new SelectblockCommand();
        new RemainingCommand();
    }

    public void onDisable() {
        instance = null;
        Service.grab(GameService.class).clearHiders();
    }

    public static HideAndSeekPlugin getInstance() {
        return instance;
    }
}
