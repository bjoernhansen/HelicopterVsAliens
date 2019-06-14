package de.helicopter_vs_aliens;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JTextPane;

import de.helicopter_vs_aliens.helicopter.Helicopter;
import de.helicopter_vs_aliens.helicopter.HelicopterTypes;
import enemy.Enemy;

import static de.helicopter_vs_aliens.helicopter.HelicopterTypes.*;

public class Menu implements Constants, Fonts
{
	private static final String
		VERSION = "Version 1.3.4";		// Spielversion
	
	private static final int
		SCORESCREEN_SPACE_BETWEEN_ROWS = 30, 
    	SCORESCREEN_X_POS_1 = 351, 
    	SCORESCREEN_X_POS_2 = 633, 
    	SCORESCREEN_Y_POS = 129,    	
    			
        SETTING_LEFT = 80,
        SETTING_COLUMN_SPACING = 145,
        SETTING_LINE_SPACING = 40,
        SETTING_TOP = 130;

	public static final String
			GAME_NAME = "Helicopter vs. Aliens";

	public static int 
		page, 							// ausgewählte Seite im Startscreen-Menü
		language = ENGLISH, 			// Sprache; = 0: English; = 1: German
		helicopter_selection,			// Helicopterauswahl im Startscreen-Menü
		level_display_timer,			// reguliert die Anzeigezeit der Levelanzeige nach einem Level-Up
		fps,							// Frames Per Second; wird über die FPS-Anzeige ausgegeben
		special_info_selection = 0,		// nur für Debugging: Auswahl der anzuzeigenden Informationen
		cross_pos,						// Position des Block-Kreuzes
		percent_health_display_length,	// Länge des String zur prozentualen Anzeige der Helikopter-Lebenspunkte
	
		// Menu-Timer
		message_timer, 					// regulieren die Dauer [frames] der Nachrichtenanzeige im Werkstatt-Menü
		money_display_timer,			// regulieren die Dauer [frames] der Geld-Anzeige im Spiel
		cross_timer,					// regulieren die Dauer [frames] der Block-Kreuz-Anzeige auf dem Startscreen
		unlocked_timer,					// regulieren die Dauer [frames] der Anzeige des freigeschalteten Helicopters 	
		effect_timer[] = new int[Helicopter.NR_OF_TYPES];	// regulieren die Helikopter-Animationen im Startscreen-Menü
  
	public static HelicopterTypes
		unlocked_type;					// Typ des freigeschalteten Helicopters
	
	public static boolean
		menue_visible,					// = true: Spielmenü ist sichtbar
		original_resulution = false,
		highlighted_helicopter = false;
	
    // Auf dem Startscreen gezeichnete Polygone
    static Polygon 
    	cross, 							// das rote Block-Kreuz
		triangle[] = new Polygon[2];	// die grönen Dreiecke auf dem Startscreen
           					
     // Konstanten   
    static final int	
    	STARTSCREEN_OFFSET_X = 4;				// x-Verschiebung der meisten Anzeigen des Startscreens
    
    private static final int 	
    	STATUS_BAR_X1 = 268, 					// x-Postion der Schrift in der Statusanzeige (Werkstatt-Menü; erste Spalte)
    	STATUS_BAR_X2 = 421, 					// x-Postion der Schrift in der Statusanzeige (Werkstatt-Menü; zweite Spalte)
    	STANDUP_OFFSET_Y = 148, 				// y-Verschiebung der Standard-Upgrades in der Statusanzeige (Werkstatt-Menü)
    	SPECUP_OFFSET_Y = 328,					// y-Verschiebung der Spezial-Upgrades in der Statusanzeige (Werkstatt-Menü)
    	HEALTH_BAR_LENGTH = 150,				// Länge des Hitpoint-Balken des Helikopters
    	HTML_SIZE = 5,							// Standard-Schriftgröße der Startscreen-Menuetexte
		HELICOPTER_DISTANCE = 250,				// Abstand zwischen den Helikopter auf dem Startscreen
		STARTSCREEN_HELICOPTER_OFFSET_Y = 33,	// y-Verschiebung der Helicopter-Leiste im Startscreen-Menü
		BONUS_MAX_DISPLAY_TIME = 200,			// Maximale Anzeigezeit des zuletzt erhalten Bonus
		CROSS_MAX_DISPLAY_TIME = 60,   			// Maximale Anzeigezeit des Block-Kreuzes (Startscreen)
		UNLOCKED_DISPLAY_TIME = 300,
    	ENEMY_HEALTH_BAR_WIDTH = 206;	
    
    static final String  
    	HELICOPTER_INFOS[][][] = 
    	{{{	"Phoenix",						"The most remarkable feature", 	
			"of Phoenix type helicopters", 	"is their heavy plating."}, 
		{	"Roch",							"Roch type helicopters are", 	
			"well known for their",			"massive firepower."},
		{	"Orochi", 						"An excellent maneuverability",	
			"is the striking feature of", 	"Orochi type helicopters."},
		{	"Kamaitachi", 					"Kamaitachi type helicopters", 
			"can attack with incredible",   "high fire rate."},
		{	"Pegasus", 						"Pegasus type helicopters",
			"are feared because of their",  "EMP generator."},
		{	"Helios", 						"Helios type helicopters",
			"are specialized in",			"using PowerUps."}},
		{{	"Phönix", 						"Helikopter der Phönix-Klasse",
			"zeichnen sich durch eine", 	"äußerst hohe Panzerung aus."},
		{	"Roch", 						"Helikopter der Roch-Klasse",
			"sind für ihre ausgesprochen",	"hohe Feuerkraft bekannt."},
		{	"Orochi", 						"Helikopter der Orochi-Klasse", 
			"haben eine ungewöhnlich", 		"gute Manovrierfähigkeit."},
		{	"Kamaitachi", 					"Helikopter der Kamaitachi-",
			"Klasse überzeugen durch", 		"besonders hohe Schussrate."},
		{	"Pegasus", 						"Helikopter der Pegasus-", 
			"Klasse sind wegen ihres", 		"EMP-Generators gefürchtet."},
		{	"Helios", 						"Helikopter der Helios-Klasse", 
			"sind auf den Einsatz von",		"PowerUps spezialisiert."}}},
    
    	SPECIALS[][] =
    	{{	"Spot light", 					"Goliath plating", 
    		"Piercing warheads", 			"Second cannon", 
    		"Short-range radiation", 		"Jumbo missiles", 
    		"Radar device", 				"Rapid fire upgrade", 
    		"Interphase generator", 		"Power-up immobilizer"},
    	{	"Scheinwerfer",					"Goliath-Panzerung",
    		"Durchstoßsprengköpfe",			"zweite Boardkanone",
    		"Nahkampf-Bestrahlung",			"Jumbo-Raketen",
    		"Radar-Vorrichtung",			"Schnellfeuer-Upgrade",
    		"Interphasengenerator",			"PowerUp-Stopper"}},
    
    	PRICE_LEVELS[][] =
    	{{	"very cheap", 
    		"cheap",
    		"medium", 
    		"expensive", 			
    		"extortionate"},
    	{	"sehr güstig",
    		"günstig",
    		"normal",		
    		"teuer",
    		"sehr teuer"}},
    	
    	HIGHSCORE_COLUMN_NAME[][] =
		{{	"Rank", 					
			"Player", 
    		"Type", 			
    		"Max Level", 
    		"Playing time", 		
    		"Crashs", 
    		"Repairs", 		
    		"Bonuses"},
    	{	"Rang",					
    		"Spieler",
    		"Typ",			
    		"Höchstlevel",
    		"Spielzeit",			
    		"Abstürze",
    		"Reparaturen",			
    		"Extra-Boni"}},
    	       		
    	THIRD_CANNON[] = 			{"Third cannon", "Dritte Bordkanone"},
    	SECOND_AND_THIRD_CANNON[] = {"Second and third cannon", 
                               		 "zweite und dritte Boardkanone"},
        PLAYING_TIME[] =			{"Playing time: ", "Spielzeit: "},
        LEVEL[] =					{"Level", "Stufe"};
        
    // Menu Objects
    static String 
    	repair_shop_time,
    	highlighted_button = "",
    	message[] = new String [4];    
    
    static GradientPaint  [] 
    	myRahmen = 	new GradientPaint[4];	    
    
    static HashMap<String, Button> 
    	inGame_button = new HashMap<>(),
    	startscreen_button = new HashMap<>(), 
    	startscreen_menu_button = new HashMap<>();
	
	public static HashMap<String, Button>
		repair_shop_button = new HashMap<>();
    
    static MyLabel 	   my_label;
	public static PowerUp[]   collected_PowerUp = new PowerUp [4];
    static Rectangle[] helicopter_frame  = new Rectangle[4];
    
    
    static void initialize_menu(Helicopter helicopter)
    {
    	helicopter_selection = (3 + MyMath.random(Helicopter.NR_OF_TYPES-1))
    						   %Helicopter.NR_OF_TYPES;
    	
    	int px1[] = {19 , 19 , 5};
    	int px2[] = {1004, 1004, 1018};
	    int py[] = {261 + STARTSCREEN_HELICOPTER_OFFSET_Y, 
	                331 + STARTSCREEN_HELICOPTER_OFFSET_Y ,
	                296 + STARTSCREEN_HELICOPTER_OFFSET_Y};
	    triangle[0] = new Polygon(px1, py, 3);
	    triangle[1] = new Polygon(px2, py, 3);
	    
	    Button.initialize_buttons(helicopter);
	    for(int i = 0; i < 4; i++)
	    {
	    	helicopter_frame[i] 
	    	    = new Rectangle(  30 + STARTSCREEN_OFFSET_X 
	    							 + i * HELICOPTER_DISTANCE, 
	    						 239 + STARTSCREEN_HELICOPTER_OFFSET_Y, 206, 116);
	    }
    }    
	    
    static void paint_GUI(Graphics2D g2d, Helicopter helicopter)
	{
		// Werkstatt-Button						
		if(Events.restart_window_visible)
		{
			boolean game_over;
			if(	Events.money > Events.repair_fee(helicopter, helicopter.damaged) 
				&& Events.level < 51)					
			{
				game_over = false;
			}
			else
			{
				game_over = true;
			}
			paint_restart_window(g2d, helicopter, game_over);
		}	
		else
		{	
			if(helicopter.is_on_the_ground())
			{
				inGame_button.get("RepairShop").paint(g2d);
				inGame_button.get("MainMenu").paint(g2d);
				
			}
			if(menue_visible){paint_ingame_menue(g2d);}
		}		
	}
	
	private static void paint_ingame_menue(Graphics2D g2d)
    {      
    	paint_frame(g2d,363, 77, 256, 231, MyColor.golden);         
        g2d.setColor(MyColor.red);
        g2d.setFont(PLAIN25);
        g2d.drawString(Button.MAIN_MENU[language], 422 + (language == ENGLISH ? 6 : 0), 106);
        inGame_button.get("MMNewGame1").paint(g2d);  
        inGame_button.get("MMStopMusic").paint(g2d); 
        inGame_button.get("MMNewGame2").paint(g2d); 
        inGame_button.get("MMCancel").paint(g2d);         
    }
    
    /** Update- and Paint-Methoden */
	
    
    
    
    static void update_startscreen(Helicopter helicopter, int counter)
	{    	
    	MyColor.calculate_variable_game_colors(counter);
    	identify_highlighted_buttons(helicopter, startscreen_button);       
        if(cross_timer > CROSS_MAX_DISPLAY_TIME){cross_timer = 0;}
        else if(cross_timer > 0 ){cross_timer++;}        
        if(message_timer != 0){message_timer++;}
		if(message_timer > 215){message_timer = 0;}
        for(int i = 0; i < Helicopter.NR_OF_TYPES; i++)
        {	        	
        	if(effect_timer[i] > 0){effect_timer[i]++;}
        }      	
		highlighted_helicopter = false;
		for(int i = 0; i < 4; i++)
		{
			if(	helicopter_frame[i].contains(helicopter.destination))
			{
				helicopter.type = HelicopterTypes.values()[(i + helicopter_selection)%Helicopter.NR_OF_TYPES];
				if(effect_timer[helicopter.type.ordinal()] == 0)
				{									
						 if(helicopter.type == PHOENIX)	  {Audio.play(Audio.teleport1);}
					else if(helicopter.type == ROCH)	  {Audio.play(Audio.shield_up);}
					else if(helicopter.type == OROCHI)	  {Audio.play(Audio.stun_activated);}
					else if(helicopter.type == KAMAITACHI){Audio.play(Audio.plasma_on);}
					else if(helicopter.type == PEGASUS)
					{
						Audio.play(Audio.emp);							
						helicopter.emp_wave 
							= new Explosion(149 
											+ STARTSCREEN_OFFSET_X 
											+ i * HELICOPTER_DISTANCE, 
											310 
											+ STARTSCREEN_HELICOPTER_OFFSET_Y);
					}
					else if(helicopter.type == HELIOS){Audio.play(Audio.shield_up);}
					Arrays.fill(effect_timer, 0);
					effect_timer[helicopter.type.ordinal()] = 1;
				}
				if(helicopter.type == KAMAITACHI && effect_timer[KAMAITACHI.ordinal()] == 70)
				{
					Audio.play(Audio.plasma_off);
				}				
				highlighted_helicopter = true;
				break;
			}			
		}		
		if(highlighted_helicopter){helicopter.rotate_propeller(7);}
		else{Arrays.fill(effect_timer, 0);}	
		
	}

	static void paint_startscreen(Graphics2D g2d, Helicopter helicopter)
    {    	    	
    	g2d.setPaint(MyColor.gradientVariableWhite);
        g2d.setFont(PLAIN80);        
        g2d.drawString(GAME_NAME, 512 - g2d.getFontMetrics().stringWidth(GAME_NAME)/2, 85);
                       
        g2d.setColor(Color.yellow);
        g2d.setFont(BOLD16);
        g2d.drawString(VERSION, 1016 - g2d.getFontMetrics().stringWidth(VERSION), 20);
        g2d.setColor(MyColor.darkGray);
        g2d.setFont(BOLD_ITALIC15);
        g2d.drawString((language == ENGLISH ? "developed by" : "ein Spiel von") + " Björn Hansen", 505, 120);
        
        
        if(message_timer == 0)
        {
        	g2d.setColor(MyColor.variableYellow);
        	g2d.setFont(PLAIN29);        
        	String temp_string = language == ENGLISH ? "Select a helicopter!" : "Wählen Sie einen Helikopter aus!";
        	g2d.drawString(temp_string, (512 - g2d.getFontMetrics().stringWidth(temp_string)/2), 185);
        }
        else
        {
        	g2d.setColor(MyColor.golden);
        	g2d.setFont(BOLD16);         	
        	
        	g2d.drawString(message[0], (512 - g2d.getFontMetrics().stringWidth(message[0])/2), 160);
        	g2d.drawString(message[1], (512 - g2d.getFontMetrics().stringWidth(message[1])/2), 185);
        	g2d.drawString(message[2], (512 - g2d.getFontMetrics().stringWidth(message[2])/2), 210);
        }                
         
        for(int i = 0; i < 4; i++)
        {
        	if(helicopter.type.ordinal() == (helicopter_selection+i)%Helicopter.NR_OF_TYPES && highlighted_helicopter){g2d.setColor(Color.white);}
            else{g2d.setColor(MyColor.lightGray);}       	
        	g2d.setFont(BOLD20);
        	
        	String class_name = HELICOPTER_INFOS[language][(helicopter_selection+i)%Helicopter.NR_OF_TYPES][0] + (language == 0 ? " type" : "-Klasse");
        	int sw = g2d.getFontMetrics().stringWidth(class_name);        	
        	g2d.drawString(class_name, 30 + STARTSCREEN_OFFSET_X + i * HELICOPTER_DISTANCE + (206-sw)/2, 225 + STARTSCREEN_HELICOPTER_OFFSET_Y);       
        	
        	g2d.setFont(new Font("Dialog", Font.BOLD, 15));
            g2d.drawString(HELICOPTER_INFOS[language][(helicopter_selection+i)%Helicopter.NR_OF_TYPES][1], 29 + STARTSCREEN_OFFSET_X + i * HELICOPTER_DISTANCE, 380 + STARTSCREEN_HELICOPTER_OFFSET_Y);
            g2d.drawString(HELICOPTER_INFOS[language][(helicopter_selection+i)%Helicopter.NR_OF_TYPES][2], 29 + STARTSCREEN_OFFSET_X + i * HELICOPTER_DISTANCE, 400 + STARTSCREEN_HELICOPTER_OFFSET_Y);
            g2d.drawString(HELICOPTER_INFOS[language][(helicopter_selection+i)%Helicopter.NR_OF_TYPES][3], 29 + STARTSCREEN_OFFSET_X + i * HELICOPTER_DISTANCE, 420 + STARTSCREEN_HELICOPTER_OFFSET_Y);          
            
            if(	helicopter_frame[i].contains(helicopter.destination))
            {
            	paint_frame(g2d, helicopter_frame[i], MyColor.darkBlue);
            }
            helicopter.paint(g2d, 66 + STARTSCREEN_OFFSET_X + i * HELICOPTER_DISTANCE, 262 + STARTSCREEN_HELICOPTER_OFFSET_Y, HelicopterTypes.values()[(helicopter_selection+i)%Helicopter.NR_OF_TYPES], 1);
            if(!helicopter_frame[i].contains(helicopter.destination.x, helicopter.destination.y))
            {
            	paint_frame(g2d, helicopter_frame[i], MyColor.translucentBlack);
            }
            if(Events.all_playable || Helicopter.is_unlocked(HelicopterTypes.values()[(helicopter_selection+i)%Helicopter.NR_OF_TYPES]))
            {
            	paint_tickmark(g2d, i, 210, 323, 15, 20);
            }
        }
        
        // die Buttons        
        for(int i = 0; i < 2; i++){for(int j = 0; j < 3; j++){startscreen_button.get(Integer.toString(i)+j).paint(g2d);}}
                   
        // die grünen Pfeile
        for(int i = 0; i < 2; i++)
        {
        	if(triangle[i].contains(helicopter.destination.x, helicopter.destination.y)){g2d.setColor(Color.green);}
            else{g2d.setColor(MyColor.arrowGreen);}
    	    g2d.fillPolygon(triangle[i]);
    	    g2d.setColor(MyColor.darkArrowGreen);
    	    g2d.drawPolygon(triangle[i]);
        }
	    
	    if(cross_timer > 0)
	    {
	    	int alpha = (int)(255*(((double)CROSS_MAX_DISPLAY_TIME-cross_timer)/(CROSS_MAX_DISPLAY_TIME/2)));
	    	if(cross_timer < CROSS_MAX_DISPLAY_TIME/2){g2d.setColor(Color.red);}
		    else{g2d.setColor(MyColor.setAlpha(Color.red, alpha));}	    	
		    g2d.fill(cross);	    
		    if(cross_timer < CROSS_MAX_DISPLAY_TIME/2){g2d.setColor(MyColor.red);}
		    else{g2d.setColor(MyColor.setAlpha(MyColor.red, alpha));}
		    g2d.setStroke(new BasicStroke(2));
		    g2d.draw(cross);
		    g2d.setStroke(new BasicStroke(1));
	    }	    
    }    
    
