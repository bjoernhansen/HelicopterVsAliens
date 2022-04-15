package de.helicopter_vs_aliens.gui.window;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.gui.BlockMessage;
import de.helicopter_vs_aliens.gui.FontProvider;
import de.helicopter_vs_aliens.gui.Label;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.gui.button.ButtonGroup;
import de.helicopter_vs_aliens.gui.button.ButtonSpecifier;
import de.helicopter_vs_aliens.gui.button.GroundButtonType;
import de.helicopter_vs_aliens.gui.button.LeftSideRepairShopButtonType;
import de.helicopter_vs_aliens.gui.button.MainMenuButtonType;
import de.helicopter_vs_aliens.gui.button.SpecialUpgradeButtonType;
import de.helicopter_vs_aliens.gui.button.StandardUpgradeButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenSubCancelButtonType;
import de.helicopter_vs_aliens.model.Paintable;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.Roch;
import de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;
import de.helicopter_vs_aliens.util.dictionary.Dictionary;
import de.helicopter_vs_aliens.util.dictionary.Language;

import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.helicopter_vs_aliens.gui.PriceLevel.CHEAP;
import static de.helicopter_vs_aliens.gui.PriceLevel.EXPENSIVE;
import static de.helicopter_vs_aliens.gui.PriceLevel.REGULAR;
import static de.helicopter_vs_aliens.gui.PriceLevel.VERY_CHEAP;
import static de.helicopter_vs_aliens.gui.WindowType.HIGH_SCORE;
import static de.helicopter_vs_aliens.gui.WindowType.REPAIR_SHOP;
import static de.helicopter_vs_aliens.gui.WindowType.SCORE_SCREEN;
import static de.helicopter_vs_aliens.gui.WindowType.SETTINGS;
import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.HELIOS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.OROCHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PHOENIX;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.ROCH;
import static de.helicopter_vs_aliens.util.dictionary.Language.ENGLISH;


public abstract class Window implements Paintable
{
	// TODO An vielen Stellen im Menü werden Zustände immer wieder neu berechnet anstatt sie einmal zu speichern
	// TODO Klasse ist zu groß --> splitten, ggf. verschiedene Menu-Unterklassen einführen für die einzelnen Untermenüs
	// --> viel weniger Statisch, sondern mit Menu-Objekt arbeiten
	
	public static final String
		DEFAULT_PLAYER_NAME = "John Doe";
		
	public static final int
		NUMBER_OF_HIGH_SCORE_COLUMN_NAMES = 8, // TODO in ein Enum überführen HighScoreColumns
        NUMBER_OF_SETTING_OPTIONS = 5,	    
		POWER_UP_SIZE = 23,
		MAXIMUM_COLLECTED_POWERUPS_COUNT = 4,
		NUMBER_OF_START_SCREEN_HELICOPTERS = 4,
		MESSAGE_LINE_COUNT = 4,
	
		BONUS_DISPLAY_TIME = 200,						// Anzeigezeit des zuletzt erhalten Bonus
		START_SCREEN_OFFSET_X = 4,						// x-Verschiebung der meisten Anzeigen des StartScreens
		HELICOPTER_DISTANCE = 250,                		// Abstand zwischen den Helikopter auf dem StartScreen
		START_SCREEN_HELICOPTER_OFFSET_Y = 33,    		// y-Verschiebung der Helicopter-Leiste im StartScreen-Menü
	
		MONEY_DISPLAY_TIME = BONUS_DISPLAY_TIME + 100,	// Anzeigezeit der Geldanzeige nach Gegner-Abschuss
		LEVEL_DISPLAY_TIME = 250,
		CROSS_MAX_DISPLAY_TIME = 60,           			// Maximale Anzeigezeit des Block-Kreuzes (StartScreen)
		UNLOCKED_DISPLAY_TIME = 300;
	
	public static int
		helicopterSelection,			// Helicopter-Auswahl im StartScreen-Menü
		fps,							// Frames per Second; wird über die FPS-Anzeige ausgegeben
		specialInfoSelection = 0,		// nur für Debugging: Auswahl der anzuzeigenden Informationen
		crossPosition,					// Position des Block-Kreuzes
	
