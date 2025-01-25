package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.LevelManager;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import org.apache.commons.lang3.BooleanUtils;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Predicate;


public final class FinalBoss extends BossEnemy
{
    private static final int
        FINAL_BOSS_POSITION_Y = 98;
    
    private static final float
        SECONDARY_COLOR_BRIGHTNESS_FACTOR = 1.3f;
    
    private final EnumMap<FinalBossServantType, Enemy>
        servants = new EnumMap<>(FinalBossServantType.class);
    
    private final EnumMap<FinalBossServantType, Integer>
        timeSinceServantDeath = new EnumMap<>(FinalBossServantType.class);
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        LevelManager.maxNr = 5; // TODO diese Zuweisung gehört hier nicht her
        Events.boss = this;
        super.doTypeSpecificInitialization();
    }
    
    @Override
    public void reset()
    {
        servants.clear();
        timeSinceServantDeath.clear();
        super.reset();
    }
    
    @Override
    protected double calculateInitialY()
    {
        return FINAL_BOSS_POSITION_Y;
    }
    
    @Override
    protected float getSecondaryColorBrightnessFactor()
    {
        return SECONDARY_COLOR_BRIGHTNESS_FACTOR;
    }
    
    @Override
    public boolean isStunnable()
    {
        return false;
    }
    
    @Override
    protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        finalBossAction();
        super.performFlightManeuver(gameRessourceProvider);
    }
    
    private void finalBossAction()
    {
        // TODO eventuell auslagern in eigene Flugmanöver-Klasse
        if(getSpeedLevel()
            .getX() > 0)
        {
            if(getSpeedLevel()
                .getX() - 0.5 <= 0)
            {
                stopMoving();
                boss.setLocation(getCenterX(),
                                 getCenterY());
                EnemyController.makeAllFinalBossServants = true;
            }
            else
            {
                getSpeedLevel()
                    .setLocation(getSpeedLevel()
                                     .getX() - 0.5, 0);
            }
        }
        else
        {
            FinalBossServantType.getValues()
                                .stream()
                                .filter(Predicate.not(servants::containsKey))
                                .forEach(servantType -> {
                                    if(isFinalBossServantCreationAllowedFor(servantType))
                                    {
                                        EnemyController.missingFinalBossServants.add(servantType);
                                    }
                                    else
                                    {
                                        incrementTimeSinceDeathCounter(servantType);
                                    }
                                });
        }
    }
    
    private boolean isFinalBossServantCreationAllowedFor(FinalBossServantType servantType)
    {
        return Calculations.tossUp(servantType.getReturnProbability())
            && hasMinimumTimeBeforeRecreationElapsed(servantType);
    }
    
    @Override
    protected void bossInactivationEvent() {}
    
    @Override
    protected void bossTypeSpecificDestructionEffect()
    {
        killOwnServants();
        
        Events.isRestartWindowVisible = true;
        Events.level = Events.maxLevel = 51;
        
        Helicopter helicopter = getHelicopter();
        helicopter.isDamaged = true;
        // TODO Konstanten definieren
        helicopter.destination.setLocation(helicopter.getX() + 40, 520.0);
        Events.determineHighscoreTimes(helicopter);
    }
    
    public void removeServant(FinalBossServantType servantType)
    {
        servants.remove(servantType);
        resetTimeSinceDeath(servantType);
    }
    
    private void resetTimeSinceDeath(FinalBossServantType servantType)
    {
        timeSinceServantDeath.put(servantType, 0);
    }
    
    public void registerServant(Enemy enemy)
    {
        FinalBossServantType.of(enemy.getType())
                            .ifPresent(servantType -> servants.put(servantType, enemy));
    }
    
    public Enemy getServant(FinalBossServantType servantType)
    {
        return servants.get(servantType);
    }
    
    private boolean hasMinimumTimeBeforeRecreationElapsed(FinalBossServantType servantType)
    {
        return timeSinceServantDeath.get(servantType) > servantType.getMinimumTimeBeforeRecreation();
    }
    
    private void incrementTimeSinceDeathCounter(FinalBossServantType servantType)
    {
        Integer timeSinceDeathCounter = timeSinceServantDeath.get(servantType);
        timeSinceServantDeath.put(servantType, timeSinceDeathCounter + 1);
    }
    
    public boolean isUpperShieldPositionAvailableFor(ShieldMaker shieldMaker)
    {
        return getShieldingBrother(shieldMaker).map(ShieldMaker::isUpperShieldMaker)
                                               .map(BooleanUtils::negate)
                                               .orElseGet(Calculations::tossUp);
    }
    
    private Optional<ShieldMaker> getShieldingBrother(ShieldMaker shieldMaker)
    {
        FinalBossServantType shieldingBrotherServantType = shieldMaker.getShieldingBrotherServantType();
        ShieldMaker shieldingBrother = (ShieldMaker)getServant(shieldingBrotherServantType);
        return Optional.ofNullable(shieldingBrother);
    }
}