    private static void paint_tickmark(Graphics2D g2d, int i, int x, int y, int w, int h)
	{
    	Enemy.paint_energy_beam(g2d, 
				x + STARTSCREEN_OFFSET_X + i * HELICOPTER_DISTANCE, 
				y + h/2 + STARTSCREEN_HELICOPTER_OFFSET_Y, 
				x + w/3 + STARTSCREEN_OFFSET_X + i * HELICOPTER_DISTANCE, 
				y + h + STARTSCREEN_HELICOPTER_OFFSET_Y);

    	Enemy.paint_energy_beam(g2d, 
				x + w + STARTSCREEN_OFFSET_X + i * HELICOPTER_DISTANCE, 
				y + STARTSCREEN_HELICOPTER_OFFSET_Y, 
				x + w/3 + STARTSCREEN_OFFSET_X + i * HELICOPTER_DISTANCE, 
				y + h + STARTSCREEN_HELICOPTER_OFFSET_Y);
	}
    
    static void update_and_paint_startscreen_menu(Graphics2D g2d, 
                                                  Helicopter helicopter, 
                                                  int counter)
    {    	
    	g2d.setColor(Color.white);
        g2d.setFont(PLAIN29);  
        if(Events.window == SETTINGS)
        {
        	g2d.drawString(language == ENGLISH ? "Settings" : "Optionen", 40, 55);           
        }
        else{g2d.drawString(startscreen_menu_button.get(Integer.toString(page)).label, 40, 55);}
       
        paint_frame_line(g2d, 26, 67, 971); 
        paint_frame(g2d, 26, 21, 971, 317); 
                
        // die Buttons        
        boolean loner = startscreen_menu_button.get("1").label.equals("") ? true : false;
        for(int m = 0; m < 8; m++)
		{
        	startscreen_menu_button.get(Integer.toString(m)).paint(g2d, loner);
        }
        startscreen_menu_button.get("Cancel").paint(g2d);
        
        if(Events.window == HELICOPTER_TYPES)
        {
        	if(page > 1 && page < 2 + Helicopter.NR_OF_TYPES)
        	{
        		helicopter.menue_paint(g2d, HelicopterTypes.values()[page-2]);
        	}
        	else if(page == 1)
        	{
        		String temp_string = "";
            	for(int i = 0; i < 7; i++)
            	{
            		for(int j = 0; j < Helicopter.NR_OF_TYPES + 1; j++)
            		{
            			if(j == 0 && i!=0)
            			{
            				g2d.setColor(MyColor.golden);
            				temp_string = Button.STANDARD_UPGRADE_LABEL[language][i-1][1-language];       				
            			}
            			else if(j != 0 && i==0)
            			{
            				g2d.setColor(MyColor.brighten_up(MyColor.helicopterColor[j-1][0]));
            				temp_string = HELICOPTER_INFOS[language][j-1][0];          				
            			}
            			else if(j == 6 && i != 0)
            			{
            				g2d.setColor(MyColor.costsColor[Helicopter.helios_costs(i-1)]);
            				temp_string = PRICE_LEVELS[language][Helicopter.helios_costs(i-1)];
            			}
            			else if(i != 0 && j != 0)
            			{
            				g2d.setColor(MyColor.costsColor[Helicopter.COSTS[j-1][i-1]]);
            				temp_string = PRICE_LEVELS[language][Helicopter.COSTS[j-1][i-1]];
            			}
            			g2d.drawString(temp_string, 200 + (j-1) * 135, 140 + (i == 0 ? 0 : 5) + (i-1) * 32);
            		}
            	}
        	}
        }
        else if(Events.window == HIGHSCORE)
        {       	
        	if(page == 0)
        	{
        		String temp_string = "";
            	for(int i = 0; i < 6; i++)
            	{
            		for(int j = 0; j < Helicopter.NR_OF_TYPES + 1; j++)
            		{
            			if(j == 0 && i!=0)
            			{
            				g2d.setColor(MyColor.golden);
            				temp_string = "Boss " + i;       				
            			}
            			else if(j != 0 && i==0)
            			{
            				g2d.setColor(MyColor.brighten_up(MyColor.helicopterColor[j-1][0]));
            				temp_string = HELICOPTER_INFOS[language][j-1][0];          				
            			}
            			else if(i != 0 && j != 0)
            			{
            				g2d.setColor(Color.white);
            				if(i==1)
            				{
            					temp_string = Events.record_time[j-1][i-1] == 0 ? "" : Long.toString(Events.record_time[j-1][i-1]) + " min";
            				}
            				else
            				{
            					temp_string = Events.record_time[j-1][i-1] == 0 ? "" : Long.toString(Events.record_time[j-1][i-1]-Events.record_time[j-1][i-2]) + " min";
            				}
            				
            			}
            			g2d.drawString(temp_string, 200 + (j-1) * 135, 150 + (i-1) * 35);
            		}
            	}
        	}
        	else
        	{
        		if(page > 1 && page < 2 + Helicopter.NR_OF_TYPES)
            	{
            		helicopter.menue_paint(g2d, HelicopterTypes.values()[page-2]);
            	}        		
        		int column_distance = 114/*135*/, top_line = 125, line_distance = 21, left_column = 55, real_left_column = left_column, x_shift = 10;
        		    			
        		g2d.setColor(Color.lightGray);    			
        		for(int i = 0; i < 8; i++)
    			{
    				if(i == 1){real_left_column = left_column - 46;}
    				else if(i == 2){real_left_column = left_column + 42;}
    				g2d.drawString(HIGHSCORE_COLUMN_NAME[language][i], 	real_left_column + i * column_distance, top_line - line_distance);
    			}
    			
        		for(int j = 0; j < HighscoreEntry.NO_OF_ENTRIES; j++)
    			{
    				HighscoreEntry temp_entry = Events.highscore[page==1?6:page-2][j];
    				
        			if(temp_entry != null)
    				{    					
        				g2d.setColor(Color.white);
        				g2d.drawString(to_string_with_space(j+1, false), left_column + x_shift , top_line + j * line_distance);
        				g2d.drawString(temp_entry.player_name, left_column - 46 + x_shift + column_distance, top_line + j * line_distance); 				 		
        				g2d.setColor(MyColor.brighten_up(MyColor.helicopterColor[temp_entry.helicopterType.ordinal()][0]));
        				g2d.drawString(HELICOPTER_INFOS[language][temp_entry.helicopterType.ordinal()][0],   real_left_column + x_shift + 2 * column_distance, top_line + j * line_distance);
        				g2d.setColor(temp_entry.max_level > 50 ? MyColor.hs_green : MyColor.hs_red);
        				int print_level = temp_entry.max_level > 50 ? 50 : temp_entry.max_level;
        				g2d.drawString(to_string_with_space(print_level), real_left_column + x_shift + 3 * column_distance, top_line + j * line_distance); 	
    					g2d.setColor(Color.white);
        				g2d.drawString(to_string_with_space((int)temp_entry.playing_time) + " min", real_left_column + x_shift + 4 * column_distance, top_line + j * line_distance); 
    					g2d.drawString(to_string_with_space(temp_entry.crashes), 		  				  real_left_column + x_shift + 5 * column_distance, top_line + j * line_distance); 
    					g2d.drawString(to_string_with_space(temp_entry.repairs),		  				  real_left_column + x_shift + 6 * column_distance, top_line + j * line_distance); 
    					g2d.setColor(MyColor.percent_color(2*temp_entry.bonus_income));
    					g2d.drawString(to_string_with_space(temp_entry.bonus_income) + "%",		  real_left_column + x_shift + 7 * column_distance, top_line + j * line_distance); 
    				}
        			else break;
    			}
        	}        	
        }        
        else if(Events.window == SETTINGS)
        {        	
        	g2d.setFont(Fonts.PLAIN18);
        	
        	g2d.setColor(MyColor.lightestGray);
        	g2d.drawString(language == ENGLISH ? "Display:"		: "Darstellung: "	, SETTING_LEFT		, SETTING_TOP);
        	g2d.drawString(language == ENGLISH ? "Music:" 		: "Musik: "			, SETTING_LEFT		, SETTING_TOP + 1 * SETTING_LINE_SPACING);        	
        	g2d.drawString(language == ENGLISH ? "Antialiasing:": "Kantenglättung: ", SETTING_LEFT		, SETTING_TOP + 2 * SETTING_LINE_SPACING);
        	g2d.drawString(language == ENGLISH ? "Language: " 	: "Sprache: "		, SETTING_LEFT		, SETTING_TOP + 3 * SETTING_LINE_SPACING);
        	g2d.drawString(language == ENGLISH ? "Player name:"	: "Spielername:"	, SETTING_LEFT		, SETTING_TOP + 4 * SETTING_LINE_SPACING);
        	        	
        	g2d.setColor(MyColor.golden);
        	g2d.drawString( Button.DISPLAY[language][Main.isFullScreen ? 1 : 0] 
        				 	  + (!Main.isFullScreen ? "" : " (" 
        				 		   + (original_resulution 
        				 			   ? Main.dm_current.getWidth() 
        				 				   + "x" 
        				 				   + Main.dm_current.getHeight() 
        				 			   : "1280x720") + ")"),
        				 	SETTING_LEFT + SETTING_COLUMN_SPACING,
        				 	SETTING_TOP);
        	
        	g2d.setColor(Audio.sound_on ? Color.green : Color.red);
        	g2d.drawString( activation_state(Audio.sound_on)						, SETTING_LEFT + SETTING_COLUMN_SPACING	, SETTING_TOP + 1 * SETTING_LINE_SPACING);        	
        	if(Audio.MICHAEL_MODE && Audio.sound_on)
        	{
        		g2d.setColor(MyColor.golden);
        		g2d.drawString("(" + (Audio.standard_bg_music ? "Classic" : "Michael" + (language == ENGLISH ? " mode" : "-Modus")) + ")", SETTING_LEFT + SETTING_COLUMN_SPACING + 25, SETTING_TOP + 1 * SETTING_LINE_SPACING);
        	}        	
        	
        	g2d.setColor(Controller.antialiasing ? Color.green : Color.red);
        	g2d.drawString(activation_state(Controller.antialiasing)			, SETTING_LEFT + SETTING_COLUMN_SPACING	, SETTING_TOP + 2 * SETTING_LINE_SPACING);
        	
        	g2d.setColor(MyColor.golden);
        	g2d.drawString(language == ENGLISH ? "English" : "Deutsch"				, SETTING_LEFT + SETTING_COLUMN_SPACING	, SETTING_TOP + 3 * SETTING_LINE_SPACING);
        	
        	if(page == 4){g2d.setColor(Color.white);}
        	g2d.drawString(HighscoreEntry.current_player_name, SETTING_LEFT + SETTING_COLUMN_SPACING, SETTING_TOP + 4 * SETTING_LINE_SPACING);	
        	        	
        	if(page == 4 && (counter/30)%2 == 0){g2d.drawString("|", SETTING_LEFT + SETTING_COLUMN_SPACING + g2d.getFontMetrics().stringWidth(HighscoreEntry.current_player_name), SETTING_TOP + 4 * SETTING_LINE_SPACING);}
        
        }        
        else if(Events.window == DESCRIPTION && page == 5)
        {
        	int x = 52, y = 120, y_offset = 35, size = PowerUp.MENU_SIZE;
        	PowerUp.paint(g2d, x, y + 0 * y_offset, size, size, Color.orange, MyColor.golden);
        	PowerUp.paint(g2d, x, y + 1 * y_offset, size, size, Color.blue, Color.cyan);
        	PowerUp.paint(g2d, x, y + 2 * y_offset, size, size, Color.white, Color.red);
        	PowerUp.paint(g2d, x, y + 3 * y_offset, size, size, Color.green, Color.yellow);
        	PowerUp.paint(g2d, x, y + 4 * y_offset, size, size, Color.magenta, Color.black);
        	PowerUp.paint(g2d, x, y + 5 * y_offset, size, size, Color.red, Color.orange);
        }
    }    
  
	private static String to_string_with_space(int value)
    {
    	return to_string_with_space(value, true);
    }
    private static String to_string_with_space(int value, boolean big)
	{		
		return (big	? (value >= 100 ? "" : "  ") : "") 
				+ (value >= 10 ? "" : "  ") 
				+ value;
	}
   
   
    
    static void update_repair_shop(Helicopter helicopter)
	{
    	identify_highlighted_buttons(helicopter, repair_shop_button);    	   	
		
		if(message_timer != 0){message_timer++;}
		if(message_timer > 110)
		{
			message[0] = message[1] = message[2] = message[3] = "";
			message_timer = 0;
		}
		if(!helicopter.damaged){helicopter.rotate_propeller(7);}		
	}

	static void 
    paint_repair_shop(Graphics2D g2d, Helicopter helicopter)
    {     	
    	// allgmeine Anzeigen
    	g2d.setPaint(MyColor.gradientVariableWhite);
        g2d.setFont(PLAIN52);
        String input_string = language == ENGLISH ? "Repair shop" : "Werkstatt";        
        g2d.drawString(input_string, 251 + (285 - g2d.getFontMetrics().stringWidth(input_string))/2, 65);
        g2d.setColor(MyColor.lightOrange);
        g2d.setFont(PLAIN22);
        g2d.drawString((language == ENGLISH ? "Credit: " : "Guthaben: ") + Events.money + " €", 27, 35);
        g2d.drawString((language == ENGLISH ? "Current level: " : "aktuelles Level: ") + Events.level, 562, 35);
        g2d.setFont(PLAIN18);       
        g2d.drawString(PLAYING_TIME[language] + repair_shop_time, 27, 75);
              
        // Helicopter-Anzeige
        paint_helicopter_display(g2d, helicopter, 0, 10); //58
        
        // Reparatur-Button
        repair_shop_button.get("RepairButton").paint(g2d); 
        
        // Die Einsätze
        g2d.setColor(Color.yellow);
        g2d.setFont(BOLD20);
        g2d.drawString(language == ENGLISH ? "Mission:" : "Einsatz:", 27, 382);
   
        repair_shop_button.get("Einsatz").paint(g2d);       
                       
        // Die Status-Leiste     
        paint_frame(g2d, 251, 117, 285, 326);
        
        g2d.setColor(Color.yellow);
        g2d.setFont(BOLD20);
        g2d.drawString(language == ENGLISH ? "Status bar:" : "Status-Übersicht:", 255, 102);
        
        g2d.setColor(MyColor.lightOrange);
        g2d.setFont(BOLD16);        
        g2d.drawString(language == ENGLISH ? "State:" : "Zustand:", STATUS_BAR_X1, STANDUP_OFFSET_Y - 5);
        
        if(helicopter.damaged)
        {            
            g2d.setColor(Color.red);
            g2d.drawString(language == ENGLISH ? "damaged" : "beschädigt", STATUS_BAR_X2, STANDUP_OFFSET_Y - 5);
        }
        else
        {
            g2d.setColor(Color.green);
            g2d.drawString(language == ENGLISH ? "ready" : "einsatzbereit", STATUS_BAR_X2, STANDUP_OFFSET_Y - 5);
        }
        
        // Standard-Upgrades
        for(int i = 0; i < 6; i++)
        {
        	g2d.setColor(MyColor.lightOrange);        
            g2d.drawString(Button.STANDARD_UPGRADE_LABEL[language][helicopter.ability_id(i)][1-language] + ":", 
            			   STATUS_BAR_X1, 
            			   STANDUP_OFFSET_Y + 25 + i * 25);        
           
            if((i != 5 && helicopter.has_max_upgrade_level[i]) 
                || ( i == ENERGY_ABILITY 
                	 && helicopter.has_max_upgrade_level[ENERGY_ABILITY] 
                	 && !(helicopter.type == OROCHI
                	 	  && !helicopter.has_max_upgrade_level[MISSILE_DRIVE])))
            {
            	g2d.setColor(MyColor.golden);
            }
            else{g2d.setColor(Color.white);}            
            if(i == 5 && helicopter.type == OROCHI)
            {
            	g2d.drawString(LEVEL[language] + " " + helicopter.level_of_upgrade[i] + " / " + (helicopter.level_of_upgrade[1]-1), STATUS_BAR_X2, STANDUP_OFFSET_Y + 150);
            }
            else{g2d.drawString(LEVEL[language] + " " + (helicopter.level_of_upgrade[i]), STATUS_BAR_X2, STANDUP_OFFSET_Y + 25 + i * 25);}            
        }
        
        // Spezial-Upgrades        
        if(helicopter.spotlight)
        {            
            g2d.setColor(MyColor.golden);
            g2d.drawString(SPECIALS[language][0], STATUS_BAR_X1, SPECUP_OFFSET_Y + 0);
        }       
        if(helicopter.goliath_plating == 2)
        {            
            g2d.setColor(MyColor.golden);
            g2d.drawString(SPECIALS[language][1], STATUS_BAR_X1, SPECUP_OFFSET_Y + 25);
        }       
        if(helicopter.has_piercing_warheads)
        {            
            g2d.setColor(MyColor.golden);
            g2d.drawString(SPECIALS[language][2], STATUS_BAR_X1, SPECUP_OFFSET_Y + 50);
        }
        if(helicopter.nr_of_cannons >= 2)
        {            
            if(helicopter.type == OROCHI && helicopter.nr_of_cannons == 2)
            {
            	g2d.setColor(Color.white);
            }
            else{g2d.setColor(MyColor.golden);}
            if(helicopter.nr_of_cannons == 3)
            {
            	g2d.drawString(SECOND_AND_THIRD_CANNON[language], STATUS_BAR_X1, SPECUP_OFFSET_Y + 75);
            }
            else
            {
            	g2d.drawString(SPECIALS[language][3], STATUS_BAR_X1, SPECUP_OFFSET_Y + 75);
            }
        }
        if(helicopter.has_5th_special())
        {            
        	g2d.setColor(MyColor.golden);            
            if(helicopter.type == PHOENIX || helicopter.type == PEGASUS)
            {
            	if(!helicopter.has_max_upgrade_level[helicopter.type == PHOENIX ? 3 : 4]){g2d.setColor(Color.white);}
            	g2d.drawString(SPECIALS[language][4 + helicopter.type.ordinal()] + " (" + LEVEL[language] + " " + (helicopter.level_of_upgrade[helicopter.type == PHOENIX ? 3 : 4]-1) + ")", STATUS_BAR_X1, SPECUP_OFFSET_Y + 100);
            }
            else
            {
            	g2d.drawString(SPECIALS[language][4 + helicopter.type.ordinal()], STATUS_BAR_X1, SPECUP_OFFSET_Y + 100);
            }
        }
        
        // Standard-Upgrades     
        g2d.setColor(Color.yellow);
        g2d.setFont(BOLD20);
        g2d.drawString(language == 0 ? "Standard upgrades:" : "Standard-Upgrades:", Button.STANDARD_UPGRADE_LOCATION.x + 4, 82);
        g2d.setFont(PLAIN15);        
        for(int i = 0; i < 6; i++){repair_shop_button.get("StandardUpgrade" + i).paint(g2d);}
                
        // Message Box        
        paint_message_frame(g2d);
        
        // Spezial-Upgrades
        g2d.setColor(Color.yellow);
        g2d.setFont(BOLD20);
        g2d.drawString(language == 0 ? "Special upgrades:" : "Spezial-Upgrades:", 774, 142);        
        g2d.setFont(PLAIN15);                
        for(int i = 0; i < 5; i++){repair_shop_button.get("Special" + i).paint(g2d);}
    }  
    
