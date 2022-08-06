package me.piggyster.hideandseek.menu;

import com.google.common.collect.Lists;
import com.mysql.cj.conf.url.SingleConnectionUrl;
import me.piggyster.api.menu.PaginatedMenu;
import me.piggyster.api.menu.item.MenuItem;
import me.piggyster.api.menu.item.SimpleItem;
import me.piggyster.api.menu.item.SkullCreator;
import me.piggyster.api.service.Service;
import me.piggyster.hideandseek.game.GameService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RemainingMenu extends PaginatedMenu {


    private static GameService gameService;

    public RemainingMenu(Player owner) {
        super(owner);

        if(gameService == null) {
            gameService = Service.grab(GameService.class);
        }
    }


    public List<Integer> getSlots() {
        return Lists.newArrayList(1, 2, 3, 4, 5, 6, 7);
    }

    public List<MenuItem> getContent() {
        List<MenuItem> list = new ArrayList<>();
        gameService.getHiders().forEach((p, s) -> {
            SimpleItem item = new SimpleItem(Material.PLAYER_HEAD).setName("&e" + p.getName());
            ItemStack itemStack = item.build();
            itemStack = SkullCreator.itemWithUuid(itemStack, p.getUniqueId());
            MenuItem menuItem = new MenuItem(new SimpleItem(itemStack));
            list.add(menuItem);
        });
        return list;
    }

    @Override
    public int getNextPageSlot() {
        return 8;
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
        return 9;
    }

    @Override
    public String getTitle() {
        return "Remaining Hiders";
    }
}
