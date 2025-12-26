package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;

public enum EnemyMissileType
{
	// TODO Verwendungen kontrollieren. Ggf. mit Hilfsmethoden Verwendung abläsen (z.B. isBuster() an Missile
	DISCHARGER(Colorations.bleachedRed),
	BUSTER(Colorations.bleachedViolet);
	
	private final Color
		correspondingBarrierColor;
	
	EnemyMissileType(Color correspondingBarrierColor)
	{
		this.correspondingBarrierColor = correspondingBarrierColor;
	}
	
	public Color getCorrespondingBarrierColor()
	{
		return correspondingBarrierColor;
	}
}