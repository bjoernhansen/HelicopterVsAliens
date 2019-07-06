package de.helicopter_vs_aliens;

import de.helicopter_vs_aliens.gui.Button;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.gui.Label;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main implements Constants
{
	public static final Dimension
		VIRTUAL_DIMENSION = new Dimension(1024, 461);
	
	private final static boolean
    	TESTMODE = false;
    
    private final static Dimension
		STANDARD_RESULUTION = new Dimension(1280, 720),
		WINDOW_SIZE = new Dimension(STANDARD_RESULUTION.width +6,
									STANDARD_RESULUTION.height+29); // 460 //489//new Dimension(1030, 492);//
			
    public static boolean
    	isFullScreen = true;
    
    public static Dimension
			displayShift;
    
    static GraphicsDevice
    	device;
    
    static JFrame
    	f;
    
    static DisplayMode
    	dm_original;
	static DisplayMode dm_standard = new DisplayMode(  STANDARD_RESULUTION.width,
    									STANDARD_RESULUTION.height,
    								  	32, 60);
	public static DisplayMode dm_current;


    public static void main(final String[] args)
    {
    	if(TESTMODE)
        {
		}
        else
        {
            final Controller controller = Controller.getInstance();
            f = new JFrame("HelicopterDefence 1.2");
            
            f.setBackground(Color.black);
            
            f.add("Center", controller);
            f.addKeyListener(controller);
            f.setResizable(false);
            f.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    Controller.shut_down();
                }
            });

            controller.setLayout(null);
            
            device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if(!device.isFullScreenSupported()){isFullScreen = false;}
            dm_original = device.getDisplayMode();
            
            Controller.savegame = Savegame.initialize();
            
            f.setUndecorated(true);
            device.setFullScreenWindow(f);
            activate_display_mode();
            switchDisplayMode(null);
            
            
            f.setVisible(true);
            
            controller.init();
            controller.start();
        }
    }

	static void switchResolution(Savegame savegame)
	{
		de.helicopter_vs_aliens.gui.Menu.original_resulution = !de.helicopter_vs_aliens.gui.Menu.original_resulution;
		savegame.original_resulution = de.helicopter_vs_aliens.gui.Menu.original_resulution;
		activate_display_mode();
		Events.settings_changed = true;
	}
    
    static void switchDisplayMode(de.helicopter_vs_aliens.gui.Button current_button)
    {
        isFullScreen = !isFullScreen;
        
        de.helicopter_vs_aliens.gui.Button.STARTSCREEN_MENU_BUTTON[ENGLISH][2][0]
        	= de.helicopter_vs_aliens.gui.Button.DISPLAY[ENGLISH][isFullScreen ? 0 : 1];
		de.helicopter_vs_aliens.gui.Button.STARTSCREEN_MENU_BUTTON[GERMAN ][2][0]
			= de.helicopter_vs_aliens.gui.Button.DISPLAY[GERMAN][isFullScreen ? 0 : 1];
   
		if(current_button != null)
		{
			current_button.label
				= Button.DISPLAY[de.helicopter_vs_aliens.gui.Menu.language][isFullScreen ? 0 : 1];
		}
		    
        f.dispose();
        f.setUndecorated(isFullScreen);
        
        if(isFullScreen)
		{
    		//Menu.startscreen_menu_button.get("5").enabled = true;
        	device.setFullScreenWindow(f);
        	activate_display_mode();
		}
		else
		{
			device.setFullScreenWindow(null);
			if(current_button != null){
				de.helicopter_vs_aliens.gui.Menu.adapt_to_window_mode(displayShift);}
	        f.setSize(WINDOW_SIZE);
	        f.setLocation( (int)(( dm_original.getWidth()
	        					  -WINDOW_SIZE.getWidth())/2),
    					   (int)(( dm_original.getHeight()
    							  -WINDOW_SIZE.getHeight())/2));
	        f.setVisible(true);
		}
    }
    
    private static void activate_display_mode()
	{
    	dm_current = de.helicopter_vs_aliens.gui.Menu.original_resulution
				? dm_original
				: dm_standard;
    	device.setDisplayMode(dm_current);
		
		displayShift = new Dimension((dm_current.getWidth()  - VIRTUAL_DIMENSION.width )/2,
									  (dm_current.getHeight() - VIRTUAL_DIMENSION.height)/2);
		if(de.helicopter_vs_aliens.gui.Menu.label == null){
			de.helicopter_vs_aliens.gui.Menu.label = new Label();}
		else
		{
			Menu.label.setBounds(displayShift.width  + 42,
					  				displayShift.height + 83, 940, 240);
		}
		Controller.getInstance().bgRepaint = 0;
	}
}