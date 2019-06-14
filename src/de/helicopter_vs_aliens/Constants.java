package de.helicopter_vs_aliens;

interface Constants extends Positions
{	
	static final int 		
		ENGLISH = 0, 
		GERMAN = 1,
	
		INACTIVE = 0,
    	ACTIVE = 1,
	    DESTROYED = 2,
	    
	    DISABLED = -1,
	    READY = 0,
	    START = 0,
	    PRE_READY = 1,	    
	    
	    INFORMATIONS = 0,
	    DESCRIPTION = 1,
	    SETTINGS = 2, 
	    CONTACT = 3,  
	    HELICOPTER_TYPES = 4,
	    HIGHSCORE = 5,
	    GAME = 6,
	    REPAIR_SHOP = 7,
	    STARTSCREEN = 8,
	    SCORESCREEN = 9,	    
	    
	    TRIPLE_DMG = 0,
	    INVINCIBLE = 1,
	    UNLIMITRED_ENERGY = 2, 
	    BOOSTED_FIRE_RATE = 3,  
	    REPARATION = 4,
	    BONUS_INCOME = 5,
				
		ROTOR_SYSTEM = 0,
    	MISSILE_DRIVE = 1,
    	PLATING = 2,
    	FIREPOWER = 3,
    	FIRE_RATE = 4,
    	ENERGY_ABILITY = 5,
    	SPELL = 6,
		
		NIGHT = 0,
		DAY = 1,
	
		TIT = 0,
		CARGO = 1, 
		BARRIER = 2;
		
	static final float
		BG_SPEED = 2.0f;
}

interface Positions
{
	static final int
		NONE = -1,
		RIGHT = 0,
		BOTTOM = 1,
		LEFT = 2,
		TOP = 4;
}