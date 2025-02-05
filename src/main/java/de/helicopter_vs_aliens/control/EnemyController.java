package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableFactory;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.enemy.basic.Carrier;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.util.Calculations;

import java.util.Arrays;
import java.util.EnumSet;


public class EnemyController
{
    private static final int
        MIN_BARRIER_LEVEL = 2,
        MIN_FUTURE_LEVEL = 8,
        MIN_KABOOM_LEVEL = 12,
        MIN_ROCK_LEVEL = 27,
        ROCK_FREE_TIME = 250,    // Zeit die mind. vergeht, bis ein neuer Hindernis-Gegner erscheint
        MAX_BARRIER_NUMBER = 3;
    
    private static final float
        ROCK_PROB = 0.05f,    // Rate mit der Rock-Gegner erscheinen
        KABOOM_PROB = 0.02f;    // Rate mit der Kaboom-Gegner erscheinen
    
    private static final EnemySelector
        ENEMY_SELECTOR = new EnemySelector();
    
    public static boolean
        makeBossTwoServants,    // make-Variablen: bestimmen, ob ein bestimmter Boss-Gegner zu erzeugen ist
        makeBoss4Servant,
        makeAllFinalBossServants;
    
    public static final EnumSet<FinalBossServantType>
        missingFinalBossServants = EnumSet.noneOf(FinalBossServantType.class);
    
    public static int barrierTimer;        // reguliert das Erscheinen von Hindernis-Gegnern
    
    private static int rockTimer;            // reguliert das Erscheinen von "Rock"-Gegnern
    
    public static Enemy
        currentRock,                    // Referenz auf den aktiven Rock-Gegner
        currentMiniBoss;    // Referenz auf den aktuellen Boss-Gegner
    
    public static Carrier
        carrierDestroyedJustNow;    // Referenz auf den zuletzt zerstörten Carrier-Gegner
    
    // TODO über Liste abbilden
    public static final Enemy[]
        livingBarrier = new Enemy[MAX_BARRIER_NUMBER];
    
    public static int currentNumberOfBarriers; // aktuelle Anzahl von "lebenden" Hindernis-Gegnern
    
    
    private final GameRessourceProvider gameRessourceProvider;
    
    
    // TODO die Begrenzung nach Anzahl funktioniert nicht mehr
    public void generateNewEnemies()
    {
        Events.lastCreationTimer++;
        if(wasCarrierDestroyedJustNow())
        {
            createCarrierServants();
        }
        else if(LevelManager.wasEnemyCreationPaused)
        {
            verifyCreationStop();
        }
        if(isBossServantCreationApproved())
        {
            createBossServant();
        }
        else if(isEnemyCreationApproved())
        {
            creation();
        }
    }
    
    private static boolean wasCarrierDestroyedJustNow()
    {
        return carrierDestroyedJustNow != null;
    }
    
    // TODO könnte diese Methode nicht direkt aufgerufen werden, wenn der Carrier zerstört wurde. Die Variable "carrierKilledJustNow" könnte dann entfallen.
    private void createCarrierServants()
    {
        for(int m = 0; m < calculateCarrierServantCount(); m++)
        {
            creation();
        }
        carrierDestroyedJustNow = null;
    }
    
    private static int calculateCarrierServantCount()
    {
        return carrierDestroyedJustNow.calculateServantCount();
    }
    
    private void verifyCreationStop()
    {
        if(areCreationStopConditionsMet())
        {
            LevelManager.wasEnemyCreationPaused = false;
            if(Events.isCurrentLevelBossLevel())
            {
                LevelManager.maxNr = 1;
                LevelManager.maxBarrierNr = 0;
                Events.setBossLevelUpConditions();
            }
        }
    }
    
    private boolean areCreationStopConditionsMet()
    {
        return gameRessourceProvider.getManageablePaintableService()
                                    .isEmptyFor(ManageablePaintableGroupType.INTACT_ENEMY)
            && carrierDestroyedJustNow == null
            && !(gameRessourceProvider.getHelicopter()
                                      .isUnacceptablyBoostedForBossLevel()
            && Events.isCurrentLevelBossLevel());
    }
    
