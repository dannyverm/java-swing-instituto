/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import clases.Conexion;
import clases.fotoBD;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrador
 */
public class FrmPrestamoLibro extends javax.swing.JDialog implements Runnable {

    private DefaultListModel modelo = new DefaultListModel();
    private ResultSet rs;
    private ResultSet rscboProf = null;
    private ResultSet rscboCiu = null;
    private ResultSet rscboTipCon = null;
    private ResultSet rscboCar = null;
    private ResultSet rscboDep = null;
    private Conexion con;
    private String[] sindicatos;
    private String[] beneficios;
    private float tran = 0.80f;
    private ArrayList arNom;
    private ArrayList arApe;
    static JDialog dialogo;
    static String texto = "";
    private String codCarrera = null;
    private String codCiudad = null;
    private String codAlumno = null;
    private String codTurno = null;
    private boolean botonagregar;
    private ResultSet rscboCol;
    private String codColegio;
    static boolean foto;
    private boolean botonmodificar;
    private ResultSet rscboTur;
    private boolean cancelar;
    private Thread hiloClic;

    /**
     * Creates new form FrmPrestamoLibro
     */
    public FrmPrestamoLibro(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        arNom = new ArrayList();
        arApe = new ArrayList();
        centrar();ActualizarGrilla();
    }

