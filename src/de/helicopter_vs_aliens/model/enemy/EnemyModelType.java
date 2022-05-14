package de.helicopter_vs_aliens.model.enemy;

public enum EnemyModelType
{
    TIT(0.28),
    CARGO(0.65),
    BARRIER(1.00);
    
    private final double
        heightFactor;   // legt das Verhältnis der Höhe zur Länge fest: width * heightFactor = height
    
    EnemyModelType(double heightFactor)
    {
        this.heightFactor = heightFactor;
    }
    
    public double getHeightFactor()
    {
        return heightFactor;
    }
}
