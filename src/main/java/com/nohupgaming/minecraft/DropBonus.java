package com.nohupgaming.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nohupgaming.minecraft.listener.block.DropBonusBlockListener;
import com.nohupgaming.minecraft.listener.entity.DropBonusEntityListener;

public class DropBonus extends JavaPlugin 
{
    private DropBonusBlockListener _bl;
    private DropBonusEntityListener _el;
    private Random _gen;
    
    public DropBonus()
    {
        _bl = new DropBonusBlockListener(this);
        _el = new DropBonusEntityListener(this);
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
        System.out.println("DropBonus has been enabled.");
    }
    
    protected void buildConfiguration() 
    {
        Configuration c = getConfiguration();
        if (c != null)
        {
            c.setProperty(Constants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() + 
                Constants.BONUS_PROBABILITY_SUFFIX, 20);
            
            List<Double> vals = new ArrayList<Double>();
            vals.add(new Double(100));
            vals.add(new Double(100));                        
            c.setProperty(Constants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() +
                Constants.BONUS_CHANCES_BRIDGE + 
                Material.COBBLESTONE.toString().toLowerCase(), vals);
            
            String valarr = ".1 0 1";
            
            c.setProperty(Constants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() +
                Constants.BONUS_CHANCES_BRIDGE + 
                Material.OBSIDIAN.toString().toLowerCase(), valarr);
            c.setProperty(Constants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() + 
                Constants.BONUS_OVERRIDE_SUFFIX, false);
            
            c.setProperty(Constants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() +
                Constants.BONUS_TOOL_BRIDGE +
                Material.DIAMOND_PICKAXE.toString().toLowerCase() + 
                Constants.BONUS_CHANCES_BRIDGE +
                Material.STONE.toString().toLowerCase()
                , 100);

            c.setProperty(Constants.BONUS_PREFIX + 
                Material.STONE.toString().toLowerCase() +
                Constants.BONUS_MAXNUMBER_SUFFIX
                , -1);
            
            c.setProperty(Constants.BONUS_PREFIX + 
                Constants.CRAFTCHICKEN +
                Constants.BONUS_PROBABILITY_SUFFIX, 99.9);
            c.setProperty(Constants.BONUS_PREFIX + 
                Constants.CRAFTCHICKEN +
                Constants.BONUS_CHANCES_BRIDGE + 
                Material.EGG.toString().toLowerCase(), 99.9);

            if (!c.save())
            {
                getServer().getLogger().warning("Unable to persist configuration files, changes will not be saved.");
            }
        }
    }
    
    public Random getGenerator()
    {
        return _gen;
    }
    
}
