package de.helicopter_vs_aliens.gui;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;


public class Button extends GameEntity
{
	// Beschriftungen in deutscher und englischer Sprache
	public static final String[][]
		MISSION = 		{{"Start overnight mission", "Start daytime mission"},
						 {"Nachteinsatz starten", "Tageinsatz starten"}},
		SOLD = 			{{"Low salary", "High salary"},
			 			 {"geringer Sold", "hoher Sold"}},
		DISPLAY = 		{{"Window mode", "Fullscreen mode"},
						 {"Fenstermodus", "Vollbildmodus"}},
		MUSIC = 		{{"Turn off music", "Turn on music" },
						 {"Musik ausschalten", "Musik einschalten"}};
	
	static final String
		LABELS_OF_START_SCREEN_BUTTONS[][][]
			= 	{{{"Informations",  "Highscore", "Contact"},
				  {"Settings", "Resume last game", "Quit"}},
				 {{"Informationen", "Highscore", "Kontakt"},
				  {"Einstellungen", "Letztes Spiel fortsetzen", "Beenden"}}};
	
	static final Point
		STANDARD_UPGRADE_BUTTON_OFFSET = new Point(559, 95);
	
	private static final int
		BUTTON_DISTANCE = 60,
		MAIN_MENU_BUTTON_X = 385,
		GROUND_BUTTON_Y = 431,
		LEFT_SIDE_REPAIR_SHOP_BUTTON_X = 23;
	
	private static final Point
		START_SCREEN_MENU_CANCEL_BUTTON_POSITION = new Point(849, 410),
		START_SCREEN_MENU_BUTTON_OFFSET 		 = new Point( 23, 370),
        START_SCREEN_MENU_BUTTON_DISTANCE 		 = new Point(160,  40),
		START_SCREEN_BUTTON_OFFSET 				 = new Point( 27, 110),
		START_SCREEN_BUTTON_DISTANCE 			 = new Point(750,  40),
        SPECIAL_UPGRADE_BUTTON_OFFSET 			 = new Point(771, 155);
    
	private static final Dimension
        UPGRADE_BUTTON_SIZE 			  = new Dimension(193, 50),
        START_SCREEN_MENU_BUTTON_SIZE 	  = new Dimension(150, 30),
        GROUND_BUTTON_SIZE 				  = new Dimension(121, 25),
        LEFT_SIDE_REPAIR_SHOP_BUTTON_SIZE = new Dimension(205, 50),
		SPECIAL_UPGRADE_BUTTON_SIZE 	  = new Dimension(184, 50),
        MAIN_MENU_BUTTON_SIZE 			  = new Dimension(211, 35);
	
	// TODO das sollte nicht alles public sein --> accessoren verwenden
	// Instanz-Variablen
	private int
		costs;			// Preis, falls es ein Kauf-Button in der Werkstatt ist
	
	private boolean
		isHighlighted,	// = true: animierter Button; wird = true gesetzt, wenn Maus über Button führt
		isEnabled,
		isMarked;		// farbliche Hervorhebungen bei besonderer Funktion
	
	private String
		label,			// Beschriftung
		secondLabel;	// zweite Beschriftung, falls vorhanden
	
	private Color
		costColor;		// Farbe, falls es ein Upgrade-Button in der Werkstatt ist
	
	private Rectangle2D
		bounds;		// Maße und Koordinaten des Buttons
	
	private boolean
		isCostButton,	// = true: Kaufbutton
		isTranslucent;	// = true: durchsichtig
	
	
	static Button makeStandardUpradeButton(StandardUpgradeType standardUpgradeType){
		List<String> standardUpgradeLabel = Menu.dictionary.standardUpgradesImprovements(standardUpgradeType);
		return new Button(	STANDARD_UPGRADE_BUTTON_OFFSET.x,
							STANDARD_UPGRADE_BUTTON_OFFSET.y + standardUpgradeType.ordinal() * BUTTON_DISTANCE,
							UPGRADE_BUTTON_SIZE.width,
							UPGRADE_BUTTON_SIZE.height,
							String.join(" ", standardUpgradeLabel),
							Menu.dictionary.price(),
							true,
							true);
	}
	
	static Button makeStartScreenMenuCancelButton()
	{
		return new Button( START_SCREEN_MENU_CANCEL_BUTTON_POSITION.x,
                           START_SCREEN_MENU_CANCEL_BUTTON_POSITION.y,
                           START_SCREEN_MENU_BUTTON_SIZE.width,
                           START_SCREEN_MENU_BUTTON_SIZE.height,
                           Menu.dictionary.cancel(),
                           null,
                           false,
                           true);
	}
	
