package de.helicopter_vs_aliens.model.explosion;

public enum ExplosionType
{
    ORDINARY,
	PLASMA,
	STUNNING,
	EMP,
	JUMBO,
	PHASE_SHIFT;
	
	public boolean isBigExplosion()
	{
		return this == JUMBO || this == PHASE_SHIFT;
	}
	
	public float getBarrierDeactivationProbabilityFactor()
	{
		return this == ExplosionType.PLASMA ? 2 : 1;
	}
}