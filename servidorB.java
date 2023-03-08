import java.net.ServerSocket;

public class servidorB {  
     public static void main(String[] args) {
        try {
            int puerto = 500001;
            ServerSocket ss = new ServerSocket(puerto);
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}
