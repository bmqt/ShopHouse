package fr.bmqt.shophouse.listener;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.bmqt.shophouse.ShopHouse;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Baptiste on 02/01/2018 for ShopHouse.
 */
public class SignListener implements Listener {


    @EventHandler
    public void onSignChange(SignChangeEvent e){
        Player player = e.getPlayer();
        if (!player.hasPermission("house.createSign"))
            return;

        if (!e.getLine(0).equalsIgnoreCase("house"))
            return;

        if (!ShopHouse.regionExist(e.getLine(1), e.getBlock().getWorld())) {
            player.sendMessage("§cErreur la région \"" + e.getLine(1) + "\" n'éxiste pas!");
            return;
        }

        int price = -1;

        try {
            price = Integer.parseInt(e.getLine(2));
        } catch (Exception ex) {
        }

        if (price <= 0){
            player.sendMessage("§cErreur dans le prix de l'habitation!");
            return;
        }

        e.setLine(0, "§1Habitation");
        e.setLine(2, "§2" + price + " émeraudes");
        e.setLine(3, "§4Clique droit.");


        player.sendMessage("§aHabitation créée !");

    }


    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block    clickedBlock     = e.getClickedBlock();
        Material clickedBlockType = clickedBlock.getType();

        if (clickedBlockType != Material.SIGN && clickedBlockType != Material.SIGN_POST && clickedBlockType != Material.WALL_SIGN)
            return;

        Sign sign = ((Sign) clickedBlock.getState());

        if (!sign.getLine(0).equalsIgnoreCase("§1Habitation")
                || !ShopHouse.regionExist(sign.getLine(1), clickedBlock.getWorld())){
            return;
        }

        int price = -1;

        try {
            price = Integer.parseInt(sign.getLine(2).replace("§2", "").replace(" émeraudes", ""));
        } catch (Exception ex) {
        }

        if (price <= 0)
            return;

        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.EMERALD){
            player.sendMessage("§cVous n'avez pas d'émeraudes dans votre main principal!");
            return;
        }

        if (item.getAmount() < price){
            player.sendMessage("§cVous n'avez pas assez d'émeraudes dans votre main principal!");
            return;
        }

        WorldGuardPlugin worldGuardPlugin = ShopHouse.getWorldGuardPlugin();
        RegionManager regionManager = worldGuardPlugin.getRegionManager(clickedBlock.getWorld());

        DefaultDomain owner = new DefaultDomain();
        owner.addPlayer(player.getUniqueId());

        ProtectedRegion protectedRegion = regionManager.getRegion(sign.getLine(1));
        protectedRegion.setOwners(owner);

        try {
            regionManager.save();
        } catch (StorageException e1) {
            e1.printStackTrace();
        }

        if (item.getAmount() > price)
            item.setAmount(item.getAmount()-price);
        else
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        player.sendMessage("§aVous venez d'acheter l'habitation §2" + sign.getLine(1) + " §apour la somme de §2" + price + " §aémeraudes !");

        sign.setLine(2, "§2Propriétaire:");
        sign.setLine(3, "§4" + player.getName());
        sign.update();

    }

}

