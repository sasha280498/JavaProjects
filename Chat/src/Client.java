import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * �������� ������� �� ����� ������������ ���������, ����� ����� � ��������� � ������ Client
 */

class ClientSomthing {
    
    private Socket socket;
    private BufferedReader in; // ����� ������ �� ������
    private BufferedWriter out; // ����� ������ � �����
    private BufferedReader inputUser; // ����� ������ � �������
    private String addr; // ip ����� �������
    private int port; // ���� ����������
    private String nickname; // ��� �������
    private Date time;
    private String dtime;
    private SimpleDateFormat dt1;
    
    /**
     * ��� �������� ���������� ������� ����� � ����� �����
     *
     * @param addr
     * @param port
     */
    
    public ClientSomthing(String addr, int port) {
        this.addr = addr;
        this.port = port;
        try {
            this.socket = new Socket(addr, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            // ������ ������ �� ������ / ������ � �����, � ������ � �������
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.pressNickname(); // ����� ������� ���������� ������� ���
            new ReadMsg().start(); // ���� �������� ��������� �� ������ � ����������� �����
            new WriteMsg().start(); // ���� ������� ��������� � ����� ���������� � ������� � ����������� �����
        } catch (IOException e) {
            // ����� ������ ���� ������ ��� �����
            // ������, ����� ������ ������������ ������:
            ClientSomthing.this.downService();
        }
        // � ��������� ������ ����� ����� ������
        // � ������ run() ����.
    }
    
    /**
     * ������� ������ ���,
     * � ������� ��� � ����������� �� ������
     */
    
    private void pressNickname() {
        System.out.print("Press your nick: ");
        try {
            nickname = inputUser.readLine();
            out.write("Hello " + nickname + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
        
    }
    
    /**
     * �������� ������
     */
    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }
    
    // ���� ������ ��������� � �������
    private class ReadMsg extends Thread {
        @Override
        public void run() {
            
            String str;
            try {
                while (true) {
                    str = in.readLine(); // ���� ��������� � �������
                    if (str.equals("stop")) {
                        ClientSomthing.this.downService(); // ��������
                        break; // ������� �� ����� ���� ������ "stop"
                    }
                    System.out.println(str); // ����� ��������� � ������� �� �������
                }
            } catch (IOException e) {
                ClientSomthing.this.downService();
            }
        }
    }
    
    // ���� ������������ ��������� ���������� � ������� �� ������
    public class WriteMsg extends Thread {
        
        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    time = new Date(); // ������� ����
                    dt1 = new SimpleDateFormat("HH:mm:ss"); // ����� ������ ����� �� ������
                    dtime = dt1.format(time); // �����
                    userWord = inputUser.readLine(); // ��������� � �������
                    if (userWord.equals("stop")) {
                        out.write("stop" + "\n");
                        ClientSomthing.this.downService(); // ��������
                        break; // ������� �� ����� ���� ������ "stop"
                    } else {
                        out.write("(" + dtime + ") " + nickname + ": " + userWord + "\n"); // ���������� �� ������
                    }
                    out.flush(); // ������
                } catch (IOException e) {
                    ClientSomthing.this.downService(); // � ������ ���������� ���� ��������
                    
                }
                
            }
        }
    }
}

public class Client {
    
    public static String ipAddr = "localhost";
    public static int port = 8888;
    
    /**
     * �������� ������-���������� � ���������� ������� � ������� �����
     * @param args
     */
    
    public static void main(String[] args) {
        new ClientSomthing(ipAddr, port);
    }
}