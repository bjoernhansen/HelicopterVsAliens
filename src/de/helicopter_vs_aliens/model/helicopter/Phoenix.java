package de.helicopter_vs_aliens.model.helicopter;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.PHOENIX;

public final class Phoenix extends Helicopter
{
    public static final int
        TELEPORT_KILL_TIME = 15,		// in dieser Zeit [frames] nach einer Teleportation vernichtete Gegner werden für den Extra-Bonus gewertet
        NICE_CATCH_TIME = 22,			// nur wenn die Zeit [frames] zwischen Teleportation und Gegner-Abschuss kleiner ist, gibt es den "NiceCath-Bonus"
        TELEPORT_INVU_TIME = 45,
        GOLIATH_COSTS = 6000;
    
    
    @Override
    public HelicopterTypes getType()
    {
        return PHOENIX;
    }
    
    @Override
    public int getGoliathCosts()
    {
        return GOLIATH_COSTS;
    }
    
    @Override
    public void obtainSomeUpgrades()
    {
        this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
        super.obtainSomeUpgrades();
    }
    
    @Override
    public boolean hasFifthSpecial()
    {
        return this.hasShortrangeRadiation;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.hasShortrangeRadiation = true;
    }
}