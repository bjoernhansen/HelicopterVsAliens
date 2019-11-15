package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.Coloration;
import de.helicopter_vs_aliens.util.Calculation;

import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.enemy.Enemy.SPEED_KILL_BONUS_TIME;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.PLASMA;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.ORDINARY;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.ENERGY_ABILITY;


public final class Kamaitachi extends Helicopter
{
    private static final int
        RAPIDFIRE_AMOUNT = 2;
    
    private int
        plasmaActivationTimer; // Timer zur Überwachung der Zeit [frames], in der die Plasma-Raketen aktiviert sind
    
    private boolean
        hasRapidFire;
    
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
    
    @Override
    Color getInputColorCannon()
    {
        if(this.plasmaActivationTimer > POWERUP_DURATION/4)
        {
            return Color.green;
        }
        else if(this.plasmaActivationTimer == 0)
        {
            return super.getInputColorCannon();
        }
        return this.isInvincible()
                ? Coloration.reversedRandomGreen()
                : Coloration.variableGreen;
    }
    
    @Override
    GradientPaint getGradientCannonHoleColor()
    {
        return this.plasmaActivationTimer == 0
                ? this.gradientHull
                : Coloration.cannonHoleGreen;
    }
    
    @Override
    public float getBaseDamage()
    {
        return  super.getBaseDamage()
                * ((this.plasmaActivationTimer == 0)
                    ? 1
                    : Calculation.plasmaDamageFactor(this.levelOfUpgrade[ENERGY_ABILITY.ordinal()]));
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        this.plasmaActivationTimer = 0;
    }

    @Override
    public void typeSpecificRewards(Enemy enemy, Missile missile, boolean beamKill)
    {
        this.bonusKillsTimer += SPEED_KILL_BONUS_TIME;
        this.bonusKills++;
        this.bonusKillsMoney += Events.lastBonus;
    }
}