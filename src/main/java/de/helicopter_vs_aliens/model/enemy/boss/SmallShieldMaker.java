package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;


public final class SmallShieldMaker extends ShieldMaker
{
    @Override
    FinalBossServantType shieldingBrother()
    {
        return FinalBossServantType.BIG_SHIELD_MAKER;
    }
}
