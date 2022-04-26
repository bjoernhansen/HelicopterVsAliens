package de.helicopter_vs_aliens.model.enemy;


import de.helicopter_vs_aliens.control.entities.GameEntityFactory;

public class EnemyFactory implements GameEntityFactory<Enemy>
{
    private static final EnemyFactory
        factory = new EnemyFactory();
    
    public static Enemy createEnemy(){
        return factory.makeInstance();
    }
    
    @Override
    public Enemy makeInstance()
    {
        return new Enemy();
    }
    
    @Override
    public Class<Enemy> getCorrespondingClass()
    {
        // TODO implementation
        return null;
    }
}
