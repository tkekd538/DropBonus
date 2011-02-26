package com.nohupgaming.minecraft;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nohupgaming.minecraft.listener.block.BlockBonusBlockListener;

public class BlockBonus extends JavaPlugin 
{
    private BlockBonusBlockListener _bl;
    
    public BlockBonus()
    {
        _bl = new BlockBonusBlockListener(this);
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
            c.setProperty(Constants.BONUS_STONE_CHANCE, 200);
            c.setProperty(Constants.BONUS_STONE_COBBLE, 1000);
            c.setProperty(Constants.BONUS_STONE_DIRT, 10);
            c.setProperty(Constants.BONUS_STONE_OBSIDIAN, 10);
            c.setProperty(Constants.BONUS_STONE_DIAMOND, 10);
            c.setProperty(Constants.BONUS_STONE_REDSTONE, 10);
            c.setProperty(Constants.BONUS_STONE_GRAVEL, 10);
            c.setProperty(Constants.BONUS_STONE_GOLD, 10);
            c.setProperty(Constants.BONUS_STONE_IRON, 10);
            c.setProperty(Constants.BONUS_STONE_COAL, 10);
            c.setProperty(Constants.BONUS_STONE_LAPIS, 10);
            c.setProperty(Constants.BONUS_STONE_MOSS, 10);

            c.setProperty("bonuses.mossy_cobblestone.probability", 200);
            c.setProperty("bonuses.mossy_cobblestone.chances.cobblestone", 1000);
    
            if (!c.save())
            {
                getServer().getLogger().warning("Unable to persist configuration files, changes will not be saved.");
            }
        }
    }
    
}
