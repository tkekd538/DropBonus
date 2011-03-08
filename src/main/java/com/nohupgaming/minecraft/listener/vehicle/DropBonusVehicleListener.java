package com.nohupgaming.minecraft.listener.vehicle;

import net.minecraft.server.EntityBoat;
import net.minecraft.server.EntityMinecart;

import org.bukkit.craftbukkit.entity.CraftBoat;
import org.bukkit.craftbukkit.entity.CraftMinecart;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleListener;

import com.nohupgaming.minecraft.DropBonus;
import com.nohupgaming.minecraft.util.DropBonusUtil;

public class DropBonusVehicleListener extends VehicleListener 
{
    DropBonus _plugin;
    
    public DropBonusVehicleListener(DropBonus p)
    {
        _plugin = p;
    }
    
    @Override
    public void onVehicleDamage(VehicleDamageEvent event) 
    {
        Vehicle v = event.getVehicle();
        Player pl = event.getAttacker() instanceof Player ?  
            pl = (Player) event.getAttacker() : null;

        int dmg = 0;
        
        if (v instanceof Boat)
        {
            dmg = ((EntityBoat)((CraftBoat) v).getHandle()).a;            
        } else if (v instanceof Minecart) {
            dmg = ((EntityMinecart)((CraftMinecart) v).getHandle()).a;            
        } 
        
        if (((dmg + event.getDamage()) * 10) > 40 &&
            DropBonusUtil.hasBonus(_plugin, pl, v))
        {
            DropBonusUtil.generateBonus(_plugin, pl, v);
        }
    }
    
    @Override
    public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) 
    {
        Vehicle v = event.getVehicle();
        double speed = v.getVelocity().length();
        Player pl = v.getPassenger() instanceof Player ?  
            pl = (Player) v.getPassenger() : null;

        if (v instanceof Boat && speed > 0.15) 
        {
            if (DropBonusUtil.hasBonus(_plugin, pl, v))
            {
                DropBonusUtil.generateBonus(_plugin, pl, v);
            }
        }
    }
    
}
