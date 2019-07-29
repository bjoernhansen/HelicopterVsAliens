package de.helicopter_vs_aliens.model.enemy;

public interface BossTypes
{
	/* TODO durch Enum ersetzen, hierfür diese Types mit aufnehmen in EnemyTypes, eine Methode einführen isEndBoss
	// soetwas gibt es auch schon (isFinalBoss etc.) zusammenführen
	// EnumSet verwenden
	//
	//private static EnumSet<EnemyTypes> bossTypes =
	//      EnumSet.of(BOSS_1, BOSS_2, ...);
	Testen:
	 bossTypes.contains(this.type)
	*/

	// BOSS 3 und 4 ID verstauschen
	int
		BOSS_1					= -1,
	 	BOSS_2					= -2,
	 	BOSS_2_SERVANT			= -3,
	 	BOSS_3					= -4,
		BOSS_4_SERVANT			= -5,
	 	BOSS_4					= -6,
	 	FINAL_BOSS				= -7,
	 	SMALL_SHIELD_MAKER		= -8,
	 	BIG_SHIELD_MAKER		= -9,
	 	BODYGUARD				= -10,
	 	HEALER					= -11,
	 	PROTECTOR				= -12;
}