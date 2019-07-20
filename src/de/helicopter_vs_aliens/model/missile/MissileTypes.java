package de.helicopter_vs_aliens.model.missile;

public interface MissileTypes
{	
	// TODO durch Enum ersetzen
	int
		STANDARD = 0,
		PLASMA = 1,
		STUNNING = 2,
		EMP = 3, // missile of type 3 does not exist, but is reserved for EMP
		JUMBO = 4,
		PHASE_SHIFT = 5;
}