	// Menu-Timer
		messageTimer, 					// regulieren die Dauer [frames] der Nachrichtenanzeige im Werkstatt-Menü
		moneyDisplayTimer,				// regulieren die Dauer [frames] der Geld-Anzeige im Spiel
		crossTimer,						// regulieren die Dauer [frames] der Block-Kreuz-Anzeige auf dem StartScreen
		unlockedTimer;					// regulieren die Dauer [frames] der Anzeige des freigeschalteten Helicopters
	
	public static int[]
		effectTimer = new int[HelicopterType.count()];	// regulieren die Helikopter-Animationen im StartScreen-Menü
	 
	public static StartScreenMenuButtonType
		page = StartScreenMenuButtonType.BUTTON_1; // ausgewählte Seite im StartScreen-Menü
	
	public static Language
		language = ENGLISH;

	public static HelicopterType
		unlockedType;					// Typ des freigeschalteten Helicopters

	
	
	public static boolean
		isMenuVisible,					// = true: Spielmenü ist sichtbar
		hasOriginalResolution = false;
	
    // Auf dem StartScreen gezeichnete Polygone
    public static Polygon
    	cross;                      // das rote Block-Kreuz
	
	public static Polygon[]
		triangle = new Polygon[2];	// die grünen Dreiecke auf dem StartScreen
	 
	public static final EnumMap<HelicopterType, Helicopter>
		helicopterDummies = new EnumMap<>(HelicopterType.class);
	 	
    // Menu Objects
    public static String
		repairShopTime;
		
    public static String []
	    // TODO daraus eine Liste<String> machen
		message = new String [MESSAGE_LINE_COUNT];
	
	public static Map<ButtonSpecifier, Button>
		buttons = new HashMap<>();
		
    public static Label
		label;
	
	public static PowerUp[]
		collectedPowerUp = new PowerUp [MAXIMUM_COLLECTED_POWERUPS_COUNT];
	
	public static Rectangle[]
		helicopterFrame = new Rectangle[NUMBER_OF_START_SCREEN_HELICOPTERS];
	
    public static FontProvider
		fontProvider = new FontProvider();
	
	public static LabelTextProvider
		labelTextProvider = new LabelTextProvider();
	
    public final static Dictionary
		dictionary = new Dictionary();
		
	public static final Timer
		levelDisplayTimer = new Timer(LEVEL_DISPLAY_TIME);			// reguliert die Anzeigezeit der Level-Anzeige nach einem Level-Up
	
	
	private static final String[]
		EMPTY_MESSAGE = {"", "", "", ""};
	
	private static Button
		highlightedButton = null;
	
	
	/** Paint-Methoden **/
	@Override
	public void paint(GraphicsAdapter graphicsAdapter)
	{
		GraphicsManager.getInstance().paint(this);
	}
	
	// Initialization
	
    public static void initialize()
    {
    	for(HelicopterType helicopterType : HelicopterType.getValues())
		{
			helicopterDummies.put(helicopterType, helicopterType.makeInstance());
		}
    	
    	helicopterSelection = (3 + Calculations.random(HelicopterType.count()-1))
    						   % HelicopterType.count();
    	
    	int[] px1 = {19 , 19 , 5};
    	int[] px2= {1004, 1004, 1018};
	    int[] py = {261 + START_SCREEN_HELICOPTER_OFFSET_Y,
	                331 + START_SCREEN_HELICOPTER_OFFSET_Y,
	                296 + START_SCREEN_HELICOPTER_OFFSET_Y};
	    triangle[0] = new Polygon(px1, py, 3);
	    triangle[1] = new Polygon(px2, py, 3);
	    
	    initializeButtons();
	    for(int i = 0; i < NUMBER_OF_START_SCREEN_HELICOPTERS; i++)
	    {
	    	helicopterFrame[i]
	    	    = new Rectangle(  30 + START_SCREEN_OFFSET_X
	    							 + i * HELICOPTER_DISTANCE,
	    						 239 + START_SCREEN_HELICOPTER_OFFSET_Y, 206, 116);
	    }
    }
	 
    
    /** Update-Methoden */
	
