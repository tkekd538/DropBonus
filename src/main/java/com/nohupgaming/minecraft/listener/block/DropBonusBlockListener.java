package com.nohupgaming.minecraft.listener.block;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import com.nohupgaming.minecraft.DropBonus;
import com.nohupgaming.minecraft.util.DropBonusUtil;

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
        Player pl = event.getPlayer();
        
        if (DropBonusUtil.hasBonus(_plugin, pl, b))
        {
            DropBonusUtil.generateBonus(_plugin, pl, b);
        }
        
        if (DropBonusUtil.isOverride(_plugin, pl, b))
        {
            b.setType(Material.AIR);
        }
        
    }    
}
