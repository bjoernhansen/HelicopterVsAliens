package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.GameEntity;

public interface GameEntityFactory<T extends GameEntity>
{
    T makeInstance();
    
    Class<? extends T> getCorrespondingClass();
}
