package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.TimeOfDay;
import de.helicopter_vs_aliens.model.enemy.EnemyModelType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.missile.EnemyMissileType;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;


public class Protector extends FinalBossServant
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.model = EnemyModelType.BARRIER;
    
        this.bounds.setRect(boss.getX() + 200,
            GROUND_Y,
            PROTECTOR_WIDTH,
            this.bounds.getHeight());
    
        helicopter.numberOfEnemiesSeen--;
        this.hitPoints = Integer.MAX_VALUE;
        this.isClockwiseBarrier = Calculations.tossUp();
        this.primaryColor = Colorations.bleachedViolet;
        this.targetSpeedLevel.setLocation(ZERO_SPEED);
    
        this.deactivationProb = 0.04f;
        this.burrowTimer = READY;
        this.shootingRate = 25;
        this.shotsPerCycle = 5;
        this.shootingCycleLength = this.shootPause
            + this.shootingRate
            * this.shotsPerCycle;
        this.shotSpeed = 10;
        this.shotType = EnemyMissileType.BUSTER;
        this.isStunable = false;
        this.secondaryColor = Colorations.dimColor(this.primaryColor, 0.75f);
        if(Events.timeOfDay == TimeOfDay.NIGHT)
        {
            this.primaryColor = Colorations.dimColor(this.primaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
            this.secondaryColor = Colorations.dimColor(this.secondaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
        }
        
        super.create(helicopter);
    }
}
