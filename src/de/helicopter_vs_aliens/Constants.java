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