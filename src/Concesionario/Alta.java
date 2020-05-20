package Concesionario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class Alta extends JFrame{
    static JFrame ventana;
    static Connection con;

    private JButton aceptarButton;
    private JButton salirButton;
    private JPanel panelAlta;
    private JTextField matricula;
    private JTextField marca;
    private JTextField modelo;
    private JTextField precio;
    private JTextField extras;
    private JLabel JLMatri;
    private JLabel JLMarca;
    private JLabel JLModelo;
    private JLabel JLExtras;
    private JLabel JLPrecio;
    private JLabel title;

    String matriculaE;
    String marcaE;
    String modeloE;
    float precioE;
    String extrasE;

    public Alta() {



        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ventana.setVisible(false);

            }
        });

        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    System.out.println("Hola");
                    matriculaE = matricula.getText();
                    marcaE = marca.getText();
                    modeloE = modelo.getText();
                    precioE = Float.parseFloat(precio.getText());
                    extrasE = extras.getText();

                    System.out.println("Antes de validar entrada");
                    boolean entradaCorrecta = validarEntrada();
                    System.out.println(" ES LA ENTRADA CORRECTA: "+entradaCorrecta);
                    if (entradaCorrecta){
                        abrirConnexion();
                        altaCoche(matriculaE, marcaE, modeloE, precioE, extrasE);
                    }


                } catch (NumberFormatException ex) {
                    return;
                } catch (ExceptionDatos ex) {
                    ex.printStackTrace();
                }


            }
        });
    }

    public static void main(String[] args) {
        iniciarVentana();
    }

    static void iniciarVentana() {
        ventana = new JFrame("Alta de un coche");

        ventana.setContentPane(new Alta().panelAlta);
        ventana.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
        ventana.setResizable(false); // Para no poder cambiar el tamaño de la ventana.

    }

    boolean validarEntrada() throws ExceptionDatos {
        boolean salida;

        if (matriculaE == "" // Si los datos de entrada están vacios mostramos la excepción
                && marcaE == ""
                && modeloE == ""
                && ((precioE == 0) || (precioE < 0))
                && extrasE != "") {
            salida = false;
            throw new ExceptionDatos("FALTAN DATOS POR ESCRIBIR");
        } else { // Todo este apartado es frente a las posiciones de la entrada.
            if (matriculaE.length() != 7){
                throw new ExceptionDatos("La matricula ocupa 7 posiciones");
            } else if(marcaE.length()>20){
                throw new ExceptionDatos("La marca ocupa menos 21 de posiciones");
            } else if(modeloE.length()>30){
                throw new ExceptionDatos("El modelo ocupa menos 31 de posiciones");
            } else if(precio.getText().length() > 10){
                throw new ExceptionDatos("El precio ocupa 8 enteros y 2 decimales");
            } else if (extrasE.length()>100){
                throw new ExceptionDatos("Los extras ocupan 100 posiciones");
            } else {
                salida = true;
            }
        }

        return salida;
    }

    static void abrirConnexion(){ // Abriendo la BBDD

        try {

            String url = "jdbc:mysql://localhost:3306/concesionario?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDateTimeCode=false&serverTimeZone=UTC";
            String user = "root";
            String pass = "";

            con = DriverManager.getConnection(url,user,pass);

            if(con!=null){

                System.out.println("Entrando correctamente!");
                con.setAutoCommit(false); // Quitando el autocommit

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    static void altaCoche(String matricula, String marca, String modelo, float precio, String extras) { // INSERT

        final String SQL_INSERT = "INSERT INTO COCHES (MATRICULA, MARCA, MODELO, PRECIO, EXTRAS) VALUES (?,?,?,?,?)"; // Creación de un String para poder hacer el INSERT.

        try {
            PreparedStatement insert_dinamico = con.prepareStatement(SQL_INSERT); // PARA HACER UN INSERT DINAMICO.

            // RELLENAMOS EL STRING DINAMICO
            insert_dinamico.setString(1, matricula);
            insert_dinamico.setString(2, marca);
            insert_dinamico.setString(3, modelo);
            insert_dinamico.setFloat(4, precio);
            insert_dinamico.setString(5, extras);



            int fila = insert_dinamico.executeUpdate();
            System.out.println(fila + " filas insertadas correctamente");
            // Una vez registrado el coche salimos del menú de alta.
        } catch (SQLIntegrityConstraintViolationException ex){
            JOptionPane.showMessageDialog(null,"¡Registro duplicado!");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true); // Activamos otra vez el commit una vez habiendo comprado que funciona correctamente nuestro insert
                con.close(); // Y cerramos nuestra conexion
                JOptionPane.showMessageDialog(null,"¡Coche registrado correctamente!");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public class ExceptionDatos extends Exception {
        public ExceptionDatos(String msg) {
            super(msg);
            JOptionPane.showMessageDialog(null, msg);
        }
    }
}
