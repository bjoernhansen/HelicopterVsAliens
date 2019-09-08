package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.PLASMA;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.ORDINARY;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.INVINCIBLE;


public final class Kamaitachi extends Helicopter
{
    private static final int RAPIDFIRE_AMOUNT = 2;
    
    private boolean hasRapidFire;
    
    @Override
    public HelicopterTypes getType()
    {
        return KAMAITACHI;
    }

    @Override
    public ExplosionTypes getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
    {
        if(this.plasmaActivationTimer > 0){return PLASMA;}
        return ORDINARY;
    }

    @Override
    public boolean hasFifthSpecial()
    {
        return this.hasRapidFire; // this.rapidfire == RAPIDFIRE_AMOUNT;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.hasRapidFire = true;
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
    public void useEnergyAbility(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
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
        this.updatePlasmaActivationTimer();
        this.evaluateBonusKills();
    }
    
    private void updatePlasmaActivationTimer()
    {
        if(this.plasmaActivationTimer > 0)
        {
            this.plasmaActivationTimer--;
            if(this.plasmaActivationTimer == 30){
                Audio.play(Audio.plasmaOff);}
        }
    }
    
    @Override
    void resetFifthSpecial()
    {
        this.hasRapidFire = false;
    }
    
    @Override
    public int calculateSumOfFireRateBooster(boolean poweredUp)
    {
        return super.calculateSumOfFireRateBooster(poweredUp)
                + (this.hasRapidFire ? RAPIDFIRE_AMOUNT : 0);
    }
    
    @Override
    public void crash()
    {
        this.plasmaActivationTimer = 0;
        super.crash();
    }
    
    @Override
    public void initMenuEffect(int i)
    {
        super.initMenuEffect(i);
        this.plasmaActivationTimer = Integer.MAX_VALUE;
    }
    
    @Override
    public void updateMenuEffect()
    {
        super.updateMenuEffect();
        if(Menu.effectTimer[this.getType().ordinal()] == 65)
        {
            this.plasmaActivationTimer = POWERUP_DURATION/4;
        }
        else if(Menu.effectTimer[this.getType().ordinal()] == 30)
        {
            Audio.play(Audio.plasmaOff);
        }
    }
    
    @Override
    public void stoptMenuEffect()
    {
        this.plasmaActivationTimer = 0;
    }
}