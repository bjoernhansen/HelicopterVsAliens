package de.helicopter_vs_aliens.model.explosion;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableFactory;


public final class ExplosionFactory implements ManageablePaintableFactory<Explosion>
{
    @Override
    public Explosion makeInstance()
    {
        return new Explosion();
    }
    
    @Override
    public Class<? extends Explosion> getCorrespondingClass()
    {
        return Explosion.class;
    }
}
