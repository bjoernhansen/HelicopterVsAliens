package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.missile.Missile;

import static de.helicopter_vs_aliens.model.explosion.ExplosionType.ORDINARY;
import static de.helicopter_vs_aliens.model.explosion.ExplosionType.PLASMA;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ENERGY_ABILITY;


public final class Kamaitachi extends Helicopter
{
    private static final int
        SPEED_KILL_BONUS_TIME 	= 15, // Zeit [frames], innerhalb welcher für einen Kamaitachi-Extra-Bonus Gegner besiegt werden müssen, erhöht sich um diesen Wert
        RAPID_FIRE_AMOUNT = 2;
    
    private static final float[]
        // Faktor, um den sich die Schadenswirkung der Raketen erhöht, wenn diese Plasmaraketen sind
        PLASMA_DAMAGE_FACTOR = {3.26f, 3.5f, 3.76f, 4.05f, 4.35f, 4.68f, 5.03f, 5.41f, 5.81f, 6.25f};
    
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
    public ExplosionType getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
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
        this.adjustFireRate(this.hasBoostedFireRate());
    }

    @Override
    public void updateUnlockedHelicopters()
    {
        if(!OROCHI.hasReachedLevel20())
        {
            Window.unlock(PEGASUS);
        }
    }

    @Override
    public void useEnergyAbility(GameRessourceProvider gameRessourceProvider)
    {
        this.activatePlasma();
    }

    private void activatePlasma()
    {
        Audio.play(Audio.plasmaOn);
        this.consumeSpellCosts();
        this.plasmaActivationTimer = POWER_UP_DURATION;
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
                + (this.hasRapidFire ? RAPID_FIRE_AMOUNT : 0);
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
        if(Window.effectTimer[this.getType().ordinal()] == 65)
        {
            this.plasmaActivationTimer = POWER_UP_FADE_TIME;
        }
        else if(Window.effectTimer[this.getType().ordinal()] == 30)
        {
            Audio.play(Audio.plasmaOff);
        }
    }
    
    @Override
    public void stopMenuEffect()
    {
        this.plasmaActivationTimer = 0;
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
        if(n > 0 && n <= PLASMA_DAMAGE_FACTOR.length)
        {
            return PLASMA_DAMAGE_FACTOR[n-1];
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
    
    public int getPlasmaActivationTimer()
    {
        return plasmaActivationTimer;
    }
    
    @Override
    public void partialReset()
    {
        super.partialReset();
        evaluateBonusKills();
    }
}