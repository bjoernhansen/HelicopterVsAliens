package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.PaintableEntity;

public interface PaintableEntityFactory<T extends PaintableEntity>
{
    T makeInstance();
    
    // TODO eingeschränkter Wildcard-Typ als Rückgabewert sollte immer vermieden werden (siehe Effective Java)
    Class<? extends T> getCorrespondingClass();
}
