/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Jane george
 */
public class ClientRun implements PeerChat, Runnable {

    private static Socket socket;
    private static OutputStream os;
    private static InputStream is;
    private static int port = 5000;
    static String host = "localhost";
    static InetAddress ipAddr;
    static InetSocketAddress remote;

    public static void joining() {
        try {

            //int port = 5000;
            //InetAddress address = InetAddress.getByName(host);
            InetAddress remoteAddress = InetAddress.getByName(host);
            remote = new InetSocketAddress(remoteAddress, port);
            ClientRun clientRun = new ClientRun();
            clientRun.joinNetwork(remote);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void routingList() {
        try {
            // System.out.println("bootstrap_node.getAddress()"+remote.getAddress());
            String ips = remote.getAddress().toString();
            ips = ips.substring(ips.lastIndexOf('.') + 1, ips.length());
            System.out.println("ips" + ips);
            int nodeId = 0;
            nodeId = Integer.parseInt(ips);
            ipAddr = InetAddress.getLocalHost();
            JSONObject jsonobj = new JSONObject();

            jsonobj.put("type", "ROUTING_LIST");
            jsonobj.put("node_id", Integer.toString(nodeId));
            jsonobj.put("ip_address", ipAddr.getHostAddress());

         //socket = new Socket( bootstrap_node.getAddress(), bootstrap_node.getPort());
            //socket = new Socket();
//            WriteMesssage wm=new WriteMesssage(socket);
//            Thread tr=new Thread(wm);
//            tr.start();
//            ReadMesssage rm=new ReadMesssage(socket);
//            Thread tr1=new Thread(rm);
//            tr1.start();
            try {
                os = socket.getOutputStream();
                is = socket.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                PrintWriter output = new PrintWriter(os, true);
                // String messagetoserver="Join_Bootstrap";
                output.println(jsonobj);

                //while(true){
                String message = br.readLine();

                System.out.println("server routing list :: ");
                System.out.println(message);

                //}
            } catch (Exception er) {
                System.out.println("view table " + er);
            }

        } catch (Exception err) {
            System.out.println("error in joining " + err);
        }
    }

    public static void leave() {
        try {
            // System.out.println("bootstrap_node.getAddress()"+remote.getAddress());
            String ips = remote.getAddress().toString();
            ips = ips.substring(ips.lastIndexOf('.') + 1, ips.length());
            System.out.println("ips" + ips);
            int nodeId = 0;
            nodeId = Integer.parseInt(ips);
            ipAddr = InetAddress.getLocalHost();
            JSONObject jsonobj = new JSONObject();

            jsonobj.put("type", "LEAVE");
            jsonobj.put("node_id", Integer.toString(nodeId));
            jsonobj.put("ip_address", ipAddr.getHostAddress());

            try {
                os = socket.getOutputStream();
                is = socket.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                PrintWriter output = new PrintWriter(os, true);
                // String messagetoserver="Join_Bootstrap";
                output.println(jsonobj);

            } catch (Exception er) {
                System.out.println("view table " + er);
            }

        } catch (Exception err) {
            System.out.println("error in joining " + err);
        }
    }

    public static void ping() {
        try {
            // System.out.println("bootstrap_node.getAddress()"+remote.getAddress());
            String ips = remote.getAddress().toString();
            ips = ips.substring(ips.lastIndexOf('.') + 1, ips.length());
            System.out.println("ips" + ips);
            int nodeId = 0;
            nodeId = Integer.parseInt(ips);
            ipAddr = InetAddress.getLocalHost();
            JSONObject jsonobj = new JSONObject();

            jsonobj.put("type", "PING");
            jsonobj.put("target_id", Integer.toString(nodeId));
            jsonobj.put("sender_id", Integer.toString(nodeId));
            jsonobj.put("ip_address", ipAddr.getHostAddress());

            try {
                os = socket.getOutputStream();
           //  is = socket.getInputStream();

                //BufferedReader br = new BufferedReader(new InputStreamReader(is));
                PrintWriter output = new PrintWriter(os, true);
                // String messagetoserver="Join_Bootstrap";
                output.println(jsonobj);

            } catch (Exception er) {
                System.out.println("view " + er);
            }

        } catch (Exception err) {
            System.out.println("error in ping" + err);
        }
    }

    public static void clientChat() {
        try {
            // System.out.println("bootstrap_node.getAddress()"+remote.getAddress());
            String ips = remote.getAddress().toString();
            ips = ips.substring(ips.lastIndexOf('.') + 1, ips.length());
            System.out.println("ips" + ips);
            int nodeId = 0;
            nodeId = Integer.parseInt(ips);
            ipAddr = InetAddress.getLocalHost();
            JSONObject jsonobj = new JSONObject();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter TAG : ");
            String tag = in.readLine();
            System.out.println("Enter the to Node ID");
            String tonodeId = in.readLine();
            System.out.println("Enter the message");
            String cMessage = in.readLine();
            jsonobj.put("type", "CHAT");
            jsonobj.put("TAG", tag);
            jsonobj.put("node_id", Integer.toString(nodeId));
            jsonobj.put("ip_address", ipAddr.getHostAddress());
            jsonobj.put("tonode_id", tonodeId);
            jsonobj.put("message", cMessage);

            try {
                os = socket.getOutputStream();
             //is = socket.getInputStream();

                // BufferedReader br = new BufferedReader(new InputStreamReader(is));
                PrintWriter output = new PrintWriter(os, true);
                // String messagetoserver="Join_Bootstrap";
                output.println(jsonobj);

              //while(true){
                // String message = br.readLine();
                //JSONParser jsonInputParser = new JSONParser();
                // JSONObject jsonObject  = (JSONObject) jsonInputParser.parse(message);
                //}
            } catch (Exception er) {

            }

        } catch (Exception err) {
            System.out.println("error in joining " + err);
        }
    }

    public void init() throws Exception {

        Thread t = new Thread(this);
        t.start();

    }

    public static void main(String args[]) throws Exception {
        ClientRun clientRun = new ClientRun();
        clientRun.init();
    }

    @Override
    public void init(Socket socket, int uid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long joinNetwork(InetSocketAddress bootstrap_node) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        long x = 0;
        try {
            System.out.println("bootstrap_node.getAddress()" + bootstrap_node.getAddress());
            String ips = bootstrap_node.getAddress().toString();
            ips = ips.substring(ips.lastIndexOf('.') + 1, ips.length());
            System.out.println("ips" + ips);
            int nodeId = 0;
            nodeId = Integer.parseInt(ips);
            ipAddr = InetAddress.getLocalHost();
            JSONObject jsonobj = new JSONObject();

            jsonobj.put("type", "JOINING_NETWORK");
            jsonobj.put("node_id", Integer.toString(nodeId));
            jsonobj.put("ip_address", ipAddr.getHostAddress());

            socket = new Socket(bootstrap_node.getAddress(), bootstrap_node.getPort());
            //socket = new Socket();
            if (socket.isClosed() == false) {
                System.out.println("Connected with server");
//            WriteMesssage wm=new WriteMesssage(socket);
//            Thread tr=new Thread(wm);
//            tr.start();

                try {
                    os = socket.getOutputStream();
                    is = socket.getInputStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    PrintWriter output = new PrintWriter(os, true);
                    // String messagetoserver="Join_Bootstrap";
                    output.println(jsonobj);
                    System.out.println("waiting for server response :: ");
                    //while(true){
                    String message = br.readLine();
                    System.out.println(message);

                    String relay = br.readLine();

                    System.out.println("relay message from server");
                    System.out.println(relay);
                    //}
                    ReadMesssage rm = new ReadMesssage(socket);
                    Thread tr1 = new Thread(rm);
                    tr1.start();
                } catch (Exception er) {

                }

            } else {
                System.out.println("Client is not connected");
            }
        } catch (Exception err) {
            System.out.println("error in joining " + err);
        }
        return x;

    }

    @Override
    public boolean leaveNetwork(long network_id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void chat(String text, String[] tags) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("Select Your operation");
                System.out.println("1) Joining");
                System.out.println("2) Leaving");
                System.out.println("3) Chat");
                System.out.println("4) Ping");
            //System.out.println("4) View Routing List");

                System.out.println("Enter your choice");
                String message = in.readLine();
                int choice = 0;
                try {
                    choice = Integer.parseInt(message);
                } catch (Exception er) {

                }
                switch (choice) {
                    case 1:
                        joining();

                        break;
                    case 2:
                        leave();
                        break;
                    case 3:
                        //tr1.resume();
                        clientChat();

                        break;
                    case 4:
                   // PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
                        // String messagetoserver="Join_Bootstrap";
                        //output.println("stop");
                        ping();

                        break;
//                case 4: //View Routing List
//                   // ClientRun clientRun=new ClientRun();
//                    //clientRun.tr1.wait();
//                    routingList();
//                    break; 

                    default:
                        break;
                }

            }
        } catch (Exception ert) {

        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

class ReadMesssage implements Runnable {

    private static Socket socket;

    ReadMesssage(Socket rSocket) {
        socket = rSocket;
    }

    public void run() {

        try {
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while (true) {
                String message = br.readLine();
                JSONParser jsonInputParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonInputParser.parse(message);

                String jsonString = (String) jsonObject.get("type");

                if (jsonString.indexOf("ROUTING_LIST") != -1) {
                    // sr.routingInformation(socket, jsonObject);
                } else if (jsonString.indexOf("Chat_INFO") != -1) {
                    //sr.chat(socket,jsonObject);
                    System.out.println("message is " + (String) jsonObject.get("message"));
                } else if (jsonString.indexOf("CHAT_RESPONSE") != -1) {
                    // sr.leaveNetwork(jsonObject);
                    System.out.println("CHAT RESPONSE :: ");
                    System.out.println(message);
                } else if (jsonString.indexOf("PINGACK") != -1) {
                    // sr.ping(socket,jsonObject);
                    System.out.println("Ping ack :: ");
                    System.out.println(message);
                }
                else if (jsonString.indexOf("LEAVE") != -1) {
                    System.out.println("Bye Client");
                    System.exit(0);
                }
                //  System.out.println(message);
            }
        } catch (Exception e) {
            //System.exit(0);
        }
    }
}
