import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteChat {
    public static void main(String[] args) {
        String nombreUsuario = JOptionPane.showInputDialog("Ingrese su nombre de usuario:");

        try {
            Socket socket = new Socket("localhost", 5000);

            PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
            escritor.println(nombreUsuario);

            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JFrame frame = new JFrame("Chat - " + nombreUsuario);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            JTextArea mensajesChat = new JTextArea();
            mensajesChat.setEditable(false);
            mensajesChat.setRows(8);
            JScrollPane panelMensajes = new JScrollPane(mensajesChat);
            panel.add(panelMensajes, BorderLayout.CENTER);

            JPanel panelDerecha = new JPanel();
            panelDerecha.setLayout(new BoxLayout(panelDerecha, BoxLayout.Y_AXIS));
            panelDerecha.setBorder(new EmptyBorder(20, 10, 30, 10));

            JList<String> listaUsuarios = new JList<>();
            DefaultListModel<String> modelo = new DefaultListModel<>();
            listaUsuarios.setModel(modelo);
            JScrollPane panelListaUsuarios = new JScrollPane(listaUsuarios);
            panelListaUsuarios.setPreferredSize(new Dimension(175, 0));

            panelDerecha.add(new JLabel("Usuarios Conectados:"));
            panelDerecha.add(panelListaUsuarios);

            panel.add(panelDerecha, BorderLayout.EAST);

            JPanel panelInferior = new JPanel();
            panelInferior.setBorder(new EmptyBorder(10, 0, 0, 0));

            JTextField textFieldEntrada = new JTextField();
            textFieldEntrada.setColumns(30);

            JButton buttonEnviar = new JButton("Enviar");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
