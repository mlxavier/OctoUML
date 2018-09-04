package util;

import controller.ClientController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by Marcus on 2016-07-30.
 */
public class NetworkUtils {

    public static String[] clientConfigDialog() {
        // Create the custom dialog.
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle(GlobalVariables.getString("connectToServerTitle"));
        dialog.setHeaderText(GlobalVariables.getString("connectToServerHeaderText"));

        ButtonType okButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ip = new TextField();
        ip.setText("127.0.0.1");
        TextField port = new TextField();
        port.setText("54555");
        TextField userName = new TextField();
        userName.setText(System.getProperty("user.name"));

        Platform.runLater(() -> ip.requestFocus());

        grid.add(new Label("IP:"), 0, 0);
        grid.add(ip, 1, 0);
        grid.add(new Label(GlobalVariables.getString("port")+":"), 0, 1);
        grid.add(port, 1, 1);
        grid.add(new Label(GlobalVariables.getString("userName")+":"), 0, 2);
        grid.add(userName, 1, 2);
        dialog.getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new String[] {
                		ip.getText(), port.getText(), userName.getText()
                		};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();

        String[] s = new String[3];
        if (result.isPresent() 
        		&& validateIP(result.get()[0]) 
        		&& Integer.parseInt(result.get()[1]) >= 1024 
        		&& Integer.parseInt(result.get()[1]) <= 65535
        		&& !(result.get()[2].isEmpty())) {
            s[0] = result.get()[0];
            s[1] = result.get()[1];
            s[2] = result.get()[2];
        } else {
            s = null;
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid input");
            alert.setHeaderText("Invalip input. \nClosing diagram.");
            alert.showAndWait();
        }
        return s;
    }

    public static String[] ServerConfigDialog() {
        // Create the custom dialog.
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle(GlobalVariables.getString("launchServerTitle"));
        dialog.setHeaderText(GlobalVariables.getString("launchServerHeaderText"));

        ButtonType okButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField port = new TextField();
        port.setText("54555");
        ChoiceBox<String> collaborationType = new ChoiceBox<String>(FXCollections.observableArrayList(
        	    "Synchronous", "UMLCollab")
        );
        collaborationType.setValue("Synchronous");
        
        TextField userName = new TextField();
        userName.setText(System.getProperty("user.name"));

        Platform.runLater(() -> collaborationType.requestFocus());

        grid.add(new Label(GlobalVariables.getString("port") + ":"), 0, 0);
        grid.add(port, 1, 0);
        grid.add(new Label(GlobalVariables.getString("collaborationType") + ":"), 0, 1);
        grid.add(collaborationType, 1, 1);
        grid.add(new Label(GlobalVariables.getString("userName") + ":"), 0, 2);
        grid.add(userName, 1, 2);
        dialog.getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new String[] {
                		port.getText(),
                		(String) collaborationType.getSelectionModel().getSelectedItem(),
                		userName.getText()
                		};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();

        String[] s = new String[3];
        if (result.isPresent() 
        		&& Integer.parseInt(result.get()[0]) >= 1024 
        		&& Integer.parseInt(result.get()[0]) <= 65535
        		&& !(result.get()[1].isEmpty())
        		&& !(result.get()[2].isEmpty())) {
            s[0] = result.get()[0];
            s[1] = result.get()[1];
            s[2] = result.get()[2];
        } else {
            s = null;
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid input");
            alert.setHeaderText("Invalip input. \nClosing diagram.");
            alert.showAndWait();
        }
        return s;
    }

    public static String[] changeCollaborationTypeDialog() {
        // Create the custom dialog.
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Change collaboration type");
        dialog.setHeaderText("Warning: All clients models with be descarted and replaced for server model");

        ButtonType okButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ChoiceBox collaborationType = new ChoiceBox(FXCollections.observableArrayList(
        	    "Synchronous", "UMLCollab")
        );
        collaborationType.setValue("Synchronous");

        Platform.runLater(() -> collaborationType.requestFocus());

        grid.add(new Label("Collaboration type:"), 0, 1);
        grid.add(collaborationType, 1, 1);
        dialog.getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new String[] {
                		(String) collaborationType.getSelectionModel().getSelectedItem()
                		};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();

        String[] s = new String[3];
        if (result.isPresent() 
        		&& !(result.get()[0].isEmpty())) {
            s[0] = result.get()[0];
        } else {
            s = null;
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid input");
            alert.setHeaderText("Invalip input. \nClosing diagram.");
            alert.showAndWait();
        }
        return s;
    }
    
    //Regex for IPv4-pattern, found: http://stackoverflow.com/questions/5667371/validate-ipv4-address-in-java
    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    /**
     * Validates whether given string is valid IPv4-address
     * @param ip String to validate
     * @return true if String is valid IPv4-address, false otherwise
     */
    public static boolean validateIP(final String ip) {
        return PATTERN.matcher(ip).matches();
    }
}
