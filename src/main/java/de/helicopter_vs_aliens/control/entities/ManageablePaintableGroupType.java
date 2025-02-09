package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.DestroyedEnemyUpdateController;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.IntactEnemyUpdateController;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionUpdateController;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.EnemyMissileUpdateController;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.missile.MissileUpdateController;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpUpdateController;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.model.scenery.SceneryObjectUpdateController;

import java.util.List;
import java.util.function.Function;


public enum ManageablePaintableGroupType {
    INTACT_ENEMY(Enemy.class, IntactEnemyUpdateController::new),
    DESTROYED_ENEMY(Enemy.class, DestroyedEnemyUpdateController::new),
    MISSILE(Missile.class, MissileUpdateController::new),
    EXPLOSION(Explosion.class, ExplosionUpdateController::new),
    SCENERY_OBJECT(SceneryObject.class, SceneryObjectUpdateController::new),
    ENEMY_MISSILE(EnemyMissile.class, EnemyMissileUpdateController::new),
    POWER_UP(PowerUp.class, PowerUpUpdateController::new);
    
    
    private static final List<ManageablePaintableGroupType>
        VALUES = List.of(values());
        
    private final Class<? extends ManageablePaintable>
        baseClass;
    
    private final Function<ManageablePaintableService, ManageablePaintableUpdateController>
        updateControllerSupplier;
    
    
    ManageablePaintableGroupType(Class<? extends ManageablePaintable> baseClass, Function<ManageablePaintableService, ManageablePaintableUpdateController> updateControllerSupplier) {
        this.baseClass = baseClass;
        this.updateControllerSupplier = updateControllerSupplier;
    }
    
    public static List<ManageablePaintableGroupType> getValues()
    {
        return VALUES;
    }
    
    @SuppressWarnings("unchecked")
    <T extends ManageablePaintable> Class<T> getBaseClass() {
        return (Class<T>)baseClass;
    }
    
    Function<ManageablePaintableService, ManageablePaintableUpdateController> getUpdateControllerSupplier()
    {
        return updateControllerSupplier;
    }
}