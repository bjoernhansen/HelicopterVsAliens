package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.graphics.painter.enemy.BarrierPainter;
import de.helicopter_vs_aliens.graphics.painter.enemy.CargoPainter;
import de.helicopter_vs_aliens.graphics.painter.enemy.TitPainter;
import de.helicopter_vs_aliens.graphics.painter.enemy.EnemyPainter;
import de.helicopter_vs_aliens.model.Paintable;

import java.util.List;
import java.util.function.Supplier;

public enum EnemyModelType
{
    TIT(0.28, TitPainter::new),
    CARGO(0.65, CargoPainter::new),
    BARRIER(1.00, BarrierPainter::new);
    
    
    private static final List<EnemyModelType>
        VALUES = List.of(values());
    
    private final double
        heightFactor;   // legt das Verhältnis der Höhe zur Länge fest: width * heightFactor = height
    
    private final Supplier<? extends EnemyPainter<? extends Enemy>>
        painterSupplier;
    
    EnemyModelType(double heightFactor, Supplier<? extends EnemyPainter<? extends Enemy>> painterSupplier)
    {
        this.heightFactor = heightFactor;
        this.painterSupplier = painterSupplier;
    }
    
    public static List<EnemyModelType> getValues()
    {
        return VALUES;
    }
    
    public double getHeightFactor()
    {
        return heightFactor;
    }
    
    public Painter<? extends Paintable> makePainterInstance()
    {
        return painterSupplier.get();
    }
}
