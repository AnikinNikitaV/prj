package server;
import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class Torrent extends Thread {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private String nick;


    public Torrent(Socket socket) throws IOException {
        this.socket = socket;

        in = new Scanner(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }
    @Override
    public void run() {
        String word;
        try{
            nick=in.nextLine();
            if (!checkInList(nick)){
                out.write("Этот ник уже занят" + "\n");
                out.flush();
                throw new IOException("Need another nick" + "\n");
            }
            send("Вы выбрали ник "+ nick + "\n");

            for (Torrent vr : Server.serverList) {
                vr.send("$add"+nick+"\n");
            }

        }
        catch (IOException e){
        }

        while (true) {
            try{
                word = in.nextLine();
                if((word.substring(word.lastIndexOf(' ') + 1)).equals("bye")) {
                    for (Torrent vr : Server.serverList) {
                        vr.send("Пользователь " + nick + " отключился" + "\n");
                        vr.send("$delete"+nick+"\n");
                    }
                    throw new NoSuchElementException();
                }

                if((word.substring(word.lastIndexOf(' ') + 1)).equals("^closeServer")) {
                    for (Torrent vr : Server.serverList) {
                        vr.send("Сервер отключен пользователем " + nick + "\n");
                    }
                    Server.serverSocket.close();
                    throw new NoSuchElementException();
                }

                for (Torrent vr : Server.serverList) {
                    vr.send(word);
                }
            }
            catch(NoSuchElementException ex){
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Server.serverList.remove(this);
    }



    public boolean checkInList(String newNick){
        String uList="";
        for(int j=0; j<Server.serverList.size()-1; j++){
            if (Server.serverList.get(j).nick.equals(newNick)){
                return false;
            } else uList+=("$add"+Server.serverList.get(j).nick+"\n");
        }
        out.write(uList);
        out.flush();
        return true;
    }

    private void send(String msg) {
            out.write(msg + "\n");
            out.flush();
    }
}
