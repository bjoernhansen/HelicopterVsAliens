package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.control.entities.GameEntitySupplier;
import de.helicopter_vs_aliens.model.GameEntity;
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

public interface GameRessourceProvider extends ActiveGameEntityProvider
{
    Helicopter getHelicopter();
    
    GameStatisticsCalculator getGameStatisticsCalculator();
    
    Scenery getScenery();
    
    GameEntitySupplier getGameEntitySupplier();
    
    Savegame getSaveGame();
    
    <T extends GameEntity> T getNewGameEntityInstance(GameEntityFactory<T> factory);
}