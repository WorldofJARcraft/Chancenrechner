/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chancenrechner;

import javafx.scene.image.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Eric Ackermann
 */
public class Chancenrechner_GUIController implements Initializable {

    //statische Variablen:
    //JavaFX GUI-Elemente
    @FXML
    private Label label;
    @FXML
    private TextField Schuesse;
    @FXML
    private TextField Ziel;
    @FXML
    private Label Label_1;
    @FXML
    private Label Label_3;
    @FXML
    private TextArea Schuetze;
    @FXML
    private Button Löschen;
    @FXML
    private Pane flaeche;
    @FXML
    private ImageView imageview;
    @FXML
    private TextField Zeitanzeiger;
    @FXML
    private RadioButton Gewehr;
    @FXML
    private RadioButton Pistole;
    @FXML
    private Button ueber;

    //Systemvariablen
    //Leinwand
    javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(815, 680);
    //Zeichner
    GraphicsContext g = canvas.getGraphicsContext2D();
    //systemeigenen Zeilenumbruch auslesen, um im Protokoll auch unter Windows, Linux, Solaris... Zeilen umbrechen zu können
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    //Zeitberechnung

    //die Zeitspeichervariable
    public static int zeit = 12 * 60;
    //speichert, wie lange wirklich geschossen wurde
    static int gesamtzeit;
    //der Zeitgeber
    static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    //der eigentliche Timer
    static ScheduledFuture<?> result;
    //die vom Timer sekündlich auszuführende Aktion:
    public Runnable TimerTask = new Runnable() {

        public void run() {
            //um verzögern und erneut aufrufen zu können
            Platform.runLater(new Runnable() {
                public void run() {
                    //wenn zeit um ist:
                    if (zeit < 1) {
                        //Information geben
                        Zeitanzeiger.setText("Zeit abgelaufen!");
                        Zeitanzeiger.setStyle("-fx-font-size: 20pt;\n"
                                + "-fx-text-fill: red;");
                        //keine Zeit mehr übrig
                        zeit--;
                    } else {
                        //übrige Zeit aktualisieren...
                        zeit--;
                        //und in Minuten und sekunden getrennt...
                        int minuten, sekunden;
                        minuten = (int) ((zeit) / (60));
                        sekunden = zeit - minuten * 60;
                        //... in die Zeitanzeige schreiben
                        Zeitanzeiger.setText("noch " + minuten + " min " + sekunden + " s");
                    }
                }
            });
        }
    };
    //Programmvariablen

    //booleans
    //ist true, wenn gerade ein Wettkampf simuliert wird, sonst false
    static boolean laeuft = false;
    //ist true, wenn Name, Schüsse u.a. eingegeben sind, sonst false
    static boolean initialisiert = false;
    //Optimum wurde gerade neu berechnet
    static boolean frischgenullt,
            //ungültige Eingabe        
            falsch = true,
            //Ringvorgabe nicht mehr erreichbar
            nichtmehrzuerreichen = false,
            //eine empfohlene Ringzahl wurde geschossen
            nurabgearbeitet,
            //erster Aufruf des voids "Empfehlungen" --> Empfehlungs-Array noch nicht wieder initialisiert
            erstesmal = true;
    boolean db;
    //Integers
    //zahl des aktuellen Schusses
    static int schuss = 0,
            //Gesamtschuesse
            schuesse,
            //Ringvorgabe
            ziel,
            //der aktuelle Schuss
            aktuell,
            //der angepeilte Durchschnitt
            optimum,
            //Zahl eingegebener Schuesse
            echteschuesse;
    //Zahl des aktuellen Schusses als Index für die Berechnung
    static int i,
            //insgesamt erzielte Ringzahl        
            gesamt,
            //Zahl der Ringe, die die aktuelle Empfehlung über das anvisierte Ziel führt
            Guthaben,
            //Summe der nach Empfehlungen noch neu zu schießender Ringe
            zwischensumme,
            //zum Auslesen der Empfehlungen nötig
            j;
    //speichert, welche Scheibe auf dem Bildschirm dargestellt wird
    static int Scheibe = 0;
    //der letzte Schuss
    static int letzter;
    //die wievielte Scheibe gerade gezeigt wird (alle 10 Schuss wird der Inhalt der aktuellen Scheibe gespeichert und diese geleert)
    static int nummer = 1;
    
    //doubles
    //speichern Koordinaten des letzten Schusses, damit dieser umgefärbt werden kann
    double letzterx, letztery;
    
    //Arrays
    //jeweils erzielte Ringzahlen
    static int[] ergebnisse,
            //Empfehlungen werden hier gespeichert
            zahl;

    //Programm-Strings
    //hier wird das Programm gespeichert
    static String Protokoll = "";
    //Name des Schützen
    public static String name = null;
    String passwort;
    String Startzeit;

