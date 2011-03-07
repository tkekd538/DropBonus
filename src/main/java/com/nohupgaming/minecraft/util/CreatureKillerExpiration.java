package com.nohupgaming.minecraft.util;

import java.util.HashMap;

import org.bukkit.entity.Entity;

public class CreatureKillerExpiration implements Runnable 
{
    private HashMap<Entity, Entity> _m;
    private Entity _e;
    
    public CreatureKillerExpiration(HashMap<Entity, Entity> map, Entity e)
    {
        _m = map;
        _e = e;
    }
    
    public void run() 
    {        
        _m.remove(_e);
    }
}
