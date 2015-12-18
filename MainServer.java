/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

/**
 *
 * @author Jane george
 */
public class MainServer implements Runnable {

    private static Socket socket[] = new Socket[100];
    private static String bootstapclient = "";
    ExecutorService executor = Executors.newFixedThreadPool(50);  //Thread Pool of 50
    static int i = 1; //client no initialized
    static List routeList = new ArrayList();
  // static List taguid=new ArrayList();
    // UserInterface ui;
    int port = 5000;
    static HashMap<String, String> routingInfo = new HashMap<String, String>();

    public MainServer() {
    }

    MainServer(UserInterface ui) {
        //  this.ui=ui;   
    }

    public static void main(String[] args) {
        try {
            MainServer sr = new MainServer();
            Thread tr1 = new Thread(sr);
            tr1.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {

        try {

            //    int flag=0;
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {

                System.out.println("Server Started and listening to the port 5000");
                socket[i] = serverSocket.accept();
                System.out.println("client" + i + " connected.");

                BufferedReader br = new BufferedReader(new InputStreamReader(socket[i].getInputStream()));
                String input = br.readLine();

                JSONParser jsonInputParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonInputParser.parse(input);

                String jsonString = (String) jsonObject.get("type");

                if (jsonString.indexOf("JOINING_NETWORK") != -1) {
                    joinNetwork(jsonObject);

                } else {
                    System.out.println("message" + input);
                    ReadMesssageServer rm = new ReadMesssageServer(socket[i], i);
                    Thread tr1 = new Thread(rm);
                    tr1.start();
                }

            }
        } catch (Exception e) {
            System.out.println("" + e);
        }
    }

    public void joinNetwork(JSONObject jsonObject) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        try {
            routingInfo.put((String) jsonObject.get("node_id"), (String) jsonObject.get("ip_address"));
            System.out.println("New Node id and Ip Indormation is added to routing list");
            System.out.println("Current Routing Table \n -----------");
            System.out.println(routingInfo);
            if (i == 1) {
                bootstapclient = (String) jsonObject.get("ip_address");
                System.out.println("bootstap client is" + bootstapclient);
            }
            routingInformation(socket[i], jsonObject);
            sendRelay(socket[i], jsonObject);
            ReadMesssageServer rm = new ReadMesssageServer(socket[i], i);
            Thread tr1 = new Thread(rm);
            tr1.start();
            i++;
        } catch (Exception eeer) {
            System.out.println("join JSON read" + eeer);
        }

    }

    public void routingInformation(Socket socket, JSONObject jsonInput) {
        try {

            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            JSONObject routingJson = new JSONObject();
            routingJson.put("type", "ROUTING_INFO");
            routingJson.put("node_id", (String) jsonInput.get("node_id"));
            routingJson.put("ip_address", (String) jsonInput.get("ip_address"));

            JSONArray routingInfoArray = new JSONArray();

            Iterator it = routingInfo.entrySet().iterator();
            while (it.hasNext()) {
                JSONObject routingEntries = new JSONObject();
                Map.Entry entry = (Map.Entry) it.next();
                routingEntries.put("node_id", entry.getKey());
                routingEntries.put("ip_address", entry.getValue());
                routingInfoArray.add(routingEntries);
            }

            routingJson.put("route_table", routingInfoArray);

            output.println(routingJson);

        } catch (Exception e) {
            System.out.println("Exception in sendRoutingInformation" + e);
        }
    }

    private void findClosest(JSONObject jsonobject) throws Exception {

        int nodeChecked = Integer.parseInt(jsonobject.get("tonode_id").toString());

        int nextNode = 0;
        int closest = 0;
        int intialDiff = 0;
        int diff = 0;
        String cNode = null;
        String closestIp = null;

        Iterator it = routingInfo.entrySet().iterator();

        if (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            nextNode = Integer.parseInt(entry.getKey().toString());
            closestIp = (String) entry.getValue();
            intialDiff = Math.abs(nextNode - nodeChecked);
            closest = nextNode;
        }

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            nextNode = Integer.parseInt(entry.getKey().toString());
            diff = Math.abs(nextNode - nodeChecked);
            if (diff < intialDiff) {
                closest = nextNode;
                closestIp = (String) entry.getValue();
                intialDiff = diff;
            }
        }

        System.out.println("CLOSEST NODE ::: " + closest);

        if (closest != nodeChecked) {
            routingInfo.put((String) jsonobject.get("node_id"), (String) jsonobject.get("ip_address"));
            System.out.println("Updating  routing list with new node id");
            System.out.println("Updated Routing Table \n -----------");
            System.out.println(routingInfo);
        } else {
            System.out.println("Updated Routing Table \n -----------");
            System.out.println(routingInfo);
        }
    }