    private static void updateStartScreen(Helicopter helicopter, int counter)
	{
    	Colorations.calculateVariableGameColors(counter);
    	identifyHighlightedButtons(helicopter, ButtonGroup.START_SCREEN);
        if(crossTimer > CROSS_MAX_DISPLAY_TIME){
			crossTimer = 0;}
        else if(crossTimer > 0 ){
			crossTimer++;}
        if(messageTimer != 0){
			messageTimer++;}
		if(messageTimer > 215){
			messageTimer = 0;}

		// TODO eventuell ist hier kein array effectTimer, sondern ein einzelner Wert erforderlich, da die effekte scheinbar
		// nicht gleichzeitig erfolgen können (reset bei wechsel des selektierten Helikopters
        for(int i = 0; i < HelicopterType.count(); i++)
        {
        	if(effectTimer[i] > 0){
				effectTimer[i]--;}
        }
		Events.previousHelicopterType = Events.nextHelicopterType;
        Events.nextHelicopterType = null;
		for(int i = 0; i < NUMBER_OF_START_SCREEN_HELICOPTERS; i++)
		{
			// TODO HelicopterDestination --> das darf nicht mehr eine Eigenschaft von Helicopter sein
			if(	helicopterFrame[i].contains(helicopter.destination))
			{
				Events.nextHelicopterType = HelicopterType.getValues().get((i + helicopterSelection)% HelicopterType.count());
				Helicopter helicopterDummy = helicopterDummies.get(Events.nextHelicopterType);
				if(Events.hasSelectedHelicopterChanged())
				{
					helicopterDummy.initMenuEffect(i);
					resetEffectTimer();
					effectTimer[Events.nextHelicopterType.ordinal()] = Events.nextHelicopterType.getEffectTime();
				}
				helicopterDummy.updateMenuEffect();
				break;
			}
		}
		if(Events.nextHelicopterType == null)
		{
			resetEffectTimer();
		}
		if(Events.hasSelectedHelicopterChanged() && Events.previousHelicopterType != null)
		{
			helicopterDummies.get(Events.previousHelicopterType).stoptMenuEffect();
		}
	}

	private static void resetEffectTimer()
	{
		Arrays.fill(effectTimer, 0);
	}

    private static void updateRepairShop(Helicopter helicopter)
	{
    	identifyHighlightedButtons(helicopter, ButtonGroup.REPAIR_SHOP);
		// TODO Timer Klasse verwenden
		if(messageTimer != 0)
		{
			messageTimer++;
		}
		if(messageTimer > 110)
		{
			clearMessage();
		}
		if(!helicopter.isDamaged){helicopter.rotatePropellerSlow();}
	}
	
	public static void updateScoreScreen(Helicopter helicopter)
	{
    	helicopter.rotatePropellerSlow();
		Button cancelButton = buttons.get(StartScreenSubCancelButtonType.CANCEL);
		boolean isHighlighted = cancelButton.getBounds().contains(helicopter.destination);
		cancelButton.setHighlighted(isHighlighted);
	}
	
	public static String minutes(long spielzeit)
	{
		if(spielzeit == 1) return Window.language == ENGLISH ? "1 minute" : "1 Minute";
		return spielzeit + (Window.language == ENGLISH ? " minutes" : " Minuten");
	}
	
	/** Displays **/
	
	public static void updateDisplays(Helicopter helicopter)
	{
		updateCreditDisplay(helicopter);
		updateForegroundDisplays();
	}
	
	private static void updateCreditDisplay(Helicopter helicopter)
	{
		if(!isMenuVisible && (moneyDisplayTimer != Timer.DISABLED || helicopter.isDamaged))
		{
			moneyDisplayTimer++;
			if(moneyDisplayTimer == BONUS_DISPLAY_TIME)
			{
				Events.lastBonus = 0;
				Events.lastExtraBonus = 0;
			}
			else if(moneyDisplayTimer > MONEY_DISPLAY_TIME)
			{
				moneyDisplayTimer = Timer.DISABLED;
			}
		}
	}
	
	private static void updateForegroundDisplays()
	{
		if(unlockedTimer > 0)
    	{
			updateUnlockedInfo();
		}
	}
    
    public static String returnTimeDisplayText(long zeit)
    {
    	long h, min, sek;
        h = ((zeit - (zeit%3600000))/3600000);
        min = (zeit - (zeit%60000))/60000;
        sek = (zeit - (zeit%1000))/1000;
        return (h%100 > 9?"":"0") + (h%100)
		   		+ ":" + (min%60  > 9?"":"0") + (min%60)
		   		+ ":" + (sek%60  > 9?"":"0") + (sek%60);
    }
    
