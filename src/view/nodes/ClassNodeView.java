package view.nodes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.WindowEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.nodes.Attribute;
import model.nodes.ClassNode;
import model.nodes.IdentifiedTextField;
import model.nodes.Operation;
import util.Constants;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;


/**
 * Visual representation of ClassNode class.
 */
public class ClassNodeView extends AbstractNodeView implements NodeView {

    private TextField title;

    private Rectangle rectangle;

    private StackPane container;
    private StackPane titlePane;
    private VBox vbox;

    private Separator firstLine;
    private Separator secondLine;

    private Line shortHandleLine;
    private Line longHandleLine;

    private final int STROKE_WIDTH = 1;

    public ClassNodeView(ClassNode node) {
        super(node);
        //setChangeListeners();

        container = new StackPane();
        rectangle = new Rectangle();
        vbox = new VBox();
        container.getChildren().addAll(rectangle, vbox);


        initVBox();
        createRectangles();
        changeHeight(node.getHeight());
        changeWidth(node.getWidth());
        initLooks();

        this.getChildren().add(container);

        this.setTranslateX(node.getTranslateX());
        this.setTranslateY(node.getTranslateY());
        createHandles();
    }

    private void createRectangles(){
        ClassNode node = (ClassNode) getRefNode();
        changeHeight(node.getHeight());
        changeWidth(node.getWidth());
        rectangle.setX(node.getX());
        rectangle.setY(node.getY());
    }

    private void changeHeight(double height){
        setHeight(height);
        rectangle.setHeight(height);
    }

    private void changeWidth(double width){
        setWidth(width);
        rectangle.setWidth(width);
        container.setMaxWidth(width);
        container.setPrefWidth(width);

        vbox.setMaxWidth(width);
        vbox.setPrefWidth(width);
        firstLine.setMaxWidth(width);
        firstLine.setPrefWidth(width);
        secondLine.setMaxWidth(width);
        secondLine.setPrefWidth(width);

        title.setMaxWidth(width);
        title.setPrefWidth(width);
    	
    	for(Node node: vbox.getChildren()) {
    		if (node instanceof Attribute || node instanceof Operation) {
    			TextField tf = (TextField) node;
    			tf.setMaxWidth(width);
    			tf.setPrefWidth(width);
    		}
    	}
    }

    private void createHandles(){
        shortHandleLine = new Line();
        longHandleLine = new Line();

        shortHandleLine.startXProperty().bind(rectangle.widthProperty().subtract(7));
        shortHandleLine.startYProperty().bind(rectangle.heightProperty().subtract(3));
        shortHandleLine.endXProperty().bind(rectangle.widthProperty().subtract(3));
        shortHandleLine.endYProperty().bind(rectangle.heightProperty().subtract(7));
        longHandleLine.startXProperty().bind(rectangle.widthProperty().subtract(15));
        longHandleLine.startYProperty().bind(rectangle.heightProperty().subtract(3));
        longHandleLine.endXProperty().bind(rectangle.widthProperty().subtract(3));
        longHandleLine.endYProperty().bind(rectangle.heightProperty().subtract(15));

        this.getChildren().addAll(shortHandleLine, longHandleLine);

    	title.textProperty().addListener(new ChangeListener<String>() {
    	    @Override
    	    public void changed(ObservableValue<? extends String> observable,
    	            String oldValue, String newValue) {
    	    	((ClassNode)getRefNode()).setTitle(newValue);
    	        System.out.println("Title changed to " + newValue + ")\n");
    	    }
    	});
    	for(Node node: vbox.getChildren()) {
    		if (node instanceof Attribute) {
            	createHandlesAttributesOperations((Attribute) node);
    		}
    		if (node instanceof Operation) {
            	createHandlesAttributesOperations((Operation) node);
    		}
    	}
    }

