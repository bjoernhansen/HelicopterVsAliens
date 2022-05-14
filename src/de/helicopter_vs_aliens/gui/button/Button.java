package de.helicopter_vs_aliens.gui.button;

import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.GameEntity;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

// TODO Button könnte evtl. von RectangularGameEntity erben, dann könnten die bounds verwendet werden
public class Button extends GameEntity
{
	private final boolean
		isPurchaseButton,	// = true: Kaufbutton
		isTranslucent;		// = true: durchsichtig
	
	private boolean
		isHighlighted,		// = true: animierter Button; wird = true gesetzt, wenn Maus über Button führt
		isEnabled,
		isMarked;			// farbliche Hervorhebungen bei besonderer Funktion
	
	private int
		costs;				// Preis, falls es ein Kauf-Button in der Werkstatt ist
		
	private String
		primaryLabel,		// Beschriftung
		secondaryLabel;		// zweite Beschriftung, falls vorhanden
	
	private Color
		costColor;			// Farbe, falls es ein Upgrade-Button in der Werkstatt ist
	
	private final Rectangle2D
		bounds;				// Maße und Koordinaten des Buttons
	
	private final ButtonCategory
		category;
	
	
	public static Button makeButton(ButtonSpecifier buttonType)
	{
		return new Button(  buttonType.getCategory(),
							buttonType.getX(),
							buttonType.getY(),
							buttonType.getPrimaryLabel());
	}
		
    private Button(ButtonCategory category, int x, int y, String primaryLabel)
	{
		this.category = category;
		this.bounds = new Rectangle2D.Float(x, y, category.getWidth(), category.getHeight());
		this.primaryLabel = primaryLabel;
		this.isPurchaseButton = category.isPurchaseButton();
		this.costColor = null;
		this.isTranslucent = category.isTranslucent();
		this.isHighlighted = false;
		this.isEnabled = true;
		this.isMarked = false;
	}
	
	public boolean isTranslucent()
	{
		return isTranslucent;
	}
	
	public boolean showsPurchasableOffer()
	{
		return this.isPurchaseButton && costs != 0;
	}
	
	public void adjustCostsTo(int costs)
	{
		this.costs = costs;
		this.updateSecondaryLabel();
	}
	
	public void adjustCostsToZero()
	{
		adjustCostsTo(0);
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
	
	public String getPrimaryLabel()
	{
		return primaryLabel;
	}
	
	public void setPrimaryLabel(String primaryLabel)
	{
		this.primaryLabel = primaryLabel;
	}
	
	public boolean isVisible()
	{
		return primaryLabel != null && !primaryLabel.isEmpty();
	}
	
	public String getSecondaryLabel()
	{
		return secondaryLabel;
	}
	
	public void updateSecondaryLabel()
	{
		if(category == ButtonCategory.MISSION)
		{
			this.secondaryLabel = Window.dictionary.sold();
		}
		else if(this.showsPurchasableOffer())
		{
			this.secondaryLabel = Window.dictionary.price() + " " + costs + " €";
		}
		else
		{
			this.secondaryLabel = "";
		}
	}
	
	public boolean hasSecondaryLabel()
	{
		return this.showsPurchasableOffer() || this.getCategory() == ButtonCategory.MISSION;
	}
	
	public boolean canHaveSecondaryLabel()
	{
		return this.category.canHaveSecondaryLabel();
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
	
	public ButtonCategory getCategory()
	{
		return category;
	}
}