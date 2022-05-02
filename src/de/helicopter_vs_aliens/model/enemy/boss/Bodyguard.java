package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

public class Bodyguard extends FinalBossServant
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.bounds.setRect(boss.getX(),
            boss.getY(),
            225,
            this.bounds.getHeight());
        this.primaryColor = Colorations.cloaked;
        this.hitpoints = 7500;
        this.targetSpeedLevel.setLocation(1, 2);
    
        this.cloakingTimer = 0;
        this.canInstantTurn = true;
        
        super.create(helicopter);
    }
}
