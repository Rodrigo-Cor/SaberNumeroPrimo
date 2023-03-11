import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) {
        try {
            //Se crea un socket para conectarse al servidorB para después solicitarle al usuario un número el cual 
            Socket cliente = new Socket("localhost", 50001);
            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
            DataInputStream dis = new DataInputStream(cliente.getInputStream());
            System.out.print("Número: ");
            Scanner entrada = new Scanner(System.in);
            long numero = entrada.nextLong();
            dos.writeLong(numero);
            dos.flush();
            String resultado = dis.readUTF();
            System.out.println(resultado);
            
            cliente.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
