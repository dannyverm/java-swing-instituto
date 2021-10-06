/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanAlumno.java
 *
 * Created on 30-may-2012, 8:38:07
 */
package PaqFormulario;

import clases.Conexion;
import clases.ImagePreview;
import clases.fotoBD;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author JulFX
 */
public class PanAlumno extends javax.swing.JPanel {

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
    private String codTurno= null;
    private boolean botonagregar;
    private ResultSet rscboCol;
    private String codColegio;
    static boolean foto;
    private boolean botonmodificar;
    private ResultSet rscboTur;


    /**
     * Creates new form PanAlumno
     */
    public PanAlumno() {
        initComponents();
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        cargaCboCiu();
        cargaCboCar();
        cargaCboCol();
        cargaCboTur();
        botonEnter();
        arNom = new ArrayList();
        arApe = new ArrayList();
    }

    private void botonEnter() {
        InputMap map = new InputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        btnAgrCar.setInputMap(0, map);
        btnAgrCiu.setInputMap(0, map);
        btnAgrCol.setInputMap(0, map);
        btncan.setInputMap(0, map);
        btngra.setInputMap(0, map);
        btnmod.setInputMap(0, map);
        btnnue.setInputMap(0, map);
        btnsal.setInputMap(0, map);
        btnbusFot.setInputMap(0, map);
        btncapFot.setInputMap(0, map);
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

    private void cargarLista(String where) {
        try {
            String sql = "SELECT alu_nom,alu_ape FROM alumno "
                    + "WHERE estado='s' AND " + where;
            //System.out.println(sql);
            rs = con.Consulta(sql);
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
        } catch (SQLException ex) {
            Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void capturarFoto() {
        DialogoCapturarFoto d = new DialogoCapturarFoto(null, true);
        dialogo = d;
        dialogo.setVisible(true);

        if (foto == true) {
            // Crea un icono que referencie a la imagen en disco
            ImageIcon ima = new ImageIcon("src\\paqImagenes\\foto.JPG");

            int ancho = lbl.getWidth(); // ancho en pixeles que tendra el icono escalado
            int alto = -1; // alto (para que conserve la proporcion pasamos -1)

            // Obtiene un icono en escala con las dimensiones especificadas
            ImageIcon iconoEscalado = new ImageIcon(ima.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_DEFAULT));
            txtara.setEnabled(true);
            txtara.grabFocus();
            lbl.setIcon(iconoEscalado);
        }
    }

    private void buscarFoto() {
        try {
            File file = null;
            JFileChooser cho = new JFileChooser();
            cho.setAccessory(new ImagePreview(cho));
            FileNameExtensionFilter filtroImagen = new FileNameExtensionFilter("JPG, PNG & GIF", "jpg", "png", "gif");
            cho.setFileFilter(filtroImagen);
            int res = cho.showOpenDialog(this);

            if (res == JFileChooser.APPROVE_OPTION) {
                file = cho.getSelectedFile();


                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream("src\\paqImagenes\\foto.JPG");
                FileChannel canalFuente = fis.getChannel();
                FileChannel canalDestino = fos.getChannel();
                canalFuente.transferTo(0, canalFuente.size(), canalDestino);
                fis.close();
                fos.close();

                // Crea un icono que referencie a la imagen en disco
                ImageIcon ima = new ImageIcon("src\\paqImagenes\\foto.JPG");


                int alto = -1; // alto (para que conserve la proporcion pasamos -1)

                // Obtiene un icono en escala con las dimensiones especificadas
                ImageIcon iconoEscalado = new ImageIcon(ima.getImage().getScaledInstance(lbl.getWidth(), alto, java.awt.Image.SCALE_DEFAULT));
                lbl.setIcon(iconoEscalado);
                txtara.setEnabled(true);
                txtara.grabFocus();
                foto = true;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException n) {
            System.out.println(n.getMessage());
        }
    }

    private String convertirFecha(String txt) {

        String f = null;
        if (txt.length() == 10) {
            f = txt.substring(6, 10) + txt.substring(3, 5) + txt.substring(0, 2);
        }

        return f;
    }

    private boolean validarHora(String hora) {
        boolean valor = false;
        int h = Integer.parseInt(hora.substring(0, 2));
        int m = Integer.parseInt(hora.substring(3, 5));
        if (h > 24 || m > 24 || h < 0 || m < 0) {
            valor = false;
        } else {
            valor = true;
        }
        return valor;
    }

    private String codigoCombo(String sql, String nombreColumTabla) {
        String codigoBD = null;
        try {
            rs = con.Consulta(sql);
            rs.next();
            codigoBD = rs.getString(nombreColumTabla);

        } catch (SQLException ex) {
            Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigoBD;
    }

    private void cargaCboCiu() {
        try {
            String sql = "SELECT * FROM ciudad ORDER BY ciu_des";
            rscboCiu = con.Consulta(sql);
            cbociu.removeAllItems();
            cbociu.addItem("");
            while (rscboCiu.next()) {
                try {
                    cbociu.addItem(rscboCiu.getString("ciu_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboCar() {
        try {
            String sql = "SELECT * FROM carrera ORDER BY car_des";
            rscboCar = con.Consulta(sql);
            cbocar.removeAllItems();
            cbocar.addItem("");
            while (rscboCar.next()) {
                try {
                    cbocar.addItem(rscboCar.getString("car_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboCol() {
        try {
            String sql = "SELECT * FROM colegio ORDER BY col_des";
            rscboCol = con.Consulta(sql);
            cbocol.removeAllItems();
            cbocol.addItem("");
            while (rscboCol.next()) {
                try {
                    cbocol.addItem(rscboCol.getString("col_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargaCboTur() {
        try {
            String sql = "SELECT * FROM turno ORDER BY tur_des";
            rscboTur = con.Consulta(sql);
            cbotur.removeAllItems();
            cbotur.addItem("");
            while (rscboTur.next()) {
                try {
                    cbotur.addItem(rscboTur.getString("tur_des"));
                } catch (SQLException ex) {
                    Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mostrarDatos() {
        try {
            //muestra datos de identificacion del funcionario
            String sqlpersona = "SELECT * FROM alumno WHERE alu_cod=" + codAlumno
                    + " AND estado='s'";
            //System.out.println(sqlpersona);
            rs = con.Consulta(sqlpersona);
            rs.next();
            txtnom1.setText(rs.getString("alu_nom"));
            txtape1.setText(rs.getString("alu_ape"));
            txtci1.setText(rs.getString("alu_ci"));
            txtfecNac.setDate(rs.getDate("alu_fec_nac"));
            txtema.setText(rs.getString("alu_ema"));
            txtdir.setText(rs.getString("alu_dir"));
            txtara.setText(rs.getString("pago"));
            txtbar.setText(rs.getString("alu_bar"));
            cboven.setSelectedItem(rs.getString("pago_ven"));
            txtnrotel.setText(rs.getString("alu_tel"));
            txtnrocel.setText(rs.getString("alu_cel"));
            cboniv.setSelectedItem(rs.getString("nivel"));
            //------------------------------------------------

            //mostrar ciudad
            String sqlciu = "SELECT c.ciu_des FROM ciudad c,alumno a WHERE "
                    + "a.ciu_cod=c.ciu_cod AND a.alu_cod=" + codAlumno;

            rs = con.Consulta(sqlciu);
            if (rs.next()) {
                cbociu.setSelectedItem(rs.getString("c.ciu_des"));
            }

            //mostrar colegio
            String sqlcolegio = "SELECT c.col_des FROM colegio c,alumno a WHERE "
                    + "a.col_cod=c.col_cod AND a.alu_cod=" + codAlumno;
            rs = con.Consulta(sqlcolegio);
            if (rs.next()) {
                cbocol.setSelectedItem(rs.getString("c.col_des"));
            }

            //mostrar carrera
            String sqlcarrera = "SELECT c.car_des FROM carrera c,alumno a WHERE "
                    + "a.car_cod=c.car_cod AND a.alu_cod=" + codAlumno;
            //System.out.println(sqlcarrera);
            rs = con.Consulta(sqlcarrera);
            if (rs.next()) {
                cbocar.setSelectedItem(rs.getString("c.car_des"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtnom1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtape1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtfecNac = new com.toedter.calendar.JDateChooser();
        txtci1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtnrocel = new javax.swing.JFormattedTextField();
        txtnrotel = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtdir = new javax.swing.JTextField();
        txtbar = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        cbociu = new javax.swing.JComboBox();
        txtema = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        btngra = new javax.swing.JButton();
        btnmod = new javax.swing.JButton();
        btncan = new javax.swing.JButton();
        btnsal = new javax.swing.JButton();
        btnnue = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        cbocol = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        cbocar = new javax.swing.JComboBox();
        btnAgrCar = new javax.swing.JButton();
        btnAgrCol = new javax.swing.JButton();
        btnAgrCiu = new javax.swing.JButton();
        btncapFot = new javax.swing.JButton();
        btnbusFot = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtara = new javax.swing.JFormattedTextField();
        cboven = new javax.swing.JComboBox();
        btnbaja = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cboniv = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        cbotur = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        txtnom = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtape = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lista = new javax.swing.JList();
        lbl = new javax.swing.JLabel();
        txtci = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnnue1 = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(960, 600));
        setMinimumSize(new java.awt.Dimension(960, 600));

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setText("Nombres");

        txtnom1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtnom1.setEnabled(false);
        txtnom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnom1ActionPerformed(evt);
            }
        });

        jLabel5.setText("Apellidos");

        txtape1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtape1.setEnabled(false);
        txtape1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtape1ActionPerformed(evt);
            }
        });

        jLabel6.setText("Fecha de nacimiento");

        txtfecNac.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtfecNac.setEnabled(false);
        txtfecNac.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtfecNacKeyPressed(evt);
            }
        });

        txtci1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtci1.setEnabled(false);
        txtci1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtci1ActionPerformed(evt);
            }
        });
        txtci1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtci1KeyPressed(evt);
            }
        });

        jLabel7.setText("C.I. Nro.");

        txtnrocel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        try {
            txtnrocel.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(####) ### - ###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtnrocel.setEnabled(false);
        txtnrocel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnrocelActionPerformed(evt);
            }
        });
        txtnrocel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtnrocelKeyPressed(evt);
            }
        });

        txtnrotel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtnrotel.setEnabled(false);
        txtnrotel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnrotelActionPerformed(evt);
            }
        });
        txtnrotel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtnrotelKeyPressed(evt);
            }
        });

        jLabel11.setText("Nro. Celular");

        jLabel10.setText("Nro. De Tel.");

        jLabel12.setText("Dirección");

        txtdir.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtdir.setEnabled(false);
        txtdir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdirActionPerformed(evt);
            }
        });

        txtbar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtbar.setEnabled(false);
        txtbar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtbarActionPerformed(evt);
            }
        });

        jLabel14.setText("Barrio");

        jLabel16.setText("Ciudad");

        cbociu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbociu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbociu.setEnabled(false);
        cbociu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbociuActionPerformed(evt);
            }
        });
        cbociu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbociuKeyPressed(evt);
            }
        });

        txtema.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtema.setEnabled(false);
        txtema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtemaActionPerformed(evt);
            }
        });

        jLabel15.setText("E-Mail");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 153, 0));
        jLabel13.setText("DATOS DE IDENTIFICACIÓN DEL ALUMNO");

        jTabbedPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 0), 2, true));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 567, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 175, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Datos Academicos", jPanel2);

        btngra.setText("Grabar");
        btngra.setEnabled(false);
        btngra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngraActionPerformed(evt);
            }
        });

        btnmod.setText("Modificar");
        btnmod.setEnabled(false);
        btnmod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmodActionPerformed(evt);
            }
        });

        btncan.setText("Cancelar");
        btncan.setEnabled(false);
        btncan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncanActionPerformed(evt);
            }
        });

        btnsal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/cerrar.png"))); // NOI18N
        btnsal.setToolTipText("Salir");
        btnsal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalActionPerformed(evt);
            }
        });

        btnnue.setText("Nuevo");
        btnnue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnueActionPerformed(evt);
            }
        });

        jLabel18.setText("Procede del Colegio");

        cbocol.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbocol.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbocol.setEnabled(false);
        cbocol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbocolActionPerformed(evt);
            }
        });
        cbocol.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbocolKeyPressed(evt);
            }
        });

        jLabel19.setText("Carrera");

        cbocar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbocar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbocar.setEnabled(false);
        cbocar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbocarActionPerformed(evt);
            }
        });
        cbocar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbocarKeyPressed(evt);
            }
        });

        btnAgrCar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/iconoAgregar.gif"))); // NOI18N
        btnAgrCar.setToolTipText("Agregar profesión");
        btnAgrCar.setEnabled(false);
        btnAgrCar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgrCarActionPerformed(evt);
            }
        });

        btnAgrCol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/iconoAgregar.gif"))); // NOI18N
        btnAgrCol.setToolTipText("Agregar profesión");
        btnAgrCol.setEnabled(false);
        btnAgrCol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgrColActionPerformed(evt);
            }
        });

        btnAgrCiu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/iconoAgregar.gif"))); // NOI18N
        btnAgrCiu.setToolTipText("Agregar profesión");
        btnAgrCiu.setEnabled(false);
        btnAgrCiu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgrCiuActionPerformed(evt);
            }
        });

        btncapFot.setText("Capturar Foto");
        btncapFot.setEnabled(false);
        btncapFot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncapFotActionPerformed(evt);
            }
        });
        btncapFot.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btncapFotKeyPressed(evt);
            }
        });

        btnbusFot.setText("Buscar Foto");
        btnbusFot.setEnabled(false);
        btnbusFot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbusFotActionPerformed(evt);
            }
        });
        btnbusFot.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnbusFotKeyPressed(evt);
            }
        });

        jLabel20.setText("Arancel");

        jLabel8.setText("Vencimiento al");

        txtara.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtara.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        txtara.setEnabled(false);
        txtara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtaraActionPerformed(evt);
            }
        });
        txtara.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtaraKeyPressed(evt);
            }
        });

        cboven.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        cboven.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cboven.setEnabled(false);
        cboven.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbovenKeyPressed(evt);
            }
        });

        btnbaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/Pulgar-abajo.jpg"))); // NOI18N
        btnbaja.setToolTipText("Baja de alumno");
        btnbaja.setEnabled(false);
        btnbaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbajaActionPerformed(evt);
            }
        });

        jLabel9.setText("Nivel de alumno/a");

        cboniv.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Universitario", "Secundaria" }));
        cboniv.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cboniv.setEnabled(false);
        cboniv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbonivKeyPressed(evt);
            }
        });

        jLabel17.setText("Turno");

        cbotur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbotur.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbotur.setEnabled(false);
        cbotur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboturActionPerformed(evt);
            }
        });
        cbotur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cboturKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(txtnom1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel5)
                        .addGap(4, 4, 4)
                        .addComponent(txtape1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(jLabel13))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7)
                        .addGap(4, 4, 4)
                        .addComponent(txtci1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addGap(4, 4, 4)
                        .addComponent(txtfecNac, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(4, 4, 4)
                                .addComponent(txtdir, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(4, 4, 4)
                                .addComponent(txtbar, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(4, 4, 4)
                                .addComponent(cbociu, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgrCiu, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtnrotel, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel11)
                                .addGap(4, 4, 4)
                                .addComponent(txtnrocel, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtema)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbocol, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgrCol, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbocar, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgrCar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnbusFot)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btncapFot))
                            .addComponent(btnsal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtara, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboven, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboniv, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(btnbaja, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(109, 109, 109)
                        .addComponent(btnnue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnmod)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btngra)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btncan))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel17)
                        .addGap(4, 4, 4)
                        .addComponent(cbotur, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtnom1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtape1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(txtci1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtfecNac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel14))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnAgrCiu)
                    .addComponent(jLabel16)
                    .addComponent(cbociu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtnrotel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtnrocel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(txtema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(cbocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgrCol))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnbusFot)
                        .addComponent(btncapFot))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(cbocar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19)
                        .addComponent(btnAgrCar)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel20)
                            .addComponent(jLabel8)
                            .addComponent(txtara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboven, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(cboniv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel17)
                            .addComponent(cbotur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btnbaja, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnnue)
                            .addComponent(btnmod)
                            .addComponent(btngra)
                            .addComponent(btncan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnsal, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13))
        );

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

        lbl.setFont(new java.awt.Font("DigifaceWide", 1, 36)); // NOI18N
        lbl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 0), 3, true));

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
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtci, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnnue1))))
                .addGap(11, 11, 11)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addComponent(txtape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(txtnom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleParent(this);
    }// </editor-fold>//GEN-END:initComponents

    private void txtciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtciActionPerformed
        if (txtci.getText().isEmpty()) {
            txtape.grabFocus();
        } else {
            cargarLista("per_ci='" + txtci.getText() + "'");
        }
    }//GEN-LAST:event_txtciActionPerformed

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

    private void txtnomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnomActionPerformed
    }//GEN-LAST:event_txtnomActionPerformed

    private void listaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listaMouseClicked
        int fila = 0;
        btncan.setEnabled(true);
        btnbaja.setEnabled(true);
        btnmod.setEnabled(true);
        btnmod.grabFocus();
        btnnue.setEnabled(false);
        while (fila <= arNom.size()) {

            if (lista.getSelectedValue().equals(arNom.get(fila) + " " + arApe.get(fila))) {
                break;
            }
            fila++;
        }
        try {
            try {
                StringTokenizer token = new StringTokenizer(String.valueOf(lista.getSelectedValue()), " ");
                String sql = "SELECT alu_cod FROM alumno WHERE alu_nom='"
                        + arNom.get(fila) + "' AND alu_ape='"
                        + arApe.get(fila) + "' AND estado='s'";
                //System.out.println(sql);
                rs = con.Consulta(sql);
                rs.next();
                codAlumno = rs.getString("alu_cod");
                fotoBD f = new fotoBD(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
                ImageIcon ima = new ImageIcon(f.getfoto(rs.getString("alu_cod")));
                ImageIcon iconoEscalado = new ImageIcon(ima.getImage().getScaledInstance(lbl.getWidth(), -1, java.awt.Image.SCALE_DEFAULT));
                lbl.setIcon(iconoEscalado);
            } catch (SQLException ex) {
                Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NullPointerException n) {
            ImageIcon im = new ImageIcon("src\\paqImagenes\\personas.png");
            lbl.setIcon(im);
        }
        //Muestra datos de la Persona
        mostrarDatos();
    }//GEN-LAST:event_listaMouseClicked

    private void txtnomKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnomKeyPressed
        if (!txtnom.getText().isEmpty()) {
            cargarLista("alu_nom LIKE '" + txtnom.getText() + "%'");
        } else {
            modelo.clear();
        }

    }//GEN-LAST:event_txtnomKeyPressed

    private void txtnom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnom1ActionPerformed
        if (txtnom1.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "El campo ''Apellidos'' no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
            txtnom1.grabFocus();
        } else {
            txtnom1.setText(txtnom1.getText().toUpperCase());
            txtape1.setEnabled(true);
            txtape1.grabFocus();
        }
}//GEN-LAST:event_txtnom1ActionPerformed

    private void txtape1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtape1ActionPerformed
        if (txtape1.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "El campo ''Nombre'' no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
            txtape1.grabFocus();
        } else {
            txtape1.setText(txtape1.getText().toUpperCase());
            txtci1.setEnabled(true);
            txtci1.grabFocus();
        }
}//GEN-LAST:event_txtape1ActionPerformed

    private void txtfecNacKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtfecNacKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB) {
            if (txtfecNac.getDate() == null) {
                JOptionPane.showMessageDialog(null, "El campo ''Fecha'' no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                txtdir.setEnabled(true);
                txtdir.grabFocus();
            }
        }
}//GEN-LAST:event_txtfecNacKeyPressed

    private void txtci1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtci1ActionPerformed
        if (txtci1.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "El campo ''C.I. Nro'' no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
            txtci1.grabFocus();
        } else {
            txtfecNac.setEnabled(true);
            txtfecNac.grabFocus();
        }
}//GEN-LAST:event_txtci1ActionPerformed

    private void txtci1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtci1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txtci1.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "El campo ''C.I. Nro'' no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
                txtci1.grabFocus();
            } else {
                txtfecNac.setEnabled(true);
                txtfecNac.grabFocus();
            }
        }

        String keypres = KeyEvent.getKeyText(evt.getKeyCode());

        if (!(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE
                || evt.getKeyCode() == KeyEvent.VK_DELETE
                || evt.getKeyCode() == KeyEvent.VK_ENTER
                || FrmPrincipal.isNumeric(String.valueOf(evt.getKeyChar())) == true)) {
            try {
                Integer.parseInt(KeyEvent.getKeyText(evt.getKeyCode()));
                System.out.println(KeyEvent.getKeyText(evt.getKeyCode()));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Solo puede Ingresar Numeros", "Error", JOptionPane.ERROR_MESSAGE);
                txtci1.setText(txtci1.getText().replace(keypres.toLowerCase(), ""));
                txtci1.setText(txtci1.getText().replace(keypres.toUpperCase(), ""));
                txtci1.grabFocus();
            }
        }
    }//GEN-LAST:event_txtci1KeyPressed

    private void txtnrocelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnrocelActionPerformed
        txtema.setEnabled(true);
        txtema.grabFocus();
}//GEN-LAST:event_txtnrocelActionPerformed

    private void txtnrocelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnrocelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtema.setEnabled(true);
            txtema.grabFocus();
        } else {
            String keypres = KeyEvent.getKeyText(evt.getKeyCode());
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                txtema.setEnabled(true);
                txtema.grabFocus();
            } else {
                if (!(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE
                        || evt.getKeyCode() == KeyEvent.VK_DELETE
                        || evt.getKeyCode() == KeyEvent.VK_ENTER
                        || FrmPrincipal.isNumeric(String.valueOf(evt.getKeyChar())) == true)) {
                    try {
                        Integer.parseInt(KeyEvent.getKeyText(evt.getKeyCode()));
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Solo puede Ingresar Numeros", "Error", JOptionPane.ERROR_MESSAGE);
                        txtnrocel.setText(txtnrocel.getText().replace(keypres.toLowerCase(), ""));
                        txtnrocel.setText(txtnrocel.getText().replace(keypres.toUpperCase(), ""));
                        txtnrocel.grabFocus();
                    }
                }
            }
        }
}//GEN-LAST:event_txtnrocelKeyPressed

    private void txtnrotelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnrotelActionPerformed
        txtnrocel.setEnabled(true);
        txtnrocel.grabFocus();
}//GEN-LAST:event_txtnrotelActionPerformed

    private void txtnrotelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnrotelKeyPressed

        String keypres = KeyEvent.getKeyText(evt.getKeyCode());

        if (!(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE
                || evt.getKeyCode() == KeyEvent.VK_DELETE
                || evt.getKeyCode() == KeyEvent.VK_ENTER
                || FrmPrincipal.isNumeric(String.valueOf(evt.getKeyChar())) == true)) {
            try {
                Integer.parseInt(KeyEvent.getKeyText(evt.getKeyCode()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Solo puede Ingresar Numeros", "Error", JOptionPane.ERROR_MESSAGE);
                txtnrotel.setText(txtnrotel.getText().replace(keypres.toLowerCase(), ""));
                txtnrotel.setText(txtnrotel.getText().replace(keypres.toUpperCase(), ""));
                txtnrotel.grabFocus();
            }

        }
}//GEN-LAST:event_txtnrotelKeyPressed

    private void txtdirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdirActionPerformed
        txtdir.setText(txtdir.getText().toUpperCase());
        txtbar.setEnabled(true);
        txtbar.grabFocus();
}//GEN-LAST:event_txtdirActionPerformed

    private void txtbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtbarActionPerformed
        txtbar.setText(txtbar.getText().toUpperCase());
        btnAgrCiu.setEnabled(true);
        cbociu.setEnabled(true);
        cbociu.grabFocus();
        //btnAgrCiu.setEnabled(true);
}//GEN-LAST:event_txtbarActionPerformed

    private void cbociuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbociuActionPerformed
}//GEN-LAST:event_cbociuActionPerformed

    private void cbociuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbociuKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbociu.getSelectedIndex() != 0) {
                String sql = "Select * From ciudad Where ciu_des='" + cbociu.getSelectedItem() + "'";
                codCiudad = codigoCombo(sql, "ciu_cod");
            }
            txtnrotel.setEnabled(true);
            txtnrotel.grabFocus();
        }
}//GEN-LAST:event_cbociuKeyPressed

    private void txtemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtemaActionPerformed
        btnAgrCol.setEnabled(true);
        cbocol.setEnabled(true);
        cbocol.grabFocus();
}//GEN-LAST:event_txtemaActionPerformed

    private void btnsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalActionPerformed
        //FrmPrincipal.CambiarPanel();
        //FrmAlumno f=new FrmAlumno(null, true);
        //f.dispose();
        
      
}//GEN-LAST:event_btnsalActionPerformed

    private void btncanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncanActionPerformed
        txtape1.setText("");
        txtape1.setEnabled(false);
        txtara.setText("");
        txtara.setEnabled(false);
        txtbar.setText("");
        txtbar.setEnabled(false);
        txtci1.setText("");
        txtci1.setEnabled(false);
        txtdir.setText("");
        txtdir.setEnabled(false);
        txtema.setText("");
        txtema.setEnabled(false);
        txtfecNac.setDate(null);
        txtfecNac.setEnabled(false);
        txtnom1.setText("");
        txtnom1.setEnabled(false);
        txtnrocel.setText("");
        txtnrocel.setEnabled(false);
        txtnrotel.setText("");
        txtnrotel.setEnabled(false);
        cbocar.setSelectedIndex(0);
        cbocar.setEnabled(false);
        cbociu.setSelectedIndex(0);
        cbociu.setEnabled(false);
        cbocol.setSelectedIndex(0);
        cbocol.setEnabled(false);
        cboven.setSelectedIndex(0);
        cboven.setEnabled(false);
        cboniv.setSelectedIndex(0);
        cboniv.setEnabled(false);
        cbotur.setSelectedIndex(0);
        cbotur.setEnabled(false);
        txtape.setText("");
        txtnom.setText("");
        txtci.setText("");
        texto = "";
        codCarrera = "";
        codCiudad = "";
        codAlumno = "";
        foto = false;
        lbl.setIcon(null);
        botonagregar = false;
        botonmodificar = false;
        btnbaja.setEnabled(false);
        btnnue.setEnabled(true);
        btnnue.grabFocus();
        btnmod.setEnabled(false);
        btngra.setEnabled(false);
        btncan.setEnabled(false);
        btnbusFot.setEnabled(false);
        btncapFot.setEnabled(false);
        btnAgrCar.setEnabled(false);
        btnAgrCiu.setEnabled(false);
        btnAgrCol.setEnabled(false);
        modelo.clear();
        lista.setModel(modelo);
    }//GEN-LAST:event_btncanActionPerformed

    private void cbocolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbocolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbocolActionPerformed

    private void cbocolKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbocolKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbocol.getSelectedIndex() != 0) {
                String sql = "Select * From colegio Where col_des='" + cbocol.getSelectedItem() + "'";
                codColegio = codigoCombo(sql, "col_cod");
            }
            btnAgrCar.setEnabled(true);
            cbocar.setEnabled(true);
            cbocar.grabFocus();
        }
    }//GEN-LAST:event_cbocolKeyPressed

    private void cbocarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbocarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbocarActionPerformed

    private void cbocarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbocarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbocar.getSelectedIndex() != 0) {
                String sql = "Select * From carrera Where car_des='" + cbocar.getSelectedItem() + "'";
                codCarrera = codigoCombo(sql, "car_cod");
            }
            btnbusFot.setEnabled(true);
            btnbusFot.grabFocus();
            btncapFot.setEnabled(true);
            txtara.setEnabled(true);
        }
    }//GEN-LAST:event_cbocarKeyPressed

    private void btnAgrCarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrCarActionPerformed
        PanCarrera p = new PanCarrera();
        JDialog d = new JDialog(new JFrame(), true);
        d.setLayout(new CardLayout());
        d.setSize(p.getSize());
        d.add(p, BorderLayout.CENTER);
        d.pack();
        dialogo = d;
        PanCarrera.btnAgr.grabFocus();
        dialogo.setVisible(true);
        if (!texto.equals("")) {
            cargaCboCar();
            cbocar.setEnabled(true);
            cbocar.setSelectedItem(texto);
            if (cbocar.getSelectedIndex() != 0) {
                String sql = "Select * From carrera Where car_des='" + cbocar.getSelectedItem() + "'";
                codCarrera = codigoCombo(sql, "car_cod");
            }
            btnbusFot.setEnabled(true);
            btnbusFot.grabFocus();
            btncapFot.setEnabled(true);
            txtara.setEnabled(true);
            texto = "";
        }
}//GEN-LAST:event_btnAgrCarActionPerformed

    private void btnAgrColActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrColActionPerformed
        PanColegio p = new PanColegio();
        JDialog d = new JDialog(new JFrame(), true);
        d.setLayout(new CardLayout());
        d.setSize(p.getSize());
        d.add(p, BorderLayout.CENTER);
        d.pack();
        dialogo = d;
        PanColegio.btnAgr.grabFocus();
        dialogo.setVisible(true);
        if (!texto.equals("")) {
            cargaCboCol();
            cbocol.setEnabled(true);
            cbocol.setSelectedItem(texto);
            if (cbocol.getSelectedIndex() != 0) {
                String sql = "Select * From colegio Where col_des='" + cbocol.getSelectedItem() + "'";
                codColegio = codigoCombo(sql, "col_cod");
            }
            cbocar.setEnabled(true);
            cbocar.grabFocus();
            btnAgrCar.setEnabled(true);
            texto = "";
        }
    }//GEN-LAST:event_btnAgrColActionPerformed

    private void btnAgrCiuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrCiuActionPerformed
        PanCiudad p = new PanCiudad();
        JDialog d = new JDialog(new JFrame(), true);
        d.setLayout(new CardLayout());
        d.setSize(p.getSize());
        d.add(p, BorderLayout.CENTER);
        d.pack();
        dialogo = d;
        PanCiudad.btnAgr.grabFocus();
        dialogo.setVisible(true);
        if (!texto.equals("")) {
            cargaCboCiu();
            cbociu.setEnabled(true);
            cbociu.setSelectedItem(texto);
            if (cbociu.getSelectedIndex() != 0) {
                String sql = "Select * From ciudad Where ciu_des='" + cbociu.getSelectedItem() + "'";
                codCiudad = codigoCombo(sql, "ciu_cod");
            }
            txtnrotel.setEnabled(true);
            txtnrotel.grabFocus();
            texto = "";
        }
    }//GEN-LAST:event_btnAgrCiuActionPerformed

    private void btnbusFotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbusFotActionPerformed
        buscarFoto();

    }//GEN-LAST:event_btnbusFotActionPerformed

    private void btncapFotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncapFotActionPerformed
        capturarFoto();
    }//GEN-LAST:event_btncapFotActionPerformed

    private void btnnueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnueActionPerformed
        try {
            botonagregar = true;
            String sql = "Select ifnull(max(alu_cod),0)+1 codigo From alumno";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            codAlumno = rs.getString(1);
            btnnue.setEnabled(false);
            btnmod.setEnabled(false);
            btncan.setEnabled(true);
            txtnom1.setEnabled(true);
            txtnom1.grabFocus();
        } catch (SQLException ex) {
            Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnnueActionPerformed

    private void btnbusFotKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnbusFotKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            btncapFot.grabFocus();
        }
    }//GEN-LAST:event_btnbusFotKeyPressed

    private void btncapFotKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btncapFotKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            txtara.grabFocus();
        }
    }//GEN-LAST:event_btncapFotKeyPressed

    private void txtaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtaraActionPerformed
        cboven.setEnabled(true);
        cboven.grabFocus();
    }//GEN-LAST:event_txtaraActionPerformed

    private void txtaraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtaraKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cboven.setEnabled(true);
            cboven.grabFocus();
        } else {
            String keypres = KeyEvent.getKeyText(evt.getKeyCode());

            if (!(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE
                    || evt.getKeyCode() == KeyEvent.VK_DELETE
                    || evt.getKeyCode() == KeyEvent.VK_ENTER
                    || FrmPrincipal.isNumeric(String.valueOf(evt.getKeyChar())) == true)) {
                try {
                    Integer.parseInt(KeyEvent.getKeyText(evt.getKeyCode()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Solo puede Ingresar Numeros", "Error", JOptionPane.ERROR_MESSAGE);
                    txtara.setText(txtara.getText().replace(keypres.toLowerCase(), ""));
                    txtara.setText(txtara.getText().replace(keypres.toUpperCase(), ""));
                    txtara.grabFocus();
                }
            }
        }
    }//GEN-LAST:event_txtaraKeyPressed

    private void btngraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngraActionPerformed
        int res = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);
        SimpleDateFormat dat = new SimpleDateFormat("yyyyMMdd");
        if (res == JOptionPane.OK_OPTION) {
            if (botonagregar == true) {

                Date d = new Date();

                String sql = "INSERT INTO alumno(alu_cod,tur_cod,ciu_cod,col_cod,car_cod,alu_nom,"
                        + "alu_ape,alu_fec_nac,alu_ci,alu_dir,alu_bar,alu_tel,alu_cel,"
                        + "alu_fec_ing,alu_ema,pago,pago_ven,nivel,estado) VALUES ("
                        + codAlumno+"," + codTurno + "," + codCiudad + "," + codColegio + "," + codCarrera
                        + ",'" + txtnom1.getText() + "','" + txtape1.getText() + "',"
                        + dat.format(txtfecNac.getDate()) + ",'" + txtci1.getText() + "','"
                        + txtdir.getText() + "','" + txtbar.getText() + "','" + txtnrotel.getText() + "','"
                        + txtnrocel.getText() + "'," + dat.format(d) + ",'"
                        + txtema.getText() + "','" + txtara.getText() + "'," + cboven.getSelectedItem()+",'"
                        + cboniv.getSelectedItem()+ "','s')";
                //System.out.println(sql);
                con.Guardar(sql);

                //Guardar foto del alumno
                if (foto == true) {
                    try {
                        String codigofoto = "SELECT IFNULL(MAX(codigo),0)+1 codigo FROM foto";
                        rs = con.Consulta(codigofoto);
                        rs.next();
                        fotoBD f = new fotoBD(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
                        f.guardarfoto(rs.getString("codigo"), codAlumno);
                    } catch (SQLException ex) {
                        Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    foto = false;
                }
                botonagregar = false;
            }
            if (botonmodificar == true) {
                String update = "UPDATE alumno SET ciu_cod=" + codCiudad + ",col_cod="
                        + codColegio + ",car_cod=" + codCarrera + ",alu_nom='" + txtnom1.getText()
                        + "',alu_ape='" + txtape1.getText() + "',alu_fec_nac=" + dat.format(txtfecNac.getDate())
                        + ",alu_ci='" + txtci1.getText() + "',alu_dir='" + txtdir.getText() + "',alu_bar='"
                        + txtbar.getText() + "',alu_tel='" + txtnrotel.getText() + "',alu_cel='" + txtnrocel.getText()
                        + "',alu_ema='" + txtema.getText() + "',pago='" + txtara.getText() + "',pago_ven="
                        + cboven.getSelectedItem()+",nivel='"+cboniv.getSelectedItem() + "' WHERE alu_cod=" + codAlumno;
                //System.out.println(update);
                con.Guardar(update);

                //Actualiza foto
                try {
                    String valfoto = "SELECT * FROM foto WHERE alu_cod=" + codAlumno;
                    rs = con.Consulta(valfoto);
                    if (rs.next()) {
                        fotoBD f = new fotoBD(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
                        f.actualizafoto(codAlumno);
                    } else {
                        String codigofoto = "SELECT IFNULL(MAX(codigo),0)+1 codigo FROM foto";
                        rs = con.Consulta(codigofoto);
                        rs.next();
                        fotoBD f = new fotoBD(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
                        f.guardarfoto(rs.getString("codigo"), codAlumno);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
                }
                foto = false;
                botonmodificar = false;
            }

            //JOptionPane.showMessageDialog(null, "Los datos se guardaron correctamente");
            this.btncan.doClick();
        } else {
            this.btncan.doClick();
        }

    }//GEN-LAST:event_btngraActionPerformed

    private void btnmodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodActionPerformed
        botonmodificar = true;
        btnnue.setEnabled(false);
        btnmod.setEnabled(false);
        btncan.setEnabled(true);
        txtnom1.setEnabled(true);
        txtnom1.grabFocus();
    }//GEN-LAST:event_btnmodActionPerformed

    private void cbovenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbovenKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cboniv.setEnabled(true);
            cboniv.grabFocus();
        }
    }//GEN-LAST:event_cbovenKeyPressed

    private void btnbajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbajaActionPerformed
        int res = JOptionPane.showConfirmDialog(null, "Dedea dar de baja a "
                + txtnom1.getText(), "Mensaje", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            String baja = "UPDATE alumno SET estado='n' WHERE alu_cod=" + codAlumno;
            con.Guardar(baja);
            btncan.doClick();
        }

    }//GEN-LAST:event_btnbajaActionPerformed

    private void btnnue1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnue1ActionPerformed
        cargarLista("alu_cod>-1");
}//GEN-LAST:event_btnnue1ActionPerformed

    private void cbonivKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbonivKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cbotur.setEnabled(true);
            cbotur.grabFocus();
        }
    }//GEN-LAST:event_cbonivKeyPressed

    private void cboturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboturActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboturActionPerformed

    private void cboturKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cboturKeyPressed