    private static void paint_message_frame(Graphics2D g2d)
    {
    	paint_frame(g2d, 773, 11, 181, 98);
    	g2d.setColor(MyColor.golden);
        g2d.setFont(BOLD14);        
        for(int i = 0; i < 4; i++){g2d.drawString(message[i], 785, 35 + i * 20); }
    }
    
	static void paint_scorescreen(Graphics2D g2d, Helicopter helicopter)
	{    	
    	// Helikopter-Anzeige
    	paint_helicopter_display(g2d, helicopter, 0, 5); 	
    	
    	g2d.setPaint(MyColor.gradientVariableWhite);
        g2d.setFont(PLAIN60);
        String temporary_string = language == ENGLISH ? "Game statistics" : "Spielstatistik"; 
        g2d.drawString((temporary_string), (981-g2d.getFontMetrics().stringWidth(temporary_string))/2, 65);
         
        paint_frame(g2d, 619, 90, 376, 298); 
        paint_frame_line(g2d, 621, 140, 372);
        paint_frame_line(g2d, 621, 249, 372);
        paint_frame(g2d, 297, 90, 250, 200); 
        
        
        startscreen_menu_button.get("Cancel").paint(g2d);       
		
		if(Events.level > 50)
		{
			g2d.setColor(Color.green);
			if(language == ENGLISH)
			{
				g2d.drawString("Mission completed in " + minuten(helicopter.scorescreen_times[4]) + "!", SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS -9);
			}
			else
			{
				g2d.drawString("Mission in " + minuten(helicopter.scorescreen_times[4]) + " erfüllt!", SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS -9);
			}			
		}
		else
		{
			g2d.setColor(Color.red);
			if(language == ENGLISH)
			{
				g2d.drawString("Mission failed after " + minuten(helicopter.scorescreen_times[4]) + " in level " + Events.level + "!", SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS - 9);
			}
			else
			{
				g2d.drawString("Mission nach " + minuten(helicopter.scorescreen_times[4]) + " in Level " + Events.level + " gescheitert!", SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS - 9);
			}
		}
        g2d.setColor(Color.white);
        g2d.drawString((language == ENGLISH ? "Playing time per boss: " : "Spielzeit pro Boss: "), SCORESCREEN_X_POS_1 - 20, SCORESCREEN_Y_POS - 9);
        
        for(int i = 0; i < 5; i++)
        {
        	if(i < (Events.level-1)/10)
        	{
        		g2d.setColor(Color.green);
        		g2d.drawString(minuten(i == 0 ? helicopter.scorescreen_times[0] : helicopter.scorescreen_times[i] - helicopter.scorescreen_times[i-1]) + " (Boss " + (i+1) + ")", SCORESCREEN_X_POS_1, SCORESCREEN_Y_POS - 9 + SCORESCREEN_SPACE_BETWEEN_ROWS * (i+1));	
        	}
        	else
        	{
        		g2d.setColor(Color.red);
        		g2d.drawString((language == ENGLISH ? "undefeated" : "nicht besiegt") + " (Boss " + (i+1) + ")", SCORESCREEN_X_POS_1, SCORESCREEN_Y_POS - 9 + SCORESCREEN_SPACE_BETWEEN_ROWS * (i+1));
        	}
        }
		
        g2d.setColor(Color.white);
		g2d.drawString((language == ENGLISH ? "Crash landings: " : "Bruchlandungen: ") 
										+ helicopter.nr_of_crashes, 
						 SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS + SCORESCREEN_SPACE_BETWEEN_ROWS * 1 + 11);
        g2d.drawString((language == ENGLISH ? "Repairs: " : "Reparaturen: ") 
        								+ helicopter.nr_of_repairs, 
        				 SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS + SCORESCREEN_SPACE_BETWEEN_ROWS * 2 + 11);
        g2d.drawString((language == ENGLISH ? "Overall earnings: " : "Gesamt-Sold: ") 
        								+  Events.overall_earnings + " €",
        				 SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS + SCORESCREEN_SPACE_BETWEEN_ROWS * 3 + 11);
        
        int        
        percentage = Events.bonus_income_percentage();
        g2d.setColor(MyColor.scorescreen[0]);	
        g2d.drawString((language == ENGLISH ? "Additional income due to extra boni: " : "Zusätzliche Einahmen durch Extra-Boni: +") + percentage + "%", SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS + SCORESCREEN_SPACE_BETWEEN_ROWS * 5);
               
        percentage = MyMath.percentage(helicopter.enemies_killed, helicopter.enemies_seen);
        g2d.setColor(MyColor.scorescreen[1]);        
        g2d.drawString((language == ENGLISH ? "Defeated enemies: " : "Besiegte Gegner: ") + helicopter.enemies_killed + (language == ENGLISH ? " of " : " von ") + helicopter.enemies_seen + " (" + percentage + "%)", SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS + SCORESCREEN_SPACE_BETWEEN_ROWS * 6);
            
        percentage = MyMath.percentage(helicopter.mini_boss_killed, helicopter.mini_boss_seen);
        g2d.setColor(MyColor.scorescreen[2]);        
        g2d.drawString((language == ENGLISH ? "Defeated mini-bosses: " : "Besiegte Mini-Bosse: ") + helicopter.mini_boss_killed + (language == ENGLISH ? " of " : " von ") + helicopter.mini_boss_seen + " (" + percentage + "%)", SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS + SCORESCREEN_SPACE_BETWEEN_ROWS * 7);
        
        percentage = (MyMath.percentage(helicopter.hit_counter, helicopter.missile_counter));
        g2d.setColor(MyColor.scorescreen[3]);        
        g2d.drawString((language == ENGLISH ? "Hit rate: " : "Raketen-Trefferquote: ") + percentage + "%", SCORESCREEN_X_POS_2, SCORESCREEN_Y_POS + SCORESCREEN_SPACE_BETWEEN_ROWS * 8); //Zielsicherheit
	}
	
	public static void update_scorescreen(Helicopter helicopter)
	{		
    	helicopter.rotate_propeller(7);    	  	
    	if(startscreen_menu_button.get("Cancel").bounds.contains(helicopter.destination))
        {
        	startscreen_menu_button.get("Cancel").highlighted = true;
        }
        else{startscreen_menu_button.get("Cancel").highlighted = false;}		
	}
	
	public static String minuten(long spielzeit)
	{
		if(spielzeit == 1) return language == ENGLISH ? "1 minute" : "1 Minute";
		return spielzeit + (language == ENGLISH ? " minutes" : " Minuten");
	}
	
	/** Displays **/
	
	public static void update_displays(Helicopter helicopter)
	{
		update_level_display();								
		update_credit_display(helicopter);		
		update_foreground_displays(helicopter);
	}
	
	private static void update_level_display()
	{
		if(level_display_timer != DISABLED)
		{
			level_display_timer++;
			if(level_display_timer > 250){level_display_timer = DISABLED;}
		}
	}
	
	private static void update_credit_display(Helicopter helicopter)
	{
		if(!menue_visible 
			&& (money_display_timer != DISABLED || helicopter.damaged))
		{				
			money_display_timer++;
			if(money_display_timer == BONUS_MAX_DISPLAY_TIME)
			{
				Events.last_bonus = 0;
				Events.last_extra_bonus = 0;
			}
			else if(	money_display_timer > 
						BONUS_MAX_DISPLAY_TIME + 100)
			{
				money_display_timer = DISABLED;
			}				
		}
	}
	
	private static void update_foreground_displays(Helicopter helicopter)
	{
		if(unlocked_timer > 0)
    	{
			update_unlocked_info(helicopter);
		}		
	}
	
	static void 
	paint_background_displays(Graphics2D g2d, 
	                          Controller hd,
	                          Helicopter helicopter)
	{
		paint_level_display(g2d, helicopter);		
		paint_praise_display(g2d);			
		if(money_display_timer != DISABLED 
			|| helicopter.damaged
			|| (helicopter.is_on_the_ground() 
				&& !Events.restart_window_visible))
		{
			paint_credit_display(g2d);		
		}		
		if(special_info_selection != 0){paint_special_info_display( g2d, hd, helicopter);}
	}
		
	private static void paint_level_display(Graphics2D g2d, Helicopter helicopter)
    {
		if(helicopter.is_on_the_ground() 
			|| level_display_timer != DISABLED)
		{
			if(Events.timeOfDay == NIGHT){g2d.setColor(Color.white);}
	        else{g2d.setColor(Color.black);}
	        g2d.setFont(PLAIN36);
	        String level_string = "";
	        if(Events.level < 51)
	        {       	
	        	if(Events.level%10 == 0)
	        	{
	        		level_string = "Boss Level " + (Events.level/10);
	        	}
	            else{level_string = "Level " + Events.level;}            
	        }       
	        FontMetrics fm = g2d.getFontMetrics();        	
	        int sw = fm.stringWidth(level_string);        
	        g2d.drawString(level_string, (981-sw)/2, 55);
		}				
    }
	
	private static void paint_praise_display(Graphics2D g2d)
    {    		
    	if(Events.commendation_timer > 0)
		{	
			if(Events.timeOfDay == NIGHT){g2d.setColor(Color.red);}
	        else{g2d.setColor(Color.red);}
	        
	        String kind_of_kill;
	        
	        if(Events.last_multi_kill == 1)
	        {
	        	kind_of_kill = "Nice Catch";
	        	g2d.setFont(PLAIN41);  
	        }        
	        else if(Events.last_multi_kill == 2)
	        {
	        	kind_of_kill = "Double Kill";
	        	g2d.setFont(PLAIN28);  
	        }
	        else if(Events.last_multi_kill == 3)
	        {
	        	kind_of_kill = "Triple Kill";
	        	g2d.setFont(PLAIN36);  
	        }
	        else if(Events.last_multi_kill == 4)
	        {
	        	kind_of_kill = "Mega Kill";
	        	g2d.setFont(PLAIN41);  
	        } 
	        else if(Events.last_multi_kill == 5)
	        {
	        	kind_of_kill = "Multi Kill";
	        	g2d.setFont(PLAIN47);  
	        }   
	        else
	        {
	        	kind_of_kill = "Monster Kill";
	        	g2d.setFont(PLAIN52);  
	        }         
	        FontMetrics fm = g2d.getFontMetrics();        	
	        int sw = fm.stringWidth(kind_of_kill);        	        
	        g2d.drawString(kind_of_kill, (981-sw)/2, 130);
		}
    }
	
	private static void paint_credit_display(Graphics2D g2d)
    {	
		if(Events.timeOfDay == NIGHT){g2d.setColor(Color.red);}
        else{g2d.setColor(MyColor.red);}
        g2d.setFont(PLAIN22);
        g2d.drawString((language == ENGLISH ? "Credit: " : "Guthaben: ") + Events.money + " €", 20, 35);
        if(Events.last_bonus > 0)
        {            
            if(Events.timeOfDay == NIGHT){g2d.setColor(MyColor.money_display_night_red);}
            else{g2d.setColor(MyColor.darkerOrange);}
            if(money_display_timer <= 23){g2d.setFont(new Font("Dialog", Font.PLAIN, money_display_timer));}
            if(money_display_timer > 23 && money_display_timer < 77){g2d.setFont(PLAIN22);}
            if(money_display_timer >= BONUS_MAX_DISPLAY_TIME-23){g2d.setFont(new Font("Dialog", Font.PLAIN, BONUS_MAX_DISPLAY_TIME - money_display_timer));}
            g2d.drawString("+" + Events.last_bonus + " €", 20, 60);
            if(Events.last_extra_bonus > 0)
            {
            	if(Events.timeOfDay == NIGHT){g2d.setColor(Color.yellow);}            
            	else{g2d.setColor(MyColor.darkYellow);}
            	g2d.drawString("+" + Events.last_extra_bonus + " €", 20, 86);
            }                    
        }        
    }	
	
	private static void paint_special_info_display( Graphics2D g2d, 
	                                                Controller hd,
	                                                Helicopter helicopter)
    {
        g2d.setColor(MyColor.red);
        g2d.setFont(PLAIN22);        
        String info_string = ""; 
        if(special_info_selection == 1)
		{
			info_string = "Kills bis LevelUp: " 
						  + Events.kills_after_levelup
						  + "/"
						  + MyMath.kills(Events.level);
		}
		else if(special_info_selection == 2)
		{
			info_string = "Aktive PowerUps: " 
						  + hd.powerUp.get(ACTIVE).size() 
						  + ";   Inaktive PowerUps: "
						  + hd.powerUp.get(INACTIVE).size();
		}
		else if(special_info_selection == 3)
		{
			info_string = "Aktive Explosionen: " 
						  + hd.explosion.get(ACTIVE).size() 
						  + ";   Inaktive Explosionen: "
						  + hd.explosion.get(INACTIVE).size();
		}
		else if(special_info_selection == 4)
		{
			info_string = "Aktive Gegner: " 
						  + (hd.enemy.get(ACTIVE).size()-Enemy.current_nr_of_barriers)  + " / " + (Enemy.max_nr)						 			
						  + ";   Zerstörte Gegner: "
						  + hd.enemy.get(DESTROYED).size()
						  + ";   Hindernisse: "
						  + Enemy.current_nr_of_barriers + " / " + Enemy.max_barrier_nr
						  + ";   Inaktive Gegner: "
						  + hd.enemy.get(INACTIVE).size();
		}
		else if(special_info_selection == 5)
		{
			info_string = "Aktive Raketen: " 
						  + hd.missile.get(ACTIVE).size()
						  + ";   Inaktive Raketen: "
						  + hd.missile.get(INACTIVE).size();
		}
		else if(special_info_selection == 6)
		{
			info_string = "Aktive gegnerische Geschosse: " 
						  + hd.enemyMissile.get(ACTIVE).size()
						  + ";   Inaktive gegnerische Geschosse: "
						  + hd.enemyMissile.get(INACTIVE).size();
		}
		else if(special_info_selection == 7)
		{
			info_string = "Aktive Hintergrundobjekte: "
						  + hd.bgObject.get(ACTIVE).size()
						  + ";   Inaktive Hintergrundobjekte: "
						  + hd.bgObject.get(INACTIVE).size();
		}
		else if(special_info_selection == 8)
		{
			info_string = "Speed level: " 
						  + helicopter.level_of_upgrade[ROTOR_SYSTEM]
						  + " +   Speed: " + helicopter.rotor_system;
		}
		else if(special_info_selection == 9)
		{
			info_string = "Bonus: " + Events.overall_earnings 
			              + "   Extra-Bonus: " + Events.extra_bonus_counter;
		}
		else if(special_info_selection == 10)
		{
			info_string = "Menü sichtbar: " + menue_visible;
		}
		else if(special_info_selection == 11)
		{
			int percentage = helicopter.enemies_seen > 0 
								? 100*helicopter.enemies_killed/helicopter.enemies_seen 
								: 0;
			info_string = (language == ENGLISH 
							? "Defeated enemies: " 
							: "Besiegte Gegner: ") 
						  + helicopter.enemies_killed 
						  + (language == ENGLISH ? " of " : " von ") 
						  + helicopter.enemies_seen 
						  + " (" 
						  + percentage 
						  + "%)";
		}
		else if(special_info_selection == 12)
		{
			int percentage = helicopter.missile_counter != 0 
								? 100*helicopter.hit_counter/helicopter.missile_counter 
								: 0;
			info_string = "Missile counter: " 
						  + helicopter.missile_counter 
						  + "; Hit counter: " 
						  + helicopter.hit_counter 
						  + "; Hit rate: " 
						  + percentage;
		}   
		else if(special_info_selection == 13)
		{
			info_string = HELICOPTER_INFOS[language][helicopter.type.ordinal()][0] + "-Klasse";
		}
        g2d.drawString("Info: " + info_string, 20, 155);
    }
	
	static void 
	paint_foreground_displays(Graphics2D g2d,
	                          Controller hd,
	                          Helicopter helicopter, 
	                          boolean show_fps)
	{
		paint_boss_health_bar(g2d);	
		paint_health_bar(g2d, helicopter, 5, MovingObject.GROUND_Y + 5);		
		paintCollectedPowerUps(g2d);			
		if(show_fps){paint_FPS_display(g2d);}		
		
		if(helicopter.is_on_the_ground())
		{
			if(!menue_visible && hd.mouse_in_window)
			{
				paint_time_display(g2d, Events.playing_time
											 + System.currentTimeMillis()
											 - Events.time_aktu);
			}
			else{paint_time_display(g2d, Events.playing_time);}
		}		
		if(unlocked_timer > 0)
    	{
    		paint_helicopter_display(g2d, helicopter, unlocked_type, 
    								 unlocked_display_position(unlocked_timer), 
    								 -50, false);
		}		
	}
	
	private static void paint_boss_health_bar(Graphics2D g2d)
	{
		if(Enemy.current_mini_boss != null)
		{
			paint_boss_health_bar(g2d, Enemy.current_mini_boss);
		}		
		else if(Events.boss != null && Events.level < 51)
		{
			paint_boss_health_bar(g2d, Events.boss);	
		}
	}
	
	private static void paint_boss_health_bar(Graphics2D g2d, Enemy boss)
    {
        g2d.setColor(MyColor.hitpoints);
        g2d.fillRect(813, 5, (ENEMY_HEALTH_BAR_WIDTH * boss.hitpoints)/boss.starting_hitpoints, 10);
        if(Events.timeOfDay == NIGHT){g2d.setColor(Color.red);}
        else{g2d.setColor(MyColor.red);}
        g2d.fillRect(813 + (ENEMY_HEALTH_BAR_WIDTH * boss.hitpoints)/boss.starting_hitpoints, 5, ENEMY_HEALTH_BAR_WIDTH - (ENEMY_HEALTH_BAR_WIDTH * boss.hitpoints)/boss.starting_hitpoints, 10);
        if(Events.timeOfDay == NIGHT){g2d.setColor(Color.white);}
        else{g2d.setColor(Color.black);}        
        g2d.drawRect(813, 5, ENEMY_HEALTH_BAR_WIDTH, 10);
    }	
	
