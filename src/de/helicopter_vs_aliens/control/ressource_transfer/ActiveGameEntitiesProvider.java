package de.helicopter_vs_aliens.control.ressource_transfer;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public interface ActiveGameEntitiesProvider
{
    Map<CollectionSubgroupType, Queue<Enemy>> getEnemies();
    
    Map<CollectionSubgroupType, Queue<Missile>> getMissiles();
    
    Map<CollectionSubgroupType, Queue<Explosion>> getExplosions();
    
    Map<CollectionSubgroupType, Queue<SceneryObject>> getSceneryObjects();
    
    Map<CollectionSubgroupType, Queue<EnemyMissile>> getEnemyMissiles();
    
    Map<CollectionSubgroupType, Queue<PowerUp>> getPowerUps();
}
