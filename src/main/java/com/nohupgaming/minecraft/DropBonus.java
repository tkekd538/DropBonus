package com.nohupgaming.minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
import com.nohupgaming.minecraft.listener.vehicle.DropBonusVehicleListener;
import com.nohupgaming.minecraft.util.DropBonusConstants;

public class DropBonus extends JavaPlugin 
{
    private DropBonusBlockListener _bl;
    private DropBonusEntityListener _el;
    private DropBonusVehicleListener _vl;
    private PermissionHandler _permissions;
    private HashMap<String, Configuration> _configs;
    private List<Block> _placed;   
    private boolean _iConomy = false;
    private int _coolit = -1;
    
    public DropBonus()
    {
        _bl = new DropBonusBlockListener(this);
        _el = new DropBonusEntityListener(this);
        _vl = new DropBonusVehicleListener(this);
        _configs = new HashMap<String, Configuration>();
        _placed = new ArrayList<Block>();
        _permissions = null;
    }
    
    public void onDisable() 
    {
        System.out.println("DropBonus " + DropBonusConstants.VERSION + " has been disabled.");
    }

    public void onEnable() 
    {
        if (!getDataFolder().exists())
        {
            buildConfiguration();
        }
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.BLOCK_BREAK, _bl, Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_PLACED, _bl, Priority.Monitor, this);
        pm.registerEvent(Type.ENTITY_DAMAGED, _el, Priority.Monitor, this);
        pm.registerEvent(Type.ENTITY_DEATH, _el, Priority.Normal, this);
        pm.registerEvent(Type.VEHICLE_COLLISION_BLOCK, _vl, Priority.Normal, this);
        pm.registerEvent(Type.VEHICLE_DAMAGE, _vl, Priority.Monitor, this);
        
        
        if (pm.getPlugin(DropBonusConstants.PERMISSIONS) != null)
        {
            Permissions perm = (Permissions) pm.getPlugin(Permissions.name);
            _permissions = perm.getHandler(); 
        }
        
        if (pm.getPlugin(DropBonusConstants.ICONOMY) != null)
        {
            _iConomy = true;
        }
        
        System.out.println("DropBonus " + DropBonusConstants.VERSION + "  has been enabled.");
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

            c.setProperty(DropBonusConstants.BLOCK_NODE + 
                DropBonusConstants.BLOCK_COOLDOWN_SUFFIX, 0);

            c.setProperty(DropBonusConstants.CREATURE_NODE + 
                DropBonusConstants.BONUS_KILLER_SUFFIX, 0);
            
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
        if (wName != null) wcfg = _configs.get(wName);
        return  wcfg == null ? getConfiguration() : wcfg;
    }
    
    public void addPlacedBlock(Block b)
    {
        _placed.add(b);
    }
    
    public void removePlacedBlock(Block b)
    {
        _placed.remove(b);
    }
    
    public boolean isPlacedBlock(Block b)
    {
        return _placed.contains(b);
    }
    
    public int getBlockCooldown()
    {
        if (_coolit < 0)
        {
            _coolit = getConfiguration().getInt(DropBonusConstants.BLOCK_NODE + 
                DropBonusConstants.BLOCK_COOLDOWN_SUFFIX, 0);
        }
        
        return _coolit;
    }
}
