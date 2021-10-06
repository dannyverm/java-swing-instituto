/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import clases.Conexion;
import com.mysql.jdbc.Connection;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author JulFX
 */
public class ReporteEstadoDeCuenta extends javax.swing.JDialog implements Runnable{
    private Connection cn = null;
    private ResultSet rs = null;
    static logoPrincipal logo;
    private Conexion con;
    private Thread HiloVerTodo;
    private Thread HiloPorCarrera;
    private Thread HiloPorTurno;
    private ResultSet rscboCar;
    private Object Codtur;
    private Object Codcar;
    /**
     * Creates new form ReporteEstadoDeCuenta
     */
    public ReporteEstadoDeCuenta(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();botonEnter();cargaCboCar();centrar();
    }
    
    private void botonEnter() {
        InputMap map = new InputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        btnAcar.setInputMap(0, map);
        btnAtur.setInputMap(0, map);
        btnAver.setInputMap(0, map);
    }
    
    private void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)),0 );//pantalla.height / 2 - (frame.height / 2));
    }
    
    private void conexion() {
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
    }
    
    private void cargaCboCar() {
        try {
            conexion();
            String sql = "SELECT * FROM carrera ORDER BY car_des";
            rscboCar = con.Consulta(sql);
            cbocar.removeAllItems();
            cbocar.addItem("");
            while (rscboCar.next()) {
                try {
                    cbocar.addItem(rscboCar.getString("car_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String comboCodigo(String consulta, String ColumnaBD, String descripcion) {
        String codigo = "";
        try {
            conexion();
            rs = con.Consulta(consulta + " WHERE " + ColumnaBD + "='" + descripcion + "'");
            //System.out.println(consulta + " WHERE " + ColumnaBD + "='" + descripcion + "'");
            rs.beforeFirst();
            if (rs.next()) {
                rs.beforeFirst();
                while (rs.next()) {

                    if (descripcion.equals(rs.getString(ColumnaBD))) {
                        codigo = rs.getString(1);
                        break;
                    }
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }
    
    private void capturarCodigoCombos() {
        if (cbocar.getSelectedIndex() > 0) {
            Codcar = comboCodigo("SELECT * FROM carrera", "car_des", (String) cbocar.getSelectedItem());
            //cboprv.grabFocus();
        }
    }
    

    @Override
    public void run() {
        if(HiloVerTodo!=null){
            try {
                ClassLoader cl = this.getClass().getClassLoader();
                InputStream fis = (cl.getResourceAsStream("paqInformes/VerTodo.jasper"));
                JasperReport reporte = (JasperReport) JRLoader.loadObject(fis);
                conexion();
                JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, null, con.getConexion());
                panel.removeAll();panel.updateUI();
                panel.add(new JRViewer(jasperPrint),"Panel1");panel.updateUI();
                HiloVerTodo=null;
            } catch (JRException ex) {
                Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(Level.SEVERE, null, ex);
            }    
        }else if(HiloPorTurno!=null){
            try {
                Map par = new HashMap();
                ClassLoader cl = this.getClass().getClassLoader();
                InputStream fis = (cl.getResourceAsStream("paqInformes/PorTurno.jasper"));
                JasperReport reporte = (JasperReport) JRLoader.loadObject(fis);
                conexion();
                par.put("turno", Codtur);
                JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, par, con.getConexion());
                panel.removeAll();panel.updateUI();
                panel.add(new JRViewer(jasperPrint),"Panel1");panel.updateUI();
                Codtur="";
                HiloPorTurno=null;
            } catch (JRException ex) {
                Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(HiloPorCarrera!=null){
            capturarCodigoCombos();
            try {
                Map par = new HashMap();
                    ClassLoader cl = this.getClass().getClassLoader();
                    InputStream fis = (cl.getResourceAsStream("paqInformes/PorCarreras.jasper"));
                    JasperReport reporte = (JasperReport) JRLoader.loadObject(fis);
                    conexion();
                    par.put("carrera", Codcar);
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, par, con.getConexion());
                    panel.removeAll();panel.updateUI();
                    panel.add(new JRViewer(jasperPrint),"Panel1");panel.updateUI();
                    Codcar="";
                    HiloPorCarrera=null;
            } catch (JRException ex) {
                Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupBoton = new javax.swing.ButtonGroup();
        panel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        rbotonma = new javax.swing.JRadioButton();
        rbotonta = new javax.swing.JRadioButton();
        rbotonno = new javax.swing.JRadioButton();
        btnAtur = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        cbocar = new javax.swing.JComboBox();
        btnAcar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnAver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Estado de cuenta");

        panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel.setLayout(new java.awt.CardLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 0), 2, true), "E.C. Por Turno"));

        grupBoton.add(rbotonma);
        rbotonma.setText("Mañana");
        rbotonma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbotonmaActionPerformed(evt);
            }
        });

        grupBoton.add(rbotonta);
        rbotonta.setText("Tarde");
        rbotonta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbotontaActionPerformed(evt);
            }
        });

        grupBoton.add(rbotonno);
        rbotonno.setText("Noche");
        rbotonno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbotonnoActionPerformed(evt);
            }
        });

        btnAtur.setText("Actualizar");
        btnAtur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAturActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(rbotonma)
                        .addGap(18, 18, 18)
                        .addComponent(rbotonta))
                    .addComponent(btnAtur))
                .addGap(18, 18, 18)
                .addComponent(rbotonno)
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbotonma)
                    .addComponent(rbotonta)
                    .addComponent(rbotonno))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAtur)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 0), 2, true), "E.C. Por Carrera"));

        jLabel24.setText("Carrera");

        cbocar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnAcar.setText("Actualizar");
        btnAcar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbocar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(72, 72, 72))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(176, Short.MAX_VALUE)
                .addComponent(btnAcar)
                .addGap(173, 173, 173))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbocar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAcar)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 0), 2, true), "Ver Todo"));

        btnAver.setText("Actualizar");
        btnAver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAver, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnAver)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbotonmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbotonmaActionPerformed
        Codtur="1";
    }//GEN-LAST:event_rbotonmaActionPerformed

    private void btnAverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAverActionPerformed
        if(HiloVerTodo==null){
            HiloVerTodo=new Thread(this);
            HiloVerTodo.start();
        }
    }//GEN-LAST:event_btnAverActionPerformed

    private void btnAturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAturActionPerformed
        if(HiloPorTurno==null){
            HiloPorTurno=new Thread(this);
            HiloPorTurno.start();
        }
    }//GEN-LAST:event_btnAturActionPerformed

    private void rbotontaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbotontaActionPerformed
        Codtur="2";
    }//GEN-LAST:event_rbotontaActionPerformed

    private void rbotonnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbotonnoActionPerformed
       Codtur="3";
    }//GEN-LAST:event_rbotonnoActionPerformed

    private void btnAcarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcarActionPerformed
        if(HiloPorCarrera==null){
            HiloPorCarrera=new Thread(this);
            HiloPorCarrera.start();
        }
    }//GEN-LAST:event_btnAcarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ReporteEstadoDeCuenta dialog = new ReporteEstadoDeCuenta(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcar;
    private javax.swing.JButton btnAtur;
    private javax.swing.JButton btnAver;
    private javax.swing.JComboBox cbocar;
    private javax.swing.ButtonGroup grupBoton;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel panel;
    private javax.swing.JRadioButton rbotonma;
    private javax.swing.JRadioButton rbotonno;
    private javax.swing.JRadioButton rbotonta;
    // End of variables declaration//GEN-END:variables
}
