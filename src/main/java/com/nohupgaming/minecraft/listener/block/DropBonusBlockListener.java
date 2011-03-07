package com.nohupgaming.minecraft.listener.block;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.nohupgaming.minecraft.DropBonus;
import com.nohupgaming.minecraft.util.BlockCooldownExpiration;
import com.nohupgaming.minecraft.util.DropBonusConstants;
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
        
        if (DropBonusUtil.hasBonus(_plugin, pl, b) &&
            !_plugin.isPlacedBlock(b))
        {
            DropBonusUtil.generateBonus(_plugin, pl, b);
        }
        
        if (DropBonusUtil.isOverride(_plugin, pl, b))
        {
            b.setType(Material.AIR);
        }        
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) 
    {
        String path = DropBonusConstants.BLOCK_NODE + 
            DropBonusConstants.BLOCK_COOLDOWN_SUFFIX;
        int cooldown = _plugin.getConfiguration().getInt(path, 0);
        if (cooldown > 0)
        {
            Block b = event.getBlock();
            _plugin.addPlacedBlock(b);
            _plugin.getServer().getScheduler().scheduleAsyncDelayedTask(_plugin, new BlockCooldownExpiration(_plugin, b), cooldown);
        }
    }
}
