package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyModelType;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.enemy.basic.Carrier;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.util.Calculations;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;


public class EnemyController
{
    private static final int
        MIN_BARRIER_LEVEL = 2,
        MIN_FUTURE_LEVEL  = 8,
        MIN_KABOOM_LEVEL  = 12,
        MIN_ROCK_LEVEL    = 27,
        ROCK_FREE_TIME = 250,    // Zeit die mind. vergeht, bis ein neuer Hindernis-Gegner erscheint
        MAX_BARRIER_NUMBER = 3;
    
    private static final float
        ROCK_PROB   = 0.05f,	// Rate mit der Rock-Gegner erscheinen
        KABOOM_PROB = 0.02f;    // Rate mit der Kaboom-Gegner erscheinen
    
    private static final EnemySelector
        ENEMY_SELECTOR = new EnemySelector();
    
    public static boolean
        makeBossTwoServants = false,    // make-Variablen: bestimmen, ob ein bestimmter Boss-Gegner zu erzeugen ist
        makeBoss4Servant = false,
        makeAllBoss5Servants = false;
    
    public static final EnumSet<FinalBossServantType>
        makeFinalBossServant = EnumSet.noneOf(FinalBossServantType.class);
    
    public static int barrierTimer;		// reguliert das Erscheinen von Hindernis-Gegnern
    
    public static int rockTimer;            // reguliert das Erscheinen von "Rock"-Gegnern
    
    public static Enemy
        currentRock,                    // Referenz auf den aktiven Rock-Gegner
        currentMiniBoss;    // Referenz auf den aktuellen Boss-Gegner
    
    public static Carrier
        carrierDestroyedJustNow;    // Referenz auf den zuletzt zerstörten Carrier-Gegner
    
    public static final Enemy[]
        livingBarrier = new Enemy [MAX_BARRIER_NUMBER];
    
    public static int currentNumberOfBarriers; // aktuelle Anzahl von "lebenden" Hindernis-Gegnern
    
    // TODO die Begrenzung nach Anzahl funktioniert nicht mehr
    public static void generateNewEnemies(GameRessourceProvider gameRessourceProvider)
    {
        Events.lastCreationTimer++;
        if(wasCarrierDestroyedJustNow()){
            createCarrierServants(gameRessourceProvider);}
        else if(LevelManager.wasEnemyCreationPaused){
            verifyCreationStop(gameRessourceProvider);}
        if(isBossServantCreationApproved()){
            createBossServant(gameRessourceProvider);}
        else if(isEnemyCreationApproved(gameRessourceProvider.getEnemies()))
        {
            creation(gameRessourceProvider);
        }
    }
    
    private static boolean wasCarrierDestroyedJustNow()
    {
        return carrierDestroyedJustNow != null;
    }
    
    // TODO könnte diese Methode nicht direkt aufgerufen werden, wenn der Carrier zerstört wurde. Die Variable "carrierKilledJustNow" könnte dann entfallen.
    private static void createCarrierServants(GameRessourceProvider gameRessourceProvider)
    {
        for(int m = 0; m < calculateCarrierServantCount(); m++)
        {
            creation(gameRessourceProvider);
        }
        carrierDestroyedJustNow = null;
    }
    
    private static int calculateCarrierServantCount()
    {
        return carrierDestroyedJustNow.calculateServantCount();
    }
    
