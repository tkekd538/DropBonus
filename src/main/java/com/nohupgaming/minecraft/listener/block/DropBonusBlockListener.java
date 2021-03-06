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
import com.nohupgaming.minecraft.util.DropBonusEvaluator;

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
        if (!event.isCancelled())
        {
            Block b = event.getBlock();
            Player pl = event.getPlayer();
            DropBonusEvaluator eval = new DropBonusEvaluator(_plugin, pl, b);
            
            if (eval.hasBonus() &&
                !_plugin.isPlacedBlock(b))
            {
                eval.generateBonus();
            }
            
            if (eval.isOverride())
            {
                b.setType(Material.AIR);
            }
        }
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) 
    {
        if (!event.isCancelled() && _plugin.getBlockCooldown() > 0)
        {
            String path = DropBonusConstants.BONUS_PREFIX + 
                event.getBlock().getType().toString().toLowerCase();
            if (_plugin.getConfiguration().getNode(path) != null)
            {
                Block b = event.getBlock();
                _plugin.addPlacedBlock(b);
                _plugin.getServer().getScheduler().scheduleAsyncDelayedTask(
                    _plugin, new BlockCooldownExpiration(_plugin, b), 
                    _plugin.getBlockCooldown());
            }
        }
    }
}
