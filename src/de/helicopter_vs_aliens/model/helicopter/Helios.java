package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.BossLevel;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.window.Window;
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
    private static final int
        COMPARISON_RECORD_TIME = 60;	// angenommene Bestzeit für Besiegen von Boss 5
    
    private static final float
        HELIOS_MAX_MONEY_DIVISOR = 110250,        // Summe der ersten 49 natürlichen Zahlen (0.5 * 49 * 50) * NIGHT_BONUS_FACTOR
        END_OF_POWERUP_GENERATION_PROBABILITY = 0.7f;
    
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
        return
            HelicopterType.PHOENIX.hasDefeatedFinalBoss()
            ? Phoenix.GOLIATH_COSTS
            : STANDARD_GOLIATH_COSTS;
    }

    @Override
    public int getPiercingWarheadsCosts()
    {
        return  ROCH.hasDefeatedFinalBoss()
                ? CHEAP_SPECIAL_COSTS
                : STANDARD_GOLIATH_COSTS;
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

    private void activatePowerUpGenerator(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUps)
    {
        this.powerUpGeneratorTimer = (int)(0.4f * POWER_UP_DURATION);
        this.consumeSpellCosts();
        Calculations.randomize();
        for(int i = 0; i < 3; i++) // TODO 3 is magic number
        {
            // TODO Implementation verbessern
            if(Calculations.getRandomOrderValue(i) == REPARATION.ordinal())
            {
                if(i == 0)
                {
                    Audio.play(Audio.powerAnnouncer[REPARATION.ordinal()]);
                }
                this.useReparationPowerUp();
            }
            else
            {
                PowerUpType powerUpType = PowerUpType.getValues().get(Calculations.getRandomOrderValue(i));
                if(i == 0)
                {
                    Audio.play(Audio.powerAnnouncer[powerUpType.ordinal()]);
                }
                powerUpController.restartPowerUpTimer(powerUpType);
                powerUpController.activatePowerUp(powerUps, powerUpType);
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
        // TODO analysieren, ob man nicht direkt die richtige Zeit für den Effekt wählen kann
        this.gainTripleDamagePermanently();
    }
    
    @Override
    public void stopMenuEffect()
    {
        this.turnOfTripleDamage();
    }
    
    private void turnOfTripleDamage()
    {
        powerUpController.turnOfTripeDamagePowerUp();
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
        Window.moneyDisplayTimer = Events.START;
    }

    @Override
    public boolean isUnacceptablyBoostedForBossLevel()
    {
        return false;
    }
    
    public static int getMaxMoney()
    {
        return HelicopterType.getNormalModeHelicopters()
                             .stream()
                             .map(Helios::getHighestRecordMoney)
                             .reduce(0, Integer::sum);
    }
    
    private static int getHighestRecordMoney(HelicopterType helicopterType)
    {
        return BossLevel.getValues()
                        .stream()
                        .filter(helicopterType::hasPassed)
                        .map(bossLevel -> recordEntryMoney(helicopterType, bossLevel))
                        .reduce(Integer::max)
                        .orElse(0);
    }
    
    private static int recordEntryMoney(HelicopterType helicopterType, BossLevel bossLevel)
    {
        long recordTime = helicopterType.getRecordTime(bossLevel);
        return (int) ((Events.MAX_MONEY * COMPARISON_RECORD_TIME * bossLevel.getBossNr())
                        / (37.5f * recordTime * Calculations.square(6 - bossLevel.getBossNr())));
    }
}