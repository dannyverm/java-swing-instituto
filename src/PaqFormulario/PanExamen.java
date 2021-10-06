/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanExamen.java
 *
 * Created on 20-jun-2012, 16:19:31
 */
package PaqFormulario;

import clases.Conexion;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author JulFX
 */
public class PanExamen extends javax.swing.JPanel {

    private float tran = 0.80f;
    private ResultSet rs;
    private Conexion con;
    private ResultSet rscboMat;
    private String codTurno;
    private ResultSet rscboTur;
    private String codMateria;

    /** Creates new form PanExamen */
    public PanExamen() {
        initComponents();
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        cargaCboMat();
        cargaCboTurno();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        AlphaComposite old = (AlphaComposite) g2.getComposite();
        g2.setComposite(AlphaComposite.SrcOver.derive(getTran()));
        super.paintComponent(g);
        g2.setComposite(old);
    }

    private float getTran() {
        return tran;
    }

    private void setTran(float tran) {
        this.tran = tran;
    }

    private String codigoCombo(String sql, String nombreColumTabla) {
        String codigoBD = null;
        try {
            rs = con.Consulta(sql);
            rs.next();
            codigoBD = rs.getString(nombreColumTabla);

        } catch (SQLException ex) {
            Logger.getLogger(PanExamen.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigoBD;
    }

    private void cargaCboMat() {
        try {
            String sql = "SELECT * FROM materia ORDER BY mat_des";
            rscboMat = con.Consulta(sql);
            cbomat.removeAllItems();
            cbomat.addItem("");
            while (rscboMat.next()) {
                try {
                    cbomat.addItem(rscboMat.getString("mat_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(PanExamen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanExamen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboTurno() {
        try {
            String sql = "SELECT * FROM turno ORDER BY tur_des";
            rscboTur = con.Consulta(sql);
            cbotur.removeAllItems();
            cbotur.addItem("");
            while (rscboTur.next()) {
                try {
                    cbotur.addItem(rscboTur.getString("tur_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(PanExamen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanExamen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void vaciarTabla() {
        int i = 0;

        while (i != tabla.getRowCount()) {
            tabla.setValueAt("", i, 0);
            tabla.setValueAt("", i, 1);
            tabla.setValueAt("", i, 2);
            tabla.setValueAt("", i, 3);
            i++;
        }
    }

    public void CargarGrilla() {

        try {

            int c = 0;
            vaciarTabla();
            String sql = "SELECT *"
                    + " FROM alumno"
                    + " WHERE nivel='" + cboniv.getSelectedItem() + "'"
                    + " AND tur_cod=" + codTurno
                    + " ORDER BY alu_ape";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (tabla.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) tabla.getModel();
                        tm.addRow(new Object[]{new String(""),new String(""), new String(""), new String("")});
                        tabla.setModel(tm);
                    }
                    tabla.setValueAt(rs.getString(9), c, 0);
                    tabla.setValueAt(rs.getString(7), c, 1);
                    tabla.setValueAt(rs.getString(6), c, 2);
                    c++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanExamen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buscarCi() {
        boolean yaHay = false;
        int preg = 0;
        try {
            String sql = "SELECT * FROM alumno WHERE alu_ci='" + txtci.getText() + "' "
                    + "AND nivel='" + cboniv.getSelectedItem() + "'"
                    + " AND tur_cod=" + codTurno;
            rs = con.Consulta(sql);
            if (rs.next()) {
                //Buscar si ya se cargo puntos obtenidos del alumno
                int fila = 0;
                System.out.println(tabla.getValueAt(fila, 3));
                while (fila < tabla.getRowCount()) {

                    if (!String.valueOf(tabla.getValueAt(fila, 3)).isEmpty() && tabla.getValueAt(fila, 1).equals(rs.getObject("alu_ape"))
                            && tabla.getValueAt(fila, 2).equals(rs.getObject("alu_nom"))) {
                        yaHay = true;
                    }

                    fila++;
                }

                if (yaHay == true) {
                    preg = JOptionPane.showConfirmDialog(null, "Ya ha ingresado puntaje obtenido por: "
                            + "\n" + rs.getString("alu_nom") + " " + rs.getString("alu_ape")
                            + "\n¿Desea modificar?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);
                }

                if (preg == JOptionPane.OK_OPTION) {
                    String res = JOptionPane.showInputDialog(this, "Ingrese puntaje obtenido por: "
                            + "\n" + rs.getString("alu_nom") + " " + rs.getString("alu_ape"));
                    int f = 0;
                    while (f < tabla.getRowCount()) {
                        if (tabla.getValueAt(f, 1).equals(rs.getObject("alu_ape"))
                                && tabla.getValueAt(f, 2).equals(rs.getObject("alu_nom"))) {
                            if (Integer.parseInt(res) > Integer.parseInt(txttp.getText())) {
                                JOptionPane.showMessageDialog(null, "El número ingresado "
                                        + "no puede ser mayor que: " + txttp.getText());
                            } else {
                                tabla.setValueAt(res, f, 3);
                                txtci.setText("");
                                txtci.grabFocus();
                                btngra.setEnabled(true);
                            }
                        }
                        f++;
                    }
                } else {
                    txtci.setText("");
                    txtci.grabFocus();
                }
            } else {
                JOptionPane.showMessageDialog(null, "El número de cedula no es correcto,"
                        + "\nNo corresponde a ningun alumno de turno o nivel especificado",
                        "No se encontro alumno", JOptionPane.ERROR_MESSAGE);
                txtci.setText("");
                txtci.grabFocus();
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanExamen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException n) {
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbomat = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cboniv = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtfec = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        txttp = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cbotur = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        btngra = new javax.swing.JButton();
        btncan = new javax.swing.JButton();
        btnsal = new javax.swing.JButton();
        txtci = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(960, 600));
        setPreferredSize(new java.awt.Dimension(960, 600));

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Materia");

        cbomat.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbomat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbomatKeyPressed(evt);
            }
        });

        jLabel2.setText("Nivel de alumno/a");

        cboniv.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Secundario", "Universitario" }));
        cboniv.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cboniv.setEnabled(false);
        cboniv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbonivKeyPressed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24));
        jLabel13.setForeground(new java.awt.Color(51, 153, 0));
        jLabel13.setText("Examen");

        jLabel3.setText("Fecha de Examen");

        txtfec.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtfec.setEnabled(false);
        txtfec.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtfecKeyPressed(evt);
            }
        });

        jLabel4.setText("Total de puntos");

        txttp.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txttp.setEnabled(false);
        txttp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttpActionPerformed(evt);
            }
        });

        jLabel5.setText("turno");

        cbotur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Universitario", "Secundario" }));
        cbotur.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbotur.setEnabled(false);
        cbotur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cboturKeyPressed(evt);
            }
        });

        tabla.setAutoCreateRowSorter(true);
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "C.I. Nro.", "Apellido", "Nombre", "Puntaje"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla.setColumnSelectionAllowed(true);
        tabla.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(tabla);
        tabla.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(0).setMaxWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setMaxWidth(100);

        btngra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/Grabar.jpg"))); // NOI18N
        btngra.setText("Grabar");
        btngra.setEnabled(false);
        btngra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngraActionPerformed(evt);
            }
        });

        btncan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/cancelar2.png"))); // NOI18N
        btncan.setText("Cancelar");
        btncan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncanActionPerformed(evt);
            }
        });

        btnsal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/salirPuerta.png"))); // NOI18N
        btnsal.setText("Salir");
        btnsal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalActionPerformed(evt);
            }
        });

        txtci.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtci.setEnabled(false);
        txtci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtciActionPerformed(evt);
            }
        });
        txtci.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtciKeyPressed(evt);
            }
        });

        jLabel10.setText("Ingrese Nro. C.I.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(266, 266, 266)
                        .addComponent(jLabel13))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addGap(4, 4, 4)
                        .addComponent(cbomat, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addGap(4, 4, 4)
                        .addComponent(cboniv, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3)
                        .addGap(4, 4, 4)
                        .addComponent(txtfec, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel5)
                        .addGap(4, 4, 4)
                        .addComponent(cbotur, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(txttp, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(154, 154, 154)
                        .addComponent(btngra)
                        .addGap(6, 6, 6)
                        .addComponent(btncan, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btnsal))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtci, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel13)
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel1))
                    .addComponent(cbomat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel2))
                    .addComponent(cboniv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel3))
                    .addComponent(txtfec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel5))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(cbotur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel4))
                    .addComponent(txttp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10)
                    .addComponent(txtci, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btngra, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btncan)
                    .addComponent(btnsal))
                .addGap(35, 35, 35))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(134, 134, 134)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(178, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalActionPerformed
        FrmPrincipal.CambiarPanel();
    }//GEN-LAST:event_btnsalActionPerformed

    private void cbomatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbomatKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbomat.getSelectedIndex() != 0) {
                String sql = "Select * From materia Where mat_des='" + cbomat.getSelectedItem() + "'";
                codMateria = codigoCombo(sql, "mat_cod");
                cboniv.setEnabled(true);
                cboniv.grabFocus();
            } else {
                JOptionPane.showMessageDialog(null, "El campo no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
                cbomat.grabFocus();
            }

        }
    }//GEN-LAST:event_cbomatKeyPressed

    private void cbonivKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbonivKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cboniv.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(null, "El campo no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                txtfec.setEnabled(true);
                txtfec.grabFocus();
            }
        }
    }//GEN-LAST:event_cbonivKeyPressed

    private void txtfecKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtfecKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txtfec.getDate() == null) {
                JOptionPane.showMessageDialog(null, "El campo no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
                txtfec.grabFocus();
            } else {
                cbotur.setEnabled(true);
                cbotur.grabFocus();
            }
        }
    }//GEN-LAST:event_txtfecKeyPressed

    private void cboturKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboturKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbotur.getSelectedIndex() != 0) {
                String sql = "Select * From turno Where tur_des='" + cbotur.getSelectedItem() + "'";
                codTurno = codigoCombo(sql, "tur_cod");
                CargarGrilla();
                tabla.updateUI();
                txttp.setEnabled(true);
                txttp.grabFocus();
            } else {
                JOptionPane.showMessageDialog(null, "El campo no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
                cbotur.grabFocus();
            }

        }
    }//GEN-LAST:event_cboturKeyPressed

    private void txttpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttpActionPerformed
        txtci.setEnabled(true);
        txtci.grabFocus();
    }//GEN-LAST:event_txttpActionPerformed

    private void txtciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtciActionPerformed
        if (txtci.getText().isEmpty()) {
            tabla.requestFocus();
            tabla.changeSelection(0, 2, false, false);
        } else {
            buscarCi();
        }
}//GEN-LAST:event_txtciActionPerformed

    private void txtciKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtciKeyPressed

        String keypres = KeyEvent.getKeyText(evt.getKeyCode());

        if (!(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE
                || evt.getKeyCode() == KeyEvent.VK_DELETE
                || evt.getKeyCode() == KeyEvent.VK_ENTER
                || FrmPrincipal.isNumeric(String.valueOf(evt.getKeyChar())) == true)) {
            if (txtci.getText().isEmpty()) {
                tabla.requestFocus();
                tabla.changeSelection(0, 2, false, false);
            } else {
                try {
                    Integer.parseInt(KeyEvent.getKeyText(evt.getKeyCode()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Solo puede Ingresar Numeros", "Error", JOptionPane.ERROR_MESSAGE);
                    txtci.setText(txtci.getText().replace(keypres.toLowerCase(), ""));
                    txtci.setText(txtci.getText().replace(keypres.toUpperCase(), ""));
                    txtci.grabFocus();
                }
            }
        }
}//GEN-LAST:event_txtciKeyPressed

    private void btngraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngraActionPerformed
        String codigoExamen = null;
        int res = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                rs = con.Consulta("SELECT IFNULL(MAX(exa_cod),0)+1 codigo FROM examen");
                if (rs.next()) {
                    codigoExamen = rs.getString("codigo");
                }
                String sql = "INSERT INTO examen (exa_cod,mat_cod,exa_fec,exa_tp)"
                        + " VALUES (" + codigoExamen + "," + codMateria + ","
                        + df.format(txtfec.getDate()) + "," + txttp.getText() + ")";
                //System.out.println(sql);
                con.Guardar(sql);
                //Guardamos puntaje por alumno
                int fil=0;
                while(fil<tabla.getRowCount()){
                    if (!String.valueOf(tabla.getValueAt(fil, 2)).isEmpty()){
                        String guardarCalif="INSERT INTO examen_alumno (exa_cod,alu_cod,calificacion) " +
                                "VALUES("+codigoExamen+"," +
                                "(SELECT alu_cod FROM alumno WHERE alu_ci='"+tabla.getValueAt(fil, 0)+"')," +
                                tabla.getValueAt(fil, 3)+")";
                        con.Guardar(guardarCalif);
                    }
                    fil++;
                }
                JOptionPane.showMessageDialog(null, "Los datos se guardaron correctamente");
            } catch (SQLException ex) {
                Logger.getLogger(PanExamen.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btngraActionPerformed

    private void btncanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncanActionPerformed
        cbomat.setSelectedIndex(0);cboniv.setSelectedIndex(0);
        cbotur.setSelectedIndex(0);txtci.setText("");
        txtfec.setDate(null);txttp.setText("");
        DefaultTableModel tm=(DefaultTableModel) tabla.getModel();
        int i=0;
        while(i<=tabla.getRowCount()){
         tm.removeRow(0);
         tabla.setModel(tm);
         i++;
        }
        
    }//GEN-LAST:event_btncanActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncan;
    private javax.swing.JButton btngra;
    private javax.swing.JButton btnsal;
    public static javax.swing.JComboBox cbomat;
    private javax.swing.JComboBox cboniv;
    private javax.swing.JComboBox cbotur;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabla;
    private javax.swing.JTextField txtci;
    private com.toedter.calendar.JDateChooser txtfec;
    private javax.swing.JTextField txttp;
    // End of variables declaration//GEN-END:variables
}
