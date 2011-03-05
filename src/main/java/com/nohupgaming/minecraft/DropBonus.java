package com.nohupgaming.minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nohupgaming.minecraft.listener.block.DropBonusBlockListener;
import com.nohupgaming.minecraft.listener.entity.DropBonusEntityListener;
import com.nohupgaming.minecraft.util.DropBonusConstants;

public class DropBonus extends JavaPlugin 
{
    private DropBonusBlockListener _bl;
    private DropBonusEntityListener _el;
    private PermissionHandler _permissions;
    private HashMap<String, Configuration> _configs;
    private boolean _iConomy = false;
    
    public DropBonus()
    {
        _bl = new DropBonusBlockListener(this);
        _el = new DropBonusEntityListener(this);        
        _configs = new HashMap<String, Configuration>();
        _permissions = null;
    }
    
    public void onDisable() 
    {
        System.out.println("DropBonus has been disabled.");
    }

    public void onEnable() 
    {
        if (!getDataFolder().exists())
        {
            buildConfiguration();
        }
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.BLOCK_BREAK, _bl, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_DAMAGED, _bl, Priority.Normal, this);
        pm.registerEvent(Type.ENTITY_DAMAGED, _el, Priority.Normal, this);
        pm.registerEvent(Type.ENTITY_DEATH, _el, Priority.Normal, this);        
        
        if (pm.getPlugin(DropBonusConstants.PERMISSIONS) != null)
        {
            Permissions perm = (Permissions) pm.getPlugin(Permissions.name);
            _permissions = perm.getHandler(); 
        }
        
        if (pm.getPlugin(DropBonusConstants.ICONOMY) != null)
        {
            _iConomy = true;
        }
        
        System.out.println("DropBonus has been enabled.");
    }
    
    public PermissionHandler getPermissionHandler()
    {
        return _permissions;
    }
    
    public boolean hasIConomy()
    {
        return _iConomy;
    }
    
    protected void buildConfiguration() 
    {
        Configuration c = getConfiguration();
        if (c != null)
        {
            c.setProperty(DropBonusConstants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() + 
                DropBonusConstants.BONUS_PROBABILITY_SUFFIX, 20);
            
            List<Double> vals = new ArrayList<Double>();
            vals.add(new Double(100));
            vals.add(new Double(100));                        
            c.setProperty(DropBonusConstants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() +
                DropBonusConstants.BONUS_CHANCES_BRIDGE + 
                Material.COBBLESTONE.toString().toLowerCase(), vals);
            
            String valarr = ".1 0 1";
            
            c.setProperty(DropBonusConstants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() +
                DropBonusConstants.BONUS_CHANCES_BRIDGE + 
                Material.OBSIDIAN.toString().toLowerCase(), valarr);
            c.setProperty(DropBonusConstants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() + 
                DropBonusConstants.BONUS_OVERRIDE_SUFFIX, false);
            
            c.setProperty(DropBonusConstants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() +
                DropBonusConstants.BONUS_TOOL_BRIDGE +
                Material.DIAMOND_PICKAXE.toString().toLowerCase() + 
                DropBonusConstants.BONUS_CHANCES_BRIDGE +
                Material.STONE.toString().toLowerCase()
                , 100);

            c.setProperty(DropBonusConstants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() +
                DropBonusConstants.BONUS_MAXNUMBER_SUFFIX
                , -1);
            
            c.setProperty(DropBonusConstants.BONUS_PREFIX + 
                DropBonusConstants.CRAFTCHICKEN +
                DropBonusConstants.BONUS_PROBABILITY_SUFFIX, 99.9);
            c.setProperty(DropBonusConstants.BONUS_PREFIX + 
                DropBonusConstants.CRAFTCHICKEN +
                DropBonusConstants.BONUS_CHANCES_BRIDGE + 
                Material.EGG.toString().toLowerCase(), 99.9);

            if (!c.save())
            {
                getServer().getLogger().warning("Unable to persist configuration files, changes will not be saved.");
            }
        }
    }
    
    public Configuration getWorldConfiguration(Player pl) 
    {
        String wName = null;
        
        if (pl != null)
        {
            wName = pl.getWorld().getName();
            if (!_configs.keySet().contains(wName))
            {
                String path = wName + ".config.yml";
                File f = new File(getDataFolder(), path);
                
                Configuration c = null;
                
                if (f.exists())
                {
                    c = new Configuration(f);
                    c.load();                
                }
                _configs.put(wName, c);
            }
        }
        
        Configuration wcfg = null;
        if (wName != null) _configs.get(wName);
        return  wcfg == null ? getConfiguration() : wcfg;
    }
    
}