    //Initialisierungsvoid: wird aufgerufen, wenn das Fenster geöffnet wird und sich aufbaut
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //Zeichenfläche an Bild der Zielscheibe heften
        flaeche.getChildren().add(canvas);
        // Aufruf der statischen Methode showMessageDialog()
        JOptionPane.showMessageDialog(null,
                "Programm Chancenrechner" + LINE_SEPARATOR + "Version 0.9" + LINE_SEPARATOR + "entwickelt von Eric Ackermann," + LINE_SEPARATOR
                + "Carl-Zeiss-Gymnasium Jena" + LINE_SEPARATOR + "betreut durch Herrn Otto Thiele und Herrn Bert Kunze" + LINE_SEPARATOR
                + "in enger Zusammenarbeit mit der Bürgeler Sportschützengesellschaft e.V." + LINE_SEPARATOR
                + "Beta-Programmversion, Nutzung auf eigene Gefahr!",
                "Über Chancenrechner",
                JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * die Methode, die dem Start/Stoppknopf zugeordnet ist
     *
     * @param event Knopf wurde gedrückt
     */
    @FXML
    private void Löschen(ActionEvent event) {
        Platform.runLater(() -> {
            Zeitanzeiger.setStyle("-fx-font-size: 20pt;\n"
                    + "-fx-text-fill: green;");
            //Ausgangsbedingungen: Programm noch nicht im benutzbaren Zustand
            if (!laeuft && !initialisiert) {
                //Vorbereitungen zum Ablauf treffen

                //Datenbank vorbereiten
                //Eintrag aus Datenbank entfernen
                if (name != null) {
                    if (db) {
                        //MySQLConnection.löschen(name);
                    }
                }
                //Variablen auf ihre Startwerte zurücksetzen
                //Integers
                nummer = 1;
                echteschuesse = 0;
                gesamt = 0;
                i = 0;
                gesamt = 0;
                Guthaben = 0;
                schuss = 0;

                //booleans
                initialisiert = true;
                falsch = true;
                nichtmehrzuerreichen = false;
                erstesmal = true;

                //Strings
                Protokoll = "";

                //Oberflächenelemente
                Schuetze.setText("");
                Löschen.setText("Start");

                //Zeichenfläche löschen
                g.clearRect(0, 0, 815, 680);
                //Vorgabe aus Textfeld auslesen
                ziel = Integer.parseInt(Ziel.getText());
                //Schusszahl einlesen
                schuesse = Integer.parseInt(Schuesse.getText());
                /*Object[] options = {"Ja", "Nein"};
                int DB = JOptionPane.showConfirmDialog(null,
                        "Möchten Sie ein Smartphone über die App verbinden?",
                        "Smartphone anbinden?",
                        JOptionPane.YES_NO_OPTION);
                if (DB == JOptionPane.NO_OPTION) //Name einlesen
                {*/
                    this.db = false;
                    name = JOptionPane.showInputDialog(null, "Bitte geben Sie Name oder ID des Schützen ein.", "Schütze eingeben", JOptionPane.OK_OPTION);
                /*} else {
                    db = true;
                    // Erstellung Array vom Datentyp Object, Hinzufügen der Komponenten		
                    JTextField bname = new JTextField();
                    JTextField bpasswort = new JPasswordField();
                    Object[] message = {"Benutzername", bname,
                        "Passwort", bpasswort};

                    JOptionPane pane = new JOptionPane(message,
                            JOptionPane.PLAIN_MESSAGE,
                            JOptionPane.DEFAULT_OPTION);
                    pane.createDialog(null, "Log-in-Daten").setVisible(true);
                    this.name = bname.getText();
                    while (this.name.equals("")) {
                        this.name = JOptionPane.showInputDialog(null, "Bitte geben Sie einen gültigen Benutzernamen ein.", "Benutzername eingeben", JOptionPane.OK_OPTION);

                    }
                    
                    while(MySQLConnection.checkname(name)){
                        name = JOptionPane.showInputDialog(null, "Benutzername schon vorhanden. Bitte geben Sie einen anderen ein.", "Benutzername eingeben", JOptionPane.DEFAULT_OPTION);
                    }
                    this.passwort = bpasswort.getText();
                    Object[] newMessage = {"Bitte geben Sie ein Passwort ein.", bpasswort};
                    while (this.passwort.equals("")) {
                        pane = new JOptionPane(newMessage,
                            JOptionPane.PLAIN_MESSAGE,
                            JOptionPane.DEFAULT_OPTION);
                    pane.createDialog(null, "Passwort eingeben").setVisible(true);
                    passwort = bpasswort.getText();

                    }
                }*/
                //Eingaben protokollieren
                Protokoll += "Bitte geben Sie Name oder ID des Schützen ein. " + name + LINE_SEPARATOR;
                Protokoll += "Schuesse: " + schuesse + LINE_SEPARATOR;
                Protokoll += "Vorgabe: " + ziel + LINE_SEPARATOR;
                //Prüfung auf Eingabefehler
                while (schuesse < 1) {
                    //solange keine gültige Schusszahl eingegeben wird, neu abfragen
                    schuesse = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie eine gültige Schusszahl ein.", "Schusszahl ungültig", JOptionPane.OK_OPTION));
                    //protokollieren
                    Protokoll += "Bitte geben Sie eine gültige Schusszahl ein. " + schuesse + LINE_SEPARATOR;
                }
                //angezeigte Schusszahl ggf. korrigieren
                Schuesse.setText("" + schuesse);
                //prüfen, ob Vorgabe erreichbar ist
                while (ziel < 0 || schuesse * 10 < ziel) {
                    ziel = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie ein gültiges Ziel ein.", "Ziel ungültig", JOptionPane.OK_OPTION));
                    Protokoll += "Bitte geben Sie ein gültiges Ziel ein. " + ziel + LINE_SEPARATOR;
                }
                //Ziel korrigieren
                Ziel.setText("" + ziel);

                //Arrays mit eingelesenen Werten auffüllen
                zahl = new int[11];
                ergebnisse = new int[schuesse];

                //anzupeilenden Durchschnittswert berechnen --> immer aufrunden, um nie unter die Vorgabe zu leiten
                optimum = (int) Math.ceil((float) ((float) ziel / (float) schuesse));
                //exakten Wert des Ziels ausgeben
                Schuetze.setText("Das Ziel: durschschnittlich " + ((float) ((float) ziel / (float) schuesse)) + " Ringe." + LINE_SEPARATOR);
                Protokoll += "Das Ziel: durschschnittlich " + ((float) ((float) ziel / (float) schuesse)) + " Ringe."
                        + LINE_SEPARATOR;
                //Zeit ermitteln: festgelegte Zeiten einhalten
                switch (schuesse) {
                    //5 Schuss => 6 Minuten,
                    case 5:
                        zeit = 6 * 60;
                        break;
                    //10 Schuss => 12 Minuten,	
                    case 10:
                        zeit = 15 * 60;
                        break;
                    //20 Schuss => 30 Minuten,	
                    case 20:
                        zeit = 30 * 60;
                        break;
                    //30 Schuss => 40 Minuten,	
                    case 30:
                        zeit = 40 * 60;
                        break;
                    //40 Schuss => 60 Minuten    
                    case 40:
                        zeit = 60 * 60;
                        break;
                    //sonst: 1 Schuss pro Minute				
                    default:
                        zeit = schuesse * 60;
                        break;
                    //Angaben jeweils in Millisekunden	
                }
                gesamtzeit = zeit;
                //verfügbare Zeit in Minuten und Sekunden aufsplitten...
                int minuten, sekunden;
                minuten = (int) ((zeit) / (60));
                sekunden = zeit - minuten * 60;
                //... und im vorgesehenen Textfeld ausgeben
                Zeitanzeiger.setText("noch " + minuten + " min " + sekunden + " s");
                Protokoll += minuten + " Minuten " + sekunden + " Sekunden übrig" + LINE_SEPARATOR;
                //Datenbankeintrag anlegen
                if (db) {
                    //MySQLConnection.insertName(name, passwort, Schuetze.getText(), gesamtzeit);
                }
                //initialisiert, aber noch nicht gestartet --> Simulation starten
            } else if (!laeuft) {
                //Programm mitteilen, dass es gestartet wurde
                laeuft = true;
                //Timer starten
                result = exec.scheduleAtFixedRate(TimerTask, 0, 1000, TimeUnit.MILLISECONDS);
                //Start-Button wird zu Stopp-Button
                Löschen.setText("Stopp");
                //Datenbank auf aktualisieren
                //aktuelle Zeit organisieren und in SQL-Zeit umwandeln
                Calendar cal = Calendar.getInstance();
                Startzeit = cal.get(Calendar.YEAR)
                        + "-" + (cal.get(Calendar.MONTH) + 1)
                        + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                        + cal.get(Calendar.MINUTE) + ":"
                        + cal.get(Calendar.SECOND);
                if (db) {
                   // MySQLConnection.updateName(name, Startzeit, Schuetze.getText());
                }
            } //Abbruch der aktuellen Simulation
            else {
                //zurücksetzen auf Urzustand
                Löschen.setText("Initialisieren");
                initialisiert = false;
                laeuft = false;
                //Timer stoppen
                result.cancel(false);
                //Eintrag aus DB entfernen
                if (db) {
                   // MySQLConnection.löschen(name);
                }
            }
        });
    }
    //Damit nicht beide Zielscheiben-RadioButtons parallel ausgewählt sein können
    @FXML
    static ToggleGroup group = new ToggleGroup();

    /**
     * Die Haupteingabemethode; wird aufgerufen, wenn der Nutzer auf die
     * Zielscheibe klickt und liest den Wert des Schusses ein
     *
     * @param event wohin geklickt wurde
     */
    @FXML
    @SuppressWarnings("empty-statement")
    private void mausklick(javafx.scene.input.MouseEvent event) {
        Platform.runLater(() -> {
            //wenn das Programm läuft, Eingaben annehmen
            if (laeuft) {
                //ein (weiterer) Schuss wurde eingegeben
                echteschuesse++;
                //alle 10 Scheiben...
                if ((echteschuesse - 1) % 10 == 0 && echteschuesse != 1) {
                    //... diese speichern und Löschen
                    //Name auf Gültigkeit überprüfen
                    if (!name.equals("") && name != null) {
                    } else {
                        name = "anonym";
                    }
                    //Calender ermittelt Datum und Uhrzeit
                    Calendar cal = Calendar.getInstance();
                    //Dateinamen des Protokolls zusammensetzen
                    String fileName = "Protokoll_" + name + "_" + "Scheibe_" + nummer + "_"
                            + cal.get(Calendar.DAY_OF_MONTH)
                            + "-" + (cal.get(Calendar.MONTH) + 1)
                            + "-" + cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.HOUR_OF_DAY) + "-"
                            + cal.get(Calendar.MINUTE) + "-"
                            + cal.get(Calendar.SECOND);

                    //JPanel, welches Zielscheibe und Zeichenfläche enthält, abfotografieren 
                    WritableImage image = flaeche.snapshot(new SnapshotParameters(), null);

                    //neue Datei erstellen...
                    File file = new File(System.getProperty("user.home") + "/Desktop/" + fileName + ".png");
                    System.out.println(System.getProperty("user.home") + "/Desktop/" + fileName + ".png");
                    try {
                        //... und das erstellte Bild hineinschreiben
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    } catch (IOException e) {
                        // Speichern schiefgegangen
                        JOptionPane.showMessageDialog(null, "Beim Speichern des Bildes trat ein Fehler auf.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                    //Zeichenfläche leeren
                    g.clearRect(0, 0, 815, 680);
                }
                //vorletzten Schuss wie die anderen Schüsse auch überzeichnen => nur letzten Schuss hervorheben
                if(schuss >= 1){
                    //Zeichenfarbe einstellen: gelber Hintergrund
                g.setFill(javafx.scene.paint.Color.YELLOW);
                g.setStroke(javafx.scene.paint.Color.YELLOW);
                //Breite der Linien in Pixeln
                g.setLineWidth(5);
                //Oval um den Ort, wo hingeklickt wurde, malen
                g.fillOval(letzterx, letztery, 22, 22);
                //Nummer des Schusses mittig in das Oval zeichnen
                //Zeichenfarbe: blaue Schrift --> blaue Schrift auf gelbem Hintergrund in allen Bereichen der Scheibe (schwarz wie weiß) lesbar
                g.setFill(javafx.scene.paint.Color.BLUE);
                g.setStroke(javafx.scene.paint.Color.BLUE);
                g.fillText((schuss) + "", letzterx+7.5-1, letztery+7.5+8);
                }
                //letzten Schuss zeichnen
                //Zeichenfarbe einstellen: roter Hintergrund
                g.setFill(javafx.scene.paint.Color.RED);
                g.setStroke(javafx.scene.paint.Color.RED);
                //Breite der Linien in Pixeln
                g.setLineWidth(5);
                //Oval um den Ort, wo hingeklickt wurde, malen
                g.fillOval(event.getX() - 7.5, event.getY() - 7.5, 22, 22);
                //neuer Schuss eingegeben
                schuss++;
                //Nummer des Schusses mittig in das Oval zeichnen
                //Zeichenfarbe: weiße Schrift --> auf rotem Hintergrund überall lesbar
                g.setFill(javafx.scene.paint.Color.WHITE);
                g.setStroke(javafx.scene.paint.Color.WHITE);
                g.fillText(schuss + "", event.getX() - 1, event.getY() + 8);
                //Koordinaten des letzten Schusses merken
                letzterx = event.getX() - 7.5;
                letztery = event.getY() - 7.5;
                //Kontrollausgabe in die Konsole
                //Koordinaten der Mitte der Scheiben
                double MitteX = 350 + 3, MitteY = 350 + 7;
                //X- und Y- Wert des Klicks ausrechnen
                double X = event.getX(), Y = event.getY();
                //Differenzen zwischen X- und Y- Werten des Schusses und X- und Y- Werten der Mitte bilden...
                X = Math.abs(X - MitteX);
                Y = Math.abs(Y - MitteY);
                //... und daraus den Abstand berechnen (Kreissatz)
                double Abstand = Math.abs(Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)));
                //überprüfen, welche Scheibe vorliegt...
                if (Scheibe == 0) {
                    //... und dem Abstand einen Schusswert zuordnen
                    if (Abstand < 6) {
                        aktuell = 10;
                    } else if (Abstand < 42) {
                        aktuell = 9;
                    } else if (Abstand < 78) {
                        aktuell = 8;
                    } else if (Abstand < 116) {
                        aktuell = 7;
                    } else if (Abstand < 153) {
                        aktuell = 6;
                    } else if (Abstand < 191) {
                        aktuell = 5;
                    } else if (Abstand < 227) {
                        aktuell = 4;
                    } else if (Abstand < 265) {
                        aktuell = 3;
                    } else if (Abstand < 305) {
                        aktuell = 2;
                    } else if (Abstand < 341) {
                        aktuell = 1;
                    } else {
                        aktuell = 0;
                    }
                }
                if (Scheibe == 1) {
                    if (Abstand < 33) {
                        aktuell = 10;
                    } else if (Abstand < 69) {
                        aktuell = 9;
                    } else if (Abstand < 104) {
                        aktuell = 8;
                    } else if (Abstand < 136) {
                        aktuell = 7;
                    } else if (Abstand < 172) {
                        aktuell = 6;
                    } else if (Abstand < 205) {
                        aktuell = 5;
                    } else if (Abstand < 240) {
                        aktuell = 4;
                    } else if (Abstand < 274) {
                        aktuell = 3;
                    } else if (Abstand < 308) {
                        aktuell = 2;
                    } else if (Abstand < 340) {
                        aktuell = 1;
                    } else {
                        aktuell = 0;
                    }
                }
                //Kontrollausgabe
                System.out.println("Eingabe: " + aktuell);
                //bei Fehlern Korrektur der Eingabe ermöglichen
                int korrektur;
                korrektur = JOptionPane.showConfirmDialog(null,
                        "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                        "Eingabe bestätigen",
                        JOptionPane.YES_NO_OPTION);
                if (korrektur == JOptionPane.NO_OPTION) {
                    aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die korrekte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                }
                while (aktuell < 0 || aktuell > 10) {
                    aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die korrekte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                }
                //protokollieren
                Protokoll += LINE_SEPARATOR + "Schuss " + (echteschuesse) + ": " + aktuell + LINE_SEPARATOR;
                //letzter Schuss eingegeben
                if (schuss >= schuesse) {
                    //Rücksetzen des Programms
                    Löschen.setText("Initialisieren");
                    initialisiert = false;
                    laeuft = false;
                    //Timer abbrechen
                    result.cancel(false);
                    //aktuellen Wert speichern
                    gesamt += aktuell;
                    //Protokoll ausgeben
                    if (db) {
                        //MySQLConnection.updateName(name, Startzeit, Schuetze.getText());
                    }
                    protokollieren();
                } else {
                    //Empfehlungen geben
                    Empfehlungen();
                }
                //Programm nicht in Wettkampfsimulation    
            } else {
                //Abstände auf Konsole ausgeben, z.B. um Programm für eine neue Scheibe zu kalibrieren
                double MitteX = 350 + 6, MitteY = 350 + 5;
                double X = event.getX(), Y = event.getY();
                X = (X - MitteX);
                Y = (Y - MitteY);
                double Abstand = Math.abs(Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)));
                System.out.println("Abstand zur Mitte: " + Abstand + ", dx= " + X + ", dy= " + Y);
            }
        });
    }

    //Protokoll in Bild- und Textform speichern
    private void protokollieren() {
        Platform.runLater(() -> {
            //Timer abbrechen
            result.cancel(false);
            //Datum und Uhrzeit ermitteln
            Calendar cal = Calendar.getInstance();
            String datum = cal.get(Calendar.DAY_OF_MONTH)
                    + "." + (cal.get(Calendar.MONTH) + 1)
                    + "." + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                    + cal.get(Calendar.MINUTE) + ":"
                    + cal.get(Calendar.SECOND);
            //Schriftfarbe: schwarz
            g.setFill(javafx.scene.paint.Color.BLACK);
            g.setStroke(javafx.scene.paint.Color.BLACK);
            g.setLineWidth(5);
            //Name auf Gültigkeit prüfen, Ziel, datum und Schusszahl in linke obere Ecke der Zielscheibe schreiben
            if (!name.equals("") && name != null) {
                g.fillText("Protokoll von " + name + "\nam " + datum + "\nVorgabe: " + ziel + " Ringe\nbei " + schuesse + " Schuss", 50, 50);
            } else {
                g.fillText("Anonymes Protokoll\nam " + datum + "\nZiel: " + ziel + " Ringe" + LINE_SEPARATOR + "bei " + schuesse + " Schuss", 50, 50);
                name = "anonym";
            }
            //Gesamtpunkte und nötige Zeit ausgeben
            Schuetze.setText("");
            String Bewertung = null;
            //prüfen, ob Vorgabe erreicht
            if (gesamt >= ziel) {
                Bewertung = "Vorgabe erreicht.";
            } else {
                Bewertung = "Vorgabe nicht erreicht.";
            }
            //temporäre Variable zur Ermittlung der nötigen Zeit
            int temp;
            //genutzte Zeit: Differenz aus Gesamtzeit und übriger Zeit ermitteln
            temp = gesamtzeit - zeit;
            int minuten, sekunden;
            minuten = (int) ((temp) / (60));
            sekunden = (temp) - minuten * 60;
            //Schussstatistik protokollieren
            g.fillText("Sie haben " + gesamt + " Ringe erzielt und\ndafür " + minuten + " Minuten und " + sekunden + " Sekunden\ngebraucht.\n"
                    + "Sie haben durchschnittlich " + (float) ((float) gesamt / (float) (echteschuesse)) + "\nRinge erzielt.\n"
                    + Bewertung, 620, 50);
            Protokoll += LINE_SEPARATOR + "Sie haben " + gesamt + " Ringe erzielt und dafür " + minuten + " Minuten und " + sekunden + " Sekunden gebraucht." + LINE_SEPARATOR
                    + "Sie haben durchschnittlich " + (float) ((float) gesamt / (float) (echteschuesse)) + " Ringe erzielt." + LINE_SEPARATOR + Bewertung;
            Schuetze.setText("Sie haben " + gesamt + " Ringe erzielt und\ndafür " + minuten + " Minuten und " + sekunden + " Sekunden\ngebraucht.\n"
                    + "Sie haben durchschnittlich " + (float) ((float) gesamt / (float) (echteschuesse)) + "\nRinge erzielt.\n"
                    + Bewertung);
            //an Android-Gerät übermitteln
            if (db) {
                //MySQLConnection.updateName(name, Startzeit, Schuetze.getText());
            }
            //Bildprotokoll speichern
            String fileName = "Protokoll_" + name + "_"
                    + cal.get(Calendar.DAY_OF_MONTH)
                    + "-" + (cal.get(Calendar.MONTH) + 1)
                    + "-" + cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.HOUR_OF_DAY) + "-"
                    + cal.get(Calendar.MINUTE) + "-"
                    + cal.get(Calendar.SECOND);
            //Inhalt des Panels, das die Zielscheibe enthält, abfotografieren 
            WritableImage image = flaeche.snapshot(new SnapshotParameters(), null);
            //Bild auf Desktop speichern
            File file = new File(System.getProperty("user.home") + "/Desktop/" + fileName + ".png");
            System.out.println(System.getProperty("user.home") + "/Desktop/" + fileName + ".png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                //Fehlermeldung geben
                JOptionPane.showMessageDialog(null, "Beim Speichern des Bildes trat ein Fehler auf.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
            //schriftliches Protokoll ausgeben
            PrintWriter pWriter = null;
            try {
                //Protokoll in Datei auf dem Desktop schreiben
                pWriter = new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/" + fileName + ".txt")));
                System.out.println(System.getProperty("user.home") + "/Desktop/" + fileName + ".txt");
                pWriter.println(Protokoll);
            } catch (IOException ioe) {
                //Fehlermeldung
                JOptionPane.showMessageDialog(null, "Beim Speichern des Textprotokolls trat ein Fehler auf.", "Fehler", JOptionPane.ERROR_MESSAGE);
                ioe.printStackTrace();
            } finally {
                //Schreiber wieder schließen
                if (pWriter != null) {
                    pWriter.flush();
                    pWriter.close();
                }
            }
        });
    }

    //gibt eine Empfehlung, wie man schießen soll, um das Ziel möglichst entspannt zu erreichen
    private void Empfehlungen() {
        Platform.runLater(() -> {
            if (erstesmal) {
                erstesmal = false;
                zahl[optimum] = schuesse;
            }
            //neuer Schuss, also muss Berechnung ausgeführt werden
            frischgenullt = falsch = false;
            //Einlesen des Wertes für den Schuss, bis dieser im Bereich 0-10 liegt
            //damit nächster Schuss eingelesen werden kann
            falsch = true;
            //aktuellen Wert Gesamtwert hinzufügen
            gesamt += aktuell;
            //Ergebnis zusätzlich einzeln protokollieren
            ergebnisse[i] = aktuell;
            //Protokoll anzeigen=Schüsse einzeln ausgeben
            Schuetze.setText("Gesamtringzahl: ");
            Protokoll += "Gesamtringzahl: ";
            //for (int j = 0; j <= i; j++) {
            Schuetze.appendText(gesamt + ";");
            Protokoll += gesamt + ";";
            letzter = aktuell;
            if (echteschuesse != 1) {
                Schuetze.appendText("\nLetzter Schuss: " + letzter);
                Protokoll += LINE_SEPARATOR + "Letzter Schuss: " + letzter;
            }
            //}
            //wenn Ziel frühzeitig erreicht, Meldung anzeigen
            if (gesamt >= ziel) {
                //cout << "\n Mit " << gesamt << " Ringen haben Sie Ihr Ziel von " << ziel << " Punkten erreicht. Ihnen stehen noch " << (schuesse - (i + 1)) << " Schuesse zur Verfuegung.";
                //Protokoll += "\n Mit " + NumberToString(gesamt) + " Punkten haben Sie Ihr Ziel von " + NumberToString(ziel) + " Punkten erreicht. Ihnen stehen noch " + NumberToString(schuesse - (i + 1))
                // + " Schuesse zur Verfuegung.";
                //keine Empfehlungen mehr geben, da jetzt sinnlos 
                nichtmehrzuerreichen = true;
                //sonst: zur Kontrolle und Übersicht Gesamtpunktzahl und noch zu erreichende Treffer ausgeben    
            } else if (!nichtmehrzuerreichen) {
                //cout << "\nBisherige Gesamtringzahl: " << gesamt;
                //Protokoll+="\nBisherige Gesamtringzahl: " + NumberToString(gesamt);

                //Empfehlungen nicht beim letzten Schuss oder wenn keine mehr nötig
                if (!((i == schuesse - 1) || nichtmehrzuerreichen)) {

                    //damit Rechnung durchgeführt werden kann
                    nurabgearbeitet = false;
                    //Fall 1: weniger als gezielt geschossen
                    if (aktuell < optimum) {
                        //Guthaben anwenden, um aktuell auf Optimum zu erhöhen => Optimum-Zahl wird später reduziert
                        if (aktuell + Guthaben >= optimum) {
                            Guthaben -= optimum - aktuell;
                        } //Programm muss Empfehlung zum Ausgleich geben
                        else {
                            //zahl[optimum] verringern, da ihr Wert Teil des neuen Vorschlages ist, aber nur, wenn zahl[optimum] größer als 0 ist
                            //zahl[optimum] nicht verringern, wenn der aktuelle Wert zu schießen war
                            if (zahl[optimum] > 0 && zahl[aktuell] <= 0) {
                                zahl[optimum]--;
                            }
                            //wenn der aktuelle Wert zu schießen war, dessen Quantität verringern
                            if (zahl[aktuell] > 0) {
                                zahl[aktuell]--;
                                nurabgearbeitet = true;
                                //jetzt kommt der schwere Teil    
                            } else {
                                //Rücksetzen der Mehrzweck-Variable
                                zwischensumme = 0;
                                //zählen, wie viele Schüsse noch zu schießen sind
                                for (int j = 0; j < 11; j++) {
                                    zwischensumme += zahl[j];
                                }
                                //Spiegelzahl des aktuellen Wertes finden und erhöhen, falls dafür genügend Schüsse übrig sind
                                if (optimum + (optimum - aktuell) >= 0 && optimum + (optimum - aktuell) <= 10 && zwischensumme + 1 <= (schuesse - (i + 1))) {
                                    zahl[optimum + (optimum - aktuell)]++;
                                } else {
                                    //Sonst Optimum neu berechnen
                                    //alle Empfehlungen zurücksetzen
                                    for (int j = 0; j < 11; j++) {
                                        zahl[j] = 0;
                                    }
                                    //neues Optimum: noch zu erreichende Punkte / übrige Schüsse
                                    //zwingend aufrunden ab der kleinsten Kommastelle, da bei Abrunden Optimum zu klein 
                                    optimum = (int) Math.ceil((((float) (((float) ziel - (float) gesamt) / ((float) schuesse - ((float) i + 1)))) + (float) 0));
                                    //Optimum zurückgesetzt => keine Aktion durch Fall 2, diese wäre unsinnig, da nötige Aktion bereits getan
                                    Guthaben = optimum * (schuesse - (i + 1)) - (ziel - gesamt);
                                    //cout << "\nGuthaben: "<< Guthaben;
                                    frischgenullt = true;
                                    //Abbruch, wenn Ziel nicht mehr erreichbar ist
                                    if (optimum > 10) {
                                        //cout << "\nZiel nicht mehr erreichbar.";
                                        //Protokoll+= "\nZiel nicht mehr erreichbar.";
                                        nichtmehrzuerreichen = true;
                                        //Sicherung
                                        //protokollieren(Protokoll,name);
                                        //sonst: Rest der Schüsse auf Optimum setzen
                                    } else {
                                        zahl[optimum] = schuesse - (i + 1);
                                    }

                                }
                            }
                        }
                    }
                    //Fall 2: mehr als gezielt geschossen
                    //Vorgehen nahezu identisch wie bei Fall 1
                    if (aktuell > optimum && !frischgenullt) {
                        if (zahl[optimum] > 0 && zahl[aktuell] <= 0) {
                            zahl[optimum]--;
                        }
                        if (zahl[aktuell] > 0) {
                            zahl[aktuell]--;
                            nurabgearbeitet = true;
                        } else {
                            zwischensumme = 0;
                            for (int j = 0; j < 11; j++) {
                                zwischensumme += zahl[j];
                            }
                            if (optimum - (aktuell - optimum) >= 0 && optimum - (aktuell - optimum) <= 10 && zwischensumme + 1 <= (schuesse - (i + 1))) {
                                zahl[optimum - (aktuell - optimum)]++;
                            } else {
                                for (int j = 0; j < 11; j++) {
                                    zahl[j] = 0;
                                }
                                optimum = (int) Math.ceil((((float) (((float) ziel - (float) gesamt) / ((float) schuesse - ((float) i + 1)))) + (float) 0));
                                Guthaben = optimum * (schuesse - (i + 1)) - (ziel - gesamt);
                                frischgenullt = true;
                                if (optimum > 10) {
                                    //cout << "\nZiel nicht mehr erreichbar.";
                                    //Protokoll+= "\n Ziel nicht mehr erreichbar.";
                                    //protokollieren(Protokoll,name);
                                    nichtmehrzuerreichen = true;

                                } else {
                                    zahl[optimum] = schuesse - (i + 1);
                                }
                            }
                        }
                    }
                    //Wenn noch Optimum-Werte zu schießen sind, nicht nur eine Empfehlung erfüllt wurde und das Optimum nicht neu berechnet wurde
                    if (zahl[optimum] > 0 && !nurabgearbeitet && !frischgenullt) {
                        zahl[optimum]--;
                    }
                    //Array sortieren
                    //zahl = sortieren(zahl, schuesse - i, optimum);
                    //Empfehlungen sinnvoll ausgeben
                    //wenn nächster Schuss = letzter Schuss, schreiben, wie viel noch zu erzielen ist
                    if (i == schuesse - 2 && (ziel - gesamt) < 11 && !nichtmehrzuerreichen) {
                        Schuetze.appendText("\nSie muessen noch " + (ziel - gesamt) + " Ringe erzielen. Dies ist Ihr letzter Schuss.");
                        Protokoll += LINE_SEPARATOR + "Sie muessen noch " + (ziel - gesamt) + " Ringe erzielen. Dies ist Ihr letzter Schuss.";

                    } //sonst Empfehlungen einzeln schreiben
                    else if (i != schuesse - 1) {
                        Schuetze.appendText("\nNaechster Schuss:");
                        Protokoll += LINE_SEPARATOR + "Naechster Schuss:";
                        System.out.println("" + (ziel - ((schuesse - (i + 2)) * 10 + gesamt)));
                        //Falls man mit den nächsten Schüssen noch das Ziel erreichen kann, selbst wenn der nächste Schuss 0 ist
                        if ((schuesse - (i + 2)) * 10 + gesamt >= ziel || (ziel - ((schuesse - (i + 2)) * 10 + gesamt)) > 10) {
                            //man kann schießen, was man möchte
                            //cout << " beliebig;";
                            //Protokoll+= " beliebig;";
                        } //sonst: mindestens best. Wert zu erreichen, da Ziel sonst nicht mehr erreicht werden kann
                        else {
                            //zu erreichender Wert: verbleibende Schüsse-1 * 10 = Wert, der mit den nächsten Schüssen maximal erreicht werden kann; dazu die Zahl bereits erzielter Treffer
                            //ziel minus dieser Wert= Mindestwert für diesen Schuss
                            Schuetze.appendText(" mindestens " + (ziel - ((schuesse - (i + 2)) * 10 + gesamt)) + " Ringe;");
                            Protokoll += " mindestens " + (ziel - ((schuesse - (i + 2)) * 10 + gesamt)) + " Ringe;";
                        }
                        //Zählvariable zurücksetzen
                        j = 0;
                        //kleinste Empfehlung suchen
                        while (zahl[j] == 0 && j < 10) {
                            j++;
                        }
                        //nur Empfehlungen ausgeben, die kleiner als 10 sind
                        if ((j < 10 || zahl[10] > 0) && j != (ziel - ((schuesse - (i + 2)) * 10 + gesamt))) {
                            if (j < 10) {
                                Schuetze.appendText("\nEmpfehlung: " + j + " oder mehr Ringe.");
                                Protokoll += LINE_SEPARATOR + "Empfehlung: " + j + " oder mehr Ringe.";
                            } else {
                                Schuetze.appendText("\nEmpfehlung: " + j + " Ringe.");
                                Protokoll += LINE_SEPARATOR + "Empfehlung: " + j + " Ringe.";
                            }
                        }
                        System.out.println("Empf.: " + j + " geben: " + (j < 10 || zahl[10] > 0));
                        //Relikt vergangener Lösung
                        for (int j = 0; j < 11; j++) {

                            if (zahl[j] != 0) {
                                System.out.println("Sie muessen noch " + zahl[j] + " mal " + j + " Treffer erzielen.");
                                //Protokoll+= "\nSie muessen noch " + NumberToString(zahl[j]) + " mal " + NumberToString(j) + " Treffer erzielen.";
                            }
                        }
                        //Prozedur für nächsten Durchlauf vorbereiten
                        i++;
                    }
                }
            }
            if (db) {
               // MySQLConnection.updateName(name, Startzeit, Schuetze.getText());
            }
        });
    }

    //Gewehrzielscheibe als Zielscheibe setzen
    @FXML
    private void Gewehrbild(ActionEvent event) {
        Platform.runLater(() -> {
            //Änderung der Zielscheibe zur Sicherheit nur außerhalb der Simulation möglich
            if (!laeuft) {
                //Bild als Datei auslesen...
                File file = new File("Bilder/Zielscheibe_Gewehr_10m.png");
                //... in ein Bild speichern...
                Image image = new Image(file.toURI().toString());
                //und schließlich Bilddarstellung auf das aktuelle Bild setzen
                imageview.setImage(image);
                //Eingabeprozedur übermitteln, dass die Gewehrscheibe angezeigt wird
                Scheibe = 0;
            }
        });
    }

    //Pistolenzielscheibe als Zielscheibe setzen
    @FXML
    private void Pistolenbild(ActionEvent event) {
        Platform.runLater(() -> {
            //Änderung der Zielscheibe zur Sicherheit nur außerhalb der Simulation möglich
            if (!laeuft) {
                //Bild als Datei auslesen...
                File file = new File("Bilder/Zielscheibe_Pistole_10m.png");
                //... in ein Bild speichern...
                Image image = new Image(file.toURI().toString());
                //und schließlich Bilddarstellung auf das aktuelle Bild setzen
                imageview.setImage(image);
                //Eingabeprozedur übermitteln, dass die Gewehrscheibe angezeigt wird
                Scheibe = 1;
            }
        });

    }

    @FXML
    private void informieren(ActionEvent event) {
        JOptionPane.showMessageDialog(null,
                "Programm Chancenrechner" + LINE_SEPARATOR + "Version 0.9" + LINE_SEPARATOR + "entwickelt von Eric Ackermann," + LINE_SEPARATOR
                + "Carl-Zeiss-Gymnasium Jena" + LINE_SEPARATOR + "betreut durch Herrn Otto Thiele und Herrn Bert Kunze" + LINE_SEPARATOR
                + "in enger Zusammenarbeit mit der Bürgeler Sportschützengesellschaft e.V." + LINE_SEPARATOR
                + "Beta-Programmversion, Nutzung auf eigene Gefahr!",
                "Über Chancenrechner",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
