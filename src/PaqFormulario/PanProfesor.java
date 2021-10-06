/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmUsuario.java
 *
 * Created on 04-jun-2010, 18:16:53
 */
package PaqFormulario;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import clases.Conexion;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 *
 * @author Danny Veron
 */
public class PanProfesor extends javax.swing.JPanel {

    private Connection cn = null;
    static Conexion con;
    private ResultSet rs = null;
    private ResultSet rs2 = null;
    private ResultSet rsSer = null;
    private Statement st = null;
    public String SerCod = null;
    private String PasUsu = null;
    public boolean botonagregar = false;
    public boolean botonmodificar = false;
    public boolean botonborrar = false;
    private float tran = 0.80f;

    /** Creates new form FrmUsuario */
    public PanProfesor() {
        initComponents();
        botonEnter();
        //System.out.println("host: " + AcesoUsuario.host);
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        ActualizarGrilla();
        ActualizarGrillaMateria();
    }

    public void ActualizarGrillaMateria() {
        String Nombre = null;
        try {

            int c = 0;

            String sql = "SELECT *"
                    + " FROM materia"
                    + " ORDER BY mat_des";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (tablaMateria.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) tablaMateria.getModel();
                        tm.addRow(new Object[]{new Boolean(false), new String("")});
                        tablaMateria.setModel(tm);
                    }
                    Nombre = rs.getString("mat_des");
                    tablaMateria.setValueAt(Nombre, c, 1);
                    c++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanProfesor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
        }
    }

    public boolean comparar(String sql) {
        boolean r = false;
        try {
            rs = con.Consulta(sql);
            if (rs.next()) {
                r = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanProfesor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TablaUsuario = new javax.swing.JTable();
        jPanel = new javax.swing.JPanel();
        btnAgr = new javax.swing.JButton();
        btnMod = new javax.swing.JButton();
        btnGra = new javax.swing.JButton();
        btnCan = new javax.swing.JButton();
        btnSal = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtNom = new javax.swing.JTextField();
        txtCod = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtApe = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtTel = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCel = new javax.swing.JFormattedTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaMateria = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(807, 514));
        setMinimumSize(new java.awt.Dimension(807, 514));

        TablaUsuario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Codigo", "Descripción"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TablaUsuario.getTableHeader().setReorderingAllowed(false);
        TablaUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaUsuarioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaUsuario);
        TablaUsuario.getColumnModel().getColumn(0).setPreferredWidth(80);
        TablaUsuario.getColumnModel().getColumn(0).setMaxWidth(80);

        jPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnAgr.setText("Agregar"); // NOI18N
        btnAgr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgrActionPerformed(evt);
            }
        });
        btnAgr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAgrKeyPressed(evt);
            }
        });

        btnMod.setText("Modificar"); // NOI18N
        btnMod.setEnabled(false);
        btnMod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModActionPerformed(evt);
            }
        });
        btnMod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnModKeyPressed(evt);
            }
        });

        btnGra.setText("Grabar"); // NOI18N
        btnGra.setEnabled(false);
        btnGra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraActionPerformed(evt);
            }
        });
        btnGra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnGraKeyPressed(evt);
            }
        });

        btnCan.setText("Cancelar"); // NOI18N
        btnCan.setEnabled(false);
        btnCan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCanActionPerformed(evt);
            }
        });
        btnCan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCanKeyPressed(evt);
            }
        });

        btnSal.setText("Cerrar"); // NOI18N
        btnSal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalActionPerformed(evt);
            }
        });
        btnSal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnSalKeyPressed(evt);
            }
        });

        jLabel13.setText("Codigo"); // NOI18N

        txtNom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtNom.setEnabled(false);
        txtNom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomActionPerformed(evt);
            }
        });

        txtCod.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtCod.setEnabled(false);
        txtCod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodActionPerformed(evt);
            }
        });

        jLabel18.setText("Nombre"); // NOI18N

        jLabel19.setText("Apellido"); // NOI18N

        txtApe.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtApe.setEnabled(false);
        txtApe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApeActionPerformed(evt);
            }
        });

        jLabel1.setText("Telefono");

        txtTel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtTel.setEnabled(false);
        txtTel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelActionPerformed(evt);
            }
        });
        txtTel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTelKeyPressed(evt);
            }
        });

        jLabel2.setText("Celular");

        txtCel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        try {
            txtCel.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(####)### - ###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtCel.setEnabled(false);
        txtCel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCelActionPerformed(evt);
            }
        });
        txtCel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCelKeyPressed(evt);
            }
        });

        tablaMateria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "---", "Materia"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tablaMateria);
        tablaMateria.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaMateria.getColumnModel().getColumn(0).setMaxWidth(80);

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addComponent(btnAgr, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMod, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGra, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCan, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSal, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtApe, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCel, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13))
                            .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel18)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtApe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtCel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnAgr, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnMod, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnGra, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnCan, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnSal, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addGap(49, 49, 49))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 36));
        jLabel3.setForeground(new java.awt.Color(51, 153, 0));
        jLabel3.setText("Profesor");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(355, 355, 355)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE))))
                .addGap(79, 79, 79))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodActionPerformed
}//GEN-LAST:event_txtCodActionPerformed

    private void txtNomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomActionPerformed
        if (!txtNom.getText().isEmpty()) {
            txtNom.setText(txtNom.getText().toUpperCase());
            txtApe.setEnabled(true);
            txtApe.grabFocus();
        }
}//GEN-LAST:event_txtNomActionPerformed

    private void btnSalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnSalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            this.btnMod.grabFocus();
        }
}//GEN-LAST:event_btnSalKeyPressed

    private void btnSalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalActionPerformed
        FrmPrincipal.CambiarPanel();
}//GEN-LAST:event_btnSalActionPerformed

    private void btnCanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnSal.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            this.btnGra.grabFocus();
        }
}//GEN-LAST:event_btnCanKeyPressed

    private void btnCanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCanActionPerformed
        int f = 0;
        botonagregar = false;
        botonborrar = false;
        botonmodificar = false;
        btnCan.setEnabled(false);
        btnGra.setEnabled(false);
        btnMod.setEnabled(false);
        btnAgr.setEnabled(true);
        btnMod.setEnabled(true);
        txtCod.setText("");
        txtNom.setText("");
        txtApe.setText("");
        txtApe.setEnabled(false);
        txtTel.setText("");
        txtTel.setEnabled(false);
        txtCel.setText("");
        txtCel.setEnabled(false);
        txtNom.setEnabled(false);
        while (f < tablaMateria.getRowCount()) {
            tablaMateria.setValueAt(null, f, 0);
            f++;
        }

        btnAgr.grabFocus();
}//GEN-LAST:event_btnCanActionPerformed

    private void btnGraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGraKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnCan.grabFocus();
        }
}//GEN-LAST:event_btnGraKeyPressed

    private void btnGraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraActionPerformed
        int res = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            if (botonagregar == true) {
                String sql = "INSERT INTO profesores (pro_cod,pro_nom,pro_ape,pro_tel,pro_cel)"
                        + " VALUES (" + this.txtCod.getText() + ",'" + this.txtNom.getText()
                        + "','" + txtApe.getText() + "','" + txtTel.getText() + "','" + txtCel.getText() + "')";
                //System.out.println(sql);
                con.Guardar(sql);
                ActualizarGrilla();
                botonagregar = false;
                //------------------------------------------------------------

                //GUARDAR materia_profesor
                int i = 0;
                while (i < tablaMateria.getRowCount()) {
                    if (tablaMateria.getValueAt(i, 0) != null) {
                        String sqlDetalles = "INSERT INTO materia_profesores (mat_cod,pro_cod) "
                                + "VALUES ((SELECT mat_cod FROM materia WHERE mat_des='" + tablaMateria.getValueAt(i, 1) + " '),"
                                + txtCod.getText() + ")";
                        con.Guardar(sqlDetalles);
                    }
                    i++;
                }
            }

            //Modificar datos tabla profesores
            if (botonmodificar == true) {
                String update = "UPDATE profesores SET "
                        + "pro_nom='" + txtNom.getText()
                        + "',pro_ape='" + txtApe.getText() + "',pro_tel='" + txtTel.getText()
                        + "',pro_cel='" + txtCel.getText() + "' WHERE pro_cod=" + txtCod.getText();
                //System.out.println(update);
                con.Guardar(update);

                //Modificar materia_profesores primero borramos todos los datos de ese prof
                //Para insertar los nuevos datos
                String delete = "DELETE FROM materia_profesores WHERE pro_cod=" + txtCod.getText();
                con.Guardar(delete);

                //Aqui guardamos los nuevos datos
                int i = 0;
                while (i < tablaMateria.getRowCount()) {
                    if (tablaMateria.getValueAt(i, 0) != null) {
                        if (tablaMateria.getValueAt(i, 0).toString().equals("true")) {
                            String sqlDetalles = "INSERT INTO materia_profesores (mat_cod,pro_cod) "
                                    + "VALUES ((SELECT mat_cod FROM materia WHERE mat_des='" + tablaMateria.getValueAt(i, 1) + " '),"
                                    + txtCod.getText() + ")";
                            con.Guardar(sqlDetalles);
                        }
                    }
                    i++;
                }

                ActualizarGrilla();
                botonmodificar = false;
            }
            JOptionPane.showMessageDialog(null, "Los datos se guardaron correctamente");
            this.btnCan.doClick();
        } else {
            this.btnCan.doClick();
        }
}//GEN-LAST:event_btnGraActionPerformed

    private void btnModKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnModKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnSal.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            btnAgr.grabFocus();
        }
}//GEN-LAST:event_btnModKeyPressed

    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
        botonmodificar = true;
        txtNom.setEnabled(true);
        txtNom.grabFocus();
        btnAgr.setEnabled(false);
        this.btnMod.setEnabled(false);
        this.btnCan.setEnabled(true);
}//GEN-LAST:event_btnModActionPerformed

    private void btnAgrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAgrKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.btnMod.grabFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            this.btnSal.grabFocus();
        }
}//GEN-LAST:event_btnAgrKeyPressed

    private void btnAgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrActionPerformed
        try {
            botonagregar = true;
            String sql = "Select ifnull(max(pro_cod),0)+1 codigo From profesores";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            this.txtCod.setText(rs.getString("codigo"));
            btnAgr.setEnabled(false);
            this.btnMod.setEnabled(false);
            this.btnCan.setEnabled(true);
            this.txtNom.setEnabled(true);
            this.txtNom.grabFocus();
        } catch (SQLException ex) {
            Logger.getLogger(FrmUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_btnAgrActionPerformed

    private void txtApeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApeActionPerformed
        if (!txtApe.getText().isEmpty()) {
            txtApe.setText(txtApe.getText().toUpperCase());
            txtTel.setEnabled(true);
            txtTel.grabFocus();
        }
    }//GEN-LAST:event_txtApeActionPerformed

    private void txtTelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelKeyPressed
        String keypres = KeyEvent.getKeyText(evt.getKeyCode());
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtCel.setEnabled(true);
            txtCel.grabFocus();
        } else {
            if (!(keypres.equals("Retroceso") || keypres.equals("Suprimir")
                    || keypres.equals("Introduzca"))) {
                try {
                    Integer.parseInt(String.valueOf(evt.getKeyChar()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Solo puede Ingresar Numeros", "Error", JOptionPane.ERROR_MESSAGE);
                    txtTel.setText(txtTel.getText().replace(keypres.toLowerCase(), ""));
                    txtTel.setText(txtTel.getText().replace(keypres.toUpperCase(), ""));
                    txtTel.grabFocus();
                }

            }
        }
    }//GEN-LAST:event_txtTelKeyPressed

    private void txtTelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelActionPerformed
        txtCel.setEnabled(true);
        txtCel.grabFocus();
    }//GEN-LAST:event_txtTelActionPerformed

    private void txtCelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCelKeyPressed
        String keypres = KeyEvent.getKeyText(evt.getKeyCode());

        if (!(keypres.equals("Retroceso") || keypres.equals("Suprimir") || keypres.equals("Introduzca"))) {
            try {
                Integer.parseInt(String.valueOf(evt.getKeyChar()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Solo puede Ingresar Numeros", "Error", JOptionPane.ERROR_MESSAGE);
                txtCel.setText(txtCel.getText().replace(keypres.toLowerCase(), ""));
                txtCel.setText(txtCel.getText().replace(keypres.toUpperCase(), ""));
                txtCel.grabFocus();
            }

        }
    }//GEN-LAST:event_txtCelKeyPressed

    private void txtCelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCelActionPerformed
        tablaMateria.setEnabled(true);
        tablaMateria.setRowSelectionInterval(0, 0);
        tablaMateria.requestFocus();
        btnGra.setEnabled(true);
    }//GEN-LAST:event_txtCelActionPerformed

    private void TablaUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaUsuarioMouseClicked
        btnCan.setEnabled(true);
        btnCan.doClick();
        btnCan.setEnabled(true);
        btnMod.grabFocus();
        try {
            StringTokenizer token = new StringTokenizer(String.valueOf(TablaUsuario.getValueAt(TablaUsuario.getSelectedRow(), 1)), " ");
            String sql1 = "SELECT * FROM profesores WHERE  pro_nom='" + token.nextToken()
                    + "' AND pro_ape='" + token.nextToken() + "'";

            try {
                String sql = "Select * FROM profesores "
                        + "WHERE pro_cod=" + this.txtCod.getText();
                //System.out.println(sql1);
                rs = con.Consulta(sql1);
                rs.next();
                txtCod.setText(rs.getString("pro_cod"));
                txtNom.setText(rs.getString("pro_nom"));
                txtApe.setText(rs.getString("pro_ape"));
                txtTel.setText(rs.getString("pro_tel"));
                txtCel.setText(rs.getString("pro_cel"));
                btnAgr.setEnabled(false);
                btnMod.setEnabled(true);

                //Buscar materia por profesor
                String sqlMateria = "SELECT m.mat_des FROM materia m,materia_profesores mp,profesores p "
                        + "WHERE m.mat_cod=mp.mat_cod AND mp.pro_cod=p.pro_cod AND p.pro_cod=" + txtCod.getText();
                //System.out.println(sqlMateria);
                rs = con.Consulta(sqlMateria);
                while (rs.next()) {
                    int fila = 0;
                    while (fila < tablaMateria.getRowCount()) {
                        if (rs.getString("m.mat_des").equals(tablaMateria.getValueAt(fila, 1))) {
                            tablaMateria.setValueAt(true, fila, 0);
                        }
                        fila++;
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(FrmUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.txtCod.setEnabled(false);
        } catch (NullPointerException n) {
        } catch (NoSuchElementException n) {
        }
    }//GEN-LAST:event_TablaUsuarioMouseClicked

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

    public float getTran() {
        return tran;
    }

    public void setTran(float tran) {
        this.tran = tran;
    }

    public void botonEnter() {
        InputMap map = new InputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        btnAgr.setInputMap(0, map);
        this.btnCan.setInputMap(0, map);
        this.btnGra.setInputMap(0, map);
        this.btnMod.setInputMap(0, map);
        this.btnSal.setInputMap(0, map);
    }

    public void vaciarTabla() {
        int i = 0;

        while (i != TablaUsuario.getRowCount()) {
            TablaUsuario.setValueAt("", i, 0);
            TablaUsuario.setValueAt("", i, 1);
            i++;
        }
    }

    public boolean validar(String query) {
        boolean ban = true;
        try {
            // System.out.println(query);

            rs = con.Consulta(query);
            if (rs.next() == false) {
                ban = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ban;
    }

    public void ActualizarGrilla() {
        String Nombre = null;
        try {

            int c = 0;
            vaciarTabla();
            String sql = "SELECT *"
                    + " FROM profesores"
                    + " ORDER BY pro_nom";
            //System.out.println(sql);
            rs2 = con.Consulta(sql);
            if (rs2.next() == true) {
                rs2.beforeFirst();
                while (rs2.next()) {
                    if (TablaUsuario.getRowCount() <= rs2.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) TablaUsuario.getModel();
                        tm.addRow(new Object[]{new String(""), new String("")});
                        TablaUsuario.setModel(tm);
                    }
                    Nombre = rs2.getString("pro_nom") + " " + rs2.getString("pro_ape");
                    TablaUsuario.setValueAt(rs2.getString(1), c, 0);
                    TablaUsuario.setValueAt(Nombre, c, 1);
                    c++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmUsuario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException n) {
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TablaUsuario;
    public static javax.swing.JButton btnAgr;
    private javax.swing.JButton btnCan;
    private javax.swing.JButton btnGra;
    private javax.swing.JButton btnMod;
    private javax.swing.JButton btnSal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tablaMateria;
    private javax.swing.JTextField txtApe;
    private javax.swing.JFormattedTextField txtCel;
    private javax.swing.JTextField txtCod;
    private javax.swing.JTextField txtNom;
    private javax.swing.JFormattedTextField txtTel;
    // End of variables declaration//GEN-END:variables
}
