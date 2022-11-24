package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.Calculations;

public final class GameStatisticsCalculator
{
    private int
        numberOfEnemiesKilled,              // Anzahl der vernichteten Gegner
        numberOfEnemiesSeen,                // Anzahl der erschienenen Gegner
        numberOfMiniBossKilled,             // Anzahl der vernichteten Mini-Bosse
        numberOfMiniBossSeen,               // Anzahl der erschienenen Mini-Bosse
        hitCounter,                         // Anzahl der getroffenen Gegner
        missileCounter,                     // Anzahl der abgeschossenen Raketen
        numberOfRepairs,                    // Anzahl der Reparaturen
        numberOfCrashes;                    // Anzahl der Abstürze
    
    
    private final static GameStatisticsCalculator
        instance = new GameStatisticsCalculator();
    
    static GameStatisticsCalculator getInstance(){
        return instance;
    }
 
    private GameStatisticsCalculator() {}
    
    public void restoreFrom(Savegame savegame)
    {
        numberOfEnemiesSeen = savegame.enemiesSeen;
        numberOfEnemiesKilled = savegame.enemiesKilled;
        numberOfMiniBossSeen = savegame.miniBossSeen;
        numberOfMiniBossKilled = savegame.miniBossKilled;
        numberOfCrashes = savegame.numberOfCrashes;
        numberOfRepairs = savegame.numberOfRepairs;
        missileCounter = savegame.missileCounter;
        hitCounter = savegame.hitCounter;
    }
    
    public void resetCounterForHighscore()
    {
        this.numberOfCrashes = 0;
        this.numberOfRepairs = 0;
        this.missileCounter = 0;
        this.hitCounter = 0;
        this.numberOfEnemiesSeen = 0;
        this.numberOfEnemiesKilled = 0;
        this.numberOfMiniBossSeen = 0;
        this.numberOfMiniBossKilled = 0;
    }
    
    public void incrementNumberOfEnemiesSeen()
    {
        numberOfEnemiesSeen++;
    }
    
    public int getKillRate()
    {
        return Calculations.percentage(numberOfEnemiesSeen, numberOfEnemiesKilled);
    }
    
    public int getNumberOfEnemiesSeen()
    {
        return numberOfEnemiesSeen;
    }
    
    public int getMissileHitRate()
    {
        return Calculations.percentage(hitCounter, missileCounter);
    }
    
    public int getMiniBossKillRate()
    {
        return Calculations.percentage(numberOfMiniBossKilled, numberOfMiniBossSeen);
    }
    
    public int getNumberOfEnemiesKilled()
    {
        return numberOfEnemiesKilled;
    }
    
    public void incrementNumberOfEnemiesKilled()
    {
        numberOfEnemiesKilled++;
    }
    
    public int getNumberOfMiniBossKilled()
    {
        return numberOfMiniBossKilled;
    }
    
    public void incrementNumberOfMiniBossKilled()
    {
        numberOfMiniBossKilled++;
    }
    
    public void incrementNumberOfMiniBossSeen()
    {
        numberOfMiniBossSeen++;
    }
    
    public int getNumberOfMiniBossSeen()
    {
        return numberOfMiniBossSeen;
    }
    
    public void incrementHitCounter()
    {
        hitCounter++;
    }
    
    public int getHitCounter()
    {
        return hitCounter;
    }
    
    public int getMissileCounter()
    {
        return missileCounter;
    }
    
    public int getNumberOfRepairs()
    {
        return numberOfRepairs;
    }
    
    public int getNumberOfCrashes()
    {
        return numberOfCrashes;
    }
    
    public void incrementMissileCounterBy(int numberOfCannons)
    {
        missileCounter += numberOfCannons;
    }
    
    public void incrementNumberOfRepairs()
    {
        numberOfRepairs++;
    }
    
    public void incrementNumberOfCrashes()
    {
        numberOfCrashes++;
    }
}
