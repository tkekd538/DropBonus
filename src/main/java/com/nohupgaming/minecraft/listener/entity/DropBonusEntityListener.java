package com.nohupgaming.minecraft.listener.entity;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.nohupgaming.minecraft.DropBonus;
import com.nohupgaming.minecraft.DropBonusUtil;

public class DropBonusEntityListener extends EntityListener 
{
    private DropBonus _plugin;
    private HashMap<Entity, Entity> _killed;
    
    public DropBonusEntityListener(DropBonus plugin)
    {
        _plugin = plugin;
        _killed = new HashMap<Entity, Entity>();
    }
    
    @Override
    public void onEntityDamage(EntityDamageEvent event) 
    {
        if (event.getEntity() instanceof LivingEntity)
        {
            LivingEntity target = ((LivingEntity) event.getEntity());
            Entity dmgBy = null;
            
            if (event instanceof EntityDamageByEntityEvent)
            {
                dmgBy = ((EntityDamageByEntityEvent) event).getDamager();                
            }
            
            if (target.getHealth()  > 0 &&
                target.getHealth() <= event.getDamage() &&
                event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) &&
                !_killed.containsKey(target))
            {
                _killed.put(target, dmgBy);
            }
        }
    }
    
    @Override
    public void onEntityDeath(EntityDeathEvent event) 
    {
        Entity e = event.getEntity();
        
        if (_killed.containsKey(e))
        {
            Entity dmgBy = _killed.get(e);
            Player pl = dmgBy instanceof Player ? (Player) dmgBy : null; 
            if (DropBonusUtil.hasBonus(_plugin, pl, e))
            {
                DropBonusUtil.generateBonus(_plugin, 
                    pl, e);
            }
            
            if (DropBonusUtil.isOverride(_plugin, pl, e))
            {
                event.getDrops().clear();
            }

            _killed.remove(e);
        }
    }
    
}
