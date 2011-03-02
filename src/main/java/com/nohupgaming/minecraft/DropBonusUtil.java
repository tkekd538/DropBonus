package com.nohupgaming.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

public class DropBonusUtil 
{
    private static Random _gen;
    
    private static class ChanceValues
    {
        double _pct;
        int _min;
        int _max;
        
        public ChanceValues(String s)
        {
            StringTokenizer st = new StringTokenizer(s, " ", false);
            _pct = st.hasMoreTokens() ? Double.parseDouble(st.nextToken()) : 0;
            
            int testMin = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
            _min = testMin < 0 ? 0 : testMin;
            
            int testMax = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1; 
            _max = testMax < _min ? _min : testMax;             
        }
        
        public double getPercentage()
        {
            return _pct;
        }
        
        public int getMinimum()
        {
            return _min;
        }
        
        public int getMaximum()
        {
            return _max;
        }
    }
    
    public static boolean isOverride(DropBonus p, Object target)
    {
        return p.getConfiguration().getBoolean(
            determinePath(target, Constants.BONUS_OVERRIDE_SUFFIX), false);
    }
    
    public static boolean hasBonus(DropBonus p, Object target)
    {        
        String path = determinePath(target, Constants.BONUS_PROBABILITY_SUFFIX);
        double opt = checkBounds(p.getConfiguration().getDouble(path, 0));        
        return rollPassed(opt);
    }
    
    public static List<ItemStack> generateBonus(DropBonus p, Player pl, Object target)
    {
        Configuration c = p.getConfiguration();
        List<ItemStack> result = new ArrayList<ItemStack>();        
        String path = null;
        
        path = determinePath(target, Constants.BONUS_MAXNUMBER_SUFFIX);
        // Maximum number of bonuses that can be met
        int max = c.getInt(path, -1);
        
        // Determine tool-specific bonuses
        if (pl != null)
        {
            path = determinePath(target, Constants.BONUS_TOOL_BRIDGE + 
                pl.getItemInHand().getType().toString().toLowerCase() + 
                Constants.BONUS_CHANCES_BRIDGE);
            
            buildBonus(c, path, max, result);
        }
                
        // Determine overall level bonuses
        path = determinePath(target, Constants.BONUS_CHANCES_BRIDGE);                
        buildBonus(c, path, max, result);
        
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
    
    private static String determinePath(Object o, String suffix)
    {
        String path = null;

        if (o instanceof Block)
        {
            path = Constants.BONUS_PREFIX + 
                ((Block) o).getType().toString().toLowerCase() + 
                suffix;
        }
        else if (o instanceof Entity)
        {
            path = Constants.BONUS_PREFIX + 
                ((Entity) o).toString().toLowerCase() + 
                suffix;
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
    
    private static int checkMinMax(int val, ChanceValues cv)
    {
        if (val < cv.getMinimum()) return cv.getMinimum();
        if (val > cv.getMaximum())
        {
            return cv.getMaximum();
        }
        
        return val;
    }
    
    private static boolean rollPassed(double val)
    {
        double roll = getGenerator().nextDouble() * 100;                
        return (val > 0 && roll > 0 && roll <= val);
    }
    
    private static boolean hasRoom(int max, int size)
    {
        return max == -1 || size < max;
    }
    
    private static Random getGenerator()
    {
        if (_gen == null)
        {
            _gen = new Random();
        }
        
        return _gen;
    }
    
    private static void buildBonus(Configuration c, String path, int max, List<ItemStack> result)
    {
        if (c.getKeys(path) != null)
        {
            for (String key : c.getKeys(path))
            {
                Material m = Material.getMaterial(key.toUpperCase());
                
                if (c.getList(path + key) != null)
                {
                    for (Object d : c.getList(path + key))
                    {
                        ChanceValues v = new ChanceValues(d.toString());
                        double opt = checkBounds(v.getPercentage());
                        if (rollPassed(opt))
                        {
                            int num = checkMinMax(getGenerator().nextInt(v.getMaximum() + 1), v);
                            
                            for (int i = 0; i < num; i++)
                            {
                                if (hasRoom(max, result.size()))
                                {
                                    result.add(new ItemStack(m, 1));
                                }
                            }                            
                        }                            
                    }
                }
                else
                {
                    ChanceValues v = new ChanceValues(c.getString(path + key, "0"));
                    double opt = checkBounds(v.getPercentage());
                    if (rollPassed(opt))
                    {
                        int num = checkMinMax(getGenerator().nextInt(
                            v.getMaximum() + 1), v);
                        
                        for (int i = 0; i < num; i++)
                        {
                            if (hasRoom(max, result.size()))
                            {
                                result.add(new ItemStack(m, 1));
                            }
                        }                            
                    }                            
                }                    
            }
        }
    }    
}
