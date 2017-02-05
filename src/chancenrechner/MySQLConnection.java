/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chancenrechner;
//Quelle: http://www.itblogging.de/java/java-mysql-jdbc-tutorial/
//https://www.youtube.com/watch?v=ki77CLTHaNc

/**
 *
 * @author Eric
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//Datenbankenpasswort: 346sOluZ@hawqj$
public class MySQLConnection {

    private static Connection conn = null;

    // Hostname
    //private static String dbHost = "192.168.0.110";
    private static String dbHost = "localhost";
    // Port -- Standard: 3306
    private static String dbPort = "3306";

    // Datenbankname
    private static String database = "chancenrechner";

    // Datenbankuser
    private static String dbUser = "root";

    // Datenbankpasswort
    private static String dbPassword = "";

    private MySQLConnection() {
        try {

            // Datenbanktreiber für ODBC Schnittstellen laden.
            // Für verschiedene ODBC-Datenbanken muss dieser Treiber
            // nur einmal geladen werden.
            Class.forName("com.mysql.jdbc.Driver");

            // Verbindung zur ODBC-Datenbank chancenrechner herstellen.
            // Es wird die JDBC-ODBC-Brücke verwendet.
            conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":"
                    + dbPort + "/" + database + "?" + "user=" + dbUser + "&"
                    + "password=" + dbPassword);
        } catch (ClassNotFoundException e) {
            System.out.println("Treiber nicht gefunden");
        } catch (SQLException e) {
            //System.out.println("Connect nicht moeglich");
            e.printStackTrace();
        }
    }

    private static Connection getInstance() {
        if (conn == null) {
            new MySQLConnection();
        }
        return conn;
    }

    public static boolean checkname(String name) {
        boolean vorhanden = false;
        conn = getInstance();

        if (conn != null) {
            // Anfrage-Statement erzeugen.
            Statement query;
            try {
                query = conn.createStatement();

                // Ergebnistabelle erzeugen und abholen.
                String sql = "SELECT * FROM `android_connect` WHERE `android_connect`.`user` = '"+name+"';";
                ResultSet result = query.executeQuery(sql);
                System.out.println(result.toString());
                // Ergebnissätze durchfahren.
                if (result.next()) {
                    vorhanden = true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return vorhanden;
    }

    /**
     * Schreibt die Namensliste in die Konsole
     */
    public static void printNameList() {
        conn = getInstance();

        if (conn != null) {
            // Anfrage-Statement erzeugen.
            Statement query;
            try {
                query = conn.createStatement();

                // Ergebnistabelle erzeugen und abholen.
                String sql = "SELECT * FROM `android_connect`";
                ResultSet result = query.executeQuery(sql);

                // Ergebnissätze durchfahren.
                while (result.next()) {
                    String first_name = result.getString("user"); // Alternativ: result.getString(1);
                    String last_name = result.getString("password"); // Alternativ: result.getString(2);
                    String Startzeit = result.getString("Startzeit");
                    String aktiv = result.getString("aktiv");
                    String Empfehlungen = result.getString("Empfehlungen");
                    String name = first_name + ", " + last_name + ", " + Startzeit + ", " + aktiv + ", " + Empfehlungen;
                    System.out.println(name);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fügt einen neuen Datensatz hinzu
     */
    public static void insertName(String Schuetze, String Passwort, String Empfehlung, int Gesamtzeit) {
        conn = getInstance();

        if (conn != null) {
            try {

                // Insert-Statement erzeugen (Fragezeichen werden später ersetzt).
                String sql = "INSERT INTO `chancenrechner`.`android_connect`"
                        + " (`user`, `password`, `aktiv`, `Startzeit`, `Gesamtzeit`, `Empfehlungen`)"
                        + "VALUES(?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                // Erstes Fragezeichen durch "firstName" Parameter ersetzen
                preparedStatement.setString(1, Schuetze);
                // Zweites Fragezeichen durch "lastName" Parameter ersetzen
                preparedStatement.setString(2, Passwort);
                // Zweites Fragezeichen durch "lastName" Parameter ersetzen
                preparedStatement.setString(3, "0");
                // Zweites Fragezeichen durch "lastName" Parameter ersetzen
                preparedStatement.setString(4, "1970-01-01 00:00:01");
                // Zweites Fragezeichen durch "lastName" Parameter ersetzen
                preparedStatement.setString(5, "" + Gesamtzeit);
                // Zweites Fragezeichen durch "lastName" Parameter ersetzen
                preparedStatement.setString(6, Empfehlung);

                // SQL ausführen.
                System.out.println(preparedStatement.toString());
                preparedStatement.executeUpdate();

                // Es wird der letzte Datensatz abgefragt
                String lastActor = "SELECT * "
                        + "FROM android_connect "
                        + "ORDER BY user DESC LIMIT 1";
                ResultSet result = preparedStatement.executeQuery(lastActor);

                // Wenn ein Datensatz gefunden wurde, wird auf diesen zugegriffen 
                if (result.next()) {
                    String first_name = result.getString("user"); // Alternativ: result.getString(1);
                    String last_name = result.getString("password"); // Alternativ: result.getString(2);
                    String Startzeit = result.getString("Startzeit");
                    String aktiv = result.getString("aktiv");
                    String Empfehlungen = result.getString("Empfehlungen");
                    String name = first_name + ", " + last_name + ", " + Startzeit + ", " + aktiv + ", " + Empfehlungen;
                    System.out.println(name);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Aktualisiert den Datensatz mit der übergebenen actorId
     */
    public static void updateName(String user, String Startzeit, String Empfehlung) {
        conn = getInstance();

        if (conn != null) {
            try {

                String querySql = "SELECT * "
                        + "FROM android_connect "
                        + "WHERE user = ?";

                // PreparedStatement erzeugen.
                PreparedStatement preparedQueryStatement = conn.prepareStatement(querySql);
                preparedQueryStatement.setString(1, user);
                ResultSet result = preparedQueryStatement.executeQuery();

                if (result.next()) {
                    String first_name = result.getString("user"); // Alternativ: result.getString(1);
                    String last_name = result.getString("password"); // Alternativ: result.getString(2);
                    String startzeit = result.getString("Startzeit");
                    String aktiv = result.getString("aktiv");
                    String Empfehlungen = result.getString("Empfehlungen");
                    String name = first_name + ", " + last_name + ", " + startzeit + ", " + aktiv + ", " + Empfehlungen;
                    System.out.println(name);
                }

                // Ergebnistabelle erzeugen und abholen.
                String updateSql = "UPDATE `chancenrechner`.`android_connect` SET `aktiv` = '1', `Startzeit` = ?,"
                        + "`Empfehlungen` = ? WHERE `android_connect`.`user` = ?;";
                PreparedStatement preparedUpdateStatement = conn.prepareStatement(updateSql);
                // Erstes Fragezeichen durch "firstName" Parameter ersetzen
                preparedUpdateStatement.setString(1, Startzeit);
                // Zweites Fragezeichen durch "lastName" Parameter ersetzen
                preparedUpdateStatement.setString(2, Empfehlung);
                // Drittes Fragezeichen durch "actorId" Parameter ersetzen
                preparedUpdateStatement.setString(3, user);
                // SQL ausführen
                preparedUpdateStatement.executeUpdate();

                // Es wird der letzte Datensatz abgefragt
                result = preparedQueryStatement.executeQuery();

                if (result.next()) {
                    String first_name = result.getString("user"); // Alternativ: result.getString(1);
                    String last_name = result.getString("password"); // Alternativ: result.getString(2);
                    String startzeit = result.getString("Startzeit");
                    String aktiv = result.getString("aktiv");
                    String Empfehlungen = result.getString("Empfehlungen");
                    String name = first_name + ", " + last_name + ", " + startzeit + ", " + aktiv + ", " + Empfehlungen;
                    System.out.println(name);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void löschen(String user) {
        conn = getInstance();

        if (conn != null) {
            try {

                String querySql = "DELETE FROM `chancenrechner`.`android_connect` WHERE `android_connect`.`user` = ?";

                // PreparedStatement erzeugen.
                PreparedStatement preparedQueryStatement = conn.prepareStatement(querySql);
                preparedQueryStatement.setString(1, user);
                preparedQueryStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
