/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPrincipal.java
 *
 * Created on 21-may-2010, 14:42:26
 */
package PaqFormulario;

import clases.Conexion;
import com.mysql.jdbc.Connection;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 * PP
 *
 * @author Danny Veron
 */
public class FrmPrincipal extends javax.swing.JFrame implements Runnable {

    private Connection cn = null;
    private ResultSet rs = null;
    static logoPrincipal logo;
    private Conexion con;
    private Thread HiloEstato;
    

    /**
     * Creates new form FrmPrincipal
     */
    @SuppressWarnings("static-access")
    public FrmPrincipal() {
        initComponents();
        centrar();
        cargar();
        this.setExtendedState(this.MAXIMIZED_BOTH);
        this.pack();
        Date date = new Date();
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        lblFecha.setText(df.format(date));
        ejecutaHilo();
    }

    private void conexion() {
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
    }

    private void ejecutaHilo() {
        if (HiloEstato == null) {
            HiloEstato = new Thread(this);
            HiloEstato.start();
        }
    }

    @Override
    public void run() {
            ActualizaEstadoDeCuenta();
    }

    private void ActualizaEstadoDeCuenta() {
        try {
            Date d = new Date();
            int mes = d.getMonth() + 1;
            int dia = d.getDate();
            String aluVen = "SELECT alu_cod,pago_ven,cuota FROM alumno";
            conexion();
            rs = con.Consulta(aluVen);
            while (rs.next()) {
                String pago = "SELECT * FROM pagos WHERE alu_cod=" + rs.getString(1)
                        + " AND mes=" + mes;
                ResultSet SelectPago = con.Consulta(pago);
                if (SelectPago.next() == false) {
                    if (dia > rs.getInt(2)) {
                        //Crear un nuevo codigo para gago a insertar
                        String sqlCodPago = "Select ifnull(max(pag_cod),0)+1 codigo From pagos";
                        ResultSet rsCodPago = con.Consulta(sqlCodPago);
                        rsCodPago.next();
                        //Ver si hay saldos anteriores
                        String sqlSaldo = "SELECT SUM(saldo) FROM pagos "
                                + "WHERE alu_cod=" + rs.getString(1);
                        ResultSet rsSaldo = con.Consulta(sqlSaldo);
                        rsSaldo.next();
                        int suma = rs.getInt(3) + rsSaldo.getInt(1);
                        //guardar deuda
                        String GuardarDeuda = "INSERT INTO pagos VALUES(" + rsCodPago.getString(1)
                                + "," + rs.getString(1) + "," + mes + "," + suma + ",'P')";
                        con.Guardar(GuardarDeuda);
                    }
                }
            }
            con.CerrarConexion();
            HiloEstato = null;
        } catch (SQLException ex) {
            Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Image getIconImage() {
        Image retValue = Toolkit.getDefaultToolkit().
                getImage(ClassLoader.getSystemResource("paqImagenes/logoDVSoft.png"));


        return retValue;
    }

    public final void cargar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        logo = new logoPrincipal();
        logo.setSize(960, 600);
        this.add(logo);
        logo.setLocation((pantalla.width / 2 - (960 / 2)), 50);//(40,50);
        this.pack();
    }

    public final void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        //setLocation((pantalla.width/2-(frame.width/2)),0);

    }

