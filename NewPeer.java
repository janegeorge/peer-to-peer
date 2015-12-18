package newpeer;

/**
 *
 * @author Jane george
 */
public class NewPeer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
//        UserInterface userI=new UserInterface();
//        userI.setVisible(true);
        
   MainServer sr=new MainServer();
  Thread tr1=new Thread(sr);
  tr1.start();
   ClientRun clientRun=new ClientRun();
        clientRun.init();
    }
    
}
