package de.helicopter_vs_aliens.model.enemy.devices;

import static de.helicopter_vs_aliens.model.enemy.Enemy.DISABLED;
import static de.helicopter_vs_aliens.model.enemy.Enemy.READY;

public class CloakingDevice
{
    public static final int
        BOOT_AND_FADE_TIME = 135,    // Zeit, die beim Vorgang der Tarnung und Enttarnung vergeht
        CLOAKED_TIME = 135;         // Zeit, die ein Gegner getarnt bleibt
    
    private static final int
        ACTIVE = 1;
    
    private int
        cloakingTimer = DISABLED;          // reguliert die Tarnung eines Gegners; = DISABLED: Gegner kann sich grundsätzlich nicht tarnen
    
    private int
        cloakingSpeed = 1;
    
    
    public void reset()
    {
        disable();
        setCloakingSpeed(1);
    }
    
    public void setToEndOfCloakedTime()
    {
        cloakingTimer = BOOT_AND_FADE_TIME + CLOAKED_TIME;
    }
    
    public boolean isActive()
    {
        return cloakingTimer >= ACTIVE;
    }
    
    public int getAlpha()
    {
        if(isNotCompletelyCloakedYet())
        {
            return 255 - 255*cloakingTimer / BOOT_AND_FADE_TIME;
        }
        else if(isUncloakingInProgress())
        {
            return 255*(cloakingTimer - CLOAKED_TIME - BOOT_AND_FADE_TIME)/ BOOT_AND_FADE_TIME;
        }
        else
        {
            return 255;
        }
    }
    
    public void run()
    {
        cloakingTimer += cloakingSpeed;
    }
    
    public boolean isShutDownCompleted()
    {
        return cloakingTimer >= CLOAKED_TIME + 2 * BOOT_AND_FADE_TIME;
    }
    
    public boolean hasJustStartedFadingAway()
    {
        return cloakingTimer == BOOT_AND_FADE_TIME + CLOAKED_TIME + cloakingSpeed;
    }
    
    public boolean isUncloakingInProgress()
    {
        return cloakingTimer > BOOT_AND_FADE_TIME + CLOAKED_TIME && cloakingTimer <= CLOAKED_TIME + 2 * BOOT_AND_FADE_TIME;
    }
    
    private boolean isNotCompletelyCloakedYet()
    {
        return cloakingTimer <= BOOT_AND_FADE_TIME;
    }
    
    public void activate()
    {
        cloakingTimer = ACTIVE;
    }
    
    public void setToStartOfCloakedTime()
    {
        cloakingTimer = BOOT_AND_FADE_TIME + cloakingSpeed;
    }
    
    public boolean isReadyToBeUsed()
    {
        return cloakingTimer == READY;
    }
    
    public boolean isEnabled()
    {
        return cloakingTimer != DISABLED;
    }
    
    public void setReadyForUse()
    {
        cloakingTimer = READY;
    }
    
    public void disable()
    {
        cloakingTimer = DISABLED;
    }
    
    public boolean isCompletelyCloaking()
    {
        return 	   cloakingTimer > BOOT_AND_FADE_TIME
                && cloakingTimer <= BOOT_AND_FADE_TIME + CLOAKED_TIME;
    }
    
    public void setCloakingSpeed(int cloakingSpeed)
    {
        this.cloakingSpeed = cloakingSpeed;
    }
}
