package com.nohupgaming.minecraft.util;

import java.util.Random;

public class DropBonusUtil 
{
    private static Random _gen;
    
    private static boolean _bankErr = false;
    private static boolean _materialErr = false;
    
    public static Random getGenerator()
    {
        if (_gen == null)
        {
            _gen = new Random();
        }
        
        return _gen;
    }

    public static int randomInt(int low, int high)
    {
        int x = getGenerator().nextInt(Math.abs(high - low) + 1);
        return (Math.min(low, high) + x);
    }
    
    public static double checkBounds(double val)
    {
        double opt = val;
        if (opt > 100) opt = 100;
        if (opt < 0) opt = 0;
        return opt;
    }
    
    public static int checkMinMax(ChanceValues cv)
    {
        int val = randomInt(cv.getMinimum(), cv.getMaximum()); 
        if (val < cv.getMinimum()) return cv.getMinimum();
        if (val > cv.getMaximum())
        {
            return cv.getMaximum();
        }
        
        return val;
    }
    
    public static boolean rollPassed(double val)
    {
        double roll = getGenerator().nextDouble() * 100;                
        return (val > 0 && roll > 0 && roll <= val);
    }
    
    public static void setBankError(boolean b)
    {
        _bankErr = b;
    }
    
    public static boolean getBankError()
    {
        return _bankErr;
    }
    
    public static void setMaterialError(boolean b)
    {
        _materialErr = b;
    }
    
    public static boolean getMaterialError()
    {
        return _materialErr;
    }
}
