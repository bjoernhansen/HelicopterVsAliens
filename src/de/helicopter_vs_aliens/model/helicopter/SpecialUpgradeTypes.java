package de.helicopter_vs_aliens.model.helicopter;

public enum SpecialUpgradeTypes {
    SPOTLIGHT,
    GOLIATH_PLATING,
    PIERCING_WARHEADS,
    EXTRA_CANNONS,
    FIFTH_SPECIAL;


    public static int size()
    {
        return values().length;
    }
}