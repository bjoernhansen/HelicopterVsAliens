package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.Calculations;

import java.applet.AudioClip;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.enemy.EnemyType.KABOOM;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PEGASUS;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.FIREPOWER;


public final class Phoenix extends Helicopter
{
    public static final int
        TELEPORT_KILL_TIME = 15,		// in dieser Zeit [frames] nach einer Teleportation vernichtete Gegner werden für den Extra-Bonus gewertet
        NICE_CATCH_TIME = 22,			// nur wenn die Zeit [frames] zwischen Teleportation und Gegner-Abschuss kleiner ist, gibt es den "NiceCatch-Bonus"
        TELEPORT_INVULNERABILITY_TIME = 45,
        GOLIATH_COSTS = 6000;

    private static final float
        ENHANCED_RADIATION_PROB	= 0.25f;
    
    private int
        enhancedRadiationTimer;
    
    private long
            pastTeleportTime;			// Zeitpunkt der letzten Nutzung des Teleporters
    
    private boolean
        hasShortRangeRadiation;     	// = true: Helikopter verfügt über Nahkampfbestrahlung
    
    
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
    
    private void evaluateBonusKills()
    {
        if(this.bonusKillsTimer > 0)
        {
            this.bonusKillsTimer--;
            if(	this.bonusKillsTimer == NICE_CATCH_TIME - TELEPORT_KILL_TIME
                && this.bonusKills > 1)
            {
                Events.extraReward(
                    this.bonusKills,
                    this.bonusKillsMoney,
                    0.75f, 0.75f, 3.5f);
            }
        }
    }
    
    @Override
    void resetFifthSpecial()
    {
        this.hasShortRangeRadiation = false;
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
        return this.hasShortRangeRadiation;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.hasShortRangeRadiation = true;
    }

    @Override
    public void updateUnlockedHelicopters()
    {
        if(!PEGASUS.hasReachedLevel20())
        {
            Window.unlock(OROCHI);
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
                this.getX() + (this.isMovingLeft
                        ? FOCAL_PNT_X_LEFT
                        : FOCAL_PNT_X_RIGHT),
                this.getY() + FOCAL_PNT_Y_POS);
    }

    @Override
    public void beAffectedByCollisionWith(Enemy enemy,
                                          Controller controller,
                                          boolean playCollisionSound)
    {
        super.beAffectedByCollisionWith(enemy, controller, playCollisionSound);
        if(this.hasShortRangeRadiation)
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
        return this.hasShortRangeRadiation
                && enemy.collisionDamageTimer == 0
                && !(enemy.type == KABOOM)
                && this.enhancedRadiationTimer == 0
                && Calculations.tossUp(ENHANCED_RADIATION_PROB);
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
        this.gainInvincibilityPermanently();
    }
    
    @Override
    public void stopMenuEffect()
    {
        this.turnOfInvincibility();
    }
    
    @Override
    public boolean isTakingKaboomDamageFrom(Enemy enemy)
    {
        return super.isTakingKaboomDamageFrom(enemy) && !this.hasShortRangeRadiation;
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
        this.tryToTeleportTo(mouseEvent.getX()- Main.displayShift.width,
                             mouseEvent.getY()- Main.displayShift.height);
    }
    
    public void tryToTeleportTo(int x, int y)
    {
        this.isSearchingForTeleportDestination = false;
        this.destination.setLocation(x, y);
        
        if(this.canTeleportTo(x, y))
        {
            Audio.play(Audio.teleport1);
            
            this.consumeSpellCosts();
            this.pastTeleportTime = System.currentTimeMillis();
            
            this.nextLocation.setLocation(x, y);
            this.correctAndSetCoordinates();
            
            if(!this.isActive || !this.isRotorSystemActive){this.activate();}
            if(this.tractor != null){this.stopTractor();}
    
            this.powerUpController.activateInvinciblePowerUpBriefly();
            
            this.bonusKills = 0;
            this.enhancedRadiationTimer = TELEPORT_INVULNERABILITY_TIME;
            this.bonusKillsTimer = NICE_CATCH_TIME;
            this.bonusKillsMoney = 0;
        }
    }
    
    private boolean canTeleportTo(int x, int y)
    {
        return this.hasEnoughEnergyForAbility()
                && !this.isDamaged
                && !Window.isMenuVisible
                && this.hasValidTeleportDestination(x, y);
    }
    
    private boolean hasValidTeleportDestination(int x, int y)
    {
        return  !(this.getMaxY() + NO_COLLISION_HEIGHT >= GROUND_Y && y >= GROUND_Y)
                && !(	   x > this.getX() + 33
                        && x < this.getX() + 133
                        && y > this.getY() + 6
                        && y < this.getY() + 106);
    }
    
    @Override
    public boolean canObtainCollisionReward()
    {
        return this.hasShortRangeRadiation;
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
    
    @Override
    public boolean hasTimeRecordingMissiles()
    {
        return true;
    }
    
    public int getEnhancedRadiationTimer()
    {
        return enhancedRadiationTimer;
    }
    
    public boolean hasShortRangeRadiation()
    {
        return hasShortRangeRadiation;
    }
}