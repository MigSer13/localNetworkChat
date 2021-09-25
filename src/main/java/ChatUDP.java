import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUDP extends JFrame {
    private JTextArea taMain;
    private JTextField tfMsg;

    private final String TITLE = "Our Tiny chat";
    private final int Loc_X = 100;
    private final int Loc_Y = 100;
    private final int WIDTH = 400;
    private final int HEIGHT = 400;

    private final int PORT = 9876;
    private final String IP_Broadcast = "192.168.1.2";


    private class Receiver extends Thread{
        @Override
        public void start(){
            super.start();
            try {
                customize();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void customize() throws Exception{
            DatagramSocket receiveSocket = new DatagramSocket(PORT);
            Pattern regex = Pattern.compile("\u0020-\uFFFF");
            while (true){
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                receiveSocket.receive(receivePacket);
                InetAddress IP_AdressSender = receivePacket.getAddress();
                int portSender = receivePacket.getPort();
                String sentence = new String(receivePacket.getData());
                Matcher matcher = regex.matcher(sentence);
                taMain.append(IP_AdressSender + ":" + portSender + ": ");
                while (matcher.find()){
                    taMain.append(sentence.substring(matcher.start(), matcher.end()));
                }
                taMain.append("\r\n");
                taMain.setCaretPosition(taMain.getText().length());
            }
        }
    }
    private void antistatik(){
        frameDraw(new ChatUDP());
        new Receiver().start();
    }

    private void frameDraw(JFrame frame){
        tfMsg = new JTextField();
        taMain = new JTextArea(HEIGHT/20, 50);
        JScrollPane scrMain = new JScrollPane(taMain);
        scrMain.setLocation(0, 0);
        taMain.setLineWrap(true);
        taMain.setEditable(false);

        JButton butSend = new JButton();
        butSend.setText("send");
        butSend.setToolTipText("broadcast a message");
        butSend.addActionListener(e-> {
            try {
                btnSendHandler();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            taMain.append("button works");
        });

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle(TITLE);
        frame.setLocation(Loc_X, Loc_Y);
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.getContentPane().add(BorderLayout.NORTH, scrMain);
        frame.getContentPane().add(BorderLayout.CENTER, tfMsg);
        frame.getContentPane().add(BorderLayout.EAST, butSend);
        frame.setVisible(true);
    }

    private void btnSendHandler() throws Exception{
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress IP_Adress = InetAddress.getByName(IP_Broadcast);
        byte[] sendData;
        String sentence = tfMsg.getText();
        tfMsg.setText("");
        sendData = sentence.getBytes(StandardCharsets.UTF_8);
        DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length, IP_Adress, PORT);
        datagramSocket.send(datagramPacket);
    }

    public static void main(String[] args) {
        new ChatUDP().antistatik();
    }
}
