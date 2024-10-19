import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ParqueaderoApp extends JFrame {
    private List<Vehiculo> vehiculos;
    private Stack<Vehiculo> dosRuedas; 
    private Stack<Vehiculo> cuatroRuedas; 
    private DefaultTableModel modeloTabla;
    private JTable tablaVehiculos;

    public ParqueaderoApp() {
        vehiculos = new LinkedList<>();
        dosRuedas = new Stack<>();
        cuatroRuedas = new Stack<>();

    
        setTitle("Sistema de Gestión de Parqueadero");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        modeloTabla = new DefaultTableModel(new Object[]{"Número", "Placa", "Tipo", "Hora Ingreso", "Valor a Pagar"}, 0);
        tablaVehiculos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaVehiculos);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 6));
        panelBotones.add(new JButton(new IngresoVehiculoAction()));
        panelBotones.add(new JButton(new VisualizarVehiculosAction()));
        panelBotones.add(new JButton(new VisualizarDosRuedasAction()));
        panelBotones.add(new JButton(new VisualizarCuatroRuedasAction()));
        panelBotones.add(new JButton(new CantidadYValorAction()));
        panelBotones.add(new JButton(new EliminarVehiculoAction()));

        panel.add(panelBotones, BorderLayout.SOUTH);
        add(panel);

        setVisible(true); 
    }

    private class Vehiculo {
        private static int contador = 0; 
        private int numero;
        private String placa;
        private String tipo;
        private String horaIngreso;
        private long tiempoIngreso; 

        public Vehiculo(String placa, String tipo, String horaIngreso) {
            this.numero = ++contador;
            this.placa = placa;
            this.tipo = tipo;
            this.horaIngreso = horaIngreso;
            this.tiempoIngreso = calcularTiempoEnMinutos(); 
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
            
            return 30;
        }
    }

    private class IngresoVehiculoAction extends AbstractAction {
        public IngresoVehiculoAction() {
            super("Ingreso Vehículo");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String placa = JOptionPane.showInputDialog("Ingrese la placa del vehículo:");
            String tipo = JOptionPane.showInputDialog("Ingrese el tipo de vehículo (Bicicleta, Ciclomotor, Motocicleta, Carro):");
            String horaIngreso = JOptionPane.showInputDialog("Ingrese la hora de ingreso (HH:mm):");

            Vehiculo vehiculo = new Vehiculo(placa, tipo, horaIngreso);
            vehiculos.add(vehiculo);

            if (tipo.equalsIgnoreCase("Bicicleta") || tipo.equalsIgnoreCase("Ciclomotor")) {
                dosRuedas.push(vehiculo); 
            } else if (tipo.equalsIgnoreCase("Motocicleta") || tipo.equalsIgnoreCase("Carro")) {
                cuatroRuedas.push(vehiculo);
            }

            actualizarTabla();
        }
    }

    private class VisualizarVehiculosAction extends AbstractAction {
        public VisualizarVehiculosAction() {
            super("Visualizar Vehículos");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            actualizarTabla();
        }
    }

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
            JOptionPane.showMessageDialog(null, "Cantidad de vehículos en parqueadero: " + vehiculos.size() + "\nValor total a pagar: " + totalValor + " COP", "Cantidad y Valor Total", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class EliminarVehiculoAction extends AbstractAction {
        public EliminarVehiculoAction() {
            super("Eliminar Vehículo");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String placa = JOptionPane.showInputDialog("Ingrese la placa del vehículo a eliminar:");
            vehiculos.removeIf(v -> v.getPlaca().equalsIgnoreCase(placa));
            actualizarTabla();
            JOptionPane.showMessageDialog(null, "Vehículo eliminado si existía.", "Eliminar Vehículo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Vehiculo v : vehiculos) {
            modeloTabla.addRow(new Object[]{
                    v.getNumero(),
                    v.getPlaca(),
                    v.getTipo(),
                    v.getHoraIngreso(),
                    v.calcularValorAPagar()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParqueaderoApp::new);
    }
}
