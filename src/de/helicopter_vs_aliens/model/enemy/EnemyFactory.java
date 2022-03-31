package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.model.AbstractGameEntityFactory;

public class EnemyFactory extends AbstractGameEntityFactory <Enemy>
{
    private static EnemyFactory factory = new EnemyFactory();
    
    public static Enemy createEnemy(){
        return factory.make();
    }
    
    @Override
    public Enemy make()
    {
        return new Enemy();
    }
}
