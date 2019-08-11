package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.MyMath;

import java.util.ArrayList;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.enemy.EnemyTypes.KABOOM;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.PEGASUS;


public final class Phoenix extends Helicopter
{
    public static final int
        TELEPORT_KILL_TIME = 15,		// in dieser Zeit [frames] nach einer Teleportation vernichtete Gegner werden für den Extra-Bonus gewertet
        NICE_CATCH_TIME = 22,			// nur wenn die Zeit [frames] zwischen Teleportation und Gegner-Abschuss kleiner ist, gibt es den "NiceCath-Bonus"
        TELEPORT_INVU_TIME = 45,
        GOLIATH_COSTS = 6000;

    private static final float
        ENHANCED_RADIATION_PROB	= 0.25f;

    @Override
    public HelicopterTypes getType()
    {
        return PHOENIX;
    }

    @Override
    void updateTimer()
    {
        super.updateTimer();
        this.evaluateBonusKills();
    }

    @Override
    public int getGoliathCosts()
    {
        return GOLIATH_COSTS;
    }

    @Override
    public void obtainSomeUpgrades()
    {
        this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
        super.obtainSomeUpgrades();
    }
    
    @Override
    public boolean hasFifthSpecial()
    {
        return this.hasShortrangeRadiation;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.hasShortrangeRadiation = true;
    }

    @Override
    public void updateUnlockedHelicopters()
    {
        if(!Events.reachedLevelTwenty[PEGASUS.ordinal()])
        {
            Menu.unlock(OROCHI);
        }
    }

    @Override
    public void tryToUseEnergyAbility(ArrayList<LinkedList<PowerUp>> powerUp, ArrayList<LinkedList<Explosion>> explosion)
    {
        if(this.isEnergyAbilityActivatable())
        {
            useEnergyAbility(powerUp, explosion);
        }
    }

    @Override
    public void useEnergyAbility(ArrayList<LinkedList<PowerUp>> powerUp, ArrayList<LinkedList<Explosion>> explosion)
    {
        this.prepareTeleportation();
    }

    private void prepareTeleportation()
    {
        this.isSearchingForTeleportDestination = true;
        this.priorTeleportLocation.setLocation(
                this.bounds.getX() + (this.isMovingLeft
                        ? FOCAL_PNT_X_LEFT
                        : FOCAL_PNT_X_RIGHT),
                this.bounds.getY() + FOCAL_PNT_Y_POS);
    }

    @Override
    public void beAffectedByCollisionWith(Enemy enemy,
                                          Controller controller,
                                          boolean playCollisionSound)
    {
        if(this.enhancedRadiationApproved(enemy))
        {
            this.enhancedRadiationTimer
                    = Math.max(	this.enhancedRadiationTimer,
                                NO_COLLISION_DMG_TIME);
        }
        else if(this.enhancedRadiationTimer == 0)
        {
            this.recentDamageTimer = RECENT_DMG_TIME;
        }
        super.beAffectedByCollisionWith(enemy, controller, playCollisionSound);
        if(this.hasShortrangeRadiation)
        {
            enemy.reactToRadiation(controller, this);
        }
    }

    private boolean enhancedRadiationApproved(Enemy enemy)
    {
        return this.hasShortrangeRadiation
                && enemy.collisionDamageTimer == 0
                && !(enemy.type == KABOOM)
                && this.enhancedRadiationTimer == READY
                && MyMath.tossUp(ENHANCED_RADIATION_PROB);
    }

    @Override
    public void resetState(boolean resetStartPos)
    {
        super.resetState(resetStartPos);
        this.enhancedRadiationTimer = 0;
    }
}