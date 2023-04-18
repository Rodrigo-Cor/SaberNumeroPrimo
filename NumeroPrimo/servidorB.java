import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class servidorB {
    // Variable que se ocupará para ver si un número es primo o no porque un número
    // primo con ser divisible con otro número que no sea el mismo o el 1, ya no lo
    // es. Entonces se considera como estado inicial que todos los numeros son
    // primos
    static String cadenaR = "NO DIVIDE";

    // Clase para crear las instancias de conexiones a los servidores A en diferentes hilos
    static class servidorA extends Thread {
        // Puerto, numero del cliente, los extremos de los intervalos y la cadena que se
        // recibe del servidor A
        int puerto;
        long n;
        long numeroI;
        long numeroF;
        String cadena;

        // Constructor de cada objeto del servidor A
        servidorA(int puerto, long n, long numeroI, long numeroF) {
            this.puerto = puerto;
            this.n = n;
            this.numeroI = numeroI;
            this.numeroF = numeroF;
        }

        // Lo que hará cada hilo
        public void run() {
            try {
                // Se crea la conexión entre el servidorB y el servidorA con el puerto de la
                // instancia del servidor A
                Socket conexion = new Socket("localhost", puerto);

                // Se crean los flujos de entrada y salida
                DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());
                DataInputStream dis = new DataInputStream(conexion.getInputStream());

                // Se mandan al servidorA el número del cliente y los intervalos
                dos.writeLong(n);
                dos.flush();
                dos.writeLong(numeroI);
                dos.flush();
                dos.writeLong(numeroF);
                dos.flush();

                // Se recibe la respuesta del servidorA
                cadena = dis.readUTF();

                // Se crea un objeto auxiliar para sincronizar con los hilos creados para la
                // modificación de la variable y en caso de encontrar un "DIVIDE", la variable
                // cadenaR se volverá "DIVIDE" porque ya no hay manera de que sea un primo
                Object aux = new Object();
                synchronized (aux) {
                    if (cadena.compareTo("DIVIDE") == 0) {
                        cadenaR = "DIVIDE";
                    }
                }

                // Se cierra la conexión
                conexion.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    // Main
    public static void main(String[] args) {
        try {
            // Se inicia el servidor en el puerto 50001
            int puerto = 50001;
            ServerSocket servidor = new ServerSocket(puerto);
            System.out.println("Servidor Iniciado");

            // Se espera una conexión
            Socket conexion = servidor.accept();

            // Se crean los flujos de entrada y salida
            DataInputStream dis = new DataInputStream(conexion.getInputStream());
            DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());

            // Se lee el número que manda el cliente
            long numeroCliente = dis.readLong();
            long k = numeroCliente / 3;

            // Se crean las 3 instancias del servidor A con sus respectivos puertos y sus
            // intervalos
            servidorA s1 = new servidorA(50002, numeroCliente, 2, k);
            servidorA s2 = new servidorA(50003, numeroCliente, k + 1, 2 * k);
            servidorA s3 = new servidorA(50004, numeroCliente, (2 * k) + 1, numeroCliente - 1);

            // Se inicia la ejecución de los 3 hilos
            s1.start();
            s2.start();
            s3.start();

            // Se espera a que termine cada uno de los hilos para obtener la respuesta
            // completa
            s1.join();
            s2.join();
            s3.join();

            // En caso de que la variable cadenaR sea igual al "NO DIVIDE" significa que es
            // primo, caso contrario no lo es y se le manda al cliente la respuesta
            if (cadenaR.compareTo("NO DIVIDE") == 0) {
                dos.writeUTF("ES PRIMO");

            } else {
                dos.writeUTF("NO ES PRIMO");

            }
            dos.flush();

            //Se cierra la conexión
            servidor.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
