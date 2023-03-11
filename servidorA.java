import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class servidorA {
    static class Manejador extends Thread {
        Socket conexion;

        Manejador(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                // Se abren los flujos de entrada y salida
                DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());
                DataInputStream dis = new DataInputStream(conexion.getInputStream());

                // Se leen los intervalos y el número del servidorB
                long numero = dis.readLong();
                long numero_inicial = dis.readLong();
                long numero_final = dis.readLong();
                String cadena = "NO DIVIDE";

                // Se hace un ciclo para cambiar el estado de "NO DIVIDE" a "DIVIDE" en caso de
                // encontrar un divisor
                for (long i = numero_inicial; i <= numero_final; i++) {
                    if (numero % i == 0) {
                        cadena = "DIVIDE";
                        // Se rompe el ciclo para evitar iteraciones innecesarias
                        break;
                    }
                }
                // Se manda al servidorB la respuesta si se encontro algun divisor
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
            // Se solicita al usuario el puerto a utilizar para asi crear 3 instancias del
            // servidorA con el mismo programa
            System.out.print("Puerto a utilizar: ");
            Scanner entrada = new Scanner(System.in);
            int puerto = entrada.nextInt();
            // Se crea el servidor con el puerto que coloco el usuario
            ServerSocket servidor = new ServerSocket(puerto);
            for (;;) {
                System.out.println("Servidor Iniciado");
                // En espera de una conexión para que después empece a ejecutarse en un hilo
                // diferente
                Socket conexion = servidor.accept();
                new Manejador(conexion).start();
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}