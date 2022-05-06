package de.helicopter_vs_aliens.model.helicopter.components;

import de.helicopter_vs_aliens.model.powerup.PowerUpType;

import java.util.EnumMap;
import java.util.Map;

public class PowerUpController
{
    private static final int
        // TODO einstellen auf 60 Frames per Second
        POWER_UP_DURATION = 930,                // Zeit [frames] welche ein eingesammeltes PowerUp aktiv bleibt
        POWER_UP_FADE_TIME = POWER_UP_DURATION / 4;
    
    private final Map<PowerUpType, Integer>
        powerUpTimers = new EnumMap<>(PowerUpType.class); // Zeit [frames] in der das PowerUp (0: bonus dmg; 1: invincible; 2: endless energy; 3: bonus fire rate) noch aktiv ist
}
