package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.util.Calculations;

import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.Events.lastBonus;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.REPARATION;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.TRIPLE_DAMAGE;


public final class Helios extends Helicopter
{
    public static final float
        HELIOS_MAX_MONEY_DIVISOR = 110250;        // Summe der ersten 49 natürlichen Zahlen (0.5 * 49 * 50) * NIGHT_BONUS_FACTOR
    public static final float END_OF_POWERUP_GENERATION_PROBABILITY = 0.7f;
    
    private int
        powerUpGeneratorTimer;
    
    private boolean
        hasPowerUpImmobilizer;  // = true: Helikopter verfügt über einen Interphasen-Generator
    
    
    @Override
    public HelicopterType getType()
    {
        return HelicopterType.HELIOS;
    }

    @Override
    void updateTimer()
    {
        if(this.powerUpGeneratorTimer > 0){this.powerUpGeneratorTimer--;}
        super.updateTimer();
    }
    
    @Override
    void resetFifthSpecial()
    {
        this.hasPowerUpImmobilizer = false;
    }
    
    @Override
    public int getGoliathCosts()
    {
        return Events.recordTime[PHOENIX.ordinal()][4] != 0 ? Phoenix.GOLIATH_COSTS : STANDARD_GOLIATH_COSTS;
    }

    @Override
    public int getPiercingWarheadsCosts()
    {
        return Events.recordTime[ROCH.ordinal()][4] != 0 ? CHEAP_SPECIAL_COSTS : STANDARD_GOLIATH_COSTS;
    }

    @Override
    public boolean hasFifthSpecial()
    {
        return this.hasPowerUpImmobilizer;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.hasPowerUpImmobilizer = true;
    }

    @Override
    public void updateUnlockedHelicopters() {}

    @Override
    public boolean isEnergyAbilityActivatable()
    {
        return this.powerUpGeneratorTimer == 0 && this.hasEnoughEnergyForAbility();
    }

    @Override
    public void useEnergyAbility(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
    {
        this.activatePowerUpGenerator(powerUp);
    }

    private void activatePowerUpGenerator(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp)
    {
        this.powerUpGeneratorTimer = (int)(0.4f * POWERUP_DURATION);
        this.consumeSpellCosts();
        Calculations.randomize();
        for(int i = 0; i < 3; i++)
        {
            if(Calculations.getRandomOrderValue(i) == REPARATION.ordinal())
            {
                if(i == 0){
                    Audio.play(Audio.powerAnnouncer[REPARATION.ordinal()]);}
                this.useReparationPowerUp();
            }
            else
            {
                this.getPowerUp( powerUp, PowerUpType.getValues()[Calculations.getRandomOrderValue(i)],
                        false, i == 0);
            }
            if(Calculations.tossUp(END_OF_POWERUP_GENERATION_PROBABILITY)){break;}
        }
    }

    @Override
    public boolean canImmobilizePowerUp()
    {
        return this.hasPowerUpImmobilizer;
    }
    
    @Override
    public void initMenuEffect(int i)
    {
        super.initMenuEffect(i);
        this.powerUpTimer[TRIPLE_DAMAGE.ordinal()] = Integer.MAX_VALUE;
    }
    
    @Override
    public void stoptMenuEffect()
    {
        this.powerUpTimer[TRIPLE_DAMAGE.ordinal()] = 0;
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        this.powerUpGeneratorTimer = 0;
    }

    @Override
    public void receiveRewardFor(Enemy enemy, Missile missile, boolean beamKill){}
    
    @Override
    public void levelUpEffect(int previousLevel)
    {
        if(Events.level > Events.maxLevel){getHeliosIncome(previousLevel, this);}
    }
    
    private static void getHeliosIncome(int previousLevel, Helicopter helicopter)
    {
        float bonusSum = 0;
        for(int i = Math.max(previousLevel, Events.maxLevel); i < Events.level; i++)
        {
            bonusSum += i*Events.heliosMaxMoney/HELIOS_MAX_MONEY_DIVISOR;
        }
        lastBonus = (int) (bonusSum * helicopter.getBonusFactor());
        Events.money += lastBonus;
        Events.overallEarnings += lastBonus;
        Menu.moneyDisplayTimer = Events.START;
    }

    @Override
    public boolean hasPowerUpsDisallowedAtBossLevel()
    {
        return false;
    }
}