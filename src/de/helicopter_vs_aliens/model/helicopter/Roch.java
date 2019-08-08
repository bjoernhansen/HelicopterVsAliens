package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.util.MyMath;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.ROCH;

public final class Roch extends Helicopter
{
    public static final int
        JUMBO_MISSILE_COSTS = 25000,
        ROCH_SECOND_CANNON_COSTS = 225000;
    
    static final float
        JUMBO_MISSILE_DMG_FACTOR = 2.36363637f;	// Faktor, um den sich die Schadenswirkung der Raketen erhöht, nachdem das Jumbo-Raketen-Spezial-Upgrade erworben wurde
    
    
    @Override
    public HelicopterTypes getType()
    {
        return ROCH;
    }
    
    @Override
    public int getPiercingWarheadsCosts()
    {
         return CHEAP_SPECIAL_COSTS;
    }
    
    @Override
    public void obtainSomeUpgrades()
    {
        this.hasPiercingWarheads = true;
        super.obtainSomeUpgrades();
    }
    
    @Override
    public boolean hasFifthSpecial()
    {
        return this.hasJumboMissiles();
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.missileDamageFactor = JUMBO_MISSILE_DMG_FACTOR;
        this.currentFirepower = (int)(this.missileDamageFactor * MyMath.dmg(this.levelOfUpgrade[StandardUpgradeTypes.FIREPOWER.ordinal()]));
    }
}