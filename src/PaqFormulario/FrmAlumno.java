/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import clases.Conexion;
import clases.ImagePreview;
import clases.fotoBD;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Administrador
 */
public class FrmAlumno extends javax.swing.JDialog {

    PanAlumno alu;
    Dimension d;
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
    static boolean ventanaemergente=false;

    /**
     * Creates new form FrmAlumno
     */
    public FrmAlumno(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();//mostrarpanel();
        centrar();
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
        cargaCboCiu();
        cargaCboCar();
        cargaCboCol();
        cargaCboTur();
        botonEnter();
        arNom = new ArrayList();
        arApe = new ArrayList();
    }

    public void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), pantalla.height / 2 - (frame.height / 2));

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
    
    private String sacarPunto(String txt) {
        txt = txt.replace(".", "").replaceAll(",", ".");

        return txt;
    }
    
    public String separadorDeMiles(double nro) {
        double num = nro;
        DecimalFormat formateador = new DecimalFormat("###,###");
        return String.valueOf(formateador.format(num));
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
            txtmat.setText(rs.getString("matricula"));
            txtara.setText(separadorDeMiles(rs.getDouble("cuota")));
            txtbar.setText(rs.getString("alu_bar"));
            cboven.setSelectedItem(rs.getString("pago_ven"));
            txtnrotel.setText(rs.getString("alu_cel"));
            txtnropapa.setText(rs.getString("alu_tel"));
            cboniv.setSelectedItem(rs.getString("nivel"));
            txtobs.setText(rs.getString("observacion"));
            //------------------------------------------------

            //mostrar ciudad
            String sqlciu = "SELECT c.ciu_des FROM ciudad c,alumno a WHERE "
                    + "a.ciu_cod=c.ciu_cod AND a.alu_cod=" + codAlumno;

            rs = con.Consulta(sqlciu);
            if (rs.next()) {
                cbociu.setSelectedItem(rs.getString("c.ciu_des"));
            }
            
            //mostrar turno
            String sqltur = "SELECT t.tur_des FROM turno t,alumno a WHERE "
                    + "a.tur_cod=t.tur_cod AND a.alu_cod=" + codAlumno;

            rs = con.Consulta(sqltur);
            if (rs.next()) {
                cbotur.setSelectedItem(rs.getString("t.tur_des"));
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

    private void mostrarpanel() {
        alu = new PanAlumno();
        d = alu.getSize();
        alu.setSize(this.getSize());
        this.add(alu, BorderLayout.CENTER);
        this.pack();
    }

    private void limpiarCampos() {
        txtara.setText("");

        txtbar.setText("");

        txtci1.setText("");

        txtdir.setText("");

        txtema.setText("");

        txtnom1.setText("");

        txtnropapa.setText("");

        txtnrotel.setText("");
        
        txtmat.setText("");

        cbocar.setSelectedIndex(0);

        cbociu.setSelectedIndex(0);

        cbocol.setSelectedIndex(0);

        cboven.setSelectedIndex(0);

        cboniv.setSelectedIndex(0);

        cbotur.setSelectedIndex(0);

        txtape.setText("");
        txtnom.setText("");
        txtci.setText("");
        lbl.setIcon(null);
    }

    private void habilitarCampos() {
        txtape1.setEditable(true);
        txtara.setEditable(true);
        txtfecNac.setEnabled(true);
        txtbar.setEditable(true);
        txtci1.setEditable(true);
        txtdir.setEditable(true);
        txtema.setEditable(true);
        txtmat.setEditable(true);
        txtnom1.setEditable(true);
        txtnropapa.setEditable(true);
        txtnrotel.setEditable(true);
        cbocar.setEnabled(true);
        cbociu.setEnabled(true);
        cbocol.setEnabled(true);
        cboven.setEnabled(true);
        cboniv.setEnabled(true);
        cbotur.setEnabled(true);
        btnbusFot.setEnabled(true);
        btncapFot.setEnabled(true);
        btnAgrCar.setEnabled(true);
        btnAgrCiu.setEnabled(true);
        btnAgrCol.setEnabled(true);
    }
    
    private String comboCodigo(String consulta, String ColumnaBD, String descripcion) {
        String codigo = "";
        try {
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
        } catch (SQLException ex) {
            Logger.getLogger(ReporteEstadoDeCuenta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }
    
    private void capturarCodigoCombos() {
        if (cbocar.getSelectedIndex() > 0) {
            codCarrera = comboCodigo("SELECT * FROM carrera", "car_des", (String) cbocar.getSelectedItem());
            //cboprv.grabFocus();
        }else if (cbociu.getSelectedIndex() > 0) {
            codCiudad = comboCodigo("SELECT * FROM ciudad", "ciu_des", (String) cbociu.getSelectedItem());
            //cboprv.grabFocus();
        }else if (cbocol.getSelectedIndex() > 0) {
            codColegio = comboCodigo("SELECT * FROM colegio", "col_des", (String) cbocol.getSelectedItem());
            //cboprv.grabFocus();
        }else if (cbotur.getSelectedIndex() > 0) {
            codTurno = comboCodigo("SELECT * FROM turno", "tur_des", (String) cbotur.getSelectedItem());
            //cboprv.grabFocus();
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
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtnom1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtape1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtci1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtnropapa = new javax.swing.JFormattedTextField();
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
        jLabel21 = new javax.swing.JLabel();
        txtmat = new javax.swing.JFormattedTextField();
        txtfecNac = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        jScrollPane2 = new javax.swing.JScrollPane();
        txtobs = new javax.swing.JTextArea();
        jLabel22 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImages(null);

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setText("Nombres");

        txtnom1.setEditable(false);
        txtnom1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtnom1.setNextFocusableComponent(txtape1);

        jLabel5.setText("Apellidos");

        txtape1.setEditable(false);
        txtape1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtape1.setNextFocusableComponent(txtci1);
        txtape1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtape1FocusGained(evt);
            }
        });

        jLabel6.setText("Fecha de nacimiento");

        txtci1.setEditable(false);
        txtci1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtci1.setNextFocusableComponent(txtfecNac);
        txtci1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtci1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtci1FocusLost(evt);
            }
        });

        jLabel7.setText("C.I. Nº");

        txtnropapa.setEditable(false);
        txtnropapa.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtnropapa.setNextFocusableComponent(txtema);
        txtnropapa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnropapaActionPerformed(evt);
            }
        });
        txtnropapa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtnropapaKeyPressed(evt);
            }
        });

        txtnrotel.setEditable(false);
        txtnrotel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtnrotel.setNextFocusableComponent(txtnropapa);
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

        jLabel11.setText("Nº Tel. del Padre/encargado");

        jLabel10.setText("Nº Tel. del Alumno");

        jLabel12.setText("Dirección");

        txtdir.setEditable(false);
        txtdir.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtdir.setNextFocusableComponent(txtbar);
        txtdir.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtdirFocusLost(evt);
            }
        });

        txtbar.setEditable(false);
        txtbar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtbar.setNextFocusableComponent(cbociu);
        txtbar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtbarFocusLost(evt);
            }
        });

        jLabel14.setText("Barrio");

        jLabel16.setText("Ciudad");

        cbociu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbociu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbociu.setEnabled(false);
        cbociu.setNextFocusableComponent(txtnrotel);
        cbociu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbociuFocusLost(evt);
            }
        });

        txtema.setEditable(false);
        txtema.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtema.setNextFocusableComponent(cbocol);

        jLabel15.setText("E-Mail");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 153, 0));
        jLabel13.setText("DATOS DE IDENTIFICACIÓN DEL ALUMNO");

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
        cbocol.setNextFocusableComponent(cbocar);
        cbocol.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbocolFocusLost(evt);
            }
        });

        jLabel19.setText("Carrera");

        cbocar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbocar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbocar.setEnabled(false);
        cbocar.setNextFocusableComponent(btnbusFot);
        cbocar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbocarFocusLost(evt);
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
        btncapFot.setNextFocusableComponent(txtmat);
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
        btnbusFot.setNextFocusableComponent(btncapFot);
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

        jLabel20.setText("Cuota");

        jLabel8.setText("Vencimiento al");

        txtara.setEditable(false);
        txtara.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtara.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        txtara.setNextFocusableComponent(cboven);
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
        cboven.setNextFocusableComponent(cboniv);

        btnbaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paqImagenes/Pulgar-abajo.jpg"))); // NOI18N
        btnbaja.setToolTipText("Baja de alumno");
        btnbaja.setEnabled(false);
        btnbaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbajaActionPerformed(evt);
            }
        });

        jLabel9.setText("Nivel de alumno/a");

        cboniv.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Secundaria", "Pre-Universitario", "Universitario" }));
        cboniv.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cboniv.setEnabled(false);
        cboniv.setNextFocusableComponent(cbotur);
        cboniv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbonivKeyPressed(evt);
            }
        });

        jLabel17.setText("Turno");

        cbotur.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbotur.setEnabled(false);
        cbotur.setNextFocusableComponent(txtobs);
        cbotur.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cboturFocusLost(evt);
            }
        });

        jLabel21.setText("Matricula");

        txtmat.setEditable(false);
        txtmat.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtmat.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        txtmat.setNextFocusableComponent(txtara);
        txtmat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtmatActionPerformed(evt);
            }
        });
        txtmat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtmatKeyPressed(evt);
            }
        });

        txtfecNac.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtfecNac.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtfecNacFocusGained(evt);
            }
        });

        txtobs.setColumns(20);
        txtobs.setLineWrap(true);
        txtobs.setRows(5);
        txtobs.setNextFocusableComponent(btngra);
        txtobs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtobsFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(txtobs);

        jLabel22.setText("Observación");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addGap(4, 4, 4)
                                    .addComponent(txtci1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtfecNac, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel12)
                                    .addGap(4, 4, 4)
                                    .addComponent(txtdir, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel14)
                                    .addGap(4, 4, 4)
                                    .addComponent(txtbar, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel16)
                                    .addGap(4, 4, 4)
                                    .addComponent(cbociu, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnAgrCiu, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel15)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtema, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtnrotel, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(4, 4, 4)
                                    .addComponent(jLabel11)
                                    .addGap(4, 4, 4)
                                    .addComponent(txtnropapa, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel18)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbocol, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnAgrCol, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel19)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(10, 10, 10)
                                            .addComponent(btnbusFot)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btncapFot))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(cbocar, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnAgrCar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel13)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtnom1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(4, 4, 4)
                                        .addComponent(jLabel5)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtape1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel21)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtmat, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel20)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtara, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cboven, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cboniv, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel17)
                                    .addGap(4, 4, 4)
                                    .addComponent(cbotur, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(41, 41, 41)
                            .addComponent(btnbaja, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(73, 73, 73)
                            .addComponent(btnnue)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnmod)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btngra)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btncan)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnsal, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel16)
                    .addComponent(cbociu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgrCiu)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(3, 3, 3)
                            .addComponent(jLabel14))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10)
                    .addComponent(txtnrotel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtnropapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(txtema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18)
                    .addComponent(cbocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgrCol))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbocar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(btnAgrCar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnbusFot)
                    .addComponent(btncapFot))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20)
                    .addComponent(jLabel8)
                    .addComponent(txtara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboven, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtmat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel17)
                    .addComponent(cbotur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(cboniv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel22)))
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnsal, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(btnbaja, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnnue)
                        .addComponent(btnmod)
                        .addComponent(btngra)
                        .addComponent(btncan)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                ImageIcon ima = new ImageIcon(f.getfoto(codAlumno));
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

    private void txtnropapaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnropapaActionPerformed
        txtema.setEnabled(true);
        txtema.grabFocus();
    }//GEN-LAST:event_txtnropapaActionPerformed

    private void txtnropapaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnropapaKeyPressed
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
                        txtnropapa.setText(txtnropapa.getText().replace(keypres.toLowerCase(), ""));
                        txtnropapa.setText(txtnropapa.getText().replace(keypres.toUpperCase(), ""));
                        txtnropapa.grabFocus();
                    }
                }
            }
        }
    }//GEN-LAST:event_txtnropapaKeyPressed

    private void txtnrotelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnrotelActionPerformed
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

    private void btngraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngraActionPerformed
        int res = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);
        SimpleDateFormat dat = new SimpleDateFormat("yyyyMMdd");
        if (res == JOptionPane.OK_OPTION) {
            capturarCodigoCombos();
            boolean ban=false;
            if (botonagregar == true) {

                Date d = new Date();
                
                String sql = "INSERT INTO alumno(alu_cod,tur_cod,ciu_cod,col_cod,car_cod,alu_nom,"
                        + "alu_ape,alu_fec_nac,alu_ci,alu_dir,alu_bar,alu_tel,alu_cel,"
                        + "alu_fec_ing,alu_ema,matricula,cuota,pago_ven,nivel,estado,observacion) VALUES ("
                        + codAlumno + "," + codTurno + "," + codCiudad + "," + codColegio + "," + codCarrera
                        + ",'" + txtnom1.getText() + "','" + txtape1.getText() + "',"
                        + dat.format(txtfecNac.getDate()) + ",'" + txtci1.getText() + "','"
                        + txtdir.getText() + "','" + txtbar.getText() + "','" + txtnrotel.getText() + "','"
                        + txtnropapa.getText() + "'," + dat.format(d) + ",'"
                        + txtema.getText() + "','" + sacarPunto(txtmat.getText()) + "','" + sacarPunto(txtara.getText())
                        + "'," + cboven.getSelectedItem() + ",'" + cboniv.getSelectedItem() + "','s','"
                        +txtobs.getText()+"')";
                //System.out.println(sql);
                ban=con.Guardar(sql);
                
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
                        + txtbar.getText() + "',alu_tel='" + txtnrotel.getText() + "',alu_cel='" + txtnropapa.getText()
                        + "',alu_ema='" + txtema.getText() + "',matricula='" + sacarPunto(txtmat.getText()) + "',cuota='" + sacarPunto(txtara.getText()) + "',pago_ven="
                        + cboven.getSelectedItem() + ",nivel='" + cboniv.getSelectedItem() + "',observacion='"
                        +txtobs.getText() +"' WHERE alu_cod=" + codAlumno;
                //System.out.println(update);
                ban=con.Guardar(update);

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

            if(ban==true){
                JOptionPane.showMessageDialog(null, "Los datos se guardaron correctamente");
            }
            this.btncan.doClick();
        } else {
            this.btncan.doClick();
        }
    }//GEN-LAST:event_btngraActionPerformed

    private void btnmodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodActionPerformed
        cancelar = false;
        botonmodificar = true;
        btnnue.setEnabled(false);
        btnmod.setEnabled(false);
        btncan.setEnabled(true);
        btngra.setEnabled(true);
        habilitarCampos();
        txtnom1.grabFocus();
    }//GEN-LAST:event_btnmodActionPerformed

    private void btncanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncanActionPerformed
        cancelar = true;
        txtape1.setText("");
        txtape1.setEditable(false);
        txtara.setText("");
        txtara.setEditable(false);
        txtbar.setText("");
        txtbar.setEditable(false);
        txtci1.setText("");
        txtci1.setEditable(false);
        txtdir.setText("");
        txtdir.setEditable(false);
        txtema.setText("");
        txtema.setEditable(false);
        txtfecNac.setDate(null);
        txtfecNac.setEnabled(false);
        txtnom1.setText("");
        txtnom1.setEditable(false);
        txtnropapa.setText("");
        txtnropapa.setEditable(false);
        txtnrotel.setText("");
        txtnrotel.setEditable(false);
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
        txtmat.setText("");
        txtmat.setEditable(false);
        txtnom.setText("");
        txtobs.setText("");
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

    private void btnsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalActionPerformed
        //FrmPrincipal.CambiarPanel();
        //FrmAlumno f=new FrmAlumno(null, true);
        //f.dispose();
        cancelar = true;
        this.dispose();

    }//GEN-LAST:event_btnsalActionPerformed

    private void btnnueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnueActionPerformed
        try {
            cancelar = false;
            botonagregar = true;
            String sql = "Select ifnull(max(alu_cod),0)+1 codigo From alumno";
            //System.out.println(sql);
            rs = con.Consulta(sql);
            rs.next();
            codAlumno = rs.getString("codigo");
            btnnue.setEnabled(false);
            btnmod.setEnabled(false);
            btncan.setEnabled(true);
            habilitarCampos();
            limpiarCampos();
            txtnom1.setEnabled(true);
            txtnom1.grabFocus();
        } catch (SQLException ex) {
            Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnnueActionPerformed

    private void btnAgrCarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrCarActionPerformed
        ventanaemergente=true;
        FrmCarrera d = new FrmCarrera(new JFrame(), true);
        d.setVisible(true);
        FrmCarrera.btnAgr.grabFocus();
        
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
        ventanaemergente=false;
    }//GEN-LAST:event_btnAgrCarActionPerformed

    private void btnAgrColActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrColActionPerformed
        ventanaemergente=true;
        FrmColegio d = new FrmColegio(new JFrame(), true);
        d.setVisible(true);
        FrmColegio.btnAgr.grabFocus();
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
        ventanaemergente=false;
    }//GEN-LAST:event_btnAgrColActionPerformed

    private void btnAgrCiuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgrCiuActionPerformed
        ventanaemergente = true;
        FrmCiudad d = new FrmCiudad(new JFrame(), true);
        //dialogo = d;
        d.setVisible(true);
        FrmCiudad.btnAgr.grabFocus();
        //dialogo.setVisible(true);
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
        ventanaemergente=false;
    }//GEN-LAST:event_btnAgrCiuActionPerformed

    private void btncapFotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncapFotActionPerformed
        capturarFoto();
        txtmat.grabFocus();
    }//GEN-LAST:event_btncapFotActionPerformed

    private void btncapFotKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btncapFotKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            txtara.grabFocus();
        }
    }//GEN-LAST:event_btncapFotKeyPressed

    private void btnbusFotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbusFotActionPerformed
        buscarFoto();
        txtmat.grabFocus();
    }//GEN-LAST:event_btnbusFotActionPerformed

    private void btnbusFotKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnbusFotKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            btncapFot.grabFocus();
        }
    }//GEN-LAST:event_btnbusFotKeyPressed

    private void txtaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtaraActionPerformed
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

    private void btnbajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbajaActionPerformed
        int res = JOptionPane.showConfirmDialog(null, "Dedea dar de baja a "
                + txtnom1.getText(), "Mensaje", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            String baja = "UPDATE alumno SET estado='n' WHERE alu_cod=" + codAlumno;
            con.Guardar(baja);
            btncan.doClick();
        }
    }//GEN-LAST:event_btnbajaActionPerformed

    private void cbonivKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbonivKeyPressed
    }//GEN-LAST:event_cbonivKeyPressed

    private void txtmatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtmatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtmatActionPerformed

    private void txtmatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtmatKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtmatKeyPressed

    private void txtci1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtci1FocusLost
        btngra.setEnabled(true);
    }//GEN-LAST:event_txtci1FocusLost

    private void txtdirFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtdirFocusLost
        txtdir.setText(txtdir.getText().toUpperCase());
    }//GEN-LAST:event_txtdirFocusLost

    private void txtbarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtbarFocusLost
        txtbar.setText(txtbar.getText().toUpperCase());

    }//GEN-LAST:event_txtbarFocusLost

    private void cbociuFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbociuFocusLost
        /*if (cbociu.getSelectedIndex() != 0) {
            String sql = "Select * From ciudad Where ciu_des='" + cbociu.getSelectedItem() + "'";
            codCiudad = codigoCombo(sql, "ciu_cod");
        }*/
    }//GEN-LAST:event_cbociuFocusLost

    private void cbocolFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbocolFocusLost
        /*if (cbocol.getSelectedIndex() != 0) {
            String sql = "Select * From colegio Where col_des='" + cbocol.getSelectedItem() + "'";
            codColegio = codigoCombo(sql, "col_cod");
        }*/
    }//GEN-LAST:event_cbocolFocusLost

    private void cbocarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbocarFocusLost
        /*if (cbocar.getSelectedIndex() != 0) {
            String sql = "Select * From carrera Where car_des='" + cbocar.getSelectedItem() + "'";
            codCarrera = codigoCombo(sql, "car_cod");
        }*/
    }//GEN-LAST:event_cbocarFocusLost

    private void cboturFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboturFocusLost
