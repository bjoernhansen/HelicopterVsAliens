package de.helicopter_vs_aliens.model.enemy.basic;

public class CallbackEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.callBack = 1;
    
        super.doTypeSpecificInitialization();
    }
}
