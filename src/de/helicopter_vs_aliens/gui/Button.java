package de.helicopter_vs_aliens.gui;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.Roch;
import de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.score.HighscoreEntry;
import de.helicopter_vs_aliens.util.Coloration;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

import static de.helicopter_vs_aliens.gui.Menu.NUMBER_OF_STARTSCREEN_BUTTONS;
import static de.helicopter_vs_aliens.gui.Menu.dictionary;
import static de.helicopter_vs_aliens.gui.PriceLevel.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;

// TODO Vererbung auch für Buttons, Enums für Button-Typen

public class Button
{
	private static final int 
		BUTTON_DISTANCE = 60;
	
	static final Point 
		STANDARD_UPGRADE_LOCATION = new Point(559, 95);
		
	private static final Dimension 
		UPGRADE_BUTTON_SIZE = new Dimension(193, 50);
	   
    // Beschriftungen in deutscher und englischer Sprache
	public static final String[][] 
		MISSION = 		{{"Start overnight mission", "Start daytime mission"},
                         {"Nachteinsatz starten", "Tageinsatz starten"}},
		SOLD = 			{{"Low salary", "High salary"},
                         {"geringer Sold", "hoher Sold"}},
		DISPLAY = 		{{"Window mode", "Fullscreen mode"},
                         {"Fenstermodus", "Vollbildmodus"}},
		ANTIALIAZING =  {{"Turn off antialiasing", "Turn on antialiasing" },
                         {"Kantenglättung aus", "Kantenglättung an"}},
		MUSIC = 		{{"Turn off music", "Turn on music" },
                         {"Musik ausschalten", "Musik einschalten"}};

