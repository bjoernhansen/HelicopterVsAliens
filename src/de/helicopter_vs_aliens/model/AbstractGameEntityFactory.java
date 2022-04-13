package de.helicopter_vs_aliens.model;

public abstract class AbstractGameEntityFactory<T extends GameEntity>
{
    abstract public T make();
}
