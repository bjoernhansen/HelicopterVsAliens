package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.enemy.AbilityStatusType;
import de.helicopter_vs_aliens.model.enemy.basic.CapturingEnemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;


public class CapturingEnemyPainter extends TitPainter<CapturingEnemy>
{
    @Override
    protected void paintAnimatedElements(GraphicsAdapter graphicsAdapter)
    {
        super.paintAnimatedElements(graphicsAdapter);
        
        // TODO Methode isTractorBeamActive einführen und überall austauschen
        if(getEnemy().getTractor() == AbilityStatusType.ACTIVE)
        {
            this.paintTractorBeam(graphicsAdapter);
        }
    }
    
    private void paintTractorBeam(GraphicsAdapter graphicsAdapter)
    {
        Helicopter helicopter = Controller.getInstance().getHelicopter();
        
        int destinationX =
            (int)(helicopter.getX()
            + (helicopter.isMovingLeft
                ? Helicopter.FOCAL_POINT_X_LEFT
                : Helicopter.FOCAL_POINT_X_RIGHT));
    
        int destinationY =
            (int)(helicopter.getY() + Helicopter.FOCAL_POINT_Y_EXP);
        
        GraphicalEntities.paintGlowingLine(
            graphicsAdapter,
            getEnemy().getPaintBounds().x,
            getEnemy().getPaintBounds().y + 1,
            destinationX,
            destinationY);
    }
}
