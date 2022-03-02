package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.model.AbstractGameEntityFactory;

public class EnemyFactory extends AbstractGameEntityFactory <Enemy>
{
    @Override
    public Enemy make()
    {
        return new Enemy();
    }
}
