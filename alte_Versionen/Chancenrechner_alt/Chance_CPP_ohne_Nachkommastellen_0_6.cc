#include<iostream>
//Konsolenausgabe mit cout und -eingabe mit cin
#include <stdio.h>
#include <stdlib.h>
//system ("PAUSE"); -> Fenster bleibt unter Windows geöffnet
#include<math.h>
//für ceil (=aufrunden)
#include <fstream>
//für Protokollausgabe
#include <String>
#include <sstream>
//für Protokollerstellung
#include<time.h>
//für Zeit in Stringnamen
#include <sys/time.h>
//für Zeitmessung
#include <windows.h>
//für Warnmeldung
using namespace std;
//für Variablennamen

//int in String wandeln
//template -> Variabel einsetzbar, auch für Double, char...
template <typename T>
string NumberToString(T pNumber)
{
 ostringstream oOStrStream;
 oOStrStream << pNumber;
 return oOStrStream.str();
}
//Protokoll-txt schreiben
static void protokollieren(string Protokoll, string name){
	//Zeitvariable
	time_t Zeitstempel;
	//Zeitzeiger
    tm *nun;
    //Zeit seit 1.1.1970 00:00 G.-time ermitteln
    Zeitstempel = time(0);
    //umwandeln in aktuelle Zeit
    nun = localtime(&Zeitstempel);
    //Dateinamen erzeugen
    string Name ="Protokoll_"+name+"_"+NumberToString(nun->tm_mday)+"-"+NumberToString(nun->tm_mon+1)+ "-"+
	NumberToString(nun->tm_year+1900)+"-"+NumberToString(nun->tm_hour)+
    "-" + NumberToString(nun->tm_min)+".txt";
    //Ausgabe
	ofstream f;
	f.open(Name.c_str(), ios::out);
    f << Protokoll << endl;
    f.close();
}

//Funktion zum Sortieren des Arrays mit den Schussempfehlungen, um wenige Empfehlungen ausgeben zu müssen;
//kommt nicht all zu oft zum Einsatz
 static int* sortieren(int zahl [], int Rest, int optimum) {
        //Sortieren des Arrays
        //1. Fall: Ausgleichende Ziele => Ziele haben gleichen Abstand zum Optimum, z.B. 4 und 8 bei Optimum 6 => Optimum erhöhen
        for (int i = 0; i < optimum; i++) {
        	//Solange ausgleichende Ziele vorhanden, die beiden um 1 senken und Optimum um je 1 (also um 2) erhöhen
            while (zahl[i] > 0 && zahl[optimum + (optimum - i)] > 0) {
                zahl[i]--;
                zahl[optimum + (optimum - i)]--;
                zahl[optimum] += 2;
            }
        }
        //2. Idee: Ziele mitteln, d.h. statt 1*0 und 1*8 2*4 => Übersichtlichkeit
        //Jede Zahl mit jeder probieren
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
            	//wenn es Mittelwert gibt,...
                if ((i + j) % 2 == 0 && i != j) {
                	//... wird dieser solange erhöht und die Zahlen gesenkt, bis die erste Zahl 0 ist
                    while (zahl[i] > 0 && zahl[j] > 0) {
                        zahl[i]--;
                        zahl[j]--;
                        zahl[(i + j) / 2] += 2;
                    }
                }

            }

        }
        //Ergebnis zurückgeben
        return zahl;
    }

