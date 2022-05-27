package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.util.Calculations;

public class Carrier extends BasicEnemy
{
    public static final float
        CARRIER_TURN_PROBABILITY_FACTOR = 0.4f;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canEarlyTurn = true;
        this.canTurn = true;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    public void die(GameRessourceProvider gameRessourceProvider, Missile missile, boolean beamKill)
    {
        super.die(gameRessourceProvider, missile, beamKill);
        EnemyController.carrierDestroyedJustNow = this;
    }
    
    @Override
    protected float getTurnProbabilityFactor()
    {
        return CARRIER_TURN_PROBABILITY_FACTOR * super.getTurnProbabilityFactor();
    }
    
    public int calculateServantCount()
    {
        return isMiniBoss()
                ? 5 + Calculations.random(3)
                : 2 + Calculations.random(2);
    }
}
