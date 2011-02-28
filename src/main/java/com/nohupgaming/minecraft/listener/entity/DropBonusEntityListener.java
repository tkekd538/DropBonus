package com.nohupgaming.minecraft.listener.entity;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.nohupgaming.minecraft.DropBonus;
import com.nohupgaming.minecraft.DropBonusUtil;

public class DropBonusEntityListener extends EntityListener 
{
    private DropBonus _plugin;
    
    public DropBonusEntityListener(DropBonus plugin)
    {
        _plugin = plugin;
    }
    
    @Override
    public void onEntityDeath(EntityDeathEvent event) 
    {
        if (DropBonusUtil.hasBonus(_plugin, event.getEntity()))
        {
            DropBonusUtil.generateBonus(_plugin, event.getEntity());
        }
    }
    
}
