import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteChat {
    public static void main(String[] args) {
        String nombreUsuario = obtenerNombreUsuario();

        try {
            Socket socket = new Socket("localhost", 5000);

            PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
            escritor.println(nombreUsuario);

            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true){
                String enUso = lector.readLine();
                if (enUso.equals("si")) {
                    JOptionPane.showMessageDialog(null, "Ese nombre de usuario ya está en uso.", "Error", JOptionPane.ERROR_MESSAGE);
                    nombreUsuario = obtenerNombreUsuario();
                    escritor.println(nombreUsuario);
                }else{
                    break;
                }
            }

            JFrame frame = new JFrame("Chat - " + nombreUsuario);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            JTextArea mensajesChat = new JTextArea();
            mensajesChat.setEditable(false);
            mensajesChat.setRows(8);
            JScrollPane panelMensajes = new JScrollPane(mensajesChat);
            panel.add(panelMensajes, BorderLayout.CENTER);
            panel.setBorder(new EmptyBorder(20, 10, 10, 0));

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
            panelInferior.setBorder(new EmptyBorder(5, 0, 5, 0));

            JTextField textFieldEntrada = new JTextField();
            textFieldEntrada.setColumns(30);

            JButton buttonEnviar = new JButton("Enviar");

            buttonEnviar.addActionListener(e -> enviarMensaje(textFieldEntrada, escritor));
            textFieldEntrada.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        enviarMensaje(textFieldEntrada, escritor);
                    }
                }
            });


            panelInferior.add(textFieldEntrada); // Agregar el JTextField al panel inferior
            panelInferior.add(buttonEnviar);

            panel.add(panelInferior, BorderLayout.SOUTH); // Alinear el panel inferior en la parte inferior del BorderLayout

            frame.getContentPane().add(panel);

            frame.setSize(700, 500); // Ajustar tamaño del JFrame
            frame.setVisible(true);

            while (true) {
                String mensajeRecibido = lector.readLine();

                if (mensajeRecibido.startsWith("[MENSAJE]: ")) {
                    String mensajeAnterior = mensajeRecibido.substring(11);
                    SwingUtilities.invokeLater(() -> {
                        mensajesChat.append(mensajeAnterior + "\n");
                    });
                }else if (mensajeRecibido.startsWith("[Usuarios]: ")) {
                    String[] usuariosConectados = mensajeRecibido.substring(11).split(", ");
                    SwingUtilities.invokeLater(() -> {
                        modelo.clear();
                        for (String usuario : usuariosConectados) {
                            modelo.addElement(usuario);
                        }
                    });
                } else {
                    mensajesChat.append(mensajeRecibido + "\n");
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String obtenerNombreUsuario() {
        while (true) {
            String nombreUsuario = JOptionPane.showInputDialog("Ingrese su nombre de usuario:");

            if (nombreUsuario == null) {
                System.exit(0); // Si se cierra el cuadro de diálogo, salimos del programa
            }

            if (!nombreUsuario.trim().isEmpty()) {
                return nombreUsuario;
            } else {
                JOptionPane.showMessageDialog(null, "El nombre de usuario no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void enviarMensaje(JTextField textFieldEntrada, PrintWriter escritor) {
        String mensaje = textFieldEntrada.getText();
        escritor.println(mensaje);
        textFieldEntrada.setText("");
    }
}

