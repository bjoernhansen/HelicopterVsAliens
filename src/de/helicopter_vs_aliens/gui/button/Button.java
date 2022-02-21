package de.helicopter_vs_aliens.gui.button;

import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;


public class Button extends GameEntity
{
	// TODO viele Konstanten können in die spezifischen ButtonType Elemente ausgelagert werden
	public static final Point
		STANDARD_UPGRADE_BUTTON_OFFSET = new Point(559, 95);
	
	private static final int
		UPGRADE_BUTTON_DISTANCE = 60,
		MAIN_MENU_BUTTON_X = 385,
		GROUND_BUTTON_Y = 431,
		LEFT_SIDE_REPAIR_SHOP_BUTTON_X = 23;
	
	private static final Point
		START_SCREEN_MENU_CANCEL_BUTTON_POSITION = new Point(849, 410),
		START_SCREEN_MENU_BUTTON_OFFSET 		 = new Point( 23, 370),
        START_SCREEN_MENU_BUTTON_DISTANCE 		 = new Point(160,  40),
        SPECIAL_UPGRADE_BUTTON_OFFSET 			 = new Point(771, 155);
    	
	// Instanz-Variablen
	private final boolean
		isCostButton,	// = true: Kaufbutton
		isTranslucent;	// = true: durchsichtig
	
	private boolean
		isHighlighted,	// = true: animierter Button; wird = true gesetzt, wenn Maus über Button führt
		isEnabled,
		isMarked;		// farbliche Hervorhebungen bei besonderer Funktion
	
	private int
		costs;			// Preis, falls es ein Kauf-Button in der Werkstatt ist
		
	private String
		label,			// Beschriftung
		secondLabel;	// zweite Beschriftung, falls vorhanden
	
	private Color
		costColor;		// Farbe, falls es ein Upgrade-Button in der Werkstatt ist
	
	private final Rectangle2D
		bounds;		// Maße und Koordinaten des Buttons
	
	private final ButtonCategory
		category;
	
	// TODO idealerweise bekommen die statischen Factory-Methoden zukünftig nur noch ein ButtonType Enum-Element, dann reicht auch eine statische Factory-Methode
	public static Button makeStandardUpradeButton(StandardUpgradeType standardUpgradeType)
	{
		List<String> standardUpgradeLabel = Menu.dictionary.standardUpgradesImprovements(standardUpgradeType);
		return new Button(	ButtonCategory.STANDARD_UPRADE,
							STANDARD_UPGRADE_BUTTON_OFFSET.x,
							STANDARD_UPGRADE_BUTTON_OFFSET.y + standardUpgradeType.ordinal() * UPGRADE_BUTTON_DISTANCE,
							String.join(" ", standardUpgradeLabel),
							Menu.dictionary.price());
	}
	
	public static Button makeStartScreenMenuCancelButton()
	{
		return new Button(	ButtonCategory.START_SCREEN_MENU_CANCEL,
							START_SCREEN_MENU_CANCEL_BUTTON_POSITION.x,
                           	START_SCREEN_MENU_CANCEL_BUTTON_POSITION.y,
                           	Menu.dictionary.cancel(),
                           	null);
	}
	
	public static Button makeStartScreenMenuButton(int buttonIndex)
	{
		int i = buttonIndex/2, j = buttonIndex%2;
		return new Button(  ButtonCategory.START_SCREEN_MENU,
							START_SCREEN_MENU_BUTTON_OFFSET.x + i * START_SCREEN_MENU_BUTTON_DISTANCE.x,
                            START_SCREEN_MENU_BUTTON_OFFSET.y + j * START_SCREEN_MENU_BUTTON_DISTANCE.y,
                            "",
                            null);
	}
	
	public static Button makeSpecialUpgradeButton(SpecialUpgradeType specialUpgradeType)
	{
		return new Button(	ButtonCategory.SPECIAL_UPGRADE,
							SPECIAL_UPGRADE_BUTTON_OFFSET.x,
							SPECIAL_UPGRADE_BUTTON_OFFSET.y + specialUpgradeType.ordinal() * UPGRADE_BUTTON_DISTANCE,
			                de.helicopter_vs_aliens.gui.Menu.dictionary.specialUpgrade(specialUpgradeType),
			                de.helicopter_vs_aliens.gui.Menu.dictionary.price());
	}

	public static Button makeStartScreenButton(ButtonSpecifier buttonType)
	{
		return new Button(  buttonType.getCategory(),
							buttonType.getX(),
							buttonType.getY(),
							buttonType.getLabel(),
							buttonType.getSecondLabel());
	}
	
	public static Button makeMainMenuButton(int posY, String label)
	{
		return new Button(	ButtonCategory.MAIN_MENU,
							MAIN_MENU_BUTTON_X,
							posY,
							label,
							null);
	}
	
	public static Button makeGroundButton(int posX, String label)
	{
		return new Button(	ButtonCategory.GROUND,
							posX,
							GROUND_BUTTON_Y,
							label,
							null);
	}
	
	public static Button makeMissionButton(int posY, String label, String priceLabel)
	{
		return new Button(	ButtonCategory.MISSION,
							LEFT_SIDE_REPAIR_SHOP_BUTTON_X,
							posY,
							label,
							priceLabel);
	}
	
	public static  Button makeRepairButton(int posY, String label, String priceLabel)
	{
		return new Button(	ButtonCategory.REPAIR,
							LEFT_SIDE_REPAIR_SHOP_BUTTON_X,
							posY,
							label,
							priceLabel);
	}
	
    private Button(ButtonCategory category, int x, int y, String label, String secondLabel)
	{
		this.category = category;
		this.bounds = new Rectangle2D.Float(x, y, category.getWidth(), category.getHeight());
		this.label = label;
		this.secondLabel = secondLabel;
		this.costs = 0;
		this.isCostButton = category.isCostButton();
		this.costColor = null;
		this.isTranslucent = category.isTranslucent();
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
	
	public boolean hasSecondLabel()
	{
		return secondLabel != null && !secondLabel.isEmpty();
	}
	
	public ButtonCategory getCategory()
	{
		return category;
	}
}