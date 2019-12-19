package client;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javafx.scene.image.Image;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class mainController {

    class Client {
        public File userListFile;
        private String ipAddr = "localhost";
        private int port = 111;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private String nickname;
        private Date time;
        private String dtime;
        private SimpleDateFormat dt1;

        Client(String nick){

            nickname=nick;
            try {
                this.socket = new Socket(ipAddr, port);
            } catch (IOException e) {
                System.err.println("Socket failed");
            }
            try {

                in = new Scanner(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                out.write(nickname + "\n");
                out.flush();

                new ReadMsg().start();
                new WriteMsg().start();

            } catch (IOException e) {
                downService();
            }
        }


        private class ReadMsg extends Thread {

            @Override
            public void run() {

                String str=in.nextLine();
                String editUserList="";
                int nickPosition;

                if (str.equals("Этот ник уже занят")) {
                    msg.appendText(str+", нажмите Ctrl+Enter и выберите другой\n");
                    message="bye\n";
                    connectAgain=true;
                } else {
                    userListFile = new File("userList.txt");
                    while (true) {
                        if (!str.equals("")){
                            if ((str.substring(0,4)).equals("$add")){
                                userList.appendText(str.substring(4)+"\n");
                            }
                            else{
                                if ((str.substring(0,7)).equals("$delete")){
                                    editUserList=userList.getText();
                                    nickPosition=editUserList.indexOf(str.substring(7));
                                    editUserList=editUserList.substring(0, nickPosition)+editUserList.substring(nickPosition+str.substring(7).length()+1);
                                    userList.setText(editUserList);
                                }
                                else msg.appendText(str + "\n");
                            }
                        }
                        str = in.nextLine();
                    }
                }
                downService();
            }
        }


        private class WriteMsg extends Thread {

            String str="%";

            @Override
            public void run() {

                while (true) {
                    if ((!str.equals(message))&&(!message.equals(""))) {
                        time = new Date(); // текущая дата
                        dt1 = new SimpleDateFormat("HH:mm:ss");
                        dtime = dt1.format(time);
                        out.write("(" + dtime + ") " + nickname + ": " + message + "\n");
                        out.flush();
                    }
                    str=message;
                    if ((str.equals("bye\n"))||(!an.getScene().getWindow().isShowing())) break;
                }
                downService();
            }
        }


        private void downService() {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                    in.close();
                    out.close();
                }
            } catch (IOException ignored) {}
        }
    }

    private String nick;
    public String message;
    public boolean connectAgain=false;

    @FXML
    public TextArea msg;

    @FXML
    public TextArea editor;

    @FXML
    private ImageView img;

    @FXML
    public TextArea userList;

    @FXML
    private Pane loadingPane3;

    @FXML
    private Pane loadingPane2;

    @FXML
    private Pane loadingPane1;

    @FXML
    private Label chatFrame;

    @FXML
    public AnchorPane an;

    @FXML
    void initialize() {
        message="";

        img.setImage(new Image("/client/loading.gif"));
        img.setOnMouseClicked(mouseEvent -> {
            img.setVisible(false);
            loadingPane1.setVisible(false);
            loadingPane2.setVisible(false);
            loadingPane3.setVisible(false);
            editor.setEditable(true);

            Stage stage = (Stage) an.getScene().getWindow();
            nick=stage.getTitle();
            chatFrame.setText(nick);
            new Client(nick);
        });

        editor.setOnKeyReleased(keyEvent -> {
            if ((keyEvent.getCode() == KeyCode.ENTER) && (keyEvent.isControlDown())) {
                if (message.equals("bye\n")||(message=editor.getText() + "\n").equals("bye\n")) {
                    bye();
                } else {
                    editor.setText("");
                }
            }
        });

    }

    void bye(){
        if (connectAgain){
            Parent root2;
            try {
                root2 = FXMLLoader.load(getClass().getResource("sample.fxml"));
                Stage connectStage = new Stage();
                connectStage.setTitle("Connecting");
                connectStage.setScene(new Scene(root2, 540, 240));
                connectStage.setResizable(false);
                connectStage.initStyle(StageStyle.UNDECORATED);
                connectStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) an.getScene().getWindow();
        stage.close();
    }
}