	private static void paint_health_bar(Graphics2D g2d, Helicopter helicopter, int x, int y)
    {
    	paint_health_bar(g2d, helicopter, x, y, HEALTH_BAR_LENGTH, true);
    }
    
    private static void paint_health_bar(Graphics2D g2d, Helicopter helicopter, int x, int y, int length, boolean rahmen)
    {
    	float relative_energy = helicopter.energy/MyMath.energy(helicopter.level_of_upgrade[ENERGY_ABILITY]);
    	float relative_life = helicopter.current_plating/helicopter.max_plating(); 
        if(rahmen)
    	{
    		g2d.setColor(MyColor.lightestGray);
    		g2d.fillRect(x+1, y+1, length + 4, 23);            
            g2d.setColor(MyColor.lightGray);      
            g2d.fillRect(x+2, y+2, length+2, 10);
            g2d.fillRect(x+2, y+13, length+2, 10);
    	}    	        
    	if(!helicopter.is_energy_ability_activateable())
    	{
    		g2d.setColor(Color.cyan);
    	}
    	else{g2d.setColor(helicopter.has_unlimited_energy() 
    						? MyColor.endlessEnergyViolet 
    						: Color.blue);}
        g2d.fillRect(x+3, y+3, (int)(length * relative_energy), 8);
        g2d.setColor(Color.gray);
        g2d.fillRect(x+3 + (int)(length * relative_energy), y+3, length - (int)(length * relative_energy), 8);
      
        g2d.setColor(helicopter.is_invincible() 
        				? Color.yellow 
        				: Color.green);
        g2d.fillRect(x+3, y+14, (int)(length * relative_life), 8);
        g2d.setColor(helicopter.recent_dmg_timer == 0 ? Color.red : MyColor.variableRed);
        g2d.fillRect(x+3 + (int)(length * relative_life), y+14, length - (int)(length * relative_life), 8);
    }
	
    private static void paintCollectedPowerUps(Graphics2D g2d)
	{
		int j = 0;
		for(int i = 0; i < 4; i++)
		{
			if(collected_PowerUp[i] != null)
			{
				collected_PowerUp[i].paint(g2d, 166 + j * 28);
				j++;
			}
		}
	}
	
    private static void paint_FPS_display(Graphics2D g2d)
    {        
        g2d.setColor(Color.white);        
        g2d.setFont(PLAIN18);
        g2d.drawString("FPS: " + (fps == 0 ? (language == ENGLISH ? 
        		       "please wait" : "bitte warten") : fps), 
        		       292, 449);
    }
    
    private static void paint_time_display(Graphics2D g2d, long zeit)
    {
    	g2d.setColor(Color.white);        
        g2d.setFont(PLAIN18);        
        String outputstring = PLAYING_TIME[language] + return_time_display_text(zeit);        
        g2d.drawString(outputstring, language == ENGLISH ? 646 : 661, 450);
    }
    
    static String return_time_display_text(long zeit)
    {
    	long h, min, sek;
        h = ((zeit - (zeit%3600000))/3600000);
        min = (zeit - (zeit%60000))/60000;
        sek = (zeit - (zeit%1000))/1000;        
        return (h%100 > 9?"":"0") + (h%100) 
		   		+ ":" + (min%60  > 9?"":"0") + (min%60)
		   		+ ":" + (sek%60  > 9?"":"0") + (sek%60);
    }
    
    static void update_unlocked_info(Helicopter helicopter)
	{    	
		unlocked_timer--;
		if(unlocked_timer ==  UNLOCKED_DISPLAY_TIME - 50)
		{
			Audio.play(Audio.cash);
		}
		helicopter.rotate_propeller(unlocked_type, 7);		
		if(unlocked_timer == 0){unlocked_type = null;}
	}
    
    static int unlocked_display_position(int timer)
	{
		if(timer < 50)
		{
			return 789 + 5 * (50 - timer);
		}
		else if(timer > UNLOCKED_DISPLAY_TIME - 50)
		{
			return 789 + 5 * (timer - UNLOCKED_DISPLAY_TIME + 50);
		}
		else return 789;
	}
    	
    private static void paint_helicopter_display(Graphics2D g2d, 
                                                 Helicopter helicopter,
                                                 int x, int y)
    {
    	paint_helicopter_display(g2d, helicopter, helicopter.type, x, y, true);
    }
    
    
    private static void paint_helicopter_display(Graphics2D g2d, 
                                                 Helicopter helicopter, 
                                                 HelicopterTypes helicopter_type,
                                                 int x, int y, 
                                                 boolean display_upgrades)
    {
        paint_frame(g2d, 26 + x,  85 + y, 200, 173, Events.window != GAME ? null : MyColor.lightestGray);  
        g2d.setColor(Color.white);
        g2d.setFont(BOLD20);
        String input_string = HELICOPTER_INFOS [language][helicopter_type.ordinal()][0] + (language == ENGLISH ? " type" : "-Klasse");
        g2d.drawString(input_string, 28 + x + (196-g2d.getFontMetrics().stringWidth(input_string))/2, 113 + y);  
        
        helicopter.paint(g2d, 59 + x, 141 + y, helicopter_type, 1, !display_upgrades);      
                
        paint_frame_line(g2d, 28 + x, 126 + y, 196);
    	paint_frame_line(g2d, 28 + x, 226 + y, 196);
        
        if(Events.window != GAME)
        {      	
        	paint_health_bar(g2d, helicopter, 30 + x, 230 + y, 187, false);
        }
        else
        {       	
            g2d.setFont(BOLD18);
            if(unlocked_timer > UNLOCKED_DISPLAY_TIME - 50)
            {
            	g2d.setColor(MyColor.red);
            	input_string = (language == ENGLISH ? "not available" : "nicht verfügbar");
            }
            else
            {
            	g2d.setColor(MyColor.darkArrowGreen);
            	input_string = (language == ENGLISH ? "unlocked" : "freigeschaltet");
            }
            g2d.drawString(input_string, 28 + x + (196-g2d.getFontMetrics().stringWidth(input_string))/2, 249 + y);  
        }
               
        if(Events.window == REPAIR_SHOP)
        {       	
        	if(helicopter.damaged)
        	{
        		g2d.setColor(Color.red);
                g2d.setFont(PLAIN14);
                g2d.drawString(language == ENGLISH ? "damaged" : "beschädigt", 34 + x, 216 + y);
        	}
        	g2d.setFont(BOLD16);
        	g2d.setColor(MyColor.plating);
        	int percent_plating = (Math.round(100f*helicopter.current_plating/helicopter.max_plating()));
        	FontMetrics fm = g2d.getFontMetrics();        	
            int sw = fm.stringWidth(""+percent_plating);        	
            g2d.drawString(percent_plating + "%", 203 - sw + x, STANDUP_OFFSET_Y + y + 69);
        }        
    }    
              
    private static void paint_restart_window(Graphics2D g2d, 
                                             Helicopter helicopter, 
                                             boolean game_over)
    {   	
    	if(!game_over)
    	{
    		paint_frame(g2d,363, 147, 256, 111, MyColor.golden);
    	}  
    	else if(Events.level < 51 || helicopter.type == HELIOS)
    	{
    		paint_frame(g2d,363, 112, 256, 146, MyColor.golden);
    	}
    	else if(language == ENGLISH)
    	{
    		paint_frame(g2d,363, 100, 256, 158, MyColor.golden);
    	}
    	else
    	{
    		paint_frame(g2d,363, 64, 256, 194, MyColor.golden);
    	}
    	      
        g2d.setFont(PLAIN18);            
        g2d.setColor(MyColor.red);        
        if(!game_over)
        {
        	g2d.drawString((language == ENGLISH ? "Your helicopter was" : "Ihr Helikopter wurde"), 410, 179);
            g2d.drawString((language == ENGLISH ? "severely damaged!"   : "schwer beschädigt!"),   410, 197);
        }
        else if(Events.level < 51)
        {            
            g2d.drawString((language == ENGLISH ? "Your helicopter was"        : "Ihr Helikopter wurde"),	   	390, 137);
            g2d.drawString((language == ENGLISH ? "severely damaged!"          : "schwer beschädigt!")  , 	    390, 155);
            g2d.drawString((language == ENGLISH ? "Unfortunately, you "        : "Leider reicht ihr Guthaben"), 390, 179);
            g2d.drawString((language == ENGLISH ? "cannot afford the repairs." : "nicht für eine Reparatur."),  390, 197);
        }
        else
        {            
            if(helicopter.type == HELIOS)
            {
            	g2d.drawString((language == ENGLISH ? "Congratulations!"         : "Herzlichen Glückwunsch!"),	   390,137);
                g2d.drawString((language == ENGLISH ? "The attack was repulsed." : "Der Angriff wurde abgwehrt."), 390,155);
                g2d.drawString((language == ENGLISH ? "Once again, mankind"      : "Wieder einmal lebt die"),	   390,179);
                g2d.drawString((language == ENGLISH ? "lives in peace!"          : "Menschheit in Frieden!"),	   390,197);
            }
            else
            {
            	int i = language == ENGLISH ? 0 : 36;
            	
            	g2d.drawString((language == ENGLISH ? "You won a great victory,"	: "Sie haben einen großen"),		390,124-i);
                g2d.drawString((language == ENGLISH ? "but the war isn't over yet." : "Sieg errungen, aber der"), 		390,142-i);
                g2d.drawString((language == ENGLISH ? "Rumor has it only helios"	: "Krieg ist noch nicht vorbei."),	390,160-i);
                g2d.drawString((language == ENGLISH ? "type helicopters can"		: "Gerüchten zufolge können "),		390,178-i);
                g2d.drawString((language == ENGLISH ? "finally stop the invasion."	: "nur Helikopter der Helios-"),	390,196-i);
                g2d.drawString((language == ENGLISH ? ""							: "Klasse die Alien-Invasion"),		390,214-i);
                g2d.drawString((language == ENGLISH ? ""							: "endgültig stoppen."),			390,232-i);
            }            
        }        
        if(game_over){inGame_button.get("MMNewGame2").paint(g2d, (language == ENGLISH ? "Start a new game!"  : "Neues Spiel starten!"));}
        else		 {inGame_button.get("MMNewGame2").paint(g2d, (language == ENGLISH ? "Go to repair shop!" : "Zurück zur Werkstatt!"));}
    }    
    
    
    
/** Grafical objects **/
    
    private static void paint_frame(Graphics2D g2d, Rectangle frame, Color filled_color)
    {  
    	paint_frame(g2d, frame.x, frame.y, frame.width, frame.height, filled_color);
    }    
    
    private static void paint_frame(Graphics2D g2d, int left, int top, int width, int height)
    {
    	paint_frame(g2d, left, top, width, height, null);
    }
        
    static void paint_frame(Graphics2D g2d, int left, int top, int width, int height, Color filled_color)
    {                          
    	myRahmen[0] = new GradientPaint(0, top-1, Color.white, 0, top+4, MyColor.darkestGray, true); 
    	myRahmen[1] = new GradientPaint(0, top+height-1, Color.white, 0, top+height+4, MyColor.darkestGray, true);
    	myRahmen[2] = new GradientPaint(left, 0, Color.white, left+5, 0, MyColor.darkestGray, true);
    	myRahmen[3] = new GradientPaint(left+width, 0, Color.white, left+width+5, 0, MyColor.darkestGray, true);    	    	
    	if(filled_color != null)
    	{
    		g2d.setPaint(filled_color);
    		g2d.fillRect(left, top, width, height);
    	} 
    	g2d.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    	g2d.setPaint(myRahmen[0]);
    	g2d.drawLine(left+1, top, left+width-2, top);
    	g2d.setPaint(myRahmen[1]);
    	g2d.drawLine(left+1, top+height, left+width-2, top+height);
    	g2d.setPaint(myRahmen[2]);
    	g2d.drawLine(left, top+1, left, top+height-2);
    	g2d.setPaint(myRahmen[3]);
    	g2d.drawLine(left+width, top+1, left+width, top+height-2);
        g2d.setStroke(new BasicStroke(1));
    }    
    
    private static void paint_frame_line(Graphics2D g2d, int left, int top, int width)
    {                          
    	myRahmen[0] = new GradientPaint(0, top-1, Color.white, 0, top+4, MyColor.darkestGray, true);    	    	
    	g2d.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    	g2d.setPaint(myRahmen[0]);
    	g2d.drawLine(left+1, top, left+width-2, top);
        g2d.setStroke(new BasicStroke(1));
    }  
        
	static void change_language(Helicopter helicopter, Savegame savegame)
	{
		Events.settings_changed = true;		
		
		language = (language+1)%2;
		savegame.language = language;
		
		update_button_labels(helicopter);
	}
		
	static void update_button_labels(Helicopter helicopter)
	{
		repair_shop_button.get("RepairButton").label = Button.REPAIR[language];		
		repair_shop_button.get("RepairButton").second_label = Button.PRICE[language];		
		repair_shop_button.get("Einsatz").label = Button.MISSION[language][Events.timeOfDay];
		repair_shop_button.get("Einsatz").second_label = Button.SOLD[language][Events.timeOfDay];
		
		inGame_button.get("RepairShop").label =  Button.REPAIR_SHOP[language];
		inGame_button.get("MainMenu").label =    Button.MAIN_MENU[language];
		inGame_button.get("MMNewGame1").label =  Button.START_NEW_GAME[language];
		inGame_button.get("MMStopMusic").label = Button.MUSIC[language][Audio.sound_on ? 0 : 1];
		inGame_button.get("MMNewGame2").label =  Button.QUIT[language];
		inGame_button.get("MMCancel").label =    Button.CANCEL[language];
				
		for(int i = 0; i < 2; i++){for(int j = 0; j < 3; j++)
		{
			startscreen_button.get(Integer.toString(i)+j).label = 
				Button.STARTSCREEN_BUTTON_LABEL[language][i][j];
		}}	
		
		for(int m = 0; m < 8; m++)
		{						
			startscreen_menu_button.get(Integer.toString(m)).label =  
				Button.STARTSCREEN_MENU_BUTTON[language][2][m];
		}
		startscreen_menu_button.get("Cancel").label = Button.CANCEL[language];
		
		for(int i = 0; i < 6; i ++)
		{
			repair_shop_button.get("StandardUpgrade" + i).label = 
				Button.STANDARD_UPGRADE_LABEL[language][i][0] + " " + 
				Button.STANDARD_UPGRADE_LABEL[language][i][1];
			repair_shop_button.get("StandardUpgrade" + i).second_label = 
				Button.PRICE[language];
		}
		for(int i = 0; i < 5; i++)
		{
			repair_shop_button.get("Special" + i).label = SPECIALS[language][i];
			repair_shop_button.get("Special" + i).second_label = Button.PRICE[language];			
		}		
		if(helicopter.type == OROCHI && helicopter.nr_of_cannons == 2)
		{
			repair_shop_button.get("Special" + 3).label  = THIRD_CANNON[language];
		}		
		repair_shop_button.get("Special" + 4).label = SPECIALS[language][4 + helicopter.type.ordinal()];
	}

	
	static void identify_highlighted_buttons(Helicopter helicopter, HashMap<String, Button> button)
	{
    	if(	highlighted_button.equals("")
        	|| !button.get(highlighted_button).bounds.contains(helicopter.destination))
        {
        	if(!highlighted_button.equals(""))
        	{
        		button.get(highlighted_button).highlighted = false;
        		highlighted_button = ""; 
        	}
        	for (Iterator<String> iterator = button.keySet().iterator(); iterator.hasNext();)
        	{
        		String key = iterator.next();     		
        		if(button.get(key).bounds.contains(helicopter.destination.x, helicopter.destination.y))
                {
            		button.get(key).highlighted = true;
            		highlighted_button = key;
            		break;
                }
        	}
        }
	}
		
	static Polygon get_cross_polygon()
    {
    	return get_cross_polygon(	190, 100, 15, 38 + STARTSCREEN_OFFSET_X
    								+ cross_pos * HELICOPTER_DISTANCE, 
    								247 + STARTSCREEN_HELICOPTER_OFFSET_Y);
    }
    
    private static Polygon get_cross_polygon(int a, int b, int z, int v, int w)
    {
    	int [] temp_x = {0, (a/2), a, a, a*b/(2*(b-z)), a, a, a/2, 0, 0, 
    	                 a*(b-2*z)/(2*(b-z)), 0};
    	int [] temp_y = {0, (b-z)/2, 0, z, (b-2*z)/2+z, b-z, b, (b-z)/2+z, 
    	                 b, b-z, (b-2*z)/2+z, z};
    	for(int i = 0; i < 12; i++){temp_x[i] += v; temp_y[i] += w;}
    	return new Polygon(temp_x, temp_y, 12);
    }
	
	static void block(int nr)
	{
		Audio.play(Audio.block);
		set_message(nr);
		message_timer = 1;
	}
    
	private static void set_message (int nr)
	{		
		if(language ==  ENGLISH)
		{
			if(nr == 1)					  
			{
				message[0] = "Your helicopter";
				message[1] = "has been repaired";
				message[2] = "already.";
				message[3] = "";
			}		
			else if(nr == 2)
			{
				message[0] = "Your helicopter";
				message[1] = "must be repaire";				   
				message[2] = "before starting a";
				message[3] = "new mission.";    
			}		
			else if(nr == 4)
			{
				message[0] = "Your helicopter";
				message[1] = "must be repaired";				   
				message[2] = "bevor the installation";
				message[3] = "of new upgrades.";   	
			}		
			else if(nr == 5)
			{
				message[0] = "This upgrade reached";
				message[1] = "maximum level.";				   
				message[2] = "";
				message[3] = "";
			}		
			else if(nr == 6)
			{
				message[0] = "You cannot afford";
				message[1] = "this upgrade.";				   
				message[2] = "";
				message[3] = ""; 
			}		
			else if(nr == 7)
			{
				message[0] = "You got this special";
				message[1] = "upgrade already.";				   
				message[2] = "";
				message[3] = "";   
			}
			else if(nr == 9)
			{
				message[0] = "You cannot afford";
				message[1] = "the repairs.";				   
				message[2] = "";
				message[3] = "";   
			}
			else assert false;
		}
		else if(language == GERMAN)
		{
			if(nr == 1)					  
			{
				message[0] = "Ihr Helikopter ist";
				message[1] = "bereits repariert ";
				message[2] = "worden.";
				message[3] = "";
			}		
			else if(nr == 2)
			{
				message[0] = "Vor einem neuen";
				message[1] = "Einsatz muss Ihr";				   
				message[2] = "Helikopter";
				message[3] = "repariert werden!";   
			}		
			else if(nr == 4)
			{
				message[0] = "Vor der Installation";
				message[1] = "neuer Upgrades muss";				   
				message[2] = "Ihr Helikopter";
				message[3] = "repariert werden!";   	
			}		
			else if(nr == 5)
			{
				message[0] = "Für dieses Upgrade";
				message[1] = "wurde die maximale";				   
				message[2] = "Ausbaustufe bereits";
				message[3] = "erreicht.";
			}		
			else if(nr == 6)
			{
				message[0] = "Ihre finanziellen";
				message[1] = "Mittel reichen für";
				message[2] = "diese Anschaffung";
				message[3] = "nicht aus.";	
			}		
			else if(nr == 7)
			{
				message[0] = "Sie haben dieses";
				message[1] = "Spezial-Upgrade";				   
				message[2] = "bereits erworben.";
				message[3] = "";   
			}
			else if(nr == 9)
			{
				message[0] = "Für eine Reparatur ";
				message[1] = "reicht ihr Guthaben ";				   
				message[2] = "nicht aus.";
				message[3] = "";   
			}
			else assert false;
		}
	}
	
