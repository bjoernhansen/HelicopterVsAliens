package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.util.Colorations;

public abstract class StandardEnemy extends Enemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = Colorations.dimColor(this.primaryColor, 1.3f);
        this.secondaryColor = Colorations.dimColor(this.primaryColor, this.dimFactor);
    }
}
