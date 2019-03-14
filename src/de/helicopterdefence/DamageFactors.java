package de.helicopterdefence;

public interface DamageFactors
{	
	/*
	 * Multiplikatoren, welche den Grundschaden von Raketen und EMPs unter
	 * bestimmten Voraussetzungen erhöhen.
	 */	 
	static final float 
		POWERUP_DAMAGE_FACTOR = 3f,				// Faktor, um den sich die Schadenswirkung von Raketen erhöht, wenn das Bonus-Damage-PowerUp eingesammelt wurde
		RADIATION_DAMAGE_FACTOR = 1.5f,			// Phönix-Klasse, nach Erwerb von Nahkampfbestrahlung: Schaden im Verhältnis zum regulären Raketenschaden, den ein Gegner bei Kollsionen  mit dem Helikopter erleidet 
		TELEPORT_DAMAGE_FACTOR = 4f,			// Phönix-Klasse: wie RADIATION_DAMAGE_FACTOR, aber für Kollisonen unmittelbar nach einem Transportvorgang
		JUMBO_MISSILE_DMG_FACTOR = 2.36363637f,	// Roch-Klasse: Faktor, um den sich die Schadenswirkung der Raketen erhöht, nachdem das Jumbo-Raketen-Spezial-Upgrade erworben wurde 
		OROCHI_XTRA_DMG_FACTOR = 1.03f, 		// Orochi-Klasse: Faktor, um den sich die Schadenswirkung von Raketen erhöht wird
		SHIFT_DAMAGE_FACTOR = 8.9f,				// Pegasus-Klasse: Faktor, um den sich die Schadenswirkung einer Rakete erhöht, wenn diese abgeschossen wird, während der Interphasen-Generator aktiviert ist
		EMP_DAMAGE_FACTOR_BOSS = 1.5f,			// Pegasus-Klasse: Schaden einer EMP-Welle im Verhältnis zum normalen Raketenschaden gegenüber von Boss-Gegnern // 1.5
		EMP_DAMAGE_FACTOR_ORDINARY = 2.5f;		// Pegasus-Klasse: wie EMP_DAMAGE_FACTOR_BOSS, nur für Nicht-Boss-Gegner // 3
}