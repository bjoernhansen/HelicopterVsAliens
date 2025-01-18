package de.helicopter_vs_aliens.model.explosion;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableController;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableFactory;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Pegasus;

import java.util.Iterator;
import java.util.Queue;

import static de.helicopter_vs_aliens.gui.window.Window.*;


public class ExplosionController
{
    private static final ManageablePaintableFactory<Explosion>
        EXPLOSION_FACTORY = new ExplosionFactory();
    
    
    private final GameRessourceProvider gameRessourceProvider;
    
    public ExplosionController(GameRessourceProvider gameRessourceProvider) {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    public Explosion createStartScreenExplosion(int i)
    {
        return new Explosion(
            149
                + START_SCREEN_OFFSET_X
                + i * HELICOPTER_DISTANCE,
            310
                + START_SCREEN_HELICOPTER_OFFSET_Y);
    }
    
/*    public void updateAll()
    {
        ManageablePaintableController manageablePaintableController = gameRessourceProvider.getManageablePaintableController();
        // TODO der PaintableEntityController könnte ggf. eine Hilfsklasse zurückgeben, die dann bereits typ spezifisch ist, so müsste nicht jedes mal wieder der Group-Type übergeben werden
        manageablePaintableController.forEachActiveEntity(ManageablePaintableGroupType.EXPLOSION,
                                                          explosion -> ((Explosion)explosion).update());
        manageablePaintableController.forEachActiveEntityIf(ManageablePaintableGroupType.EXPLOSION,
                                                            explosion -> ((Explosion)explosion).onExplosionEnd(),
                                                            explosion -> ((Explosion)explosion).hasExpired());
        manageablePaintableController.removeIf(ManageablePaintableGroupType.EXPLOSION,
                                               explosion -> ((Explosion)explosion).hasExpired());
    }*/
    
    public void updateAll()
    {
        Queue<Explosion> explosions = gameRessourceProvider.getManageablePaintableController()
                                                           .getExplosions();
        for(Iterator<Explosion> explosionIterator = explosions.iterator(); explosionIterator.hasNext(); )
        {
            Explosion explosion = explosionIterator.next();
            explosion.update();
            if(explosion.hasExpired())
            {
                explosionIterator.remove();
                explosion.onExplosionEnd();
            }
        }
    }
    
    public void start(double x, double y,
                      ExplosionType explosionType,
                      boolean extraDamage)
    {
        start(x, y, explosionType, extraDamage, null);
    }
    
    public void start(
        double x, double y,
        ExplosionType explosionType,
        boolean extraDamage,
        Enemy source)
    {
        Explosion explosion = gameRessourceProvider.getManageablePaintableController()
                                                   .activateEntity(EXPLOSION_FACTORY);
        explosion.initialize(x, y, explosionType, extraDamage, source);
    }
}
