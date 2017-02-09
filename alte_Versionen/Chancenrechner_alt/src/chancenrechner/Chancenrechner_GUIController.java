/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chancenrechner;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.Timer;

/**
 *
 * @author Eric
 */
public class Chancenrechner_GUIController implements Initializable {

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
    //Leinwand
    javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(700, 700);
    GraphicsContext g = canvas.getGraphicsContext2D();

    static int schuss = 0, schuesse, ziel, aktuell, optimum, echteschuesse;
    static String Protokoll;
    static boolean laeuft = false;
    String name;
    static JOptionPane pane;
    static JTextField Eingabe;
    @FXML
    public TextField Zeitanzeiger;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        flaeche.getChildren().add(canvas);
    }

    @FXML
    private void Kreiszeichnen(ActionEvent event) {
    }
    public static int zeit = 12 * 60, gesamtzeit = zeit;
    public Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(2500),
            ae -> timelineactionPerformed()
            ));
    public void timelineactionPerformed() {
                    // 1 Sekunde abziehen 
                    zeit--;
                    int minuten, sekunden;
                    minuten = (int) ((zeit) / (60));
                    sekunden = (zeit) - minuten * 60;
                    // Zahl in Label darstellen 
                    Zeitanzeiger.setText(""+minuten + " Minuten " + sekunden + " Sekunden übrig");
                    System.out.println(minuten + " Minuten " + sekunden + " Sekunden übrig");
                    // Falls Zähler = 0, Countdown abgelaufen! 
                    if (zeit == 0) {
                        Zeitanzeiger.setText("Zeit abgelaufen!");
                        System.out.println("Zeit abgelaufen!");
                        // Timer stoppen 
                        timeline.stop();
                        laeuft = false;
                        Löschen.setText("Start");
                        gesamt += aktuell;
                        protokollieren();
                    }
    }
                    @FXML
    private void Löschen(ActionEvent event) {
        if (!laeuft) {
            laeuft = true;
            schuss = 0;
            g.clearRect(0, 0, 1000, 1000);
            schuesse = Integer.parseInt(Schuesse.getText());
            name = JOptionPane.showInputDialog(null, "Bitte geben Sie Name oder ID des Schützen ein.", "Schütze eingeben", JOptionPane.OK_OPTION);
            while (schuesse < 1) {
                schuesse = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie eine gültige Schusszahl ein.", "Schusszahl ungültig", JOptionPane.OK_OPTION));
            }
            Schuesse.setText("" + schuesse);
            ziel = Integer.parseInt(Ziel.getText());
            while (ziel < 0 || schuesse * 10 < ziel) {
                ziel = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie ein gültiges Ziel ein.", "Ziel ungültig", JOptionPane.OK_OPTION));
            }
            Löschen.setText("Stopp");
            Ziel.setText("" + ziel);
            optimum = (int) Math.ceil((float) ((float) ziel / (float) schuesse));
            Schuetze.setText("Das Ziel: durschschnittlich " + ((float) ((float) ziel / (float) schuesse)) + " Ringe.\n");
            ergebnisse = new int[schuesse];
            //Zeitlimit
            //Festlegung:
            switch (schuesse) {
                //5 Schuss => 6 Minuten,
                case 5:
                    zeit = 6 * 60;
                    break;
                //10 Schuss => 12 Minuten,	
                case 10:
                    zeit = 12 * 60;
                    break;
                //20 Schuss => 30 Minuten,	
                case 20:
                    zeit = 30 * 60;
                    break;
                //30 Schuss => 40 Minuten	
                case 30:
                    zeit = 40 * 60;
                    break;
                //sonst: 1 Schuss pro Minute				
                default:
                    zeit = schuesse * 60;
                    break;
            }
            gesamtzeit = zeit;
            //Angaben jeweils in Millisekunden
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
            } else {
            Löschen.setText("Start");
            laeuft = false;
            timeline.stop();
        }
    }

    @FXML
    private void mausgezogen(javafx.scene.input.MouseEvent event) {
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void mausklick(javafx.scene.input.MouseEvent event) {
        if (laeuft) {
            g.setFill(javafx.scene.paint.Color.BLACK);
            g.setStroke(javafx.scene.paint.Color.BLACK);
            g.setLineWidth(5);
            g.fillOval(event.getX() - 7.5, event.getY() - 7.5, 15, 15);
            schuss++;
            g.setFill(javafx.scene.paint.Color.WHITE);
            g.setStroke(javafx.scene.paint.Color.WHITE);
            g.fillText(schuss + "", event.getX() - 3, event.getY() + 3);
            System.out.println("Koordinaten: (" + event.getX() + ";" + event.getY() + ")");
            double MitteX = 350 - 17, MitteY = 350 + 6;
            double X = event.getX(), Y = event.getY();
            X = Math.abs(X - MitteX);
            Y = Math.abs(Y - MitteY);
            double Abstand = Math.abs(Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)));
            Object[] message;
            int korrektur;
            if (Abstand < 1 * 30) {
                aktuell = 10;
                System.out.println("Eingabe: " + aktuell);
                korrektur = JOptionPane.showConfirmDialog(null,
                        "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                        "Eingabe bestätigen",
                        JOptionPane.YES_NO_OPTION);
                if (korrektur == JOptionPane.NO_OPTION) {
                    aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                }

            } else {
                if (Abstand < 2 * 30) {
                    aktuell = 9;
                    System.out.println("Eingabe: " + aktuell);
                    korrektur = JOptionPane.showConfirmDialog(null,
                            "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                            "Eingabe bestätigen",
                            JOptionPane.YES_NO_OPTION);
                    if (korrektur == JOptionPane.NO_OPTION) {
                        aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                    }

                } else {
                    if (Abstand < 3 * 30) {
                        aktuell = 8;
                        System.out.println("Eingabe: " + aktuell);
                        korrektur = JOptionPane.showConfirmDialog(null,
                                "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                                "Eingabe bestätigen",
                                JOptionPane.YES_NO_OPTION);
                        if (korrektur == JOptionPane.NO_OPTION) {
                            aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                        }

                    } else {
                        if (Abstand < 4 * 30 - 6) {
                            aktuell = 7;
                            System.out.println("Eingabe: " + aktuell);
                            korrektur = JOptionPane.showConfirmDialog(null,
                                    "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                                    "Eingabe bestätigen",
                                    JOptionPane.YES_NO_OPTION);
                            if (korrektur == JOptionPane.NO_OPTION) {
                                aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                            }

                        } else {
                            if (Abstand < 5 * 30 - 8) {
                                aktuell = 6;
                                System.out.println("Eingabe: " + aktuell);
                                korrektur = JOptionPane.showConfirmDialog(null,
                                        "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                                        "Eingabe bestätigen",
                                        JOptionPane.YES_NO_OPTION);
                                if (korrektur == JOptionPane.NO_OPTION) {
                                    aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                                }

                            } else {
                                if (Abstand < 6 * 30 - 11) {
                                    aktuell = 5;
                                    System.out.println("Eingabe: " + aktuell);
                                    korrektur = JOptionPane.showConfirmDialog(null,
                                            "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                                            "Eingabe bestätigen",
                                            JOptionPane.YES_NO_OPTION);
                                    if (korrektur == JOptionPane.NO_OPTION) {
                                        aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                                    }

                                } else {
                                    if (Abstand < 7 * 30 - 11) {
                                        aktuell = 4;
                                        System.out.println("Eingabe: " + aktuell);
                                        korrektur = JOptionPane.showConfirmDialog(null,
                                                "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                                                "Eingabe bestätigen",
                                                JOptionPane.YES_NO_OPTION);
                                        if (korrektur == JOptionPane.NO_OPTION) {
                                            aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                                        }

                                    } else {
                                        if (Abstand < 8 * 30 - 12) {
                                            aktuell = 3;
                                            System.out.println("Eingabe: " + aktuell);
                                            korrektur = JOptionPane.showConfirmDialog(null,
                                                    "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                                                    "Eingabe bestätigen",
                                                    JOptionPane.YES_NO_OPTION);
                                            if (korrektur == JOptionPane.NO_OPTION) {
                                                aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                                            }

                                        } else {
                                            if (Abstand < 9 * 30 - 15) {
                                                aktuell = 2;
                                                System.out.println("Eingabe: " + aktuell);
                                                korrektur = JOptionPane.showConfirmDialog(null,
                                                        "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                                                        "Eingabe bestätigen",
                                                        JOptionPane.YES_NO_OPTION);
                                                if (korrektur == JOptionPane.NO_OPTION) {
                                                    aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                                                }

                                            } else {
                                                if (Abstand < 10 * 30 - 19) {
                                                    aktuell = 1;
                                                    System.out.println("Eingabe: " + aktuell);
                                                    korrektur = JOptionPane.showConfirmDialog(null,
                                                            "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                                                            "Eingabe bestätigen",
                                                            JOptionPane.YES_NO_OPTION);
                                                    if (korrektur == JOptionPane.NO_OPTION) {
                                                        aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                                                    }

                                                } else {

                                                    aktuell = 0;
                                                    System.out.println("Eingabe: " + aktuell);
                                                    korrektur = JOptionPane.showConfirmDialog(null,
                                                            "Möchten Sie die Eingabe \"" + aktuell + "\" bestätigen?",
                                                            "Eingabe bestätigen",
                                                            JOptionPane.YES_NO_OPTION);
                                                    if (korrektur == JOptionPane.NO_OPTION) {
                                                        aktuell = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte geben Sie die erzielte Ringzahl ein!", "Ringzahl eingeben", JOptionPane.OK_OPTION));
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (schuss >= schuesse) {
                timeline.stop();
                laeuft = false;
                Löschen.setText("Start");
                gesamt += aktuell;
                protokollieren();
            } else {
                Empfehlungen();
            }
        } else {
            double MitteX = 350 - 17, MitteY = 350 + 6;
            double X = event.getX(), Y = event.getY();
            X = (X - MitteX);
            Y = (Y - MitteY);
            double Abstand = Math.abs(Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)));
            System.out.println("Abstand zur Mitte: " + Abstand + ", dx= " + X + ", dy= " + Y);
        }
    }

    private void protokollieren() {
        Calendar cal = Calendar.getInstance();
        String datum = cal.get(Calendar.DAY_OF_MONTH)
                + "." + (cal.get(Calendar.MONTH) + 1)
                + "." + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                + cal.get(Calendar.MINUTE) + ":"
                + cal.get(Calendar.SECOND);
        g.setFill(javafx.scene.paint.Color.BLACK);
        g.setStroke(javafx.scene.paint.Color.BLACK);
        g.setLineWidth(5);
        g.fillText("Protokoll von " + name + "\nam " + datum + "\nZiel: " + ziel + " Ringe", 50, 50);
        //Gesamtpunkte und nötige Zeit ausgeben
        Schuetze.setText("");
        String Bewertung = null;
        if (gesamt >= ziel) {
            Bewertung = "Herzlichen Glückwunsch, Sie haben\n      Ihre Zielringzahl erreicht.";
        } else {
            Bewertung = "Sie haben Ihr Ziel leider \n      nicht erreicht.";
        }
        zeit = gesamtzeit - zeit;
        int minuten, sekunden;
        minuten = (int) ((zeit) / (60));
        sekunden = (zeit) - minuten * 60;
        g.fillText("Sie haben " + gesamt + " Ringe erzielt und dafür\n" +minuten+" Minuten und "+sekunden+" Sekunden\ngebraucht.\n"
                + "Sie haben durchschnittlich " + (float) ((float) gesamt / (float) (echteschuesse + 1)) + "\nRinge erzielt.\n"
                + Bewertung, 500, 50);
        String fileName = "Protokoll_" + name + "_"
                + cal.get(Calendar.DAY_OF_MONTH)
                + "-" + (cal.get(Calendar.MONTH) + 1)
                + "-" + cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.HOUR_OF_DAY) + "-"
                + cal.get(Calendar.MINUTE) + "-"
                + cal.get(Calendar.SECOND) + ".png";
        // Erstelle ein BufferedImage 
        int w = (int) flaeche.getWidth();
        int h = (int) flaeche.getHeight();
        float quality = 1f;
        WritableImage bi = new WritableImage(w, h);

        // Male das JPanel in das BufferedImage 
        WritableImage image = flaeche.snapshot(new SnapshotParameters(), null);

        // TODO: probably use a file chooser here
        File file = new File(System.getProperty("user.home") + "/Desktop/" + fileName);
        System.out.println(System.getProperty("user.home") + "/Desktop/" + fileName);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            // TODO: handle exception here
            JOptionPane.showMessageDialog(null, "Beim Speichern des Bildes trat ein Fehler auf.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
    static int i = 0, gesamt = 0, Guthaben = 0, zwischensumme, j;
    static int[] ergebnisse, zahl = new int[11];
    static boolean frischgenullt, falsch = true, nichtmehrzuerreichen = false, nurabgearbeitet, erstesmal = true;

    private void Empfehlungen() {
        if (erstesmal) {
            erstesmal = false;
            zahl[optimum] = schuesse;
        }
        //zählen, wie oft geschossen wurde
        echteschuesse = i;
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
        //}
        //wenn Ziel frühzeitig erreicht, Meldung anzeigen
        if (gesamt >= ziel) {
            //cout << "\n Mit " << gesamt << " Ringen haben Sie Ihr Ziel von " << ziel << " Punkten erreicht. Ihnen stehen noch " << (schuesse - (i + 1)) << " Schuesse zur Verfuegung.";
            //Protokoll += "\n Mit " + NumberToString(gesamt) + " Punkten haben Sie Ihr Ziel von " + NumberToString(ziel) + " Punkten erreicht. Ihnen stehen noch " + NumberToString(schuesse - (i + 1))
            // + " Schuesse zur Verfuegung.";
            //keine Empfehlungen mehr geben, da jetzt sinnlos 
            nichtmehrzuerreichen = true;
            //sonst: zur Kontrolle und Übersicht Gesamtpunktzahl und noch zu erreichende Treffer ausgeben    
        } else {
            //cout << "\nBisherige Gesamtringzahl: " << gesamt;
            //Protokoll+="\nBisherige Gesamtringzahl: " + NumberToString(gesamt);
        }
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
            //Wenn noch Optmum-Werte zu schießen sind, nicht nur eine Empfehlung erfüllt wurde und das Optimum nicht neu berechnet wurde
            if (zahl[optimum] > 0 && !nurabgearbeitet && !frischgenullt) {
                zahl[optimum]--;
            }
            //Array sortieren
            //zahl = sortieren(zahl, schuesse - i, optimum);
            //Empfehlungen sinnvoll ausgeben
            //wenn nächster Schuss = letzter Schuss, schreiben, wie viel noch zu erzielen ist
            if (i == schuesse - 2 && (ziel - gesamt) < 11 && !nichtmehrzuerreichen) {
                Schuetze.appendText("\nSie muessen noch " + (ziel - gesamt) + " Ringe erzielen. Dies ist Ihr letzter Schuss.");
                Protokoll += "\nSie muessen noch " + (ziel - gesamt) + " Ringe erzielen. Dies ist Ihr letzter Schuss.";

            } //sonst Empfehlungen einzeln schreiben
            else if (i != schuesse - 1) {
                Schuetze.appendText("\nNaechster Schuss:");
                Protokoll += "\nNaechster Schuss:";
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
                    Schuetze.appendText("\nEmpfehlung: " + j + " oder mehr Ringe.");
                    Protokoll += "\nEmpfehlung: " + j + " oder mehr Ringe.";
                }
                System.out.println("Empf.: " + j + " geben: " + (j < 10 || zahl[10] > 0));
                //Relikt vergangener Lösung
                for (int j = 0; j < 11; j++) {

                    if (zahl[j] != 0) {
                        System.out.println("Sie muessen noch " + zahl[j] + " mal " + j + " Treffer erzielen.");
                        //Protokoll+= "\nSie muessen noch " + NumberToString(zahl[j]) + " mal " + NumberToString(j) + " Treffer erzielen.";
                    }
                }
                i++;
            }
        }
    }

}
