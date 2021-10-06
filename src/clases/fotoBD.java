/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

/**
 *
 * @author dani
 */
import PaqFormulario.AcesoUsuario;
import com.mysql.jdbc.PacketTooBigException;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JOptionPane;

/**
 * @web http://jc-mouse.blogspot.com
 * @author mouse
 */
public class fotoBD {

    private Conexion con;
    private Image data;
    AcesoUsuario ax;

    public fotoBD(String ip, String bd, String usr, String pass) {
        ax = new AcesoUsuario();
        con = new Conexion(ip, bd, usr, pass);
    }

    public void guardarfoto(String codFoto, String codPersona) {
        {
            FileInputStream fis = null;
            try {
                File file = new File("src\\paqImagenes\\foto.JPG");
                fis = new FileInputStream(file);
                PreparedStatement pstm = con.getConexion().prepareStatement("INSERT INTO"
                        + " foto(codigo, alu_cod,foto) " + " VALUES(?,?,?)");
                pstm.setString(1, codFoto);
                pstm.setString(2, codPersona);
                pstm.setBinaryStream(3, fis, (long) file.length());
                pstm.execute();
                pstm.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(fotoBD.class.getName()).log(Level.SEVERE, null, ex);
            }catch(PacketTooBigException pac){
              JOptionPane.showMessageDialog(null, "La Imagen selccionada sobrepasa el limite de MB");   
            }
            catch (SQLException e) {
                Logger.getLogger(fotoBD.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(fotoBD.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void actualizafoto(String codAlumno) {
        {
            FileInputStream fis = null;
            try {
                File file = new File("src\\paqImagenes\\foto.JPG");
                fis = new FileInputStream(file);
                FileInputStream is = new FileInputStream("src\\paqImagenes\\foto.JPG");
                PreparedStatement pstm = con.getConexion().prepareStatement("UPDATE"
                        + " foto SET foto=? WHERE alu_cod=?");
                
                pstm.setBlob(1,is);
                pstm.setString(2, codAlumno);
                pstm.execute();
                pstm.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(fotoBD.class.getName()).log(Level.SEVERE, null, ex);
            } catch(PacketTooBigException pac){
              JOptionPane.showMessageDialog(null, "La Imagen selccionada sobrepasa el limite de MB");
            } catch (SQLException e) {
                Logger.getLogger(fotoBD.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(fotoBD.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
//metodo  que dado un parametro "id" realiza una consulta y devuelve como resultado
// una imagen
    public Image getfoto(String per_cod) {
        try {
            PreparedStatement pstm = con.getConexion().prepareStatement("SELECT "
                    + " foto "
                    + " FROM foto "
                    + " where alu_cod = ? ");
            pstm.setString(1, per_cod);
            ResultSet res = pstm.executeQuery();
            int i = 0;
            while (res.next()) {
                //se lee la cadena de bytes de la base de datos
                byte[] b = res.getBytes("foto");
                // esta cadena de bytes sera convertida en una imagen
                data = ConvertirImagen(b);
                i++;
            }
            res.close();
        } catch (IOException ex) {
            Logger.getLogger(fotoBD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return data;
    }

    //metodo que dada una cadena de bytes la convierte en una imagen con extension jpeg
    private Image ConvertirImagen(byte[] bytes) throws IOException {
        Image imag = null;
        boolean ban = false;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Iterator readers = ImageIO.getImageReadersByFormatName("png");
            ImageReader reader = (ImageReader) readers.next();
            Object source = bis; // File or InputStream
            ImageInputStream iis = ImageIO.createImageInputStream(source);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            imag = reader.read(0, param);
        } catch (IIOException im) {
            ban = true;
        }
        if (ban == true) {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Iterator readers = ImageIO.getImageReadersByFormatName("JPG");
            ImageReader reader = (ImageReader) readers.next();
            Object source = bis; // File or InputStream
            ImageInputStream iis = ImageIO.createImageInputStream(source);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            imag = reader.read(0, param);
        }

        return imag;
    }
}