    private static boolean isBossServantCreationApproved()
    {
        return makeBossTwoServants
            || makeBoss4Servant
            || makeAllFinalBossServants
            || !missingFinalBossServants.isEmpty();
    }
    
    private void createBossServant()
    {
        // TODO Wir haben hier 3 boolesche Variablen, nur um Festzulegen, welche Servants zu erzeugen sind. Ein Enum wäre hier besser.
        if(makeBossTwoServants)
        {
            createBoss2Servants();
        }
        else if(makeBoss4Servant)
        {
            makeBoss4Servant = false;
            creation();
        }
        else if(makeAllFinalBossServants)
        {
            createAllFinalBossServants();
        }
        else
        {
            createMissingFinalBossServants();
        }
    }
    
    private boolean isEnemyCreationApproved()
    {
        int numberOfEnemies = gameRessourceProvider.getManageablePaintableService()
                                                   .numberOfActiveEntities(ManageablePaintableGroupType.INTACT_ENEMY);
        return !hasNumberOfEnemiesReachedLimit(numberOfEnemies)
            && !isMajorBossActive()
            && !LevelManager.wasEnemyCreationPaused
            && !Events.wasMaximumLevelExceeded()
            && Events.hasEnoughTimePassedSinceLastCreation()
            && Events.wereRandomRequirementsMet(LevelManager.maxNr + LevelManager.maxBarrierNr - numberOfEnemies);
    }
    
    private static boolean hasNumberOfEnemiesReachedLimit(int numberOfEnemies)
    {
        return numberOfEnemies >= LevelManager.maxNr + LevelManager.maxBarrierNr;
    }
    
    private boolean isMajorBossActive()
    {
        return gameRessourceProvider.getManageablePaintableService().isMajorBossActive();
    }
    
    private void createBoss2Servants()
    {
        makeBossTwoServants = false;
        LevelManager.wasEnemyCreationPaused = true;
        LevelManager.nextBossEnemyType = EnemyType.BOSS_2_SERVANT;
        LevelManager.maxNr = 12;
        for(int m = 0; m < LevelManager.maxNr; m++)
        {
            creation();
        }
        LevelManager.nextBossEnemyType = null;
        LevelManager.maxNr = 3;
    }
    
    private void createAllFinalBossServants()
    {
        makeAllFinalBossServants = false;
        FinalBossServantType.getValues()
                            .forEach(servantType -> {
                                LevelManager.nextBossEnemyType = servantType.getEnemyType();
                                creation();
                            });
    }
    
    private void createMissingFinalBossServants()
    {
        FinalBossServantType.getValues()
                            .forEach(servantType -> {
                                if(missingFinalBossServants.contains(servantType))
                                {
                                    missingFinalBossServants.remove(servantType);
                                    LevelManager.nextBossEnemyType = servantType.getEnemyType();
                                    creation();
                                }
                            });
    }
    
    private void creation()
    {
        ManageablePaintableFactory<Enemy> enemyFactory = getEnemyFactory();
        Enemy enemy = gameRessourceProvider.getManageablePaintableService()
                                            .activateEntity(enemyFactory);
        enemy.reset();
        if(enemy.countsForTotalAmountOfEnemiesSeen())
        {
            gameRessourceProvider.getGameStatisticsCalculator()
                                 .incrementNumberOfEnemiesSeen();
        }
        Events.lastCreationTimer = 0;
        enemy.initialize();
    }
    
    private ManageablePaintableFactory<Enemy> getEnemyFactory()
    {
        if(wasCarrierDestroyedJustNow())
        {
            return EnemyType.ESCAPED_SPEEDER;
        }
        if(barrierCreationApproved())
        {
            return getNextBarrierType();
        }
        if(rockCreationApproved())
        {
            return EnemyType.ROCK;
        }
        if(kaboomCreationApproved())
        {
            return EnemyType.KABOOM;
        }
        if(isBossEnemyToBeCreated())
        {
            return LevelManager.nextBossEnemyType;
        }
        return getNextDefaultEnemyType();
    }
    
