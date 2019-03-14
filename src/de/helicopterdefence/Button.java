package de.helicopterdefence;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

interface PriceLevels
{
	static final int
		VERY_CHEAP = 0,
		CHEAP = 1,
		REGULAR = 2,
		EXPENSIVE = 3,
		EXTORTIONATE = 4;
}

class Button implements Constants, Fonts, Costs, PriceLevels
{
	private static final int 
		BUTTON_DISTANCE = 60;
	
	static final Point 
		STANDARD_UPGRADE_LOCATION = new Point(559, 95);
		
	private static final Dimension 
		UPGRADE_BUTTON_SIZE = new Dimension(193, 50);
	    
    // Beschriftungen in deutscher und englischer Sprache
	static final String   
		REPAIR[] = 			 {"Repair helicopter", "Reparatur durchf�hren"},
		PRICE[]	= 			 {"Price: ", "Kosten: "},
		REPAIR_SHOP[] =		 {"Repair shop", "zur Werkstatt"},
		MAIN_MENU[] = 		 {"Main menu", "Hauptmen�"},
		START_NEW_GAME[] = 	 {"Start a new game", "Neues Spiel starten"},
		QUIT[] = 			 {"Quit", "Spiel beenden"},
		CANCEL[] = 			 {"Cancel", "zur�ck"},	
		MISSION[][] = 		 {{"Start overnight mission", "Start daytime mission"},
                             {"Nachteinsatz starten", "Tageinsatz starten"}},
    	SOLD[][] = 			{{"Low salary", "High salary"}, 
                             {"geringer Sold", "hoher Sold"}},
        DISPLAY[][] = 		{{"Window mode", "Fullscreen mode"},
                             {"Fenstermodus", "Vollbildmodus"}},
        ANTIALIAZING[][] =  {{"Turn off antialiasing", "Turn on antialiasing" }, 
                             {"Kantengl�ttung aus", "Kantengl�ttung an"}},
        MUSIC[][] = 		{{"Turn off music", "Turn on music" }, 
                             {"Musik ausschalten", "Musik einschalten"}},
	
		STARTSCREEN_MENU_BUTTON[][][] 
			=	{{{"Plot", "Changes since 1.0", "Game instructions", 
				   "Changes since 1.1", "Credits", "Copyright", "", ""},    															
				  {"Game description", "Finances & Repair", "Upgrades", 
				   "Boss-Enemies", "Controls", "Power-ups", "Helicopter types",
				   "Hardcore mode"},   													
				  {"Window mode", "Turn off antialiasing", "Turn off music", 
				   "German", "Player name", "",
				   Audio.MICHAEL_MODE ? "Change music mode": "", ""}, 
				  {"Contact", "", "", "", "", "", "", ""},
				  {"General information", "Upgrade costs", "Ph�nix",  "Roch", 
				   "Orochi", "Kamaitachi", "Pegasus", "Helios"},
				  {"Record times", "Overall", "Phoenix",  "Roch", "Orochi", 
				   "Kamaitachi", "Pegasus", "Helios"}},
				 {{"Handlung", "�nderungen seit 1.0", "Spielanleitung", 
				   "�nderungen seit 1.1", "Credits", "Copyright", "", ""},    															
				  {"Spielbeschreibung", "Finanzen/Reparatur", "Upgrades", 
				   "Boss-Gegner", "Bedienung", "PowerUps", "Helikopter-Klassen",
				   "Hardcore-Modus"},   													
				  {"Fenstermodus", "Antialiasing aus", "Musik ausschalten", 
				   "Englisch", "Spielername", "",
				   Audio.MICHAEL_MODE ? "Musik �ndern": "", ""}, 
				  {"Kontakt", "", "", "", "", "", "", ""},
				  {"Allgemeines", "Upgrade-Kosten","Ph�nix", "Roch",  "Orochi",
				   "Kamaitachi", "Pegasus", "Helios"},
				  {"Bestzeiten", "Gesamt", "Ph�nix",  "Roch", "Orochi", 
				   "Kamaitachi", "Pegasus", "Helios"}}}, 

