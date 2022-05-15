package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.model.enemy.barrier.BigBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.DiggerBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.CloakedBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.Protector;
import de.helicopter_vs_aliens.model.enemy.barrier.PushingBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.ShieldingBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.ShootingBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.SmallBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.StunningBarrier;
import de.helicopter_vs_aliens.model.enemy.boss.BigShieldMaker;
import de.helicopter_vs_aliens.model.enemy.boss.Bodyguard;
import de.helicopter_vs_aliens.model.enemy.boss.FinalBoss;
import de.helicopter_vs_aliens.model.enemy.boss.FirstBoss;
import de.helicopter_vs_aliens.model.enemy.boss.FourthBoss;
import de.helicopter_vs_aliens.model.enemy.boss.FourthBossServant;
import de.helicopter_vs_aliens.model.enemy.boss.Healer;
import de.helicopter_vs_aliens.model.enemy.boss.SecondBoss;
import de.helicopter_vs_aliens.model.enemy.boss.SecondBossServant;
import de.helicopter_vs_aliens.model.enemy.boss.SmallShieldMaker;
import de.helicopter_vs_aliens.model.enemy.boss.ThirdBoss;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.AmbushingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.BatchwiseFlyingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.CallbackEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.CapturingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Carrier;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.ChaoticallyFlyingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.CloakedEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.CrazyEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Dodger;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.EscapedSpeeder;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Freighter;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Kaboom;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.LonelySpeeder;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.LoopingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Rock;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Runabout;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Shooter;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.SinusoidallyFlyingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.SmallCruiser;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.TeleportingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.TinyVessel;
import de.helicopter_vs_aliens.util.ColorRange;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public enum EnemyType implements GameEntityFactory<Enemy>
{
    TINY( // ab Level 1
        TinyVessel::new,
        TinyVessel.class,
        EnemyModelType.TIT,
        ColorRange.of(new Color(180, 120, 0), 30, 30, 15),
        1,
        2,
        110),
    
    SMALL( // ab Level 3
        SmallCruiser::new,
        SmallCruiser.class,
        EnemyModelType.TIT,
        ColorRange.of(new Color(140, 65, 0), 25, 35, 25),
        2,
        3,
        125),
    
    RUNABOUT( // level 5
        Runabout::new,
        Runabout.class,
        EnemyModelType.TIT,
        ColorRange.of(new Color(100, 100, 40), 30, 30, 25),
        2,
        2,
        100),
    
    FREIGHTER( // ab Level 7
        Freighter::new,
        Freighter.class,
        EnemyModelType.CARGO,
        ColorRange.of(new Color(100, 50, 45), 30, 30, 20),
        4,
        25,
        145),
    
    BATCHWISE( // ab Level 11
        BatchwiseFlyingEnemy::new,
        BatchwiseFlyingEnemy.class,
        EnemyModelType.TIT,
        ColorRange.of(new Color(135, 80, 85), 30, 20, 30),
        6,
        16,
        130),
    
    SINUS( // ab Level 13
        SinusoidallyFlyingEnemy::new,
        SinusoidallyFlyingEnemy.class,
        EnemyModelType.TIT,
        ColorRange.of(new Color(185, 70, 135), 40, 30, 40),
        6,
        6,
        110),
    
    DODGER( // ab Level 16
        Dodger::new,
        Dodger.class,
        EnemyModelType.TIT,
        ColorRange.of(new Color(85, 35, 95), 20, 30, 30),
        9,
        24,
        170),
    
    CHAOTIC( // ab Level 21
        ChaoticallyFlyingEnemy::new,
        ChaoticallyFlyingEnemy.class,
        EnemyModelType.TIT,
        ColorRange.of(new Color(150, 130, 75), 20, 25, 30),
        11,
        22,
        125),
        
    CALLBACK( // ab Level 24
        CallbackEnemy::new,
        CallbackEnemy.class,
        EnemyModelType.TIT,
        ColorRange.of(new Color(70, 130, 30), 40, 50, 45),
        10,
        30,
        95),
    
    SHOOTER( // ab Level 26
        Shooter::new, 
        Shooter.class, 
        EnemyModelType.CARGO, 
        ColorRange.of(new Color(80, 80, 80), 25, 25, 25),   
        12,
        60,
        80),
    
    CLOAK( // ab Level 31
        CloakedEnemy::new, 
        CloakedEnemy.class,
        EnemyModelType.CARGO, 
        ColorRange.withoutScatteringOf(Colorations.cloakedEnemy),   
        16, 
        100, 
        85),
    
    LONELY_SPEEDER( // ab Level 35
        LonelySpeeder::new, 
        LonelySpeeder.class, 
        EnemyModelType.TIT, 
        ColorRange.of(new Color(75, 75, 75), 30, 30, 30),   
        14, 
        26, 
        70),

    CARRIER( // ab Level 36
        Carrier::new, 
        Carrier.class, 
        EnemyModelType.CARGO, 
        ColorRange.of(new Color(70, 60, 45), 15, 10, 10),
        19, 
        450, 
        165),

    CRAZY( // ab Level 37
        CrazyEnemy::new, 
        CrazyEnemy.class, 
        EnemyModelType.TIT, 
        ColorRange.of(new Color(180, 230, 20), 50, 20, 60),   
        22, 
        140, 
        115),      
    
    AMBUSH( // ab Level 41
        AmbushingEnemy::new, 
        AmbushingEnemy.class,
        EnemyModelType.TIT,
        ColorRange.of(new Color(30, 60, 120), 40, 40, 40),   
        30, 
        150, 
        95),
        
    LOOPING( // ab Level 43
        LoopingEnemy::new, 
        LoopingEnemy.class, 
        EnemyModelType.TIT,
        ColorRange.withoutScatteringOf(Colorations.cloakedEnemy),
        30, 
        330, 
        105),
        
    CAPTURING( // ab Level 45
        CapturingEnemy::new, 
        CapturingEnemy.class, 
        EnemyModelType.TIT, 
        ColorRange.of(new Color(5, 105, 90), 55, 40, 30),   
        30, 
        520, 
        115),

    TELEPORTING( // ab Level 46
        TeleportingEnemy::new, 
        TeleportingEnemy.class, 
        EnemyModelType.CARGO, 
        ColorRange.of(new Color(190, 10, 15), 40, 60, 60),   
        35, 
        500, 
        130),
    
    // Boss-Gegner
    BOSS_1(
        FirstBoss::new, 
        FirstBoss.class, 
        EnemyModelType.TIT, 
        ColorRange.withoutScatteringOf(new Color(115, 70, 100)),
        75, 
        225, 
        275),
    
    BOSS_2(
        SecondBoss::new, 
        SecondBoss.class, 
        EnemyModelType.CARGO,
        ColorRange.withoutScatteringOf(new Color(85, 85, 85)),
        100, 
        500, 
        250),
        
    BOSS_2_SERVANT(
        SecondBossServant::new, 
        SecondBossServant.class, 
        EnemyModelType.TIT,
        ColorRange.of(new Color(80, 80, 80), 25, 25, 25),
        5, 
        15, 
        65),
    
    BOSS_3(
        ThirdBoss::new, 
        ThirdBoss.class, 
        EnemyModelType.TIT,
        ColorRange.withoutScatteringOf(Colorations.cloakedEnemy),
        500, 
        1750, 
        250),
    
    BOSS_4(
        FourthBoss::new, 
        FourthBoss.class, 
        EnemyModelType.TIT,
        ColorRange.withoutScatteringOf(Color.red),
        1250, 
        10000, 
        250),
    
    BOSS_4_SERVANT(
        FourthBossServant::new, 
        FourthBossServant.class, 
        EnemyModelType.TIT,
        ColorRange.of(new Color(80, 80, 80), 20, 20, 20),
        1, 
        100, 
        85),
    
    FINAL_BOSS(
        FinalBoss::new, 
        FinalBoss.class, 
        EnemyModelType.TIT,
        ColorRange.withoutScatteringOf(Colorations.brown),
        5000, 
        25000, 
        450),
    
    SMALL_SHIELD_MAKER(
        SmallShieldMaker::new, 
        SmallShieldMaker.class, 
        EnemyModelType.TIT,
        ColorRange.withoutScatteringOf(new Color(25, 125, 105)),
        55, 
        3000, 
        125),
    
    BIG_SHIELD_MAKER(
        BigShieldMaker::new, 
        BigShieldMaker.class, 
        EnemyModelType.TIT,
        ColorRange.withoutScatteringOf(new Color(105, 135, 65)),
        80, 
        4250, 
        145),
    
    BODYGUARD(
        Bodyguard::new, 
        Bodyguard.class, 
        EnemyModelType.TIT,
        ColorRange.withoutScatteringOf(Colorations.cloakedEnemy),
        150, 
        7500, 
        225),
    
    HEALER(
        Healer::new, 
        Healer.class, 
        EnemyModelType.CARGO,
        ColorRange.withoutScatteringOf(Color.white),
        65, 
        3500, 
        115),
    
    PROTECTOR(
        Protector::new,
        Protector.class, 
        EnemyModelType.BARRIER,
        ColorRange.getDefault(),
        25,
        Constants.COUNTLESS_HIT_POINTS,
        90),
    
    // Hindernisse (Barrier)
    SMALL_BARRIER( // ab Level 2
        SmallBarrier::new, 
        SmallBarrier.class, 
        EnemyModelType.BARRIER,
        ColorRange.withoutScatteringOf(Colorations.bleachedGreen),
        4,
        Constants.COUNTLESS_HIT_POINTS,
        65), 
    
    BIG_BARRIER( // ab Level 6
        BigBarrier::new, 
        BigBarrier.class,
        EnemyModelType.BARRIER,
        ColorRange.withoutScatteringOf(Colorations.bleachedGreen),
        4,
        Constants.COUNTLESS_HIT_POINTS,
        150),
    
    STUNNING_BARRIER( // ab Level 12
        StunningBarrier::new, 
        StunningBarrier.class, 
        EnemyModelType.BARRIER,
        ColorRange.withoutScatteringOf(Colorations.bleachedYellow),
        5,
        Constants.COUNTLESS_HIT_POINTS,
        65),
    
    PUSHING_BARRIER( // ab Level 15
        PushingBarrier::new, 
        PushingBarrier.class, 
        EnemyModelType.BARRIER,
        ColorRange.withoutScatteringOf(Colorations.bleachedOrange),
        6,
        Constants.COUNTLESS_HIT_POINTS,
        105),
    
    SHOOTING_BARRIER( // ab Level 18
        ShootingBarrier::new, 
        ShootingBarrier.class, 
        EnemyModelType.BARRIER,
        ColorRange.getDefault(),
        7,
        Constants.COUNTLESS_HIT_POINTS,
        85),
    
    BURROWING_BARRIER( // ab Level 32
        DiggerBarrier::new, 
        DiggerBarrier.class, 
        EnemyModelType.BARRIER,
        ColorRange.getDefault(),
        9,
        Constants.COUNTLESS_HIT_POINTS,
        80),
            
    SHIELDING_BARRIER( // ab Level 42
        ShieldingBarrier::new, 
        ShieldingBarrier.class, 
        EnemyModelType.BARRIER,
        ColorRange.withoutScatteringOf(Colorations.shieldingBarrierTurquoise),
        4,
        Constants.COUNTLESS_HIT_POINTS,
        80),
    
    CLOAKED_BARRIER( // ab Level 44
        CloakedBarrier::new, 
        CloakedBarrier.class, 
        EnemyModelType.BARRIER,
        ColorRange.getDefault(),
        15,
        Constants.COUNTLESS_HIT_POINTS,
        100),
    
    // Hindernis (Rock-Enemy)
    ROCK( // ab Level 27
        Rock::new, 
        Rock.class, 
        EnemyModelType.CARGO, 
        ColorRange.getDefault(),
        0, 
        2, 
        300),
    
    // sonstige Gegner
    KABOOM( // ab Level 12
        Kaboom::new, 
        Kaboom.class, 
        EnemyModelType.TIT, 
        ColorRange.withoutScatteringOf(Color.white),
        0,
        Constants.COUNTLESS_HIT_POINTS,
        120),
    
    ESCAPED_SPEEDER( // ab Level 36
        EscapedSpeeder::new, 
        EscapedSpeeder.class, 
        EnemyModelType.TIT,
        ColorRange.of(new Color(75, 75, 75), 30, 30, 30),
        14, 
        26, 
        70);
        
    private static final List<EnemyType>
        VALUES = List.of(values());
    
    private static class Constants
    {
        public static final int
            COUNTLESS_HIT_POINTS = Integer.MAX_VALUE;
    }
    
    private final static Set<EnemyType>
        BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(BOSS_1, PROTECTOR)),
        FINAL_BOSS_SERVANT_TYPES = Collections.unmodifiableSet(EnumSet.range(SMALL_SHIELD_MAKER, PROTECTOR)),
        BARRIERS = Collections.unmodifiableSet(EnumSet.range(SMALL_BARRIER, CLOAKED_BARRIER)),
        CLOAKABLE_AS_MINI_BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(LONELY_SPEEDER, TELEPORTING));
    
    private static final float
        BOUNTY_MULTIPLIER = 7.5f;
    
    
    private final Supplier<? extends Enemy>
        instanceSupplier;
    
    private final Class<? extends Enemy>
        enemyClass;
    
    private final EnemyModelType
        model;
    
    private final ColorRange
        colorRange;
    
    private final int
        strength; // Stärke des Gegners; bestimmt die Höhe der Belohnung bei Abschuss
    
    private final int
        hitPoints;
    
    private final int
        width;
    
    EnemyType(Supplier<? extends Enemy> instanceSupplier, Class<? extends Enemy> enemyClass, EnemyModelType enemyModelType, ColorRange colorRange, int strength, int hitPoints, int width)
    {
        this.instanceSupplier = instanceSupplier;
        this.enemyClass = enemyClass;
        this.model = enemyModelType;
        this.colorRange = colorRange;
        this.strength = strength;
        this.hitPoints = hitPoints;
        this.width = width;
    }
    
    static Set<EnemyType> getFinalBossServantTypes()
    {
        return FINAL_BOSS_SERVANT_TYPES;
    }
    
    static Set<EnemyType> getBarrierTypes()
    {
        return BARRIERS;
    }
    
    public boolean isMajorBoss()
    {
        return BOSS_TYPES.contains(this);
    }
    
    public boolean isFinalBossServant()
    {
        return FINAL_BOSS_SERVANT_TYPES.contains(this);
    }
    
    boolean isMinorServant()
    {
        return this == BOSS_2_SERVANT || this == BOSS_4_SERVANT;
    }
    
    boolean isServant()
    {
        return	this.isMinorServant() || this.isFinalBossServant();
    }
    
    boolean isMainBoss()
    {
        return this.isMajorBoss() && !this.isServant();
    }
    
    public boolean isShieldMaker()
    {
        return this  == SMALL_SHIELD_MAKER || this == BIG_SHIELD_MAKER;
    }
    
    public boolean isCloakableAsMiniBoss()
    {
        return CLOAKABLE_AS_MINI_BOSS_TYPES.contains(this);
    }
    
    public int getStrength()
    {
        return this.strength;
    }
    
    public static List<EnemyType> getValues()
    {
        return VALUES;
    }
    
    @Override
    public Enemy makeInstance()
    {
        Enemy enemy = instanceSupplier.get();
        enemy.type = this;
        return enemy;
    }
    
    @Override
    public Class<? extends Enemy> getCorrespondingClass()
    {
        return enemyClass;
    }
    
    public int getBounty()
    {
        return (int)(BOUNTY_MULTIPLIER * this.getStrength());
    }
    
    public int getHitPoints()
    {
        return hitPoints;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public EnemyModelType getModel()
    {
        return model;
    }
    
    public Color calculateColor()
    {
        return colorRange.selectColor();
    }
}