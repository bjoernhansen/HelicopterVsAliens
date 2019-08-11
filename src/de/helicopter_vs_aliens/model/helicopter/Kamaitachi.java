package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.util.ArrayList;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;


public final class Kamaitachi extends Helicopter
{
    public static final int RAPIDFIRE_AMOUNT = 2;
    
    @Override
    public HelicopterTypes getType()
    {
        return KAMAITACHI;
    }

    @Override
    public int getCurrentMissileType(boolean stunningMissile)
    {
        if(this.plasmaActivationTimer > 0){return PLASMA;}
        return STANDARD;
    }

    @Override
    public boolean hasFifthSpecial()
    {
        return this.rapidfire == RAPIDFIRE_AMOUNT;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.rapidfire = RAPIDFIRE_AMOUNT;
    }

    @Override
    public void updateUnlockedHelicopters()
    {
        if(!Events.reachedLevelTwenty[OROCHI.ordinal()])
        {
            Menu.unlock(PEGASUS);
        }
    }

    @Override
    public void useEnergyAbility(ArrayList<LinkedList<PowerUp>> powerUp, ArrayList<LinkedList<Explosion>> explosion)
    {
        this.activatePlasma();
    }

    private void activatePlasma()
    {
        Audio.play(Audio.plasmaOn);
        this.energy -= this.hasUnlimitedEnergy() ? 0 : this.spellCosts;
        this.plasmaActivationTimer = POWERUP_DURATION;
    }

    @Override
    void updateTimer()
    {
        super.updateTimer();
        if(this.plasmaActivationTimer > 0)
        {
            this.plasmaActivationTimer--;
            if(this.plasmaActivationTimer == 30){
                Audio.play(Audio.plasmaOff);}
        }
        this.evaluateBonusKills();
    }

    @Override
    public int calculateSumOfFireRateBooster(boolean poweredUp)
    {
        return super.calculateSumOfFireRateBooster(poweredUp) + this.rapidfire;
    }
}