    private void initVBox(){
        ClassNode node = (ClassNode) getRefNode();

        vbox.setPadding(new Insets(5, 0, 5, 0));
        vbox.setSpacing(5);

        titlePane = new StackPane();

        firstLine = new Separator();
        firstLine.setMaxWidth(node.getWidth());

        secondLine = new Separator();
        secondLine.setMaxWidth(node.getWidth());

        title = new TextField();
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        if(node.getTitle() != null) {
            title.setText(node.getTitle());
        }
        title.setAlignment(Pos.CENTER);

        titlePane.getChildren().add(title);
        vbox.getChildren().addAll(titlePane, firstLine);
        
        if (node.getAttributes() != null) {
        	System.out.println("initVBox.node.getAttributes():\n"+node.getAttributes());        	
            for(String text : node.getAttributes().split("\\r?\\n")){
            	if (text.contains(";")) {
            		text = text.substring(text.indexOf(";")+1);
            	} 
            	Attribute attribute = new Attribute(text);
            	attribute.setFont(Font.font("Verdana", 10));
                vbox.getChildren().add(attribute);
            }
        }

        vbox.getChildren().addAll(secondLine);

        if (node.getOperations() != null) {
        	System.out.println("initVBox.node.getOperations():\n"+node.getOperations());        	
        	for(String text : node.getOperations().split("\\r?\\n")){
               	if (text.contains(";")) {
            		text = text.substring(text.indexOf(";")+1);
            	}         	
            	Operation operation = new Operation(text);
            	operation.setFont(Font.font("Verdana", 10));
                vbox.getChildren().add(operation);
            }
        }

        if (operationsSize() > 0) {
            secondLine.setVisible(true);
        } else {
            secondLine.setVisible(false);
        }
    }

