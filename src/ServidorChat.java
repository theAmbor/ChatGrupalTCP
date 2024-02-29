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

                Thread t = new Thread(new ManejadorCliente(clienteSocket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ManejadorCliente implements Runnable {
        private Socket clienteSocket;
        private BufferedReader lector;
        private PrintWriter escritor;
        private String nombreUsuario;

        public ManejadorCliente(Socket socket) {
            try {
                clienteSocket = socket;
                lector = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
                escritor = new PrintWriter(clienteSocket.getOutputStream(), true);

                nombreUsuario = lector.readLine();
                usuariosConectados.add(nombreUsuario);
                enviarUsuariosConectados();

                flujosSalida.add(escritor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String mensaje;
            try {
                while ((mensaje = lector.readLine()) != null) {
                    enviarMensajeATodos(nombreUsuario + ": " + mensaje);
                    enviarUsuariosConectados();
                }
            } catch (IOException e) {
                System.out.println(nombreUsuario + " se ha desconectado");
                usuariosConectados.remove(nombreUsuario);
                flujosSalida.remove(escritor);
                enviarUsuariosConectados();
            }
        }

        private void enviarMensajeATodos(String mensaje) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String mensajeFormateado = "[" + sdf.format(new Date()) + "] " + mensaje;

            for (PrintWriter writer : flujosSalida) {
                writer.println(mensajeFormateado);
            }
        }

        private static void enviarUsuariosConectados() {
            for (PrintWriter writer : flujosSalida) {
                writer.println("[Usuarios]: " + String.join(", ", usuariosConectados));
            }
        }
    }
}

