package de.helicopter_vs_aliens.gui.window;

import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.graphics.GraphicsApiType;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.util.font.FontSpecification;
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
    // TODO möglichst alle längeren Texte innerhalb dieser Klasse in die property datei überführen und über das Dictionary aufrufen
    // zum Aufbau der String eine StringBuilder verwenden um die einheitlich und HTML spezifisdcjhen ELemente zu Ergänzen

    String getLabel(Language language, WindowType window, StartScreenMenuButtonType page)
    {
        GraphicsApiType graphicsApiType = GameResources.getProvider()
                                                       .getGraphicsApiType();

        FontSpecification fontSpecification = language.getFontSpecification(window, page, graphicsApiType);

        if(language == ENGLISH)
        {
            if(window  == INFORMATION)
            {
                if(page == BUTTON_1)
                {
                    // Handlung
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "It is the year 2371. Since the Mars " +
                            "colonies' War of Independence a hundred years in the " +
                            "past, there were no military conflicts among humans and " +
                            "thus expenditures for military purposes had been " +
                            "minimized. But now thousands of hostile flying " +
                            "vessels of unknown origin have been spotted on the " +
                            "northern Sahara and along the African Mediterranean " +
                            "coast. Some of these flying vessels already started " +
                            "attacking the North African mega-cities including the " +
                            "world's capital Cairo. Karanijem Su, president of the " +
                            "World Government, has declared a state of emergency " +
                            "and is now asking reservists and volunteers from all " +
                            "over the world for help. You are one of them." +

                            "<body></html>";
                }
                else if(page == BUTTON_2)
                {
                    // Änderungen seit 1.0
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "- To make the start of play easier for beginners, the " +
                            "first levels' difficulty was<br>" +
                            "&nbsp reduced significantly. Additionally, the game " +
                            "manual is now much more detailed.<br>" +
                            "- The rather unpopular <font color=\"#FFFFD2\">Phoenix" +
                            "<font color=\"#D2D2D2\">, <font color=\"#FFFFD2\">" +
                            "Orochi<font color=\"#D2D2D2\"> and " +
                            "<font color=\"#FFFFD2\">Kamaitachi type" +
                            "<font color=\"#D2D2D2\"> helicopters are now<br>" +
                            "&nbsp considerably more powerful.<br>- Moving backgrounds " +
                            "have been implemented.<br>- There are now \"save " +
                            "states\" every five levels. Once reached, the player " +
                            "can no<br>" +
                            "&nbsp longer fall back in a level below." +
                            "<br>- Many other changes affecting game " +
                            "balance, graphics or the upgrade system<br>" +
                            "&nbsp were made." +

                            "</font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Änderungen seit 1.1
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "- To improve the game's graphics, antialiasing and " +
                            "gradient colors were used.<br>" +
                            "- Each <font color=\"#FFFFD2\">helicopter type" +
                            "<font color=\"#D2D2D2\"> now has its own energy " +
                            "consuming " +
                            "<font color=\"#FFFFD2\">standard upgrade" +
                            "<font color=\"#D2D2D2\"> and " +
                            "at least one unique<br>" +
                            "&nbsp <font color=\"#FFFFD2\">" +
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
                            "occasionally uncommon strong enemies " +
                            "can now occur.<br>&nbsp These <font color=\"#FFFFD2\">" +
                            "minor boss enemies<font color=\"#D2D2D2\"> (see section " +
                            "<font color=\"#FFFFFF\">\"Enemies\"" +
                            "<font color=\"#D2D2D2\">) can lose " +
                            "<font color=\"#FFFFD2\">" +
                            "power-ups<font color=\"#D2D2D2\"> (see section " +
                            "<font color=\"#FFFFFF\">\"Power-ups\"" +
                            "<font color=\"#D2D2D2\">)" +
                            "<br> &nbsp which temporarily " +
                            "improve the helicopter.<br>- The game is now a real " +
                            "application and does no longer " +
                            "depend on the explorer. <br>- A save function as well as a <font color=\"#FFFFD2\">" +
                            "high-score<font color=\"#D2D2D2\"> is now available."+
                            "<br>- Lots of new enemy classes have been implemented." +
                            "<br>- Many other changes affecting controls, game balance, " +
                            "graphics or the upgrade system were made." +

                            "</b></font></span></html>";
                }
                else if(page == BUTTON_5)
                {
                    // Credits
                    return
                        "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Before the game starts, you can select from " + HelicopterType.count() + " " +
                            "<font color=\"#FFFFD2\">helicopter types" +
                            "<font color=\"#D2D2D2\"> which differ in their starting " +
                            "attributes and their available upgrades. With the " +
                            "helicopter of your choice, you are supposed to destroy " +
                            "as many enemies as possible. Each successful " +
                            "destruction of a hostile flying vessels is financially rewarded. " +
                            "After a certain number of " +
                            "destroyed enemies, you will proceed to higher levels " +
                            "where you " +
                            "have to face more and more powerful enemies. The more " +
                            "difficult it is to eliminate an opponent, the more " +
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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "helicopter type (see section " +
                            "<font color=\"#FFFFFF\">\"Helicopter types\"" +
                            "<font color=\"#D2D2D2\">)." +
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Boss-Gegner
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "HelicopterDefence is exclusively mouse controlled:<br>" +
                            "Your helicopter always moves towards the mouse cursor. " +
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
                            "color=\"#FFFFD2\">electromagnetic " +
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
                    int lineDistance = graphicsApiType == GraphicsApiType.GRAPHICS_2D ? 3 : 6;
                    int headingMargin = graphicsApiType == GraphicsApiType.GRAPHICS_2D ? 1 : 4;

                    return
                        "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            ".line-distance {font-size: " + lineDistance + "px;}" +
                            ".heading-margin {font-size: " + headingMargin + "px;}" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "After destruction, some opponents drop one of the 6 " +
                            "following <font color=\"#FFFFD2\">power-ups" +
                            "<font color=\"#D2D2D2\">:" +

                            wrapWithDivClass("<br><br>", "heading-margin") +
                            "Bonus credit" +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            "Unlimited energy for 15 seconds" +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            "Partial repairs" +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            Helicopter.INVULNERABILITY_DAMAGE_REDUCTION + "% Indestructibility " + "for 15 seconds" +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            "Triple damage for 15 seconds" +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            "Increased fire rate for 15 seconds" +
                            "</font></html>";
                }
                else if(page == BUTTON_8)
                {
                    // Spezial-Modus
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "You can choose from " + HelicopterType.count() + " different " +
                            "<font color=\"#FFFFD2\">helicopter types" +
                            "<font color=\"#D2D2D2\">. Each " +
                            "of these helicopters has a unique, energy-consuming " +
                            "ability and at least one " +
                            "<font color=\"#FFFFD2\">special upgrade" +
                            "<font color=\"#D2D2D2\">, which can " +
                            "only be purchased by this helicopter type. Furthermore, " +
                            "the helicopter types differ in terms of price and " +
                            "upgrade ability of the individual " +
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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "<font color=\"#FFFFD2\">Orochi type helicopters" +
                            "<font color=\"#D2D2D2\"> are true all-rounder: They fly " +
                            "pretty fast, have missiles with excellent drive and are " +
                            "still properly armor-plated. The " +
                            "<font color=\"#FFFFD2\">fire rate" +
                            "<font color=\"#D2D2D2\"> of " +
                            "Orochi type helicopters is limited, but only they can " +
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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<font color=\"#FFFFD2\">bonus credit." +
                            "</font></html>";
                }
                else if(page == BUTTON_7)
                {
                    // Pegasus
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

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
                            "<br>Bj\u00F6rn Hansen" +
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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Wir schreiben das Jahr 2371. Seit dem " +
                            "Unabh\u00E4ngigkeitskrieg der Mars-Kolonien, der nun schon " +
                            "\u00FCber 100 Jahre zur\u00FCck liegt, hat es keine " +
                            "kriegerischen Auseinandersetzungen unter Menschen mehr " +
                            "gegeben und Ausgaben f\u00FCr milit\u00E4rische Zwecke sind " +
                            "folglich auf ein Minimum reduziert worden. " +
                            "\n\nDoch nun wurden tausende feindlicher Flugobjekte " +
                            "unbekannter Herkunft \u00FCber der n\u00F6rdlichen " +
                            "Sahara und an der afrikanischen Mittelmeerk\u00FCste " +
                            "gesichtet. Einige dieser Flugobjekte haben bereits die " +
                            "nordafrikanischen Megametropolen unter Beschuss " +
                            "genommen, darunter auch Welthauptstadt Kairo. " +
                            "\n\nKaranijem Su, der Pr\u00E4sident der Weltregierung, hat " +
                            "den Ausnahmezustand verh\u00E4ngt und bittet " +
                            "Reservisten und Freiwillige aus aller Welt um Hilfe. " +
                            "Sie sind einer davon." +
                            "</b></font></html>";
                }
                else if(page == BUTTON_2)
                {
                    // Änderungen seit 1.0
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "- Zu Gunsten einer gr\u00F6\u00DFeren Einsteigerfreundlichkeit " +
                            "wurde der Schwierigkeitsgrad der<br>" +
                            "&nbsp ersten Levels deutlich gesenkt und die " +
                            "Spielanleitung wesentlich ausf\u00FChrlicher gestaltet.<br>" +
                            "- Die bisher eher unbeliebten Helikopter der " +
                            "<font color=\"#FFFFD2\">Ph\u00F6nix-" +
                            "<font color=\"#D2D2D2\">, " +
                            "<br>	&nbsp <font color=\"#FFFFD2\">Orochi-" +
                            "<font color=\"#D2D2D2\"> und " +
                            "<font color=\"#FFFFD2\">Kamaitachi-Klasse" +
                            "<font color=\"#D2D2D2\"> sind jetzt schlagkr\u00E4ftiger.<br>" +
                            "- Das Spiel verf\u00FCgt nun \u00FCber bewegte Hintergr\u00FCnde.<br>" +
                            "- Statt wie zuvor alle 10 erreicht der Spieler jetzt " +
                            "schon alle 5 Level einen " +
                            "Spielstand,<br> &nbsp von dem aus er nach Reparatur " +
                            "oder Absturz nicht mehr zur\u00FCck fallen kann.<br>" +
                            "- Viele \u00C4nderungen, die unter anderem Spielbalance, " +
                            "Grafik<br>	&nbsp oder das Upgrade-System betreffen, " +
                            "wurden vorgenommen." +
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Änderungen seit 1.1
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "- Zur Verbesserung der Grafik kommen Gradientenfarben " +
                            "und Antialiasing zum Einsatz.<br>" +
                            "- Jede Helikopter-Klasse verf\u00FCgt nun \u00FCber ein eigenes, " +
                            "energieverbrauchendes <font color=\"#FFFFD2\">\"Standard-Upgrade\"<font color=\"#D2D2D2\"> " +
                            "sowie \u00FCber <br> &nbsp mindestens ein eigenes <font color=\"#FFFFD2\">\"Spezial-Upgrade\"<font color=\"#D2D2D2\">. Damit unterscheiden sich die Helikopter-" +
                            "Klassen jetzt deutlicher.<br>" +
                            "- Mit der <font color=\"#FFFFD2\">Pegasus-" +
                            "<font color=\"#D2D2D2\"> und der  <font color=\"#FFFFD2\">Helios-Klasse" +
                            "<font color=\"#D2D2D2\"> stehen dem Spieler nun " +
                            "zwei weitere Helikopter-Klassen zur Verf\u00FCgung.<br>" +
                            "- Als neuer Spielmodus ist der <font color=\"#FFFFD2\">Spezial-Modus" +
                            "<font color=\"#D2D2D2\"> verf\u00FCgbar, welcher nur von der Helios-Klasse gespielt werden kann.<br>" +
                            "- F\u00FCr zus\u00E4tzlichen Spielreiz sorgen zuf\u00E4llig " +
                            "auftretende <font color=\"#FFFFD2\">\"Mini-Endgegner\"" +
                            "<font color=\"#D2D2D2\">. <br>	&nbsp Diese k\u00F6nnen " +
                            "<font color=\"#FFFFD2\">PowerUps" +
                            "<font color=\"#D2D2D2\"> " +
                            "verlieren, welche den Helikopter kurzfristig " +
                            "verbessern.<br>" +
                            "- Das Spiel l\u00E4uft in einer vom Explorer unabh\u00E4ngigen " +
                            "Applikation.<br>" +
                            "- Eine Speicherfunktion und eine Highscore wurden implementiert.<br>" +
                            "- Eine Reihe neuer Gegner-Klassen wurden dem Spiel hinzugef\u00FCgt.<br>" +
                            "- Viele weitere \u00C4nderungen, welche die Bedienung, das " +
                            "Upgrade-System, <br>	&nbsp die Grafik oder die " +
                            "Spielbalance betreffen wurden vorgenommen." +
                            "</b></font></span></html>";
                }
                else if(page == BUTTON_5)
                {
                    // Credits
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Besonderer Dank gilt allen meinen Beta-Testern, die " +
                            "HelikopterDefence 1.0 " +
                            "ausf\u00FChrlich gespielt und mich mit hilfreichen Tipps " +
                            "und Verbesserungsvorschl\u00E4gen " +
                            "unterst\u00FCtzt haben:<br><font color=\"#FFFFD2\">Alexander " +
                            "Schmuck, Andreas Lotze, Boris Sapancilar, Fynn Hansen, Julian Tan, " +
                            "Hauke Holm, Henner Holm, Michael Sujatta,<br>Sascha " +
                            "Degener, Thorsten R\u00FCckert, Tim Schumacher, " +
                            "Yannik Muthmann<br><font color=\"#D2D2D2\"><br>" +
                            "Besonders bedanken m\u00F6chte ich mich bei " +
                            "<font color=\"#FFFFD2\">Fabian Gebert" +
                            "<font color=\"#D2D2D2\"> f\u00FCr eine Vielzahl " +
                            "wertvoller technischer Ratschl\u00E4ge, welche meine " +
                            "Entwicklungsarbeit an diesem Spiel erheblich " +
                            "bereichert haben. Ich bedanke mich auch bei " +
                            "<font color=\"#FFFFD2\">Michael Sujatta" +
                            "<font color=\"#D2D2D2\"> f\u00FCrs Korrekturlesen und bei " +
                            "<font color=\"#FFFFD2\">Hauke Holm" +
                            "<font color=\"#D2D2D2\"> f\u00FCr seine Hilfe bei der " +
                            "Bearbeitung " +
                            "von Audio-Dateien. Vielen Dank an " +
                            "<font color=\"#FFFFD2\">Tobias P. Eser" +
                            "<font color=\"#D2D2D2\">. Mit ihm zusammen habe ich " +
                            "noch zu " +
                            "Schulzeiten mein Interesse an der " +
                            "Computerspielentwicklung entdeckt. Zuletzt m\u00F6chte ich " +
                            "mich " +
                            "bei <font color=\"#FFFFD2\">Prof. Till Tantau" +
                            "<font color=\"#D2D2D2\"> f\u00FCr eine exzellente " +
                            "Informatikvorlesung bedanken. Seine Lehrveranstaltung " +
                            "hat den Grundstein zur Entwicklung dieses Spiels " +
                            "gelegt.<br><br>Du m\u00F6chtest auch Beta-Tester werden? " +
                            "Kein Problem! Schicke Deine Verbesserungsvorschl\u00E4ge an: " +
                            "<font color=\"#FFFFD2\">info@HelicopterDefence.de" +
                            "</font></b></html>";
                }
                else if(page == BUTTON_6)
                {
                    // Copyright
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Dies ist ein Freeware-Spiel. Die Weitergabe ist also " +
                            "ausdr\u00FCcklich erlaubt und auch erw\u00FCnscht! " +
                            "Programm\u00E4nderungen jeglicher Art sind allerdings dem " +
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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Zu Beginn des Spiels stehen Ihnen " + HelicopterType.count() + " verschiedene " +
                            "<font color=\"#FFFFD2\">Helikopter-Klassen" +
                            "<font color=\"#D2D2D2\"> mit unterschiedlichen " +
                            "Startwerten zur " +
                            "Auswahl. Mit dem Helikopter Ihrer Wahl sollten Sie " +
                            "m\u00F6glichst viele feindliche Flugobjekte abschie\u00DFen. Jede " +
                            "erfolgreiche Zerst\u00F6rung eines feindlichen Flugk\u00F6rpers " +
                            "wird finanziell " +
                            "belohnt. Nach einer bestimmten " +
                            "Anzahl von Treffern k\u00F6nnen Sie in h\u00F6here Level " +
                            "aufsteigen, in denen immer schwerere Gegner auf Sie " +
                            "warten. " +
                            "Die Bezahlung bei Eliminierung von feindlichen Fliegern " +
                            "mit besonders komplizierten Flugman\u00F6vern ist " +
                            "entsprechend h\u00F6her. " +
                            "Mit dem so erwirtschafteten Geld k\u00F6nnen Sie in der " +
                            "<font color=\"#FFFFD2\">Werkstatt" +
                            "<font color=\"#D2D2D2\"> Besch\u00E4digungen am Helikopter " +
                            "reparieren lassen sowie neue " +
                            "Upgrades erwerben, mit denen ihr Helikopter noch " +
                            "schlagfertiger wird." +
                            "</font></html>";
                }
                else if(page == BUTTON_2)
                {
                    // Finanzen/Reparatur
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Nach dem Absturz des Helikopters (Totalschaden) muss " +
                            "in der <font color=\"#FFFFD2\">Werkstatt" +
                            "<font color=\"#D2D2D2\"> eine <font color=\"#FFFFD2\">" +
                            "Reparatur<font color=\"#D2D2D2\"> durchgef\u00FChrt werden," +
                            " bevor ein neuer Einsatz geflogen werden kann. Wenn " +
                            "der Spieler in diesem Fall nicht " +
                            "\u00FCber gen\u00FCgend Guthaben verf\u00FCgt, um die Instandsetzung " +
                            "zu finanzieren, ist das Spiel " +
                            "beendet. Nat\u00FCrlich kann der Spieler Reparaturen auch " +
                            "bereits nach kleinen " +
                            "Besch\u00E4digungen durchf\u00FChren. Der Preis f\u00FCr eine " +
                            "Reparatur h\u00E4ngt vom Ausma\u00DF der " +
                            "Besch\u00E4digung ab. Bei einem Totalschaden verteuert sich " +
                            "die Reparatur. Nach einer Reparatur f\u00E4llt der Spieler " +
                            "zum letzten <font color=\"#FFFFD2\">\"sicheren\" Level" +
                            "<font color=\"#D2D2D2\"> " +
                            "(1, 6, 11, 16, usw.) zur\u00FCck.<br><br>Die Pr\u00E4mie, die " +
                            "ein Spieler f\u00FCr abgeschossene Gegner " +
                            "erh\u00E4lt, h\u00E4ngt von deren St\u00E4rke und der aktuellen " +
                            "Sold-Stufe ab. Piloten eines mit " +
                            "<font color=\"#FFFFD2\">Scheinwerfern" +
                            "<font color=\"#D2D2D2\"> ausgestatteten Helikopters " +
                            "k\u00F6nnen sowohl <font color=\"#FFFFD2\">Tag-" +
                            "<font color=\"#D2D2D2\"> als auch " +
                            "<font color=\"#FFFFD2\">Nachteins\u00E4tze" +
                            "<font color=\"#D2D2D2\"> " +
                            "fliegen und erhalten daher einen Verdienstbonus von " +
                            "50%.<br><br>In der Werkstatt kann der " +
                            "Spieler anhand der farblichen Markierungen um die " +
                            "Upgrade-Buttons erfahren, wie " +
                            "preisg\u00FCnstig ein Upgrade ist: <font color=\"#82FF82\">" +
                            "gr\u00FCn<font color=\"#D2D2D2\"> - sehr guter Preis; " +
                            "<font color=\"#D2FFB4\">gelbgr\u00FCn" +
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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Die Helikopter-Upgrades unterteilen sich in " +
                            "<font color=\"#FFFFD2\">Standard-" +
                            "<font color=\"#D2D2D2\"> und <font color=\"#FFFFD2\">" +
                            "Spezial-Upgrades<font color=\"#D2D2D2\">. Mit den " +
                            "Standard-Upgrades " +
                            "k\u00F6nnen <font color=\"#FFFFD2\">Hauptrotor" +
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
                            "Helikopter-Klasse k\u00F6nnen die einzelnen Standard-" +
                            "Upgrades bis Stufe 6, 8 oder 10 ausgebaut werden. " +
                            "\nSpezial-Upgrades werden nur einmal erworben und " +
                            "k\u00F6nnen (mit wenigen Ausnahmen) nicht weiter gesteigert " +
                            "werden. " +
                            "Neben den <font color=\"#FFFFD2\">Scheinwerfern" +
                            "<font color=\"#D2D2D2\"> f\u00FCr <font color=\"#FFFFD2\">" +
                            "Nachteins\u00E4tze<font color=\"#D2D2D2\"> sind folgende " +
                            "Spezialupgrades f\u00FCr jede Helikopter-Klasse verf\u00FCgbar: " +
                            "\n<font color=\"#FFFFD2\">Goliath-Panzerung" +
                            "<font color=\"#D2D2D2\"> (verbessert die Effektivit\u00E4t " +
                            "der Standardpanzerung), " +
                            "\n<font color=\"#FFFFD2\">Durchsto\u00DFsprengk\u00F6pfe" +
                            "<font color=\"#D2D2D2\"> (dieselbe Rakete kann mehrere " +
                            "Gegner treffen) und " +
                            "\n<font color=\"#FFFFD2\">zweite Bordkanone" +
                            "<font color=\"#D2D2D2\">\t(gleichzeitiges abschie\u00DFen " +
                            "zweier Raketen). " +
                            "Dar\u00FCber hinaus existieren weitere Spezial-Upgrades, die " +
                            "nur f\u00FCr eine " +
                            "einzige Helicoper-Klasse verf\u00FCgbar sind (siehe " +
                            "Abschnitt <font color=\"#FFFFFF\">\"Helikopter-" +
                            "Klassen\"<font color=\"#D2D2D2\">). " +
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Boss-Gegner
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Alle 10 Level trifft der Spieler auf einen besonders " +
                            "schwer zu besiegenden <font color=\"#FFFFD2\">Boss-" +
                            "Gegner<font color=\"#D2D2D2\">, " +
                            "aber auch in den Standard-Levels k\u00F6nnen gelegentlich " +
                            "kleine Boss-Gegner (<font color=\"#FFFFD2\">Mini-Bosse" +
                            "<font color=\"#D2D2D2\">) erscheinen, welche schwerer " +
                            "zu zerst\u00F6ren sind als gew\u00F6hnliche Gegner. " +
                            "F\u00FCr den Abschuss eines Boss-Gegners erh\u00E4lt der Spieler " +
                            "eine gro\u00DFz\u00FCgige finanzielle Belohnung. Alle Boss-Gegner " +
                            "verlieren nach ihrer Zerst\u00F6rung au\u00DFerdem " +
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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Die Steuerung des Helikopters erfolgt ausschlie\u00DFlich mit der Maus:<br>" +
                            "Der Helikopter bewegt sich immer auf den Maus-Cursor " +
                            "zu. " +
                            "Das Bet\u00E4tigen der linken Maustaste bewirkt den " +
                            "Abschuss einer Rakete. " +
                            "Mit der mittleren Maustaste (Mausrad) l\u00E4sst sich der Helikopter wenden. " +
                            "Er schie\u00DFt dann in die entgegengesetzte Richtung.<br>" +
                            "Die Spezialfertigkeit der jeweiligen " +
                            "<font color=\"#FFFFD2\">Helikopter-Klasse" +
                            "<font color=\"#D2D2D2\"> kann mit der rechten Maustaste eingesetzt werden:" +
                            "<br>- Benutzen des <font color=\"#FFFFD2\">Teleporters" +
                            "<font color=\"#D2D2D2\"> (<font color=\"#FFFFD2\">" +
                            "Ph\u00F6nix-Klasse<font color=\"#D2D2D2\">): mit gedr\u00FCckt " +
                            "gehaltener rechter Maustaste<br> " +
                            "&nbsp den Maus-Cursor an einen anderen Ort ziehen und " +
                            "dort die rechte Maustaste l\u00F6sen" +
                            "<br>- Verwenden des <font color=\"#FFFFD2\">Energieschildes" +
                            "<font color=\"#D2D2D2\"> (<font color=\"#FFFFD2\">" +
                            "Roch-Klasse<font color=\"#D2D2D2\">): rechte " +
                            "Maustaste dr\u00FCcken und gedr\u00FCckt halten" +
                            "<br>- <font color=\"#FFFFD2\">Stopp-Rakete" +
                            "<font color=\"#D2D2D2\"> abfeuern (" +
                            "<font color=\"#FFFFD2\">Orochi-Klasse" +
                            "<font color=\"#D2D2D2\">): mit gedr\u00FCckt gehaltener " +
                            "rechter Maustaste eine Rakete abfeuern" +
                            "<br>- Aktivieren der <font color=\"#FFFFD2\">Plasma-" +
                            "Raketen<font color=\"#D2D2D2\"> (" +
                            "<font color=\"#FFFFD2\">Kamaitachi-Klasse" +
                            "<font color=\"#D2D2D2\">): rechte Maustaste dr\u00FCcken" +
                            "<br>- Ausl\u00F6sen einer <font color=\"#FFFFD2\">EMP-" +
                            "Schockwelle<font color=\"#D2D2D2\"> (" +
                            "<font color=\"#FFFFD2\">Pegasus-Klasse" +
                            "<font color=\"#D2D2D2\">): rechte Maustaste dr\u00FCcken" +
                            "<br>- Aktivieren des <font color=\"#FFFFD2\">PU-" +
                            "Generators<font color=\"#D2D2D2\"> (" +
                            "<font color=\"#FFFFD2\">Helios-Klasse" +
                            "<font color=\"#D2D2D2\">): rechte Maustaste dr\u00FCcken" +
                            "<br>Nach dem Landen des Helikopter wird ein Button " +
                            "sichtbar, mit dem der Spieler jederzeit in die " +
                            "<font color=\"#FFFFD2\">Werkstatt" +
                            "<font color=\"#D2D2D2\"> " +
                            "zur\u00FCckkehren kann." +
                            "</font></html>";
                }
                else if(page == BUTTON_6)
                {
                    // PowerUps
                    int lineDistance = graphicsApiType == GraphicsApiType.GRAPHICS_2D ? 3 : 6;
                    int headingMargin = graphicsApiType == GraphicsApiType.GRAPHICS_2D ? 1 : 4;

                    return
                        "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            ".line-distance {font-size: " + lineDistance + "px;}" +
                            ".heading-margin {font-size: " + headingMargin + "px;}" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Einige Gegner verlieren nach Ihrem Abschuss eines der " +
                            "6 folgenden <font color=\"#FFFFD2\">PowerUps" +
                            "<font color=\"#D2D2D2\">: " +

                            wrapWithDivClass("<br><br>", "heading-margin") +
                            "Extra-Bonus" +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            "unendlich Energie f\u00FCr 15 Sekunden" +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            "Teil-Reparatur" +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            Helicopter.INVULNERABILITY_DAMAGE_REDUCTION + "% Unverwundbarkeit f\u00FCr 15 Sekunden " +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            "3-fache Feuerkraft f\u00FCr 15 " + "Sekunden" +

                            wrapWithDivClass("<br><br>", "line-distance") +
                            "erh\u00F6hte Schussrate f\u00FCr 15 Sekunden " +
                            "</font></html>";
                }
                else if(page == BUTTON_8)
                {
                    // Spezial-Modus
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Im <font color=\"#FFFFD2\">Spezial-Modus"
                            + "<font color=\"#D2D2D2\"> erh\u00E4lt der Spieler keine "
                            + "Belohnung f\u00FCr "
                            + "abgeschossene Gegner. Stattdessen wird in diesem "
                            + "Spielmodus jeder Levelaufstieg belohnt. Je "
                            + "erfolgreicher Sie mit den anderen Helikopter-Klassen "
                            + "gespielt haben, desto mehr Geld erhalten Sie. "
                            + "<br><br>Hintergrund: Die Weltregierung unter "
                            + "President Kranijem Su steht der Helios-Klasse sehr "
                            + "skeptisch gegen\u00FCber. 'Woher haben die Konstrukteure "
                            + "ihr Wissen \u00FCber die Verwendung au\u00DFerirdischer "
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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Der Spieler hat die Wahl zwischen " + HelicopterType.count() + " verschiedenen " +
                            "Helikopter-Klassen. " +
                            "Jede dieser 5 Klassen verf\u00FCgt \u00FCber eine einzigartige, " +
                            "energieverbrauchende Fertigkeit " +
                            "sowie \u00FCber mindestens ein <font color=\"#FFFFD2\">" +
                            "Spezial-Upgrade<font color=\"#D2D2D2\">, welches nur " +
                            "von dieser Helikopter-Klasse " +
                            "erworben werden kann. Dar\u00FCber hinaus unterscheiden " +
                            "sich die einzelnen Helikopter-Klassen hinsichtlich des " +
                            "Preises und der Ausbauf\u00E4higkeit der einzelnen " +
                            "<font color=\"#FFFFD2\">Standard-Upgrades" +
                            "<font color=\"#D2D2D2\"> (siehe Abschnitt " +
                            "<font color=\"#FFFFFF\">\"Finanzen/Reparatur\"" +
                            "<font color=\"#D2D2D2\"> sowie <font color=\"#FFFFFF\">" +
                            "\"Upgrades\"<font color=\"#D2D2D2\">). Insbesondere hat " +
                            "jede Helikopter-Klasse eigene M\u00F6glichkeiten an " +
                            "zus\u00E4tzliche finanzielle Belohnungen, die sogenannten <font color=\"#FFFFD2\">Extra-Boni" +
                            "<font color=\"#D2D2D2\">, zu kommen. Diese Extra-Boni werden f\u00FCr " +
                            "besonders eindrucksvolle " +
                            "Abwehrleistungen ausgezahlt. N\u00E4heres hierzu " +
                            "finden Sie in den detaillierten " +
                            "Beschreibungen zu den einzelnen Helikopter-Klassen." +
                            "</font></html>";
                }
                else if(page == BUTTON_3)
                {
                    // Phönix
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Die <font color=\"#FFFFD2\">Ph\u00F6nix-Klasse" +
                            "<font color=\"#D2D2D2\"> ist die robusteste der 5 " +
                            "Helikopter-Klassen, denn f\u00FCr keine andere " +
                            "Helikopter-Klasse k\u00F6nnen die <font color=\"#FFFFD2\">" +
                            "Standard-Upgrades<font color=\"#D2D2D2\"> der " +
                            "<font color=\"#FFFFD2\">Panzerung" +
                            "<font color=\"#D2D2D2\"> sowie das " +
                            "<font color=\"#FFFFD2\">SpezialUpgrade " +
                            "\"Goliathpanzerung\"<font color=\"#D2D2D2\"> so " +
                            "preiswert erworben werden. Die schwere Panzerung " +
                            "bezahlt die Ph\u00F6nix-Klasse allerdings mit einem " +
                            "schwachen <font color=\"#FFFFD2\">Hauptrotor" +
                            "<font color=\"#D2D2D2\">, der sie auch zur langsamsten " +
                            "der 5 verf\u00FCgbaren Helikopter-Klassen macht. Durch " +
                            "einen <font color=\"#FFFFD2\">Teleporter" +
                            "<font color=\"#D2D2D2\">, welcher den " +
                            "Helikopter an einen anderen Ort beamt und ihn f\u00FCr " +
                            "einen kurzen Augenblick " +
                            "unverwundbar macht, wird dieser Nachteil jedoch " +
                            "ausgeglichen. Helikopter der " +
                            "Ph\u00F6nix-Klasse, welche das Spezial-Upgrade " +
                            "<font color=\"#FFFFD2\">\"Nahkampfbestrahlung\"" +
                            "<font color=\"#D2D2D2\"> erworben haben, " +
                            "\u00FCberraschen ihre Gegner mit einer intensiven " +
                            "kurzreichweitigen Strahlung, welche die " +
                            "Au\u00DFenh\u00FClle feindlicher Flugobjekte aufweicht. Die so " +
                            "geschw\u00E4chten Gegner werden bei " +
                            "Kollisionen mit dem Helikopter schwer besch\u00E4digt, " +
                            "w\u00E4hrend der Helikopter selbst weniger " +
                            "Schaden nimmt als gew\u00F6hnlich. Durch Steigerung der " +
                            "<font color=\"#FFFFD2\">Feuerkraft" +
                            "<font color=\"#D2D2D2\"> wird gleichzeitig auch die " +
                            "Intensit\u00E4t der Nahkampfbestrahlung vergr\u00F6\u00DFert. Wenn " +
                            "sich ein Helikopter der Ph\u00F6nix-Klasse " +
                            "an einen anderen Ort teleportiert und dort mit Hilfe " +
                            "der Nahmkampfbestrahlung mehrere " +
                            "Gegner gleichzeitig ausschaltet, dann wird dieses " +
                            "gewagte Flugman\u00F6ver mit einem " +
                            "<font color=\"#FFFFD2\">Extra-Bonus" +
                            "<font color=\"#D2D2D2\"> belohnt. Auch f\u00FCr das " +
                            "unmittelbare Abschie\u00DFen eines Gegners nach Nutzung des " +
                            "Teleporters erh\u00E4lt der Spieler einen Extra-Bonus." +
                            "</b></font></html>";
                }
                else if(page == BUTTON_4)
                {
                    // Roch
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "F\u00FCr die  <font color=\"#FFFFD2\">Roch-Klasse " +
                            "<font color=\"#D2D2D2\"> kann die " +
                            "<font color=\"#FFFFD2\">Feuerkraft" +
                            "<font color=\"#D2D2D2\"> besonders kosteng\u00FCnstig " +
                            "gesteigert und das " +
                            "<font color=\"#FFFFD2\">Spezial-Upgrade \"" +
                            "Durchsto\u00DFsprengk\u00F6pfe\"<font color=\"#D2D2D2\"> sehr " +
                            "preiswert erworben werden. Au\u00DFerdem ist " +
                            "ausschlie\u00DFlich f\u00FCr die Roch-Klasse das Spezial-Upgrade " +
                            "<font color=\"#FFFFD2\">\"Jumbo-Raketen\"" +
                            "<font color=\"#D2D2D2\">, welches den " +
                            "Raketen eine au\u00DFerordentlich gro\u00DFe Sprengkraft " +
                            "verleiht, verf\u00FCgbar. Somit kann " +
                            "die Roch-Klasse <i>in puncto</i> Feuerkraft von keiner " +
                            "anderen Helikopter-Klasse " +
                            "\u00FCbertroffen werden. Die <font color=\"#FFFFD2\">" +
                            "Schussrate<font color=\"#D2D2D2\"> l\u00E4sst allerdings " +
                            "sehr zu w\u00FCnschen \u00FCbrig und " +
                            "auch die schwache <font color=\"#FFFFD2\">Panzerung" +
                            "<font color=\"#D2D2D2\"> stellt ein weiteres Manko " +
                            "dieser Helikopter-Klasse dar. " +
                            "Das  <font color=\"#FFFFD2\">Energie-Schild " +
                            "<font color=\"#D2D2D2\"> der Roch-Klasse hilft " +
                            "allerdings dabei, diesen Nachteil zu " +
                            "kompensieren. Wenn es einem Helikopter der Roch-Klasse " +
                            "gelingt, mehrere " +
                            "Gegner mit derselben Rakete zu zerst\u00F6ren, dann wird " +
                            "diese glorreiche Tat mit " +
                            "einem  <font color=\"#FFFFD2\">Extra-Bonus" +
                            "<font color=\"#D2D2D2\"> belohnt." +
                            "</font></html>";
                }
                else if(page == BUTTON_5)
                {
                    // Orochi
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Helikopter der <font color=\"#FFFFD2\">Orochi-Klasse" +
                            "<font color=\"#D2D2D2\"> sind wahre \"Allrounder\": " +
                            "Sie sind sehr schnell, verf\u00FCgen " +
                            "\u00FCber Raketen mit sehr gutem Antrieb und sind trotzdem " +
                            "ordentlich gepanzert. Zwar kann " +
                            "die <font color=\"#FFFFD2\">Schussrate" +
                            "<font color=\"#D2D2D2\"> dieser Helikopter-Klasse nur " +
                            "begrenzt gesteigert werden, daf\u00FCr kann nur " +
                            "f\u00FCr die Orochi-Klasse eine <font color=\"#FFFFD2\">" +
                            "dritte Bordkanone<font color=\"#D2D2D2\"> sowie eine " +
                            "<font color=\"#FFFFD2\">Radar-Vorrichtung" +
                            "<font color=\"#D2D2D2\"> installiert " +
                            "werden. Mit letzterer k\u00F6nnen getarnte Gegner aufgesp\u00FCrt " +
                            "werden. Bei ausreichend Energie " +
                            "kann diese Helikopter-Klasse au\u00DFerdem " +
                            "<font color=\"#FFFFD2\">Stopp-Raketen" +
                            "<font color=\"#D2D2D2\"> abfeuern, welche getroffene " +
                            "Gegner zur\u00FCck sto\u00DFen und f\u00FCr einen kurzen Augenblick " +
                            "kampfunf\u00E4hig machen. Durch Steigerung des " +
                            "<font color=\"#FFFFD2\">Raketenantriebs" +
                            "<font color=\"#D2D2D2\"> kann die R\u00FCcksto\u00DFwirkung " +
                            "weiter erh\u00F6ht werden. Wenn die " +
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
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Die reine Feuerkraft der <font color=\"#FFFFD2\">" +
                            "Kamaitachi-Klasse<font color=\"#D2D2D2\"> ist sehr " +
                            "gering. Dank der preiswerten " +
                            "Upgrades auf die <font color=\"#FFFFD2\">Schussrate" +
                            "<font color=\"#D2D2D2\"> sowie des nur f\u00FCr die " +
                            "Kamaitachi-Klasse verf\u00FCgbaren " +
                            "<font color=\"#FFFFD2\">Spezial-Upgrades " +
                            "\"Schnellfeuer\"<font color=\"#D2D2D2\"> erreicht diese " +
                            "Helikopter-Klasse allerdings eine enorm " +
                            "hohe Schussrate. Im Zusammenspiel mit den " +
                            "<font color=\"#FFFFD2\">Plasma-Raketen" +
                            "<font color=\"#D2D2D2\"> der Kamaitachi-Klasse, " +
                            "welche die Feuerkraft f\u00FCr 15 Sekunden erheblich erh\u00F6ht, " +
                            "f\u00FChrt dies zu einer " +
                            "gewaltigen Schadenswirkung pro Sekunde. Da die " +
                            "Aktivierung der Plasma-Raketen " +
                            "allerdings Energie verbraucht, k\u00F6nnen sie nicht " +
                            "permanent eingesetzt werden. Der " +
                            "Zeitpunkt ihrer Aktivierung muss daher wohl \u00FCberlegt " +
                            "sein. Wenn es dem " +
                            "Kamaitachi-Piloten gelingt, innerhalb sehr kurzer Zeit " +
                            "eine gro\u00DFe Anzahl feindlicher " +
                            "Flugobjekte abzuschie\u00DFen, dann wird diese Leistung mit " +
                            "einem gro\u00DFz\u00FCgigen <font color=\"#FFFFD2\">Extra-Bonus" +
                            "<font color=\"#D2D2D2\"> " +
                            "belohnt." +
                            "</font></html>";
                }
                else if(page == BUTTON_7)
                {
                    // Pegasus
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Helikopter der <font color=\"#FFFFD2\">Pegasus-Klasse" +
                            "<font color=\"#D2D2D2\"> w\u00E4ren stark benachteiligt, " +
                            "wenn sie sich allein auf ihre " +
                            "Raketen verlassen m\u00FCssten. Dank ihres m\u00E4chtigen " +
                            "<font color=\"#FFFFD2\">EMP-Generators" +
                            "<font color=\"#D2D2D2\"> zur Aussendung " +
                            "elektromagnetischer Schockwellen, welche alle " +
                            "feindlichen Flugobjekte im Umkreis schwer " +
                            "besch\u00E4digen und f\u00FCr einen kurzen Augenblick au\u00DFer " +
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
                            "<font color=\"#D2D2D2\">. Dieser erm\u00F6glicht es dem " +
                            "Helikopter partiell in eine andere Dimension zu " +
                            "gelangen, sobald er eine zeitlang keine Raketen " +
                            "abgefeuert hat. In diesem Zustand zwischen zwei " +
                            "Dimensionen ist der Helikopter gegen\u00FCber jeglichen " +
                            "Angriffen gesch\u00FCtzt. Durch Steigerung der " +
                            "<font color=\"#FFFFD2\">Schussrate" +
                            "<font color=\"#D2D2D2\"> wird ebenfalls die f\u00FCr einen " +
                            "Phasensprung n\u00F6tige Zeit verk\u00FCrzt. Raketen, die w\u00E4hrend " +
                            "eines Phasensprungs " +
                            "abgeschossen werden, befinden sich in einem " +
                            "intermedi\u00E4ren Zustand zwischen zwei " +
                            "Dimensionen und k\u00F6nnen daher die feindliche Panzerung " +
                            "leicht durchdringen und besonders " +
                            "schweren Schaden beim Gegner anrichten. Nach Abschuss " +
                            "einer Rakete f\u00FCllt der Helikopter allerdings wieder in " +
                            "seinen Normalzustand zur\u00FCck." +
                            "</b></font></html>";
                }
                else if(page == BUTTON_8)
                {
                    // Helios
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Mit dem Ziel eine besonders schlagfertige Helikopter-"
                            + "Klasse zu erschaffen, haben die Konstrukteure der "
                            + "<font color=\"#FFFFD2\">Helios-Klasse<font color=\"#D2D2D2\"> ihre Upgrade-Erfahrung mit allen anderen "
                            + "Helikopter-Klassen genutzt. Je erfolgreicher Sie also "
                            + "mit den anderen Helikopter-Klassen gespielt haben, "
                            + "desto geringer fallen die <font color=\"#FFFFD2\">Upgrade-Kosten<font color=\"#D2D2D2\"> f\u00FCr die "
                            + "Helios-Klasse aus. Die Konstrukteure "
                            + "haben nicht einmal davor Halt gemacht, au\u00DFerirdische "
                            + "Technologie aus abgest\u00FCrzten Flugobjekten zu verbauen: "
                            + "Ein <font color=\"#FFFFD2\">PowerUp-Stopper<font color=\"#D2D2D2\"> hilft dabei, die Bewegung von "
                            + "PowerUps zu kontrollieren, was ihr Einsammeln "
                            + "erleichtert. Unter gro\u00DFem Energieaufwand k\u00F6nnen diese "
                            + "Helikopter au\u00DFerdem einen <font color=\"#FFFFD2\">PU-Generator<font color=\"#D2D2D2\"> zur Erzeugung "
                            + "von PowerUps nutzen. Die Helios-Klasse l\u00E4sst sich nur "
                            + "im <font color=\"#FFFFD2\">Spezial-Modus<font color=\"#D2D2D2\"> spielen."
                            + "</font></html>";
                }
            }
            else if(window  == CONTACT)
            {
                if(page == BUTTON_1)
                {
                    return
                            "<html><head><style>" +
                            "body { font-size: " + fontSpecification.getSize() + "px; font-family: Dialog; color: #D2D2D2; }" +
                            "</style></head><body>" + fontSpecification.boldString() +

                            "Du hast neue Ideen oder Verbesserungsvorschl\u00E4ge f\u00FCr " +
                            "HelikopterDefence?<br> Dann schreibe eine E-Mail an: " +
                            "<font color=\"#FFFFD2\">info@HelicopterDefence.de" +
                            "<font color=\"#D2D2D2\"> " +
                            "<br><br>Gerne nehme ich auch \u00DCbersetzunghilfen an, " +
                            "falls du deine Muttersprache " +
                            "in der Sprachauswahl vermisst und behilflich sein " +
                            "m\u00F6chtest. <br><br>Ich freue mich darauf, von dir zu " +
                            "h\u00F6ren. <br><br>Viele Gr\u00FC\u00DFe" +
                            "<br>Bj\u00F6rn Hansen" +
                            "</font></html>";
                }
            }
        }
        return "";
    }

    private static String wrapWithDivClass(String text, String className) {
        return "<div class=\"" + className + "\">" + text + "</div>";
    }
}
