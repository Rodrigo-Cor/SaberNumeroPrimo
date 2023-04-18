package ChatMulticast;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MulticastChat {
    private static final String MULTICAST_ADDRESS = "239.0.0.0";
    private static final int PORT = 50000;
    private static final int MESSAGE_LENGTH = 100;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Uso: java MulticastChat <nombre_usuario>");
            System.exit(1);
        }

        String username = args[0];
        InetAddress multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);

        // Obtener la interfaz de red correcta para unirse al grupo multicast
        NetworkInterface multicastInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

        MulticastSocket multicastSocket = new MulticastSocket(PORT);
        multicastSocket.joinGroup(new java.net.InetSocketAddress(multicastAddress, PORT), multicastInterface);

        ReceiverThread receiverThread = new ReceiverThread(username, multicastSocket);
        receiverThread.start();


        while (true) {
		System.out.print("Escribe tu mensaje: ");
            String text = System.console().readLine();
            
            if (text.isEmpty()) {
                continue;
            }

            // Limitar el mensaje a la longitud especificada
            if (text.length() > MESSAGE_LENGTH) {
                text = text.substring(0, MESSAGE_LENGTH);
            }

            String message = username + "--->" + text;
            message = String.format("%1$-" + MESSAGE_LENGTH + "s", message);
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buffer, MESSAGE_LENGTH, multicastAddress, PORT);
            multicastSocket.send(packet);
        }
    }

    private static class ReceiverThread extends Thread {
        private final String username;
        private final MulticastSocket multicastSocket;

        public ReceiverThread(String username, MulticastSocket multicastSocket) {
            this.username = username;
            this.multicastSocket = multicastSocket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    byte[] buffer = new byte[MESSAGE_LENGTH];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                    String[] parts = message.split("--->");

                    if (parts.length == 2) {
                        String sender = parts[0];
                        String text = parts[1];

                        if (!sender.equals(username)) {
                            System.out.printf("\n%s--->%s%n", sender, text);
                            System.out.print("Escribe tu mensaje: ");
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}