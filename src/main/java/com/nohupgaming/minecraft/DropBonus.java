package com.nohupgaming.minecraft;

import java.util.Random;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nohupgaming.minecraft.listener.block.DropBonusBlockListener;

public class DropBonus extends JavaPlugin 
{
    private DropBonusBlockListener _bl;
    private Random _gen;
    
    public DropBonus()
    {
        _bl = new DropBonusBlockListener(this);
        _gen = new Random();
    }
    
    public void onDisable() 
    {
        System.out.println("BlockBonus has been disabled.");
    }

    public void onEnable() 
    {
        if (!getDataFolder().exists())
        {
            buildConfiguration();
        }
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.BLOCK_BREAK, _bl, Priority.Normal, this);
        System.out.println("BlockBonus has been enabled.");
    }
    
    protected void buildConfiguration() 
    {
        Configuration c = getConfiguration();
        if (c != null)
        {
            c.setProperty(Constants.BONUS_STONE_CHANCE, 20);
            c.setProperty(Constants.BONUS_STONE_COBBLE, 100);
            c.setProperty(Constants.BONUS_STONE_DIRT, 1);
            c.setProperty(Constants.BONUS_STONE_OBSIDIAN, .1);
            c.setProperty(Constants.BONUS_STONE_DIAMOND, 1);
            c.setProperty(Constants.BONUS_STONE_REDSTONE, 1);
            c.setProperty(Constants.BONUS_STONE_GRAVEL, 1);
            c.setProperty(Constants.BONUS_STONE_GOLD, 1);
            c.setProperty(Constants.BONUS_STONE_IRON, 1);
            c.setProperty(Constants.BONUS_STONE_COAL, 1);
            c.setProperty(Constants.BONUS_STONE_LAPIS, 1);
            c.setProperty(Constants.BONUS_STONE_MOSS, 1);
            
            c.setProperty(Constants.BONUS_CHICKEN_CHANCE, 99.9);
            c.setProperty(Constants.BONUS_CHICKEN_EGG, 99.9);

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