		STANDARD_UPGRADE_LABEL[][][]
			= 	{{{"Improve", "Rotor system"}, 
    			  {"Improve", "Missile drive"}, 
    			  {"Fortify", "Plating"}, 
    			  {"Boost", "Firepower"},
    			  {"Increase", "Fire rate"}, 
    			  {"Improve", "Energy ability"},     			
    			  {"Enhance", "Teleporter"},
    			  {"Strengthen", "Energy shield"},
    			  {"Upgrade", "Stunning missile"},
    			  {"Upgrade", "Plasma missile"},
    			  {"Improve", "EMP generator"},
    			  {"Improve", "PU generator"}},
                 {{"Hauptrotor", "verbessern"}, 
    			  {"Raketenantrieb", "erh�hen"}, 
    			  {"Panzerung", "vest�rken"}, 
    			  {"Feuerkraft", "erh�hen"}, 
    			  {"Schussrate", "steigern"}, 
    			  {"Energie-UPs", "verbessern"},
    			  {"Teleporter", "verbessern"},
    			  {"Energie-Schild", "aufwerten"},
    			  {"Stopp-Rakete", "aufwerten"},
    			  {"Plasma-Rakete", "aufwerten"},
    			  {"EMP-Generator", "steigern"},
    			  {"PU-Generator", "verbessern"}}},   
    			  
    	STARTSCREEN_BUTTON_LABEL[][][]  
    		= 	{{{"Informations",  "Highscore", "Contact"}, 
    			  {"Settings", "Resume last game", "Quit"}},
                 {{"Informationen", "Highscore", "Kontakt"}, 
    			  {"Einstellungen", "Letztes Spiel fortsetzen", "Beenden"}}};
    
	int costs;				// Preis, falls es ein Kauf-Button in der Werkstatt ist	
	 	
	boolean 
		highlighted,	// = true: animierter Button; wird = true gesetzt, wenn Maus �ber Button f�hrt 
		enabled,
		marked;			// farbliche Hervorhebungen bei besonderer Funktion
	
	String 
		label,			// Beschriftung
		second_label;	// zweite Beschriftung, falls vorhanden
	
	Color 
		cost_color;		// Farbe, falls es ein Upgrade-Button in der Werkstatt ist	
	
	Rectangle2D 
		bounds;		// Ma�e und Koordinaten des Buttons
	
	private boolean 
		cost_button,	// = true: Kaufbutton
		translucent;	// = true: durchsichtig
	
	
    private Button(int x, int y, int width, int height, String label, String second_label, boolean cost_button, boolean translucent)
	{
		this.bounds = new Rectangle2D.Float(x, y, width, height);
		this.label = label;	
		this.second_label = second_label;
		this.costs = 0;
		this.cost_button = cost_button;
		this.cost_color = null;
		this.translucent = translucent;
		this.highlighted = false;
		this.enabled = true;
		this.marked = false;
	}		
	
    // Erstellen und Pre-Initialisieren der Buttons
	static void initialize_buttons(Helicopter helicopter)
	{
		Menu.repair_shop_button.put("RepairButton", new Button(23, 287, 205, 50, REPAIR[Menu.language], PRICE[Menu.language], true, true));		
		Menu.repair_shop_button.put("Einsatz", new Button(23, 395, 205, 50, MISSION[Menu.language][Events.timeOfDay], SOLD[Menu.language][helicopter.spotlight ? 1 : 0], false, true));
		Menu.inGame_button.put("RepairShop",   new Button(451, 431, 121, 25, REPAIR_SHOP[Menu.language], null, false, false));
		Menu.inGame_button.put("MainMenu",     new Button(897, 431, 121, 25, MAIN_MENU[Menu.language], null, false, false));
		Menu.inGame_button.put("MMNewGame1",   new Button(385, 116, 211, 35, START_NEW_GAME[Menu.language], null, false, false));
		Menu.inGame_button.put("MMStopMusic",  new Button(385, 161, 211, 35, MUSIC[Menu.language][Audio.sound_on ? 0 : 1], null, false, false));
		Menu.inGame_button.put("MMNewGame2",   new Button(385, 206, 211, 35, QUIT[Menu.language], null, false, false));
		Menu.inGame_button.put("MMCancel",     new Button(385, 251, 211, 35, CANCEL[Menu.language], null, false, false));
				
		for(int i = 0; i < 2; i++){for(int j = 0; j < 3; j++)
		{
			Menu.startscreen_button.put( Integer.toString(i)+j, 
										 new Button(  27 + Menu.STARTSCREEN_OFFSET_X + i * 750, 
												 	 110 + j * 40, 211, 30, 
												 	 STARTSCREEN_BUTTON_LABEL[Menu.language][i][j], 
												 	 null, false, true));
		}}
		for(int i = 0; i < 6; i ++)
		{
			Menu.repair_shop_button.put( "StandardUpgrade" + i, 
										new Button(	STANDARD_UPGRADE_LOCATION.x, 
										STANDARD_UPGRADE_LOCATION.y + i * BUTTON_DISTANCE, 
										UPGRADE_BUTTON_SIZE.width, UPGRADE_BUTTON_SIZE.height, 
										STANDARD_UPGRADE_LABEL[Menu.language][i][0] + " " 
										+ STANDARD_UPGRADE_LABEL[Menu.language][i][1],
										PRICE[Menu.language], true, true));
		}
		for(int i = 0; i < 5; i++)
		{
			Menu.repair_shop_button.put("Special" + i, new Button(771, 155 + i * 60, 184, 50, Menu.SPECIALS[Menu.language][i],  PRICE[Menu.language], true, true));		
		}		
		for(int m = 0; m < 8; m++)
		{				
			int i = m/2, j = m%2;			
			Menu.startscreen_menu_button.put(Integer.toString(m), new Button( 23 + i * 160, 370 + j * 40, 150, 30, "", null, false, true));
		}
				
		Menu.startscreen_button.get("11").marked = true;   // Der "Letztes Spiel fortsetzen"-Button ist markiert 
		Menu.startscreen_button.get("11").enabled = HelicopterDefence.savegame.valid ? true : false;
		
		Menu.startscreen_menu_button.put("Cancel", new Button( 849, 410, 150, 30, CANCEL[Menu.language], null, false, true));
		
				
		if(HighscoreEntry.current_player_name.equals("John Doe"))
		{
			Menu.startscreen_button.get("10").marked = true;
		}
	}
    
