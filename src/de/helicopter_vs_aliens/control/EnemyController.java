package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.util.Calculations;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.DESTROYED;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;

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
        carrierDestroyedJustNow,  		// Referenz auf den zuletzt zerstörten Carrier-Gegner
        currentMiniBoss;    // Referenz auf den aktuellen Boss-Gegner
    
    public static final Enemy[]
        livingBarrier = new Enemy [MAX_BARRIER_NUMBER];
    
    public static int currentNumberOfBarriers; // aktuelle Anzahl von "lebenden" Hindernis-Gegnern
    
    // TODO die Begrenzung nach Anzahl funktioniert nicht mehr
    public static void generateNewEnemies(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy, Helicopter helicopter)
    {
        Events.lastCreationTimer++;
        if(wasCarrierDestroyedJustNow()){
            createCarrierServants(helicopter, enemy);}
        else if(LevelManager.wasEnemyCreationPaused){
            verifyCreationStop(enemy, helicopter);}
        if(isBossServantCreationApproved()){
            createBossServant(helicopter, enemy);}
        else if(isEnemyCreationApproved(enemy))
        {
            creation(helicopter, enemy);
        }
    }
    
    private static boolean wasCarrierDestroyedJustNow()
    {
        return carrierDestroyedJustNow != null;
    }
    
    // TODO könnte diese Methode nicht direkt aufgerufen werden, wenn der Carrier zerstört wurde. Die Variable "carrierKilledJustNow" könnte dann entfallen.
    private static void createCarrierServants(Helicopter helicopter,
                                              EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        for(int m = 0;
            m < (carrierDestroyedJustNow.isMiniBoss
                ? 5 + Calculations.random(3)
                : 2 + Calculations.random(2));
            m++)
        {
            creation(helicopter, enemy);
        }
        carrierDestroyedJustNow = null;
    }
    
    private static void verifyCreationStop(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy,
                                           Helicopter helicopter)
    {
        if(	enemy.get(ACTIVE).isEmpty()
            && carrierDestroyedJustNow == null
            && !(helicopter.isUnacceptablyBoostedForBossLevel()
            && Events.isBossLevel()) )
        {
            LevelManager.wasEnemyCreationPaused = false;
            if(Events.isBossLevel())
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
    
    private static void createBossServant(Helicopter helicopter,
                                          EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        // TODO Wir haben hier 3 boolesche Variablen, nur um Festzulegen, welche Servants zu erzeugen sind. Ein Enum wäre hier besser.
        if(makeBossTwoServants)
        {
            createBoss2Servants(helicopter, enemy);
        }
        else if(makeBoss4Servant)
        {
            makeBoss4Servant = false;
            creation(helicopter, enemy);
        }
        else if(makeAllBoss5Servants)
        {
            createAllBoss5Servants(helicopter, enemy);
        }
        else
        {
            FinalBossServantType.getValues()
                                .forEach(servantType -> {
                                    if (makeFinalBossServant.contains(servantType))
                                    {
                                        makeFinalBossServant.remove(servantType);
                                        LevelManager.nextBossEnemyType = servantType.getEnemyType();
                                        creation(helicopter, enemy);
                                    }
                                });
        }
    }
    
    private static boolean isEnemyCreationApproved(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemies)
    {
        int numberOfEnemies = enemies.get(ACTIVE).size();
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
    
    private static boolean isMajorBossActive(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        return !enemy.get(ACTIVE).isEmpty() && enemy.get(ACTIVE).getFirst().type.isMajorBoss();
    }
    
    private static void createBoss2Servants(Helicopter helicopter,
                                            EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        makeBossTwoServants = false;
        LevelManager.wasEnemyCreationPaused = true;
        LevelManager.nextBossEnemyType = EnemyType.BOSS_2_SERVANT;
        LevelManager.maxNr = 12;
        for (int m = 0; m < LevelManager.maxNr; m++)
        {
            creation(helicopter, enemy);
        }
    }
    
    private static void createAllBoss5Servants(Helicopter helicopter,
                                               EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        makeAllBoss5Servants = false;
        FinalBossServantType.getValues().forEach(servantType -> {
            LevelManager.nextBossEnemyType = servantType.getEnemyType();
            creation(helicopter, enemy);
        });
    }
    
    
    public static void creation(Helicopter helicopter, EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemies)
    {
		/*Iterator<Enemy> i = enemy.get(INACTIVE).iterator();
		Enemy e;
		if (i.hasNext())
		{
			e = i.next();
			i.remove();
		}
		else{e = EnemyFactory.createEnemy();}*/
        
        LinkedList<Enemy> activeEnemies = enemies.get(ACTIVE);
        int activeEnemyCount = activeEnemies.size();
        GameEntityFactory<Enemy> enemyFactory = getEnemyFactory(activeEnemyCount);
        Enemy enemy = enemyFactory.makeInstance();
        activeEnemies.add(enemy);
        if(enemy.countsForTotalAmountOfEnemiesSeen()){helicopter.numberOfEnemiesSeen++;}
        Events.lastCreationTimer = 0;
        enemy.initialize(helicopter);
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
            && !Events.isBossLevel()
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
            && !Events.isBossLevel()
            && rockTimer == 0
            && Calculations.tossUp(ROCK_PROB);
    }
    
    private static boolean kaboomCreationApproved()
    {
        return Events.level >= MIN_KABOOM_LEVEL
            && !Events.isBossLevel()
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
    public static void updateAllActive(Controller controller,
                                       Helicopter helicopter)
    {
        if(rockTimer > 0){
            rockTimer--;}
        if(Scenery.backgroundMoves && barrierTimer > 0){
            barrierTimer--;}
        countBarriers(controller.enemies);
        
        for(Iterator<Enemy> i = controller.enemies.get(ACTIVE).iterator(); i.hasNext();)
        {
            Enemy enemy = i.next();
            if(!enemy.isDestroyed() && !enemy.isMarkedForRemoval)
            {
                enemy.update(controller, helicopter);
            }
            else if(enemy.isDestroyed())
            {
                i.remove();
                controller.enemies.get(DESTROYED).add(enemy);
            }
            else
            {
                enemy.clearImage();
                i.remove();
                // controller.enemies.get(INACTIVE).add(enemy);
            }
        }
    }
    
    private static void countBarriers(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        Arrays.fill(livingBarrier, null);
        currentNumberOfBarriers = 0;
        for(Enemy e : enemy.get(ACTIVE))
        {
            if (e.getModel() == BARRIER
                && !e.isDestroyed()
                && !e.isMarkedForRemoval)
            {
                livingBarrier[currentNumberOfBarriers] = e;
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
