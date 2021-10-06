/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AcesoUsuario.java
 *
 * Created on 25-nov-2010, 10:56:50
 */
package PaqFormulario;

import clases.Conexion;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.sql.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Danny Veron
 */
public final class AcesoUsuario extends javax.swing.JFrame {

    private Connection cn;
    private Statement st;
    private ResultSet rs;
    static char[] password;
    static String CodigoUsuario;
    public String usu = "ADMIN";
    public String pas = "jesus33maria13";
    private InputMap map;
    private InputStream mapTexto;
    public Conexion con;
    public static String host, base, user, pass;
    byte[] textocifrado;
    static String codigocatedra;
    PBEParameterSpec param;
    PBEKeySpec pbekey;
    SecretKeyFactory secretfac;
    SecretKey clave;
    static String sede;

    /**
     * Creates new form AcesoUsuario
     */
    public AcesoUsuario() {

        initComponents();
        centrar();
        leearchivodb();
        botonEnter();
    }
    
    @Override
    public Image getIconImage() {
        Image retValue = Toolkit.getDefaultToolkit().
                getImage(ClassLoader.getSystemResource("paqImagenes/logoDVSoft.png"));


        return retValue;
    }

    public void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), pantalla.height / 2 - (frame.height / 2));
    }

    public void botonEnter() {

        map = new InputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        btncan.setInputMap(0, map);
        btnsal.setInputMap(0, map);
        btnace.setInputMap(0, map);

    }
    
    public byte[] encriptar(String textoPlano) {
        byte[] textoencriptado = null;
        try {
            String claveOriginal = textoPlano;
            String semilla = "0123456789";
            // Generamos una clave secreta.
            SecretKeySpec desKey = new SecretKeySpec(new String((semilla.trim().concat("99999999")).substring(0, 8)).getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            textoencriptado = cipher.doFinal(claveOriginal.getBytes());
            //textoencriptado= new String(textocifrado, "UTF8");
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return textoencriptado;
    }

    public String desencriptar(byte[] textoPlano) {
        String texto = null;
        try {

            String semilla = "0123456789";
            SecretKeySpec desKey = new SecretKeySpec(new String((semilla.trim().concat("99999999")).substring(0, 8)).getBytes(), "DES");
            Cipher cifrador = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cifrador.init(Cipher.DECRYPT_MODE, desKey);
            byte[] textodesencriptado = cifrador.doFinal(textoPlano);
            texto = (new String(textodesencriptado, "UTF8"));
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return texto;
    }

    public void acceso() {
        if (usu.equals(txtusu.getText()) && pas.equals(txtcon.getText())) {
        } else {
            JOptionPane.showMessageDialog(null, "Nombre de usuario o contraseña incorrecta", "ERROR", JOptionPane.ERROR_MESSAGE);
            txtusu.setText("");
            txtcon.setText("");
            txtusu.grabFocus();
        }
    }

    public void leearchivodb() {
        InputStream f = getClass().getResourceAsStream("/clases/config.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(f));
        //File f = new File("/clases/config.txt");
        String s = "";
        //lee caracteres contenidos en archivo
        try {
            //FileReader fr = new FileReader(f);
            //BufferedReader br = new BufferedReader(fr);
            s = br.readLine();
            //System.out.println(s);
            //para leer palabra por palabra desde el archivo
            StringTokenizer tk = new StringTokenizer(s, "-");
            base = tk.nextToken();
            host = tk.nextToken();
            user = "root";
            pass = "";
        } catch (FileNotFoundException e) {
            System.out.println("Error al abrir el archivo");
        } catch (IOException e) {
            System.out.println("Error al leer");
        }

    }

    /*public void escribirArchivoBD() {
     File f = new File("src/clases/config.txt");
     try {
     FileWriter fw = new FileWriter(f);
     fw.write(txtbd.getText() + "-" + txtip.getText() + "-" + txtuser.getText() + "-" + txtpas.getText());
     //System.out.println(txtpas.getPassword());
     fw.close();
     } catch (IOException e) {
     System.out.println(e.toString());
     return;
     }

     conexionbd();
     }*/
    public void conexionbd() {
        //host = txtip.getText();
        //base = txtbd.getText();
        //user = txtuser.getText();
        //pass = txtpas.getText();

        con = new Conexion(host, base, user, pass);
        if (con.conexiondb == false) {
            System.exit(0);
        }

    }

    public boolean ValidarUsuario() {
        boolean val = false;
        try {
            String consul = "SELECT * FROM usuario WHERE usu_nic='" + txtusu.getText() + "'";
            //System.out.println(consul);
            conexionbd();
            ResultSet res;
            res = con.Consulta(consul);
            if (res.next()) {
                String nombre, contrasena;
                CodigoUsuario = res.getString("usu_cod");
                nombre = res.getString("usu_nic");
                contrasena = res.getString("usu_pas");
                //System.out.println("usuario: "+nombre+" contraseña: "+contrasena);
                if (!((nombre.equals(txtusu.getText())) && (contrasena.equals(txtcon.getText())))) {
                    val = true;
                } /*else {
                    //codigocatedra = res.getString("cat_codigo");
                    String sed = "SELECT * FROM catedra WHERE cat_codigo=" + res.getString("cat_codigo");
                    res = con.Consulta(sed);
                    res.next();
                    sede = res.getString("cat_sede");
                }*/
            } else {
                val = true;
            }
            if (val == true) {
                JOptionPane.showMessageDialog(null, "Datos incorrectos", "ERROR", JOptionPane.ERROR_MESSAGE);
                txtcon.setText("");
                txtusu.setText("");
                txtusu.grabFocus();
                btnace.setEnabled(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    /**
     * This method is called from within the constructor to initialize theform.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        txtusu = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btncan = new javax.swing.JButton();
        btnace = new javax.swing.JButton();
        btnsal = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        txtcon = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Acceso de Usuario");
        setIconImage(getIconImage());
        setMaximizedBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setMinimumSize(null);
        setResizable(false);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/contraseña .png"))); // NOI18N
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);

        txtusu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtusu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtusuActionPerformed(evt);
            }
        });
        txtusu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtusuFocusLost(evt);
            }
        });

        jLabel1.setText("Usuario");

        jLabel2.setText("Contraseña");

        btncan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/cancelar.png"))); // NOI18N
        btncan.setToolTipText("Cancelar");
        btncan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncanActionPerformed(evt);
            }
        });

        btnace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/Listo.png"))); // NOI18N
        btnace.setToolTipText("Aceptar");
        btnace.setEnabled(false);
        btnace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaceActionPerformed(evt);
            }
        });

        btnsal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/salirPuerta.png"))); // NOI18N
        btnsal.setToolTipText("Salir");
        btnsal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalActionPerformed(evt);
            }
        });

        txtcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtconActionPerformed(evt);
            }
        });
        txtcon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtconFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtcon)
                            .addComponent(txtusu, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(btnace, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btncan, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnsal, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnace, btncan, btnsal});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtusu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtcon, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btnace, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btncan, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnsal)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnace, btncan, btnsal});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaceActionPerformed
        if (ValidarUsuario() == false) {
            FrmPrincipal principal = new FrmPrincipal();
            principal.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnaceActionPerformed

    private void txtusuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtusuActionPerformed
        txtusu.setText(txtusu.getText().toUpperCase());
        txtcon.setEnabled(true);
        txtcon.grabFocus();

    }//GEN-LAST:event_txtusuActionPerformed

    private void txtconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtconActionPerformed
        btnace.setEnabled(true);
        btnace.grabFocus();
        btnace.doClick();
    }//GEN-LAST:event_txtconActionPerformed

    private void btnsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalActionPerformed
        AcesoUsuario.this.dispose();
    }//GEN-LAST:event_btnsalActionPerformed

    private void btncanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncanActionPerformed
        txtusu.setText("");
        txtcon.setText("");
        txtusu.grabFocus();
    }//GEN-LAST:event_btncanActionPerformed

    private void txtusuFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtusuFocusLost
        txtusu.setText(txtusu.getText().toUpperCase());
        txtcon.setEnabled(true);
        txtcon.grabFocus();
    }//GEN-LAST:event_txtusuFocusLost

    private void txtconFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtconFocusLost
            btnace.setEnabled(true);
            btnace.grabFocus();                   
    }//GEN-LAST:event_txtconFocusLost

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // setTheme(String themeName, String licenseKey, String logoString)
                    com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme("Green", "INSERT YOUR LICENSE KEY HERE", "DV Soft");
                    // select the Look and Feel
                    UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");

                    new AcesoUsuario().setVisible(true);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(AcesoUsuario.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnace;
    private javax.swing.JButton btncan;
    private javax.swing.JButton btnsal;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPasswordField txtcon;
    private javax.swing.JTextField txtusu;
    // End of variables declaration//GEN-END:variables
}
