package ServidorSocketsSeguros;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SecureServer {

    // directorio donde se guardan los archivos
    private static final String FILES_DIRECTORY = "files/";

    // puerto del servidor
    private static final int SERVER_PORT = 50000;

    public static void main(String[] args) {
        //Crear los repositorios y par de llaves de confianza para el cliente y servidor
        String keyStoreFile = "keystore_servidor.jks";
        String keyStorePassword = "1234567";
        System.setProperty("javax.net.ssl.keyStore", keyStoreFile);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
        
        
        try {
            // creación del socket seguro
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory
                    .getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(SERVER_PORT);
            System.out
                    .println("Servidor iniciado en " + InetAddress.getLocalHost().getHostAddress() + ":" + SERVER_PORT);

            // aceptación de conexiones entrantes
            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("Nueva conexión desde " + clientSocket.getInetAddress().getHostAddress());

                // creación de un nuevo thread para manejar la conexión
                Thread workerThread = new SecureServerWorkerThread(clientSocket);
                workerThread.start();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clase que maneja las conexiones entrantes del servidor
     */
    private static class SecureServerWorkerThread extends Thread {

        private SSLSocket clientSocket;

        public SecureServerWorkerThread(SSLSocket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                // creación de streams de entrada y salida para la comunicación con el cliente
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                // lectura de la petición del cliente
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String request = reader.readLine();
                long contentLength = -1;

                if (request.startsWith("GET ")) {
                    // En el caso de una petición GET no es necesario leer la longitud del archivo
                } else if (request.startsWith("PUT ")) {
                    // En el caso de una petición PUT se espera leer la longitud del archivo
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Content-Length:")) {
                            contentLength = Long.parseLong(line.substring("Content-Length:".length()).trim());
                            break;
                        }
                    }
                    if (contentLength == -1) {
                        System.err.println("Petición inválida: falta la longitud del archivo.");
                        return;
                    }
                } else {
                    // En el caso de una petición no soportada, se responde con un error
                    outputStream.write("ERROR\n".getBytes());
                    return;
                }

                if (request != null) {
                    // separación de la petición en sus partes: verbo, nombre del archivo y longitud
                    // del archivo
                    String[] parts = request.split(" ");
                    String verb = parts[0];
                    String filename = parts[1];
                    long fileSize = contentLength;
                    if (verb.equals("GET")) {
                        // petición GET: lectura del archivo y envío al cliente
                        File file = new File(FILES_DIRECTORY + filename);
                        if (file.exists() && file.canRead()) {
                            fileSize = file.length();
                            FileInputStream fileInputStream = new FileInputStream(file);
                            byte[] fileContents = new byte[(int) fileSize];
                            int totalBytesRead = 0;
                            while (totalBytesRead < fileSize) {
                                int bytesRead = fileInputStream.read(fileContents, totalBytesRead,
                                        (int) fileSize - totalBytesRead);
                                if (bytesRead == -1) {
                                    break;
                                }
                                totalBytesRead += bytesRead;
                            }
                            fileInputStream.close();
                            if (totalBytesRead == fileSize) {
                                // envío de la respuesta al cliente
                                outputStream.write("OK\n".getBytes());
                                outputStream.write((fileSize + "\n").getBytes());
                                outputStream.write(fileContents);
                            } else {
                                outputStream.write("ERROR\n".getBytes());
                            }
                        } else {
                            outputStream.write("ERROR\n".getBytes());
                        }
                    }

                    else if (verb.equals("PUT")) {
                        // petición PUT: escritura del archivo en el servidor
                        FileOutputStream fileOutputStream = new FileOutputStream(FILES_DIRECTORY + filename);
                        byte[] buffer = new byte[4096];
                        long totalBytesRead = 0;
                        while (fileSize > 0) {
                            int bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize));
                            if (bytesRead == -1) {
                                break;
                            }
                            fileOutputStream.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;
                            System.out.println(totalBytesRead);
                            fileSize -= bytesRead;
                        }
                        fileOutputStream.close();

                        if (totalBytesRead == contentLength) {
                            // envío de la respuesta al cliente
                            outputStream.write("OK\n".getBytes());
                        } else {
                            outputStream.write("ERROR\n".getBytes());
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
