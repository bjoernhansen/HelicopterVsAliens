package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.explosion.ExplosionController;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;

public final class StunningBarrier extends Barrier
{
    private static final int
        STATIC_CHARGE_TIME = 110;
    
    private static final float
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
    public void performLocationAdaptionAction()
    {
        if (isStaticallyCharged())
        {
            startStaticDischarge();
        }
    }
    
    private boolean isStaticallyCharged()
    {
        return staticChargeTimer == READY && snoozeTimer <= SNOOZE_TIME;
    }
    
    private void startStaticDischarge()
    {
        staticChargeTimer = STATIC_CHARGE_TIME;
        getHelicopter().receiveStaticCharge(ENERGY_CONSUMPTION_FACTOR);
        Audio.play(Audio.emp);
        ExplosionController explosionController = getGameRessourceProvider().getExplosionController();
        explosionController.start(
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
