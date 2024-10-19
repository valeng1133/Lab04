import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ParqueaderoApp extends JFrame {
    private List<Vehiculo> vehiculos; // Lista para almacenar vehículos
    private Stack<Vehiculo> dosRuedas; // Pila para vehículos de 2 ruedas
    private Stack<Vehiculo> cuatroRuedas; // Pila para vehículos de 4 ruedas
    private DefaultTableModel modeloTabla;
    private JTable tablaVehiculos;

    public ParqueaderoApp() {
        vehiculos = new LinkedList<>();
        dosRuedas = new Stack<>();
        cuatroRuedas = new Stack<>();

        // Configuración de la ventana
        setTitle("Sistema de Gestión de Parqueadero");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Tabla para mostrar vehículos
        modeloTabla = new DefaultTableModel(new Object[]{"Número", "Placa", "Tipo", "Hora Ingreso", "Valor a Pagar"}, 0);
        tablaVehiculos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaVehiculos);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botones de acción
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 6));
        JButton btnIngreso = new JButton("Ingreso Vehículo");
        JButton btnVisualizar = new JButton("Visualizar Vehículos");
        JButton btnVisualizarDosRuedas = new JButton("Visualizar 2 Ruedas");
        JButton btnVisualizarCuatroRuedas = new JButton("Visualizar 4 Ruedas");
        JButton btnCantidadYValor = new JButton("Cantidad y Valor Total");
        JButton btnEliminar = new JButton("Eliminar Vehículo");

        // Agregamos los botones al panel
        panelBotones.add(btnIngreso);
        panelBotones.add(btnVisualizar);
        panelBotones.add(btnVisualizarDosRuedas);
        panelBotones.add(btnVisualizarCuatroRuedas);
        panelBotones.add(btnCantidadYValor);
        panelBotones.add(btnEliminar);

        panel.add(panelBotones, BorderLayout.SOUTH);
        add(panel);

        // Asignar acciones a los botones
        btnIngreso.addActionListener(new IngresoVehiculoAction());
        btnVisualizar.addActionListener(new VisualizarVehiculosAction());
        btnVisualizarDosRuedas.addActionListener(new VisualizarDosRuedasAction());
        btnVisualizarCuatroRuedas.addActionListener(new VisualizarCuatroRuedasAction());
        btnCantidadYValor.addActionListener(new CantidadYValorAction());
        btnEliminar.addActionListener(new EliminarVehiculoAction());

        setVisible(true); // Hacemos visible la ventana
    }

    // Clase interna para vehículos
    private class Vehiculo {
        private static int contador = 0; // Contador para asignar números automáticamente
        private int numero;
        private String placa;
        private String tipo;
        private String horaIngreso;
        private long tiempoIngreso; // Tiempo en minutos

        public Vehiculo(String placa, String tipo, String horaIngreso) {
            this.numero = ++contador;
            this.placa = placa;
            this.tipo = tipo;
            this.horaIngreso = horaIngreso;
            this.tiempoIngreso = calcularTiempoEnMinutos(); // Calculamos el tiempo de ingreso
        }

        public int getNumero() {
            return numero;
        }

        public String getPlaca() {
            return placa;
        }

        public String getTipo() {
            return tipo;
        }

        public String getHoraIngreso() {
            return horaIngreso;
        }

        public long getTiempoIngreso() {
            return tiempoIngreso;
        }

        public long calcularValorAPagar() {
            long valorPorMinuto;
            switch (tipo) {
                case "Bicicleta":
                case "Ciclomotor":
                    valorPorMinuto = 20;
                    break;
                case "Motocicleta":
                    valorPorMinuto = 30;
                    break;
                case "Carro":
                    valorPorMinuto = 60;
                    break;
                default:
                    valorPorMinuto = 0;
                    break;
            }
            return valorPorMinuto * tiempoIngreso;
        }

        private long calcularTiempoEnMinutos() {
            // Aquí puedes implementar un método para calcular el tiempo real desde la hora de ingreso
            // Por simplicidad, lo dejaremos en 30 minutos para este ejemplo
            return 30;
        }
    }

    // Acción para ingresar un vehículo
    private class IngresoVehiculoAction extends AbstractAction {
        public IngresoVehiculoAction() {
            super("Ingreso Vehículo");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String placa = JOptionPane.showInputDialog("Ingrese la placa del vehículo:");
            if (placa == null || placa.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "La placa no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Verificar si la placa ya está registrada
            for (Vehiculo v : vehiculos) {
                if (v.getPlaca().equalsIgnoreCase(placa)) {
                    JOptionPane.showMessageDialog(null, "La placa ya está registrada.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            String tipo = JOptionPane.showInputDialog("Ingrese el tipo de vehículo (Bicicleta, Ciclomotor, Motocicleta, Carro):");
            if (tipo == null || tipo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El tipo no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String horaIngreso = JOptionPane.showInputDialog("Ingrese la hora de ingreso (HH:mm):");
            if (horaIngreso == null || horaIngreso.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "La hora de ingreso no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Vehiculo vehiculo = new Vehiculo(placa, tipo, horaIngreso);
            vehiculos.add(vehiculo);

            if (tipo.equalsIgnoreCase("Bicicleta") || tipo.equalsIgnoreCase("Ciclomotor")) {
                dosRuedas.push(vehiculo); // Agregamos a la pila de 2 ruedas
            } else if (tipo.equalsIgnoreCase("Motocicleta") || tipo.equalsIgnoreCase("Carro")) {
                cuatroRuedas.push(vehiculo); // Agregamos a la pila de 4 ruedas
            }

            actualizarTabla();
        }
    }

    // Acción para visualizar vehículos
    private class VisualizarVehiculosAction extends AbstractAction {
        public VisualizarVehiculosAction() {
            super("Visualizar Vehículos");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            actualizarTabla();
        }
    }

    // Acción para visualizar vehículos de 2 ruedas
    private class VisualizarDosRuedasAction extends AbstractAction {
        public VisualizarDosRuedasAction() {
            super("Visualizar 2 Ruedas");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder sb = new StringBuilder("Vehículos de 2 Ruedas:\n");
            long total = 0;
            for (Vehiculo v : dosRuedas) {
                long valor = v.calcularValorAPagar();
                total += valor;
                sb.append("Placa: ").append(v.getPlaca()).append(", Valor a Pagar: ").append(valor).append(" COP\n");
            }
            sb.append("Total: ").append(total).append(" COP");
            JOptionPane.showMessageDialog(null, sb.toString(), "Vehículos de 2 Ruedas", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Acción para visualizar vehículos de 4 ruedas
    private class VisualizarCuatroRuedasAction extends AbstractAction {
        public VisualizarCuatroRuedasAction() {
            super("Visualizar 4 Ruedas");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder sb = new StringBuilder("Vehículos de 4 Ruedas:\n");
            long total = 0;
            for (Vehiculo v : cuatroRuedas) {
                long valor = v.calcularValorAPagar();
                total += valor;
                sb.append("Placa: ").append(v.getPlaca()).append(", Valor a Pagar: ").append(valor).append(" COP\n");
            }
            sb.append("Total: ").append(total).append(" COP");
            JOptionPane.showMessageDialog(null, sb.toString(), "Vehículos de 4 Ruedas", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Acción para mostrar cantidad y valor total
    private class CantidadYValorAction extends AbstractAction {
        public CantidadYValorAction() {
            super("Cantidad y Valor Total");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            long totalValor = 0;
            for (Vehiculo v : vehiculos) {
                totalValor += v.calcularValorAPagar();
            }
            JOptionPane.showMessageDialog(null, "Cantidad de vehículos en parqueadero: " + vehiculos.size() + "\nValor total a pagar: " + totalValor + " COP", "Resumen", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Acción para eliminar un vehículo
    private class EliminarVehiculoAction extends AbstractAction {
        public EliminarVehiculoAction() {
            super("Eliminar Vehículo");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String placa = JOptionPane.showInputDialog("Ingrese la placa del vehículo a eliminar:");
            if (placa == null || placa.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "La placa no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Vehiculo vehiculoAEliminar = null;
            for (Vehiculo v : vehiculos) {
                if (v.getPlaca().equalsIgnoreCase(placa)) {
                    vehiculoAEliminar = v;
                    break;
                }
            }
            if (vehiculoAEliminar != null) {
                vehiculos.remove(vehiculoAEliminar);
                if (vehiculoAEliminar.getTipo().equalsIgnoreCase("Bicicleta") || vehiculoAEliminar.getTipo().equalsIgnoreCase("Ciclomotor")) {
                    dosRuedas.remove(vehiculoAEliminar); // Eliminar de la pila de 2 ruedas
                } else if (vehiculoAEliminar.getTipo().equalsIgnoreCase("Motocicleta") || vehiculoAEliminar.getTipo().equalsIgnoreCase("Carro")) {
                    cuatroRuedas.remove(vehiculoAEliminar); // Eliminar de la pila de 4 ruedas
                }
                JOptionPane.showMessageDialog(null, "Vehículo eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(null, "Vehículo no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actualizarTabla() {
        // Limpiar la tabla antes de actualizar
        modeloTabla.setRowCount(0);
        for (Vehiculo v : vehiculos) {
            modeloTabla.addRow(new Object[]{v.getNumero(), v.getPlaca(), v.getTipo(), v.getHoraIngreso(), v.calcularValorAPagar()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ParqueaderoApp());
    }
}