    public static boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static void CambiarPanel() {
        logo.removeAll();
        logo.updateUI();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFecha = new javax.swing.JLabel();
        menPrincipal = new javax.swing.JMenuBar();
        menArc = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        menPro = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        mencuo = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        menInf = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        menRef = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        MenServi = new javax.swing.JMenuItem();
        MenUsuario = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        menAyu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gestion-Instituto");
        setIconImage(getIconImage());
        setResizable(false);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        lblFecha.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        lblFecha.setForeground(new java.awt.Color(0, 153, 0));

        menPrincipal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 0), 2, true));

        menArc.setMnemonic('a');
        menArc.setText("Archivo"); // NOI18N

        jMenuItem4.setText("Salir"); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        menArc.add(jMenuItem4);

        menPrincipal.add(menArc);

        menPro.setMnemonic('g');
        menPro.setText("Gestion"); // NOI18N
        menPro.setAutoscrolls(true);
        menPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menProActionPerformed(evt);
            }
        });

        jMenuItem7.setText("Alumno");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        menPro.add(jMenuItem7);

        mencuo.setText("Cuota");
        mencuo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mencuoActionPerformed(evt);
            }
        });
        menPro.add(mencuo);

        jMenuItem5.setText("Examen");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        menPro.add(jMenuItem5);

        jMenu1.setText("Libros");

        jMenuItem10.setText("Agregar Libro");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        jMenuItem6.setText("Prestamo");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem11.setText("Devolucion");
        jMenu1.add(jMenuItem11);

        jMenuItem8.setText("Lista de Libros Pestados");
        jMenu1.add(jMenuItem8);

        menPro.add(jMenu1);

        menPrincipal.add(menPro);

        menInf.setMnemonic('i');
        menInf.setText("Informe"); // NOI18N

        jMenuItem12.setText("Estado de cuenta");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        menInf.add(jMenuItem12);

        menPrincipal.add(menInf);

        menRef.setMnemonic('r');
        menRef.setText("Referenciales"); // NOI18N
        menRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menRefActionPerformed(evt);
            }
        });

        jMenuItem2.setText("Carrera");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        menRef.add(jMenuItem2);

        jMenuItem9.setText("Ciudad");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        menRef.add(jMenuItem9);

        jMenuItem1.setText("Colegio");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menRef.add(jMenuItem1);

        MenServi.setText("Materia"); // NOI18N
        MenServi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenServiActionPerformed(evt);
            }
        });
        menRef.add(MenServi);

        MenUsuario.setText("Usuario"); // NOI18N
        MenUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenUsuarioActionPerformed(evt);
            }
        });
        menRef.add(MenUsuario);

        jMenuItem3.setText("Profesor");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        menRef.add(jMenuItem3);

        menPrincipal.add(menRef);

        menAyu.setText("Ayuda");
        menPrincipal.add(menAyu);

        setJMenuBar(menPrincipal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(424, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(479, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void menRefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menRefActionPerformed
    }//GEN-LAST:event_menRefActionPerformed

    private void MenServiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenServiActionPerformed
        logo.removeAll();
        logo.updateUI();
        PanMateria pan = new PanMateria();
        logo.add(pan, "Panel1");
        logo.updateUI();
        PanMateria.btnAgr.grabFocus();
    }//GEN-LAST:event_MenServiActionPerformed

    private void MenUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenUsuarioActionPerformed
        logo.removeAll();
        logo.updateUI();
        FrmUsuario pan = new FrmUsuario();
        logo.add(pan, "Panel1");
        logo.updateUI();
        FrmUsuario.btnAgr.grabFocus();
    }//GEN-LAST:event_MenUsuarioActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        FrmCiudad ciu = new FrmCiudad(this, true);
        ciu.setVisible(true);
        FrmCiudad.btnAgr.grabFocus();
}//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

        FrmCarrera car = new FrmCarrera(this, true);
        car.setVisible(true);
        FrmCarrera.btnAgr.grabFocus();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void menProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menProActionPerformed
        //logo.removeAll();logo.updateUI();
        //PanAlumno pan=new PanAlumno();
        //logo.add(pan,"Panel1");logo.updateUI();
        //pan.btnnue.grabFocus();
        FrmAlumno al = new FrmAlumno(null, true);
        al.setVisible(true);

    }//GEN-LAST:event_menProActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        FrmColegio col = new FrmColegio(this, true);
        col.setVisible(true);
        FrmColegio.btnAgr.grabFocus();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed

        FrmAlumno alu = new FrmAlumno(this, true);
        alu.setVisible(true);

    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        logo.removeAll();
        logo.updateUI();
        PanProfesor pro = new PanProfesor();
        logo.add(pro, "Panel1");
        logo.updateUI();
        PanProfesor.btnAgr.grabFocus();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        logo.removeAll();
        logo.updateUI();
        PanExamen pro = new PanExamen();
        logo.add(pro, "Panel1");
        logo.updateUI();
        PanExamen.cbomat.grabFocus();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void mencuoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mencuoActionPerformed
        logo.removeAll();
        logo.updateUI();
        FrmCuota cuo = new FrmCuota(this, false);
        cuo.setVisible(true);
    }//GEN-LAST:event_mencuoActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        
    }//GEN-LAST:event_formFocusGained

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        ReporteEstadoDeCuenta re=new ReporteEstadoDeCuenta(this, true);
        re.setVisible(true);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
       FrmPrestamoLibro lib=new FrmPrestamoLibro(this, true);
       lib.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        FrmNuevoLibro lib=new FrmNuevoLibro(this, true);
        lib.btnAgr.grabFocus();
        lib.setVisible(true);
        
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // setTheme(String themeName, String licenseKey, String logoString)
                    com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme("Green", "INSERT YOUR LICENSE KEY HERE", "Hospital de Clinicas");

                    // select the Look and Feel
                    UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");

                    new FrmPrincipal().setVisible(true);

                } catch (Exception ex) {
                }

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem MenServi;
    private javax.swing.JMenuItem MenUsuario;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    public static javax.swing.JLabel lblFecha;
    private javax.swing.JMenu menArc;
    private javax.swing.JMenu menAyu;
    private javax.swing.JMenu menInf;
    private javax.swing.JMenuBar menPrincipal;
    private javax.swing.JMenu menPro;
    private javax.swing.JMenu menRef;
    private javax.swing.JMenuItem mencuo;
    // End of variables declaration//GEN-END:variables
}
