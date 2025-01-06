package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;

import java.util.Queue;

public interface ActiveManageablePaintableProvider
{
    Queue<Enemy> getIntactEnemies();
    
    Queue<Enemy> getDestroyedEnemies();
    
    Queue<Missile> getMissiles();
    
    Queue<Explosion> getExplosions();
    
    Queue<SceneryObject> getSceneryObjects();
    
    Queue<EnemyMissile> getEnemyMissiles();
    
    Queue<PowerUp> getPowerUps();
}
