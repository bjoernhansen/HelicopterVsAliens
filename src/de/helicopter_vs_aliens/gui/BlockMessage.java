package de.helicopter_vs_aliens.gui;

import java.util.*;


public enum BlockMessage
{
    HELICOPTER_ALREADY_REPAIRED,
    UNREPAIRED_BEFORE_MISSION,
    UNREPAIRED_BEFORE_UPGRADE,
    REACHED_MAXIMUM_LEVEL,
    NOT_ENOUGH_MONEY_FOR_UPGRADE,
    UPGRADE_ALREADY_INSTALLED,
    NOT_ENOUGH_MONEY_FOR_REPAIRS,
    HELIOS_NOT_AVAILABLE,
    HELICOPTER_NOT_AVAILABLE;
    
    
    private static final BlockMessage[]
        defensiveCopyOfValues = values();
        
    private static final List<String>
        SUBKEYS = List.of(
                "helicopterAlreadyRepaired.",
                "notRepairedBeforeMission.",
                "notRepairedBeforeUpgrade.",
                "reachendMaximumLevel.",
                "notEnoughMoneyForUpgrade.",
                "upgradeAlreadyInstalled.",
                "notEnoughMoneyForRepairs.",
                "heliosNotAvailable.",
                "helicopterNotAvailable.");
    
    
    public String getKey()
    {
        return "blockMessage." + SUBKEYS.get(this.ordinal());
    }
    
    public static BlockMessage[] getValues()
    {
        return defensiveCopyOfValues;
    }
}