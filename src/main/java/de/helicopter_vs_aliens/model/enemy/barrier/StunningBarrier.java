package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.audio.Audio;

import java.util.Map;
import java.util.Queue;

public final class StunningBarrier extends Barrier
{
    private static final int
        STATIC_CHARGE_TIME = 110;
    
    public static final float
        ENERGY_CONSUMPTION_FACTOR = 2.5f;
    
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
            startStaticDischarge(gameRessourceProvider);
        }
    }
    
    public boolean isStaticallyCharged()
    {
        return staticChargeTimer == READY && snoozeTimer <= SNOOZE_TIME;
    }
    
    private void startStaticDischarge(GameRessourceProvider gameRessourceProvider)
    {
        staticChargeTimer = STATIC_CHARGE_TIME;
        getHelicopter().receiveStaticCharge(ENERGY_CONSUMPTION_FACTOR);
        Audio.play(Audio.emp);
        Explosion.start(gameRessourceProvider,
                        getHelicopter(),
                        (int)getCenterX(),
                        (int)getCenterY(),
                        ExplosionType.STUNNING,
                        false,
                        this);
    }
    
    @Override
    protected void updateTimer()
    {
        super.updateTimer();
        if(	staticChargeTimer > 0) {staticChargeTimer--;}
    }
}
