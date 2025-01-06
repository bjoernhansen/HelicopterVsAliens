package de.helicopter_vs_aliens.control.entities;

public interface ManageablePaintableFactory<T extends ManageablePaintable>
{
    T makeInstance();
    
    Class<? extends T> getCorrespondingClass();
}