int main(){
		//Programmablauf protokollieren
		string Protokoll("");
		//Falls Empfehlungen zu höherer Punktzahl führen, als erwünscht, wird Überschuss gespeichert und später gutgeschrieben
	    int Guthaben =0;
		//Optimum frisch geändert; also keine weiteren Empfehlungen in diesem Durchlauf ausgeben, da dies unsinnige Empfehlungen zur Folge
		//hätte und die Arbeit sowieso in diesem Schritt erledigt ist
        bool frischgenullt = false;
        //tritt ein, wenn Empfehlung umgesetzt wurde; verhindert, dass Optimum-Wert dabei herabgesetzt wird
        bool nurabgearbeitet = false;
        //tritt ein, wenn Ziel nicht mehr zu erreichen ist
        bool nichtmehrzuerreichen = false;
        //Zahl der noch zu feuernden Schüsse
        int zwischensumme;
		//Zahl der Gesamtschüsse
		int j=0;
        int schuesse;
        //Einlesen dieser
        string name;
        //Hinweis, dass Programm noch experimental; Name verewigen
        cout << "Programm Chancenrechner Version 0.6 ALPHA\n"
		<< "Entwickler: Eric Ackermann, Carl-Zeiss-Gymnasium Jena\n"
		<< "Experimentelle Programmversion, Benutzung auf eigene Gefahr.\n";
		Protokoll+="Programm Chancenrechner Version 0.5 ALPHA\nEntwickler: Eric Ackermann- Carl-Zeiss-Gymnasium Jena\nExperimentelle Programmversion, Nutzung auf eigene Gefahr.\n";
        //Schützen einlesen
        cout << "Schuetze: ";
        getline(cin, name); 
        for(int i=0;i<name.size();i++){
			if(name[i]==' '){
				name[i]='_';
			}
		}
        Protokoll+="Schuetze: "+name+"\n";
        cout << "Serie: ";
        cin >> schuesse;
		Protokoll+="Serie: " + NumberToString(schuesse)+"\n";
        while(schuesse<=0){
			cout << "\nEingabefehler. Frage erneut ab.\nSerie:";
			cin >> schuesse;
		Protokoll+="\nEingabefehler. Frage erneut ab.\nSerie:"+NumberToString(schuesse)+"\n";
		}
		//Zeitlimit
		long int zeit;
		//Festlegung:
		switch(schuesse){
		//5 Schuss => 6 Minuten,
		case 5:
			zeit = 6 * 60 * 1000;
			break;
		//10 Schuss => 12 Minuten,	
		case 10:
			zeit = 12 * 60 * 1000;
			break;
		//20 Schuss => 30 Minuten,	
		case 20:
			zeit = 30 * 60 * 1000;
			break;
		//30 Schuss => 40 Minuten	
		case 30:
			zeit = 40 * 60 * 1000;
			break;
		//sonst: 1 Schuss pro Minute				
		default:
			zeit = schuesse * 60 * 1000;
			break;
		//Angaben jeweils in Millisekunden	
		}
		//Ausgabe in Minuten des Zeitlimits
		cout << "Zeitlimit: " << zeit/(60*1000) << " Minuten." << endl;
		//Zeitvariablen
		struct timeval tp;
		long int start, zeit_aktuell;
		//Zeitlimit überschritten?
		bool ueberschritten = false;
        //Punkte, die maximal erreicht werden können
        int Punkte = 10;
        //Punkte, die höchstens erreicht werden können
        int maxPkt = schuesse * Punkte;
        //aktuell geschossene Punktzahl
		int aktuell = 0;
		//Array-Zeiger, an den das Array mit den Empfehlungen gehängt wird;
		//Index ist Schussempfehlung, Inhalt deren Quantität
        int* zahl;
        //Empfehlungen von 0-10 Punkten, da nur 10 möglich; Index 0-10 benötigt
		zahl = new int[11];
        for (int i = 0; i < 11; i++) {
            zahl[i] = 0;
        }
        //insgesamt erreichte Punktzahl
        int gesamt = 0;
		//Array mit der Liste der erzielten Treffer
        int ergebnisse [schuesse] ;
        //für den Fall eines Eingabefehlers
        bool falsch = true;
        //Einlesen des Ziels
        cout << "Ringe zu erreichen: ";
        int ziel;
		cin >> ziel;
		Protokoll+="Ringe zu erreichen: "+NumberToString(ziel)+"\n";
		//für Durchschnittsberechnung
		int echteschuesse;
		//auf Eingabefehler des Ziels prüfen
		while (ziel > maxPkt || ziel <=0 ) {
            cout << "\nEingabefehler. Frage erneut ab.\nRinge zu erreichen: ";
            cin >> ziel;
        Protokoll+="\nEingabefehler. Frage erneut ab.\nRinge zu erreichen: "+NumberToString(ziel)+"\n";
        }  
        	//Optimalwert: durchschnittlich nötiger Wert pro Schuss
            int optimum = ceil((float)((float)ziel / (float)schuesse));
            //Ziel: nur durchschnittlich nötige Punkte schießen
            zahl[optimum] = schuesse;
            //nicht jedes Ziel realisierbar => Ziel muss durch Schüsse teilbar sein
            //Anpassung des Ziels
            //cout << "Empfehlungen werden gegeben zum Erreichen von " << (optimum * schuesse) << " Punkten.\n";
            //Protokoll+="Empfehlungen werden gegeben zum Erreichen von " + NumberToString(optimum * schuesse) + " Punkten.\n";
            //Ziel bekanntgeben
			cout << "Das Ziel: durschschnittlich " << (float)((float)ziel / (float)schuesse) << " Ringe.\n";
			Protokoll+= "Das Ziel: durschschnittlich " + NumberToString((float)((float)ziel / (float)schuesse)) + " Ringe.\n";
			//wenn schuesse*optimum (angepeilter Wert) genau ziel ist, bleibt nichts über; sonst ist Guthaben Differenz zwischen angepeiltem Ziel und Ziel des Schützen
			Guthaben=schuesse*optimum-ziel;
			gettimeofday(&tp, NULL);
			start = tp.tv_sec * 1000 + tp.tv_usec / 1000;
			//Hauptschleife: Schüsse einzeln durchgehen
            for (int i = 0; i < schuesse; i++) {
            	//zählen, wie oft geschossen wurde
				echteschuesse=i;
				//neuer Schuss, also muss Berechnung ausgeführt werden
                frischgenullt = false;
                //Einlesen des Wertes für den Schuss, bis dieser im Bereich 0-10 liegt
                while (falsch) {
                	//aktuelle Zeit ermitteln,...
                	gettimeofday(&tp, NULL);
					zeit_aktuell = tp.tv_sec * 1000 + tp.tv_usec / 1000;
					//... und prüfen, ob Zeitlimit überschritten wurde
					if(zeit_aktuell - start > zeit && !ueberschritten){
					Protokoll+= "\nSie haben die zur Verfügung stehende Zeit überschritten.";	
					//falls ja, benachrichtigen	
					MessageBox(NULL, "Sie haben die zur Verfügung stehende Zeit überschritten.", "Zeit überschritten", MB_OK);
					ueberschritten = true;
					}
                    cout << "\nSchuss " << (i + 1) << ": ";
                    Protokoll+= "\nSchuss " + NumberToString(i + 1) + ": ";
                    cin >> aktuell;
                    Protokoll+=NumberToString(aktuell) + "\n";
                    if (aktuell >= 0 && aktuell <= 10) {
                        falsch = false;
                    } else {
                        cout << "Eingabefehler. Frage erneut ab.";
                        Protokoll+="Eingabefehler. Frage erneut ab.";
                    }
                }
                //damit nächster Schuss eingelesen werden kann
                falsch = true;
                //aktuellen Wert Gesamtwert hinzufügen
                gesamt += aktuell;
                //Ergebnis zusätzlich einzeln protokollieren
                ergebnisse[i] = aktuell;
                //Protokoll anzeigen=Schüsse einzeln ausgeben
                cout << "Gesamtringzahl: ";
                Protokoll+="Gesamtringzahl: ";
                //for (int j = 0; j <= i; j++) {
                    cout << gesamt << ";";
                    Protokoll+= NumberToString(gesamt) + ";";
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
                if(!((i==schuesse-1)||nichtmehrzuerreichen)){
                
                //damit Rechnung durchgeführt werden kann
                nurabgearbeitet = false;
                //Fall 1: weniger als gezielt geschossen
                if (aktuell < optimum) {
                	//Guthaben anwenden, um aktuell auf Optimum zu erhöhen => Optimum-Zahl wird später reduziert
                	if(aktuell+Guthaben>=optimum){
                		Guthaben-=optimum-aktuell;
                	}
                	//Programm muss Empfehlung zum Ausgleich geben
                	else{
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
                        for(int j=0;j<11;j++){
						zwischensumme+=zahl[j];
						}
						//Spiegelzahl des aktuellen Wertes finden und erhöhen, falls dafür genügend Schüsse übrig sind
                        if (optimum + (optimum - aktuell) >= 0 && optimum + (optimum - aktuell) <= 10&&zwischensumme+1<=(schuesse-(i+1))) {
                            zahl[optimum + (optimum - aktuell)]++;
                        } else {
                            //Sonst Optimum neu berechnen
                            //alle Empfehlungen zurücksetzen
                            for (int j = 0; j < 11; j++) {
                                    zahl[j] = 0;
                                }
                                //neues Optimum: noch zu erreichende Punkte / übrige Schüsse
								//zwingend aufrunden ab der kleinsten Kommastelle, da bei Abrunden Optimum zu klein 
                                optimum = (int)ceil((((float) (((float)ziel - (float)gesamt) / ((float)schuesse - ((float)i + 1)))) + (float) 0));
                                //Optimum zurückgesetzt => keine Aktion durch Fall 2, diese wäre unsinnig, da nötige Aktion bereits getan
								Guthaben= optimum*(schuesse-(i+1))-(ziel-gesamt);
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
                }}
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
                        for(int j=0;j<11;j++){
						zwischensumme+=zahl[j];
						}
                        if (optimum - (aktuell - optimum) >= 0 && optimum - (aktuell - optimum) <= 10&&zwischensumme+1<=(schuesse-(i+1))) {
                            zahl[optimum - (aktuell - optimum)]++;
                        } else {
						for (int j = 0; j < 11; j++) {
                                    zahl[j] = 0;
                                }
                                optimum = (int)ceil((((float) (((float)ziel - (float)gesamt) / ((float)schuesse - ((float)i + 1)))) + (float) 0));
                                Guthaben= optimum*(schuesse-(i+1))-(ziel-gesamt);
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
                zahl = sortieren(zahl, schuesse - i, optimum);
                //Empfehlungen sinnvoll ausgeben
                //wenn nächster Schuss = letzter Schuss, schreiben, wie viel noch zu erzielen ist
                if (i == schuesse-2 && (ziel - gesamt) < 11&&!nichtmehrzuerreichen) {
                    cout << "\nSie muessen noch " << (ziel - gesamt) << " Ringe erzielen. Dies ist Ihr letzter Schuss.";
                    Protokoll+= "\nSie muessen noch " + NumberToString(ziel - gesamt) + " Ringe erzielen. Dies ist Ihr letzter Schuss.";
                    
                }
                //sonst Empfehlungen einzeln schreiben
                else if (i!=schuesse-1){
                cout << "\nNaechster Schuss:";
                Protokoll+= "\nNaechster Schuss:";
                //Falls man mit den nächsten Schüssen noch das Ziel erreichen kann, selbst wenn der nächste Schuss 0 ist
                if((schuesse-(i+2))*10+gesamt>=ziel||(ziel-((schuesse-(i+2))*10+gesamt))>10){
                	//man kann schießen, was man möchte
					//cout << " beliebig;";
                	//Protokoll+= " beliebig;";
                }
                //sonst: mindestens best. Wert zu erreichen, da Ziel sonst nicht mehr erreicht werden kann
                else{
                	//zu erreichender Wert: verbleibende Schüsse-1 * 10 = Wert, der mit den nächsten Schüssen maximal erreicht werden kann; dazu die Zahl bereits erzielter Treffer
                	//ziel minus dieser Wert= Mindestwert für diesen Schuss
                	cout << " mindestens "<<(ziel-((schuesse-(i+2))*10+gesamt))<<" Ringe;";
                	Protokoll+= " mindestens "+NumberToString(ziel-((schuesse-(i+2))*10+gesamt))+" Ringe;";
                }
                //Zählvariable zurücksetzen
            	j=0;
            		//kleinste Empfehlung suchen
				    while (zahl[j] == 0&&j<10) {
						j++;
                    }
                    //nur Empfehlungen ausgeben, die kleiner als 10 sind
                    if(j<10){
                        cout << "\nEmpfehlung: "<<j<<" oder mehr Ringe.";
                        Protokoll+= "\nEmpfehlung: "+NumberToString(j)+" oder mehr Ringe.";
                    }//Relikt vergangener Lösung
                    //for (int j = 0; j < 11; j++) {

                    //if (zahl[j] != 0) {
                        //cout << "\nSie muessen noch " << zahl[j] << " mal " << j << " Treffer erzielen.";
                        //Protokoll+= "\nSie muessen noch " + NumberToString(zahl[j]) + " mal " + NumberToString(j) + " Treffer erzielen.";
                    //}
                //}
            }
			}
			}
			//wenn Zeitlimit überschritten, benachrichtigen
			gettimeofday(&tp, NULL);
			zeit_aktuell = tp.tv_sec * 1000 + tp.tv_usec / 1000;
			if(zeit_aktuell - start > zeit && !ueberschritten){
			ueberschritten = true;	
			MessageBox(NULL, "Sie haben die zur Verfügung stehende Zeit überschritten.", "Zeit überschritten", MB_OK);
			Protokoll+= "\nSie haben die zur Verfügung stehende Zeit überschritten.";
			}
			int minuten, sekunden;
			minuten = (int)((zeit_aktuell - start)/(60*1000));
			sekunden = ((int)((zeit_aktuell - start)/(1000))) - minuten * 60;
            //Gesamtpunkte und nötige Zeit ausgeben
            cout << "\nSie haben " << gesamt << " Ringe erzielt und dafuer " << minuten << " Minuten "
			<< sekunden << " Sekunden gebraucht.";
            Protokoll+= "\nSie haben " +NumberToString(gesamt) + " Ringe erzielt und dafuer " + NumberToString(minuten) + " Minuten "
			+ NumberToString(sekunden) + " Sekunden gebraucht.";;
            //durchschnittliche Punktzahl
            cout << "\nSie haben durchschnittlich "<< (float)((float)gesamt/(float)(echteschuesse+1)) << " Ringe erzielt.";
            Protokoll+= "\nSie haben durchschnittlich "+ NumberToString((float)((float)gesamt/(float)(echteschuesse+1))) + " Ringe erzielt.";
			//Überprüfung, ob Ziel erreicht
            if (gesamt >= ziel) {
                cout << "\nHerzlichen Glueckwunsch, Sie haben Ihre Zielringzahl erreicht.\n";
                Protokoll+="\nHerzlichen Glueckwunsch, Sie haben Ihre Zielringzahl erreicht.\n"; 
            } else {
                cout << "\nSie haben Ihr Ziel leider nicht erreicht.\n";
                Protokoll+= "\nSie haben Ihr Ziel leider nicht erreicht.\n";
            }
            //cout << "Wiederholung: \n" << Protokoll;
            
    protokollieren(Protokoll,name);
        //Fenster in Windows offen halten
	system ("PAUSE");
	//Meldung an OS: Programm erfolgreich abgelaufen
return 0;
}


