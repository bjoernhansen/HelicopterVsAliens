package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.explosion.Explosion;

import java.util.Map;
import java.util.Queue;

import static de.helicopter_vs_aliens.model.explosion.ExplosionType.STUNNING;

public class StunningBarrier extends Barrier
{
    private static final int
        STATIC_CHARGE_TIME = 110;
    
    private int
        staticChargeTimer;
    
    
    @Override
    public void reset()
    {
        super.reset();
        staticChargeTimer = DISABLED;
    }
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        rotorColor = 2;
        staticChargeTimer = READY;
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void performVerticalTurn()
    {
        super.performVerticalTurn();
        if(isFlyingUp()){Audio.play(Audio.landing);}
    }
    
    @Override
    public void performLocationAdaptionAction(GameRessourceProvider gameRessourceProvider)
    {
        super.performLocationAdaptionAction(gameRessourceProvider);
        if (isStaticallyCharged())
        {
            startStaticDischarge(gameRessourceProvider.getExplosions());
        }
    }
    
    public boolean isStaticallyCharged()
    {
        return staticChargeTimer == READY && snoozeTimer <= SNOOZE_TIME;
    }
    
    private void startStaticDischarge(Map<CollectionSubgroupType, Queue<Explosion>> explosions)
    {
        staticChargeTimer = STATIC_CHARGE_TIME;
        getHelicopter().receiveStaticCharge(STUNNING_BARRIER_ENERGY_CONSUMPTION_FACTOR);
        Audio.play(Audio.emp);
        Explosion.start(explosions, getHelicopter(), (int)getCenterX(), (int)getCenterY(), STUNNING, false, this);
    }
    
    @Override
    protected void updateTimer()
    {
        super.updateTimer();
        if(	staticChargeTimer > 0) {staticChargeTimer--;}
    }
}