    static void updateUnlockedInfo()
	{
		unlockedTimer--;
		if(unlockedTimer ==  UNLOCKED_DISPLAY_TIME - 50)
		{
			Audio.play(Audio.cash);
		}
		Window.helicopterDummies.get(unlockedType).rotatePropellerSlow();
		if(unlockedTimer == 0){
			unlockedType = null;}
	}
    
	public static void changeLanguage(Controller controller)
	{
		Events.settingsChanged = true;
		setLanguage(getNextLanguage());
		controller.getSaveGame().language = language;
		
		updateButtonLabels(controller.getHelicopter());
	}

	private static Language getNextLanguage() {
		return Language.values()[(language.ordinal()+1)% Language.values().length];
	}

	public static void setLanguage(Language language)
	{
		Window.language = language;
		Window.dictionary.switchLanguageTo(language);
	}
	
	public static void updateStartScreenSubButtons()
	{
		StartScreenMenuButtonType.getValues().forEach(buttonSpecifier -> {
			StartScreenMenuButtonType buttonType = (StartScreenMenuButtonType) buttonSpecifier;
			buttons.get(buttonSpecifier).setPrimaryLabel(dictionary.startScreenSubButtonName(buttonType));
		});
		buttons.get(StartScreenSubCancelButtonType.CANCEL).setPrimaryLabel(dictionary.cancel());
	}
	
