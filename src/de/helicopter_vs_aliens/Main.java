package de.helicopter_vs_aliens;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Button;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.gui.Label;
import de.helicopter_vs_aliens.score.Savegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static de.helicopter_vs_aliens.util.dictionary.Languages.ENGLISH;
import static de.helicopter_vs_aliens.util.dictionary.Languages.GERMAN;

public class Main implements Constants
{
	public static final Dimension
		VIRTUAL_DIMENSION = new Dimension(1024, 461);
	
	private final static boolean
    	TESTMODE = true;
    
    private final static Dimension
		STANDARD_RESULUTION = new Dimension(1280, 720),
		WINDOW_SIZE = new Dimension(STANDARD_RESULUTION.width +6,
									STANDARD_RESULUTION.height+29); // 460 //489//new Dimension(1030, 492);//
			
    public static boolean
    	isFullScreen = true;
    
    public static Dimension
			displayShift;
    
    private static GraphicsDevice
    	device;
    
    private static JFrame
		frame;
    
    private static DisplayMode
		originalDisplayMode,
		standardDisplayMode = new DisplayMode(  STANDARD_RESULUTION.width,
    									STANDARD_RESULUTION.height,
    								  	32, 60);
	public static DisplayMode currentDisplayMode;


    public static void main(final String[] args)
    {
    	if(TESTMODE)
        {
        	/*
        	Dictionary dictionary = new Dictionary(Languages.GERMAN, HelicopterTypes.OROCHI);
            
            List<String> list = dictionary.getSpecialUpgrades();
            list.forEach(System.out::println);
            
    
            dictionary.switchLanguageTo(ENGLISH);
            
            list.forEach(System.out::println);

    
    
            dictionary.switchHelicopterTypeTo(HELIOS);
    
            list.forEach(System.out::println);
            */
   
		}
        else
        {
            final Controller controller = Controller.getInstance();
            frame = new JFrame("Helicopter vs. Aliens");
            
            frame.setBackground(Color.black);
            
            frame.add("Center", controller);
            frame.addKeyListener(controller);
            frame.setResizable(false);
            frame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    Controller.shutDown();
                }
            });

            controller.setLayout(null);
            
            device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if(!device.isFullScreenSupported()){isFullScreen = false;}
            originalDisplayMode = device.getDisplayMode();
            
            Controller.savegame = Savegame.initialize();
            
            frame.setUndecorated(true);
            device.setFullScreenWindow(frame);
            activateDisplayMode();
            switchDisplayMode(null);
            
            
            frame.setVisible(true);
            
            controller.init();
            controller.start();
        }
    }

	static void switchResolution(Savegame savegame)
	{
		Menu.originalResulution = !Menu.originalResulution;
		savegame.originalResulution = Menu.originalResulution;
		activateDisplayMode();
		Events.settingsChanged = true;
	}
    
    public static void switchDisplayMode(Button currentButton)
    {
        isFullScreen = !isFullScreen;
        
        Button.STARTSCREEN_MENU_BUTTON[ENGLISH.ordinal()][2][0]
        	= Button.DISPLAY[ENGLISH.ordinal()][isFullScreen ? 0 : 1];
		Button.STARTSCREEN_MENU_BUTTON[GERMAN.ordinal() ][2][0]
			= Button.DISPLAY[GERMAN.ordinal()][isFullScreen ? 0 : 1];
   
		if(currentButton != null)
		{
			currentButton.label
				= Button.DISPLAY[Menu.language.ordinal()][isFullScreen ? 0 : 1];
		}
		    
        frame.dispose();
        frame.setUndecorated(isFullScreen);
        
        if(isFullScreen)
		{
    		//Menu.startscreen_menu_button.get("5").enabled = true;
        	device.setFullScreenWindow(frame);
        	activateDisplayMode();
		}
		else
		{
			device.setFullScreenWindow(null);
			if(currentButton != null){
				Menu.adaptToWindowMode(displayShift);}
	        frame.setSize(WINDOW_SIZE);
	        frame.setLocation( (int)(( originalDisplayMode.getWidth()
	        					  -WINDOW_SIZE.getWidth())/2),
    					   (int)(( originalDisplayMode.getHeight()
    							  -WINDOW_SIZE.getHeight())/2));
	        frame.setVisible(true);
		}
    }
    
    private static void activateDisplayMode()
	{
    	currentDisplayMode = Menu.originalResulution
				? originalDisplayMode
				: standardDisplayMode;
    	device.setDisplayMode(currentDisplayMode);
		
		displayShift = new Dimension((currentDisplayMode.getWidth()  - VIRTUAL_DIMENSION.width )/2,
									  (currentDisplayMode.getHeight() - VIRTUAL_DIMENSION.height)/2);
		if(Menu.label == null){
			Menu.label = new Label();}
		else
		{
			Menu.label.setBounds(displayShift.width  + 42,
					  				displayShift.height + 83, 940, 240);
		}
		Controller.getInstance().backgroundRepaint = 0;
	}
}