    private void initLooks(){
        rectangle.setStrokeWidth(STROKE_WIDTH);
        rectangle.setFill(Color.LIGHTSKYBLUE);
        rectangle.setStroke(Color.BLACK);
        StackPane.setAlignment(title, Pos.CENTER);
        BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTSKYBLUE, CornerRadii.EMPTY, Insets.EMPTY);
        Background background =  new Background(backgroundFill);
        title.setBackground(background);
    	for(Node node: vbox.getChildren()) {
    		if (node instanceof Attribute || node instanceof Operation) {
    			TextField tf = (TextField) node;
            	tf.setPadding(new Insets(0));
            	tf.setBackground(background);
    		}
    	}
    }

    public void setSelected(boolean selected){
        if(selected){
            rectangle.setStrokeWidth(2);
            setStroke(Constants.selected_color);
        } else {
            rectangle.setStrokeWidth(1);
            setStroke(Color.BLACK);
        }
    }

    public void setStrokeWidth(double scale){
        rectangle.setStrokeWidth(scale);
    }

    public void setFill(Paint p) {
        rectangle.setFill(p);
    }

    public void setStroke(Paint p) {
        rectangle.setStroke(p);
    }

    public Bounds getBounds(){
        return container.getBoundsInParent();
    }
    
    private int attributesSize() {
    	int cont = 0;
    	for(Node node: vbox.getChildren()) {
    		if (node instanceof Attribute) {
    	    	cont++;
    		}
    	}
    	return cont;
    }

    private int operationsSize() {
    	int cont = 0;
    	for(Node node: vbox.getChildren()) {
    		if (node instanceof Operation) {
    	    	cont++;
    		}
    	}
    	return cont;
    }
    
    
    private void createHandlesAttributesOperations(IdentifiedTextField textfield) {
    	
    	textfield.setOnKeyReleased(new EventHandler<KeyEvent>() {
    	    public void handle(KeyEvent ke) {
    	    	System.out.println("setOnKeyReleased");
    	    	String fullText = "";
    	    	if (textfield instanceof Attribute) {
        	    	for(Node node: vbox.getChildren()) {
        	    		if (node instanceof Attribute) {
        	    			IdentifiedTextField tf = (IdentifiedTextField) node;
                	    	fullText = fullText + vbox.getChildren().indexOf(tf) +
                	    			";" + tf.getXmiId() + "|" + tf.getText() + System.getProperty("line.separator");
        	    		}
        	    	}
        	    	System.out.println("Broadcasting:\n"+fullText);
        	    	((ClassNode)getRefNode()).setAttributes(fullText);
    	    	} else if (textfield instanceof Operation) {
        	    	for(Node node: vbox.getChildren()) {
        	    		if (node instanceof Operation) {
        	    			IdentifiedTextField tf = (IdentifiedTextField) node;
                	    	fullText = fullText + vbox.getChildren().indexOf(tf) +
                	    			";" + tf.getXmiId() + "|" + tf.getText() + System.getProperty("line.separator");
        	    		}
        	    	}
        	    	System.out.println("Broadcasting:\n"+fullText);
    	    		((ClassNode)getRefNode()).setOperations(fullText);
    	    	}    	    	
    	    }
    	});
    	
    	MenuItem cmItemMoveUp;
    	cmItemMoveUp = new MenuItem("Move Up");
    	cmItemMoveUp.setUserData(textfield);
		cmItemMoveUp.setOnAction(new EventHandler<ActionEvent>() {
    	    public void handle(ActionEvent e) {
    	    	System.out.println("Move Up");
    	    	IdentifiedTextField modifiedTextField = (IdentifiedTextField) ((MenuItem) e.getSource()).getUserData();
    	    	String fullText = "";
    	    	int index = vbox.getChildren().indexOf(modifiedTextField);
	    		System.out.println("textfield instanceof Attribute && "+index+" > 2");
    	    	if (textfield instanceof Attribute && index > 2) {
    	    		index--;
    	    		vbox.getChildren().remove(modifiedTextField);
       				vbox.getChildren().add(index,modifiedTextField);    	
        	    	for(Node node: vbox.getChildren()) {
        	    		if (node instanceof Attribute) {
        	    			IdentifiedTextField tf = (IdentifiedTextField) node;
                	    	fullText = fullText + vbox.getChildren().indexOf(tf) + ";" + tf.getXmiId() +
                	    			"|" + tf.getText() + System.getProperty("line.separator");
        	    		}
        	    	}
        	    	System.out.println("Broadcasting:\n"+fullText);
        	    	((ClassNode)getRefNode()).setAttributes(fullText);
    	    	} else if (textfield instanceof Operation && index > (3+attributesSize())) {
    	    		System.out.println("textfield instanceof Operation && "+index+" > "+(3+attributesSize()));
    	    		index--;
    				vbox.getChildren().remove(modifiedTextField);
    				vbox.getChildren().add(index,modifiedTextField);    	
        	    	for(Node node: vbox.getChildren()) {
        	    		if (node instanceof Operation) {
        	    			IdentifiedTextField tf = (IdentifiedTextField) node;
                	    	fullText = fullText + vbox.getChildren().indexOf(tf) + ";" + tf.getXmiId() +
                	    			"|" + tf.getText() + System.getProperty("line.separator");
        	    		}
        	    	}
        	    	System.out.println("Broadcasting:\n"+fullText);
    	    		((ClassNode)getRefNode()).setOperations(fullText);
    	    	}  
    	    }
        });
    	MenuItem cmItemMoveDown;
    	cmItemMoveDown = new MenuItem("Move Down");    	
    	cmItemMoveDown.setUserData(textfield);
		cmItemMoveDown.setOnAction(new EventHandler<ActionEvent>() {
    	    public void handle(ActionEvent e) {
    	    	System.out.println("Move Down");
    	    	IdentifiedTextField modifiedTextField = (IdentifiedTextField) ((MenuItem) e.getSource()).getUserData();
    	    	String fullText = "";
    	    	int index = vbox.getChildren().indexOf(modifiedTextField);
	    		System.out.println("textfield instanceof Attribute && "+index+" < "+(1+attributesSize()));
    	    	if (textfield instanceof Attribute && index < (1+attributesSize())) {
    	    		index++;
    	    		vbox.getChildren().remove(modifiedTextField);
       				vbox.getChildren().add(index,modifiedTextField);    	
        	    	for(Node node: vbox.getChildren()) {
        	    		if (node instanceof Attribute) {
        	    			IdentifiedTextField tf = (IdentifiedTextField) node;
                	    	fullText = fullText + vbox.getChildren().indexOf(tf) + ";" + tf.getXmiId() +
                	    			"|" + tf.getText() + System.getProperty("line.separator");
        	    		}
        	    	}
        	    	System.out.println("Broadcasting:\n"+fullText);
    	    		((ClassNode)getRefNode()).setAttributes(fullText);
    	    	} else if (textfield instanceof Operation && index < (2+attributesSize()+operationsSize())) {
    	    		System.out.println("textfield instanceof Operation && "+index+" < "+(2+attributesSize()+operationsSize()));
    	    		index++;
    				vbox.getChildren().remove(modifiedTextField);
    				vbox.getChildren().add(index,modifiedTextField);    	
        	    	for(Node node: vbox.getChildren()) {
        	    		if (node instanceof Operation) {
        	    			IdentifiedTextField tf = (IdentifiedTextField) node;
                	    	fullText = fullText + vbox.getChildren().indexOf(tf) + ";" + tf.getXmiId() +
                	    			"|" + tf.getText() + System.getProperty("line.separator");
        	    		}
        	    	}
        	    	System.out.println("Broadcasting:\n"+fullText);
    	    		((ClassNode)getRefNode()).setOperations(fullText);
    	    	}  
    	    }
        });    		

    	MenuItem cmItemAdd;
    	if (textfield instanceof Attribute) {
        	cmItemAdd = new MenuItem("Add attribute");
        	cmItemAdd.setOnAction(event -> {
    	    	addAttribute();
            });            
    	} else {
        	cmItemAdd = new MenuItem("Add operation");
        	cmItemAdd.setOnAction(event -> {
    	    	addOperation();
            });            
    	}
        
		MenuItem cmItemDelete;
		if (textfield instanceof Attribute) {
    		cmItemDelete = new MenuItem("Delete attribute");
		} else {
    		cmItemDelete = new MenuItem("Delete operation");
    	}
    	cmItemDelete.setUserData(textfield);
    	cmItemDelete.setOnAction(new EventHandler<ActionEvent>() {
    	    public void handle(ActionEvent e) {
    	    	System.out.println("Delete attribute/operation");
    	    	IdentifiedTextField modifiedTextField = (IdentifiedTextField) ((MenuItem) e.getSource()).getUserData();
    	    	vbox.getChildren().remove(modifiedTextField);
    	    	String attributesfullText = "";
    	    	for (Node node: vbox.getChildren()) {
        	    	if (node instanceof Attribute) {
        	    		IdentifiedTextField tf = (IdentifiedTextField) node;
        	    		attributesfullText = attributesfullText + vbox.getChildren().indexOf(tf) +
            	    			";" + tf.getXmiId() + "|" + tf.getText() + System.getProperty("line.separator");
        	    	}
    	    	} 
    	    	System.out.println("Broadcasting:\n"+attributesfullText);
    	    	String operationsFullText = "";
    	    	for (Node node: vbox.getChildren()) {
        	    	if (node instanceof Operation) {
        	    		IdentifiedTextField tf = (IdentifiedTextField) node;
        	    		operationsFullText = operationsFullText + vbox.getChildren().indexOf(tf) +
            	    			";" + tf.getXmiId() + "|" + tf.getText() + System.getProperty("line.separator");
        	    	}
    	    	} 
    	    	System.out.println("Broadcasting:\n"+operationsFullText);
	    		((ClassNode)getRefNode()).setAttributesAndOperations(attributesfullText,operationsFullText);
    	    }
    	});
		
       	ContextMenu contextMenu = new ContextMenu();
    	contextMenu.getItems().addAll(cmItemMoveUp,cmItemMoveDown,cmItemAdd,cmItemDelete);
    	textfield.setContextMenu(contextMenu);
    }

    
    private void initLooksAttributeOperation(IdentifiedTextField textField) {
		textField.setFont(Font.font("Verdana", 10));
		textField.setStyle("-fx-prompt-text-fill: white");
        BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTSKYBLUE, CornerRadii.EMPTY, Insets.EMPTY);
        Background background =  new Background(backgroundFill);
        textField.setPadding(new Insets(0));
        textField.setBackground(background);    	
    }
    
    
    public void addAttribute() {
    	Attribute textField = new Attribute("");
		initLooksAttributeOperation(textField);
		vbox.getChildren().add(2+attributesSize(),textField);    	
    	createHandlesAttributesOperations(textField);
    }
    
    public void addOperation() {
    	Operation textField = new Operation("");
		initLooksAttributeOperation(textField);
		vbox.getChildren().add(textField);    	
    	createHandlesAttributesOperations(textField);
    }    

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    	
        super.propertyChange(evt);
        if (evt.getPropertyName().equals(Constants.changeNodeX)) {
            setX((double) evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeNodeY)) {
            setY((double) evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeNodeWidth)) {
            changeWidth((double) evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeNodeHeight)) {
            changeHeight((double) evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeNodeTitle)) {
        	String newValue = (String) evt.getNewValue();
        	// Update text if it was altered
        	if (!title.getText().equals(newValue)) {
        		title.setText(newValue);
        	}            
            if (title.getText() == null || title.getText().equals("")) {
                firstLine.setVisible(false);
            } else {
            	firstLine.setVisible(true);
            }
        } else if ( evt.getPropertyName().equals(Constants.changeClassNodeAttributes) ) {
        	String newValue = (String) evt.getNewValue();
        	System.out.println("propertyChangeAttributes:\n"+newValue);
        	// Check for removed attributes
        	for (int cont = 0; cont < vbox.getChildren().size(); cont++ ) {
        		Node node = vbox.getChildren().get(cont);
        		if ( node instanceof Attribute ) {
            		IdentifiedTextField localTextField = (IdentifiedTextField) node;
            		if (!newValue.contains(localTextField.getXmiId())) {
                    	System.out.println(localTextField+" not found. Removing...");
                        vbox.getChildren().remove(localTextField);
            		}
        		}
        	}
        	// Check for new attributes
        	for(String text : newValue.split("\\r?\\n")) {
            	String array[] = text.split(";");
            	int index = Integer.parseInt(array[0]);
            	Attribute remoteTextField = new Attribute(array[1]);
            	boolean found = false;
            	for (Node node: vbox.getChildren()) {
            		if ( node instanceof Attribute ) {
                		IdentifiedTextField localTextField = (IdentifiedTextField) node;
                		if (localTextField.getXmiId().equals(remoteTextField.getXmiId())) {
                			found = true;
                			break;
                		}
            		}
            	}
            	if (!found) {
                	System.out.println(remoteTextField+" not found. Adding with index = "+index);
            		initLooksAttributeOperation(remoteTextField);
            		createHandlesAttributesOperations(remoteTextField);
            		vbox.getChildren().add(index,remoteTextField);
            	}            	
        	}
        	// Check for text altered or attribute moved up or down
        	for(String text : newValue.split("\\r?\\n")){
            	String array[] = text.split(";");
            	int index = Integer.parseInt(array[0]);
            	IdentifiedTextField remoteTextField = new IdentifiedTextField(array[1]);
            	for (Node node: vbox.getChildren()) {
            		if ( node instanceof Attribute ) {
                		IdentifiedTextField localTextField = (IdentifiedTextField) node;
                    	if (localTextField.getXmiId().equals(remoteTextField.getXmiId())) {
                			// Update text if it was altered
                			if (!localTextField.getText().equals(remoteTextField.getText())) {
                            	System.out.println(localTextField+" is old. Updating to "+remoteTextField);
                    			localTextField.setText(remoteTextField.getText());
                			}
                			// If moved upper or down
                			if (vbox.getChildren().indexOf(localTextField) != index) {
                            	System.out.println(localTextField+" index is " + vbox.getChildren().indexOf(localTextField) +
                            			". Updating to index to "+ index);
                				vbox.getChildren().remove(localTextField);
                				vbox.getChildren().add(index,localTextField);    	
                			}
                			break;
                		}
            		}
            	}
        	}
	        if (operationsSize() > 0) {
	            secondLine.setVisible(true);
	        } else {
	            secondLine.setVisible(false);
	        }

        } else if ( evt.getPropertyName().equals(Constants.changeClassNodeOperations) ) {
        	String newValue = (String) evt.getNewValue();
        	System.out.println("propertyChangeOperations:\n"+newValue);
        	// Check for removed operations
        	for (int cont = 0; cont < vbox.getChildren().size(); cont++ ) {
        		Node node = vbox.getChildren().get(cont);
        		if ( node instanceof Operation ) {
            		IdentifiedTextField localTextField = (IdentifiedTextField) node;
            		if (!newValue.contains(localTextField.getXmiId())) {
                    	System.out.println(localTextField+" not found. Removing...");
                        vbox.getChildren().remove(localTextField);
            		}
        		}
        	}
        	// Check for new operations
        	for(String text : newValue.split("\\r?\\n")) {
            	String array[] = text.split(";");
            	int index = Integer.parseInt(array[0]);
            	Operation remoteTextField = new Operation(array[1]);
            	boolean found = false;
            	for (Node node: vbox.getChildren()) {
            		if ( node instanceof Operation ) {
                		IdentifiedTextField localTextField = (IdentifiedTextField) node;
                		if (localTextField.getXmiId().equals(remoteTextField.getXmiId())) {
                			found = true;
                			break;
                		}
            		}
            	}
            	if (!found) {
                	System.out.println(remoteTextField+" not found. Adding with index = "+index);
            		initLooksAttributeOperation(remoteTextField);
            		createHandlesAttributesOperations(remoteTextField);
            		vbox.getChildren().add(index,remoteTextField);
            	}            	
        	}
        	// Check for text altered or attribute or operation moved up or down
        	for(String text : newValue.split("\\r?\\n")){
            	String array[] = text.split(";");
            	int index = Integer.parseInt(array[0]);
            	IdentifiedTextField remoteTextField = new IdentifiedTextField(array[1]);
            	for (Node node: vbox.getChildren()) {
            		if ( node instanceof Operation ) {
                		IdentifiedTextField localTextField = (IdentifiedTextField) node;
                    	if (localTextField.getXmiId().equals(remoteTextField.getXmiId())) {
                			// Update text if it was altered
                			if (!localTextField.getText().equals(remoteTextField.getText())) {
                            	System.out.println(localTextField+" is old. Updating to "+remoteTextField);
                    			localTextField.setText(remoteTextField.getText());
                			}
                			// If moved upper or down
                			if (vbox.getChildren().indexOf(localTextField) != index) {
                            	System.out.println(localTextField+" index is " + vbox.getChildren().indexOf(localTextField) +
                            			". Updating index to "+ index);
                				vbox.getChildren().remove(localTextField);
                				vbox.getChildren().add(index,localTextField);    	
                			}
                			break;
                		}
            		}
            	}
        	}
	        if (operationsSize() > 0) {
	            secondLine.setVisible(true);
	        } else {
	            secondLine.setVisible(false);
	        }
        }
    }
}
