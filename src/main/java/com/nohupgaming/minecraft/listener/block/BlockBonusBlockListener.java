package com.nohupgaming.minecraft.listener.block;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import com.nohupgaming.minecraft.BlockBonus;

public class BlockBonusBlockListener extends BlockListener 
{
    private final BlockBonus _plugin;
    private Random _gen;
    
    public BlockBonusBlockListener(final BlockBonus plugin)
    {
        _plugin = plugin;
        _gen = new Random();
    }
    
    @Override
    public void onBlockBreak(BlockBreakEvent event) 
    {
        if (hasBonus(event.getBlock().getType()))
        {
            generateBonus(event.getBlock());
        }
    }
    
    private boolean hasBonus(Material m)
    {
        String path = "bonuses." + m.toString().toLowerCase() + ".probability";
        int opt = _plugin.getConfiguration().getInt(path, 0);
        if (opt > 0)
        {
            long roll = Math.round(_gen.nextDouble() * 1000);
            return (roll > 0 && roll <= opt);
        }
        
        return false;
    }
    
    private List<ItemStack> generateBonus(Block b)
    {
        List<ItemStack> result = new ArrayList<ItemStack>();
        String path = "bonuses." + b.getType().toString().toLowerCase() + ".chances";
        Configuration c = _plugin.getConfiguration();
        
        for (String key : c.getKeys(path))
        {
            Material m = Material.getMaterial(key.toUpperCase());
            int opt = c.getInt(path + "." + key, 0);

            //Bounds checking
            if (opt > 1000) opt = 1000;
            if (opt < 0) opt = 0;
            
            if (opt > 0)
            {
                long roll = Math.round(_gen.nextDouble() * 1000);                
                if (roll > 0 && roll <= opt)
                {
                    result.add(new ItemStack(m, 1));
                }
            }
        }
        
        for (ItemStack stack : result)
        {
            b.getWorld().dropItemNaturally(b.getLocation(), stack);
        }
        
        return result;
    }
}
