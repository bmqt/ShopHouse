package fr.bmqt.shophouse;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import fr.bmqt.shophouse.listener.SignListener;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShopHouse extends JavaPlugin {

    private static WorldGuardPlugin worldGuardPlugin;

    @Override
    public void onEnable() {
       worldGuardPlugin = ((WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard"));
       getServer().getPluginManager().registerEvents(new SignListener(), this);
    }


    public static WorldGuardPlugin getWorldGuardPlugin() {
        return worldGuardPlugin;
    }

    public static boolean regionExist(String regionName, World world)
    {
        return worldGuardPlugin.getRegionManager(world).getRegions().containsKey(regionName);
    }

}
