package com.nohupgaming.minecraft.util;

import org.bukkit.block.Block;

import com.nohupgaming.minecraft.DropBonus;

public class BlockCooldownExpiration implements Runnable {

    DropBonus _p;
    Block _b;
    
    public BlockCooldownExpiration(DropBonus p, Block b)
    {
        _p = p;
        _b = b;
    }
    
    public void run() 
    {
        if (_p.isPlacedBlock(_b))
        {
            _p.removePlacedBlock(_b);            
        }
    }

}
