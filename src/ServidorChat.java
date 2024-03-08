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
            // Se elimina el fichero historial con contenido de la anterior ejecución y se crea uno nuevo y vacío para evitar errores FileNotFound
            File file = new File("historial.txt");
                if (file.exists()) {
                    file.delete();
                    file.createNewFile();
                }

            ServerSocket serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor de Chat iniciado en el puerto " + PUERTO);

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado");

                Thread t = new Thread(new ManejadorCliente(clienteSocket));
                t.start();
            }
        } catch (FileNotFoundException fnf) {
            System.out.println("El fichero historial no existe.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("\n");
        }
    }

    private static class ManejadorCliente implements Runnable {
        private Socket clienteSocket;
        private BufferedReader lector;
        private PrintWriter escritor;
        private String nombreUsuario;

        int enUso = 0;

        private static final String ARCHIVO_MENSAJES = "historial.txt"; // Nombre del archivo donde se va a almacenar el historial de mensajes

        public ManejadorCliente(Socket socket) {
            try {
                clienteSocket = socket;
                lector = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
                escritor = new PrintWriter(clienteSocket.getOutputStream(), true);

                while (true){
                    nombreUsuario = lector.readLine();
                    for(String usuario : usuariosConectados){
                        if (nombreUsuario.equals(usuario)){
                            enUso++;
                        }
                    }
                    if(enUso > 0){
                        escritor.println("si");
                        enUso = 0;
                    }else{
                        escritor.println("no");
                        break;
                    }
                }
                usuariosConectados.add(nombreUsuario);
                enviarUsuariosConectados();
                // Se envia el historial de mensajes al cliente
                enviarMensajesAnteriores(escritor);

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
                    // Se guarda el mensaje en el archivo del historial
                    enviarMensajeATodos(nombreUsuario + ": " + mensaje);
                    guardarMensaje(nombreUsuario + ": " + mensaje);
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

        private void enviarMensajesAnteriores(PrintWriter escritor) {
            try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_MENSAJES))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    escritor.println(linea);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void guardarMensaje(String mensaje) {
            try (FileWriter fw = new FileWriter(ARCHIVO_MENSAJES, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String mensajeFormateado = "[MENSAJE]: " + "[" + sdf.format(new Date()) + "] " + mensaje;
                out.println(mensajeFormateado);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