    //method to send relay messages about peer joining
    private void sendRelay(Socket socket, JSONObject jsonobject) throws Exception {
        System.out.println("Sending Relay Messages");
        int nodeChecked = Integer.parseInt(jsonobject.get("node_id").toString());

        int nextNode = 0;
        int closest = 0;
        int intialDiff = 0;
        int diff = 0;
        String cNode = null;
        String closestIp = null;

        Iterator it = routingInfo.entrySet().iterator();

        if (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            nextNode = Integer.parseInt(entry.getKey().toString());
            closestIp = (String) entry.getValue();
            intialDiff = Math.abs(nextNode - nodeChecked);
            closest = nextNode;
        }

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            nextNode = Integer.parseInt(entry.getKey().toString());
            diff = Math.abs(nextNode - nodeChecked);
            if (diff < intialDiff) {
                closest = nextNode;
                closestIp = (String) entry.getValue();
                intialDiff = diff;
            }
        }

        System.out.println("CLOSEST NODE ::: " + closest);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("type", "JOINING_RELAY");
        jsonObj.put("node_id", jsonobject.get("node_id"));
        jsonObj.put("closestNode", closest);
        jsonObj.put("gateway_ip", (String) jsonobject.get("ip_address"));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        output.println(jsonObj);

    }

    public void ping(Socket socket1, JSONObject jsonobject) {
        try {
            
            
            Iterator it = routingInfo.entrySet().iterator();
            int j = 1, Targetflag = 0;
            while (it.hasNext()) {
                JSONObject routingEntries = new JSONObject();
                Map.Entry entry = (Map.Entry) it.next();
                routingEntries.put("node_id", entry.getKey());
                routingEntries.put("ip_address", entry.getValue());
                
                if (entry.getKey().toString().equals(jsonobject.get("sender_id"))) {
                    Targetflag = 0;
                    JSONObject pingJson = new JSONObject();
                        pingJson.put("type", "PINGACK");
                 pingJson.put("node_id", jsonobject.get("sender_id"));
                pingJson.put("ip_address", (String) jsonobject.get("ip_address"));
                    PrintWriter output1 = new PrintWriter(socket[j].getOutputStream(), true);
                    output1.println(pingJson);
                    break;
                } else {
                    Targetflag = 1;
                }
                j++;
                //routingInfoArray.add(routingEntries);
            }
            j = 1;
            if (Targetflag == 1) {
                System.out.println("Target node is not available in my routing list");
            }
            
//            
//            
//            
//            
//            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
//            JSONObject pingJson = new JSONObject();
//            pingJson.put("type", "PINGACK");
//            pingJson.put("node_id", jsonobject.get("sender_id"));
//            pingJson.put("ip_address", (String) jsonobject.get("ip_address"));
//            // output.println("");
//            output.println(pingJson);
        } catch (Exception e) {
            System.out.println("Exception in sendACK");
        }
    }

    public void chat(Socket socket1, JSONObject jsonobject) {
        try {
            findClosest(jsonobject);
            JSONObject chatJson = new JSONObject();
            chatJson.put("type", "CHAT_RESPONSE");
            chatJson.put("TAG", (String) jsonobject.get("TAG"));
            chatJson.put("node_id", (String) jsonobject.get("node_id"));
            chatJson.put("sender_id", (String) jsonobject.get("tonode_id"));

            Iterator it = routingInfo.entrySet().iterator();
            int j = 1, Targetflag = 0;
            while (it.hasNext()) {
                //JSONObject routingEntries = new JSONObject();
                Map.Entry entry = (Map.Entry) it.next();
                //routingEntries.put("node_id", entry.getKey());
                //routingEntries.put("ip_address", entry.getValue());
                // System.out.println("entry.getKey().toString()"+entry.getKey().toString()+" ...."+(String)jsonobject.get("node_id"));
                if (entry.getKey().toString().equals((String) jsonobject.get("node_id"))) {
                    PrintWriter output = new PrintWriter(socket[j].getOutputStream(), true);
                    output.println(chatJson);
                }

                if (entry.getKey().toString().equals((String) jsonobject.get("tonode_id"))) {
                    Targetflag = 0;
                    JSONObject routingJson = new JSONObject();
                    routingJson.put("type", "Chat_INFO");
                    routingJson.put("TAG", (String) jsonobject.get("node_id"));
                    routingJson.put("node_id", (String) jsonobject.get("node_id"));
                    routingJson.put("ip_address", (String) jsonobject.get("ip_address"));
                    routingJson.put("tonode_id", (String) jsonobject.get("tonode_id"));
                    routingJson.put("message", (String) jsonobject.get("message"));
                    PrintWriter output1 = new PrintWriter(socket[j].getOutputStream(), true);
                    output1.println(routingJson);
                   // break;
                } else {
                    Targetflag = 1;
                }
                j++;
                //routingInfoArray.add(routingEntries);
            }
            j = 1;
            if (Targetflag == 1) {
                System.out.println("Target node is not available in my routing list");
            }
                       // routingJson.put("route_table",routingInfoArray);

        } catch (Exception e) {
            System.out.println("Exception in" + e);
            e.printStackTrace();
        }
    }

    public boolean leaveNetwork(long network_id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void chat(String text, String[] tags) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void addToRouteTable(JSONObject jsonObject) {
        routingInfo.put((String) jsonObject.get("node_id"), (String) jsonObject.get("ip_address"));
        System.out.println("Current Routing Table");
        System.out.println(routingInfo);
    }

    //method for the client to leave a network
    public void leaveNetwork(JSONObject jsonobject) throws Exception{
         Iterator it = routingInfo.entrySet().iterator();
         System.out.println("routingInfo"+routingInfo.size());
         if(routingInfo.size()>1){
            int j = 1, bootflag = 0;
            while (it.hasNext()) {
                //JSONObject routingEntries = new JSONObject();
                Map.Entry entry = (Map.Entry) it.next();
                 if(bootflag==1){
                  bootstapclient= entry.getValue().toString();
                  bootflag=0;
              }

                if (entry.getKey().toString().equals((String) jsonobject.get("node_id"))) {
                    if(bootstapclient.equals((String) jsonobject.get("ip_address"))){
                        bootflag=1;
                   routingInfo.remove((String) jsonobject.get("node_id"));
                   System.out.println("Routing Table after removing the node");
                   System.out.println(routingInfo);
                    JSONObject routingJson = new JSONObject();
                    routingJson.put("type", "LEAVE");
                    routingJson.put("routingInfo", routingInfo);
                    PrintWriter output1 = new PrintWriter(socket[j].getOutputStream(), true);
                    output1.println(routingJson);
                    break;
                    }
                    else{
                      routingInfo.remove((String) jsonobject.get("node_id"));
                   System.out.println("Routing Table after removing the node");
                   System.out.println(routingInfo);
                    JSONObject routingJson = new JSONObject();
                    routingJson.put("type", "LEAVE");
                    routingJson.put("routingInfo", routingInfo);
                    PrintWriter output1 = new PrintWriter(socket[j].getOutputStream(), true);
                    output1.println(routingJson);
                    break;
                    }
                }
                j++;
                //routingInfoArray.add(routingEntries);
            }
            j = 1;
           
         }
         else{
             System.out.println("Sorry!!! Cant Leave Bootstrap node");
         }
            ///////////
        
    }
}

