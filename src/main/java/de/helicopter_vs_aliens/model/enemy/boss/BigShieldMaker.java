package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.util.Calculations;

public final class BigShieldMaker extends ShieldMaker
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        shootTimer = 0;
        shootingRate = 25;
        shotSpeed = 1;
        
        super.doTypeSpecificInitialization();
    }
    
    @Override
    FinalBossServantType getShieldingBrotherServantType()
    {
        return FinalBossServantType.SMALL_SHIELD_MAKER;
    }
    
    @Override
    protected boolean hasDeadlyShots()
    {
        return Calculations.tossUp();
    }
}