	static void set_ss_message (HelicopterTypes helicopterType)
	{		
		if(helicopterType == OROCHI || helicopterType == KAMAITACHI || helicopterType == PEGASUS)
		{			
			if(language == ENGLISH)
			{
				message[0] = HELICOPTER_INFOS[language][helicopterType.ordinal()][0] + " type helicopters are not available yet.";
				message[1] = "They will be unlocked after you reached level 20 with a";
				message[2] = HELICOPTER_INFOS[language][helicopterType.ordinal()-2][0] + " or a " + HELICOPTER_INFOS[language][helicopterType == PEGASUS ? 3 : 4][0] + " type helicopter for the first time.";
				message[3] = "";
			}
			else
			{
				message[0] = "Die " +  HELICOPTER_INFOS[language][helicopterType.ordinal()][0] + "-Klasse ist noch nicht verfügbar.";
				message[1] = "Sie wird freigeschaltet, sobald Sie erstmalig mit der " ;				   
				message[2] = HELICOPTER_INFOS[language][helicopterType.ordinal()-2][0] + "- oder der " + HELICOPTER_INFOS[language][helicopterType == PEGASUS ? 3 : 4][0] + "-Klasse Level 20 erreicht haben.";
				message[3] = "";   
			}			
		}
		else if(helicopterType == HELIOS)
		{
			if(language == ENGLISH)
			{
				message[0] = "Helios type helicopters are not available yet.";
				message[1] = "They will be unlocked after you defeated";
				message[2] = "a boss enemy for the first time.";
				message[3] = "";
			}
			else
			{
				message[0] = "Die Helios-Klasse ist noch nicht verfügbar.";
				message[1] = "Sie wird freigeschaltet, sobald sie erstmalig" ;				   
				message[2] = "einen Boss-Gegner besiegt haben.";
				message[3] = "";   
			}
		}
	}	
	   
