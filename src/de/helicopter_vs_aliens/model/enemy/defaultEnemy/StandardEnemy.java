package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

public abstract class StandardEnemy extends Enemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = Colorations.dimColor(this.primaryColor, 1.3f);
        this.secondaryColor = Colorations.dimColor(this.primaryColor, this.dimFactor);
        
        super.create(helicopter);
    }
}
