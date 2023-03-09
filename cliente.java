import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) {
        try {
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
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
