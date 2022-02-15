package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.enemy.Enemy.SPEED_KILL_BONUS_TIME;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.ORDINARY;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.PLASMA;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ENERGY_ABILITY;


public final class Kamaitachi extends Helicopter
{
    private static final int
        RAPIDFIRE_AMOUNT = 2;
    
    private static final float[]
        // Faktor, um den sich die Schadenswirkung der Raketen erhöht, wenn diese Plasmaraketen sind
        PLASMA_DMG_FACTOR = {3.26f, 3.5f, 3.76f, 4.05f, 4.35f, 4.68f, 5.03f, 5.41f, 5.81f, 6.25f};
    
    private int
        plasmaActivationTimer; // Timer zur Überwachung der Zeit [frames], in der die Plasma-Raketen aktiviert sind
    
    private boolean
        hasRapidFire;
    
    @Override
    public HelicopterType getType()
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
    public void useEnergyAbility(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
    {
        this.activatePlasma();
    }

    private void activatePlasma()
    {
        Audio.play(Audio.plasmaOn);
        this.consumeSpellCosts();
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
    
    public void evaluateBonusKills()
    {
        if(this.bonusKillsTimer > 0)
        {
            this.bonusKillsTimer--;
            if(this.bonusKillsTimer == 0)
            {
                if(this.bonusKills > 1)
                {
                    Events.extraReward(
                        this.bonusKills,
                        this.bonusKillsMoney,
                        0.5f, 0.75f, 3.5f); // 0.25f, 0.5f, 3.0f);
                }
                this.bonusKillsMoney = 0;
                this.bonusKills = 0;
            }
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
                ? Colorations.reversedRandomGreen()
                : Colorations.variableGreen;
    }
    
    @Override
    GradientPaint getGradientCannonHoleColor()
    {
        return this.plasmaActivationTimer == 0
                ? this.gradientHull
                : Colorations.cannonHoleGreen;
    }
    
    @Override
    public float getBaseDamage()
    {
        return  super.getBaseDamage()
                * ((this.plasmaActivationTimer == 0)
                    ? 1
                    : this.plasmaDamageFactor(this.getUpgradeLevelOf(ENERGY_ABILITY)));
    }
    
    private float plasmaDamageFactor(int n)
    {
        if(n > 0 && n <= PLASMA_DMG_FACTOR.length)
        {
            return PLASMA_DMG_FACTOR[n-1];
        }
        return 0;
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