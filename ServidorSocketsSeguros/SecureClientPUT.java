package ServidorSocketsSeguros;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class SecureClientPUT {

    public static void main(String[] args) {
        String trustStoreFile = "keystore_cliente.jks";
        String trustStorePassword = "1234567";
        System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

        // Verificar que se hayan proporcionado los argumentos necesarios
        if (args.length != 3) {
            System.err
                    .println("Uso: java SecureClientPUT <IP del servidor> <puerto del servidor> <nombre del archivo>");
            return;
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String filename = args[2];
        // Leer el archivo
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            System.err.println("El archivo especificado no existe");
            return;
        }
        byte[] fileContent = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            long bytesRead = 0;
            long totalBytesRead = 0;
            long longitud = fileContent.length;
            while (longitud > 0 && (bytesRead = fileInputStream.read(fileContent, (int)totalBytesRead, (int)longitud)) != -1) {
                totalBytesRead += bytesRead;
                longitud -= bytesRead;
            }
            if (totalBytesRead != fileContent.length) {
                System.err.println("Error al leer el archivo");
                return;
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        try {
            // Establecer la conexión con el servidor
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverIP, serverPort);
            System.out
                    .println("Conectado al servidor " + InetAddress.getLocalHost().getHostAddress() + ":" + serverPort);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            // Enviar la petición PUT al servidor
            String request = "PUT " + filename + "\r\n";
            request += "Content-Length: " + fileContent.length + "\r\n";
            request += "\r\n";
            outputStream.write(request.getBytes());
            outputStream.write(fileContent);
            outputStream.flush();

            // Leer la respuesta del servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String response = reader.readLine();
            if (response.equals("OK")) {
                System.out.println(response);
                System.out.println("Archivo recibido con exito");
            } else {
                System.err.println("El servidor no pudo escribir el archivo");
            }

        } catch (IOException e) {
            System.err.println("Error al establecer la conexión con el servidor: " + e.getMessage());
            return;
        }
    }
}
