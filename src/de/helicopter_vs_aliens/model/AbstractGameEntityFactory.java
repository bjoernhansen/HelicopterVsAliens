package de.helicopter_vs_aliens.model;

public abstract class AbstractGameEntityFactory<T extends Paintable>
{
    abstract public  T make();
}
