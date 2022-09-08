package dev.failures.balancetopx.Commands;

import dev.failures.balancetopx.BalanceTopX;
import dev.failures.balancetopx.Utils.ColorUtil;
import dev.failures.balancetopx.Utils.MapUtil;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BalTopCommand implements @Nullable CommandExecutor {
    private BalanceTopX main;
    HashMap<String, Double> balances = new HashMap<>();

    public BalTopCommand(BalanceTopX main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1 && args[0].equals("reload")) {
            if (!(sender instanceof Player)) return false;
            Player pl = (Player) sender;

            if (!pl.hasPermission("balancetopx.reload")) return false;
            main.reloadConfig();
            pl.sendMessage(ColorUtil.colorize("&aBalanceTopX has been reloaded."));
            return true;
        }

        int players = main.getConfig().getInt("top-tracker-amount");
        List<Integer> slots = main.getConfig().getIntegerList("top-tracker-slots");

        for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            balances.put(p.getName(), main.vault.getBalance(p));
        }

        HashMap<String, Double> sortedbalance = MapUtil.sortByValue(balances);
        String[] name = new String[players];
        Double[] balance = new Double[players];
        String selfn = "0";
        Double selfb = 0.0;
        Integer selfp = 0;

        int counter = 0;
        for(Map.Entry<String, Double> sorted : sortedbalance.entrySet()) {
            if(sorted.getKey().equals(sender.getName())) {
                selfn = sorted.getKey();
                selfb = sorted.getValue();
                selfp = counter;
            }
            name[counter] = sorted.getKey();
            balance[counter] = sorted.getValue();
            counter++;
            if(counter == players) break;
        }

        Gui baltop = new Gui(main.getConfig().getInt("gui-rows"), ColorUtil.colorize(main.getConfig().getString("gui-title")));

        Set<String> a = main.getConfig().getConfigurationSection("gui-contents").getKeys(false);
        for(String items: a) {
            Material mat = Material.getMaterial(main.getConfig().getString("gui-contents."+items+".material"));
            List<String> il = ColorUtil.colorizeList(main.getConfig().getStringList("gui-contents."+items+".lore"));
            String in = ColorUtil.colorize(main.getConfig().getString("gui-contents."+items+".name"));

            List<Integer> is = main.getConfig().getIntegerList("gui-contents."+items+".slots");
            if(is.size() == 0) {
                is.add(main.getConfig().getInt("gui-contents."+items+".slots"));
            }
            for(Integer slot : is) {
                GuiItem sitem = ItemBuilder.from(mat).setLore(il).setName(in).asGuiItem(event -> { event.setCancelled(true); });
                baltop.setItem(slot,sitem);
            }
        }

        counter = 0;
        for(Integer slot : slots) {
            GuiItem heads;
            if(name[counter] == null) {
                heads = ItemBuilder.from(Material.PLAYER_HEAD)
                        .setName(ColorUtil.colorize(main.getConfig().getString("head-name-empty")))
                        .setLore(replacedLore(main.getConfig().getStringList("head-lore-empty"),selfp,selfb,selfn))
                        .setSkullTexture(main.getConfig().getString("head-texture-empty"))
                        .asGuiItem(event -> {
                            event.setCancelled(true);
                        });
            } else {
                OfflinePlayer op = main.getServer().getOfflinePlayer(name[counter]);
                heads = ItemBuilder.from(Material.PLAYER_HEAD)
                        .setSkullOwner(op)
                        .setName(ColorUtil.colorize(main.getConfig().getString("head-name")
                                .replace("%player-name%", op.getName())
                                .replace("%ranking%", Integer.toString(counter + 1))
                                .replace("%player-bal%", Double.toString(balance[counter]))))
                        .setLore(replacedLore(main.getConfig().getStringList("head-lore"), counter, balance[counter], name[counter]))
                        .asGuiItem(event -> {
                            event.setCancelled(true);
                        });
            }
            counter++;
            baltop.setItem(slot,heads);
            if(counter == players) break;
        }

        if(main.getConfig().getBoolean("self-ranking")) {
            GuiItem self = ItemBuilder.from(Material.PLAYER_HEAD)
                    .setSkullOwner((OfflinePlayer)sender)
                    .setName(ColorUtil.colorize(main.getConfig().getString("head-name")
                            .replace("%player-name%",sender.getName())
                            .replace("%ranking%",Integer.toString(selfp+1))
                            .replace("%player-bal%",Double.toString(selfb))))
                    .setLore(replacedLore(main.getConfig().getStringList("head-lore"),selfp,selfb,selfn))
                    .asGuiItem(event -> {
                        event.setCancelled(true);
                    });
            baltop.setItem(main.getConfig().getInt("self-slot"),self);
        }

        baltop.open((HumanEntity)sender);
        return false;
    }

    private List<String> replacedLore(List<String> lore, Integer ranking, Double bal, String name) {
        List<String> replaced = new ArrayList<>();
        for(String l : lore) {
            replaced.add(ColorUtil.colorize(l
                    .replace("%player-bal%",Double.toString(bal))
                    .replace("%ranking%",Integer.toString(ranking+1))
                    .replace("%player-name%",name)
            ));
        }
        return replaced;
    }

}
