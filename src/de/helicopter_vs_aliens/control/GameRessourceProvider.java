package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.control.entities.GameEntityRecycler;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.score.Savegame;

import java.util.LinkedList;
import java.util.Map;

public interface GameRessourceProvider
{
    Helicopter getHelicopter();
    
    GameStatisticsCalculator getGameStatisticsCalculator();
    
    Map<CollectionSubgroupType, LinkedList<Enemy>> getEnemies();
    
    Map<CollectionSubgroupType, LinkedList<Missile>> getMissiles();
    
    Map<CollectionSubgroupType, LinkedList<Explosion>> getExplosions();
    
    Map<CollectionSubgroupType, LinkedList<EnemyMissile>> getEnemyMissiles();
    
    Map<CollectionSubgroupType, LinkedList<PowerUp>> getPowerUps();
    
    Scenery getScenery();
    
    public GameEntityRecycler getGameEntityRecycler();
    
    public Savegame getSaveGame();
}
