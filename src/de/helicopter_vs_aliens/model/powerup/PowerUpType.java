package de.helicopter_vs_aliens.model.powerup;

public enum PowerUpType
{
    TRIPLE_DAMAGE,
    INVINCIBLE,
    UNLIMITRED_ENERGY,
    BOOSTED_FIRE_RATE,
    REPARATION,
    BONUS_INCOME;
    
    private static final PowerUpType[]
            defensiveCopyOfValues = values();
    
    public static PowerUpType[] getValues()
    {
        return defensiveCopyOfValues;
    }
}