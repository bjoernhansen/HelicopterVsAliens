package de.helicopter_vs_aliens.gui;
import de.helicopter_vs_aliens.*;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import static de.helicopter_vs_aliens.PriceLevels.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;

public class Button implements Constants, Fonts, Costs
{
	private static final int 
		BUTTON_DISTANCE = 60;
	
	static final Point 
		STANDARD_UPGRADE_LOCATION = new Point(559, 95);
		
	private static final Dimension 
		UPGRADE_BUTTON_SIZE = new Dimension(193, 50);
	    
    // Beschriftungen in deutscher und englischer Sprache
	static final String   
		REPAIR[] = 			 {"Repair helicopter", "Reparatur durchführen"};
	static final String PRICE[]	= 			 {"Price: ", "Kosten: "};
	static final String REPAIR_SHOP[] =		 {"Repair shop", "zur Werkstatt"};
	static final String MAIN_MENU[] = 		 {"Main menu", "Hauptmenü"};
	static final String START_NEW_GAME[] = 	 {"Start a new game", "Neues Spiel starten"};
	static final String QUIT[] = 			 {"Quit", "Spiel beenden"};
	static final String CANCEL[] = 			 {"Cancel", "zurück"};
	public static final String[][] MISSION = 		 {{"Start overnight mission", "Start daytime mission"},
                             {"Nachteinsatz starten", "Tageinsatz starten"}};
	public static final String[][] SOLD = 			{{"Low salary", "High salary"},
                             {"geringer Sold", "hoher Sold"}};
	public static final String[][] DISPLAY = 		{{"Window mode", "Fullscreen mode"},
                             {"Fenstermodus", "Vollbildmodus"}};
	public static final String[][] ANTIALIAZING =  {{"Turn off antialiasing", "Turn on antialiasing" },
                             {"Kantenglättung aus", "Kantenglättung an"}};
	public static final String[][] MUSIC = 		{{"Turn off music", "Turn on music" },
                             {"Musik ausschalten", "Musik einschalten"}};

	public static final String[][][] STARTSCREEN_MENU_BUTTON
			=	{{{"Plot", "Changes since 1.0", "Game instructions", 
				   "Changes since 1.1", "Credits", "Copyright", "", ""},    															
				  {"Game description", "Finances & Repair", "Upgrades", 
				   "Boss-Enemies", "Controls", "Power-ups", "Helicopter types",
				   "Hardcore mode"},   													
				  {"Window mode", "Turn off antialiasing", "Turn off music", 
				   "German", "Player name", "",
				   Audio.MICHAEL_MODE ? "Change music mode": "", ""},
				  {"Contact", "", "", "", "", "", "", ""},
				  {"General information", "Upgrade costs", "Phönix",  "Roch",
				   "Orochi", "Kamaitachi", "Pegasus", "Helios"},
				  {"Record times", "Overall", "Phoenix",  "Roch", "Orochi", 
				   "Kamaitachi", "Pegasus", "Helios"}},
				 {{"Handlung", "Änderungen seit 1.0", "Spielanleitung",
				   "Änderungen seit 1.1", "Credits", "Copyright", "", ""},
				  {"Spielbeschreibung", "Finanzen/Reparatur", "Upgrades", 
				   "Boss-Gegner", "Bedienung", "PowerUps", "Helikopter-Klassen",
				   "Hardcore-Modus"},   													
				  {"Fenstermodus", "Antialiasing aus", "Musik ausschalten", 
				   "Englisch", "Spielername", "",
				   Audio.MICHAEL_MODE ? "Musik ändern": "", ""},
				  {"Kontakt", "", "", "", "", "", "", ""},
				  {"Allgemeines", "Upgrade-Kosten","Phönix", "Roch",  "Orochi",
				   "Kamaitachi", "Pegasus", "Helios"},
				  {"Bestzeiten", "Gesamt", "Phönix",  "Roch", "Orochi",
				   "Kamaitachi", "Pegasus", "Helios"}}};

