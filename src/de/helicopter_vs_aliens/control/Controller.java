package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.entities.GameEntityRecycler;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.Colorations;

import javax.swing.JPanel;
import java.awt.Color;
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
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.DESTROYED;
import static de.helicopter_vs_aliens.gui.WindowType.GAME;


public final class Controller extends JPanel implements Runnable, KeyListener,
									   MouseListener, MouseMotionListener, 
									   WindowListener
{
	// TODO variablen sortieren (public - private, statisch ...
	// TODO und mehr über getter beziehen nicht so viel publik (und nicht so viel statisch)
	
	private static final boolean
		STOP_GAME_WHEN_MOUSE_OUTSIDE_WINDOW = true;
	
	private static final int
		BACKGROUND_PAINT_DISABLED = -1;

	private static final long
		serialVersionUID = 5775063502338544548L, 
		DELAY = 16;

	private FrameSkipStatusType
		frameSkipStatus = FrameSkipStatusType.DISABLED;
		
	static long	
		tm;
	
	public static boolean
		antialiasing = true;
		
	private static final Controller
		controller = new Controller();
		
	private Savegame
		saveGame;
		
	private int
		numberOfFrames;

	public int
		framesCounter = 0,
		backgroundRepaintTimer = 0;
	
	private long
		fpsStartTime;
	
	public boolean
		showFps = false;

	public boolean
		mouseInWindow = true;
		
	private Helicopter
		helicopter = HelicopterType.getDefault().makeInstance();

	public EnumMap<CollectionSubgroupType, LinkedList<Enemy>>
		enemies = new EnumMap<>(CollectionSubgroupType.class);
	public EnumMap<CollectionSubgroupType, LinkedList<Missile>>
		missiles = new EnumMap<>(CollectionSubgroupType.class);
	public EnumMap<CollectionSubgroupType, LinkedList<Explosion>>
		explosions = new EnumMap<>(CollectionSubgroupType.class);
	public EnumMap<CollectionSubgroupType, LinkedList<EnemyMissile>>
		enemyMissiles = new EnumMap<>(CollectionSubgroupType.class);
	public EnumMap<CollectionSubgroupType, LinkedList<PowerUp>>
		powerUps = new EnumMap<>(CollectionSubgroupType.class);

	Graphics2D offGraphics;

	private Thread
		animator;
	
	private Image
		offImage;
	
	private Rectangle2D
		wholeScreenClip;
	
	private final Scenery
		scenery = new Scenery();
		
	private final WindowManager
		windowManager = new WindowManager();
	
	private final GameEntityRecycler
		gameEntityRecycler = new GameEntityRecycler();
	
	
	private Controller(){}
	
	public void init()
	{					
		Audio.initialize();
		
		Dimension offDimension = getSize();
		this.wholeScreenClip = new Rectangle2D.Double(0, 0, offDimension.getWidth(), offDimension.getHeight());
		this.offImage = createImage((int) offDimension.getWidth(), (int) offDimension.getHeight());
		this.offGraphics = (Graphics2D) this.offImage.getGraphics();		
		this.offGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);		
		this.offGraphics.fill(this.wholeScreenClip);
		
		Shape offscreenClip = new Rectangle2D.Double(0,
													 0,
													 Main.VIRTUAL_DIMENSION.getWidth(),
													 Main.VIRTUAL_DIMENSION.getHeight());
		this.offGraphics.setClip(offscreenClip);
						
		add(Window.label);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);		
		Window.initialize();
		Window.updateButtonLabels(helicopter);
		initializeLists();
		scenery.createInitialSceneryObjects();
	}
	
	void initializeLists()
	{
		// TODO alle Listen von inaktivierten überführen in GameEntityRecycler
		// TODO die Verwaltung der Listen für aktive in eine eigene Klasse überführen
		CollectionSubgroupType.getStandardSubgroupTypes().forEach(standardSubgroupTypes -> {
			this.missiles.put(	   		standardSubgroupTypes, new LinkedList<>());
			this.explosions.put(	   	standardSubgroupTypes, new LinkedList<>());
			this.scenery.getSceneryObjects().put(	standardSubgroupTypes, new LinkedList<>());
			this.enemyMissiles.put( 	standardSubgroupTypes, new LinkedList<>());
			this.powerUps.put(	   		standardSubgroupTypes, new LinkedList<>());
			this.enemies.put(		   	standardSubgroupTypes, new LinkedList<>());
		});
		this.enemies.put(DESTROYED, new LinkedList<>());
	}
	
	public void start()
	{
		this.animator = new Thread(this);
		this.animator.start();
		Audio.refreshBackgroundMusic();
	}

	public void shutDown()
	{
		System.exit(0);
	}
	
	@Override
	public void run()
	{
		if(this.frameSkipStatus != FrameSkipStatusType.ACTIVE){tm = System.currentTimeMillis();}
		while(Thread.currentThread() == this.animator)
		{			
			repaint();
			try				
			{
				tm += DELAY;
				long pauseTime = tm - System.currentTimeMillis();
				if(pauseTime < 1)
				{
					this.frameSkipStatus = FrameSkipStatusType.ACTIVE;
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
		Graphics2D g2d = (Graphics2D) g;
		if(this.offGraphics != null)
		{			
			if(this.frameSkipStatus == FrameSkipStatusType.INACTIVE){
				this.frameSkipStatus = FrameSkipStatusType.DISABLED;}
			else
			{					
				if(this.backgroundRepaintTimer != BACKGROUND_PAINT_DISABLED){this.repaintBackground(g2d);}
				g.drawImage(this.offImage, 
							Main.displayShift.width, // 0
							Main.displayShift.height, // 0
							null);
			}
			
			if(Main.isFullScreen || this.mouseInWindow){updateGame();}
			
			if(this.frameSkipStatus == FrameSkipStatusType.ACTIVE)
			{
				this.frameSkipStatus = FrameSkipStatusType.INACTIVE;
			}
			else
			{
				this.offGraphics.setColor(Colorations.bg);
				this.offGraphics.fill(this.wholeScreenClip);	
				paintFrame(this.offGraphics);
			}			
		}
	}
	
	private void repaintBackground(Graphics2D g2d)
	{
		if(backgroundRepaintTimer > 1){backgroundRepaintTimer = Timer.DISABLED;}
		else backgroundRepaintTimer++;
		g2d.setColor(Color.black);
		g2d.fillRect(	0,
			0,
			Main.currentDisplayMode.getWidth(),
			Main.currentDisplayMode.getHeight());
	}
	
	private void updateGame()
	{		
		this.framesCounter++;
		Timer.countDownActiveTimers();
		if(WindowManager.window ==  GAME)
		{			
			calculateFps();
			if(!Window.isMenuVisible)
			{				
		    	Colorations.calculateVariableGameColors(this.framesCounter);
				
				scenery.update(controller);
				Events.updateTimer();
				Window.updateDisplays(this.helicopter);
				Enemy.updateAllDestroyed(this, this.helicopter);
				Missile.updateAll(this, this.helicopter);
				Enemy.updateAllActive(this, this.helicopter);
				EnemyMissile.updateAll(this.enemyMissiles, this.helicopter);
				Events.checkForLevelUp(this);
				Enemy.generateNewEnemies(this.enemies, this.helicopter);
				this.helicopter.update(this.missiles, this.explosions);
				Explosion.updateAll(this.helicopter, this.explosions);
				PowerUp.updateAll(this.powerUps, this.helicopter);
			}			
		}
		else
		{
			Colorations.calculateVariableMenuColors(this.framesCounter);
			Window.update(this, this.helicopter);
		}
	}
	
	private void paintFrame(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		GraphicsManager.getInstance().setGraphics2D(g2d);
		windowManager.paintWindow(g2d);
	}
	
	private void calculateFps()
	{
		if(this.showFps)
		{
			long timeDiff = System.currentTimeMillis() - this.fpsStartTime;
			this.numberOfFrames++;
			if(timeDiff > 3000)
			{
				Window.fps = Math.round(1000f*this.numberOfFrames /timeDiff);
				this.fpsStartTime = System.currentTimeMillis();
				this.numberOfFrames = 0;
			}
		}
	}	
	
	public void switchFpsVisibleState()
	{
		if(WindowManager.window ==  GAME)
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
	public void keyPressed	 (KeyEvent   e){Events.keyTyped(e, this);}

	@Override
	public void mousePressed (MouseEvent e){Events.mousePressed(e, this);}

	@Override
	public void mouseReleased(MouseEvent e){Events.mouseReleased(e, helicopter);}

	@Override
	public void mouseDragged (MouseEvent e){Events.mouseMovedOrDragged(e, helicopter);}

	@Override
	public void mouseMoved   (MouseEvent e){Events.mouseMovedOrDragged(e, helicopter);}
		
	@Override
	public void mouseEntered(MouseEvent e)
	{		
		if(WindowManager.window ==  GAME && STOP_GAME_WHEN_MOUSE_OUTSIDE_WINDOW)
		{
			this.mouseInWindow = true;
			Events.lastCurrentTime = System.currentTimeMillis();
		}
		this.backgroundRepaintTimer = 0;
	}	
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		if(WindowManager.window ==  GAME && STOP_GAME_WHEN_MOUSE_OUTSIDE_WINDOW)
		{
			this.mouseInWindow = false;
			Events.playingTime += System.currentTimeMillis() - Events.lastCurrentTime;
		}
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
		Window.dictionary.switchHelicopterTypeTo(helicopter.getType());
	}
	
	public static Controller getInstance()
	{
		return controller;
	}
	
	public GameEntityRecycler getGameEntityRecycler()
	{
		return gameEntityRecycler;
	}
	
	public Scenery getScenery()
	{
		return this.scenery;
	}
	
	public Savegame getSaveGame()
	{
		return saveGame;
	}
	
	public void setSaveGame(Savegame saveGame)
	{
		this.saveGame = saveGame;
	}
}