	static void clear_message()
	{
		message[0] = "";
		message[1] = "";				   
		message[2] = "";
		message[3] = "";   
	}
	
	
	static void update_labeltext()
	{	    
		my_label.setText("");
	    		
		if(language == ENGLISH)
		{
			if(Events.window == INFORMATIONS)
			{
				if(page == 0)
				{				
					// Handlung
					my_label.setText(		
					"<html><font size = \"" + HTML_SIZE + "\" face=\"Dialog\"" +
					" color=\"#D2D2D2\">It is the year 2371. Since the Mars " +
					"colinies' War of Independence a hundred years in the " +
					"past, there were no military conflicts among humans and " +
					"thus spendings for military purposes had been reduced " +
					"to a minimum. But now thousands of hostile flying " +
					"vessels of unknown origin have been spotted on the " +
					"northern Sahara and along the African Mediterranean " +
					"coast. Some of these flying vessels already started " +
					"attacking the North African mega-cities including the " +
					"world's capital Cairo. Karanijem Su, president of the " +
					"World Government, has declared a state of emergency " +
					"and is now asking reservists and volunteers from all " +
					"over the world for help. You are one of them." + 
			        "</b></font></html>"
			        );
				}
				else if(page == 1)
				{
					// Änderungen seit 1.0
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE + "\" face=\"Dialog\"" +
					" color=\"#D2D2D2\">" +
					"- To make the start of play easier for beginners, the " +
					"first levels' difficulty was<br>" +
					"&nbsp reduced significantly. Additionally, the game " +
					"manual is now much more detailed. <br>- " +
					"The rather unpopular <font color=\"#FFFFD2\">Phoenix" +
					"<font color=\"#D2D2D2\">, <font color=\"#FFFFD2\">" +
					"Orochi<font color=\"#D2D2D2\"> and " +
					"<font color=\"#FFFFD2\">Kamaitachi type" +
					"<font color=\"#D2D2D2\"> helicopters are now " +
					"considerably more powerful.<br>- Moving backgrounds " +
					"have been implemented.<br>- There are now \"save " +
					"states\" every five levels. Once reached, the player " +
					"can no longer fall back in a level below." +
					"<br>- Many other changes affecting game " +
					"balance, graphics or the upgrade system were made." +
		            "</b></font></html>");
				}
				else if(page == 3)
				{
					// Änderungen seit 1.1
					my_label.setText(	
					"<html><font size = \"" + (HTML_SIZE-1) + "\" face=\"" +
					"Dialog\" color=\"#D2D2D2\"><b>" +
					"- To improve the game's graphics, antialiasing and " +
					"gradient colors were used.<br>" +
					"- Each <font color=\"#FFFFD2\">helicopter type" +
					"<font color=\"#D2D2D2\"> now has its own energy " +
					"consuming " +
					"<font color=\"#FFFFD2\">standard upgarde" +
					"<font color=\"#D2D2D2\"> and " +
					"at least one unique<br>&nbsp <font color=\"#FFFFD2\">" +
					"special upgrade<font color=\"#D2D2D2\">. Thus, the " +
					"different helicopter types " +
					"differ from each other much more clearly now." +
					"<br>- A <font color=\"#FFFFD2\">" +
					"hardcore mode<font color=\"#D2D2D2\"> (see section " +
					"<font color=\"#FFFFFF\">\"Hardcore mode\"" +
					"<font color=\"#D2D2D2\">) is now available." +
					"<br>- <font color=\"#FFFFD2\">Pegasus" +
					"<font color=\"#D2D2D2\"> and<font color=\"#FFFFD2\"> Helios type" +
					"<font color=\"#D2D2D2\"> helicopters are " +
					"now available. Helios type helicopters can only be played in hardcore mode." +					
					"<br>- Also in non-boss levels, " +
					"ocasionally uncommon strong enemies " +
					"can now occour.<br>&nbsp These <font color=\"#FFFFD2\">" +
					"minor boss enemies<font color=\"#D2D2D2\"> (see section " +
					"<font color=\"#FFFFFF\">\"Enemies\"" +
					"<font color=\"#D2D2D2\">) can lose " +
					"<font color=\"#FFFFD2\">" +
					"power-ups<font color=\"#D2D2D2\"> (see section " +
					"<font color=\"#FFFFFF\">\"Power-ups\"" +
					"<font color=\"#D2D2D2\">)" +
					"<br> &nbsp which temporarly " +
					"improve the helicopter.<br>- The game is now a real " +
					"application and does no longer " +
					"depent on the explorer. <br>- A save function as well as a <font color=\"#FFFFD2\">" +
					"highscore<font color=\"#D2D2D2\"> is now available."+
					"<br>- Lots of new enemy classes have been implemented." +					
					"<br>- Many other changes affecting controls, game balance, " +
					"graphics or the upgrade system were made." +
					"</b></font></span></html>"
					);
				}
				else if(page == 4)
				{				
					// Credits
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + 
					"\" face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"Special thanks to all my beta testers, who extensively " +
					"played HelicopterDefence 1.0 and supported me with " +
					"valuable improvement suggestions:<br>" +
					"<font color=\"#FFFFD2\">Alexander Schmuck" +
					"<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">Andreas Lotze" +
					"<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">Boris Sapancilar" +
					"<font color=\"#D2D2D2\">, <font color=\"#FFFFD2\">Fynn " +
					"Hansen<font color=\"#D2D2D2\">, <font color=\"#FFFFD2\">" +
					"Julian Tan<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">Hauke Holm" +
					"<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">Henner Holm<font " +
					"color=\"#D2D2D2\">, <font color=\"#FFFFD2\">Michael " +
					"Sujatta<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\"><br>Sascha Degener" +
					"<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">Thorsten " +
					"Rueckert<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">Tim Schumacher" +
					"<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">Yannick " +
					"Muthmann<font color=\"#D2D2D2\"><br><br> Especially, " +
					"I'd like to thank <font color=\"#FFFFD2\">Fabian " +
					"Gebert<font color=\"#D2D2D2\"> for helpful technical " +
					"advice. I also want to thank <font color=\"#FFFFD2\">" +
					"Michael Sujatta<font color=\"#D2D2D2\"> for proofreading " +
					"and <font color=\"#FFFFD2\">Hauke Holm" +
					"<font color=\"#D2D2D2\"> for assisting " +
					"me with editing audio files. Many thanks to " +
					"<font color=\"#FFFFD2\">Tobias P. " +
					"Eser<font color=\"#D2D2D2\">. Due to him I have " +
					"discovered my interest in " +
					"computer game development. Finally, I would like to " +
					"thank <font color=\"#FFFFD2\">Prof. Till Tantau" +
					"<font color=\"#D2D2D2\"> for an excellent computer " +
					"science lecture. His courses have laid the foundation " +
					"for the development of this game.<br><br>You want to be " +
					"a beta tester? No problem! Send your improvement " +
					"suggestions to: <font color=\"#FFFFD2\">" +
					"info@HelicopterDefence.de<font color=\"#D2D2D2\">" +				
					"</font></b></html>"
					);
				}		
				else if(page == 5)
				{				
					// Copyright
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE + 
					"\" face=\"Dialog\" color=\"#D2D2D2\">" +
					"This is a freeware game. The passing on of this game " +
					"to others is therefore explicitly allowed and you can " +
					"feel encouraged to do so! However, program changes of " +
					"any kind may only be carried out by the developer of " +
					"this game." +
					"</font></html>"
		            );
				}
			}
			else if(Events.window == DESCRIPTION)
			{
				if(page == 0)
				{				
					// Spielbeschreibung
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE +
					"\" face=\"Dialog\" color=\"#D2D2D2\">" +
		            "Before the game starts, you can select from " + Helicopter.NR_OF_TYPES + " " +
		            "<font color=\"#FFFFD2\">helicopter types" +
		            "<font color=\"#D2D2D2\"> which differ in their starting " +
		            "attributes and their available upgrades. With the " +
		            "helicopter of your choice, you are supposed to destroy " +
		            "as many enemies as possible. Each successful " +
		            "destruction of a hostile flying vessels is financially rewarded. " +
		            "After a certain number of " +
		            "desroyed enemies, you will proceed to higher levels " +
		            "where you " +
		            "have to face more and more powerful enemies. The more " +
		            "difficult it is to elminate an opponent, the more " +
		            "generously the destruction is rewarded. You can spend " +
		            "your earned money in the <font color=\"#FFFFD2\">repair " +
		            "shop<font color=\"#D2D2D2\"> on repairs or on " +
		            "new upgrades which improve your helicopter." +
		            "</font></html>"
					);
				}
				else if(page == 1)
				{
					// Finanzen/Reparatur
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) +
					"\" face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"After a crash, your helicopter must be repaired before " +
					"you can start a new mission. If you can't afford the " +
					"repairs, the game is over. Of course, you can request " +
					"a repair even after minor damages. The price for a " +
					"repair depends on how heavily damaged the helicopter " +
					"is. By repairing your helicopter before a crash, you " +
					"can save money. After repairing your helicopter, you " +
					"will fall back to the last " +
					"<font color=\"#FFFFD2\">\"save\" level" +
					"<font color=\"#D2D2D2\">" +
					" (1, 6, 11, 16 " +
					"and so on).<br><br>How well you are paid for the " +
					"destruction " +
					"of an enemy depends on how strong the enemy was and how " +
					"well you are paid. Pilots whose helicopter is equipped " +
					"with a <font color=\"#FFFFD2\">spot light" +
					"<font color=\"#D2D2D2\"> can fly " +
					"<font color=\"#FFFFD2\">night-" +
					"<font color=\"#D2D2D2\"> and " +
					"<font color=\"#FFFFD2\">daytime missions" +
					"<font color=\"#D2D2D2\">. " +
					"Due to this flexibility, their income is higher." +
					"<br><br>To " +
					"find out whether an upgrade is cheap or expansive, you " +
					"can have a look at the buttons' colors:<br> " +
					"<font color=\"#82FF82\">green<font color=\"#D2D2D2\"> - " +
					"very cheap; <font color=\"#D2FFB4\">yellow green" +
					"<font color=\"#D2D2D2\"> - cheap; " +
					"<font color=\"#FFD200\">yellow<font color=\"#D2D2D2\"> " +
					"- standard price; <font color=\"#FFA578\">orange" +
					"<font color=\"#D2D2D2\"> - expansive; " +
					"<font color=\"#FF7369" +
					"\">red<font color=\"#D2D2D2\"> - extortionate" +
		            "</b></font></html>"
					);
				}
				else if(page == 2)
				{
					// Upgrades
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + 
					"\" face=\"Dialog\" color=\"#D2D2D2\">" +
					"There are two types of upgrades: " +
					"<font color=\"#FFFFD2\">Standard upgrades<font " +
					"color=\"#D2D2D2\"> and <font " +
					"color=\"#FFFFD2\">special upgrades<font " +
					"color=\"#D2D2D2\">. With the standard upgrades you can " +
					"enhance <font color=\"#FFFFD2\">rotor system" +
					"<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">missile " +
					"drive<font color=\"#D2D2D2\">, <font color=\"#FFFFD2\">" +
					"plating<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">firepower" +
					"<font color=\"#D2D2D2\">, " +
					"<font color=\"#FFFFD2\">fire rate" +
					"<font color=\"#D2D2D2\"> as well as a type specific " +
					"energy consuming ability (see section " +
					"<font color=\"#FFFFFF\">\"" +
					"Helicopter types\"" +
					"<font color=\"#D2D2D2\">). Depending on " +
					"the helicopter type, each of the standard upgrades can " +
					"be improved to level 6, 8 or even 10. Special upgrades " +
					"are purchased only once and (with only a few " +
					"exceptions) cannot be upgraded further. In addition to " +
					"<font color=\"#FFFFD2\">spot lights" +
					"<font color=\"#D2D2D2\"> for " +
					"<font color=\"#FFFFD2\">night " +
					"missions<font color=\"#D2D2D2\">, the following special " +
					"upgrades are available for all helicopter types: " +
					"<font color=\"#FFFFD2\">Goliath plating" +
					"<font color=\"#D2D2D2\"> (improves the effectiveness of " +
					"the standard plating), " +
					"<font color=\"#FFFFD2\">piercing " +
					"warheads<font color=\"#D2D2D2\"> (the same missile " +
					"can hit multiple opponents) and " +
					"<font color=\"#FFFFD2\">second cannon" +
					"<font color=\"#D2D2D2\"> " +
					"(simultaneous launch of two missiles). There are some " +
					"other special upgrades only available for a particular " +
					"helicoper type (see section " +
					"<font color=\"#FFFFFF\">\"Helicopter types\"" +
					"<font color=\"#D2D2D2\">)." +
		            "</b></font></html>"
					);
				}
				else if(page == 3)
				{
					// Boss-Gegner
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" face=\"Dialog" +
					"\" color=\"#D2D2D2\">" +
					"Every 10 levels, the player encounters a " +
					"<font color=\"#FFFFD2\">boss enemy" +
					"<font color=\"#D2D2D2\"> " +
					"which is particularly difficult to defeat. " +
					"Occasionally, <font color=\"#FFFFD2\">minor boss " +
					"enemies<font color=\"#D2D2D2\"> will also appear in " +
					"the standard levels. These enemies are harder to " +
					"destroy than the regular ones. For each successful " +
					"destruction of a boss enemy, the player receives " +
					"a generous reward. " +
					"After their destruction, " +
					"all boss enemies drop a " +
					"<font color=\"#FFFFD2\">power-up" +
					"<font color=\"#D2D2D2\"> " +
					"(see section <font color=\"#FFFFFF\">\"Power-ups\"" +
					"<font color=\"#D2D2D2\">)." +
					"</font></html>"
					);
				}
				else if(page == 4)
				{
					// Bedienung
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\"><b>" +		
					"HelicopterDefence is exclusively mouse controlled:<br>" +
					"Your Helicopter always moves towards the mouse cursor. " +
					"Press the left mouse button to launch missiles. " +
					"You can turn around your helicopter by pressing the middle mouse button (scroll wheel). " +
					"This enables you to shoot in the opposite direction.<br>" +
					"With the right mouse button you can use the " +
					"unique energy ability of your helicopter:<br>-	Using " +
					"the <font color=\"#FFFFD2\">teleporter" +
					"<font color=\"#D2D2D2\"> (" +
					"<font color=\"#FFFFD2\">Phoenix " +
					"type<font color=\"#D2D2D2\">): Move the mouse cursor " +
					"while holding<br> &nbsp the right mouse button and " +
					"release the " +
					"right button at the location of your choice.<br>" +
					"-	Using the <font color=\"#FFFFD2\">energy shield" +
					"<font color=\"#D2D2D2\"> (" +
					"<font color=\"#FFFFD2\">Roch " +
					"type<font color=\"#D2D2D2\">): Press the right mouse " +
					"button and hold it.<br>" +
					"- Launching a <font color=\"#FFFFD2\">stunning " +
					"missile<font color=\"#D2D2D2\"> (" +
					"<font color=\"#FFFFD2\">Orochi type" +
					"<font color=\"#D2D2D2\">): Launch a missile (left mouse " +
					"button) while holding the right mouse button.<br>" +
					"- Activate <font color=\"#FFFFD2\">plasma missiles" +
					"<font color=\"#D2D2D2\"> (" +
					"<font color=\"#FFFFD2\">Kamaitachi " +
					"class<font color=\"#D2D2D2\">): Press the right mouse " +
					"button.<br>- Triggering an <font " +
					"color=\"#FFFFD2\">electro magnetic " +
					"pulse<font color=\"#D2D2D2\"> (" +
					"<font color=\"#FFFFD2\">Pegasus-class" +
					"<font color=\"#D2D2D2\">): Press the right mouse button." +					
					"<br>- Using the <font color=\"#FFFFD2\">PU generator" +
					"<font color=\"#D2D2D2\"> (" +
					"<font color=\"#FFFFD2\">Helios " +
					"class<font color=\"#D2D2D2\">): Press the right mouse " +					
					"<br>After landing the helicopter, the " +
					"<font color=\"#FFFFD2\">" +
					"repair shop<font color=\"#D2D2D2\"> button becomes " +
					"visible. Press this button to enter the repair shop." +
				    "</b></font></html>" 
					);
				}
				else if(page == 5)
				{
					// PowerUps
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"After destruction, some opponents drop one of the 6 " +
					"following <font color=\"#FFFFD2\">power-ups" +
					"<font color=\"#D2D2D2\">: " +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br>" +
					"<br><font size = \"" + (HTML_SIZE-1) + "\">" +
					"Bonus credit" +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" +
					"Unlimited energy for 15 seconds" +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" +
					"Partial repairs " +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" +
					Helicopter.INVU_DMG_REDUCTION + "% Indestructibility " +
					"for 15 seconds " +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" + 
					"Triple damage for 15 seconds" +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" +
					"Increased fire rate for 15 seconds" +
					"</b></font></html>" 
					);
				}				
				else if(page == 7)
				{
					// Hardcore-Mode
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" face=\"Dialog" +
					"\" color=\"#D2D2D2\">" +										
					"In <font color=\"#FFFFD2\">hardcore mode<font color=\"#D2D2D2\">, players do not receive any "
					+ "financial reward for the "
					+ "destruction of a hostile flying vessel. Instead, "
					+ "every level up is generously rewarded. The more "
					+ "successful you played with the other helicopter "
					+ "classes, the more money you can make."
					+ "<br><br>Background: The world government leaded by "
					+ "President Karanijem Su has no trust in the loyalty "
					+ "of <font color=\"#FFFFD2\">Helios type<font color=\"#D2D2D2\"> helicopter "
					+ "constructors. 'Where did they get the knowledge to "
					+ "use alien technology?' Therefore, he excluded them "
					+ "from the reservist program and denies them his "
					+ "support. Fortunately, in a moment of despair, a "
					+ "league of helicopter pilots formed, willing to "
					+ "spend a part of their income to pilots of Helios "
					+ "type helicopters."				
					+ "<font color=\"#D2D2D2\">" +
					"</font></html>"
					);
				}				
			}
			else if(Events.window == HELICOPTER_TYPES)
			{
				if(page == 0)
				{				
					// Allgemein
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
					"You can choose from " + Helicopter.NR_OF_TYPES + " different " +
					"<font color=\"#FFFFD2\">helicopter types" +
					"<font color=\"#D2D2D2\">. Each " +
					"of these helicopters has a unique, energy-consuming " +
					"ability and at least one " +
					"<font color=\"#FFFFD2\">special upgrade" +
					"<font color=\"#D2D2D2\">, which can " +
					"only be purchased by this helicopter type. Furthermore, " +
					"the helicopter types differ in terms of price and " +
					"upgradeability of the individual " +
					"<font color=\"#FFFFD2\">standard upgrades" +
					"<font color=\"#D2D2D2\"> " +
					"(see section <font color=\"#FFFFFF\">\"Finances & " +
					"repairs\"<font color=\"#D2D2D2\"> and " +
					"<font color=\"#FFFFFF\">\"Upgrades\"" +
					"<font color=\"#D2D2D2\">)." +
					" Thus, the playing style of each helicopter type may " +
					"differ considerably. In particular, each helicopter " +
					"type has its own way to acquire " +
					"<font color=\"#FFFFD2\">bonus credit" +
					"<font color=\"#D2D2D2\">, " +
					"which is a reward for very impressive defensive " +
					"performance. " +
					"For more details, have a look at the detailed " +
					"descriptions of each helicopter type." +
					"</font></html>"
		            );
				}				
				else if(page == 2)
				{
					// Phönix
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"<font color=\"#FFFFD2\">Phoenix type helicopters" +
					"<font color=\"#D2D2D2\"> are the best armor-plated " +
					"from all helicopter types since all plating-related " +
					"upgrades including <font color=\"#FFFFD2\">Goliath " +
					"plating<font color=\"#D2D2D2\"> can be purchased at " +
					"low-cost. In contrast, the <font color=\"#FFFFD2\">" +
					"rotor system<font color=\"#D2D2D2\"> of Phoenix type " +
					"helicopters is substandard and thus their top speed is " +
					"pretty slow. Actually, this disadvantage is not too " +
					"severe since all Phoenix type helicopters are equipped " +
					"with a <font color=\"#FFFFD2\">teleporting device" +
					"<font color=\"#D2D2D2\">, which can beam the " +
					"helicopter to another location and make it " +
					"indestructible for a brief moment. Only Phoenix type " +
					"helicopters can purchase the <font color=\"#FFFFD2\">" +
					"special upgrade<font color=\"#D2D2D2\"> " +
					"<font color=\"#FFFFD2\">\"Short-range radiation\"" +
					"<font color=\"#D2D2D2\">. With this upgrade, the " +
					"plating of all opponents in close distance will be " +
					"considerably weakened, causing them to take severe " +
					"damage when colliding with the helicopter. On the " +
					"other hand, the helicopter will take less damage when " +
					"colliding with irradiated flying vessels. " +
					"<font color=\"#FFFFD2\">Standard " +
					"upgrades<font color=\"#D2D2D2\"> of the helicopter's " +
					"<font color=\"#FFFFD2\">firepower" +
					"<font color=\"#D2D2D2\"> will also " +
					"improve the intensity of short-range radiation. When a " +
					"helicopter teleports itself to another location and " +
					"there instantly destroys multiple enemies, the player " +
					"will be rewarded with <font color=\"#FFFFD2\">bonus " +
					"credit<font color=\"#D2D2D2\">. Another way to " +
					"receive a bonus credit is to eliminate a hostile flying " +
					"vessel by missile attack immediately after using the " +
					"teleporting device. " +
					"</b></font></html>"
					);
				}
				else if(page == 3)
				{
					// Roch
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +	
					"For all <font color=\"#FFFFD2\">Roch type helicopters" +
					"<font color=\"#D2D2D2\">, the <font color=\"#FFFFD2\">" +
					"firepower standard upgrades<font color=\"#D2D2D2\"> as " +
					"well as the <font color=\"#FFFFD2\">special upgrade \"" +
					"Piercing missiles\"<font color=\"#D2D2D2\"> can be " +
					"purchased at very low cost. " +
					"Additionally, only Roch type helicopters can acquire " +
					"the special upgrade <font color=\"#FFFFD2\">\"Jumbo " +
					"missiles\"<font color=\"#D2D2D2\">, which gives " +
					"their missiles enormous explosive force. Because of " +
					"this, no other helicopter class can reach comparable " +
					"high firepower. However, Roch type helicopters only " +
					"have pretty low <font color=\"#FFFFD2\">fire rate" +
					"<font color=\"#D2D2D2\"> and also their " +
					"<font color=\"#FFFFD2\">plating<font color=\"#D2D2D2\"> is " +
					"inferior. However, an <font color=\"#FFFFD2\">energy " +
					"shield<font color=\"#D2D2D2\"> compensates for " +
					"their weak plating. If the pilot of a Roch type " +
					"helicopter manages to destroy multiple flying vessels " +
					"with the same missile, he will be rewarded with " +
					"<font color=\"#FFFFD2\">bonus credit." +
					"</font></html>"
		            ); 
				}
				else if(page == 4)
				{
					// Orochi
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
					"<font color=\"#FFFFD2\">Orochi type helicopters" +
					"<font color=\"#D2D2D2\"> are true all-rounder: They fly " +
					"pretty fast, have missiles with excellent drive and are " +
					"still properly armor-plated. The " +
					"<font color=\"#FFFFD2\">fire rate" +
					"<font color=\"#D2D2D2\"> of " +
					"Orchi type helicopters is limited, but only they can " +
					"purchase a <font color=\"#FFFFD2\">third cannon<font " +
					"color=\"#D2D2D2\"> and install a " +
					"<font color=\"#FFFFD2\">radar device" +
					"<font color=\"#D2D2D2\">. " +
					"With the " +
					"latter, cloaked flying vessels can be detected. " +
					"Provided with enough energy, this helicopter can also " +
					"launch <font color=\"#FFFFD2\">stunning missiles" +
					"<font color=\"#D2D2D2\">, which bounce back enemies " +
					"and stun them for a brief moment. By improving " +
					"<font color=\"#FFFFD2\">missile " +
					"drive<font color=\"#D2D2D2\">, the knock-back effect " +
					"of stunning missiles is " +
					"further increased. If an Orochi type helicopter " +
					"launches several missiles at the same time and at " +
					"least two of these destroy a hostile flying vessel, " +
					"the helicopter's pilot will be rewarded with a " +
					"<font color=\"#FFFFD2\">bonus " +
					"credit<font color=\"#D2D2D2\">." + 
					"</font></html>"
					); 
				}
				else if(page == 5)
				{
					// Kamaitachi				
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +	
					"<font color=\"#FFFFD2\">Kamaitachi type helicopters" +
					"<font color=\"#D2D2D2\"> have weak " +
					"<font color=\"#FFFFD2\">firepower" +
					"<font color=\"#D2D2D2\">, but " +
					"thanks to low-cost <font color=\"#FFFFD2\">standard " +
					"upgrades<font color=\"#D2D2D2\"> on " +
					"<font color=\"#FFFFD2\">fire rate" +
					"<font color=\"#D2D2D2\"> and " +
					"the <font color=\"#FFFFD2\">rapid fire upgrade" +
					"<font color=\"#D2D2D2\">, which is only available  for " +
					"Kamaitachi type helicopters, it can reach an extreme " +
					"high fire rate. Combined with " +
					"<font color=\"#FFFFD2\">plasma missiles" +
					"<font color=\"#D2D2D2\">, which " +
					"have much more explosive force, this helicopter type " +
					"can achieve considerable high damage output per second. " +
					"However, the activation of plasma missiles consumes " +
					"energy and only lasts for 15 seconds. Therefore, plasma " +
					"missiles can not be used permanently and the timing of " +
					"their activation must be considered carefully. If the " +
					"pilot of a Kamaitachi type helicopter manages to " +
					"destroy " +
					"multiple enemies within a very short period of time, he " +
					"will be rewarded for his brave actions with generous " +
					"<font color=\"#FFFFD2\">bonus credit" +
					"</font></html>"
		            ); 
				}				
				else if(page == 6)
				{
					// Pegasus
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"<font color=\"#FFFFD2\">Pegasus type helicopters" +
					"<font color=\"#D2D2D2\"> would be at a serious " +
					"disadvantage if they had to rely on their missiles " +
					"alone. However, these helicopters are equipped with a " +
					"powerful <font color=\"#FFFFD2\">EMP generator" +
					"<font color=\"#D2D2D2\"> which emits electromagnetic " +
					"pulses that damage and stun all hostile flying vessels " +
					"in a certain area around the helicopter. Upgrades of " +
					"the EMP generator increase the damage output as well " +
					"as the EMP's area of effect. The destruction of " +
					"multiple enemies with the same electromagnetic pulse " +
					"is rewarded with <font color=\"#FFFFD2\">bonus credit" +
					"<font color=\"#D2D2D2\">. Another important " +
					"feature of Pegasus type helicopters is their " +
					"<font color=\"#FFFFD2\">interphase " +
					"generator<font color=\"#D2D2D2\">. It allows the " +
					"helicopter to partially shift " +
					"into another dimension. In this state, the helicopter " +
					"is protected against any kind of damage. If no missile " +
					"was launched and no EMP was released for a certain " +
					"time, the interphase generator is activated " +
					"automatically. Upgrading <font color=\"#FFFFD2\">fire " +
					"rate<font color=\"#D2D2D2\"> also shortens " +
					"the time required for phase shifting. Missiles that " +
					"are launched during a phase shift, are in an " +
					"intermediate state between two dimensions and can " +
					"therefore easily penetrate the enemy's plating " +
					"resulting in severe damage. After launching a missile, " +
					"the helicopter shifts back into its original state." +
					"</b></font></html>"
		            ); 
				}	
				else if(page == 7)
				{
					// Helios
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
					
					"Upgraded with alien technology collected from crashed "
					+ "alien vessels, <font color=\"#FFFFD2\">Helios type "
					+ "helicopters<font color=\"#D2D2D2\"> are a powerful "
					+ "weapon against the alien invasion. With their "
					+ "installed <font color=\"#FFFFD2\">PU Generator"
					+ "<font color=\"#D2D2D2\">, they can use energy to "
					+ "generate <font color=\"#FFFFD2\">power-ups"
					+ "<font color=\"#D2D2D2\"> and they "
					+ "can even control PowerUp movement: with a "
					+ "<font color=\"#FFFFD2\">power-up immobilizer "
					+ "<font color=\"#D2D2D2\">installed, "
					+ "power-ups can be forced to the ground where they can be "
					+ "collected more easily. Helios type Helicopters are "
					+ "heavily supported by the league of Helicopter pilots "
					+ "who are willing to share their individually upgrade "
					+ "knowledge. So the more successful you played with the "
					+ "other helicopter classes, the less you will suffer "
					+ "from high <font color=\"#FFFFD2\">upgrade costs. "
					+ "<br><font color=\"#D2D2D2\">Helios-type helicopters can "
					+ "only be played in <font color=\"#FFFFD2\">"
					+ "hardcore mode.<font color=\"#D2D2D2\">" +					
					"<font color=\"#D2D2D2\">" +
					"</font></html>"
		            ); 
				}
			}			
			else if(Events.window == CONTACT)
			{
				if(page == 0)
				{
					my_label.setFont(PLAIN18);
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE + "\" " +
							"face=\"Dialog\" color=\"#D2D2D2\">" +
				    "You have some new ideas or want to provide suggestions " +
				    "for improvements for HelicopterDefence?<br> Then don't " +
				    "hesitate to write me an email: " +
					"<font color=\"#FFFFD2\">info@HelicopterDefence.de" +
					"<font color=\"#D2D2D2\"> " +
					"<br><br>If you can't select your native language in the " +
					"settings and are willing to help, I gladly except your " +
					"translation assistance. <br><br>I'm looking forward to " +
					"hearing from you!" +
					"<br><br>Best regards," +
					"<br>Björn Hansen" +
					"</font></html>"
				    );
				}
			}
		}
		else if(language == GERMAN)
		{
			if(Events.window == INFORMATIONS)
			{
				if(page == 0)
				{				
					// Handlung
					my_label.setText(		
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
					"Wir schreiben das Jahr 2371. Seit dem " +
					"Unabhängigkeitskrieg der Mars-Kolonien, der nun schon " +
					"über 100 Jahre zurück liegt, hat es keine " +
					"kriegerischen Auseinandersetzungen unter Menschen mehr " +
			        "gegeben und Ausgaben für militärische Zwecke sind " +
			        "folglich auf ein Minimum reduziert worden. " +	    
			        "\n\nDoch nun wurden tausende feindlicher Flugobjekte " +
			        "unbekannter Herkunft über der nördlichen " +
			        "Sahara und an der afrikanischen Mittelmeerküste " +
			        "gesichtet. Einige dieser Flugobjekte haben bereits die " +
			        "nordafrikanischen Megametropolen unter Beschuss " +
			        "genommen, darunter auch Welthauptstadt Kairo. " +	    
			        "\n\nKaranijem Su, der Präsident der Weltregierung, hat " +
			        "den Ausnahmezustand verhängt und bittet " +
			        "Reservisten und Freiwillige aus aller Welt um Hilfe. " +
			        "Sie sind einer davon." +
			        "</b></font></html>"
			        );
				}
				else if(page == 1)
				{
					// Änderungen seit 1.0
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE + "\" " +
							"face=\"Dialog\" color=\"#D2D2D2\">" +
					"- Zu Gunsten einer größeren Einsteigerfreundlichkeit " +
					"wurde der Schwierigkeitsgrad der<br>" +
		            "&nbsp ersten Levels deutlich gesenkt und die " +
		            "Spielanleitung wesentlich ausführlicher gestaltet.<br>" +
		            "- Die bisher eher unbeliebten Helikopter der " +
		            "<font color=\"#FFFFD2\">Phönix-" +
		            "<font color=\"#D2D2D2\">, " +
		            "<br>	&nbsp <font color=\"#FFFFD2\">Orochi-" +
		            "<font color=\"#D2D2D2\"> und " +
		            "<font color=\"#FFFFD2\">Kamaitachi-Klasse" +
		            "<font color=\"#D2D2D2\"> sind jetzt schlagkräftiger.<br>" +
		            "- Das Spiel verfügt nun über bewegte Hintergründe.<br>" +
		            "- Statt wie zuvor alle 10 erreicht der Spieler jetzt " +
		            "schon alle 5 Level einen " +
		            "Spielstand,<br> &nbsp von dem aus er nach Reparatur " +
		            "oder Absturz nicht mehr zurück fallen kann.<br>" +
		            "- Viele Änderungen, die unter anderem Spielbalance, " +
		            "Grafik<br>	&nbsp oder das Upgrade-System betreffen, " +
		            "wurden vorgenommen." +
		            "</b></font></html>");
				}
				else if(page == 3)
				{
					// Änderungen seit 1.1
					my_label.setText(	
					"<html><font = \"" + (HTML_SIZE-1) + "\" face=\"Dialog\" " +
					"color=\"#D2D2D2\"><b>" +
					"- Zur Verbesserung der Grafik kommen Gradientenfarben " +
					"und Antialiasing zum Einsatz.<br>" +
					"- Jede Helikopter-Klasse verfügt nun über ein eigenes, " +
					"energieverbrauchendes <font color=\"#FFFFD2\">\"Standard-Upgrade\"<font color=\"#D2D2D2\"> " +
					"sowie über <br> &nbsp mindestens ein eigenes <font color=\"#FFFFD2\">\"Spezial-Upgrade\"<font color=\"#D2D2D2\">. Damit unterscheiden sich die Helikopter-" +
					"Klassen jetzt deutlicher.<br>" +
					"- Mit der <font color=\"#FFFFD2\">Pegasus-" +
					"<font color=\"#D2D2D2\"> und der  <font color=\"#FFFFD2\">Helios-Klasse" +
					"<font color=\"#D2D2D2\"> stehen dem Spieler nun " +
					"zwei weitere Helikopter-Klassen zur Verfügung.<br>" +
					"- Als neuer Spielmodus ist der <font color=\"#FFFFD2\">Hardcore-Modus" +
					"<font color=\"#D2D2D2\"> verfügbar, welcher nur von der Helios-Klasse gespielt werden kann.<br>" +
					"- Für zusätzlichen Spielreiz sorgen zufällig " +
					"auftretende <font color=\"#FFFFD2\">\"Mini-Endgegner\"" +
					"<font color=\"#D2D2D2\">. <br>	&nbsp Diese können " +
					"<font color=\"#FFFFD2\">PowerUps" +
					"<font color=\"#D2D2D2\"> " +
					"verlieren, welche den Helikopter kurzfristig " +
					"verbessern.<br>" +					
					"- Das Spiel läuft in einer vom Explorer unabhängigen " +
					"Applikation.<br>" +
					"- Eine Speicherfunktion und eine Highscore wurden implementiert.<br>" +
					"- Eine Reihe neuer Gegner-Klassen wurden dem Spiel hinzugefügt.<br>" +
					"- Viele weitere Änderungen, welche die Bedienung, das " +
					"Upgrade-System, <br>	&nbsp die Grafik oder die " +
					"Spielbalance betreffen wurden vorgenommen." +
					"</b></font></span></html>"
					);
				}
				else if(page == 4)
				{				
					// Credits
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
							"face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"Besonderer Dank gilt allen meinen Beta-Testern, die " +
					"HelikopterDefence 1.0 " +
					"ausführlich gespielt und mich mit hilfreichen Tipps " +
					"und Verbesserungsvorschlägen " +
					"unterstützt haben:<br><font color=\"#FFFFD2\">Alexander " +
					"Schmuck, Andreas Lotze, Boris Sapancilar, Fynn Hansen, Julian Tan, " +
					"Hauke Holm, Henner Holm, Michael Sujatta,<br>Sascha " +
					"Degener, Thorsten Rückert, Tim Schumacher, " +
					"Yannik Muthmann<br><font color=\"#D2D2D2\"><br>" +
					"Besonders bedanken möchte ich mich bei " +
					"<font color=\"#FFFFD2\">Fabian Gebert" +
					"<font color=\"#D2D2D2\"> für eine Vielzahl " +
					"wertvoller technischer Ratschläge, welche meine " +
					"Entwicklungsarbeit an diesem Spiel erheblich " +
					"bereichert haben. Ich bedanke mich auch bei " + 
					"<font color=\"#FFFFD2\">Michael Sujatta" +
					"<font color=\"#D2D2D2\"> fürs Korrekturlesen und bei " +
					"<font color=\"#FFFFD2\">Hauke Holm" +
					"<font color=\"#D2D2D2\"> für seine Hilfe bei der " +
					"Bearbeitung " +
					"von Audio-Dateien. Vielen Dank an " +
					"<font color=\"#FFFFD2\">Tobias P. Eser" +
					"<font color=\"#D2D2D2\">. Mit ihm zusammen habe ich " +
					"noch zu " +
					"Schulzeiten mein Interesse an der " +
					"Computerspielentwicklung entdeckt. Zuletzt möchte ich " +
					"mich " +
					"bei <font color=\"#FFFFD2\">Prof. Till Tantau" +
					"<font color=\"#D2D2D2\"> für eine exzellente " +
					"Informatikvorlesung bedanken. Seine Lehrveranstaltung " +
					"hat den Grundstein zur Entwicklung dieses Spiels " +
					"gelegt.<br><br>Du möchtest auch Beta-Tester werden? " +
					"Kein Problem! Schicke Deine Verbesserungsvorschläge an: " +
					"<font color=\"#FFFFD2\">info@HelicopterDefence.de" +			
					"</font></b></html>"
					);
				}		
				else if(page == 5)
				{				
					// Copyright
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
					"Dies ist ein Freeware-Spiel. Die Weitergabe ist also " +
					"ausdrücklich erlaubt und auch erwünscht! " +
		            "Programmänderungen jeglicher Art sind allerdings dem " +
		            "Programmierer vorbehalten!" +
		            "</font></html>"
		            );
				}
			}
			else if(Events.window == DESCRIPTION)
			{
				if(page == 0)
				{				
					// Spielbeschreibung
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
		            "Zu Beginn des Spiels stehen Ihnen " + Helicopter.NR_OF_TYPES + " verschiedene " +
		            "<font color=\"#FFFFD2\">Helikopter-Klassen" +
		            "<font color=\"#D2D2D2\"> mit unterschiedlichen " +
		            "Startwerten zur " +
		            "Auswahl. Mit dem Helikopter Ihrer Wahl sollten Sie " +
		            "möglichst viele feindliche Flugobjekte abschießen. Jede " +
		            "erfolgreiche Zerstörung eines feindlichen Flugkörpers " +
		            "wird finanziell " +
		            "belohnt. Nach einer bestimmten " +
		            "Anzahl von Treffern können Sie in höhere Level " +
		            "aufsteigen, in denen immer schwerere Gegner auf Sie " +
		            "warten. " +
		            "Die Bezahlung bei Eliminierung von feindlichen Fliegern " +
		            "mit besonders komplizierten Flugmanövern ist " +
		            "entsprechend höher. " +
		            "Mit dem so erwirtschafteten Geld können Sie in der " +
		            "<font color=\"#FFFFD2\">Werkstatt" +
		            "<font color=\"#D2D2D2\"> Beschädigungen am Helikopter " +
		            "reparieren lassen sowie neue " +
		            "Upgrades erwerben, mit denen ihr Helikopter noch " +
		            "schlagfertiger wird." +
		            "</font></html>"
					);
				}
				else if(page == 1)
				{
					// Finanzen/Reparatur
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"Nach dem Absturz des Helikopters (Totalschaden) muss " +
					"in der <font color=\"#FFFFD2\">Werkstatt" +
					"<font color=\"#D2D2D2\"> eine <font color=\"#FFFFD2\">" +
					"Reparatur<font color=\"#D2D2D2\"> durchgeführt werden," +
					" bevor ein neuer Einsatz geflogen werden kann. Wenn " +
					"der Spieler in diesem Fall nicht " +
					"über genügend Guthaben verfügt, um die Instandsetzung " +
					"zu finanzieren, ist das Spiel " +
					"beendet. Natürlich kann der Spieler Reparaturen auch " +
					"bereits nach kleinen " +
					"Beschädigungen durchführen. Der Preis für eine " +
					"Reparatur hängt vom Ausmaß der " +
					"Beschädigung ab. Bei einem Totalschaden verteuert sich " +
					"die Reparatur. Nach einer Reparatur fällt der Spieler " +
					"zum letzten <font color=\"#FFFFD2\">\"sicheren\" Level" +
					"<font color=\"#D2D2D2\"> " +
					"(1, 6, 11, 16, usw.) zurück.<br><br>Die Prämie, die " +
					"ein Spieler für abgeschossene Gegner " +
					"erhält, hängt von deren Stärke und der aktuellen " +
					"Sold-Stufe ab. Piloten eines mit " +
					"<font color=\"#FFFFD2\">Scheinwerfern" +
					"<font color=\"#D2D2D2\"> ausgestatteten Helikopters " +
					"können sowohl <font color=\"#FFFFD2\">Tag-" +
					"<font color=\"#D2D2D2\"> als auch " +
					"<font color=\"#FFFFD2\">Nachteinsätze" +
					"<font color=\"#D2D2D2\"> " +
					"fliegen und erhalten daher einen Verdienstbonus von " +
					"50%.<br><br>In der Werkstatt kann der " +
					"Spieler anhand der farblichen Markierungen um die " +
					"Upgrade-Buttons erfahren, wie " +
					"preisgünstig ein Upgrade ist: <font color=\"#82FF82\">" +
					"grün<font color=\"#D2D2D2\"> - sehr guter Preis; " +
					"<font color=\"#D2FFB4\">gelbgrün" +
					"<font color=\"#D2D2D2\"> - guter Preis; " +
					"<font color=\"#FFD200\">gelb<font color=\"#D2D2D2\"> - " +
					"angemessener Preis; <font color=\"#FFA578\">orange" +
					"<font color=\"#D2D2D2\"> - hoher Preis; " +
					"<font color=\"#FF7369\">rot<font color=\"#D2D2D2\"> - " +
					"Wucher" +
		            "</b></font></html>"
					);
				}
				else if(page == 2)
				{
					// Upgrades
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
					"Die Helikopter-Upgrades unterteilen sich in " +
					"<font color=\"#FFFFD2\">Standard-" +
					"<font color=\"#D2D2D2\"> und <font color=\"#FFFFD2\">" +
					"Spezial-Upgrades<font color=\"#D2D2D2\">. Mit den " +
					"Standard-Upgrades " +
		            "können <font color=\"#FFFFD2\">Hauptrotor" +
		            "<font color=\"#D2D2D2\">, <font color=\"#FFFFD2\">" +
		            "Raketenantrieb<font color=\"#D2D2D2\">, " +
		            "<font color=\"#FFFFD2\">Panzerung" +
		            "<font color=\"#D2D2D2\">, <font color=\"#FFFFD2\">" +
		            "Feuerkraft<font color=\"#D2D2D2\">, " +
		            "<font color=\"#FFFFD2\">Schussrate" +
		            "<font color=\"#D2D2D2\"> sowie eine klassenspezifische " +
		            "Energie-Fertigkeit (siehe Abschnitt " +
		            "<font color=\"#FFFFFF\">\"Helikopter-Klassen\"" +
		            "<font color=\"#D2D2D2\">) gesteigert werden. Je nach " +
		            "Helikopter-Klasse können die einzelnen Standard-" +
		            "Upgrades bis Stufe 6, 8 oder 10 ausgebaut werden. " +
		            "\nSpezial-Upgrades werden nur einmal erworben und " +
		            "können (mit wenigen Ausnahmen) nicht weiter gesteigert " +
		            "werden. " +
		            "Neben den <font color=\"#FFFFD2\">Scheinwerfern" +
		            "<font color=\"#D2D2D2\"> für <font color=\"#FFFFD2\">" +
		            "Nachteinsätze<font color=\"#D2D2D2\"> sind folgende " +
		            "Spezialupgrades für jede Helikopter-Klasse verfügbar: " +
		            "\n<font color=\"#FFFFD2\">Goliath-Panzerung" +
		            "<font color=\"#D2D2D2\"> (verbessert die Effektivität " +
		            "der Standardpanzerung), " +
		            "\n<font color=\"#FFFFD2\">Durchstoßsprengköpfe" +
		            "<font color=\"#D2D2D2\"> (dieselbe Rakete kann mehrere " +
		            "Gegner treffen) und " +
		            "\n<font color=\"#FFFFD2\">zweite Bordkanone" +
		            "<font color=\"#D2D2D2\">\t(gleichzeitiges abschießen " +
		            "zweier Raketen). " +
		            "Darüber hinaus existieren weitere Spezial-Upgrades, die " +
		            "nur für eine " +
		            "einzige Helicoper-Klasse verfügbar sind (siehe " +
		            "Abschnitt <font color=\"#FFFFFF\">\"Helikopter-" +
		            "Klassen\"<font color=\"#D2D2D2\">). " +
		            "</b></font></html>"
					);
				}
				else if(page == 3)
				{
					// Boss-Gegner
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
					"Alle 10 Level trifft der Spieler auf einen besonders " +
					"schwer zu besiegenden <font color=\"#FFFFD2\">Boss-" +
					"Gegner<font color=\"#D2D2D2\">, " +
					"aber auch in den Standard-Levels können gelegentlich " +
					"kleine Boss-Gegner (<font color=\"#FFFFD2\">Mini-Bosse" +
					"<font color=\"#D2D2D2\">) erscheinen, welche schwerer " +
					"zu zerstören sind als gewöhnliche Gegner. " +
					"Für den Abschuss eines Boss-Gegners erhält der Spieler " +
					"eine großzügige finanzielle Belohnung. Alle Boss-Gegner " +
					"verlieren nach ihrer Zerstörung außerdem " +
					"ein <font color=\"#FFFFD2\">PowerUp" +
					"<font color=\"#D2D2D2\"> (siehe Abschitt " +
					"<font color=\"#FFFFFF\">\"PowerUps\"" +
					"<font color=\"#D2D2D2\">)." +
					"</font></html>"
					);
				}
				else if(page == 4)
				{
					// Bedienung
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\"><b>" +		
					"Die Steuerung des Helikopters erfolgt ausschließlich mit der Maus:<br>" +
					"Der Helikopter bewegt sich immer auf den Maus-Cursor " +
					"zu. " +
				    "Das Betätigen der linken Maustaste bewirkt den " +
				    "Abschuss einer Rakete. " +
				    "Mit der mittleren Maustaste (Mausrad) lässt sich der Helikopter wenden. " +
				    "Er schießt dann in die entgegengesetzte Richtung.<br>" +
				    "Die Spezialfertigkeit der jeweiligen " +
				    "<font color=\"#FFFFD2\">Helikopter-Klasse" +
				    "<font color=\"#D2D2D2\"> kann mit der rechten Maustaste eingesetzt werden:" +
				    "<br>- Benutzen des <font color=\"#FFFFD2\">Teleporters" +
				    "<font color=\"#D2D2D2\"> (<font color=\"#FFFFD2\">" +
				    "Phönix-Klasse<font color=\"#D2D2D2\">): mit gedrückt " +
				    "gehaltener rechter Maustaste<br> " +
				    "&nbsp den Maus-Cursor an einen anderen Ort ziehen und " +
				    "dort die rechte Maustaste lösen" +
				    "<br>- Verwenden des <font color=\"#FFFFD2\">Energieschildes" +
				    "<font color=\"#D2D2D2\"> (<font color=\"#FFFFD2\">" +
				    "Roch-Klasse<font color=\"#D2D2D2\">): rechte " +
				    "Maustaste drücken und gedrückt halten" +
				    "<br>- <font color=\"#FFFFD2\">Stopp-Rakete" +
				    "<font color=\"#D2D2D2\"> abfeuern (" +
				    "<font color=\"#FFFFD2\">Orochi-Klasse" +
				    "<font color=\"#D2D2D2\">): mit gedrückt gehaltener " +
				    "rechter Maustaste eine Rakete abfeuern" +
				    "<br>- Aktivieren der <font color=\"#FFFFD2\">Plasma-" +
				    "Raketen<font color=\"#D2D2D2\"> (" +
				    "<font color=\"#FFFFD2\">Kamaitachi-Klasse" +
				    "<font color=\"#D2D2D2\">): rechte Maustaste drücken" +
				    "<br>- Auslösen einer <font color=\"#FFFFD2\">EMP-" +
				    "Schockwelle<font color=\"#D2D2D2\"> (" +
				    "<font color=\"#FFFFD2\">Pegasus-Klasse" +
				    "<font color=\"#D2D2D2\">): rechte Maustaste drücken" +
					"<br>- Aktivieren des <font color=\"#FFFFD2\">PU-" +
					"Generators<font color=\"#D2D2D2\"> (" +
					"<font color=\"#FFFFD2\">Helios-Klasse" +
					"<font color=\"#D2D2D2\">): rechte Maustaste drücken" +
				    "<br>Nach dem Landen des Helikopter wird ein Button " +
				    "sichtbar, mit dem der Spieler jederzeit in die " +
				    "<font color=\"#FFFFD2\">Werkstatt" +
				    "<font color=\"#D2D2D2\"> " +
				    "zurückkehren kann." +
				    "</font></html>" 
					);
				}
				else if(page == 5)
				{
					// PowerUps
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
							"face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"Einige Gegner verlieren nach Ihrem Abschuss eines der " +
					"6 folgenden <font color=\"#FFFFD2\">PowerUps" +
					"<font color=\"#D2D2D2\">: " +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" +
					"Geld (<font color=\"#FFFFD2\">Extra-Bonus<font color=\"#D2D2D2\">)" +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" +
					"unendlich Energie für 15 Sekunden" +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" +
					"Teil-Reparatur " +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" +
					Helicopter.INVU_DMG_REDUCTION + "% Unverwundbarkeit " +
					"für 15 Sekunden " +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" + 
					"3-fache Feuerkraft für 15 " + "Sekunden" +
					"<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
					"<font size = \"" + (HTML_SIZE-1) + "\">" +
					"erhöhte Schussrate für 15 Sekunden " +
					"</b></font></html>" 
					);
				}
				else if(page == 7)
				{
					// Hardcore-Modus
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" face=\"Dialog" +
					"\" color=\"#D2D2D2\">" +										
					"Im <font color=\"#FFFFD2\">Hardcore-Modus"
					+ "<font color=\"#D2D2D2\"> erhält der Spieler keine "
					+ "Belohnung für "
					+ "abgeschossene Gegner. Stattdessen wird in diesem "
					+ "Spielmodus jeder Levelaufstieg belohnt. Je "
					+ "erfolgreicher Sie mit den anderen Helikopter-Klassen "
					+ "gespielt haben, desto mehr Geld erhalten Sie. "
					+ "<br><br>Hintergrund: Die Weltregierung unter "
					+ "President Kranijem Su steht der Helios-Klasse sehr "
					+ "skeptisch gegenüber. 'Woher haben die Konstrukteure "
					+ "ihr Wissen über die Verwendung außerirdischer "
					+ "Technologie?' Daher wurden alle Piloten von "
					+ "Helikoptern der Helios-Klasse vom "
					+ "Reservisten-Programm ausgeschlossen."
					+ "<font color=\"#D2D2D2\">)." +
					"</font></html>"
					);
				}
			}
			else if(Events.window == HELICOPTER_TYPES)
			{
				if(page == 0)
				{				
					// Allgemein
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
					"Der Spieler hat die Wahl zwischen " + Helicopter.NR_OF_TYPES + " verschiedenen " +
					"Helikopter-Klassen. " +
					"Jede dieser 5 Klassen verfügt über eine einzigartige, " +
					"energieverbrauchende Fertigkeit " +
					"sowie über mindestens ein <font color=\"#FFFFD2\">" +
					"Spezial-Upgrade<font color=\"#D2D2D2\">, welches nur " +
					"von dieser Helikopter-Klasse " +
					"erworben werden kann. Darüber hinaus unterscheiden " +
					"sich die einzelnen Helikopter-Klassen hinsichtlich des " +
					"Preises und der Ausbaufähigkeit der einzelnen " +
					"<font color=\"#FFFFD2\">Standard-Upgrades" +
					"<font color=\"#D2D2D2\"> (siehe Abschnitt " +
					"<font color=\"#FFFFFF\">\"Finanzen/Reparatur\"" +
					"<font color=\"#D2D2D2\"> sowie <font color=\"#FFFFFF\">" +
					"\"Upgrades\"<font color=\"#D2D2D2\">). Insbesondere hat " +
					"jede Helikopter-Klasse eigene Möglichkeiten an " +
					"zusätzliche finanzielle Belohnungen, die sogenannten <font color=\"#FFFFD2\">Extra-Boni" +
					"<font color=\"#D2D2D2\">, zu kommen. Diese Extra-Boni werden für " +
					"besonders eindrucksvolle " +
					"Abwehrleistungen ausgezahlt. Näheres hierzu " +
					"finden Sie in den detaillierten " +
					"Beschreibungen zu den einzelnen Helikopter-Klassen." +
					"</font></html>"
		            );
				}				
				else if(page == 2)
				{
					// Phönix
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"Die <font color=\"#FFFFD2\">Phönix-Klasse" +
					"<font color=\"#D2D2D2\"> ist die robusteste der 5 " +
					"Helikopter-Klassen, denn für keine andere " +
					"Helikopter-Klasse können die <font color=\"#FFFFD2\">" +
					"Standard-Upgrades<font color=\"#D2D2D2\"> der " +
					"<font color=\"#FFFFD2\">Panzerung" +
					"<font color=\"#D2D2D2\"> sowie das " +
					"<font color=\"#FFFFD2\">SpezialUpgrade " +
					"\"Goliathpanzerung\"<font color=\"#D2D2D2\"> so " +
					"preiswert erworben werden. Die schwere Panzerung " +
					"bezahlt die Phönix-Klasse allerdings mit einem " +
					"schwachen <font color=\"#FFFFD2\">Hauptrotor" +
					"<font color=\"#D2D2D2\">, der sie auch zur langsamsten " +
					"der 5 verfügbaren Helikopter-Klassen macht. Durch " +
					"einen <font color=\"#FFFFD2\">Teleporter" +
					"<font color=\"#D2D2D2\">, welcher den " +
					"Helikopter an einen anderen Ort beamt und ihn für " +
					"einen kurzen Augenblick " +
					"unverwundbar macht, wird dieser Nachteil jedoch " +
					"ausgeglichen. Helikopter der " +
					"Phönix-Klasse, welche das Spezial-Upgrade " +
					"<font color=\"#FFFFD2\">\"Nahkampfbestrahlung\"" +
					"<font color=\"#D2D2D2\"> erworben haben, " +
					"überraschen ihre Gegner mit einer intensiven " +
					"kurzreichweitigen Strahlung, welche die " +
					"Außenhülle feindlicher Flugobjekte aufweicht. Die so " +
					"geschwächten Gegner werden bei " +
					"Kollisionen mit dem Helikopter schwer beschädigt, " +
					"während der Helikopter selbst weniger " +
					"Schaden nimmt als gewöhnlich. Durch Steigerung der " +
					"<font color=\"#FFFFD2\">Feuerkraft" +
					"<font color=\"#D2D2D2\"> wird gleichzeitig auch die " +
					"Intensität der Nahkampfbestrahlung vergrößert. Wenn " +
					"sich ein Helikopter der Phönix-Klasse " +
					"an einen anderen Ort teleportiert und dort mit Hilfe " +
					"der Nahmkampfbestrahlung mehrere " +
					"Gegner gleichzeitig ausschaltet, dann wird dieses " +
					"gewagte Flugmanöver mit einem " +
					"<font color=\"#FFFFD2\">Extra-Bonus" +
					"<font color=\"#D2D2D2\"> belohnt. Auch für das " +
					"unmittelbare Abschießen eines Gegners nach Nutzung des " +
					"Teleporters erhält der Spieler einen Extra-Bonus." +
					"</b></font></html>"
					);
				}				
				else if(page == 3)
				{
					// Roch
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +	
					"Für die  <font color=\"#FFFFD2\">Roch-Klasse " +
					"<font color=\"#D2D2D2\"> kann die " +
					"<font color=\"#FFFFD2\">Feuerkraft" +
					"<font color=\"#D2D2D2\"> besonders kostengünstig " +
					"gesteigert und das " +
					"<font color=\"#FFFFD2\">Spezial-Upgrade \"" +
					"Durchstoßsprengköpfe\"<font color=\"#D2D2D2\"> sehr " +
					"preiswert erworben werden. Außerdem ist " +
					"ausschließlich für die Roch-Klasse das Spezial-Upgrade " +
					"<font color=\"#FFFFD2\">\"Jumbo-Raketen\"" +
					"<font color=\"#D2D2D2\">, welches den " +
					"Raketen eine außerordentlich große Sprengkraft " +
					"verleiht, verfügbar. Somit kann " +
					"die Roch-Klasse <i>in puncto</i> Feuerkraft von keiner " +
					"anderen Helikopter-Klasse " +
					"übertroffen werden. Die <font color=\"#FFFFD2\">" +
					"Schussrate<font color=\"#D2D2D2\"> lässt allerdings " +
					"sehr zu wünschen übrig und " +
					"auch die schwache <font color=\"#FFFFD2\">Panzerung" +
					"<font color=\"#D2D2D2\"> stellt ein weiteres Manko " +
					"dieser Helikopter-Klasse dar. " +
					"Das  <font color=\"#FFFFD2\">Energie-Schild " +
					"<font color=\"#D2D2D2\"> der Roch-Klasse hilft " +
					"allerdings dabei, diesen Nachteil zu " +
					"kompensieren. Wenn es einem Helikopter der Roch-Klasse " +
					"gelingt, mehrere " +
					"Gegner mit derselben Rakete zu zerstören, dann wird " +
					"diese glorreiche Tat mit " +
					"einem  <font color=\"#FFFFD2\">Extra-Bonus" +
					"<font color=\"#D2D2D2\"> belohnt." +
					"</font></html>"
		            ); 
				}
				else if(page == 4)
				{
					// Orochi
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
					"Helikopter der <font color=\"#FFFFD2\">Orochi-Klasse" +
					"<font color=\"#D2D2D2\"> sind wahre \"Allrounder\": " +
					"Sie sind sehr schnell, verfügen " +
					"über Raketen mit sehr gutem Antrieb und sind trotzdem " +
					"ordentlich gepanzert. Zwar kann " +
					"die <font color=\"#FFFFD2\">Schussrate" +
					"<font color=\"#D2D2D2\"> dieser Helikopter-Klasse nur " +
					"begrenzt gesteigert werden, dafür kann nur " +
					"für die Orochi-Klasse eine <font color=\"#FFFFD2\">" +
					"dritte Bordkanone<font color=\"#D2D2D2\"> sowie eine " +
					"<font color=\"#FFFFD2\">Radar-Vorrichtung" +
					"<font color=\"#D2D2D2\"> installiert " +
					"werden. Mit letzterer können getarnte Gegner aufgespürt " +
					"werden. Bei ausreichend Energie " +
					"kann diese Helikopter-Klasse außerdem " +
					"<font color=\"#FFFFD2\">Stopp-Raketen" +
					"<font color=\"#D2D2D2\"> abfeuern, welche getroffene " +
					"Gegner zurück stoßen und für einen kurzen Augenblick " +
					"kampfunfähig machen. Durch Steigerung des " +
					"<font color=\"#FFFFD2\">Raketenantriebs" +
					"<font color=\"#D2D2D2\"> kann die Rückstoßwirkung " +
					"weiter erhöht werden. Wenn die " +
					"Orochi-Klasse mehrere Raketen gleichzeitig abfeuert " +
					"und mindestens zwei von diesen " +
					"jeweils ein oder mehrere feindliche Flugobjekte " +
					"ausschalten, dann wird dies mit einem " +
					"<font color=\"#FFFFD2\">Extra-Bonus" +
					"<font color=\"#D2D2D2\"> belohnt." + 
					"</font></html>"
					); 
				}
				else if(page == 5)
				{
					// Kamaitachi				
					my_label.setText(
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +	
					"Die reine Feuerkraft der <font color=\"#FFFFD2\">" +
					"Kamaitachi-Klasse<font color=\"#D2D2D2\"> ist sehr " +
					"gering. Dank der preiswerten " +
					"Upgrades auf die <font color=\"#FFFFD2\">Schussrate" +
					"<font color=\"#D2D2D2\"> sowie des nur für die " +
					"Kamaitachi-Klasse verfügbaren " +
					"<font color=\"#FFFFD2\">Spezial-Upgrades " +
					"\"Schnellfeuer\"<font color=\"#D2D2D2\"> erreicht diese " +
					"Helikopter-Klasse allerdings eine enorm " +
					"hohe Schussrate. Im Zusammenspiel mit den " +
					"<font color=\"#FFFFD2\">Plasma-Raketen" +
					"<font color=\"#D2D2D2\"> der Kamaitachi-Klasse, " +
					"welche die Feuerkraft für 15 Sekunden erheblich erhöht, " +
					"führt dies zu einer " +
					"gewaltigen Schadenswirkung pro Sekunde. Da die " +
					"Aktivierung der Plasma-Raketen " +
					"allerdings Energie verbraucht, können sie nicht " +
					"permanent eingesetzt werden. Der " +
					"Zeitpunkt ihrer Aktivierung muss daher wohl überlegt " +
					"sein. Wenn es dem " +
					"Kamaitachi-Piloten gelingt, innerhalb sehr kurzer Zeit " +
					"eine große Anzahl feindlicher " +
					"Flugobjekte abzuschießen, dann wird diese Leistung mit " +
					"einem großzügigen <font color=\"#FFFFD2\">Extra-Bonus" +
					"<font color=\"#D2D2D2\"> " +
					"belohnt." +
					"</font></html>"
		            ); 
				}
				else if(page == 6)
				{
					// Pegasus
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE-1) + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\"><b>" +
					"Helikopter der <font color=\"#FFFFD2\">Pegasus-Klasse" +
					"<font color=\"#D2D2D2\"> wären stark benachteiligt, " +
					"wenn sie sich allein auf ihre " +
					"Raketen verlassen müssten. Dank ihres mächtigen " +
					"<font color=\"#FFFFD2\">EMP-Generators" +
					"<font color=\"#D2D2D2\"> zur Aussendung " +
					"elektromagnetischer Schockwellen, welche alle " +
					"feindlichen Flugobjekte im Umkreis schwer " +
					"beschädigen und für einen kurzen Augenblick außer " +
					"Gefecht setzen, steht diese " +
					"Helikopter-Klasse den anderen allerdings in nichts " +
					"nach. Durch Verbesserung des EMP-Generators kann die " +
					"Reichweite sowie die Schadenswirkung der EMP-" +
					"Schockwellen gesteigert werden. Wenn mehrere Gegner " +
					"durch dieselbe Schockwelle vernichtet werden, " +
					"dann wird dies mit einem <font color=\"#FFFFD2\">Extra-" +
					"Bonus<font color=\"#D2D2D2\"> belohnt. Eine weitere " +
					"Besonderheit der Pegasus-Klasse ist ihr " +
					"<font color=\"#FFFFD2\">Interphasengenerator" +
					"<font color=\"#D2D2D2\">. Dieser ermöglicht es dem " +
					"Helikopter partiell in eine andere Dimension zu " +
					"gelangen, sobald er eine zeitlang keine Raketen " +
					"abgefeuert hat. In diesem Zustand zwischen zwei " +
					"Dimensionen ist der Helikopter gegenüber jeglichen " +
					"Angriffen geschützt. Durch Steigerung der " +
					"<font color=\"#FFFFD2\">Schussrate" +
					"<font color=\"#D2D2D2\"> wird ebenfalls die für einen " +
					"Phasensprung nötige Zeit verkürzt. Raketen, die während " +
					"eines Phasensprungs " +
					"abgeschossen werden, befinden sich in einem " +
					"intermediären Zustand zwischen zwei " +
					"Dimensionen und können daher die feindliche Panzerung " +
					"leicht durchdringen und besonders " +
					"schweren Schaden beim Gegner anrichten. Nach Abschuss " +
					"einer Rakete füllt der Helikopter allerdings wieder in " +
					"seinen Normalzustand zurück." +
					"</b></font></html>"
		            ); 
				}	
				else if(page == 7)
				{
					// Helios
					my_label.setText(
					"<html><font size = \"" + (HTML_SIZE) + "\" " +										
					"face=\"Dialog\" color=\"#D2D2D2\">" +					
					"Mit dem Ziel eine besonders schlagfertige Helikopter-"
					+ "Klasse zu erschaffen, haben die Konstrukteure der "
					+ "<font color=\"#FFFFD2\">Helios-Klasse<font color=\"#D2D2D2\"> ihre Upgrade-Erfahrung mit allen anderen "
					+ "Helikopter-Klassen genutzt. Je erfolgreicher Sie also "
					+ "mit den anderen Helikopter-Klassen gespielt haben, "
					+ "desto geringer fallen die <font color=\"#FFFFD2\">Upgrade-Kosten<font color=\"#D2D2D2\"> für die "
					+ "Helios-Klasse aus. Die Konstrukteure "
					+ "haben nicht einmal davor Halt gemacht, außerirdische "
					+ "Technologie aus abgestürzten Flugobjekten zu verbauen: "
					+ "Ein <font color=\"#FFFFD2\">PowerUp-Stopper<font color=\"#D2D2D2\"> hilft dabei, die Bewegung von "
					+ "PowerUps zu kontrollieren, was ihr Einsammeln "
					+ "erleichtert. Unter großem Energieaufwand können diese "
					+ "Helikopter außerdem einen <font color=\"#FFFFD2\">PU-Generator<font color=\"#D2D2D2\"> zur Erzeugung "
					+ "von PowerUps nutzen. Die Helios-Klasse lässt sich nur "
					+ "im <font color=\"#FFFFD2\">Hardcore-Modus<font color=\"#D2D2D2\"> spielen." 
					+ "</font></html>"
		            ); 
				}
			}			
			else if(Events.window == CONTACT)
			{
				if(page == 0)
				{
					my_label.setFont(PLAIN18);
					my_label.setText(	
					"<html><font size = \"" + HTML_SIZE + "\" " +
					"face=\"Dialog\" color=\"#D2D2D2\">" +
				    "Du hast neue Ideen oder Verbesserungsvorschläge für " +
				    "HelikopterDefence?<br> Dann schreibe eine E-Mail an: " +
					"<font color=\"#FFFFD2\">info@HelicopterDefence.de" +
					"<font color=\"#D2D2D2\"> " +
					"<br><br>Gerne nehme ich auch Übersetzunghilfen an, " +
					"falls du deine Muttersprache " +
					"in der Sprachauswahl vermisst und behilflich sein " +
					"möchtest. <br><br>Ich freue mich darauf, von dir zu " +
					"hören. <br><br>Viele Grüße" +
					"<br>Björn Hansen" +
					"</font></html>"
				    );
				}
			}
		}
	}

	private static String activation_state(boolean on)
	{		
		return on ? (language == ENGLISH ? "on" : "an"):(language == ENGLISH ? "off" : "aus");
	}
	
	public static void adapt_to_new_window(boolean just_entered)
	{
		page = 0;
		if(Events.window != HIGHSCORE && Events.window != SETTINGS){my_label.setVisible(true);}
		update_labeltext();
		cross_timer = 0;
		message_timer = 0;
		if(just_entered){stop_button_highlighting(startscreen_button);}
	}
	
	static void stop_button_highlighting(HashMap<String, Button> button)
	{
		if(!highlighted_button.equals(""))
		{
			button.get(highlighted_button).highlighted = false;
			highlighted_button = "";
		}
	}

	static void update_collected_powerUps(Helicopter helicopter, PowerUp powerUp)
	{
		helicopter.powerUp_timer[powerUp.type] = Math.max(helicopter.powerUp_timer[powerUp.type], Helicopter.POWERUP_DURATION);
		if(collected_PowerUp[powerUp.type] == null){powerUp.moveToStatusbar();}
		else
		{
			collected_PowerUp[powerUp.type].surface = MyColor.setAlpha(collected_PowerUp[powerUp.type].surface, 255);	
			collected_PowerUp[powerUp.type].cross = MyColor.setAlpha(collected_PowerUp[powerUp.type].cross, 255);
		}
	}

	public static void update_repairShopButtons(Helicopter helicopter)
	{
		repair_shop_button.get("Einsatz").label = Button.MISSION[language][Events.timeOfDay];
		repair_shop_button.get("Einsatz").second_label = Button.SOLD[language][helicopter.spotlight ? 1 : 0];			
		if(helicopter.spotlight)
		{
			repair_shop_button.get("Special" + 0).costs = 0;
		}
		if(helicopter.goliath_plating == 2)
		{
			repair_shop_button.get("Special" + 1).costs = 0;
		}
		if(helicopter.has_piercing_warheads)
		{
			repair_shop_button.get("Special" + 2).costs = 0;
		}
		if(helicopter.nr_of_cannons  != 1)
		{
			if(helicopter.hasAllCannons())
	    	{
	    		repair_shop_button.get("Special" + 3).costs = 0;	
	    		if(helicopter.nr_of_cannons == 3)
	    		{
	    			repair_shop_button.get("Special" + 3).label = THIRD_CANNON[language];
	    		}
	    	}
	    	else
	    	{
	    		repair_shop_button.get("Special" + 3).costs = 125000;
	    		repair_shop_button.get("Special" + 3).label = THIRD_CANNON[language];
	    	}
		}
		if(helicopter.has_5th_special())
		{
			repair_shop_button.get("Special" + 4).costs = 0;
		}	
		for(int i = 0; i < 6; i++)
		{    		
			if(helicopter.has_max_upgrade_level[i])
			{
				repair_shop_button.get("StandardUpgrade" + i).costs = 0;
			}
			else
			{
				repair_shop_button.get("StandardUpgrade" + i).costs 
					= MyMath.costs(helicopter.type,
								   helicopter.upgrade_costs[i], 
								   helicopter.level_of_upgrade[i]);
			}    				    		
		}
		repair_shop_button.get("RepairButton").costs = 0;
	}
	
	public static void unlock(HelicopterTypes heli_type)
	{
		unlocked_type = heli_type;
		unlocked_timer = UNLOCKED_DISPLAY_TIME;	
	}

	public static void paint(Graphics2D g2d, Controller hd, Helicopter helicopter)
	{
		if(Events.window == GAME)
		{
			paint_foreground_displays(g2d, hd, helicopter, hd.show_fps);	
			paint_GUI(g2d, helicopter);		
		}			
		else if(Events.window == REPAIR_SHOP)
		{				
			paint_repair_shop(g2d, helicopter);
		}		 
		else if(Events.window == STARTSCREEN)
		{				
			paint_startscreen(g2d, helicopter);
		}
		else if(Events.window == SCORESCREEN)
		{				
			paint_scorescreen(g2d, helicopter);
		}		
		else
		{				
			update_and_paint_startscreen_menu(g2d, helicopter, hd.frames_counter);
		}		
	}
	
	static void repaint_bg(Graphics g, Controller hd)
	{
		if(hd.bg_repaint > 1){hd.bg_repaint = DISABLED;}
		else hd.bg_repaint++;		
		g.setColor(Color.black);
		g.fillRect(0,
				   0,
				   Main.dm_current.getWidth(),
				   Main.dm_current.getHeight());
	}

	public static void update(Controller hd, Helicopter helicopter)
	{
		if(Events.window == REPAIR_SHOP)
		{			
			update_repair_shop(helicopter);
		}		 
		else if(Events.window == STARTSCREEN)
		{						
			update_startscreen(helicopter, hd.frames_counter);
		}
		else if(Events.window == SCORESCREEN)
		{
			update_scorescreen(helicopter);
		}		
		else
		{				
			identify_highlighted_buttons(helicopter, startscreen_menu_button);
		}		
	}

	public static void adapt_to_window_mode(Dimension display_shift)
	{
		//startscreen_menu_button.get("5").enabled = false;
		my_label.setBounds(display_shift.width  + 42, 
  						   display_shift.height + 83, 940, 240);
	}
} 

class MyLabel extends JTextPane
{
	private static final long serialVersionUID = 1L;
			
	MyLabel()
	{			
		setBounds(Main.display_shift.width  + 42, 
				  Main.display_shift.height + 83, 940, 240);
		setEditable(false);
		setEnabled(true);
		setBackground(Color.black);
		setContentType("text/html");
		setOpaque(true);
		setVisible(false);
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
								Controller.antialiasing ?
								RenderingHints.VALUE_ANTIALIAS_ON : 
								RenderingHints.VALUE_ANTIALIAS_OFF);
		super.paintComponent(g2d);	
	}
}