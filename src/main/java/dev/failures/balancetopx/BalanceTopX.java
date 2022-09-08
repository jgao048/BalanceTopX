package dev.failures.balancetopx;

import dev.failures.balancetopx.Commands.BalTopCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class BalanceTopX extends JavaPlugin {
    public Economy vault = null;

    @Override
    public void onEnable() {
        if(!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
        }

        getCommand("baltop").setExecutor(new BalTopCommand(this));
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vault = rsp.getProvider();
        return vault != null;
    }
}