if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (cbotur.getSelectedIndex() != 0) {
                String sql = "Select * From turno Where tur_des='" + cbotur.getSelectedItem() + "'";
                codTurno = codigoCombo(sql, "tur_cod");
                System.out.println(codTurno);
            }
            btngra.setEnabled(true);
            btngra.grabFocus();
        }
    }//GEN-LAST:event_cboturKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgrCar;
    private javax.swing.JButton btnAgrCiu;
    private javax.swing.JButton btnAgrCol;
    private javax.swing.JButton btnbaja;
    private javax.swing.JButton btnbusFot;
    private javax.swing.JButton btncan;
    private javax.swing.JButton btncapFot;
    private javax.swing.JButton btngra;
    private javax.swing.JButton btnmod;
    public static javax.swing.JButton btnnue;
    public static javax.swing.JButton btnnue1;
    private javax.swing.JButton btnsal;
    private javax.swing.JComboBox cbocar;
    private javax.swing.JComboBox cbociu;
    private javax.swing.JComboBox cbocol;
    private javax.swing.JComboBox cboniv;
    private javax.swing.JComboBox cbotur;
    private javax.swing.JComboBox cboven;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbl;
    private javax.swing.JList lista;
    private javax.swing.JTextField txtape;
    private javax.swing.JTextField txtape1;
    private javax.swing.JFormattedTextField txtara;
    private javax.swing.JTextField txtbar;
    public static javax.swing.JTextField txtci;
    private javax.swing.JTextField txtci1;
    private javax.swing.JTextField txtdir;
    private javax.swing.JTextField txtema;
    private com.toedter.calendar.JDateChooser txtfecNac;
    private javax.swing.JTextField txtnom;
    private javax.swing.JTextField txtnom1;
    private javax.swing.JFormattedTextField txtnrocel;
    private javax.swing.JTextField txtnrotel;
    // End of variables declaration//GEN-END:variables
}