	static final String STANDARD_UPGRADE_LABEL[][][]
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
    			  {"Raketenantrieb", "erhöhen"},
    			  {"Panzerung", "vestärken"},
    			  {"Feuerkraft", "erhöhen"},
    			  {"Schussrate", "steigern"}, 
    			  {"Energie-UPs", "verbessern"},
    			  {"Teleporter", "verbessern"},
    			  {"Energie-Schild", "aufwerten"},
    			  {"Stopp-Rakete", "aufwerten"},
    			  {"Plasma-Rakete", "aufwerten"},
    			  {"EMP-Generator", "steigern"},
    			  {"PU-Generator", "verbessern"}}};

	static final String STARTSCREEN_BUTTON_LABEL[][][]
    		= 	{{{"Informations",  "Highscore", "Contact"}, 
    			  {"Settings", "Resume last game", "Quit"}},
                 {{"Informationen", "Highscore", "Kontakt"}, 
    			  {"Einstellungen", "Letztes Spiel fortsetzen", "Beenden"}}};
	
	public int costs;				// Preis, falls es ein Kauf-Button in der Werkstatt ist
	 	
	public boolean
		highlighted;    // = true: animierter Button; wird = true gesetzt, wenn Maus über Button führt
	public boolean enabled;
	public boolean marked;			// farbliche Hervorhebungen bei besonderer Funktion
	
	public String
		label;            // Beschriftung
	public String second_label;	// zweite Beschriftung, falls vorhanden
	
	public Color
		cost_color;		// Farbe, falls es ein Upgrade-Button in der Werkstatt ist	
	
	public Rectangle2D
		bounds;		// Maße und Koordinaten des Buttons
	
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
		Menu.repairShopButton.put("RepairButton", new Button(23, 287, 205, 50, REPAIR[Menu.language.ordinal()], PRICE[Menu.language.ordinal()], true, true));
		Menu.repairShopButton.put("Einsatz", new Button(23, 395, 205, 50, MISSION[Menu.language.ordinal()][Events.timeOfDay], SOLD[Menu.language.ordinal()][helicopter.spotlight ? 1 : 0], false, true));
		Menu.inGameButton.put("RepairShop",   new Button(451, 431, 121, 25, REPAIR_SHOP[Menu.language.ordinal()], null, false, false));
		Menu.inGameButton.put("MainMenu",     new Button(897, 431, 121, 25, MAIN_MENU[Menu.language.ordinal()], null, false, false));
		Menu.inGameButton.put("MMNewGame1",   new Button(385, 116, 211, 35, START_NEW_GAME[Menu.language.ordinal()], null, false, false));
		Menu.inGameButton.put("MMStopMusic",  new Button(385, 161, 211, 35, MUSIC[Menu.language.ordinal()][Audio.isSoundOn ? 0 : 1], null, false, false));
		Menu.inGameButton.put("MMNewGame2",   new Button(385, 206, 211, 35, QUIT[Menu.language.ordinal()], null, false, false));
		Menu.inGameButton.put("MMCancel",     new Button(385, 251, 211, 35, CANCEL[Menu.language.ordinal()], null, false, false));
				
		for(int i = 0; i < 2; i++){for(int j = 0; j < 3; j++)
		{
			Menu.startscreen_button.put( Integer.toString(i)+j, 
										 new Button(  27 + Menu.STARTSCREEN_OFFSET_X + i * 750, 
												 	 110 + j * 40, 211, 30, 
												 	 STARTSCREEN_BUTTON_LABEL[Menu.language.ordinal()][i][j], 
												 	 null, false, true));
		}}
		for(int i = 0; i < 6; i ++)
		{
			Menu.repairShopButton.put( "StandardUpgrade" + i,
										new Button(	STANDARD_UPGRADE_LOCATION.x, 
										STANDARD_UPGRADE_LOCATION.y + i * BUTTON_DISTANCE, 
										UPGRADE_BUTTON_SIZE.width, UPGRADE_BUTTON_SIZE.height, 
										STANDARD_UPGRADE_LABEL[Menu.language.ordinal()][i][0] + " " 
										+ STANDARD_UPGRADE_LABEL[Menu.language.ordinal()][i][1],
										PRICE[Menu.language.ordinal()], true, true));
		}
		for(int i = 0; i < 5; i++)
		{
			Menu.repairShopButton.put("Special" + i, new Button(771, 155 + i * 60, 184, 50, Menu.SPECIALS[Menu.language.ordinal()][i],  PRICE[Menu.language.ordinal()], true, true));
		}		
		for(int m = 0; m < 8; m++)
		{				
			int i = m/2, j = m%2;			
			Menu.startscreen_menu_button.put(Integer.toString(m), new Button( 23 + i * 160, 370 + j * 40, 150, 30, "", null, false, true));
		}
				
		Menu.startscreen_button.get("11").marked = true;   // Der "Letztes Spiel fortsetzen"-Button ist markiert 
		Menu.startscreen_button.get("11").enabled = Controller.savegame.valid;
		
		Menu.startscreen_menu_button.put("Cancel", new Button( 849, 410, 150, 30, CANCEL[Menu.language.ordinal()], null, false, true));
		
				
		if(HighscoreEntry.currentPlayerName.equals("John Doe"))
		{
			Menu.startscreen_button.get("10").marked = true;
		}
	}
    
	// Helicopter-spezifische Anpassung der Werkstatt-Button-Beschriftungen
	public static void initialize(Helicopter helicopter)
	{
		Menu.repairShopButton.get("Einsatz").label = MISSION[Menu.language.ordinal()][Events.timeOfDay];
		Menu.repairShopButton.get("Einsatz").second_label = SOLD[Menu.language.ordinal()][helicopter.spotlight ? 1 : 0];
		
		for(int i = 0; i < 6; i++)
    	{    		
			if(!helicopter.has_max_upgrade_level[i])
			{
				Menu.repairShopButton.get("StandardUpgrade" + i).costs = MyMath.costs(helicopter.getType(), helicopter.upgrade_costs[i], helicopter.levelOfUpgrade[i]);
				Menu.repairShopButton.get("StandardUpgrade" + i).cost_color = MyColor.costsColor[helicopter.upgrade_costs[i]];
			}
    	}
		Menu.repairShopButton.get("StandardUpgrade" + 5).label
			= STANDARD_UPGRADE_LABEL[Menu.language.ordinal()][6 + helicopter.getType().ordinal()][0]
			  + " " 
			  + STANDARD_UPGRADE_LABEL[Menu.language.ordinal()][6 + helicopter.getType().ordinal()][1];
		Menu.repairShopButton.get("Special" + 0).costs = SPOTLIGHT_COSTS;
		Menu.repairShopButton.get("Special" + 0).cost_color = MyColor.costsColor[CHEAP.ordinal()];
		Menu.repairShopButton.get("Special" + 1).costs = (helicopter.getType() == PHOENIX || (helicopter.getType() == HELIOS && Events.recordTime[PHOENIX.ordinal()][4] != 0)) ? PHOENIX_GOLIATH_COSTS  : STANDARD_GOLIATH_COSTS ;
		Menu.repairShopButton.get("Special" + 1).cost_color = (helicopter.getType() == PHOENIX || (helicopter.getType() == HELIOS && Events.recordTime[PHOENIX.ordinal()][4] != 0)) ? MyColor.costsColor[VERY_CHEAP.ordinal()] : MyColor.costsColor[REGULAR.ordinal()];
		Menu.repairShopButton.get("Special" + 2).costs = (helicopter.getType() == ROCH || (helicopter.getType() == HELIOS && Events.recordTime[ROCH.ordinal()][4] != 0)) ? CHEAP_SPECIAL_COSTS  : STANDARD_SPECIAL_COSTS ;
		Menu.repairShopButton.get("Special" + 2).cost_color = (helicopter.getType() == ROCH || (helicopter.getType() == HELIOS && Events.recordTime[ROCH.ordinal()][4] != 0)) ? MyColor.costsColor[VERY_CHEAP.ordinal()] : MyColor.costsColor[REGULAR.ordinal()];
		Menu.repairShopButton.get("Special" + 3).costs = (helicopter.getType() == OROCHI || (helicopter.getType() == HELIOS && Events.recordTime[OROCHI.ordinal()][4] != 0)) ? CHEAP_SPECIAL_COSTS  : helicopter.getType() == ROCH ? ROCH_SECOND_CANNON_COSTS  : STANDARD_SPECIAL_COSTS ;
		Menu.repairShopButton.get("Special" + 3).cost_color = (helicopter.getType() == OROCHI || (helicopter.getType() == HELIOS && Events.recordTime[OROCHI.ordinal()][4] != 0)) ? MyColor.costsColor[VERY_CHEAP.ordinal()] : helicopter.getType() == ROCH ? MyColor.costsColor[EXPENSIVE.ordinal()] : MyColor.costsColor[REGULAR.ordinal()];
		Menu.repairShopButton.get("Special" + 3).label = Menu.SPECIALS[Menu.language.ordinal()][3];
		Menu.repairShopButton.get("Special" + 4).costs = helicopter.getType() != ROCH ? CHEAP_SPECIAL_COSTS : JUMBO_MISSILE_COSTS ;
		Menu.repairShopButton.get("Special" + 4).cost_color = helicopter.getType() != ROCH ? MyColor.costsColor[VERY_CHEAP.ordinal()] : MyColor.costsColor[CHEAP.ordinal()];
		Menu.repairShopButton.get("Special" + 4).label = Menu.SPECIALS[Menu.language.ordinal()][4 + helicopter.getType().ordinal()];
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
	            g2d.drawString(this.second_label + this.costs + " €", (int)this.bounds.getX() + 7, (int)this.bounds.getY() + 40);
	        }
	        else if(!this.cost_button && this.second_label != null)
	        {
	        	g2d.drawString(this.second_label, (int)this.bounds.getX() + 7, (int)this.bounds.getY() + 40);
	        }
    	}
    }
	
	public static void updateScreenMenueButtons(WindowTypes window)
	{
		for(int m = 0; m < 8; m++)
		{
			Menu.startscreen_menu_button.get(Integer.toString(m)).label = STARTSCREEN_MENU_BUTTON[Menu.language.ordinal()][window.ordinal()][m];
		}
	}
}	