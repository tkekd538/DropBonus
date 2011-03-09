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
import com.nohupgaming.minecraft.util.DropBonusEvaluator;

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
        if (!event.isCancelled())
        {
            Vehicle v = event.getVehicle();
            Player pl = event.getAttacker() instanceof Player ?  
                pl = (Player) event.getAttacker() : null;
            DropBonusEvaluator eval = new DropBonusEvaluator(_plugin, pl, v);
    
            int dmg = 0;
            
            if (v instanceof Boat)
            {
                dmg = ((EntityBoat)((CraftBoat) v).getHandle()).a;            
            } else if (v instanceof Minecart) {
                dmg = ((EntityMinecart)((CraftMinecart) v).getHandle()).a;            
            } 
            
            if (((dmg + event.getDamage()) * 10) > 40 &&
                eval.hasBonus())
            {
                eval.generateBonus();
            }
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
            DropBonusEvaluator eval = new DropBonusEvaluator(_plugin, pl, v);
            if (eval.hasBonus())
            {
                eval.generateBonus();
            }
        }
    }
    
}
