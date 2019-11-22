package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.Coloration;
import de.helicopter_vs_aliens.util.Calculation;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.enemy.EnemyType.KABOOM;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PEGASUS;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.FIREPOWER;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.INVINCIBLE;


public final class Phoenix extends Helicopter
{
    public static final int
        TELEPORT_KILL_TIME = 15,		// in dieser Zeit [frames] nach einer Teleportation vernichtete Gegner werden für den Extra-Bonus gewertet
        NICE_CATCH_TIME = 22,			// nur wenn die Zeit [frames] zwischen Teleportation und Gegner-Abschuss kleiner ist, gibt es den "NiceCath-Bonus"
        TELEPORT_INVULNERABILITY_TIME = 45,
        GOLIATH_COSTS = 6000;

    private static final float
        ENHANCED_RADIATION_PROB	= 0.25f;
    
    private int
        enhancedRadiationTimer;
    
    private long
            pastTeleportTime;			// Zeitpunkt der letzten Nutzung des Teleporters
    
    private boolean
        hasShortrangeRadiation;     	// = true: Helikopter verfügt über Nahkampfbestrahlng
    
    
    @Override
    public HelicopterType getType()
    {
        return PHOENIX;
    }

    @Override
    void updateTimer()
    {
        super.updateTimer();
        if(this.enhancedRadiationTimer > 0)	{this.enhancedRadiationTimer--;}
        this.evaluateBonusKills();
    }
    
    @Override
    void resetFifthSpecial()
    {
        this.hasShortrangeRadiation = false;
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
    public void tryToUseEnergyAbility(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
    {
        if(this.isEnergyAbilityActivatable())
        {
            useEnergyAbility(powerUp, explosion);
        }
    }

    @Override
    public void useEnergyAbility(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
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
        super.beAffectedByCollisionWith(enemy, controller, playCollisionSound);
        if(this.hasShortrangeRadiation)
        {
            enemy.reactToRadiation(controller, this);
        }
    }
    
    @Override
    void startRecentDamageTimer()
    {
        if(this.enhancedRadiationTimer == 0)
        {
            super.startRecentDamageTimer();
        }
    }

    private boolean enhancedRadiationApproved(Enemy enemy)
    {
        return this.hasShortrangeRadiation
                && enemy.collisionDamageTimer == 0
                && !(enemy.type == KABOOM)
                && this.enhancedRadiationTimer == 0
                && Calculation.tossUp(ENHANCED_RADIATION_PROB);
    }
    
    @Override
    public float getProtectionFactor()
    {
        return this.enhancedRadiationTimer == 0
            ? super.getProtectionFactor()
            : 0.0f;
    }
    
    @Override
    public boolean isFifthSpecialOnMaximumStrength()
    {
        return this.hasMaximumUpgradeLevelFor(FIREPOWER);
    }
    
    @Override
    public void initMenuEffect(int i)
    {
        super.initMenuEffect(i);
        this.powerUpTimer[INVINCIBLE.ordinal()] = Integer.MAX_VALUE;
    }
    
    @Override
    public void stoptMenuEffect()
    {
        this.powerUpTimer[INVINCIBLE.ordinal()] = 0;
    }
    
    @Override
    void paintComponents(Graphics2D g2d, int left, int top)
    {
        // Nahkampfbestrahlung
        if (this.hasShortrangeRadiation)
        {
            g2d.setColor(this.enhancedRadiationTimer == 0
                ? Coloration.radiation[Events.timeOfDay.ordinal()]
                : Coloration.enhancedRadiation[Events.timeOfDay.ordinal()]);
            g2d.fillOval(left + (this.hasLeftMovingAppearance() ? -9 : 35), top + 19, 96, 54);
        }
        super.paintComponents(g2d, left, top);
    }
    
    @Override
    public boolean isTakingKaboomDamageFrom(Enemy enemy)
    {
        return super.isTakingKaboomDamageFrom(enemy) && !this.hasShortrangeRadiation;
    }
    
    @Override
    AudioClip getCollisionAudio()
    {
        return this.enhancedRadiationTimer == 0
            ? Audio.explosion1
            : Audio.explosion2;
    }
    
    @Override
    public void rightMouseButtonReleaseAction(MouseEvent mouseEvent)
    {
        this.teleportTo(mouseEvent.getX()- Main.displayShift.width,
                        mouseEvent.getY()- Main.displayShift.height);
    }
    
    public void teleportTo(int x, int y)
    {
        this.isSearchingForTeleportDestination = false;
        this.destination.setLocation(x, y);
        
        if(	(this.currentEnergy >= this.spellCosts || this.hasUnlimitedEnergy())
            && !this.isDamaged
            && !Menu.isMenuVisible
            && !(this.bounds.getMaxY() + NO_COLLISION_HEIGHT >= GROUND_Y
            && y >= GROUND_Y)
            && !(	   x > this.bounds.getX() + 33
            && x < this.bounds.getX() + 133
            && y > this.bounds.getY() + 6
            && y < this.bounds.getY() + 106))
        {
            Audio.play(Audio.teleport1);
            this.currentEnergy -= this.hasUnlimitedEnergy() ? 0 : this.spellCosts;
            this.pastTeleportTime = System.currentTimeMillis();
            
            this.nextLocation.setLocation(x, y);
            this.correctAndSetCoordinates();
            
            if(!this.isActive || !this.isRotorSystemActive){this.setActivationState(true);}
            if(this.tractor != null){this.stopTractor();}
            this.powerUpTimer[INVINCIBLE.ordinal()] = Math.max(this.powerUpTimer[INVINCIBLE.ordinal()], TELEPORT_INVULNERABILITY_TIME);
            this.bonusKills = 0;
            this.enhancedRadiationTimer = TELEPORT_INVULNERABILITY_TIME;
            this.bonusKillsTimer = NICE_CATCH_TIME;
            this.bonusKillsMoney = 0;
        }
    }
    
    @Override
    public boolean canObtainCollisionReward()
    {
        return this.hasShortrangeRadiation;
    }
    
    @Override
    void startRecentDamageEffect(Enemy enemy)
    {
        if(this.enhancedRadiationApproved(enemy))
        {
            this.enhancedRadiationTimer = NO_COLLISION_DAMAGE_TIME;
        }
        else super.startRecentDamageEffect(enemy);
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        this.enhancedRadiationTimer = 0;
        this.isSearchingForTeleportDestination = false;
    }
    
    @Override
    public boolean deservesMantisReward(long missileLaunchingTime)
    {
        return this.bonusKillsTimer > 0
                && missileLaunchingTime > this.pastTeleportTime;
    }

    @Override
    public void typeSpecificRewards(Enemy enemy, Missile missile, boolean beamKill)
    {
        if(beamKill)
        {
            this.bonusKills++;
            this.bonusKillsMoney += Events.lastBonus;
        }
    }
}