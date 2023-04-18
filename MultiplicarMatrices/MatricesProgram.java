import java.io.*;
import java.net.*;

public class MatricesProgram {

    //Número de nodos
    private static final int NODE_0 = 0;
    private static final int NODE_1 = 1;
    private static final int NODE_2 = 2;
    private static final int NODE_3 = 3;

    //Direcciones IP Publicas de los nodos
    private static final String NODE_1_IP = "20.169.32.40";
    private static final String NODE_2_IP = "20.38.6.113";
    private static final String NODE_3_IP = "20.163.120.158";
    
    //Puertos para hacer la conexión
    private static final int NODE_1_PORT = 50000;
    private static final int NODE_2_PORT = 50000;
    private static final int NODE_3_PORT = 50000;    
    

    private static final int MATRIX_SIZE = 3000;

    public static void printMatrix(double[][] matriz) {
        int filas = matriz.length;
        int columnas = matriz[0].length;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(matriz[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        int nodeNumber = Integer.parseInt(args[0]);

        if (nodeNumber == NODE_0) {
            // Inicializar matrices
            double[][] A = new double[MATRIX_SIZE][MATRIX_SIZE];
            double[][] B = new double[MATRIX_SIZE][MATRIX_SIZE];
            double[][] C = new double[MATRIX_SIZE][MATRIX_SIZE];

            for (int i = 0; i < MATRIX_SIZE; i++) {
                for (int j = 0; j < MATRIX_SIZE; j++) {
                    A[i][j] = (2 * i) + j;
                    B[i][j] = (3 * i) - j;
                }
            }

            // Transponer matriz B
            for (int i = 0; i < MATRIX_SIZE; i++) {
                for (int j = i + 1; j < MATRIX_SIZE; j++) {
                    double temp = B[i][j];
                    B[i][j] = B[j][i];
                    B[j][i] = temp;
                }
            }

            int subMatrixSize = MATRIX_SIZE / 3;
            double[][] A1 = new double[subMatrixSize][MATRIX_SIZE];
            double[][] A2 = new double[subMatrixSize][MATRIX_SIZE];
            double[][] A3 = new double[subMatrixSize][MATRIX_SIZE];
            double[][] BT1 = new double[subMatrixSize][MATRIX_SIZE];
            double[][] BT2 = new double[subMatrixSize][MATRIX_SIZE];
            double[][] BT3 = new double[subMatrixSize][MATRIX_SIZE];

            for (int i = 0; i < subMatrixSize; i++) {
                System.arraycopy(A[i], 0, A1[i], 0, MATRIX_SIZE);
                System.arraycopy(A[i + subMatrixSize], 0, A2[i], 0, MATRIX_SIZE);
                System.arraycopy(A[i + (2 * subMatrixSize)], 0, A3[i], 0, MATRIX_SIZE);
                System.arraycopy(B[i], 0, BT1[i], 0, MATRIX_SIZE);
                System.arraycopy(B[i + subMatrixSize], 0, BT2[i], 0, MATRIX_SIZE);
                System.arraycopy(B[i + (2 * subMatrixSize)], 0, BT3[i], 0, MATRIX_SIZE);

            }

            // Conectar con nodos 1, 2 y 3
            try (Socket socket1 = new Socket(NODE_1_IP, NODE_1_PORT);
                    Socket socket2 = new Socket(NODE_2_IP, NODE_2_PORT);
                    Socket socket3 = new Socket(NODE_3_IP, NODE_3_PORT)) {

                // Enviar submatrices a los nodos correspondientes
                ObjectOutputStream out1 = new ObjectOutputStream(socket1.getOutputStream());
                out1.writeObject(A1);
                out1.writeObject(BT1);
                out1.writeObject(BT2);
                out1.writeObject(BT3);

                ObjectOutputStream out2 = new ObjectOutputStream(socket2.getOutputStream());
                out2.writeObject(A2);
                out2.writeObject(BT1);
                out2.writeObject(BT2);
                out2.writeObject(BT3);

                ObjectOutputStream out3 = new ObjectOutputStream(socket3.getOutputStream());
                out3.writeObject(A3);
                out3.writeObject(BT1);
                out3.writeObject(BT2);
                out3.writeObject(BT3);

                // Recibiendo matriz C1, C2, C3 del nodo 1
                ObjectInputStream ois1 = new ObjectInputStream(socket1.getInputStream());
                double[][] C1 = (double[][]) ois1.readObject();
                double[][] C2 = (double[][]) ois1.readObject();
                double[][] C3 = (double[][]) ois1.readObject();

                // Recibiendo matriz C4, C5, C6 del nodo 2
                ObjectInputStream ois2 = new ObjectInputStream(socket2.getInputStream());
                double[][] C4 = (double[][]) ois2.readObject();
                double[][] C5 = (double[][]) ois2.readObject();
                double[][] C6 = (double[][]) ois2.readObject();

                // Recibiendo matriz C7, C8, C9 del nodo 3
                ObjectInputStream ois3 = new ObjectInputStream(socket3.getInputStream());
                double[][] C7 = (double[][]) ois3.readObject();
                double[][] C8 = (double[][]) ois3.readObject();
                double[][] C9 = (double[][]) ois3.readObject();

                // Combinando las matrices C para formar la matriz completa
                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    System.arraycopy(C1[i], 0, C[i], 0, MATRIX_SIZE / 3);
                    System.arraycopy(C2[i], 0, C[i], MATRIX_SIZE / 3, MATRIX_SIZE / 3);
                    System.arraycopy(C3[i], 0, C[i], 2 * MATRIX_SIZE / 3, MATRIX_SIZE / 3);
                }
                for (int i = MATRIX_SIZE / 3; i < 2 * MATRIX_SIZE / 3; i++) {
                    System.arraycopy(C4[i - MATRIX_SIZE / 3], 0, C[i], 0, MATRIX_SIZE / 3);
                    System.arraycopy(C5[i - MATRIX_SIZE / 3], 0, C[i], MATRIX_SIZE / 3, MATRIX_SIZE / 3);
                    System.arraycopy(C6[i - MATRIX_SIZE / 3], 0, C[i], 2 * MATRIX_SIZE / 3, MATRIX_SIZE / 3);
                }
                for (int i = 2 * MATRIX_SIZE / 3; i < MATRIX_SIZE; i++) {
                    System.arraycopy(C7[i - 2 * MATRIX_SIZE / 3], 0, C[i], 0, MATRIX_SIZE / 3);
                    System.arraycopy(C8[i - 2 * MATRIX_SIZE / 3], 0, C[i], MATRIX_SIZE / 3, MATRIX_SIZE / 3);
                    System.arraycopy(C9[i - 2 * MATRIX_SIZE / 3], 0, C[i], 2 * MATRIX_SIZE / 3, MATRIX_SIZE / 3);
                }

                // Calcular checksum
                double checksum = 0;
                for (int i = 0; i < MATRIX_SIZE; i++) {
                    for (int j = 0; j < MATRIX_SIZE; j++) {
                        checksum += C[i][j];
                    }
                }

                // Imprimir checksum o matrices A, B y C
                if (MATRIX_SIZE == 12) {
                    System.out.println("Matriz A:");
                    printMatrix(A);
                    System.out.println("Matriz B:");
                    printMatrix(B);
                    System.out.println("Matriz C:");
                    printMatrix(C);
                }

                System.out.println("Checksum: " + checksum);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (nodeNumber == NODE_1) {
            try (ServerSocket serverSocket = new ServerSocket(NODE_1_PORT);
                    Socket socket = serverSocket.accept();
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                
                // Recibir A1, BT1, BT2, BT3
                double[][] A1 = (double[][]) ois.readObject();
                double[][] BT1 = (double[][]) ois.readObject();
                double[][] BT2 = (double[][]) ois.readObject();
                double[][] BT3 = (double[][]) ois.readObject();

                double[][] C1 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
                double[][] C2 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
                double[][] C3 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];

                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                        double sum = 0;
                        for (int k = 0; k < MATRIX_SIZE; k++) {
                            sum += A1[i][k] * BT1[j][k];
                        }
                        C1[i][j] = sum;
                    }
                }

                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                        double sum = 0;
                        for (int k = 0; k < MATRIX_SIZE; k++) {
                            sum += A1[i][k] * BT2[j][k];
                        }
                        C2[i][j] = sum;
                    }
                }

                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                        double sum = 0;
                        for (int k = 0; k < MATRIX_SIZE; k++) {
                            sum += A1[i][k] * BT3[j][k];
                        }
                        C3[i][j] = sum;
                    }
                }
                
                // Enviar C1, C2, C3 al nodo 0
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(C1);
                oos.writeObject(C2);
                oos.writeObject(C3);

            } catch (IOException |

                    ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (nodeNumber == NODE_2) {
            try (
                    ServerSocket serverSocket = new ServerSocket(NODE_2_PORT);
                    Socket socket = serverSocket.accept();
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                
                // Recibir A2, BT1, BT2, BT3  
                double[][] A2 = (double[][]) ois.readObject();
                double[][] BT1 = (double[][]) ois.readObject();
                double[][] BT2 = (double[][]) ois.readObject();
                double[][] BT3 = (double[][]) ois.readObject();

                
                double[][] C4 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
                double[][] C5 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
                double[][] C6 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];

                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                        double sum = 0;
                        for (int k = 0; k < MATRIX_SIZE; k++) {
                            sum += A2[i][k] * BT1[j][k];
                        }
                        C4[i][j] = sum;
                    }
                }

                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                        double sum = 0;
                        for (int k = 0; k < MATRIX_SIZE; k++) {
                            sum += A2[i][k] * BT2[j][k];
                        }
                        C5[i][j] = sum;
                    }
                }

                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                        double sum = 0;
                        for (int k = 0; k < MATRIX_SIZE; k++) {
                            sum += A2[i][k] * BT3[j][k];
                        }
                        C6[i][j] = sum;
                    }
                }

                // Enviar C4, C5, C6 al nodo 0
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(C4);
                oos.writeObject(C5);
                oos.writeObject(C6);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (nodeNumber == NODE_3) {
    
            try (
                    ServerSocket serverSocket = new ServerSocket(NODE_3_PORT);
                    Socket socket = serverSocket.accept();
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                 // Recibir A3, BT1, BT2, BT3
                double[][] A3 = (double[][]) ois.readObject();
                double[][] BT1 = (double[][]) ois.readObject();
                double[][] BT2 = (double[][]) ois.readObject();
                double[][] BT3 = (double[][]) ois.readObject();

                double[][] C7 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
                double[][] C8 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
                double[][] C9 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];

                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                        double sum = 0;
                        for (int k = 0; k < MATRIX_SIZE; k++) {
                            sum += A3[i][k] * BT1[j][k];
                        }
                        C7[i][j] = sum;
                    }
                }

                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                        double sum = 0;
                        for (int k = 0; k < MATRIX_SIZE; k++) {
                            sum += A3[i][k] * BT2[j][k];
                        }
                        C8[i][j] = sum;
                    }
                }

                for (int i = 0; i < MATRIX_SIZE / 3; i++) {
                    for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                        double sum = 0;
                        for (int k = 0; k < MATRIX_SIZE; k++) {
                            sum += A3[i][k] * BT3[j][k];
                        }
                        C9[i][j] = sum;
                    }
                }

                // Enviar C7, C8, C9 al nodo 0
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(C7);
                oos.writeObject(C8);
                oos.writeObject(C9);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
