package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.events.MouseEvent;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.geometry.Dimension;

import javax.sound.sampled.Clip;

import static de.helicopter_vs_aliens.model.enemy.EnemyType.KABOOM;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.FIREPOWER;


public final class Phoenix extends Helicopter
{
    public static final int
        TELEPORT_KILL_TIME = 15;

    public static final int
        NICE_CATCH_TIME = 22;

    public static final int
        TELEPORT_INVULNERABILITY_TIME = 45;

    public static final int
        GOLIATH_COSTS = 6000;

    private static final float
        ENHANCED_RADIATION_PROB = 0.25f;

    private static final float
        RADIATION_DAMAGE_FACTOR = 1.5f;

    private static final float
        TELEPORT_DAMAGE_FACTOR = 4f;    // wie RADIATION_DAMAGE_FACTOR, aber für Kollisionen unmittelbar nach einem Transportvorgang


    private int
        enhancedRadiationTimer;

    private long
        pastTeleportTime;            // Zeitpunkt der letzten Nutzung des Teleporters

    private boolean
        hasShortRangeRadiation;        // = true: Helikopter verfügt über Nahkampfbestrahlung
    
    
    Phoenix(GameRessourceProvider gameRessourceProvider)
    {
        super(gameRessourceProvider);
    }
    
    @Override
    public HelicopterType getType()
    {
        return PHOENIX;
    }

    @Override
    void updateTimer()
    {
        super.updateTimer();
        if(enhancedRadiationTimer > 0)
        {
            enhancedRadiationTimer--;
        }
        evaluateBonusKills();
    }

    private void evaluateBonusKills()
    {
        if(bonusKillsTimer > 0)
        {
            bonusKillsTimer--;
            if(bonusKillsTimer == NICE_CATCH_TIME - TELEPORT_KILL_TIME
                && bonusKills > 1)
            {
                Events.extraReward(
                    bonusKills,
                    bonusKillsMoney,
                    0.75f, 0.75f, 3.5f);
            }
        }
    }

    @Override
    void resetFifthSpecial()
    {
        hasShortRangeRadiation = false;
    }

    @Override
    public int getGoliathCosts()
    {
        return GOLIATH_COSTS;
    }

    @Override
    public void obtainSomeUpgrades()
    {
        platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
        super.obtainSomeUpgrades();
    }

    @Override
    public boolean hasFifthSpecial()
    {
        return hasShortRangeRadiation;
    }

