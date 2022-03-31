package de.helicopter_vs_aliens.gui.window;

import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.util.dictionary.Language;

import static de.helicopter_vs_aliens.gui.WindowType.CONTACT;
import static de.helicopter_vs_aliens.gui.WindowType.DESCRIPTION;
import static de.helicopter_vs_aliens.gui.WindowType.HELICOPTER_TYPES;
import static de.helicopter_vs_aliens.gui.WindowType.INFORMATION;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_1;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_2;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_3;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_4;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_5;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_6;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_7;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_8;
import static de.helicopter_vs_aliens.util.dictionary.Language.ENGLISH;
import static de.helicopter_vs_aliens.util.dictionary.Language.GERMAN;

class LabelTextProvider
{
    // TODO möglichst alle längeren Texte innerhalb dieser Klasse ins Dictionary überführen
    
    private static final int
        HTML_SIZE = 5;    // Standard-Schriftgröße der StartScreen-Menü-Texte
    
    String getLabel(Language language, WindowType window, StartScreenMenuButtonType page)
    {
        if(language == ENGLISH)
        {
            if(window  == INFORMATION)
            {
                if(page == BUTTON_1)
                {
                    // Handlung
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_2)
                {
                    // Änderungen seit 1.0
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Änderungen seit 1.1
                    return
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
                            "special mode<font color=\"#D2D2D2\"> (see section " +
                            "<font color=\"#FFFFFF\">\"Special mode\"" +
                            "<font color=\"#D2D2D2\">) is now available." +
                            "<br>- <font color=\"#FFFFD2\">Pegasus" +
                            "<font color=\"#D2D2D2\"> and<font color=\"#FFFFD2\"> Helios type" +
                            "<font color=\"#D2D2D2\"> helicopters are " +
                            "now available. Helios type helicopters can only be played in special mode." +
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
                            "</b></font></span></html>";
                }
                else if(page == BUTTON_5)
                {
                    // Credits
                    return
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
                            "</font></b></html>";
                }
                else if(page == BUTTON_6)
                {
                    // Copyright
                    return
                        "<html><font size = \"" + HTML_SIZE +
                            "\" face=\"Dialog\" color=\"#D2D2D2\">" +
                            "This is a freeware game. The passing on of this game " +
                            "to others is therefore explicitly allowed and you can " +
                            "feel encouraged to do so! However, program changes of " +
                            "any kind may only be carried out by the developer of " +
                            "this game." +
                            "</font></html>";
                }
            }
            else if(window  == DESCRIPTION)
            {
                if(page == BUTTON_1)
                {
                    // Spielbeschreibung
                    return
                        "<html><font size = \"" + HTML_SIZE +
                            "\" face=\"Dialog\" color=\"#D2D2D2\">" +
                            "Before the game starts, you can select from " + HelicopterType.size() + " " +
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
                            "</font></html>";
                }
                else if(page == BUTTON_2)
                {
                    // Finanzen/Reparatur
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_3)
                {
                    // Upgrades
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Boss-Gegner
                    return
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
                            "</font></html>";
                }
                else if(page == BUTTON_5)
                {
                    // Bedienung
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_6)
                {
                    // PowerUps
                    return
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
                            Helicopter.INVULNERABILITY_DAMAGE_REDUCTION + "% Indestructibility " +
                            "for 15 seconds " +
                            "<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
                            "<font size = \"" + (HTML_SIZE-1) + "\">" +
                            "Triple damage for 15 seconds" +
                            "<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
                            "<font size = \"" + (HTML_SIZE-1) + "\">" +
                            "Increased fire rate for 15 seconds" +
                            "</b></font></html>";
                }
                else if(page == BUTTON_8)
                {
                    // Spezial-Modus
                    return
                        "<html><font size = \"" + HTML_SIZE + "\" face=\"Dialog" +
                            "\" color=\"#D2D2D2\">" +
                            "In <font color=\"#FFFFD2\">special mode<font color=\"#D2D2D2\">, players do not receive any "
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
                            "</font></html>";
                }
            }
            else if(window  == HELICOPTER_TYPES)
            {
                if(page == BUTTON_1)
                {
                    // Allgemein
                    return
                        "<html><font size = \"" + HTML_SIZE + "\" " +
                            "face=\"Dialog\" color=\"#D2D2D2\">" +
                            "You can choose from " + HelicopterType.size() + " different " +
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
                            "</font></html>";
                }
                else if(page == BUTTON_3)
                {
                    // Phönix
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Roch
                    return
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
                            "</font></html>";
                }
                else if(page == BUTTON_5)
                {
                    // Orochi
                    return
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
                            "</font></html>";
                }
                else if(page == BUTTON_6)
                {
                    // Kamaitachi
                    return
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
                            "</font></html>";
                }
                else if(page == BUTTON_7)
                {
                    // Pegasus
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_8)
                {
                    // Helios
                    return
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
                            + "special mode.<font color=\"#D2D2D2\">" +
                            "<font color=\"#D2D2D2\">" +
                            "</font></html>";
                }
            }
            else if(window  == CONTACT)
            {
                if(page == BUTTON_1)
                {
                    return
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
                            "</font></html>";
                }
            }
        }
        else if(Window.language == GERMAN)
        {
            if(window  == INFORMATION)
            {
                if(page == BUTTON_1)
                {
                    // Handlung
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_2)
                {
                    // Änderungen seit 1.0
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Änderungen seit 1.1
                    return
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
                            "- Als neuer Spielmodus ist der <font color=\"#FFFFD2\">Spezial-Modus" +
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
                            "</b></font></span></html>";
                }
                else if(page == BUTTON_5)
                {
                    // Credits
                    return
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
                            "</font></b></html>";
                }
                else if(page == BUTTON_6)
                {
                    // Copyright
                    return
                        "<html><font size = \"" + HTML_SIZE + "\" " +
                            "face=\"Dialog\" color=\"#D2D2D2\">" +
                            "Dies ist ein Freeware-Spiel. Die Weitergabe ist also " +
                            "ausdrücklich erlaubt und auch erwünscht! " +
                            "Programmänderungen jeglicher Art sind allerdings dem " +
                            "Programmierer vorbehalten!" +
                            "</font></html>";
                }
            }
            else if(window  == DESCRIPTION)
            {
                if(page == BUTTON_1)
                {
                    // Spielbeschreibung
                    return
                        "<html><font size = \"" + HTML_SIZE + "\" " +
                            "face=\"Dialog\" color=\"#D2D2D2\">" +
                            "Zu Beginn des Spiels stehen Ihnen " + HelicopterType.size() + " verschiedene " +
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
                            "</font></html>";
                }
                else if(page == BUTTON_2)
                {
                    // Finanzen/Reparatur
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_3)
                {
                    // Upgrades
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Boss-Gegner
                    return
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
                            "</font></html>";
                }
                else if(page == BUTTON_5)
                {
                    // Bedienung
                    return
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
                            "</font></html>";
                }
                else if(page == BUTTON_6)
                {
                    // PowerUps
                    return
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
                            Helicopter.INVULNERABILITY_DAMAGE_REDUCTION + "% Unverwundbarkeit " +
                            "für 15 Sekunden " +
                            "<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
                            "<font size = \"" + (HTML_SIZE-1) + "\">" +
                            "3-fache Feuerkraft für 15 " + "Sekunden" +
                            "<font size = \"" + (HTML_SIZE-2) + "\"><br><br>" +
                            "<font size = \"" + (HTML_SIZE-1) + "\">" +
                            "erhöhte Schussrate für 15 Sekunden " +
                            "</b></font></html>";
                }
                else if(page == BUTTON_8)
                {
                    // Spezial-Modus
                    return
                        "<html><font size = \"" + HTML_SIZE + "\" face=\"Dialog" +
                            "\" color=\"#D2D2D2\">" +
                            "Im <font color=\"#FFFFD2\">Spezial-Modus"
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
                            "</font></html>";
                }
            }
            else if(window  == HELICOPTER_TYPES)
            {
                if(page == BUTTON_1)
                {
                    // Allgemein
                    return
                        "<html><font size = \"" + HTML_SIZE + "\" " +
                            "face=\"Dialog\" color=\"#D2D2D2\">" +
                            "Der Spieler hat die Wahl zwischen " + HelicopterType.size() + " verschiedenen " +
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
                            "</font></html>";
                }
                else if(page == BUTTON_3)
                {
                    // Phönix
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Roch
                    return
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
                            "</font></html>";
                }
                else if(page == BUTTON_5)
                {
                    // Orochi
                    return
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
                            "</font></html>";
                }
                else if(page == BUTTON_6)
                {
                    // Kamaitachi
                    return
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
                            "</font></html>";
                }
                else if(page == BUTTON_7)
                {
                    // Pegasus
                    return
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
                            "</b></font></html>";
                }
                else if(page == BUTTON_8)
                {
                    // Helios
                    return
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
                            + "im <font color=\"#FFFFD2\">Spezial-Modus<font color=\"#D2D2D2\"> spielen."
                            + "</font></html>";
                }
            }
            else if(window  == CONTACT)
            {
                if(page == BUTTON_1)
                {
                    return
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
                            "</font></html>";
                }
            }
        }
        return "";
    }
}
