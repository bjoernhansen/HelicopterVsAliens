package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableFactory;


public final class EnemyMissileFactory implements ManageablePaintableFactory<EnemyMissile>
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
