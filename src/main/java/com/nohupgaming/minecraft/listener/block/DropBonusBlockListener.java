package com.nohupgaming.minecraft.listener.block;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import com.nohupgaming.minecraft.DropBonus;
import com.nohupgaming.minecraft.DropBonusUtil;

public class DropBonusBlockListener extends BlockListener 
{
    private final DropBonus _plugin;
    
    public DropBonusBlockListener(final DropBonus plugin)
    {
        _plugin = plugin;
    }
    
    @Override
    public void onBlockBreak(BlockBreakEvent event) 
    {        
        Block b = event.getBlock();
        
        if (DropBonusUtil.hasBonus(_plugin, event.getPlayer(), b))
        {
            DropBonusUtil.generateBonus(_plugin, event.getPlayer(), b);
        }
        
        if (DropBonusUtil.isOverride(_plugin, b))
        {
            b.setType(Material.AIR);
        }
        
    }    
}
