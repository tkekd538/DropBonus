package com.nohupgaming.minecraft.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import com.nohupgaming.minecraft.DropBonus;

public class DropBonusEvaluator 
{

    private DropBonus _plugin;
    private Player _pl;
    private Object _obj;
    private List<ItemStack> _result;

    public DropBonusEvaluator(DropBonus db, Player pl, Object obj)
    {
        _plugin = db;
        _pl = pl;
        _obj = obj;
        _result = new ArrayList<ItemStack>();        
    }
    
    public boolean isOverride()
    {
        return _plugin.getWorldConfiguration(_pl).getBoolean(
            determinePath(_obj, DropBonusConstants.BONUS_OVERRIDE_SUFFIX), false);
    }
    
    public boolean hasBonus()
    {        
        String path = determinePath(_obj, DropBonusConstants.BONUS_PROBABILITY_SUFFIX);
        double opt = DropBonusUtil.checkBounds(_plugin.getWorldConfiguration(_pl).getDouble(path, 0));
        return hasPermission(path) && DropBonusUtil.rollPassed(opt);
    }
    
    public boolean requiresKiller()
    {
        boolean result = false; 
        String path = determinePath(_obj, DropBonusConstants.BONUS_KILLER_SUFFIX);
        result = _plugin.getWorldConfiguration(_pl).getBoolean(path, false);

        if (!result)
        {
            path = DropBonusConstants.CREATURE_NODE + DropBonusConstants.BONUS_KILLER_SUFFIX;
            result = _plugin.getWorldConfiguration(_pl).getBoolean(path, false);
        }

        return result;        
    }
    
    public void generateBonus()
    {
        Configuration c = _plugin.getWorldConfiguration(_pl);
        String path = null;
        
        path = determinePath(_obj, DropBonusConstants.BONUS_MAXNUMBER_SUFFIX);
        // Maximum number of bonuses that can be met
        int max = c.getInt(path, -1);
        
        // Determine tool-specific bonuses
        if (_pl != null)
        {            
            path = determinePath(_obj, DropBonusConstants.BONUS_TOOL_BRIDGE + 
                _pl.getItemInHand().getType().toString().toLowerCase());
            
            if (hasPermission(path))
            {
                path = determinePath(_obj, DropBonusConstants.BONUS_TOOL_BRIDGE + 
                    _pl.getItemInHand().getType().toString().toLowerCase() + 
                    DropBonusConstants.BONUS_COINS_SUFFIX);
                
                // Determine tool-specific bank adjustment
                if (_plugin.hasIConomy()) 
                {
                    affectBank(c, _pl, path);
                }
                
                path = determinePath(_obj, DropBonusConstants.BONUS_TOOL_BRIDGE + 
                    _pl.getItemInHand().getType().toString().toLowerCase() + 
                    DropBonusConstants.BONUS_CHANCES_BRIDGE);
                buildBonus(c, path, max);
                
                path = determinePath(_obj, DropBonusConstants.BONUS_TOOL_BRIDGE + 
                    _pl.getItemInHand().getType().toString().toLowerCase() + 
                    DropBonusConstants.BONUS_TOOLDAMAGE_SUFFIX);
                affectTool(c, _pl, path);
            }
        }
        
        // Determine overall level bank adjustment
        path = determinePath(_obj, DropBonusConstants.BONUS_COINS_SUFFIX);        
        if (_plugin.hasIConomy()) affectBank(c, _pl, path);        
                
        // Determine overall level bonuses
        path = determinePath(_obj, DropBonusConstants.BONUS_CHANCES_BRIDGE);
        if (hasPermission(path))
        {
            buildBonus(c, path, max);
        }
        
        dropItemStack();        
    }
    
    private String determinePath(Object o, String suffix)
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
    
    
    private boolean hasPermission(String path)
    {
        if (_plugin.getPermissionHandler() != null && _pl != null)
        {
            return _plugin.getPermissionHandler().has(_pl, path);
        }
        return true;
    }
    
    private boolean hasRoom(int max, int size)
    {
        return max == -1 || size < max;
    }    
    
    private void affectBank(Configuration c, Player pl, String path)
    {
        try
        {
            if (c.getString(path) != null)
            {
                ChanceValues v = new ChanceValues(c.getString(path));
                
                double opt = DropBonusUtil.checkBounds(v.getPercentage());
                if (DropBonusUtil.rollPassed(opt))
                {
                    if(iConomy.getBank().hasAccount(pl.getName())) 
                    {
                        int amt = DropBonusUtil.checkMinMax(v);
                        
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
            if (!DropBonusUtil.getBankError())
            {
                System.out.println("DropBonus : Unable to apply bank bonuses : " + e.getMessage());
                DropBonusUtil.setBankError(true);
            }
        }
    }
    
    private void alertBank(Configuration c, Player pl, String path, int amt)
    {
        String msg = c.getString(path);
        if (msg != null)
        {
            pl.sendMessage(msg.replaceAll("\\[amount\\]", Integer.toString(amt)));
        }
    }
    
    private void affectTool(Configuration c, Player pl, String path)
    {
        if (c.getString(path) != null)
        {
            ItemStack _obj = pl.getItemInHand();
            _obj.setDurability((short) (_obj.getDurability() + 
                 c.getInt(path, 0)));
        }
    }
    
    private void buildBonus(Configuration c, String path, int max)
    {
        if (c.getKeys(path) != null)
        {
            for (String key : c.getKeys(path))
            {
                Material m = Material.getMaterial(key.toUpperCase());
                
                if (m != null && hasPermission(path + key))
                {
                    if (c.getList(path + key) != null)
                    {
                        for (Object prop : c.getList(path + key))
                        {
                            buildItemStack(m, prop.toString(), max);
                        }
                    }
                    else
                    {
                        buildItemStack(m, c.getString(path + key, "0"), max);
                    }
                }
                else 
                {
                    if (m == null && !DropBonusUtil.getMaterialError())
                    {
                        System.out.println("DropBonus : Configuration file " + 
                            "issue : Unable to find a material named " + key);
                        DropBonusUtil.setMaterialError(true);
                    }
                }
            }
        }
    } 
    
    private void buildItemStack(Material m, String prop, int max)
    {
        ChanceValues v = new ChanceValues(prop);
        double opt = DropBonusUtil.checkBounds(
            v.getPercentage());

        if (DropBonusUtil.rollPassed(opt))
        {
            int num = DropBonusUtil.checkMinMax(v);
            
            for (int i = 0; i < num; i++)
            {
                if (hasRoom(max, _result.size()))
                {
                    if (v.getDataValue() > 0)
                    {
                        _result.add(
                            new ItemStack(m.getId(), 1, 
                            (short) 0, 
                            new Byte(v.getDataValue())));
                    }
                    else
                    {
                        _result.add(new ItemStack(m, 1));
                    }
                }
            }                            
        }                            
    }
    
    private void dropItemStack()
    {
        for (ItemStack stack : _result)
        {
            if (_obj instanceof Block)
            {
                Block b = ((Block) _obj);
                b.getWorld().dropItemNaturally(b.getLocation(), stack);
            } else if (_obj instanceof Entity)
            {
                Entity e = ((Entity) _obj);
                e.getWorld().dropItemNaturally(e.getLocation(), stack);
            }
        }
    }
}
