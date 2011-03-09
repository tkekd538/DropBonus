package com.nohupgaming.minecraft.util;

import java.util.StringTokenizer;

public class ChanceValues 
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
