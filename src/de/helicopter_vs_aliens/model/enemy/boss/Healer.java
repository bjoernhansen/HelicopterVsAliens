package de.helicopter_vs_aliens.model.enemy.boss;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class Healer extends FinalBossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canDodge = true;
    
        super.doTypeSpecificInitialization();
    }
}
