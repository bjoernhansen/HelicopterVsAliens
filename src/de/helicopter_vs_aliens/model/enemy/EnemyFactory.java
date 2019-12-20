package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.model.AbstractGameEntityFactory;
import de.helicopter_vs_aliens.model.Paintable;

public class EnemyFactory extends AbstractGameEntityFactory <Enemy>
{
    @Override
    public Enemy make()
    {
        return new Enemy();
    }
}
