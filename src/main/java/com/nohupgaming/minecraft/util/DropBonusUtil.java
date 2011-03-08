package com.nohupgaming.minecraft.util;

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

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import com.nohupgaming.minecraft.DropBonus;

public class DropBonusUtil 
{
    private static Random _gen;
    
    private static boolean _bankErr = false;
    private static boolean _materialErr = false;
    
    private static class ChanceValues
    {
        double _pct;
        int _min;
        int _max;
        byte _data;
        
        public ChanceValues(String s)
        {
            StringTokenizer st = new StringTokenizer(s, " ", false);
            _pct = st.hasMoreTokens() ? Double.parseDouble(st.nextToken()) : 0;
            
            _min = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
            
            int testMax = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1; 
            _max = testMax < _min ? _min : testMax;             

            _data = st.hasMoreTokens() ? Byte.parseByte(st.nextToken()) : 0;             
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
        
        public byte getDataValue()
        {
            return _data;
        }
        
        public String toString()
        {
            return "Percentage: " + _pct + "; Minimum: " + _min + "; Maximum: " + _max + "; Data: " +_data;
        }
    }
    
    public static boolean isOverride(DropBonus p, Player pl, Object target)
    {
        return p.getWorldConfiguration(pl).getBoolean(
            determinePath(target, DropBonusConstants.BONUS_OVERRIDE_SUFFIX), false);
    }
    
    public static boolean hasBonus(DropBonus p, Player pl, Object target)
    {        
        String path = determinePath(target, DropBonusConstants.BONUS_PROBABILITY_SUFFIX);
        System.out.println("Path is " + path);
        double opt = checkBounds(p.getWorldConfiguration(pl).getDouble(path, 0));
        return hasPermission(p, pl, path) && rollPassed(opt);
    }
    
    public static boolean requiresKiller(DropBonus p, Player pl, Object target)
    {
        boolean result = false; 
        String path = determinePath(target, DropBonusConstants.BONUS_KILLER_SUFFIX);
        result = p.getWorldConfiguration(pl).getBoolean(path, false);

        if (!result)
        {
            path = DropBonusConstants.CREATURE_NODE + DropBonusConstants.BONUS_KILLER_SUFFIX;
            result = p.getWorldConfiguration(pl).getBoolean(path, false);
        }

        return result;        
    }
    
