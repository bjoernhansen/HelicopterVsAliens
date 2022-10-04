package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.enemy.boss.FinalBoss;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.util.Objects;

public class FinalBossPainter extends TitPainter<FinalBoss>
{
    @Override
    protected void paintUncloaked(GraphicsAdapter graphicsAdapter, Helicopter helicopter, int g2DSel)
    {
        super.paintUncloaked(graphicsAdapter, helicopter, g2DSel);
    
        FinalBossServantType.getShieldMaker()
                            .stream()
                            .map(getEnemy()::getOperatorServant)
                            .filter(Objects::nonNull)
                            .filter(Enemy::isShielding)
                            .forEach(servant -> this.paintShieldBeam(graphicsAdapter, servant));
    }
    
    private void paintShieldBeam(GraphicsAdapter graphicsAdapter, Enemy enemy)
    {
        GraphicalEntities.paintGlowingLine(
            graphicsAdapter,
            enemy.getPaintBounds().x + (enemy.getDirectionX() + 1)/2 * enemy.getPaintBounds().width,
            enemy.getPaintBounds().y,
            Events.boss.getPaintBounds().x + Events.boss.getPaintBounds().width/48,
            Events.boss.getPaintBounds().y + Events.boss.getPaintBounds().width/48);
    }
}
