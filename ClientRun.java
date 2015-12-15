/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpeer;

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
public class ClientRun implements PeerChat{
     private static Socket socket;
     private static OutputStream os;
     private static InputStream is;
     private int port = 5000;
     InetAddress ipAddr;
 public static void joining(){
      try
        {
             
            String host = "192.168.0.6";
            int port = 5000;
            //InetAddress address = InetAddress.getByName(host);
            InetAddress remoteAddress=InetAddress.getByName(host);
            InetSocketAddress remote=new InetSocketAddress(remoteAddress,port);
            ClientRun clientRun=new ClientRun();
            clientRun.joinNetwork(remote);
           
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
 }
   
    public static void main(String args[]) throws Exception
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            System.out.println("Select Your operation");
            System.out.println("1) Joining");
            System.out.println("2) Leaving");
            System.out.println("3) View Bootstrap node");
            System.out.println("4) View Routing List");
            System.out.println("5) Chat");
            System.out.println("Enter your choice");
            String message=in.readLine();
            int choice=0;
            try{
              choice=Integer.parseInt(message);  
            }
            catch(Exception er){
                
            }
            switch(choice){
                case 1: 
                    joining();
                   
                    break;
                case 2: 
                    
                    break;
                case 3: 
                    System.out.println("Bootstrap node is : 127.0.0.1");
                    break;  
                case 4: 
                    break; 
                case 5: 
                    break;
                default: break;    
            }
          
        } 
       

    }

    @Override
    public void init(Socket socket, int uid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long joinNetwork(InetSocketAddress bootstrap_node) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       long x=0;
        try{
            System.out.println("bootstrap_node.getAddress()"+bootstrap_node.getAddress());
            String ips=bootstrap_node.getAddress().toString();
            ips=ips.substring(ips.lastIndexOf('.')+1,ips.length());
            System.out.println("ips"+ips);
            int nodeId=0;
            nodeId=Integer.parseInt(ips);
            ipAddr = InetAddress.getLocalHost();
            JSONObject jsonobj = new JSONObject();
        
            jsonobj.put("type","JOINING_NETWORK");
            jsonobj.put("node_id",Integer.toString(nodeId));
            jsonobj.put("ip_address",ipAddr.getHostAddress());
			
         socket = new Socket( bootstrap_node.getAddress(), bootstrap_node.getPort());
            //socket = new Socket();
            if(socket.isClosed()==false){
            System.out.println("Connected with server");
//            WriteMesssage wm=new WriteMesssage(socket);
//            Thread tr=new Thread(wm);
//            tr.start();
//            ReadMesssage rm=new ReadMesssage(socket);
//            Thread tr1=new Thread(rm);
//            tr1.start();
            try{
             os = socket.getOutputStream();
             is = socket.getInputStream();
             
             BufferedReader br = new BufferedReader(new InputStreamReader(is));
             PrintWriter output = new PrintWriter(os,true);
            // String messagetoserver="Join_Bootstrap";
             output.println(jsonobj);
              System.out.println("waiting for server response :: ");
              //while(true){
            String message = br.readLine();
//            if(message.equals("join_success"))
//            {
//                System.exit(0);
//            }
//           
            //System.out.println("Enter your message");
            System.out.println(message);
             //}
            }
            catch(Exception er){
                
            }
            
            
            }
            else{
               System.out.println("Client is not connected"); 
            }
        }
        catch(Exception err){
            System.out.println("error in joining "+err);
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
}
class WriteMesssage implements Runnable{
       private static Socket socket;
       WriteMesssage(Socket wSocket){
           socket=wSocket;
       }
   public void run(){
            try{
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                while(true){
                   System.out.println("Enter your message");
                   String message=in.readLine();
            bw.write(message+"\n");
                 if(message.equalsIgnoreCase("Bye")){
                     try
            {
                break;
                //Thread.sleep(1000);
                //socket.close();
                //System.exit(0);
            }
            catch(Exception e){}
                     
                }
                
            bw.flush();
               }
            bw.flush();
            System.exit(0);
            }
            catch(Exception e){
                
            }
     
    } 
}
class ReadMesssage implements Runnable{
       private static Socket socket;
       ReadMesssage(Socket rSocket){
           socket=rSocket;
       }
  public void run(){
 
            try{
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
           while(true){
            String message = br.readLine();
            if(message.equals("null"))
            {
                System.exit(0);
            }
            System.out.println("Message received from the server :: " +message);
            //System.out.println("Enter your message");
            System.out.println(message);
             }
            }
            catch(Exception e){
                System.exit(0);
            }  
    }  
}
