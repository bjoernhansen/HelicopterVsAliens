package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Diese Klasse dient der Verwaltung von gleichzeitig abgeschossenen Raketen. Sie ist ausschließlich für die Klassen
 * Roch und Orochi zur Ermittlung von Extra-Boni relevant.
 */
public class MissileGroupCoordination
{
    private int
        killCount; // Kills dieser Rakete
    
    private int
        earnedMoney; // mit dieser Rakete durch Gegner-Vernichtung verdientes Geld
        
    private final List<MissileGroupCoordination> companions = new ArrayList<>();
    
    private int
        killsByCompanions; // Treffer durch eine andere, gleichzeitig abgefeuerte Rakete
    
    private int
        successfulCompanionCount; // Anzahl anderer, gleichzeitig abgefeuerte Rakete, die wenigstens einen Gegner vernichtet haben
    
    private final Missile
        missile;
    
    
    public MissileGroupCoordination(Missile missile)
    {
        this.missile = missile;
    }
    
    /**
     * Zurücksetzen der Statistik für diese Instanz
     */
    void reset()
    {
        killCount = 0;
        earnedMoney = 0;
        killsByCompanions = 0;
        successfulCompanionCount = 0;
        companions.clear();
    }
    
    private boolean hasQualifiedForFirstCreditOn(Enemy enemy)
    {
        return missile.intersects(enemy) && !hasKilled();
    }
    
    private void assignKill()
    {
        killCount++;
        earnedMoney += Events.lastBonus;
    }
    
    private boolean hasKilled()
    {
        return killCount > 0;
    }
    
    /**
     * Wenn mehrere Raketen gleichzeitig einen Gegner treffen, der durch diese Treffer zerstört wird, dann soll
     * dieser Kill bevorzugt der Rakete zugerechnet werden, die bisher noch keine eigenen tödlichen Treffer hatte.
     *
     * @param killedEnemy         der vernichtete Gegner, der für die Statistik berücksichtigt wird
     * @param hasPiercingWarheads gibt an, ob es sich um eine Rakete mit Durchstoßsprengköpfen handelt
     */
    void assignKillToSelfOrCompanion(Enemy killedEnemy, boolean hasPiercingWarheads)
    {
        if (hasKilled() && hasPiercingWarheads) {
            findAnyCompanionQualifiedForFirstCreditOn(killedEnemy)
                .ifPresentOrElse(
                    MissileGroupCoordination::assignKill, // Begleiter übernimmt den Kill
                    this::assignKill // kein Begleiter qualifiziert: Kill selbst zuweisen
                );
        } else {
            assignKill();
        }
    }
    
    private Optional<MissileGroupCoordination> findAnyCompanionQualifiedForFirstCreditOn(Enemy enemy) {
        return companions.stream()
                         .filter(companion -> companion.hasQualifiedForFirstCreditOn(enemy))
                         .findAny();
    }

    private int getSuccessfulShots()
    {
        return (hasKilled() ? 1 : 0) + successfulCompanionCount;
    }
    
    void inactivateWith(Grantable reward)
    {
        if(companions.isEmpty())
        {
            if(numberOfClusterKills() > 1)
            {
                reward.grant();
            }
        }
        else if(numberOfClusterKills() > 0)
        {
            companions.get(0).receiveAchievementsFrom(this);
        }
        removeFromCluster();
    }
    
    private int numberOfClusterKills()
    {
        return killCount + killsByCompanions;
    }
    
    private void receiveAchievementsFrom(MissileGroupCoordination companion)
    {
        earnedMoney += companion.earnedMoney;
        killsByCompanions += companion.numberOfClusterKills();
        successfulCompanionCount += companion.getSuccessfulShots();
    }
    
    private void removeFromCluster()
    {
        companions.forEach(companion -> companion.disconnectFrom(this));
    }
    
    private void disconnectFrom(MissileGroupCoordination companion)
    {
        companions.remove(companion);
    }
    
    /**
     * Gewährung eines Extra-Belohnung entsprechend der Anzahl von vernichteten Gegner mit Raketen desselben Missile-Clusters
     */
    void grantExtraRewardForMultipleKillsWithSingleShot()
    {
        Events.extraReward(numberOfClusterKills(), earnedMoney, 0.5f, 0.75f, 3.0f);
    }
    
    /**
     * Gewährung einer Extra-Belohnung entsprechend der Anzahl von Raketen aus einem Missile-Cluster, die jeweils
     * mindestens einen Gegner vernichtet haben
     */
    void grantExtraRewardForSuccessfulShots()
    {
        int successfulShots = getSuccessfulShots();
        switch(successfulShots)
        {
            case 1 -> Events.extraReward(numberOfClusterKills(), earnedMoney, 0.25f, 0.0f, 0.25f);
            case 2 -> Events.extraReward(numberOfClusterKills(), earnedMoney, 1.5f, 0.0f, 1.5f);
            case 3 -> Events.extraReward(numberOfClusterKills(), earnedMoney, 4f, 0.0f, 4f);
            default -> throw new IllegalStateException("Number of successful shots has to be between 1 and 3, but was " + successfulShots);
        }
    }
    
    /**
     * Beitreten zu dem Raketen-Verband einer anderen Rakete
     * @param missileGroupCoordination Missile-Cluster-Verwalter-Instanz einer anderen Rakete
     */
    void joinClusterWith(MissileGroupCoordination missileGroupCoordination)
    {
        addCompanion(missileGroupCoordination);
        missileGroupCoordination.addCompanion(this);
    }
    
    private void addCompanion(MissileGroupCoordination companion)
    {
        companions.add(companion);
    }
}