class ReadMesssageServer implements Runnable {

    private static Socket socket;
    int ivalue = 0;

    ReadMesssageServer(Socket rSocket, int i) {
        socket = rSocket;
        ivalue = i;
        System.out.println("constructor " + ivalue);
    }

    public void run() {
        try {
            MainServer sr = new MainServer();
            System.out.println("Waiting for client response");
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while (true) {
                String message = br.readLine();
//                if(!message.equals("")){
                // System.out.println("Message received from client"+ivalue+" is :: "+message);
//                
//                }
                JSONParser jsonInputParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonInputParser.parse(message);

                String jsonString = (String) jsonObject.get("type");
                if (jsonString.indexOf("JOINING_NETWORK") != -1) {
                    sr.joinNetwork(jsonObject);

                } else if (jsonString.indexOf("ROUTING_LIST") != -1) {
                    sr.routingInformation(socket, jsonObject);
                } else if (jsonString.indexOf("CHAT") != -1) {
                    sr.chat(socket, jsonObject);
                } else if (jsonString.indexOf("LEAVE") != -1) {
                    sr.leaveNetwork(jsonObject);
                } else if (jsonString.indexOf("PING") != -1) {
                    sr.ping(socket, jsonObject);
                }
                if (message.equalsIgnoreCase("Bye")) {

                    int j = --sr.i;
                    System.out.println("j value" + j);
                    try {

                        if (j == 1) {
                            socket.close();
                        }
                    } catch (Exception e) {
                    }
                    System.out.println("Bye client" + ivalue + " ....");
                    if (j == 1) {
                        System.out.println("All Clients close their connections from pool");
                        System.out.println("Bye...");
                        System.exit(0);
                    }
                } else {
                    // System.out.println("Server message");
                }
            }
        } catch (Exception e) {
            //System.out.println("reading " + e);
            //e.printStackTrace();
        }
    }
}
