package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;

import java.util.List;


public enum ManageablePaintableGroupType {
    INTACT_ENEMY(Enemy.class),
    DESTROYED_ENEMY(Enemy.class),
    MISSILE(Missile.class),
    EXPLOSION(Explosion.class),
    SCENERY_OBJECT(SceneryObject.class),
    ENEMY_MISSILE(EnemyMissile.class),
    POWER_UP(PowerUp.class);
    
    
    private static final List<ManageablePaintableGroupType>
        VALUES = List.of(values());
        
    private final Class<? extends ManageablePaintable> baseClass;
    
    ManageablePaintableGroupType(Class<? extends ManageablePaintable> baseClass) {
        this.baseClass = baseClass;
    }
    
    @SuppressWarnings("unchecked")
    <T extends ManageablePaintable> Class<T> getBaseClass() {
        return (Class<T>)baseClass;
    }
    
    static List<ManageablePaintableGroupType> getValues()
    {
        return VALUES;
    }
}