	public static void updateButtonLabels(Helicopter helicopter)
	{
		// TODO Buttons sollten einen Supplier haben, mit dem sie selbst aktualisieren können und dann eine Update-Funktion nutzen
		
		buttons.get(LeftSideRepairShopButtonType.REPAIR).setPrimaryLabel(dictionary.repair());
		buttons.get(LeftSideRepairShopButtonType.REPAIR).updateSecondaryLabel();
		buttons.get(LeftSideRepairShopButtonType.MISSION).setPrimaryLabel(dictionary.mission());
		buttons.get(LeftSideRepairShopButtonType.MISSION).updateSecondaryLabel();
		
		buttons.get(GroundButtonType.REPAIR_SHOP).setPrimaryLabel(dictionary.toTheRepairShop());
		buttons.get(GroundButtonType.MAIN_MENU).setPrimaryLabel(dictionary.mainMenu());
		buttons.get(MainMenuButtonType.NEW_GAME_1).setPrimaryLabel(dictionary.startNewGame());
		buttons.get(MainMenuButtonType.STOP_MUSIC).setPrimaryLabel(dictionary.audioActivation());
		buttons.get(MainMenuButtonType.NEW_GAME_2).setPrimaryLabel(dictionary.quit());
		buttons.get(MainMenuButtonType.CANCEL).setPrimaryLabel(dictionary.cancel());
		
		StartScreenButtonType.getValues()
							 .forEach(buttonType -> buttons.get(buttonType)
														   .setPrimaryLabel(buttonType.getPrimaryLabel()));
		
		updateStartScreenSubButtons();
		
		StandardUpgradeButtonType.getValues().forEach(buttonSpecifier -> {
			StandardUpgradeButtonType buttonType = (StandardUpgradeButtonType)buttonSpecifier;
			StandardUpgradeType standardUpgradeType = buttonType.getStandardUpgradeType();
			buttons.get(buttonSpecifier).setPrimaryLabel(String.join(" ", dictionary.standardUpgradesImprovements(standardUpgradeType)));
			buttons.get(buttonSpecifier).updateSecondaryLabel();
		});
		
		SpecialUpgradeButtonType.getValues().forEach(buttonSpecifier -> {
			SpecialUpgradeButtonType buttonType = (SpecialUpgradeButtonType)buttonSpecifier;
			SpecialUpgradeType specialUpgradeType = buttonType.getSpecialUpgradeType();
			buttons.get(buttonSpecifier).setPrimaryLabel(dictionary.specialUpgrade(specialUpgradeType));
			buttons.get(buttonSpecifier).updateSecondaryLabel();
		});
	
		if(helicopter.getType() == OROCHI && helicopter.numberOfCannons == 2)
		{
			buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).setPrimaryLabel(dictionary.thirdCannon());
		}
	}
 
	static void identifyHighlightedButtons(Helicopter helicopter, ButtonGroup buttonGroup)
	{
    	if(isSearchForButtonToBeHighlightedNecessary(helicopter))
        {
			stopButtonHighlighting();
			for(ButtonSpecifier buttonSpecifier : buttonGroup.getButtonSpecifiers())
			{
				Button button = buttons.get(buttonSpecifier);
				if(button == null || helicopter == null)
				{
					String.format("Button: %s; Helicopter: %s", button, helicopter);
				}
				if (button.getBounds().contains(helicopter.destination.x, helicopter.destination.y))
				{
					button.setHighlighted(true);
					highlightedButton = button;
					break;
				}
			}
        }
	}
	
	private static boolean isSearchForButtonToBeHighlightedNecessary(Helicopter helicopter)
	{
		return highlightedButton == null
			   || !highlightedButton.getBounds().contains(helicopter.destination);
	}
	
	public static Polygon getCrossPolygon()
    {
    	// TODO mit Konstanten arbeiten, ggf. braucht man nicht zwei Methoden --> lokale Variablen einsetzen
		return getCrossPolygon(	190, 100, 15, 38 + START_SCREEN_OFFSET_X
    								+ crossPosition * HELICOPTER_DISTANCE,
    								247 + START_SCREEN_HELICOPTER_OFFSET_Y);
    }
    
    private static Polygon getCrossPolygon(int a, int b, int z, int v, int w)
    {
    	int [] tempX = {0, (a/2), a, a, a*b/(2*(b-z)), a, a, a/2, 0, 0,
    	                 a*(b-2*z)/(2*(b-z)), 0};
    	int [] tempY = {0, (b-z)/2, 0, z, (b-2*z)/2+z, b-z, b, (b-z)/2+z,
    	                 b, b-z, (b-2*z)/2+z, z};
    	for(int i = 0; i < 12; i++){tempX[i] += v; tempY[i] += w;}
    	return new Polygon(tempX, tempY, 12);
    }
	
	public static void block(BlockMessage blockMessage)
	{
		Audio.play(Audio.block);
        message = dictionary.blockMessage(blockMessage);
		messageTimer = 1;
	}
	
	public static void setStartScreenMessageForBlocking(HelicopterType helicopterType)
	{
		message = dictionary.helicopterNotAvailable(helicopterType);
	}
	
	public static void clearMessage()
	{
	    message = EMPTY_MESSAGE;
		messageTimer = 0;
	}
	
	public static void updateStartScreenSubLabelText()
	{
		label.setText(labelTextProvider.getLabel(language, WindowManager.window, page));
	}
	
	public static void adaptToNewWindow(boolean justEntered)
	{
		page = StartScreenMenuButtonType.BUTTON_1;
		if(WindowManager.window  != HIGH_SCORE && WindowManager.window  != SETTINGS){
			label.setVisible(true);}
		updateStartScreenSubLabelText();
		crossTimer = 0;
		messageTimer = 0;
		if(justEntered){
			stopButtonHighlighting();}
	}
	
	public static void stopButtonHighlighting()
	{
		Optional.ofNullable(highlightedButton).ifPresent(
			button -> {
				button.setHighlighted(false);
				highlightedButton = null;
			}
		);
	}

	public static void updateCollectedPowerUps(Helicopter helicopter, PowerUp powerUp)
	{
		helicopter.powerUpTimer[powerUp.getType().ordinal()] = Math.max(helicopter.powerUpTimer[powerUp.getType().ordinal()], Helicopter.POWERUP_DURATION);
		if(collectedPowerUp[powerUp.getType().ordinal()] == null){powerUp.moveToStatusbar();}
		else
		{
			collectedPowerUp[powerUp.getType().ordinal()].setOpaque();
		}
	}

	public static void updateRepairShopButtons(Helicopter helicopter)
	{
		buttons.get(LeftSideRepairShopButtonType.MISSION).setPrimaryLabel(dictionary.mission());
		buttons.get(LeftSideRepairShopButtonType.MISSION).updateSecondaryLabel();
		
		if(helicopter.hasSpotlights)
		{
			buttons.get(SpecialUpgradeButtonType.SPOTLIGHT).adjustCostsToZero();
		}
		
		if(helicopter.hasGoliathPlating())
		{
			buttons.get(SpecialUpgradeButtonType.GOLIATH_PLATING).adjustCostsToZero();
		}
		
		if(helicopter.hasPiercingWarheads)
		{
			buttons.get(SpecialUpgradeButtonType.PIERCING_WARHEADS).adjustCostsToZero();
		}
		
		if(helicopter.numberOfCannons != 1)
		{
			if(helicopter.hasAllCannons())
	    	{
	    		buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).adjustCostsToZero();
	    		if(helicopter.numberOfCannons == 3)
	    		{
	    			buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).setPrimaryLabel(dictionary.thirdCannon());
	    		}
	    	}
	    	else
	    	{
	    		buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).adjustCostsTo(Helicopter.STANDARD_SPECIAL_COSTS);
	    		buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).setPrimaryLabel(dictionary.thirdCannon());
	    	}
		}
		
		if(helicopter.hasFifthSpecial())
		{
			buttons.get(SpecialUpgradeButtonType.FIFTH_SPECIAL).adjustCostsToZero();
		}
		
		StandardUpgradeButtonType.getValues().forEach(buttonSpecifier -> {
			StandardUpgradeButtonType buttonType = (StandardUpgradeButtonType) buttonSpecifier;
			StandardUpgradeType standardUpgradeType = buttonType.getStandardUpgradeType();
			if(helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType))
			{
				buttons.get(buttonSpecifier).adjustCostsToZero();
			}
			else
			{
				buttons.get(buttonSpecifier).adjustCostsTo(helicopter.getUpgradeCostFor(standardUpgradeType));
			}
		});
		
		buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsToZero();
	}
	
	public static void unlock(HelicopterType heliType)
	{
		unlockedType = heliType;
		unlockedTimer = UNLOCKED_DISPLAY_TIME;
	}

	public static void update(Controller controller, Helicopter helicopter)
	{
		if(WindowManager.window  == REPAIR_SHOP)
		{
			updateRepairShop(helicopter);
		}
		else if(WindowManager.window  == START_SCREEN)
		{
			updateStartScreen(helicopter, controller.framesCounter);
		}
		else if(WindowManager.window  == SCORE_SCREEN)
		{
			updateScoreScreen(helicopter);
		}
		else
		{
			identifyHighlightedButtons(helicopter, ButtonGroup.START_SCREEN_MENU);
		}
	}

	public static void adaptToWindowMode(Dimension displayShift)
	{
		label.setBounds(	displayShift.width  + 42,
  						   	displayShift.height + 83,
							940,
							240);
	}
	
	public static void reset()
	{
		stopButtonHighlighting();
		cross = null;
		crossTimer = 0;
		messageTimer = 0;
	}
	
	public static void conditionalReset()
	{
		isMenuVisible = false;
		buttons.get(MainMenuButtonType.NEW_GAME_2).setPrimaryLabel(dictionary.quit());
		moneyDisplayTimer = Timer.DISABLED;
		levelDisplayTimer.start();
		unlockedTimer = 0;
	}

	public static void blockHelicopterSelection(HelicopterType nextHelicopterType)
	{
		Audio.play(Audio.block);
		crossPosition = (Events.nextHelicopterType.ordinal() - helicopterSelection + HelicopterType.count())% HelicopterType.count();
		cross = getCrossPolygon();
		crossTimer = 1;
		messageTimer = 1;
		setStartScreenMessageForBlocking(nextHelicopterType);
	}
	
	// Erstellen und Pre-Initialisieren der Buttons
	static void initializeButtons()
	{
		ButtonGroup.COMPLETE.getButtonSpecifiers()
							.forEach(buttonSpecifier -> buttons.put(buttonSpecifier, Button.makeButton(buttonSpecifier)));
				
		buttons.get(StartScreenButtonType.RESUME_LAST_GAME).setMarked(true);
		buttons.get(StartScreenButtonType.RESUME_LAST_GAME).setEnabled(Controller.getInstance().getSaveGame().isValid());
		
		if(Events.currentPlayerName.equals(Window.DEFAULT_PLAYER_NAME))
		{
			buttons.get(StartScreenButtonType.SETTINGS).setMarked(true);
		}
	}
	
	// Helicopter-spezifische Anpassung der Werkstatt-Button-Beschriftungen
	// TODO vielleicht können die spezifischen Beschriftungen unnötig gemacht werden, wenn gleich die richtigen Werte verwendet werden
	public static void finalizeRepairShopButtons()
	{
		Helicopter helicopter = Controller.getInstance().getHelicopter();
		buttons.get(LeftSideRepairShopButtonType.MISSION).setPrimaryLabel(dictionary.mission());
		buttons.get(LeftSideRepairShopButtonType.MISSION).updateSecondaryLabel();
		
		StandardUpgradeButtonType.getValues().forEach(buttonSpecifier -> {
			StandardUpgradeType standardUpgradeType = ((StandardUpgradeButtonType)buttonSpecifier).getStandardUpgradeType();
			if(!helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType))
			{
				buttons.get(buttonSpecifier).adjustCostsTo(helicopter.getUpgradeCostFor(standardUpgradeType));
				buttons.get(buttonSpecifier).setCostColor(helicopter.getPriceLevelFor(standardUpgradeType).getColor());
			}
		});
		
		List<String> standardUpgradeLabel = dictionary.energyAbilityImprovements();
		buttons.get(StandardUpgradeButtonType.ENERGY_ABILITY).setPrimaryLabel(String.join(" ", standardUpgradeLabel));
		
		// TODO hier die eingeführten Methoden mit Rückgabe der Preise verwenden bzw. weitere notwendige anlegen
		buttons.get(SpecialUpgradeButtonType.SPOTLIGHT).adjustCostsTo(helicopter.getSpotlightCosts());
		buttons.get(SpecialUpgradeButtonType.SPOTLIGHT).setCostColor(CHEAP.getColor());
		buttons.get(SpecialUpgradeButtonType.GOLIATH_PLATING).adjustCostsTo(helicopter.getGoliathCosts());
		buttons.get(SpecialUpgradeButtonType.GOLIATH_PLATING).setCostColor((helicopter.getType() == PHOENIX || (helicopter.getType() == HELIOS && PHOENIX.hasDefeatedFinalBoss())) ? VERY_CHEAP.getColor() : REGULAR.getColor());
		buttons.get(SpecialUpgradeButtonType.PIERCING_WARHEADS).adjustCostsTo((helicopter.getType() == ROCH || (helicopter.getType() == HELIOS && ROCH.hasDefeatedFinalBoss())) ? Helicopter.CHEAP_SPECIAL_COSTS  : Helicopter.STANDARD_SPECIAL_COSTS);
		buttons.get(SpecialUpgradeButtonType.PIERCING_WARHEADS).setCostColor((helicopter.getType() == ROCH || (helicopter.getType() == HELIOS && ROCH.hasDefeatedFinalBoss())) ? VERY_CHEAP.getColor() : REGULAR.getColor());
		buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).adjustCostsTo((helicopter.getType() == OROCHI || (helicopter.getType() == HELIOS && OROCHI.hasDefeatedFinalBoss())) ? Helicopter.CHEAP_SPECIAL_COSTS  : helicopter.getType() == ROCH ? Roch.ROCH_SECOND_CANNON_COSTS  : Helicopter.STANDARD_SPECIAL_COSTS);
		buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).setCostColor((helicopter.getType() == OROCHI || (helicopter.getType() == HELIOS && OROCHI.hasDefeatedFinalBoss())) ? VERY_CHEAP.getColor() : helicopter.getType() == ROCH ? EXPENSIVE.getColor() : REGULAR.getColor());
		buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).setPrimaryLabel(dictionary.extraCannons());
		buttons.get(SpecialUpgradeButtonType.FIFTH_SPECIAL).adjustCostsTo(helicopter.getFifthSpecialCosts());
		buttons.get(SpecialUpgradeButtonType.FIFTH_SPECIAL).setCostColor(helicopter.getType() != ROCH ? VERY_CHEAP.getColor() : CHEAP.getColor());
		buttons.get(SpecialUpgradeButtonType.FIFTH_SPECIAL).setPrimaryLabel(dictionary.fifthSpecial());
	}
	
	public static void updateRepairShopButtonsAfterSpotlightPurchase()
	{
		// TODO Enum für RepairShopButtons anlegen
		buttons.get(LeftSideRepairShopButtonType.MISSION).setPrimaryLabel(dictionary.mission());
		buttons.get(LeftSideRepairShopButtonType.MISSION).updateSecondaryLabel();
		buttons.get(SpecialUpgradeButtonType.SPOTLIGHT).adjustCostsToZero();
	}
}