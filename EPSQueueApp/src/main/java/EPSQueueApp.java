import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class EPSQueueApp extends JFrame {
    private Queue<Paciente> colaTurnos;
    private JLabel lblTurnoActual, lblTiempoRestante;
    private JTextArea txtTurnosPendientes;
    private Timer temporizador;
    private int tiempoRestante = 5; // Tiempo en segundos
    private JButton btnExtenderTiempo, btnNuevoTurno;

    public EPSQueueApp() {
        colaTurnos = new LinkedList<>(); // Cola de pacientes

        // Configuramos la ventana
        setTitle("EPS - Asignación de Turnos para Entrega de Medicamentos");
        setSize(500, 400);
        setLayout(new GridLayout(5, 1)); // Layout en forma de rejilla con 5 filas

        // Creamos los componentes de la ventana
        lblTurnoActual = new JLabel("Turno Actual: Ninguno");
        lblTiempoRestante = new JLabel("Tiempo Restante: 0 s");
        txtTurnosPendientes = new JTextArea(5, 20);
        txtTurnosPendientes.setEditable(false);
        txtTurnosPendientes.setBorder(BorderFactory.createTitledBorder("Turnos Pendientes"));

        btnExtenderTiempo = new JButton("Extender Tiempo");
        btnNuevoTurno = new JButton("Nuevo Ingreso");

        // Añadimos los componentes a la ventana
        add(lblTurnoActual);
        add(lblTiempoRestante);
        add(new JScrollPane(txtTurnosPendientes)); // Añadimos el JTextArea con scroll
        add(btnExtenderTiempo);
        add(btnNuevoTurno);

        // Configuramos los botones
        btnNuevoTurno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarFormularioNuevoIngreso();
            }
        });

        btnExtenderTiempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extenderTiempo();
            }
        });

        iniciarTemporizador(); // Iniciamos el temporizador

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configuramos para cerrar la ventana al salir
        setVisible(true); // Hacemos visible la ventana
    }

    // Método para mostrar un formulario cuando se ingresa un nuevo paciente
    private void mostrarFormularioNuevoIngreso() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese Nombre:");
        String edadStr = JOptionPane.showInputDialog(this, "Ingrese Edad:");
        int edad = Integer.parseInt(edadStr);
        String afiliacion = JOptionPane.showInputDialog(this, "Afiliación (POS/PC):");
        int respuestaEmbarazo = JOptionPane.showConfirmDialog(this, "¿Está embarazada?", "Condición Especial", JOptionPane.YES_NO_OPTION);
        int respuestaLimitacion = JOptionPane.showConfirmDialog(this, "¿Tiene limitación motriz?", "Condición Especial", JOptionPane.YES_NO_OPTION);

        boolean esEmbarazada = (respuestaEmbarazo == JOptionPane.YES_OPTION);
        boolean tieneLimitacionMotriz = (respuestaLimitacion == JOptionPane.YES_OPTION);

        // Creamos un nuevo paciente con la información ingresada
        Paciente paciente = new Paciente(nombre, edad, afiliacion, esEmbarazada, tieneLimitacionMotriz);
        colaTurnos.add(paciente); // Añadimos al paciente a la cola
        actualizarTurnosPendientes(); // Actualizamos la etiqueta de turnos pendientes
    }

    // Método que inicia el temporizador que llama a los pacientes cada 5 segundos
    private void iniciarTemporizador() {
        temporizador = new Timer();
        temporizador.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (tiempoRestante > 0) {
                    tiempoRestante--;
                    lblTiempoRestante.setText("Tiempo Restante: " + tiempoRestante + " s");
                } else {
                    if (!colaTurnos.isEmpty()) {
                        llamarSiguienteTurno();
                    } else {
                        lblTurnoActual.setText("Turno Actual: Ninguno");
                    }
                }
            }
        }, 0, 1000); // Intervalo de 1 segundo
    }

    // Método que llama al siguiente paciente
    private void llamarSiguienteTurno() {
        Paciente siguiente = colaTurnos.poll(); // Sacamos al siguiente paciente de la cola
        if (siguiente != null) {
            lblTurnoActual.setText("Turno Actual: " + siguiente.getNombre());
            tiempoRestante = 5; // Reiniciamos el tiempo a 5 segundos
        }
        actualizarTurnosPendientes(); // Actualizamos la lista de turnos pendientes
    }

    // Método para extender el tiempo del paciente actual
    private void extenderTiempo() {
        tiempoRestante += 5; // Añadimos 5 segundos al tiempo restante
        lblTiempoRestante.setText("Tiempo Restante: " + tiempoRestante + " s");
    }

    // Método para actualizar la lista de turnos pendientes
    private void actualizarTurnosPendientes() {
        StringBuilder turnos = new StringBuilder();
        for (Paciente p : colaTurnos) {
            turnos.append(p.getNombre()).append("\n");
        }
        txtTurnosPendientes.setText(turnos.toString());
    }

    // Clase Paciente
    private static class Paciente {
        private String nombre;
        private int edad;
        private String afiliacion; // POS o PC
        private boolean esEmbarazada;
        private boolean tieneLimitacionMotriz;

        public Paciente(String nombre, int edad, String afiliacion, boolean esEmbarazada, boolean tieneLimitacionMotriz) {
            this.nombre = nombre;
            this.edad = edad;
            this.afiliacion = afiliacion;
            this.esEmbarazada = esEmbarazada;
            this.tieneLimitacionMotriz = tieneLimitacionMotriz;
        }

        public String getNombre() {
            return nombre;
        }

        public boolean esPrioritario() {
            return (edad >= 60 || edad < 12 || esEmbarazada || tieneLimitacionMotriz || afiliacion.equalsIgnoreCase("PC"));
        }
    }

    public static void main(String[] args) {
        new EPSQueueApp();
    }
}
