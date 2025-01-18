package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.BossLevel;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.util.Calculations;

import java.util.Queue;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.ROCH;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.REPARATION;


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
    
    
    Helios(GameRessourceProvider gameRessourceProvider)
    {
        super(gameRessourceProvider);
    }
    
    @Override
    public HelicopterType getType()
    {
        return HelicopterType.HELIOS;
    }

    @Override
    void updateTimer()
    {
        if(powerUpGeneratorTimer > 0){
            powerUpGeneratorTimer--;}
        super.updateTimer();
    }
    
    @Override
    void resetFifthSpecial()
    {
        hasPowerUpImmobilizer = false;
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
        return hasPowerUpImmobilizer;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        hasPowerUpImmobilizer = true;
    }

    @Override
    public void updateUnlockedHelicopters() {}

    @Override
    public boolean isEnergyAbilityActivatable()
    {
        return powerUpGeneratorTimer == 0 && hasEnoughEnergyForAbility();
    }

    @Override
    public void useEnergyAbility()
    {
        activatePowerUpGenerator();
    }

    private void activatePowerUpGenerator()
    {
        powerUpGeneratorTimer = (int)(0.4f * POWER_UP_DURATION);
        consumeSpellCosts();
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
                useReparationPowerUp();
            }
            else
            {
                PowerUpType powerUpType = PowerUpType.getValues().get(Calculations.getRandomOrderValue(i));
                if(i == 0)
                {
                    Audio.play(Audio.powerAnnouncer[powerUpType.ordinal()]);
                }
                powerUpController.restartPowerUpTimer(powerUpType);
                powerUpController.activatePowerUp(powerUpType);
            }
            if(Calculations.tossUp(END_OF_POWERUP_GENERATION_PROBABILITY)){break;}
        }
    }

    @Override
    public boolean canImmobilizePowerUp()
    {
        return hasPowerUpImmobilizer;
    }
    
    @Override
    public void initMenuEffect(int i)
    {
        super.initMenuEffect(i);
        // TODO analysieren, ob man nicht direkt die richtige Zeit für den Effekt wählen kann
        gainTripleDamagePermanently();
    }
    
    @Override
    public void stopMenuEffect()
    {
        turnOfTripleDamage();
    }
    
    private void turnOfTripleDamage()
    {
        powerUpController.turnOfTripeDamagePowerUp();
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        powerUpGeneratorTimer = 0;
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
        Events.lastBonus = (int) (bonusSum * helicopter.getBonusFactor());
        Events.money += Events.lastBonus;
        Events.overallEarnings += Events.lastBonus;
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