    private void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), pantalla.height / 2 - (frame.height / 2));

    }

    private void conexion() {
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
    }
    
     private void vaciarTabla() {
        int i = 0;

        while (i != tabla1.getRowCount()) {
            tabla1.setValueAt("", i, 0);
            tabla1.setValueAt("", i, 1);
            i++;
        }
    }
    
    private void ActualizarGrilla() {
        String Nombre = null;
        try {
            conexion();
            int c = 0;
            vaciarTabla();
            String sql = "SELECT *"
                    + " FROM libro"
                    + " ORDER BY lib_des";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            if (rs.next() == true) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (tabla1.getRowCount() <= rs.getRow() - 1) {
                        DefaultTableModel tm = (DefaultTableModel) tabla1.getModel();
                        tm.addRow(new Object[]{new String(""), new String("")});
                        tabla1.setModel(tm);
                    }
                    Nombre = rs.getString("lib_des");
                    tabla1.setValueAt(Nombre, c, 0);
                    tabla1.setValueAt(rs.getString(4), c, 1);
                    c++;
                }
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FrmPrestamoLibro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarLista(String where) {
        try {
            conexion();
            String sql = "SELECT alu_nom,alu_ape FROM alumno "
                    + "WHERE estado='s' AND " + where;
            //System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            //System.out.println(rs.getString(1));
            modelo.clear();
            lista.setModel(modelo);
            rs.beforeFirst();
            int f = 0;
            while (rs.next()) {
                arNom.add(f, rs.getString("alu_nom"));
                arApe.add(f, rs.getString("alu_ape"));

                modelo.addElement(rs.getString("alu_nom") + " " + rs.getString("alu_ape"));
                lista.setModel(modelo);
                f++;
            }
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FrmPrestamoLibro.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void limpiar() {
        /*txtape1.setText("");
         txtcar.setText("");
         txtci1.setText("");
         txtmonto.setText("");
         txtnom1.setText("");
         txtsaldo.setText("");
         txtturno.setText("");
         txtfac.setText("");
         cbomes.setSelectedIndex(0);*/
    }

    public void mostrarDatos() {
        try {
            conexion();
            //muestra datos de identificacion del Alumno
            String sqlpersona = "SELECT * FROM alumno WHERE alu_cod=" + codAlumno
                    + " AND estado='s'";
            System.out.println(sqlpersona);
            rs = con.Consulta(sqlpersona);
            rs.next();
            txtnom1.setText(rs.getString("alu_nom"));
            txtape1.setText(rs.getString("alu_ape"));
            txtci1.setText(rs.getString("alu_ci"));
            //txtfec.setDate(new Date())
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FrmCuota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clicEnLista() {
        try {
            int fila = 0;
            while (fila <= arNom.size()) {

                if (lista.getSelectedValue().equals(arNom.get(fila) + " " + arApe.get(fila))) {
                    break;
                }
                fila++;
            }
                    StringTokenizer token = new StringTokenizer(String.valueOf(lista.getSelectedValue()), " ");
                    String sql = "SELECT alu_cod FROM alumno WHERE alu_nom='"
                            + arNom.get(fila) + "' AND alu_ape='"
                            + arApe.get(fila) + "' AND estado='s'";
                    //System.out.println(sql);
                    conexion();
                    rs = con.Consulta(sql);
                    rs.next();
                    codAlumno = rs.getString("alu_cod");
                    con.CerrarConexion();
            limpiar();
            mostrarDatos();

            hiloClic = null;
        } catch (SQLException ex) {
            Logger.getLogger(FrmPrestamoLibro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        clicEnLista();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnsal = new javax.swing.JButton();
        btncan = new javax.swing.JButton();
        btngra = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtci1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtape1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtnom1 = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaDetalle = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        txtnom = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtape = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lista = new javax.swing.JList();
        txtci = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnnue1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabla1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestion de Libros");

        btnsal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/salirPuerta.png"))); // NOI18N
        btnsal.setToolTipText("Salir");
        btnsal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalActionPerformed(evt);
            }
        });

        btncan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/cancelar.png"))); // NOI18N
        btncan.setToolTipText("Cancelar");
        btncan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncanActionPerformed(evt);
            }
        });

        btngra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/Grabar.jpg"))); // NOI18N
        btngra.setToolTipText("Cancelar");
        btngra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngraActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Detalle de Prestamo"));

        txtci1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtci1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtci1ActionPerformed(evt);
            }
        });

        jLabel4.setText("C.I. Nro");

        jLabel5.setText("Apellidos");

        txtape1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtape1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtape1ActionPerformed(evt);
            }
        });
        txtape1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtape1KeyPressed(evt);
            }
        });

        jLabel6.setText("Nombre");

        txtnom1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtnom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnom1ActionPerformed(evt);
            }
        });
        txtnom1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtnom1KeyPressed(evt);
            }
        });

        tablaDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Cod.", "Libro", "Cantidad"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tablaDetalle);
        tablaDetalle.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaDetalle.getColumnModel().getColumn(0).setMaxWidth(80);
        tablaDetalle.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaDetalle.getColumnModel().getColumn(2).setMaxWidth(90);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(7, 7, 7)
                        .addComponent(txtci1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addGap(4, 4, 4)
                        .addComponent(txtape1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtnom1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel4))
                    .addComponent(txtci1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel5))
                    .addComponent(txtape1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtnom1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Busqueda"));

        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtnom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtnom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnomActionPerformed(evt);
            }
        });
        txtnom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtnomKeyPressed(evt);
            }
        });

        jLabel2.setText("Apellidos");

        txtape.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtape.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtapeActionPerformed(evt);
            }
        });
        txtape.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtapeKeyPressed(evt);
            }
        });

        jLabel1.setText("C.I. Nro");

        lista.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lista);

        txtci.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtciActionPerformed(evt);
            }
        });

        jLabel3.setText("Nombre");

        btnnue1.setText("Mostrar Todo");
        btnnue1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnue1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3)
                        .addGap(4, 4, 4)
                        .addComponent(txtnom, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addGap(4, 4, 4)
                        .addComponent(txtape, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addGap(7, 7, 7)
                        .addComponent(txtci, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnnue1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtci, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnnue1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addComponent(txtape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(txtnom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabla1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Libro", "Cant. Disponible"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tabla1);
        tabla1.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabla1.getColumnModel().getColumn(1).setMaxWidth(90);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(263, 263, 263)
                        .addComponent(btngra, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(btncan, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(231, 231, 231)
                        .addComponent(btnsal, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btncan, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnsal)
                    .addComponent(btngra, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtnomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnomActionPerformed
    }//GEN-LAST:event_txtnomActionPerformed

    private void txtnomKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnomKeyPressed
        if (!txtnom.getText().isEmpty()) {
            cargarLista("alu_nom LIKE '" + txtnom.getText() + "%'");
        } else {
            modelo.clear();
        }
    }//GEN-LAST:event_txtnomKeyPressed

    private void txtapeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtapeActionPerformed
        if (txtape.getText().isEmpty()) {
            txtnom.grabFocus();
        }
    }//GEN-LAST:event_txtapeActionPerformed

    private void txtapeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtapeKeyPressed
        if (!txtape.getText().isEmpty()) {
            cargarLista("alu_ape LIKE '" + txtape.getText() + "%'");
        } else {
            modelo.clear();
        }
    }//GEN-LAST:event_txtapeKeyPressed

    private void listaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listaMouseClicked
        if (hiloClic == null) {
            hiloClic = new Thread(this);
            hiloClic.start();
        }
    }//GEN-LAST:event_listaMouseClicked

    private void txtciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtciActionPerformed
        if (txtci.getText().isEmpty()) {
            txtape.grabFocus();
        } else {
            cargarLista("alu_ci='" + txtci.getText() + "'");
        }
    }//GEN-LAST:event_txtciActionPerformed

    private void btnnue1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnue1ActionPerformed
        cargarLista("alu_cod>-1");
    }//GEN-LAST:event_btnnue1ActionPerformed

    private void txtci1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtci1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtci1ActionPerformed

    private void txtape1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtape1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtape1ActionPerformed

    private void txtape1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtape1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtape1KeyPressed

    private void txtnom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnom1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnom1ActionPerformed

    private void txtnom1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnom1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnom1KeyPressed

    private void btnsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnsalActionPerformed

    private void btncanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncanActionPerformed
    }//GEN-LAST:event_btncanActionPerformed

    private void btngraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngraActionPerformed

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
            java.util.logging.Logger.getLogger(FrmPrestamoLibro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmPrestamoLibro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmPrestamoLibro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmPrestamoLibro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmPrestamoLibro dialog = new FrmPrestamoLibro(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btncan;
    private javax.swing.JButton btngra;
    public static javax.swing.JButton btnnue1;
    private javax.swing.JButton btnsal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList lista;
    private javax.swing.JTable tabla1;
    private javax.swing.JTable tablaDetalle;
    private javax.swing.JTextField txtape;
    private javax.swing.JTextField txtape1;
    public static javax.swing.JTextField txtci;
    public static javax.swing.JTextField txtci1;
    private javax.swing.JTextField txtnom;
    private javax.swing.JTextField txtnom1;
    // End of variables declaration//GEN-END:variables
}