	// Helicopter-spezifische Anpassung der Werkstatt-Button-Beschriftungen
	static void initialize(Helicopter helicopter)
	{
		Menu.repair_shop_button.get("Einsatz").label = MISSION[Menu.language][Events.timeOfDay];
		Menu.repair_shop_button.get("Einsatz").second_label = SOLD[Menu.language][helicopter.spotlight ? 1 : 0];
		
		for(int i = 0; i < 6; i++)
    	{    		
			if(!helicopter.has_max_upgrade_level[i])
			{
				Menu.repair_shop_button.get("StandardUpgrade" + i).costs = MyMath.costs(helicopter.type, helicopter.upgrade_costs[i], helicopter.level_of_upgrade[i]);
				Menu.repair_shop_button.get("StandardUpgrade" + i).cost_color = MyColor.costsColor[helicopter.upgrade_costs[i]];
			}
    	}
		Menu.repair_shop_button.get("StandardUpgrade" + 5).label 
			= STANDARD_UPGRADE_LABEL[Menu.language][6 + helicopter.type][0] 
			  + " " 
			  + STANDARD_UPGRADE_LABEL[Menu.language][6 + helicopter.type][1];		
		Menu.repair_shop_button.get("Special" + 0).costs = SPOTLIGHT_COSTS;
		Menu.repair_shop_button.get("Special" + 0).cost_color = MyColor.costsColor[CHEAP];
		Menu.repair_shop_button.get("Special" + 1).costs = (helicopter.type == PHOENIX || (helicopter.type == HELIOS && Events.record_time[PHOENIX][4] != 0)) ? PHOENIX_GOLIATH_COSTS  : STANDARD_GOLIATH_COSTS ;
		Menu.repair_shop_button.get("Special" + 1).cost_color = (helicopter.type == PHOENIX || (helicopter.type == HELIOS && Events.record_time[PHOENIX][4] != 0)) ? MyColor.costsColor[VERY_CHEAP] : MyColor.costsColor[REGULAR];
		Menu.repair_shop_button.get("Special" + 2).costs = (helicopter.type == ROCH || (helicopter.type == HELIOS && Events.record_time[ROCH][4] != 0)) ? CHEAP_SPECIAL_COSTS  : STANDARD_SPECIAL_COSTS ;
		Menu.repair_shop_button.get("Special" + 2).cost_color = (helicopter.type == ROCH || (helicopter.type == HELIOS && Events.record_time[ROCH][4] != 0)) ? MyColor.costsColor[VERY_CHEAP] : MyColor.costsColor[REGULAR];
		Menu.repair_shop_button.get("Special" + 3).costs = (helicopter.type == OROCHI || (helicopter.type == HELIOS && Events.record_time[OROCHI][4] != 0)) ? CHEAP_SPECIAL_COSTS  : helicopter.type == ROCH ? ROCH_SECOND_CANNON_COSTS  : STANDARD_SPECIAL_COSTS ;
		Menu.repair_shop_button.get("Special" + 3).cost_color = (helicopter.type == OROCHI || (helicopter.type == HELIOS && Events.record_time[OROCHI][4] != 0)) ? MyColor.costsColor[VERY_CHEAP] : helicopter.type == ROCH ? MyColor.costsColor[EXPENSIVE] : MyColor.costsColor[REGULAR];
		Menu.repair_shop_button.get("Special" + 3).label = Menu.SPECIALS[Menu.language][3];
		Menu.repair_shop_button.get("Special" + 4).costs = helicopter.type != ROCH ? CHEAP_SPECIAL_COSTS : JUMBO_MISSILE_COSTS ;
		Menu.repair_shop_button.get("Special" + 4).cost_color = helicopter.type != ROCH ? MyColor.costsColor[VERY_CHEAP] : MyColor.costsColor[CHEAP];
		Menu.repair_shop_button.get("Special" + 4).label = Menu.SPECIALS[Menu.language][4 + helicopter.type];
	}
	
