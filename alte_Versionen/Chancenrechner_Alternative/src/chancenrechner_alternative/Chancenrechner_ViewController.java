/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chancenrechner_alternative;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;

/**
 *
 * @author Eric
 */
public class Chancenrechner_ViewController implements Initializable {

    @FXML
    private Label Label_OL;
    @FXML
    private Label Label_UL;
    @FXML
    private Label Label_M;
    @FXML
    private Label Label_OR;
    @FXML
    private TextField schuesse;
    @FXML
    private TextField aktuell;
    @FXML
    private TextField ziel;
    @FXML
    private TextField naechster;
    @FXML
    private TextField Ist;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    static boolean gewarnt = false;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (Integer.parseInt(schuesse.getText()) > 0) {
            String Protokoll = "";
            int gesamtschuesse = Integer.parseInt(schuesse.getText());
            String name = "";
            for (int i = 0; i < name.length() - 1; i++) {
                if (name.charAt(i) == ' ') {
                    name = new String("" + name.substring(0, i - 1) + name.substring(i + 1));
                }
            }
            int zielpunkte = Integer.parseInt(ziel.getText());
            Ist.setText((Integer.parseInt(Ist.getText()) + Integer.parseInt(aktuell.getText())) + "");
            schuesse.setText(Integer.parseInt(schuesse.getText()) - 1 + "");
            int maximal = 10 * (Integer.parseInt(schuesse.getText()) - 1);
            if (maximal >= (Integer.parseInt(ziel.getText())) - Integer.parseInt(Ist.getText())) {
                naechster.setText("beliebig");
            } else if (((Integer.parseInt(ziel.getText()) - Integer.parseInt(Ist.getText()))) - maximal < 10) {
                naechster.setText(((Integer.parseInt(ziel.getText()) - Integer.parseInt(Ist.getText()))) - maximal + " oder mehr");
            } else if (((Integer.parseInt(ziel.getText()) - Integer.parseInt(Ist.getText()))) - maximal == 10) {
                naechster.setText("10");
            } else {
                if (!gewarnt) {
                    JOptionPane.showMessageDialog(null, "Ziel von " + ziel.getText() + " Punkten ist nicht mehr erreichbar. Möglich sind nur noch "
                            + ((Integer.parseInt(schuesse.getText())-1) * 10 + Integer.parseInt(Ist.getText())+2*Integer.parseInt(aktuell.getText())) + " Punkte", "Ziel nicht mehr erreichbar", JOptionPane.WARNING_MESSAGE);
                    gewarnt = true;
                }
                naechster.setText("beliebig");
            }
        }
    }

}
