package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.entities.PaintableEntityFactory;


public final class EnemyMissileFactory implements PaintableEntityFactory<EnemyMissile>
{
    @Override
    public EnemyMissile makeInstance()
    {
        return new EnemyMissile();
    }
    
    @Override
    public Class<? extends EnemyMissile> getCorrespondingClass()
    {
        return EnemyMissile.class;
    }
}