	void paint(Graphics2D g2d){paint(g2d, null, false);}
	void paint(Graphics2D g2d, String button_label){paint(g2d, button_label, false);}
	void paint(Graphics2D g2d, boolean loner){paint(g2d, null, loner);}
	private void paint(Graphics2D g2d, String button_label, boolean loner)
	{
    	if(!this.label.equals("") && !loner)
    	{
			String used_label = button_label != null ? button_label : this.label;		
			if((this.highlighted && this.enabled) || !this.translucent)
			{
				g2d.setPaint(this.highlighted ? MyColor.gradientVariableGray : MyColor.lightestGray);
				g2d.fill(this.bounds);
			}
	    	g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
	    	if(this.cost_color == null){g2d.setColor(this.enabled ? this.marked ? MyColor.variableWhite : Color.white : MyColor.lightGray);}
	        else{g2d.setColor(this.cost_color);}  
	    	if(this.translucent){g2d.draw(this.bounds);}
	    	else
	    	{
	    		g2d.drawLine((int)this.bounds.getX(), (int)this.bounds.getY(), (int)(this.bounds.getX() + this.bounds.getWidth()), (int)this.bounds.getY());
	        	g2d.drawLine((int)this.bounds.getX(), (int)this.bounds.getY(), (int)(this.bounds.getX()), (int)(this.bounds.getY()+this.bounds.getHeight()));
	        	if(this.cost_color == null){g2d.setColor(MyColor.gray);}
	            else{g2d.setColor(this.cost_color);}    	
	        	g2d.drawLine((int)(this.bounds.getX()+this.bounds.getWidth()), (int)this.bounds.getY(), (int)(this.bounds.getX() + this.bounds.getWidth()), (int)(this.bounds.getY()+this.bounds.getHeight()));
	        	g2d.drawLine((int)this.bounds.getX(), (int)(this.bounds.getY()+this.bounds.getHeight()), (int)(this.bounds.getX() + this.bounds.getWidth()), (int)(this.bounds.getY()+this.bounds.getHeight()));
	    	}
	        g2d.setStroke(new BasicStroke(1));        
	        	        
	        if(this.second_label != null || this.cost_button)
	        {	        	
	        	if(this.cost_color == null){g2d.setColor(MyColor.lightOrange);}
	            else{g2d.setColor(this.cost_color);}
	            
	        	g2d.setFont(BOLD14);        	          
	        	g2d.drawString(used_label, (int)this.bounds.getX() + 7, (int)this.bounds.getY() + 20);
	        }
	        else
	        {
	        	if(this.translucent)
	        	{
	        		g2d.setFont(BOLD15);	        		
	        		if(this.enabled){g2d.setColor(this.marked ? MyColor.variableMarkedButton : Color.yellow); }
		        	else{g2d.setColor(MyColor.lightGray);} 		
	        	}
	        	else
	        	{
	        		g2d.setFont(PLAIN18); 
	        		g2d.setColor(Color.black);
	        	}        		
	        	FontMetrics fm = g2d.getFontMetrics();        	
	            int sw = fm.stringWidth(used_label);
		        g2d.drawString(used_label, (int)(this.bounds.getX() + (this.bounds.getWidth()-sw)/2), (int)(this.bounds.getY() + this.bounds.getHeight() - this.bounds.getHeight()/2+6));
	        }        
	        g2d.setColor(Color.white);
	        if(this.costs != 0)
	        {            
	            g2d.drawString(this.second_label + this.costs + " �", (int)this.bounds.getX() + 7, (int)this.bounds.getY() + 40);
	        }
	        else if(!this.cost_button && this.second_label != null)
	        {
	        	g2d.drawString(this.second_label, (int)this.bounds.getX() + 7, (int)this.bounds.getY() + 40);
	        }
    	}
    }
	
	static void update_screen_menue_buttons(int window)
	{
		for(int m = 0; m < 8; m++)
		{
			Menu.startscreen_menu_button.get(Integer.toString(m)).label = STARTSCREEN_MENU_BUTTON[Menu.language][window][m];
		}
	}
}	