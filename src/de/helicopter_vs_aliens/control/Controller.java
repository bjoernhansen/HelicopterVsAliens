package de.helicopter_vs_aliens.control;

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

import de.helicopter_vs_aliens.Constants;
import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.HelicopterFactory;
import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.MyColor;

import static de.helicopter_vs_aliens.gui.WindowTypes.GAME;

public class Controller extends JPanel implements Runnable, KeyListener,
									   MouseListener, MouseMotionListener, 
									   WindowListener, Constants
{
	private static final long 
		serialVersionUID = 5775063502338544548L, 
		DELAY = 16;
	
	static int
		skipFrameFlag = DISABLED;
		
	static long	
		tm;
	
	public static boolean
		antialiasing = true;
		
	private static Controller
		controller = new Controller();
		
	public static Savegame
		savegame;
		
	int numberOfFrames;

	public int
		framesCounter = 0,
		backgroundRepaint = 0;
	
	long
		fpsStartTime;
	
	public boolean
		showFps = false;

	public boolean
		mouseInWindow = true;
		
	private Helicopter
		helicopter = HelicopterFactory.create(HelicopterTypes.getDefault());
	
	public ArrayList<LinkedList<Enemy>>
		enemies = new ArrayList<>(3);
	public ArrayList<LinkedList<Missile>>
		missile = new ArrayList<>(2);	
	public ArrayList<LinkedList<Explosion>>
		explosion =    new ArrayList<>(2);	
	public ArrayList<LinkedList<EnemyMissile>>
		enemyMissile = new ArrayList<>(2);	
	public ArrayList<LinkedList<BackgroundObject>>
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
	
	public void init()
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
						
		add(Menu.label);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);		
		Menu.initializeMenu(this.helicopter);
		Menu.updateButtonLabels(this.helicopter);
		this.initializeLists();
		BackgroundObject.initialize(this.bgObject);
	}
	
	void initializeLists()
	{
		for(int i = 0; i < 2; i++)
		{
			this.missile.add(	   i, new LinkedList<>());
			this.explosion.add(	   i, new LinkedList<>());
			this.bgObject.add(	   i, new LinkedList<>());
			this.enemyMissile.add( i, new LinkedList<>());
			this.powerUp.add(	   i, new LinkedList<>());
			this.enemies.add(		   i, new LinkedList<>());
		}		
		this.enemies.add(DESTROYED, new LinkedList<>());
	}
	
	public void start()
	{
		this.animator = new Thread(this);
		this.animator.start();
		Audio.refreshBackgroundMusic();
	}

	public static void shutDown(){System.exit(0);}
	
	
	@Override
	public void run()
	{
		if(skipFrameFlag != ACTIVE){tm = System.currentTimeMillis();}
		while(Thread.currentThread() == this.animator)
		{			
			repaint();
			try				
			{
				tm += DELAY;
				long pauseTime = tm - System.currentTimeMillis();
				if(pauseTime < 1)
				{
					skipFrameFlag = ACTIVE;
				}
				else{Thread.sleep(pauseTime);}
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
			if(skipFrameFlag == INACTIVE){
				skipFrameFlag = DISABLED;}
			else
			{					
				if(this.backgroundRepaint != DISABLED){Menu.repaintBackground(g, this);}
				g.drawImage(this.offImage, 
							Main.displayShift.width, // 0
							Main.displayShift.height, // 0
							null);
			}
			
			if(Main.isFullScreen || this.mouseInWindow){updateGame();}
			
			if(skipFrameFlag == ACTIVE)
			{
				//skipped_counter++;
				skipFrameFlag = INACTIVE;
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
		this.framesCounter++;
		if(Events.window == GAME)
		{			
			calculateFps();
			
			// aktualisieren aller sichtbaren Objekte
			if(!Menu.isMenueVisible)
			{				
		    	MyColor.calculateVariableGameColors(this.framesCounter);
				BackgroundObject.update(this, this.bgObject);
				Events.updateTimer();
				Menu.updateDisplays(this.helicopter);
				Enemy.updateAllDestroyed(this, this.helicopter);
				Missile.updateAll(this, this.helicopter);
				Enemy.updateAllActive(this, this.helicopter);
				EnemyMissile.updateAll(this.enemyMissile, this.helicopter);
				Events.checkForLevelup(this, this.helicopter);
				Enemy.generateNewEnemies(this.enemies, this.helicopter);
				this.helicopter.update(this.missile, this.explosion);
				Explosion.updateAll(this.helicopter, this.explosion);
				PowerUp.updateAll(this.powerUp, this.helicopter);
			}			
		}
		else
		{
			MyColor.calculateVariableMenuColors(this.framesCounter);
			Menu.update(this, this.helicopter);
		}
	}
	
	private void paintFrame(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;		
		
		if(Events.window == GAME)
		{						
			// zeichnen aller sichtbaren Objekte						
			BackgroundObject.paintBackground(g2d, this.bgObject);
			Menu.paintBackgroundDisplays( g2d, this, this.helicopter);
			if(Enemy.currentRock != null){Enemy.currentRock.paint(g2d, this.helicopter);}
			Enemy.paintAllDestroyed(g2d, this, this.helicopter);
			Missile.paintAllMissiles( g2d, this);
			Enemy.paintAllActive(g2d, this, this.helicopter);
			EnemyMissile.paintAll(g2d, this.enemyMissile);
			this.helicopter.paint(g2d, Events.timeOfDay);			
			Explosion.paintAll(g2d, this.explosion);
			PowerUp.paintAll(g2d, this.powerUp);
			BackgroundObject.paintForeground(g2d, this.bgObject);
		}
		Menu.paint(g2d, this, this.helicopter);
	}
	
	private void calculateFps()
	{
		if(this.showFps)
		{
			long timeDiff = System.currentTimeMillis() - this.fpsStartTime;
			this.numberOfFrames++;
			if(timeDiff > 3000)
			{
				Menu.fps = Math.round(1000f*this.numberOfFrames /timeDiff);
				this.fpsStartTime = System.currentTimeMillis();
				this.numberOfFrames = 0;
			}
		}
	}	
	
	public void switchFpsVisibleState()
	{
		if(Events.window == GAME)
		{
			if(this.showFps)
			{
				this.showFps = false;
			}
			else
			{					
				this.showFps = true;
				this.fpsStartTime = System.currentTimeMillis();
				this.numberOfFrames = 0;
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
		this.backgroundRepaint = READY;
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