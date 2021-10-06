/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

/**
 *
 * @author dani
 */
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Conexion {

    public String driver, url, ip, bd, usr, pass;
    public Connection conexion;
    public Statement St;
    public ResultSet reg;
    public boolean conexiondb = false;

    public Conexion(String ip, String bd, String usr, String pass) {

        driver = "com.mysql.jdbc.Driver";
        this.bd = bd;
        this.usr = usr;
        this.pass = pass;
        url = new String("jdbc:mysql://" + ip + "/" + bd);
        try {
            Class.forName(driver).newInstance();
            conexion = DriverManager.getConnection(url, usr, pass);
            conexiondb = true;
            //System.out.println("Conexion a Base de Datos " + bd + " Ok");
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, "Error al tratar de abrir la base de Datos " + bd, "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error al tratar de abrir la base de Datos" + bd + " : " + exc);
            conexiondb = false;
        }

    }

    public Connection getConexion() {
        return conexion;
    }

    public Connection CerrarConexion() throws SQLException {
        conexion.close();
        conexion = null;
        return conexion;
    }

    public ResultSet Consulta(String sql) {
        String error = "";
        try {
            St = conexion.createStatement();
            reg = St.executeQuery(sql);
        } catch (Exception ee) {
            error = ee.getMessage();
        }
        return (reg);
    }

    public boolean Guardar(String sql) {
        //String error = "";
        boolean ban = false;
        try {
            St = conexion.createStatement();
            St.executeUpdate(sql);
            ban = true;
            //JOptionPane.showMessageDialog(null, "Los datos se guardaron correctamente");
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ban;
    }
}
