package Concesionario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class App extends JFrame {

    Connection con; // Conexión a BBDD

    private JPanel panelMain;
    private JButton botonSalir;
    private JButton buttonAlta;
    private JButton buttonConsulta;
    private JButton buttonModificacion;
    private JButton buttonBaja;
    private JTable tabla;
    private JScrollPane jps;
    private JButton actualizarTablaButton;
    private JTextField JTextMatricula;
    private JLabel JLMatricula;
    private JLabel JLMarca;
    private JTextField JTMarca;
    private JTextField JTModelo;
    private JLabel JLModelo;
    private JLabel JLExtras;
    private JTextField JTExtras;
    private JTextField JTPrecio;
    private JLabel JLPrecio;
    private JLabel Concesionario;

    public App() {

        iniciarVentana();
        rellenarJTable();

        botonSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "¡Hasta luego!");
                System.exit(0);
            }
        });

        buttonAlta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Alta altaCoche = new Alta();
                altaCoche.iniciarVentana();

            }
        });

        buttonConsulta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // DATOS DE ENTRADA.
                String matriculaE;
                String marcaE;
                String modeloE;
                float precioE;
                String extrasE;

                if (JTextMatricula.getText().length() > 7) {
                    JOptionPane.showMessageDialog(null, "La matricula ocupa 7 posiciones");
                } else if (JTMarca.getText().length() > 20) {
                    JOptionPane.showMessageDialog(null, "La matricula ocupa 7 posiciones");
                } else if (JTModelo.getText().length() > 30) {
                    JOptionPane.showMessageDialog(null, "El modelo ocupa menos 31 de posiciones");
                } else if (JTExtras.getText().length() > 100) {
                    JOptionPane.showMessageDialog(null, "Los extra no ocupan más de 100 posiciones");
                } else if (JTPrecio.getText().length() > 10) {
                    JOptionPane.showMessageDialog(null, "El precio ocupa 8 enteros y 2 decimales");
                } else {

                    matriculaE = JTextMatricula.getText();
                    marcaE = JTMarca.getText();
                    modeloE = JTModelo.getText();
                    extrasE = JTExtras.getText();
                    if (JTPrecio.getText().equals("")) {
                        precioE = 0;
                    } else {
                        precioE = Float.parseFloat(JTPrecio.getText());
                    }

                    ArrayList<Coche> listaDevuelta;

                    abrirConexion();
                    listaDevuelta = consultaCoches(matriculaE, marcaE, modeloE, precioE, extrasE);

                    construirTabla(listaDevuelta);
                    System.out.println("\n\tEmpiezo a crear la tabla ");
                    System.out.println(" -- LISTA DEVUELTA -- " + listaDevuelta.size());
                }
            }
        });

        buttonBaja.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int fila_seleccionada = tabla.getSelectedRow();

                System.out.println(tabla.getValueAt(fila_seleccionada, 0).toString());
                System.out.println(tabla.getValueAt(fila_seleccionada, 1).toString());
                System.out.println(tabla.getValueAt(fila_seleccionada, 2).toString());
                System.out.println(tabla.getValueAt(fila_seleccionada, 3).toString());
                System.out.println(tabla.getValueAt(fila_seleccionada, 4).toString());

                String matricula = tabla.getValueAt(fila_seleccionada, 0).toString();

                int resp = JOptionPane.showConfirmDialog(null, "¿Está seguro de dar de baja este registro?");

                if (resp == 0) { // Si desea borrar el registro procedemos a borramos.

                    abrirConexion();
                    eliminarCoche(matricula);

                }
            }
        });

        buttonModificacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int fila_seleccionada = tabla.getSelectedRow();

                System.out.println(tabla.getValueAt(fila_seleccionada, 0).toString());
                System.out.println(tabla.getValueAt(fila_seleccionada, 1).toString());
                System.out.println(tabla.getValueAt(fila_seleccionada, 2).toString());
                System.out.println(tabla.getValueAt(fila_seleccionada, 3).toString());
                System.out.println(tabla.getValueAt(fila_seleccionada, 4).toString());

                String matricula = tabla.getValueAt(fila_seleccionada, 0).toString();
                String marca = tabla.getValueAt(fila_seleccionada, 1).toString();
                String modelo = tabla.getValueAt(fila_seleccionada, 2).toString();
                String precio = tabla.getValueAt(fila_seleccionada, 3).toString();
                String extras = tabla.getValueAt(fila_seleccionada, 4).toString();

                int resp = JOptionPane.showConfirmDialog(null, "¿Está seguro de modificar este registro?");

                if (resp == 0) { // Si desea borrar el registro procedemos a borramos.

                    abrirConexion();
                    float prec = Float.parseFloat(precio);

                    modificarCoche(matricula, marca, modelo, extras, precio);

                }
            }
        });

        actualizarTablaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rellenarJTable();
            }
        });
    }

    private void iniciarVentana() {
        JFrame ventana = new JFrame("CONCESIONARIO");

        ventana.setContentPane(panelMain);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
        ventana.setResizable(true);

        ventana.setTitle("Concesionario");
    }

    /**
     * LOS SIGUIENTES TRES MÉTODOS RELLENAN NUESTRO COMPONENTE PRINCIPAL DE LA APLICACIÓN, EL JTABLE.
     */
    private void rellenarJTable() {
        abrirConexion();

        ArrayList<Coche> listaDevuelta = consultaCoches("", "", "", 0, ""); // Arraylist que me llenará la tabla.

        construirTabla(listaDevuelta);
    }

    public void construirTabla(ArrayList<Coche> listaCoches) {

        final String[] encabezadoTabla = { // Primer paso. creo un array con el la primera fila de la tabla..
                "Matricula", "Marca", "Modelo", "Precio", "Extras"
        };

        String informacion[][] = obtenerMatriz(listaCoches); // Segundo paso. obtengo la información de la bbdd, que previamente transforme en un arraylist y la meto en una matriz.

        tabla = new JTable(informacion, encabezadoTabla); // Tercer paso. creo el JTable, con la información del encabezado y la información de dentro

        jps.setViewportView(tabla);

    }

    private String[][] obtenerMatriz(ArrayList<Coche> listaCoches) {

        String matrizInfo[][] = new String[listaCoches.size()][5]; // Las filas equivaldrán al número de registros encontrados y las columnas son las 5 de la tabla.

        for (int i = 0; i < matrizInfo.length; i++) { // Recorro el array...

            matrizInfo[i][0] = listaCoches.get(i).getMatricula();
            matrizInfo[i][1] = listaCoches.get(i).getMarca();
            matrizInfo[i][2] = listaCoches.get(i).getModelo();
            matrizInfo[i][3] = listaCoches.get(i).getPrecio() + "";
            matrizInfo[i][4] = listaCoches.get(i).getExtras();

        }

        return matrizInfo;
    }

    /**
     * BBDD -- OPCIONES DE BASES DE DATOS
     */
    private void abrirConexion() { // Abriendo la BBDD

        try {

            String url = "jdbc:mysql://localhost:3306/concesionario?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDateTimeCode=false&serverTimeZone=UTC";
            String user = "root";
            String pass = "";

            con = DriverManager.getConnection(url, user, pass);

            if (con != null) {

                System.out.println("Entrando correctamente!");
                con.setAutoCommit(false); // Quitando el autocommit

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void eliminarCoche(String matricula) { /** PARRAFO PARA BORRAR UN COCHE DE LA BBDD
     */
        int contador = 0;

        try {

            String queryDelete = "DELETE FROM COCHES WHERE MATRICULA = ?"; // CREO LA SENTENCIA DEL DELETE
            PreparedStatement preparedStmt = con.prepareStatement(queryDelete);

            preparedStmt.setString(1, matricula);

            contador = preparedStmt.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (contador == 0) {
                    JOptionPane.showMessageDialog(null, "NO HAY REGISTROS CON LA MATRICULA INDICADA");
                } else {
                    JOptionPane.showMessageDialog(null, "Registro borrado correctamente!");
                }

                con.setAutoCommit(true); // ACTIVAMOS EL AUTOCOMMIT DE NUEVO
                con.close(); // Cierrro la conexion

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    private ArrayList<Coche> consultaCoches(String matricula, String marca, String modelo, float precio, String extras) {

        /** EN ESTE PARRAFO LO QUE HARÉ SERÁ UNA CONSULTA DINÁMICA, DEPENDIENDO DE LA ENTRADA, LA SELECT SE HARA DE UNA FORMA
         * U OTRA y devolveré un arraylist.
         * */
        ArrayList<Coche> listaCoches = new ArrayList<Coche>();

        try {

            String SQL_SELECT = "SELECT * FROM COCHES";
            final String where = " WHERE ";
            final String and = " AND ";
            boolean primero = true;
            boolean matri = false, mar = false, mod = false, pre = false, ext = false; // BOOLEANS UTILIZADOS PARA EL PREPARE STATEMENT

            if (!matricula.equals("")) {
                if (primero) {
                    SQL_SELECT += where;
                    primero = false;
                }
                SQL_SELECT += " MATRICULA LIKE ? ";
                matri = true;
            }

            if (!marca.equals("")) {
                if (primero) {
                    SQL_SELECT += where;
                    primero = false;
                } else {
                    SQL_SELECT += and;
                }
                SQL_SELECT += "MARCA LIKE ?";
                mar = true;
            }

            if (!modelo.equals("")) {
                if (primero) {
                    SQL_SELECT += where;
                    primero = false;
                } else {
                    SQL_SELECT += and;
                }
                SQL_SELECT += " MODELO LIKE ?";
                mod = true;
            }

            if (precio != 0) {
                if (primero) {
                    SQL_SELECT += where;
                    primero = false;
                } else {
                    SQL_SELECT += and;
                }

                SQL_SELECT += " PRECIO LIKE ?";
                pre = true;
            }

            if (!extras.equals("")) {
                if (primero) {
                    SQL_SELECT += where;
                    primero = false;
                } else {
                    SQL_SELECT += and;
                }

                SQL_SELECT += " EXTRAS LIKE ?";
                ext = true;
            }
            SQL_SELECT += " ORDER BY MATRICULA";

            PreparedStatement select_dinamico = con.prepareStatement(SQL_SELECT); // PARA HACER UN INSERT DINAMICO.

            int nPosicionDinamica = 1; // Esta posicion es la que utilizaré para

            if (matri) {
                select_dinamico.setString(nPosicionDinamica, "%" + matricula + "%");
                nPosicionDinamica++;
            }

            if (mar) {
                select_dinamico.setString(nPosicionDinamica, "%" + marca + "%");
                nPosicionDinamica++;
            }

            if (mod) {
                select_dinamico.setString(nPosicionDinamica, "%" + modelo + "%");
                nPosicionDinamica++;
            }

            if (pre) {
                String aux = "%" + (int) precio + "%";
                select_dinamico.setString(nPosicionDinamica, aux);
                nPosicionDinamica++;
            }

            if (ext) {
                select_dinamico.setString(nPosicionDinamica, "%" + extras + "%");
                nPosicionDinamica++;
            }


            System.out.println(select_dinamico); // CONSULTA A UTILIZAR.


            ResultSet registro = select_dinamico.executeQuery();

            int contadorRegistros = 0;


            while (registro.next()) { // LEO TODOS LOS REGISTROS QUE HAYAN SALIDO DE LA QUERY.

                // Voy añadiendo registros...


                // CREO UN NUEVO OBJETO COCHE CADA VEZ QUE COJA UN REGISTRO.
                Coche coche = new Coche();
                coche.setMatricula(registro.getString("MATRICULA"));
                coche.setMarca(registro.getString("MARCA"));
                coche.setModelo(registro.getString("MODELO"));
                coche.setExtras(registro.getString("EXTRAS"));

                coche.setPrecio(registro.getFloat("PRECIO"));

                listaCoches.add(coche);

                String matriQuery = registro.getString("MATRICULA");
                String marcaQuery = registro.getString("MARCA");
                String modelQuery = registro.getString("MODELO");
                Float preciQuery = registro.getFloat("PRECIO");
                String extraQuery = registro.getString("EXTRAS");


                contadorRegistros++;

                System.out.println("\n**************************************");
                System.out.println("Registro " + contadorRegistros);
                System.out.println("Matricula " + matriQuery);
                System.out.println("Marca " + marcaQuery);
                System.out.println("Modelo " + modelQuery);
                System.out.println("Precio " + preciQuery);
                System.out.println("Extras " + extraQuery);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                con.close(); // Y por último cerramos la BBDD
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return listaCoches;
    }

    private void modificarCoche(String matricula, String marca, String modelo, String extras, String prec) {

        /** Este método servirá para actualizar en la BBDD un registro
         * */


        int contadorRegistros = 0;
        float precio = Float.parseFloat(prec);

        try {

            String SQL_UPDATE = "UPDATE COCHES";
            final String where = " WHERE ";
            final String coma = " , ";
            boolean primero = true;
            boolean mar = false, mod = false, pre = false, ext = false; // BOOLEANS UTILIZADOS PARA EL PREPARE STATEMENT


            if (!marca.equals("")) {
                if (primero) {
                    SQL_UPDATE += " SET ";
                    primero = false;
                } else {
                    SQL_UPDATE += coma;
                }
                SQL_UPDATE += "MARCA = ?";
                mar = true;
            }

            if (!modelo.equals("")) {
                if (primero) {
                    SQL_UPDATE += where;
                    primero = false;
                } else {
                    SQL_UPDATE += coma;
                }
                SQL_UPDATE += " MODELO = ?";
                mod = true;
            }

            if (precio != 0) {
                if (primero) {
                    SQL_UPDATE += where;
                    primero = false;
                } else {
                    SQL_UPDATE += coma;
                }

                SQL_UPDATE += " PRECIO = ?";
                pre = true;
            }

            if (!extras.equals("")) {
                if (primero) {
                    SQL_UPDATE += where;
                    primero = false;
                } else {
                    SQL_UPDATE += coma;
                }

                SQL_UPDATE += " EXTRAS = ?";
                ext = true;
            }

            SQL_UPDATE += " WHERE MATRICULA = ?";

            PreparedStatement update_dinamico = con.prepareStatement(SQL_UPDATE); // PARA HACER UN INSERT DINAMICO.

            int nPosicionDinamica = 1; // Esta posicion es la que utilizaré para


            if (mar) {
                update_dinamico.setString(nPosicionDinamica, marca);
                nPosicionDinamica++;
            }

            if (mod) {
                update_dinamico.setString(nPosicionDinamica, modelo);
                nPosicionDinamica++;
            }

            if (pre) {
                update_dinamico.setFloat(nPosicionDinamica, precio);
                nPosicionDinamica++;
            }

            if (ext) {
                update_dinamico.setString(nPosicionDinamica, extras);
                nPosicionDinamica++;
            }

            update_dinamico.setString(nPosicionDinamica, matricula); // POR ÚLTIMO PONGO LA MATRICULA EN EL WHERE DEL UPDATE.

            System.out.println(update_dinamico); // UPDATE A UTILIZAR A UTILIZAR.


            contadorRegistros = update_dinamico.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {

                if (contadorRegistros > 0) {
                    JOptionPane.showMessageDialog(null, "EL COCHE CON LA MATRICULA '" + matricula + "' HA SIDO ACTUALIZADO CORRECTAMENTE");
                } else {
                    JOptionPane.showMessageDialog(null, "NO SE PUEDE MODIFICAR LA MATRÍCULA");
                }
                con.setAutoCommit(true);
                con.close(); // Y por último cerramos la BBDD
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }


    public static void main(String[] args) {

        App concesionario = new App();

    }

}
