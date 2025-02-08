package de.helicopter_vs_aliens.model.helicopter.components;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.Phoenix;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpController;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;

import java.util.EnumMap;
import java.util.Map;


public class PowerUpStateController
{
    private final Map<PowerUpType, Integer>
        powerUpTimers = new EnumMap<>(PowerUpType.class); // Zeit [frames] in der das PowerUp (0: bonus dmg; 1: invincible; 2: endless energy; 3: bonus fire rate) noch aktiv ist
    
    private final GameRessourceProvider
        gameRessourceProvider;
    
    private final Helicopter
        helicopter;
    
    public PowerUpStateController(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
        helicopter = gameRessourceProvider.getHelicopter();
    }
    
    public void turnOfAllBoosters()
    {
        // TODO Methode eventuell unnötig
        PowerUpType.getStatusBarPowerUpTypes()
                   .forEach(this::turnOfPowerUp);
    }
    
    public void restartPowerUpTimer(PowerUpType powerUpType)
    {
        becomeBoostered(powerUpType, Helicopter.POWER_UP_DURATION);
    }
    
    public boolean isPowerUpActive(PowerUpType powerUpType)
    {
        return getRemainingTimeBoosted(powerUpType) > 0;
    }
    
    public void reset()
    {
        PowerUpType.getStatusBarPowerUpTypes()
                   .forEach(this::turnOfPowerUp);
    }
    
    public boolean isAnyPowerUpForbiddenAtBossLevelActive()
    {
        // PowerUps acquired by cheats do not fade
        return PowerUpType.getStatusBarPowerUpTypes()
                          .stream()
                          .anyMatch(this::isPoweredUpWithoutCheats);
    }
    
    public void startDecayOfAllActivePowerUps()
    {
        PowerUpType.getStatusBarPowerUpTypes()
                   .stream()
                   .filter(this::isPoweredUpWithoutCheats)
                   .forEach(this::startPowerUpDecay);
    }
    
    public void evaluatePowerUpActivationStates()
    {
        PowerUpType.getStatusBarPowerUpTypes()
                   .forEach(powerUpType -> {
                       if(isPowerUpActive(powerUpType))
                       {
                           countDownPowerUpTimer(powerUpType);
                           if(!isPowerUpActive(powerUpType) && Window.collectedPowerUps.containsKey(powerUpType))
                           {
                               Audio.play(Audio.powerUpFade2);
                               Window.collectedPowerUps.get(powerUpType)
                                                       .setCollected();
                               Window.collectedPowerUps.remove(powerUpType);
                               if(powerUpType == PowerUpType.BOOSTED_FIRE_RATE)
                               {
                                   helicopter.adjustFireRate(false);
                               }
                           }
                           else if(isBoosterStartingToFadeRightNow(powerUpType))
                           {
                               Audio.play(Audio.powerUpFade1);
                           }
                           else if(isBoosterFading(powerUpType) && Window.collectedPowerUps.containsKey(powerUpType))
                           {
                               int remainingTimeBoosted = getRemainingTimeBoosted(powerUpType);
                               Window.changeCollectedPowerUpColorationForFading(powerUpType, remainingTimeBoosted);
                           }
                       }
                   });
    }
    
    public void switchPowerUpActivationState(PowerUpType powerUpType)
    {
        if(isPowerUpActive(powerUpType))
        {
            Audio.play(Audio.powerUpFade2);
            turnOfPowerUp(powerUpType);
            Window.removeCollectedPowerUp(powerUpType);
            if(powerUpType == PowerUpType.BOOSTED_FIRE_RATE)
            {
                helicopter.adjustFireRate(false);
            }
        }
        else
        {
            Audio.play(Audio.powerAnnouncer[powerUpType.ordinal()]);
            becomeBoosteredPermanently(powerUpType);
            activatePowerUp(powerUpType);
        }
    }
    
    public void activatePowerUp(PowerUpType powerUpType)
    {
        if(Window.collectedPowerUps.containsKey(powerUpType))
        {
            Window.collectedPowerUps.get(powerUpType).setOpaque();
        }
        else
        {
            PowerUp powerUp = PowerUpController.getInstance(gameRessourceProvider, powerUpType);
            powerUp.initialize();
            powerUp.moveToStatusbar();
            if(powerUpType == PowerUpType.BOOSTED_FIRE_RATE)
            {
                helicopter.adjustFireRate(true);
            }
        }
    }
    
    public void turnOfTripeDamagePowerUp()
    {
        turnOfPowerUp(PowerUpType.TRIPLE_DAMAGE);
    }
    
    public void turnOfInvinciblePowerUp()
    {
        turnOfPowerUp(PowerUpType.INVINCIBLE);
    }
    
    public void activateInvinciblePowerUpPermanently()
    {
        becomeBoosteredPermanently(PowerUpType.INVINCIBLE);
    }
    
    public void activateInvinciblePowerUpBriefly()
    {
        becomeBoostered(PowerUpType.INVINCIBLE, Phoenix.TELEPORT_INVULNERABILITY_TIME);
    }
    
    public void activateTripleDamagePowerUpPermanently()
    {
        becomeBoosteredPermanently(PowerUpType.TRIPLE_DAMAGE);
    }

    private void becomeBoostered(PowerUpType powerUpType, int minimumDuration)
    {
        int duration = Math.max(getRemainingTimeBoosted(powerUpType), minimumDuration);
        powerUpTimers.put(powerUpType, duration);
    }
    
    private void becomeBoosteredPermanently(PowerUpType powerUpType)
    {
        powerUpTimers.put(powerUpType, Integer.MAX_VALUE);
    }
    
    private void turnOfPowerUp(PowerUpType powerUpType)
    {
        powerUpTimers.put(powerUpType, 0);
    }
    
    private void startPowerUpDecay(PowerUpType powerUpType)
    {
        int duration = Math.min(Helicopter.POWER_UP_FADE_TIME + 1, getRemainingTimeBoosted(powerUpType));
        powerUpTimers.put(powerUpType, duration);
    }
    
    private boolean isPoweredUpWithoutCheats(PowerUpType powerUpType)
    {
        return isPowerUpActive(powerUpType) && !isPoweredUpWithCheats(powerUpType);
    }
    
    private boolean isPoweredUpWithCheats(PowerUpType powerUpType)
    {
        return getRemainingTimeBoosted(powerUpType) > Integer.MAX_VALUE/2;
    }
    
    private boolean isBoosterStartingToFadeRightNow(PowerUpType powerUpType)
    {
        return powerUpTimers.get(powerUpType) == Helicopter.POWER_UP_FADE_TIME;
    }
    
    private boolean isBoosterFading(PowerUpType powerUpType)
    {
        return getRemainingTimeBoosted(powerUpType) < Helicopter.POWER_UP_FADE_TIME;
    }
    
    private void countDownPowerUpTimer(PowerUpType powerUpType)
    {
        int nextTimerValue = getRemainingTimeBoosted(powerUpType) - 1;
        powerUpTimers.put(powerUpType, nextTimerValue);
    }
    
    private int getRemainingTimeBoosted(PowerUpType powerUpType)
    {
        return powerUpTimers.get(powerUpType);
    }
}