    private boolean barrierCreationApproved()
    {
        int numberOfEnemies = gameRessourceProvider.getManageablePaintableService()
                                                    .numberOfActiveEntities(ManageablePaintableGroupType.INTACT_ENEMY);
        return Events.level >= MIN_BARRIER_LEVEL
            && !Events.isCurrentLevelBossLevel()
            && barrierTimer == 0
            && (Calculations.tossUp(0.35f)
            || (numberOfEnemies - currentNumberOfBarriers >= LevelManager.maxNr))
            && currentNumberOfBarriers < LevelManager.maxBarrierNr;
    }
    
    private static EnemyType getNextBarrierType()
    {
        int randomBarrierSelectionModifier = isBarrierFromFutureCreationApproved()
            ? Calculations.random(3)
            : 0;
        int selectedBarrierIndex = Calculations.random(Math.min(LevelManager.selectionBarrier + randomBarrierSelectionModifier,
                                                                EnemyType.getBarrierTypes()
                                                                         .size()));
        return (EnemyType)EnemyType.getBarrierTypes()
                                   .toArray()[selectedBarrierIndex];
    }
    
    private static boolean isBarrierFromFutureCreationApproved()
    {
        return Calculations.tossUp(0.05f) && Events.level >= MIN_FUTURE_LEVEL;
    }
    
    private static boolean rockCreationApproved()
    {
        return currentRock == null
            && Events.level >= MIN_ROCK_LEVEL
            && !Events.isCurrentLevelBossLevel()
            && rockTimer == 0
            && Calculations.tossUp(ROCK_PROB);
    }
    
    private static boolean kaboomCreationApproved()
    {
        return Events.level >= MIN_KABOOM_LEVEL
            && !Events.isCurrentLevelBossLevel()
            && Calculations.tossUp(KABOOM_PROB);
    }
    
    private static boolean isBossEnemyToBeCreated()
    {
        return LevelManager.nextBossEnemyType != null;
    }
    
    private static EnemyType getNextDefaultEnemyType()
    {
        return ENEMY_SELECTOR.getType(Calculations.random(LevelManager.selection));
    }
    
    public EnemyController(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    // TODO gehört in eine eigene Klasse
    public void updateAllActive()
    {
        if(rockTimer > 0)
        {
            rockTimer--;
        }
        if(Scenery.isBackgroundMoving && barrierTimer > 0)
        {
            barrierTimer--;
        }
        countBarriers();
        
        ManageablePaintableService manageablePaintableService = gameRessourceProvider.getManageablePaintableService();
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.INTACT_ENEMY,
                                                            Enemy::isIntactAndNotForRemoval,
                                                            Enemy::update);
        manageablePaintableService.removeEnemyToBackgroundIf(Enemy::isDestroyed);
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.INTACT_ENEMY,
                                                            Enemy::shouldBeRemoved,
                                                            Enemy::clearImage);
        manageablePaintableService.removeIf(ManageablePaintableGroupType.INTACT_ENEMY,
                                               Enemy::shouldBeRemoved);
    }
    
    public void updateAllDestroyed()
    {
        ManageablePaintableService manageablePaintableService = gameRessourceProvider.getManageablePaintableService();
        manageablePaintableService.forEachActiveEntity(ManageablePaintableGroupType.DESTROYED_ENEMY,
                                                          Enemy::updateDead);
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.DESTROYED_ENEMY,
                                                            Enemy::hasCollisionWithHelicopter,
                                                            Enemy::collision);
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.DESTROYED_ENEMY,
                                                            Enemy::isMarkedForRemoval,
                                                            Enemy::clearImage);
        manageablePaintableService.removeIf(ManageablePaintableGroupType.DESTROYED_ENEMY,
                                               Enemy::isMarkedForRemoval);
    }
    
    private void countBarriers()
    {
        Arrays.fill(livingBarrier, null);
        currentNumberOfBarriers = 0;
        gameRessourceProvider.getManageablePaintableService()
                             .forEachActiveEntityIf(ManageablePaintableGroupType.INTACT_ENEMY,
                                                    Enemy::isIntactBarrierAndNotForRemoval,
                                                    enemy -> {
                                                        livingBarrier[currentNumberOfBarriers] = enemy;
                                                        currentNumberOfBarriers++;
                                                    });
    }
    
    public static void removeCurrentRock()
    {
        currentRock = null;
        rockTimer = ROCK_FREE_TIME;
    }
}
