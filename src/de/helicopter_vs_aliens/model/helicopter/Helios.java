package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpTypes;
import de.helicopter_vs_aliens.util.MyMath;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.REPARATION;


public final class Helios extends Helicopter
{
    @Override
    public HelicopterTypes getType()
    {
        return HelicopterTypes.HELIOS;
    }

    @Override
    void updateTimer()
    {
        if(this.powerUpGeneratorTimer > 0){this.powerUpGeneratorTimer--;}
        super.updateTimer();
    }

    @Override
    int getUpgradeCosts(int i)
    {
        return heliosCosts(i);
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
    public void useEnergyAbility(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
    {
        this.activatePowerUpGenerator(powerUp);
    }

    private void activatePowerUpGenerator(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp)
    {
        this.powerUpGeneratorTimer = (int)(0.4f * POWERUP_DURATION);
        this.energy -= this.hasUnlimitedEnergy() ? 0 : this.spellCosts;
        MyMath.randomize();
        for(int i = 0; i < 3; i++)
        {
            if(MyMath.getRandomOrderValue(i) == REPARATION.ordinal())
            {
                if(i == 0){
                    Audio.play(Audio.powerAnnouncer[REPARATION.ordinal()]);}
                this.useReparationPowerUp();
            }
            else
            {
                this.getPowerUp( powerUp, PowerUpTypes.values()[MyMath.getRandomOrderValue(i)],
                        false, i == 0);
            }
            if(MyMath.tossUp(0.7f)){break;}
        }
    }

    @Override
    public void resetState(boolean resetStartPos)
    {
        super.resetState(resetStartPos);
        this.powerUpGeneratorTimer = 0;
    }
}