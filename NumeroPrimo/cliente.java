import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) {
        try {
            // Se crea un socket para conectarse al servidorB
            Socket cliente = new Socket("localhost", 50001);

            // Se crean los flujos de entrada y salida
            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
            DataInputStream dis = new DataInputStream(cliente.getInputStream());

            // Se solicita al cliente un número para despues mandarlo al servidorB
            System.out.print("Número: ");
            Scanner entrada = new Scanner(System.in);
            long numero = entrada.nextLong();
            dos.writeLong(numero);
            dos.flush();

            // Se recibe la respuesta de parte del servidorB para saber si ese número es
            // primo o no
            String resultado = dis.readUTF();
            System.out.println(resultado);

            // Se cierra la conexión
            cliente.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
