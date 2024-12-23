package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;


/**
 * Diese Klasse dient der Verwaltung von gleichzeitig abgeschossenen Raketen. Sie ist ausschließlich für die Klassen
 * Roch und Orochi zur Ermittlung von Extra-Boni relevant.
 */
public class MissileGroupCoordination
{
    // nur für Roch- und Orochi-Klasse relevant
    private int
        kills;
    
    private int
        earnedMoney;	// mit dieser Rakete durch Gegner-Vernichtung verdientes Geld
    
    private final MissileGroupCoordination []
        sister = new MissileGroupCoordination [2];	// nur für Roch- und Orochi Klasse: Schwesterraketen (werden gleichzeitig abgefeuert)
    
    private int
        sisterKills;			// nur Orochi Klasse: Kills der (gleichzeitig abgefeuerten) Schwesterrakete(n)
    
    private int
        nrOfHittingSisters;	// Anzahl der Schwesterraketen, die wenigstens einen Gegner vernichtet haben
    
    private final Missile
        missile;
    
    
    public MissileGroupCoordination(Missile missile)
    {
        this.missile = missile;
    }
    
    void reset()
    {
        kills = 0;
        earnedMoney = 0;
        sisterKills = 0;
        nrOfHittingSisters = 0;
        sister[0] = null;
        sister[1] = null;
    }
    
    private boolean atLeastOneSisterHasQualifiedForFirstCreditOn(Enemy enemy)
    {
        return hasFirstSisterQualifiedForFirstCreditOn(enemy)
            || hasSecondSisterQualifiedForFirstCreditOn(enemy);
    }
    
    private boolean hasFirstSisterQualifiedForFirstCreditOn(Enemy enemy)
    {
        return sister[0] != null && sister[0].hasQualifiedForFirstCreditOn(enemy);
    }
    
    private boolean hasSecondSisterQualifiedForFirstCreditOn(Enemy enemy)
    {
        return sister[1] != null && sister[1].hasQualifiedForFirstCreditOn(enemy);
    }
    
    private boolean hasQualifiedForFirstCreditOn(Enemy enemy)
    {
        return missile.intersects(enemy) && !hasKilled();
    }
    
    private void creditFirstSister()
    {
        sister[0].credit();
    }
    
    private void creditSecondSister()
    {
        sister[1].credit();
    }
    
    private void credit()
    {
        kills++;
        earnedMoney += Events.lastBonus;
    }
    
    private boolean hasKilled()
    {
        return kills > 0;
    }
    
    void creditItselfOrSisterOn(Enemy enemy, boolean hasPiercingWarheads)
    {
        if(hasKilled()
            && hasPiercingWarheads
            && atLeastOneSisterHasQualifiedForFirstCreditOn(enemy))
        {
            if(hasFirstSisterQualifiedForFirstCreditOn(enemy))
            {
                creditFirstSister();
            }
            else if(hasSecondSisterQualifiedForFirstCreditOn(enemy))
            {
                creditSecondSister();
            }
        }
        else
        {
            credit();
        }
    }
    
    private int numberOfCountedKills()
    {
        return kills + sisterKills;
    }
    
    private int getNonFailedShots()
    {
        return (hasKilled() ? 1 : 0) + nrOfHittingSisters;
    }
    
    void helicopterTypeSpecificInactivation(Grantable typeSpecificReward)
    {
        if(isOnlyChild())
        {
            if(numberOfCountedKills() > 1)
            {
                typeSpecificReward.grant();
            }
        }
        else if(numberOfCountedKills() > 0)
        {
            transferAchievementsToAnyOtherSister();
        }
        disconnectFromSisters();
    }
    
    private void transferAchievementsToAnyOtherSister()
    {
        for(int j = 0; true; j++)
        {
            if(sister[j] != null)
            {
                sister[j].earnedMoney += earnedMoney;
                sister[j].sisterKills += numberOfCountedKills();
                sister[j].nrOfHittingSisters += getNonFailedShots();
                break;
            }
        }
    }
    
    private void disconnectFromSisters()
    {
        for(int j = 0; j < 2; j++)
        {
            if(sister[j] != null)
            {
                if(sister[j].sister[0] == this)
                {
                    sister[j].sister[0] = null;
                }
                else if(sister[j].sister[1] == this)
                {
                    sister[j].sister[1] = null;
                }
                else
                {
                    throw new IllegalStateException("If a sister exists then it has to point to this missile.");
                }
            }
        }
    }
    
    void grantExtraRewardForMultipleKillsWithSingleShot()
    {
        Events.extraReward(numberOfCountedKills(), earnedMoney, 0.5f, 0.75f, 3.0f);
    }
    
    private boolean isOnlyChild()
    {
        return sister[0] == null && sister[1] == null;
    }
    
    void grantExtraRewardForNonFailedShots()
    {
        int nonFailedShots = getNonFailedShots();
        if(nonFailedShots == 1)
        {
            Events.extraReward(numberOfCountedKills(), earnedMoney, 0.25f, 0.0f, 0.25f);
        }
        if(nonFailedShots == 2)
        {
            Events.extraReward(numberOfCountedKills(), earnedMoney, 1.5f, 0.0f, 1.5f);
        }
        else if(nonFailedShots == 3)
        {
            Events.extraReward(numberOfCountedKills(), earnedMoney, 4f, 0.0f, 4f);
        }
        else
        {
            throw new IllegalStateException("Number of non failed shots has to be between 1 and 3, but was " + nonFailedShots);
        }
    }
    
    void joinClusterWith(MissileGroupCoordination missileGroupCoordination)
    {
        addSister(missileGroupCoordination);
        missileGroupCoordination.addSister(this);
    }
    
    private void addSister(MissileGroupCoordination missileGroupCoordination)
    {
        if(sister[0] == null)
        {
            sister[0] = missileGroupCoordination;
        }
        else if(sister[1] == null)
        {
            sister[1] = missileGroupCoordination;
        }
        else {
            throw new UnsupportedOperationException("Adding of more than 2 sisters is not supported!");
        }
    }
}