	static Button makeStartScreenMenuButton(int buttonIndex)
	{
		int i = buttonIndex/2, j = buttonIndex%2;
		return new Button(  START_SCREEN_MENU_BUTTON_OFFSET.x + i * START_SCREEN_MENU_BUTTON_DISTANCE.x,
                            START_SCREEN_MENU_BUTTON_OFFSET.y + j * START_SCREEN_MENU_BUTTON_DISTANCE.y,
                            START_SCREEN_MENU_BUTTON_SIZE.width,
                            START_SCREEN_MENU_BUTTON_SIZE.height,
                            "",
                            null,
                            false,
                            true);
	}
	
	static Button makeSpecialUpgradeButton(SpecialUpgradeType specialUpgradeType)
	{
		return new Button(	SPECIAL_UPGRADE_BUTTON_OFFSET.x,
							SPECIAL_UPGRADE_BUTTON_OFFSET.y + specialUpgradeType.ordinal() * BUTTON_DISTANCE,
							SPECIAL_UPGRADE_BUTTON_SIZE.width,
							SPECIAL_UPGRADE_BUTTON_SIZE.height,
			                Menu.dictionary.specialUpgrade(specialUpgradeType),
			                Menu.dictionary.price(),
			                true,
			                true);
	}
	
	static Button makeStartScreenButton(int indexX, int indexY)
	{
		return new Button(  START_SCREEN_BUTTON_OFFSET.x + Menu.START_SCREEN_OFFSET_X + indexX * START_SCREEN_BUTTON_DISTANCE.x,
							START_SCREEN_BUTTON_OFFSET.y + indexY * START_SCREEN_BUTTON_DISTANCE.y,
							211,
							30,
			                LABELS_OF_START_SCREEN_BUTTONS[Menu.language.ordinal()][indexX][indexY],
			                null,
                            false,
                            true);
	}
	
	static Button makeMainMenuButton(int posY, String label)
	{
		return new Button(MAIN_MENU_BUTTON_X, posY, MAIN_MENU_BUTTON_SIZE.width, MAIN_MENU_BUTTON_SIZE.height, label, null, false, false);
	}
	
	static Button makeGroundButton(int posX, String label)
	{
		return new Button(posX, GROUND_BUTTON_Y, GROUND_BUTTON_SIZE.width, GROUND_BUTTON_SIZE.height, label, null, false, false);
	}
	
	static Button makeLeftSideRepairShopButton(int posY, String label, String priceLabel)
	{
		return new Button(LEFT_SIDE_REPAIR_SHOP_BUTTON_X, posY, LEFT_SIDE_REPAIR_SHOP_BUTTON_SIZE.width, LEFT_SIDE_REPAIR_SHOP_BUTTON_SIZE.height, label, priceLabel, true, true);
	}
	
    private Button(int x, int y, int width, int height, String label, String second_label, boolean cost_button, boolean translucent)
	{
		this.bounds = new Rectangle2D.Float(x, y, width, height);
		this.label = label;
		this.secondLabel = second_label;
		this.costs = 0;
		this.isCostButton = cost_button;
		this.costColor = null;
		this.isTranslucent = translucent;
		this.isHighlighted = false;
		this.isEnabled = true;
		this.isMarked = false;
	}
	
	public boolean isCostButton()
	{
		return isCostButton;
	}
	
	public boolean isTranslucent()
	{
		return isTranslucent;
	}
	
	public int getCosts()
	{
		return costs;
	}
	
	public void setCosts(int costs)
	{
		this.costs = costs;
	}
	
	public void setCostsToZero()
	{
		this.costs = 0;
	}
	
	public boolean isHighlighted()
	{
		return isHighlighted;
	}
	
	public void setHighlighted(boolean highlighted)
	{
		isHighlighted = highlighted;
	}
	
	public boolean isEnabled()
	{
		return isEnabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		isEnabled = enabled;
	}
	
	public boolean isMarked()
	{
		return isMarked;
	}
	
	public void setMarked(boolean marked)
	{
		isMarked = marked;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public boolean isLabelEmpty()
	{
		return label == null || label.equals("");
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getSecondLabel()
	{
		return secondLabel;
	}
	
	public void setSecondLabel(String secondLabel)
	{
		this.secondLabel = secondLabel;
	}
	
	public Color getCostColor()
	{
		return costColor;
	}
	
	public void setCostColor(Color costColor)
	{
		this.costColor = costColor;
	}
	
	public Rectangle2D getBounds()
	{
		return bounds;
	}
}