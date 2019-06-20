package de.helicopter_vs_aliens;

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
			
    static boolean
    	isFullScreen = true;
    
    static Dimension
    	display_shift;
    
    static GraphicsDevice
    	device;
    
    static JFrame
    	f;
    
    static DisplayMode
    	dm_original,
    	dm_standard = new DisplayMode(  STANDARD_RESULUTION.width,
    									STANDARD_RESULUTION.height,
    								  	32, 60),
    	dm_current;


    public static void main(final String[] args)
    {
    	if(TESTMODE)
        {
		}
        else
        {
            final Controller app = new Controller();
            Controller.object = app;
            f = new JFrame("HelicopterDefence 1.2");
            
            f.setBackground(Color.black);
            
            f.add("Center", app);
            f.addKeyListener(app);
            f.setResizable(false);
            f.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    Controller.shut_down();
                }
            });

            app.setLayout(null);
            
            device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if(!device.isFullScreenSupported()){isFullScreen = false;}
            dm_original = device.getDisplayMode();
            
            Controller.savegame = Savegame.initialize();
            
            f.setUndecorated(true);
            device.setFullScreenWindow(f);
            activate_display_mode();
            switchDisplayMode(null);
            
            
            f.setVisible(true);
            
            app.init();
            app.start();
        }
    }

	static void switchResolution(Savegame savegame)
	{
		Menu.original_resulution = !Menu.original_resulution;
		savegame.original_resulution = Menu.original_resulution;
		activate_display_mode();
		Events.settings_changed = true;
	}
    
    static void switchDisplayMode(Button current_button)
    {
        isFullScreen = !isFullScreen;
        
        Button.STARTSCREEN_MENU_BUTTON[ENGLISH][2][0]
        	= Button.DISPLAY[ENGLISH][isFullScreen ? 0 : 1];
		Button.STARTSCREEN_MENU_BUTTON[GERMAN ][2][0]
			= Button.DISPLAY[GERMAN][isFullScreen ? 0 : 1];
   
		if(current_button != null)
		{
			current_button.label
				= Button.DISPLAY[Menu.language][isFullScreen ? 0 : 1];
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
			if(current_button != null){Menu.adapt_to_window_mode(display_shift);}
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
    	dm_current = Menu.original_resulution
				? dm_original
				: dm_standard;
    	device.setDisplayMode(dm_current);
		
		display_shift = new Dimension((dm_current.getWidth()  - VIRTUAL_DIMENSION.width )/2,
									  (dm_current.getHeight() - VIRTUAL_DIMENSION.height)/2);
		if(Menu.my_label == null){Menu.my_label = new MyLabel();}
		else
		{
			Menu.my_label.setBounds(display_shift.width  + 42,
					  				display_shift.height + 83, 940, 240);
		}
		Controller.object.bg_repaint = 0;
	}
}