package sample;

import Objects.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class cController {

	int addNum = 0, subNum = 0, mulNum = 0, divNum = 0;
	Model model = new Model();
	private chandlerHandler handler = new chandlerHandler(this);
	
	public cController() {
		new Thread(handler).start();
	}
	
	//FXML
    @FXML
    private Button uAdd;

    @FXML
    private Button uSub;

    @FXML
    private Button uMul;

    @FXML
    private Button uDiv;

    @FXML
    private Label AddNumber;

    @FXML
    private Label MulNumber;

    @FXML
    private Label SubNumber;

    @FXML
    private Label DivNumber;

    @FXML
    private Button upAdd;

    @FXML
    private Button downAdd;

    @FXML
    private Button upSub;

    @FXML
    private Button downSub;

    @FXML
    private Button upMul;

    @FXML
    private Button downMul;

    @FXML
    private Button upDiv;

    @FXML
    private Button downDiv;

    @FXML
    void addToDiv(ActionEvent event) {
    	this.divNum++;
    	this.DivNumber.setText(Integer.toString(divNum));
    }

    @FXML
    void addToMul(ActionEvent event) {
    	this.mulNum++;
    	this.MulNumber.setText(Integer.toString(mulNum));
    }

    @FXML
    void addToSub(ActionEvent event) {
    	this.subNum++;
    	this.SubNumber.setText(Integer.toString(subNum));
    }

    @FXML
    void addToUp(ActionEvent event) {
    	this.addNum++;
    	this.AddNumber.setText(Integer.toString(addNum));

    }

    @FXML
    void subToAdd(ActionEvent event) {
    	this.addNum--;
    	this.AddNumber.setText(Integer.toString(addNum));
    }

    @FXML
    void subToDiv(ActionEvent event) {
    	this.divNum--;
    	this.DivNumber.setText(Integer.toString(divNum));
    }

    @FXML
    void subToMul(ActionEvent event) {
    	this.mulNum--;
    	this.MulNumber.setText(Integer.toString(mulNum));
    }

    @FXML
    void subToSub(ActionEvent event) {
    	this.subNum--;
    	this.SubNumber.setText(Integer.toString(subNum));
    }

    @FXML
    void updateAdd(ActionEvent event) {
    	Message msg;
    	msg = model.createRequest('s', Integer.parseInt(String.valueOf(this.SubNumber.getText())));
    	this.handler.forwardMessage(msg);
    	resetValues();
    }

    @FXML
    void updateDiv(ActionEvent event) {
    	Message msg;
    	msg = model.createRequest('d', Integer.parseInt(String.valueOf(this.SubNumber.getText())));
    	this.handler.forwardMessage(msg);
    	resetValues();
    }

    @FXML
    void updateMul(ActionEvent event) {
    	Message msg;
    	msg = model.createRequest('m', Integer.parseInt(String.valueOf(this.SubNumber.getText())));
    	this.handler.forwardMessage(msg);
    	resetValues();
    }

    @FXML
    void updateSub(ActionEvent event) {
    	Message msg;
    	msg = model.createRequest('r', Integer.parseInt(String.valueOf(this.SubNumber.getText())));
    	this.handler.forwardMessage(msg);
    	resetValues();
    }
    
    @FXML
    void resetValues(){
    	this.addNum = 0;
    	this.subNum = 0;
    	this.mulNum = 0;
    	this.divNum = 0;
    	this.AddNumber.setText(Integer.toString(addNum));
    	this.SubNumber.setText(Integer.toString(subNum));
    	this.MulNumber.setText(Integer.toString(mulNum));
    	this.DivNumber.setText(Integer.toString(divNum));
    }
}
