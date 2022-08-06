package me.piggyster.hideandseek.game;

import com.google.common.collect.ImmutableMap;
import me.piggyster.api.color.ColorAPI;
import me.piggyster.api.service.PluginService;
import me.piggyster.api.util.RandomCollection;
import me.piggyster.hideandseek.HideAndSeekPlugin;
import me.piggyster.hideandseek.disguise.DisguiseType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService implements PluginService<HideAndSeekPlugin> {

    private static final HideAndSeekPlugin plugin = HideAndSeekPlugin.getInstance();
    private static final Location SHORT_BUS = new Location(Bukkit.getWorld("event"), -494, 75, -579);
    private static final Location ENTRANCE = new Location(Bukkit.getWorld("event"), -487, 74, -556);



    private boolean started;
    private Map<Player, HidingPlayer> hiders;
    private Map<Player, SeekingPlayer> seekers;
    private Map<Player, Boolean> limbo; //true - seeker : false - hider

    public void initialize() {
        hiders = new HashMap<>();
        seekers = new HashMap<>();
        limbo = new HashMap<>();
        initializeSchedulers();
    }

    public Map<Player, HidingPlayer> getHiders() {
        return ImmutableMap.copyOf(hiders);
    }

    public boolean isStarted() {
        return started;
    }

    public void addLimbo(Player player, boolean status) {
        limbo.put(player, status);
    }

    public boolean isLimbo(Player player) {
        return limbo.containsKey(player);
    }

    public void start() {
        started = true;

        AtomicInteger taskId = new AtomicInteger(0);
        AtomicInteger countdown = new AtomicInteger(6);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Bukkit.broadcastMessage(ColorAPI.process("&6&lHIDE AND SEEK &f&l- &eBegins in " + countdown.decrementAndGet() + " seconds."));
            if(countdown.get() == 1) {
                limbo.forEach((player, bool) -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    if(bool) {
                        addSeeker(player);
                        player.sendTitle(ColorAPI.process("&cYou are a Seeker"), "Eliminate all of the hiders!", 10, 70, 20);
                        player.teleport(SHORT_BUS);
                    } else {
                        addHider(player);
                        player.sendTitle(ColorAPI.process("&bYou are a Hider"), "Go find a safe place!", 10, 70, 20);
                        player.teleport(ENTRANCE);
                    }
                });
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    seekers.forEach((p, s) -> {
                        p.teleport(ENTRANCE);
                    });
                    Bukkit.broadcastMessage(ColorAPI.process("&6&lHIDE AND SEEK &f&l- &eThe seekers have been released."));
                }, 2400L);
                limbo.clear();
                Bukkit.getScheduler().cancelTask(taskId.get());
                Bukkit.broadcastMessage(ColorAPI.process("&6&lHIDE AND SEEK &f&l- &eHas started! Seekers will be released in 2 minutes."));
            }
        }, 0L, 20L);
        taskId.set(task.getTaskId());
    }

    public void stop() {
        clearHiders();
        clearSeekers();
    }

    public void addHider(Player player) {
        HidingPlayer hidingPlayer = new HidingPlayer(player, DisguiseType.values()[ThreadLocalRandom.current().nextInt(DisguiseType.values().length)]);
        hiders.put(player, hidingPlayer);
        hidingPlayer.updateDisguise();
        seekers.remove(player);
    }

    public boolean isHider(Player player) {
        return hiders.containsKey(player);
    }

    public HidingPlayer getHider(Player player) {
        return hiders.get(player);
    }

    public void clearHiders() {
        hiders.forEach((player, hider) -> hider.remove());
        hiders.clear();
    }

    public HidingPlayer getHider(Location location) {
        for(Map.Entry<Player, HidingPlayer> entry : hiders.entrySet()) {
            if(location.equals(entry.getKey().getLocation().getBlock().getLocation())) return entry.getValue();
        }
        return null;
    }

    public void addSeeker(Player player) {
        SeekingPlayer seekingPlayer = new SeekingPlayer(player);
        seekers.put(player, seekingPlayer);
        hiders.remove(player);
    }

    public boolean isSeeker(Player player) {
        return seekers.containsKey(player);
    }

    public SeekingPlayer getSeeker(Player player) {
        return seekers.get(player);
    }

    public void clearSeekers() {
        seekers.forEach((player, seeker) -> player.getInventory().clear());
        seekers.clear();
    }

    public void removeSeeker(Player player) {
        seekers.remove(player);
    }

    public void removeHider(Player player) {
        hiders.remove(player);
    }

    private void initializeSchedulers() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            hiders.forEach((player, hider) -> {
                if(!hider.isGrounded() || !hider.getDisguiseType().isBlock()) return;
                BlockData data = hider.getDisguiseType().getMaterial().createBlockData();
                if(data instanceof Directional directional) {
                    directional.setFacing(player.getFacing());
                }
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if(p == player) return;
                    p.sendBlockChange(player.getLocation(), data);
                });
            });
        }, 0, 20L);
    }

    public HideAndSeekPlugin getPlugin() {
        return plugin;
    }
}
