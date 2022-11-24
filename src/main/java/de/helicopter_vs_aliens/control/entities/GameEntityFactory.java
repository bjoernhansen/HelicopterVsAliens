package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.GameEntity;

public interface GameEntityFactory<T extends GameEntity>
{
    T makeInstance();
    
    // TODO eingeschränkter Wildcard-Typ als Rückgabewert sollte immer vermieden werden (siehe Effective Java)
    Class<? extends T> getCorrespondingClass();
}
