package de.helicopter_vs_aliens;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JPanel;

import de.helicopter_vs_aliens.helicopter.Helicopter;
import de.helicopter_vs_aliens.enemy.Enemy;
import de.helicopter_vs_aliens.helicopter.HelicopterFactory;
import de.helicopter_vs_aliens.helicopter.HelicopterTypes;
import de.helicopter_vs_aliens.helicopter.Phoenix;

public class Controller extends JPanel implements Runnable, KeyListener,
									   MouseListener, MouseMotionListener, 
									   WindowListener, Constants
{
	private static final long 
		serialVersionUID = 5775063502338544548L, 
		DELAY = 16;
	
	static int
		skip_frame_flag = DISABLED;
		
	static long	
		tm;
	
	static boolean 
		antialiasing = true;
	
		
	private static Controller
		controller = new Controller();
		
	static Savegame 
		savegame;
		
	int nr_of_frames,		
		frames_counter = 0,
		bg_repaint = 0;
	
	long 
		fps_start_time;
	
	boolean 
		show_fps = false,
		mouse_in_window = true;
		
	private Helicopter
		helicopter = new Helicopter();
	
	public ArrayList<LinkedList<Enemy>> 		
		enemy = new ArrayList<>(3);	
	public ArrayList<LinkedList<Missile>> 		
		missile = new ArrayList<>(2);	
	public ArrayList<LinkedList<Explosion>> 	
		explosion =    new ArrayList<>(2);	
	public ArrayList<LinkedList<EnemyMissile>> 
		enemyMissile = new ArrayList<>(2);	
	public ArrayList<LinkedList<BgObject>> 	
		bgObject = 	   new ArrayList<>(2);	
	public ArrayList<LinkedList<PowerUp>> 		
		powerUp = 	   new ArrayList<>(2);
		
	Thread animator;
	Image offImage;
	Graphics2D offGraphics;
	
	private Dimension offDimension;
	private Shape offscreenClip;
	private Rectangle2D wholeScreenClip;

	
	private Controller(){}
	
	void init()
	{					
		Audio.initialize();
		
		this.offscreenClip = new Rectangle2D.Double(0, 
													0, 
													Main.VIRTUAL_DIMENSION.getWidth(),
													Main.VIRTUAL_DIMENSION.getHeight());	
		this.offDimension = getSize();
		this.wholeScreenClip = new Rectangle2D.Double(0, 0, this.offDimension.getWidth(), this.offDimension.getHeight());				
		this.offImage = createImage((int)this.offDimension.getWidth(), (int)this.offDimension.getHeight());
		this.offGraphics = (Graphics2D) this.offImage.getGraphics();		
		this.offGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);		
		this.offGraphics.fill(this.wholeScreenClip);
		this.offGraphics.setClip(this.offscreenClip);	
						
		add(Menu.my_label);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);		
		Menu.initialize_menu(this.helicopter);		
		Menu.update_button_labels(this.helicopter);		
		this.initialize_lists();		
		BgObject.initialize(this.bgObject);
	}
	
	void initialize_lists()
	{
		for(int i = 0; i < 2; i++)
		{
			this.missile.add(	   i, new LinkedList<Missile>());				
			this.explosion.add(	   i, new LinkedList<Explosion>());		
			this.bgObject.add(	   i, new LinkedList<BgObject>());		
			this.enemyMissile.add( i, new LinkedList<EnemyMissile>());
			this.powerUp.add(	   i, new LinkedList<PowerUp>());
			this.enemy.add(		   i, new LinkedList<Enemy>());
		}		
		this.enemy.add(DESTROYED, new LinkedList<Enemy>());
	}
	
	void start()
	{
		this.animator = new Thread(this);
		this.animator.start();
		Audio.refresh_bg_music();	
	}

	static void shut_down(){System.exit(0);}
	
	
	@Override
	public void run()
	{
		if(skip_frame_flag != ACTIVE){tm = System.currentTimeMillis();}
		while(Thread.currentThread() == this.animator)
		{			
			repaint();
			try				
			{
				tm += DELAY;
				long pause_time = tm - System.currentTimeMillis();
				if(pause_time < 1)
				{
					skip_frame_flag = ACTIVE;
				}
				else{Thread.sleep(pause_time);}				
			}
			catch(InterruptedException e)
			{
				e.printStackTrace(); 
				break;
			}						
		}
	}		

	// Berechnen und Zeichnen des Spiels
	@Override
	protected void paintComponent(Graphics g)
	{		
		if(this.offGraphics != null)
		{			
			if(skip_frame_flag == INACTIVE){skip_frame_flag = DISABLED;}
			else
			{					
				if(this.bg_repaint != DISABLED){Menu.repaint_bg(g, this);}
				g.drawImage(this.offImage, 
							Main.display_shift.width, // 0
							Main.display_shift.height, // 0
							null);
			}
			
			if(Main.isFullScreen || this.mouse_in_window){updateGame();}
			
			if(skip_frame_flag == ACTIVE)
			{
				//skipped_counter++;
				skip_frame_flag = INACTIVE;
			}
			else
			{				
				//unskipped_counter++;
				this.offGraphics.setColor(MyColor.bg);				
				this.offGraphics.fill(this.wholeScreenClip);	
				paintFrame(this.offGraphics);
			}			
		}
	}
	
	private void updateGame()
	{		
		this.frames_counter++;
		if(Events.window == GAME)
		{			
			calculate_fps();
			
			// aktualisieren aller sichtbaren Objekte
			if(!Menu.menue_visible)
			{				
		    	MyColor.calculate_variable_game_colors(this.frames_counter);
				BgObject.update(this, this.bgObject);
				Events.update_timer();
				Menu.update_displays(this.helicopter);				
				Enemy.update_all_destroyed(this, this.helicopter);
				Missile.update_all(this, this.helicopter);
				Enemy.update_all_active(this, this.helicopter);
				EnemyMissile.update_all(this.enemyMissile, this.helicopter);
				Events.check_for_levelup(this, this.helicopter);
				Enemy.generate_new_enemies(this.enemy, this.helicopter);
				this.helicopter.update(this.missile, this.explosion);
				Explosion.update_all(this.helicopter, this.explosion);
				PowerUp.update_all(this.powerUp, this.helicopter);
			}			
		}
		else
		{
			MyColor.calculate_variable_menu_colors(this.frames_counter);
			Menu.update(this, this.helicopter);
		}
	}
	
	private void paintFrame(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;		
		
		if(Events.window == GAME)
		{						
			// zeichnen aller sichtbaren Objekte						
			BgObject.paint_background(g2d, this.bgObject);			
			Menu.paint_background_displays( g2d, this, this.helicopter);
			if(Enemy.current_rock != null){Enemy.current_rock.paint(g2d, this.helicopter);}
			Enemy.paint_all_destroyed(g2d, this, this.helicopter);			
			Missile.paint_all_missiles( g2d, this);			
			Enemy.paint_all_active(g2d, this, this.helicopter);			
			EnemyMissile.paint_all(g2d, this.enemyMissile);			
			this.helicopter.paint(g2d, Events.timeOfDay);			
			Explosion.paint_all(g2d, this.explosion);			
			PowerUp.paint_all(g2d, this.powerUp);			
			BgObject.paintForeground(g2d, this.bgObject);
		}
		Menu.paint(g2d, this, this.helicopter);
	}
	
	private void calculate_fps()
	{
		if(this.show_fps)
		{
			long time_diff = System.currentTimeMillis() - this.fps_start_time;
			this.nr_of_frames++;
			if(time_diff > 3000)
			{
				Menu.fps = Math.round(1000f*this.nr_of_frames/time_diff);					
				this.fps_start_time = System.currentTimeMillis();
				this.nr_of_frames = 0;
			}
		}
	}	
	
	public void switch_FPS_visible_state()
	{
		if(Events.window == GAME)
		{
			if(this.show_fps)
			{
				this.show_fps = false;
			}
			else
			{					
				this.show_fps = true;
				this.fps_start_time = System.currentTimeMillis();					
				this.nr_of_frames = 0;
			}
		}		 
	}
	
	// Behandlung von Fenster-, Tastatur- und Mausereignisse
	@Override
	public void keyPressed	 (KeyEvent   e){Events.keyTyped(e, this, this.helicopter, savegame);}

	@Override
	public void mousePressed (MouseEvent e){Events.mousePressed(e, this, this.helicopter);}

	@Override
	public void mouseReleased(MouseEvent e){Events.mouseReleased(e, this.helicopter);}

	@Override
	public void mouseDragged (MouseEvent e){Events.mouseMovedOrDragged(e, this.helicopter);}	

	@Override
	public void mouseMoved   (MouseEvent e){Events.mouseMovedOrDragged(e, this.helicopter);}
		
	@Override
	public void mouseEntered(MouseEvent e)
	{		
		/*if(Events.window == GAME)
		{
			this.mouse_in_window = true;
			Events.time_aktu = System.currentTimeMillis();
		}*/
		this.bg_repaint = READY;
	}	
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		/*if(Events.window == GAME)
		{
			this.mouse_in_window = false;
			Events.playing_time += System.currentTimeMillis() - Events.time_aktu;			
		}
		*/
	}
	
	// ungenutzte Listener-Methoden	
	@Override
	public void keyTyped(KeyEvent e){}

	@Override
	public void keyReleased(KeyEvent e){}
	
	@Override
	public void windowDeiconified(WindowEvent e){}
	
	@Override
	public void mouseClicked(MouseEvent e){}
	
	@Override
	public void windowOpened(WindowEvent e){}
	
	@Override
	public void windowClosing(WindowEvent e){}

	@Override
	public void windowClosed(WindowEvent e){}

	@Override
	public void windowIconified(WindowEvent e){}

	@Override
	public void windowActivated(WindowEvent e){}

	@Override
	public void windowDeactivated(WindowEvent e){}
	
	public Helicopter getHelicopter()
	{
		return helicopter;
	}
	
	public void setHelicopter(Helicopter helicopter)
	{
		this.helicopter = helicopter;
	}
	
	public static Controller getInstance()
	{
		return controller;
	}
}