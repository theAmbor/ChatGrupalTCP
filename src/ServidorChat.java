import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class ServidorChat {
    private static final int PUERTO = 5000;
    private static ArrayList<PrintWriter> flujosSalida = new ArrayList<>();
    private static ArrayList<String> usuariosConectados = new ArrayList<>();
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor de Chat iniciado en el puerto " + PUERTO);

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
