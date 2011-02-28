package com.nohupgaming.minecraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

public class DropBonusUtil 
{
    public static boolean hasBonus(DropBonus p, Object target)
    {        
        String path = determinePath(target);
        double opt = checkBounds(p.getConfiguration().getDouble(path, 0));
        
        if (opt > 0)
        {
            long roll = Math.round(p.getGenerator().nextDouble() * 100);
            return (roll > 0 && roll <= opt);
        }
        
        return false;
    }
    
    public static List<ItemStack> generateBonus(DropBonus p, Object target)
    {
        String path = determinePath(target);
        Configuration c = p.getConfiguration();

        List<ItemStack> result = new ArrayList<ItemStack>();
                
        for (String key : c.getKeys(path))
        {
            Material m = Material.getMaterial(key.toUpperCase());
            double opt = checkBounds(c.getDouble(path + "." + key, 0));

            if (opt > 0)
            {
                long roll = Math.round(p.getGenerator().nextDouble() * 100);                
                if (roll > 0 && roll <= opt)
                {
                    result.add(new ItemStack(m, 1));
                }
            }
        }
        
        for (ItemStack stack : result)
        {
            if (target instanceof Block)
            {
                Block b = ((Block) target);
                b.getWorld().dropItemNaturally(b.getLocation(), stack);
            } else if (target instanceof Entity)
            {
                Entity e = ((Entity) target);
                e.getWorld().dropItemNaturally(e.getLocation(), stack);
            }
        }
        
        return result;
    }
    
    private static String determinePath(Object o)
    {
        String path = null;
        
        if (o instanceof Block)
        {
            path = "bonuses." + ((Block) o).getType().toString()
                .toLowerCase() + ".probability";
        }
        else if (o instanceof Entity)
        {
            path = "bonuses." + ((Entity) o).toString()
                .toLowerCase() + ".probability";
        }
        
        return path;
    }
    
    private static double checkBounds(double val)
    {
        double opt = val;
        if (opt > 100) opt = 100;
        if (opt < 0) opt = 0;
        return opt;
    }
}