    private static void verifyCreationStop(GameRessourceProvider gameRessourceProvider)
    {
        if(	gameRessourceProvider.getEnemies().get(CollectionSubgroupType.ACTIVE).isEmpty()
            && carrierDestroyedJustNow == null
            && !(gameRessourceProvider.getHelicopter().isUnacceptablyBoostedForBossLevel()
            && Events.isCurrentLevelBossLevel()) )
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
    
    private static boolean isBossServantCreationApproved()
    {
        return makeBossTwoServants
            || makeBoss4Servant
            || makeAllBoss5Servants
            || hasToMakeBoss5Servants();
    }
    
    private static boolean hasToMakeBoss5Servants()
    {
        return FinalBossServantType.getValues()
                                   .stream()
                                   .anyMatch(makeFinalBossServant::contains);
    }
    
    private static void createBossServant(GameRessourceProvider gameRessourceProvider)
    {
        // TODO Wir haben hier 3 boolesche Variablen, nur um Festzulegen, welche Servants zu erzeugen sind. Ein Enum wäre hier besser.
        if(makeBossTwoServants)
        {
            createBoss2Servants(gameRessourceProvider);
        }
        else if(makeBoss4Servant)
        {
            makeBoss4Servant = false;
            creation(gameRessourceProvider);
        }
        else if(makeAllBoss5Servants)
        {
            createAllBoss5Servants(gameRessourceProvider);
        }
        else
        {
            FinalBossServantType.getValues()
                                .forEach(servantType -> {
                                    if (makeFinalBossServant.contains(servantType))
                                    {
                                        makeFinalBossServant.remove(servantType);
                                        LevelManager.nextBossEnemyType = servantType.getEnemyType();
                                        creation(gameRessourceProvider);
                                    }
                                });
        }
    }
    
    private static boolean isEnemyCreationApproved(Map<CollectionSubgroupType, Queue<Enemy>> enemies)
    {
        int numberOfEnemies = enemies.get(CollectionSubgroupType.ACTIVE).size();
        return     !hasNumberOfEnemiesReachedLimit(numberOfEnemies)
            && !isMajorBossActive(enemies)
            && !LevelManager.wasEnemyCreationPaused
            && !Events.wasMaximumLevelExceeded()
            && Events.hasEnoughTimePassedSinceLastCreation()
            && Events.wereRandomRequirementsMet(LevelManager.maxNr + LevelManager.maxBarrierNr - numberOfEnemies);
    }
    
    private static boolean hasNumberOfEnemiesReachedLimit(int numberOfEnemies)
    {
        return numberOfEnemies >= LevelManager.maxNr + LevelManager.maxBarrierNr;
    }
    
    private static boolean isMajorBossActive(Map<CollectionSubgroupType, Queue<Enemy>> enemies)
    {
        return !enemies.get(CollectionSubgroupType.ACTIVE).isEmpty() && enemies.get(CollectionSubgroupType.ACTIVE).element().type.isMajorBoss();
    }
    
    private static void createBoss2Servants(GameRessourceProvider gameRessourceProvider)
    {
        makeBossTwoServants = false;
        LevelManager.wasEnemyCreationPaused = true;
        LevelManager.nextBossEnemyType = EnemyType.BOSS_2_SERVANT;
        LevelManager.maxNr = 12;
        for (int m = 0; m < LevelManager.maxNr; m++)
        {
            creation(gameRessourceProvider);
        }
        LevelManager.nextBossEnemyType = null;
        LevelManager.maxNr = 3;
    }
    
    private static void createAllBoss5Servants(GameRessourceProvider gameRessourceProvider)
    {
        makeAllBoss5Servants = false;
        FinalBossServantType.getValues().forEach(servantType -> {
            LevelManager.nextBossEnemyType = servantType.getEnemyType();
            creation(gameRessourceProvider);
        });
    }
    
    
    public static void creation(GameRessourceProvider gameRessourceProvider)
    {
        Queue<Enemy> activeEnemies = gameRessourceProvider.getEnemies().get(CollectionSubgroupType.ACTIVE);
        int activeEnemyCount = activeEnemies.size();
        GameEntityFactory<Enemy> enemyFactory = getEnemyFactory(activeEnemyCount);
        Enemy enemy = gameRessourceProvider.getNewGameEntityInstance(enemyFactory);
        enemy.reset();
        activeEnemies.add(enemy);
        if(enemy.countsForTotalAmountOfEnemiesSeen())
        {
            gameRessourceProvider.getGameStatisticsCalculator().incrementNumberOfEnemiesSeen();
        }
        Events.lastCreationTimer = 0;
        enemy.initialize();
    }
    
    private static GameEntityFactory<Enemy> getEnemyFactory(int activeEnemyCount)
    {
        if(wasCarrierDestroyedJustNow()){return EnemyType.ESCAPED_SPEEDER;}
        if(barrierCreationApproved(activeEnemyCount)){return getNextBarrierType();}
        if(rockCreationApproved()){return EnemyType.ROCK;}
        if(kaboomCreationApproved()){return EnemyType.KABOOM;}
        if(isBossEnemyToBeCreated()){return LevelManager.nextBossEnemyType;}
        return getNextDefaultEnemyType();
    }
    
    private static boolean barrierCreationApproved(int numberOfEnemies)
    {
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
        int selectedBarrierIndex = Calculations.random(Math.min(LevelManager.selectionBarrier + randomBarrierSelectionModifier, EnemyType.getBarrierTypes().size()));
        return (EnemyType) EnemyType.getBarrierTypes()
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
    
    
    // TODO gehört in eine eigene Klasse
    /* Die folgende Funktion reguliert die Gegner-Bewegung:
     * 1. Unter Berücksichtigung jeglicher Eventualitäten (specialManöver, ausweichen, etc.)
     *	  werden die neuen Koordinaten berechnet.
     * 2. Der Gegner wird an Stelle seiner neuen Koordinaten gemalt.
     */
    public static void updateAllActive(GameRessourceProvider gameRessourceProvider)
    {
        if(rockTimer > 0){
            rockTimer--;}
        if(Scenery.backgroundMoves && barrierTimer > 0){
            barrierTimer--;}
        countBarriers(gameRessourceProvider.getEnemies());
        
        for(Iterator<Enemy> iterator = gameRessourceProvider.getEnemies().get(CollectionSubgroupType.ACTIVE).iterator(); iterator.hasNext();)
        {
            Enemy enemy = iterator.next();
            if(enemy.isIntact() && !enemy.isMarkedForRemoval)
            {
                enemy.update(gameRessourceProvider);
            }
            else if(enemy.isDestroyed())
            {
                iterator.remove();
                gameRessourceProvider.getEnemies().get(CollectionSubgroupType.DESTROYED).add(enemy);
            }
            else
            {
                enemy.clearImage();
                iterator.remove();
                gameRessourceProvider.getGameEntitySupplier()
                                     .store(enemy);
            }
        }
    }
    
    private static void countBarriers(Map<CollectionSubgroupType, Queue<Enemy>> enemies)
    {
        Arrays.fill(livingBarrier, null);
        currentNumberOfBarriers = 0;
        for(Enemy enemy  : enemies.get(CollectionSubgroupType.ACTIVE))
        {
            if (enemy.getModel() == EnemyModelType.BARRIER
                && enemy.isIntact()
                && !enemy.isMarkedForRemoval)
            {
                livingBarrier[currentNumberOfBarriers] = enemy;
                currentNumberOfBarriers++;
            }
        }
    }
    
    public static void removeCurrentRock()
    {
        currentRock = null;
        rockTimer = ROCK_FREE_TIME;
    }
}
