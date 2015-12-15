/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpeer;

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
public class MainServer implements Runnable{
    private static Socket socket;
    private static String bootstapclient="";
  ExecutorService executor = Executors.newFixedThreadPool(50);  //Thread Pool of 50
   static int i=1; //client no initialized
   static List routeList=new ArrayList();
  // static List taguid=new ArrayList();
  // UserInterface ui;
    int port = 5000;
   static HashMap<String,String> routingInfo = new HashMap<String,String>();
    public MainServer() {
    }
   MainServer(UserInterface ui){
  //  this.ui=ui;   
   }
  public static void main(String[] args)
    {
        try
        {
 MainServer sr=new MainServer();
  Thread tr1=new Thread(sr);
  tr1.start();
            
          
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
     
    } 
//    private void socketWork(ExecutorService executor, final Socket socket,final int ivalue) {
//    executor.execute(new Runnable() {
//        public void run() {
//             ReadMesssageServer rm=new ReadMesssageServer(socket,ivalue);
//            Thread tr1=new Thread(rm);
//            tr1.start();  
//            
//        }
//    });
//}
     public void run(){

            try{
              
           
        //    int flag=0;
            ServerSocket serverSocket = new ServerSocket(port);
              while(true){
                 // System.out.println("i value"+i);
                 // if(i<=5){
            System.out.println("Server Started and listening to the port 5000");  
            socket = serverSocket.accept();
            System.out.println("client"+i+" connected.");
            //socketWork(executor, socket,i);
//            String ips=socket.getInetAddress().toString();
//            ips=ips.substring(ips.lastIndexOf('.')+1,ips.length());
//            System.out.println("ips"+ips);
//            int uid=0;
//            uid=Integer.parseInt(ips);
//            init(socket,uid); 
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input = br.readLine();
					
            JSONParser jsonInputParser = new JSONParser();
            JSONObject jsonInput  = (JSONObject) jsonInputParser.parse(input);

            String jsonString = (String)jsonInput.get("type");

            if(jsonString.indexOf("JOINING_NETWORK")!=-1)
            {
                    joinNetwork(jsonInput);
                    sendRoutingInformation(socket,jsonInput);
            }
           
            
            //routeList.add(socket.getInetAddress());
            //ui.listModel.addElement(socket.getInetAddress());
           
//            InetAddress remoteAddress=InetAddress.getByName(socket.getInetAddress().toString());
//            InetSocketAddress remote=new InetSocketAddress(remoteAddress,port);
//            MainServer mainServer=new MainServer();
//            mainServer.joinNetwork(remote);
         //    System.out.println("routeList"+routeList.get(0));
         //   i++;
           // flag=0;

                }
            }
            catch(Exception e){
                System.out.println(""+e);  
            }
         } 

   

    
    public void joinNetwork(JSONObject jsonInput) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       try{
        routingInfo.put((String)jsonInput.get("node_id"), (String)jsonInput.get("ip_address"));
		System.out.println("Current Routing Table \n -----------");
		System.out.println(routingInfo);
                 if(i==1){
                bootstapclient=(String)jsonInput.get("ip_address");
                System.out.println("bootstap client is"+bootstapclient);
               }
                 i++;
       }
       catch(Exception eeer){
           System.out.println("join JSON read"+eeer);
       }
                
    }

   public void sendRoutingInformation(Socket socket,JSONObject jsonInput)
	{
		try
		{
                 
			PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
		
			JSONObject routingJson = new JSONObject();
			routingJson.put("type","ROUTING_INFO");
		        routingJson.put("node_id",(String)jsonInput.get("node_id"));
			routingJson.put("ip_address",(String)jsonInput.get("ip_address"));
			
			JSONArray routingInfoArray = new JSONArray();
			
			Iterator it = routingInfo.entrySet().iterator();
                        while (it.hasNext()) 
                        {
                            JSONObject routingEntries = new JSONObject();
                            Map.Entry entry = (Map.Entry)it.next();
                            routingEntries.put("node_id", entry.getKey());
                            routingEntries.put("ip_address", entry.getValue());
                            routingInfoArray.add(routingEntries);
                        }
			
                        routingJson.put("route_table",routingInfoArray);
					
			output.println(routingJson);
			
	
		}
		catch(Exception e)
		{
			System.out.println("Exception in sendRoutingInformation"+e);
		}
	}
    public boolean leaveNetwork(long network_id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void chat(String text, String[] tags) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

private void addToRouteTable(JSONObject jsonObject) 
	{
		routingInfo.put((String)jsonObject.get("node_id"), (String)jsonObject.get("ip_address"));
		System.out.println("Current Routing Table");
		System.out.println(routingInfo);
	}

	//method for the client to leave a network
	public void leaveNetwork(JSONObject jsonObject)
	{
               
		routingInfo.remove((String)jsonObject.get("node_id"));
		System.out.println("Routing Table after removing the node");
		System.out.println(routingInfo);
	}
}
class ReadMesssageServer implements Runnable{
       private static Socket socket;
       int ivalue=0;
       ReadMesssageServer(Socket rSocket,int i){
           socket=rSocket;
           ivalue=i;
           System.out.println("constructor "+ivalue);
       }
  public void run(){
          try{
              MainServer sr=new MainServer();
            System.out.println("Waiting for client response");
           InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                while(true){
                String message = br.readLine();
                if(!message.equals("")){
                System.out.println("Message received from client"+ivalue+" is :: "+message);
                
                }
                    if (message.equalsIgnoreCase("Bye")) {

                        int j = --sr.i;
                        System.out.println("j value"+j);
                        try {

                            if (j == 1) {
                                socket.close();
                            }
                        } catch (Exception e) {
                        }
                        System.out.println("Bye client"+ivalue+" ....");
                        if (j == 1) {
                             System.out.println("All Clients close their connections from pool");
                             System.out.println("Bye...");
                            System.exit(0);
                        }
                    } else {
                        // System.out.println("Server message");
                }
            }
            }
            catch(Exception e){
                
            }
        }
}
