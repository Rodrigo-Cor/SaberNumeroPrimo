package ServidorSocketsSeguros;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class SecureClientGET {

    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        String trustStoreFile = "keystore_cliente.jks";
        String trustStorePassword = "1234567";
        System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

        // Verificar que se hayan proporcionado los argumentos necesarios
        if (args.length != 3) {
            System.err
                    .println("Uso: java SecureClientGET <IP del servidor> <puerto del servidor> <nombre del archivo>");
            return;
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String filename = args[2];

        try {
            // Establecer la conexión con el servidor
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverIP, serverPort);
            System.out
                    .println("Conectado al servidor " + InetAddress.getLocalHost().getHostAddress() + ":" + serverPort);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            // Enviar la petición GET al servidor
            String request = "GET " + filename + "\r\n";
            outputStream.write(request.getBytes());

            // Leer la respuesta del servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String response = reader.readLine();
            if (response != null && response.equals("OK")) {
                // lectura de la longitud del archivo
                String lengthString = reader.readLine();
                long length = Long.parseLong(lengthString);

                // lectura del archivo y escritura en disco
                FileOutputStream fileOutputStream = new FileOutputStream(filename);
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                long totalBytesRead = 0;
                while (totalBytesRead < length && (bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
                fileOutputStream.close();

                if (totalBytesRead == length) {
                    System.out.println("Archivo recibido con éxito.");
                } else {
                    System.err.println("No se pudo recibir el archivo.");
                }
            } else {
                System.err.println("El servidor respondió con un error.");
            }
        } catch (IOException e) {
            System.err.println("Error al establecer la conexión con el servidor: " + e.getMessage());
            return;
        }
    }

}
