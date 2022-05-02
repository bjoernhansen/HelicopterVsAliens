package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public abstract class ShieldMaker extends FinalBossServant
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.bounds.setRect(boss.getX(),
                            boss.getY(),
                            this.type == EnemyType.SMALL_SHIELD_MAKER ? 125 : 145,
                            this.bounds.getHeight());
        this.direction.x = Calculations.randomDirection();
        this.shieldMakerTimer = READY;
        this.setShieldingPosition();
        
        super.create(helicopter);
    }}
