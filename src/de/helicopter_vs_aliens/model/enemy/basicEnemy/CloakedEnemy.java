package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.util.Colorations;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class CloakedEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = Colorations.cloaked;
        this.targetSpeedLevel.setLocation( 0.5 + Math.random(),
            1 + 0.5*Math.random());
        this.canLearnKamikaze = true;
        this.canInstantTurn = true;
        this.cloakingTimer = CLOAKING_TIME + CLOAKED_TIME;
        this.uncloakingSpeed = 2;
        this.canEarlyTurn = true;
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
}
