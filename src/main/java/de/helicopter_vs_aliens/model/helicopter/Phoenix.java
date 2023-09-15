package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.events.MouseEvent;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.util.Calculations;

import javax.sound.sampled.Clip;

import static de.helicopter_vs_aliens.model.enemy.EnemyType.KABOOM;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.OROCHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PEGASUS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PHOENIX;
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


    @Override
    public HelicopterType getType()
    {
        return PHOENIX;
    }

    @Override
    void updateTimer()
    {
        super.updateTimer();
        if (this.enhancedRadiationTimer > 0)
        {
            this.enhancedRadiationTimer--;
        }
        this.evaluateBonusKills();
    }

    private void evaluateBonusKills()
    {
        if (this.bonusKillsTimer > 0)
        {
            this.bonusKillsTimer--;
            if (this.bonusKillsTimer == NICE_CATCH_TIME - TELEPORT_KILL_TIME
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
        if (!PEGASUS.hasReachedLevel20())
        {
            Window.unlock(OROCHI);
        }
    }

    @Override
    public void tryToUseEnergyAbility(GameRessourceProvider gameRessourceProvider)
    {
        if (this.isEnergyAbilityActivatable())
        {
            useEnergyAbility(gameRessourceProvider);
        }
    }

    @Override
    public void useEnergyAbility(GameRessourceProvider gameRessourceProvider)
    {
        this.prepareTeleportation();
    }

    private void prepareTeleportation()
    {
        this.isSearchingForTeleportDestination = true;
        this.priorTeleportLocation.setLocation(
            this.getX() + (this.isMovingLeft
                ? FOCAL_POINT_X_LEFT
                : FOCAL_POINT_X_RIGHT),
            this.getY() + FOCAL_PNT_Y_POS);
    }

    @Override
    public void beAffectedByCollisionWith(Enemy enemy,
                                          GameRessourceProvider gameRessourceProvider,
                                          boolean playCollisionSound)
    {
        super.beAffectedByCollisionWith(enemy, gameRessourceProvider, playCollisionSound);
        if (this.hasShortRangeRadiation)
        {
            enemy.reactToRadiation(gameRessourceProvider);
        }
    }

    @Override
    void startRecentDamageTimer()
    {
        if (this.enhancedRadiationTimer == 0)
        {
            super.startRecentDamageTimer();
        }
    }

    private boolean enhancedRadiationApproved(Enemy enemy)
    {
        return this.hasShortRangeRadiation
            && enemy.collisionDamageTimer == 0
            && enemy.getType() != KABOOM
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
    Clip getCollisionAudio()
    {
        return this.enhancedRadiationTimer == 0
            ? Audio.explosion1
            : Audio.explosion2;
    }

    @Override
    public void rightMouseButtonReleaseAction(MouseEvent mouseEvent)
    {
        this.tryToTeleportTo(mouseEvent.getX() - Main.displayShift.width,
            mouseEvent.getY() - Main.displayShift.height);
    }

    public void tryToTeleportTo(int x, int y)
    {
        this.isSearchingForTeleportDestination = false;
        this.destination.setLocation(x, y);

        if (this.canTeleportTo(x, y))
        {
            Audio.play(Audio.teleport1);

            this.consumeSpellCosts();
            this.pastTeleportTime = System.currentTimeMillis();

            this.nextLocation.setLocation(x, y);
            this.correctAndSetCoordinates();

            if (!this.isActive || !this.isRotorSystemActive)
            {
                this.activate();
            }
            if (this.tractor != null)
            {
                this.stopTractor();
            }

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
        return !(this.getMaxY() + NO_COLLISION_HEIGHT >= GROUND_Y && y >= GROUND_Y)
            && !(x > this.getX() + 33
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
        if (this.enhancedRadiationApproved(enemy))
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
        if (beamKill)
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

    @Override
    public int calculateCollisionDamage()
    {
        return (int) (currentBaseFirepower
            * (this.bonusKillsTimer > NICE_CATCH_TIME - TELEPORT_KILL_TIME
            ? TELEPORT_DAMAGE_FACTOR
            : RADIATION_DAMAGE_FACTOR));
    }


}