	public static final String[][][] STARTSCREEN_MENU_BUTTON
			=	{{{"Plot", "Changes since 1.0", "Game instructions", 
				   "Changes since 1.1", "Credits", "Copyright", "", ""},    															
				  {"Game description", "Finances & Repair", "Upgrades", 
				   "Boss-Enemies", "Controls", "Power-ups", "Helicopter types",
				   "Special mode"},
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
				   "Spezial-Modus"},
				  {"Fenstermodus", "Antialiasing aus", "Musik ausschalten", 
				   "Englisch", "Spielername", "",
				   Audio.MICHAEL_MODE ? "Musik ändern": "", ""},
				  {"Kontakt", "", "", "", "", "", "", ""},
				  {"Allgemeines", "Upgrade-Kosten","Phönix", "Roch",  "Orochi",
				   "Kamaitachi", "Pegasus", "Helios"},
				  {"Bestzeiten", "Gesamt", "Phönix",  "Roch", "Orochi",
				   "Kamaitachi", "Pegasus", "Helios"}}};

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
	public String secondLabel;	// zweite Beschriftung, falls vorhanden
	
	public Color
			costColor;		// Farbe, falls es ein Upgrade-Button in der Werkstatt ist
	
	public Rectangle2D
		bounds;		// Maße und Koordinaten des Buttons
	
	private boolean
		costButton,	// = true: Kaufbutton
		translucent;	// = true: durchsichtig
	
	
    private Button(int x, int y, int width, int height, String label, String second_label, boolean cost_button, boolean translucent)
	{
		this.bounds = new Rectangle2D.Float(x, y, width, height);
		this.label = label;	
		this.secondLabel = second_label;
		this.costs = 0;
		this.costButton = cost_button;
		this.costColor = null;
		this.translucent = translucent;
		this.highlighted = false;
		this.enabled = true;
		this.marked = false;
	}		
	
    // Erstellen und Pre-Initialisieren der Buttons
	static void initializeButtons(Helicopter helicopter)
	{
		Menu.repairShopButton.put("RepairButton", new Button(23, 287, 205, 50, dictionary.repair(), dictionary.price(), true, true));
		Menu.repairShopButton.put("Einsatz", new Button(23, 395, 205, 50, MISSION[Menu.language.ordinal()][Events.timeOfDay.ordinal()], SOLD[Menu.language.ordinal()][helicopter.hasSpotlights ? 1 : 0], false, true));
		Menu.inGameButton.put("RepairShop",   new Button(451, 431, 121, 25, dictionary.toTheRepairShop(), null, false, false));
		Menu.inGameButton.put("MainMenu",     new Button(897, 431, 121, 25, dictionary.mainMenu(), null, false, false));
		Menu.inGameButton.put("MMNewGame1",   new Button(385, 116, 211, 35, dictionary.startNewGame(), null, false, false));
		Menu.inGameButton.put("MMStopMusic",  new Button(385, 161, 211, 35, MUSIC[Menu.language.ordinal()][Audio.isSoundOn ? 0 : 1], null, false, false));
		Menu.inGameButton.put("MMNewGame2",   new Button(385, 206, 211, 35, dictionary.quit(), null, false, false));
		Menu.inGameButton.put("MMCancel",     new Button(385, 251, 211, 35, dictionary.cancel(), null, false, false));
				
		for(int i = 0; i < NUMBER_OF_STARTSCREEN_BUTTONS.x; i++)
		{
			for(int j = 0; j < NUMBER_OF_STARTSCREEN_BUTTONS.y; j++)
			{
				Menu.startscreenButton.put( Integer.toString(i)+j,
											 new Button(  27 + Menu.STARTSCREEN_OFFSET_X + i * 750,
														 110 + j * 40, 211, 30,
														 STARTSCREEN_BUTTON_LABEL[Menu.language.ordinal()][i][j],
														 null, false, true));
			}
		}
		for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
		{
			List<String> standardUpgradeLabel = dictionary.standardUpgradesImprovements(standardUpgradeType);
			
			Menu.repairShopButton.put( "StandardUpgrade" + standardUpgradeType.ordinal(),
										new Button(STANDARD_UPGRADE_LOCATION.x,
										STANDARD_UPGRADE_LOCATION.y + standardUpgradeType.ordinal() * BUTTON_DISTANCE,
										UPGRADE_BUTTON_SIZE.width, UPGRADE_BUTTON_SIZE.height,
										String.join(" ", standardUpgradeLabel),
										dictionary.price(), true, true));
		}
		for(SpecialUpgradeType specialUpgradeType : SpecialUpgradeType.getValues())
		{
			int i = specialUpgradeType.ordinal();
			Menu.repairShopButton.put("Special" + i, new Button(771, 155 + i * 60, 184, 50,
																dictionary.specialUpgrade(specialUpgradeType),
																dictionary.price(),
																true,
																true));
		}
		for(int m = 0; m < 8; m++)
		{				
			int i = m/2, j = m%2;			
			Menu.startscreenMenuButton.put(Integer.toString(m), new Button( 23 + i * 160, 370 + j * 40, 150, 30, "", null, false, true));
		}
				
		Menu.startscreenButton.get("11").marked = true;   // Der "Letztes Spiel fortsetzen"-Button ist markiert
		Menu.startscreenButton.get("11").enabled = Controller.savegame.valid;
		
		Menu.startscreenMenuButton.put("Cancel", new Button( 849, 410, 150, 30, Menu.dictionary.cancel(), null, false, true));

		if(HighscoreEntry.currentPlayerName.equals("John Doe"))
		{
			Menu.startscreenButton.get("10").marked = true;
		}
	}
    
	// Helicopter-spezifische Anpassung der Werkstatt-Button-Beschriftungen
	// TODO vielleicht können die spezifischen Beschriftungen unnötig gemacht werden, wenn gleich die richtigen Werte verwendet werden
	// TODO sollte nach Menu verschoben werden
	public static void initialize(Helicopter helicopter)
	{
		Menu.repairShopButton.get("Einsatz").label = MISSION[Menu.language.ordinal()][Events.timeOfDay.ordinal()];
		Menu.repairShopButton.get("Einsatz").secondLabel = SOLD[Menu.language.ordinal()][helicopter.hasSpotlights ? 1 : 0];

		for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
    	{    		
			if(!helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType))
			{
				Menu.repairShopButton.get("StandardUpgrade" + standardUpgradeType.ordinal()).costs
						= helicopter.getUpgradeCostFor(standardUpgradeType);
				Menu.repairShopButton.get("StandardUpgrade" + standardUpgradeType.ordinal()).costColor
						= helicopter.getPriceLevelFor(standardUpgradeType).getColor();
			}
    	}
		List<String> standardUpgradeLabel = dictionary.energyAbilityImprovements();
		Menu.repairShopButton.get("StandardUpgrade" + 5).label = String.join(" ", standardUpgradeLabel);
		// TODO hier die eingeführten Methoden mit Rückgabe der Preise verwenden
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.SPOTLIGHT.ordinal()).costs = helicopter.getSpotlightCosts();
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.SPOTLIGHT.ordinal()).costColor = CHEAP.getColor();
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.GOLIATH_PLATING.ordinal()).costs = helicopter.getGoliathCosts();
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.GOLIATH_PLATING.ordinal()).costColor = (helicopter.getType() == PHOENIX || (helicopter.getType() == HELIOS && Events.recordTime[PHOENIX.ordinal()][4] != 0)) ? VERY_CHEAP.getColor() : REGULAR.getColor();
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.PIERCING_WARHEADS.ordinal()).costs = (helicopter.getType() == ROCH || (helicopter.getType() == HELIOS && Events.recordTime[ROCH.ordinal()][4] != 0)) ? Helicopter.CHEAP_SPECIAL_COSTS  : Helicopter.STANDARD_SPECIAL_COSTS ;
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.PIERCING_WARHEADS.ordinal()).costColor = (helicopter.getType() == ROCH || (helicopter.getType() == HELIOS && Events.recordTime[ROCH.ordinal()][4] != 0)) ? VERY_CHEAP.getColor() : REGULAR.getColor();
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.EXTRA_CANNONS.ordinal()).costs = (helicopter.getType() == OROCHI || (helicopter.getType() == HELIOS && Events.recordTime[OROCHI.ordinal()][4] != 0)) ? Helicopter.CHEAP_SPECIAL_COSTS  : helicopter.getType() == ROCH ? Roch.ROCH_SECOND_CANNON_COSTS  : Helicopter.STANDARD_SPECIAL_COSTS ;
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.EXTRA_CANNONS.ordinal()).costColor = (helicopter.getType() == OROCHI || (helicopter.getType() == HELIOS && Events.recordTime[OROCHI.ordinal()][4] != 0)) ? VERY_CHEAP.getColor() : helicopter.getType() == ROCH ? EXPENSIVE.getColor() : REGULAR.getColor();
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.EXTRA_CANNONS.ordinal()).label = dictionary.extraCannons();
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.FIFTH_SPECIAL.ordinal()).costs = helicopter.getFifthSpecialCosts();
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.FIFTH_SPECIAL.ordinal()).costColor = helicopter.getType() != ROCH ? VERY_CHEAP.getColor() : CHEAP.getColor();
		Menu.repairShopButton.get("Special" + SpecialUpgradeType.FIFTH_SPECIAL.ordinal()).label = dictionary.fifthSpecial();
	}
	
	void paint(Graphics2D g2d){paint(g2d, null, false);}
	void paint(Graphics2D g2d, String buttonLabel){paint(g2d, buttonLabel, false);}
	void paint(Graphics2D g2d, boolean loner){paint(g2d, null, loner);}
	private void paint(Graphics2D g2d, String buttonLabel, boolean loner)
	{
    	if(!this.label.equals("") && !loner)
    	{
			String usedLabel = buttonLabel != null ? buttonLabel : this.label;
			if((this.highlighted && this.enabled) || !this.translucent)
			{
				g2d.setPaint(this.highlighted ? Coloration.gradientVariableGray : Coloration.lightestGray);
				g2d.fill(this.bounds);
			}
	    	g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
	    	if(this.costColor == null){g2d.setColor(this.enabled ? this.marked ? Coloration.variableWhite : Color.white : Coloration.lightGray);}
	        else{g2d.setColor(this.costColor);}
	    	if(this.translucent){g2d.draw(this.bounds);}
	    	else
	    	{
	    		g2d.drawLine((int)this.bounds.getX(), (int)this.bounds.getY(), (int)(this.bounds.getX() + this.bounds.getWidth()), (int)this.bounds.getY());
	        	g2d.drawLine((int)this.bounds.getX(), (int)this.bounds.getY(), (int)(this.bounds.getX()), (int)(this.bounds.getY()+this.bounds.getHeight()));
	        	if(this.costColor == null){g2d.setColor(Coloration.gray);}
	            else{g2d.setColor(this.costColor);}
	        	g2d.drawLine((int)(this.bounds.getX()+this.bounds.getWidth()), (int)this.bounds.getY(), (int)(this.bounds.getX() + this.bounds.getWidth()), (int)(this.bounds.getY()+this.bounds.getHeight()));
	        	g2d.drawLine((int)this.bounds.getX(), (int)(this.bounds.getY()+this.bounds.getHeight()), (int)(this.bounds.getX() + this.bounds.getWidth()), (int)(this.bounds.getY()+this.bounds.getHeight()));
	    	}
	        g2d.setStroke(new BasicStroke(1));        
	        	        
	        if(this.secondLabel != null || this.costButton)
	        {	        	
	        	if(this.costColor == null){g2d.setColor(Coloration.lightOrange);}
	            else{g2d.setColor(this.costColor);}
	        	g2d.setFont(Menu.fontProvider.getBold(14));
	        	g2d.drawString(usedLabel, (int)this.bounds.getX() + 7, (int)this.bounds.getY() + 20);
	        }
	        else
	        {
	        	if(this.translucent)
	        	{
	        		g2d.setFont(Menu.fontProvider.getBold(15));
	        		if(this.enabled){g2d.setColor(this.marked ? Coloration.variableMarkedButton : Color.yellow); }
		        	else{g2d.setColor(Coloration.lightGray);}
	        	}
	        	else
	        	{
	        		g2d.setFont(Menu.fontProvider.getPlain(18));
	        		g2d.setColor(Color.black);
	        	}        		
	        	FontMetrics fm = g2d.getFontMetrics();        	
	            int sw = fm.stringWidth(usedLabel);
		        g2d.drawString(usedLabel, (int)(this.bounds.getX() + (this.bounds.getWidth()-sw)/2), (int)(this.bounds.getY() + this.bounds.getHeight() - this.bounds.getHeight()/2+6));
	        }        
	        g2d.setColor(Color.white);
	        if(this.costs != 0)
	        {            
	            g2d.drawString(this.secondLabel + this.costs + " €", (int)this.bounds.getX() + 7, (int)this.bounds.getY() + 40);
	        }
	        else if(!this.costButton && this.secondLabel != null)
	        {
	        	g2d.drawString(this.secondLabel, (int)this.bounds.getX() + 7, (int)this.bounds.getY() + 40);
	        }
    	}
    }
	
	public static void updateScreenMenuButtons(WindowType window)
	{
		for(int m = 0; m < 8; m++)
		{
			Menu.startscreenMenuButton.get(Integer.toString(m)).label = STARTSCREEN_MENU_BUTTON[Menu.language.ordinal()][window.ordinal()][m];
		}
	}
}	