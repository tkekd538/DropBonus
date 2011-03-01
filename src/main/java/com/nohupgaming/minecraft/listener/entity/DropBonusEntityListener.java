package com.nohupgaming.minecraft.listener.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.nohupgaming.minecraft.DropBonus;
import com.nohupgaming.minecraft.DropBonusUtil;

public class DropBonusEntityListener extends EntityListener 
{
    private DropBonus _plugin;
    private List<Entity> _killed;
    
    public DropBonusEntityListener(DropBonus plugin)
    {
        _plugin = plugin;
        _killed = new ArrayList<Entity>();
    }
    
    @Override
    public void onEntityDamage(EntityDamageEvent event) 
    {
        if (event.getEntity() instanceof LivingEntity)
        {
            LivingEntity target = ((LivingEntity) event.getEntity());
            
            if (target.getHealth()  > 0 &&
                target.getHealth() <= event.getDamage() &&
                event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) &&
                !_killed.contains(target))
            {
                _killed.add(target);
            }
        }
    }
    
    @Override
    public void onEntityDeath(EntityDeathEvent event) 
    {
        Entity e = event.getEntity();
        
        if (_killed.contains(e))
        {
            if (DropBonusUtil.hasBonus(_plugin, e))
            {
                DropBonusUtil.generateBonus(_plugin, e);
            }
            
            if (DropBonusUtil.isOverride(_plugin, e))
            {
                event.getDrops().clear();
            }

            _killed.remove(e);
        }
    }
    
}