/*        if (cbotur.getSelectedIndex() != 0) {
            String sql = "Select * From turno Where tur_des='" + cbotur.getSelectedItem() + "'";
            codTurno = codigoCombo(sql, "tur_cod");
            
        }*/
    }//GEN-LAST:event_cboturFocusLost

    private void txtape1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtape1FocusGained
        if (txtnom1.getText().equals("") && cancelar == false) {
            txtnom1.grabFocus();
            JOptionPane.showMessageDialog(null, "El campo ''Nombre'' no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            txtnom1.setText(txtnom1.getText().toUpperCase());
        }
    }//GEN-LAST:event_txtape1FocusGained

    private void txtci1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtci1FocusGained
        if (txtape1.getText().equals("") && cancelar == false) {
            txtape1.grabFocus();
            JOptionPane.showMessageDialog(null, "El campo ''Apellido'' no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE); 
        } else {
            txtape1.setText(txtape1.getText().toUpperCase());
        }
    }//GEN-LAST:event_txtci1FocusGained

    private void txtfecNacFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtfecNacFocusGained
        if (txtci1.getText().equals("")) {
            txtci1.grabFocus();
            JOptionPane.showMessageDialog(null, "El campo ''C.I. Nro'' no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtfecNacFocusGained

    private void txtobsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtobsFocusLost
      
    }//GEN-LAST:event_txtobsFocusLost

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
            java.util.logging.Logger.getLogger(FrmAlumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmAlumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmAlumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmAlumno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmAlumno dialog = new FrmAlumno(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
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
    private javax.swing.JFormattedTextField txtmat;
    private javax.swing.JTextField txtnom;
    private javax.swing.JTextField txtnom1;
    private javax.swing.JFormattedTextField txtnropapa;
    private javax.swing.JTextField txtnrotel;
    private javax.swing.JTextArea txtobs;
    // End of variables declaration//GEN-END:variables
}
