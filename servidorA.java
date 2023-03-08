import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class servidorA {
    static class Manejador extends Thread {
        Socket conexion;

        Manejador(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());
                DataInputStream dis = new DataInputStream(conexion.getInputStream());
                long numero = dis.readLong();
                long numero_inicial = dis.readLong();
                long numero_final = dis.readLong();
                String cadena = "NO DIVIDE";
                for (long i = numero_inicial; i <= numero_final; i++) {
                    if (numero % i == 0) {
                        cadena = "DIVIDE";
                    }
                }
                dos.writeUTF(cadena);
                dos.flush();
                conexion.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            

        }
    }

    public static void main(String[] args) {
        try {
            int puerto = 50000;
            ServerSocket servidor = new ServerSocket(puerto);
            for (;;) {
                Socket conexion = servidor.accept();
                new Manejador(conexion).start();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}