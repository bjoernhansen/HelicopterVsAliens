package de.helicopter_vs_aliens.model.explosion;

import de.helicopter_vs_aliens.control.entities.PaintableEntityFactory;


public final class ExplosionFactory implements PaintableEntityFactory<Explosion>
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