    public static List<ItemStack> generateBonus(DropBonus p, Player pl, Object target)
    {
        Configuration c = p.getWorldConfiguration(pl);
        List<ItemStack> result = new ArrayList<ItemStack>();        
        String path = null;
        
        path = determinePath(target, DropBonusConstants.BONUS_MAXNUMBER_SUFFIX);
        // Maximum number of bonuses that can be met
        int max = c.getInt(path, -1);
        
        // Determine tool-specific bonuses
        if (pl != null)
        {            
            path = determinePath(target, DropBonusConstants.BONUS_TOOL_BRIDGE + 
                pl.getItemInHand().getType().toString().toLowerCase());
            
            if (hasPermission(p, pl, path))
            {
                path = determinePath(target, DropBonusConstants.BONUS_TOOL_BRIDGE + 
                    pl.getItemInHand().getType().toString().toLowerCase() + 
                    DropBonusConstants.BONUS_COINS_SUFFIX);
                
                // Determine tool-specific bank adjustment
                if (p.hasIConomy()) 
                {
                    affectBank(c, pl, path);
                }
                
                path = determinePath(target, DropBonusConstants.BONUS_TOOL_BRIDGE + 
                    pl.getItemInHand().getType().toString().toLowerCase() + 
                    DropBonusConstants.BONUS_CHANCES_BRIDGE);
                buildBonus(c, path, max, result);
                
                path = determinePath(target, DropBonusConstants.BONUS_TOOL_BRIDGE + 
                    pl.getItemInHand().getType().toString().toLowerCase() + 
                    DropBonusConstants.BONUS_TOOLDAMAGE_SUFFIX);
                affectTool(c, pl, path);
            }
        }
        
        // Determine overall level bank adjustment
        path = determinePath(target, DropBonusConstants.BONUS_COINS_SUFFIX);        
        if (p.hasIConomy()) affectBank(c, pl, path);        
                
        // Determine overall level bonuses
        path = determinePath(target, DropBonusConstants.BONUS_CHANCES_BRIDGE);
        if (hasPermission(p, pl, path))
        {
            buildBonus(c, path, max, result);
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
    
    private static String determinePath(Object o, String suffix)
    {
        String path = null;

        if (o instanceof Block)
        {
            path = DropBonusConstants.BONUS_PREFIX + 
                ((Block) o).getType().toString().toLowerCase() + 
                suffix;
        }
        else if (o instanceof Entity)
        {
            path = DropBonusConstants.BONUS_PREFIX + 
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
    
    private static int randomInt(int low, int high)
    {
        int x = getGenerator().nextInt(Math.abs(high - low) + 1);
        return (Math.min(low, high) + x);
    }
    
    private static boolean hasPermission(DropBonus p, Player pl, String path)
    {
        if (p.getPermissionHandler() != null && pl != null)
        {
            return p.getPermissionHandler().has(pl, path);
        }
        return true;
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
    
    private static void affectBank(Configuration c, Player pl, String path)
    {
        try
        {
            if (c.getString(path) != null)
            {
                ChanceValues v = new ChanceValues(c.getString(path));
                
                double opt = checkBounds(v.getPercentage());
                if (rollPassed(opt))
                {
                    if(iConomy.getBank().hasAccount(pl.getName())) 
                    {
                        int amt = checkMinMax(randomInt(v.getMinimum(), v.getMaximum()), v);
                        
                        Account account = iConomy.getBank().getAccount(pl.getName());
                        account.add(amt);
                        account.save();
                        
                        
                        if (amt != 0)
                        {
                            path = DropBonusConstants.MESSAGES_PREFIX + 
                                DropBonusConstants.BANK_NODE + (amt > 0 ? 
                                    DropBonusConstants.MESSAGES_BANKPOSITIVE_SUFFIX :
                                    DropBonusConstants.MESSAGES_BANKNEGATIVE_SUFFIX);
                            alertBank(c, pl, path, amt);
                        }                                                
                    }
                }
            }
        }
        catch (Exception e)
        {
            if (!_bankErr)
            {
                System.out.println("DropBonus : Unable to apply bank bonuses : " + e.getMessage());
                _bankErr = true;
            }
        }
    }
    
    private static void alertBank(Configuration c, Player pl, String path, int amt)
    {
        String msg = c.getString(path);
        if (msg != null)
        {
            pl.sendMessage(msg.replaceAll("\\[amount\\]", Integer.toString(amt)));
        }
    }
    
    private static void affectTool(Configuration c, Player pl, String path)
    {
        if (c.getString(path) != null)
        {
            ItemStack target = pl.getItemInHand();
            target.setDurability((short) (target.getDurability() + 
                 c.getInt(path, 0)));
        }
    }
    
    private static void buildBonus(Configuration c, String path, int max, List<ItemStack> result)
    {
        if (c.getKeys(path) != null)
        {
            for (String key : c.getKeys(path))
            {
                Material m = Material.getMaterial(key.toUpperCase());
                
                if (m != null)
                {
                    if (c.getList(path + key) != null)
                    {
                        for (Object d : c.getList(path + key))
                        {
                            ChanceValues v = new ChanceValues(d.toString());
                            double opt = checkBounds(v.getPercentage());
                            
                            if (rollPassed(opt))
                            {
                                int num = checkMinMax(randomInt(v.getMinimum(), v.getMaximum()), v);
                                
                                for (int i = 0; i < num; i++)
                                {
                                    if (hasRoom(max, result.size()))
                                    {
                                        if (v.getDataValue() > 0)
                                        {
                                            result.add(new ItemStack(m.getId(), 1, (short) 0, new Byte(v.getDataValue())));
                                        }
                                        else
                                        {
                                            result.add(new ItemStack(m, 1));
                                        }
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
                            int num = checkMinMax(randomInt(v.getMinimum(), v.getMaximum()), v);
                            
                            for (int i = 0; i < num; i++)
                            {
                                if (hasRoom(max, result.size()))
                                {
                                    if (v.getDataValue() > 0)
                                    {
                                        result.add(new ItemStack(m.getId(), 1, (short) 0, new Byte(v.getDataValue())));
                                    }
                                    else
                                    {
                                        result.add(new ItemStack(m, 1));
                                    }
                                }
                            }                            
                        }                            
                    }
                }
                else 
                {
                    if (!_materialErr)
                    {
                        System.out.println("DropBonus : Configuration file issue : Unable to find a material named " + key);
                        _materialErr = true;
                    }
                }
            }
        }
    }    
}
