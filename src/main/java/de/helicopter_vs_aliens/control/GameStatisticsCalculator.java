package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.Calculations;

public final class GameStatisticsCalculator
{
    private int numberOfEnemiesKilled; // Anzahl der vernichteten Gegner
    private int numberOfEnemiesSeen; // Anzahl der erschienenen Gegner
    private int numberOfMiniBossKilled; // Anzahl der vernichteten Mini-Bosse
    private int numberOfMiniBossSeen; // Anzahl der erschienenen Mini-Bosse
    private int hitCounter; // Anzahl der getroffenen Gegner
    private int missileCounter; // Anzahl der abgeschossenen Raketen
    private int numberOfRepairs; // Anzahl der Reparaturen
    private int numberOfCrashes; // Anzahl der Abstürze
    
    
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
        numberOfCrashes = 0;
        numberOfRepairs = 0;
        missileCounter = 0;
        hitCounter = 0;
        numberOfEnemiesSeen = 0;
        numberOfEnemiesKilled = 0;
        numberOfMiniBossSeen = 0;
        numberOfMiniBossKilled = 0;
    }
    
    void incrementNumberOfEnemiesSeen()
    {
        numberOfEnemiesSeen++;
    }
    
    public int getKillRate()
    {
        return Calculations.percentage(numberOfEnemiesKilled, numberOfEnemiesSeen);
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