    @Override
    public void obtainFifthSpecial()
    {
        hasShortRangeRadiation = true;
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
    public void useEnergyAbility()
    {
        prepareTeleportation();
    }

    private void prepareTeleportation()
    {
        isSearchingForTeleportDestination = true;
        priorTeleportLocation.setLocation(
            getX() + (isMovingLeft
                ? FOCAL_POINT_X_LEFT
                : FOCAL_POINT_X_RIGHT),
            getY() + FOCAL_PNT_Y_POS);
    }

    @Override
    public void beAffectedByCollisionWith(Enemy enemy,
                                          boolean playCollisionSound)
    {
        super.beAffectedByCollisionWith(enemy, playCollisionSound);
        if(hasShortRangeRadiation)
        {
            enemy.reactToRadiation();
        }
    }

    @Override
    void startRecentDamageTimer()
    {
        if(enhancedRadiationTimer == 0)
        {
            super.startRecentDamageTimer();
        }
    }

    private boolean enhancedRadiationApproved(Enemy enemy)
    {
        return hasShortRangeRadiation
            && enemy.collisionDamageTimer == 0
            && enemy.getType() != KABOOM
            && enhancedRadiationTimer == 0
            && Calculations.tossUp(ENHANCED_RADIATION_PROB);
    }

    @Override
    public float getProtectionFactor()
    {
        return enhancedRadiationTimer == 0
            ? super.getProtectionFactor()
            : 0.0f;
    }

    @Override
    public boolean isFifthSpecialOnMaximumStrength()
    {
        return hasMaximumUpgradeLevelFor(FIREPOWER);
    }

    @Override
    public void initMenuEffect(int i)
    {
        super.initMenuEffect(i);
        gainInvincibilityPermanently();
    }

    @Override
    public void stopMenuEffect()
    {
        turnOfInvincibility();
    }

    @Override
    public boolean isTakingKaboomDamageFrom(Enemy enemy)
    {
        return super.isTakingKaboomDamageFrom(enemy) && !hasShortRangeRadiation;
    }

    @Override
    Clip getCollisionAudio()
    {
        return enhancedRadiationTimer == 0
            ? Audio.explosion1
            : Audio.explosion2;
    }

    @Override
    public void rightMouseButtonReleaseAction(MouseEvent mouseEvent, double scalingFactor)
    {
        Dimension displayShift = getGameRessourceProvider().getDisplayShift();
        tryToTeleportTo(
            (int)(mouseEvent.getX()/scalingFactor) - displayShift.getWidth(),
            (int)(mouseEvent.getY()/scalingFactor) - displayShift.getHeight());
    }

    public void tryToTeleportTo(int x, int y)
    {
        isSearchingForTeleportDestination = false;
        destination.setLocation(x, y);

        if(canTeleportTo(x, y))
        {
            Audio.play(Audio.teleport1);

            consumeSpellCosts();
            pastTeleportTime = System.currentTimeMillis();

            nextLocation.setLocation(x, y);
            correctAndSetCoordinates();

            if(!isActive || !isRotorSystemActive)
            {
                activate();
            }
            if(tractor != null)
            {
                stopTractor();
            }

            powerUpStateController.activateInvinciblePowerUpBriefly();

            bonusKills = 0;
            enhancedRadiationTimer = TELEPORT_INVULNERABILITY_TIME;
            bonusKillsTimer = NICE_CATCH_TIME;
            bonusKillsMoney = 0;
        }
    }

    private boolean canTeleportTo(int x, int y)
    {
        return hasEnoughEnergyForAbility()
            && !isDamaged
            && !Window.isMenuVisible
            && hasValidTeleportDestination(x, y);
    }

    private boolean hasValidTeleportDestination(int x, int y)
    {
        return !(getMaxY() + NO_COLLISION_HEIGHT >= GROUND_Y && y >= GROUND_Y)
            && !(x > getX() + 33
            && x < getX() + 133
            && y > getY() + 6
            && y < getY() + 106);
    }

    @Override
    public boolean canObtainCollisionReward()
    {
        return hasShortRangeRadiation;
    }

    @Override
    void startRecentDamageEffect(Enemy enemy)
    {
        if(enhancedRadiationApproved(enemy))
        {
            enhancedRadiationTimer = NO_COLLISION_DAMAGE_TIME;
        }
        else super.startRecentDamageEffect(enemy);
    }

    @Override
    public void resetStateTypeSpecific()
    {
        enhancedRadiationTimer = 0;
        isSearchingForTeleportDestination = false;
    }

    @Override
    public boolean deservesMantisReward(long missileLaunchingTime)
    {
        return bonusKillsTimer > 0
            && missileLaunchingTime > pastTeleportTime;
    }

    @Override
    public void typeSpecificRewards(Enemy enemy, Missile missile, boolean beamKill)
    {
        if(beamKill)
        {
            bonusKills++;
            bonusKillsMoney += Events.lastBonus;
        }
    }
    
    @Override
    void resetMissile(Missile missile)
    {
        super.resetMissile(missile);
        missile.setBackTimeRecorder();
    }
    

    public int getEnhancedRadiationTimer()
    {
        return enhancedRadiationTimer;
    }

    public boolean hasShortRangeRadiation()
    {
        return hasShortRangeRadiation;
    }

    @Override
    public int calculateCollisionDamage()
    {
        return (int) (currentBaseFirepower
            * (bonusKillsTimer > NICE_CATCH_TIME - TELEPORT_KILL_TIME
            ? TELEPORT_DAMAGE_FACTOR
            : RADIATION_DAMAGE_FACTOR));
    }
}
