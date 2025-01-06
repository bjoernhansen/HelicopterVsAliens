package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableFactory;


public final class MissileFactory implements ManageablePaintableFactory<Missile>
{
    @Override
    public Missile makeInstance()
    {
        return new Missile();
    }
    
    @Override
    public Class<? extends Missile> getCorrespondingClass()
    {
        return Missile.class;
    }
}
