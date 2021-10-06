/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PaqFormulario;

import clases.Conexion;
import clases.fotoBD;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Administrador
 */
public class FrmCuota extends javax.swing.JDialog implements Runnable{

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
     * Creates new form FrmCuota
     */
    public FrmCuota(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        centrar();
        arNom = new ArrayList();
        arApe = new ArrayList();
        botonEnter();
    }

    private void centrar() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = this.getSize();
        setLocation((pantalla.width / 2 - (frame.width / 2)), pantalla.height / 2 - (frame.height / 2));

    }

    private void botonEnter() {
        InputMap map = new InputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
        btncan.setInputMap(0, map);
        btngra.setInputMap(0, map);
        btnver.setInputMap(0, map);
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

    private void conexion() {
        con = new Conexion(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
    }
    //Ejecutando hilos
    @Override
    public void run(){
        clicEnLista();
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
            Logger.getLogger(FrmCuota.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void limpiar() {
        txtape1.setText("");
        txtcar.setText("");
        txtci1.setText("");
        txtmonto.setText("");
        txtnom1.setText("");
        txtsaldo.setText("");
        txtturno.setText("");
        txtfac.setText("");
        cbomes.setSelectedIndex(0);
    }

    public void mostrarDatos() {
        try {
            conexion();
            //muestra datos de identificacion del funcionario
            String sqlpersona = "SELECT * FROM alumno WHERE alu_cod=" + codAlumno
                    + " AND estado='s'";
            //System.out.println(sqlpersona);
            rs = con.Consulta(sqlpersona);
            rs.next();
            txtnom1.setText(rs.getString("alu_nom"));
            txtape1.setText(rs.getString("alu_ape"));
            txtci1.setText(rs.getString("alu_ci"));
            txtfec.setDate(new Date());

            //------------------------------------------------

            //mostrar carrera
            String sqlcarrera = "SELECT c.car_des FROM carrera c,alumno a WHERE "
                    + "a.car_cod=c.car_cod AND a.alu_cod=" + codAlumno;
            //System.out.println(sqlcarrera);
            rs = con.Consulta(sqlcarrera);
            if (rs.next()) {
                txtcar.setText(rs.getString("c.car_des"));
            }

            //mostrar turno
            String sqltur = "SELECT t.tur_des FROM turno t,alumno a WHERE "
                    + "a.tur_cod=t.tur_cod AND a.alu_cod=" + codAlumno;

            rs = con.Consulta(sqltur);
            if (rs.next()) {
                txtturno.setText(rs.getString("t.tur_des"));
            }

            //mostrar numero de factura
            String nroFac = "Select ifnull(max(nrofactura),0)+1 codigo From detalle_pago";
            rs = con.Consulta(nroFac);
            rs.next();
            txtfac.setText(rs.getString(1));
            con.CerrarConexion();

        } catch (SQLException ex) {
            Logger.getLogger(FrmCuota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void EstadoCuenta() {
        double saldo = 0;
        double cuota = 0;
        double suma = 0;
        try {
            Date d = new Date();
            int mes = d.getMonth();
            cbomes.setSelectedIndex(mes + 1);
            cbomes.setEnabled(true);
            //System.out.println(sqlSaldo);
            String sqlCuota = "SELECT cuota FROM alumno WHERE alu_cod=" + codAlumno;
            ResultSet rescuota;
            conexion();
            rescuota = con.Consulta(sqlCuota);
            if (rescuota.next()) {
                cuota = rescuota.getDouble(1);
            }
            con.CerrarConexion();

            conexion();
            String sqlSaldo = "SELECT SUM(saldo) FROM pagos "
                    + "WHERE alu_cod=" + codAlumno;
            rs = con.Consulta(sqlSaldo);
            if (rs.next()) {
                saldo = rs.getDouble(1);
                //Corroboramos estado de mes actual      
                String sqlMes = "SELECT MAX(mes),estado FROM pagos "
                        + "WHERE alu_cod=" + codAlumno + " GROUP BY mes";
                ResultSet rsmes = con.Consulta(sqlMes);

                if (rsmes.next()) {
                    if (rsmes.getInt(1) == (mes + 1)) {
                        suma = saldo;
                    } else {
                        suma = saldo + cuota;
                    }
                } else {
                    suma = cuota;
                }
            } else {
                suma = cuota;
            }

            txtsaldo.setText(separadorDeMiles(suma));
            con.CerrarConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FrmCuota.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void clicEnLista(){
        int fila = 0;
        /*btncan.setEnabled(true);
         btnbaja.setEnabled(true);
         btnmod.setEnabled(true);
         btnmod.grabFocus();
         btnnue.setEnabled(false);*/
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
                conexion();
                rs = con.Consulta(sql);
                rs.next();
                codAlumno = rs.getString("alu_cod");
                fotoBD f = new fotoBD(AcesoUsuario.host, AcesoUsuario.base, AcesoUsuario.user, AcesoUsuario.pass);
                ImageIcon ima = new ImageIcon(f.getfoto(codAlumno));
                ImageIcon iconoEscalado = new ImageIcon(ima.getImage().getScaledInstance(lbl.getWidth(), -1, java.awt.Image.SCALE_DEFAULT));
                lbl.setIcon(iconoEscalado);
                con.CerrarConexion();
            } catch (SQLException ex) {
                Logger.getLogger(PanAlumno.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NullPointerException n) {
            ImageIcon im = new ImageIcon("src\\paqImagenes\\personas.png");
            lbl.setIcon(im);
        }
        //Muestra datos de la Persona
        limpiar();
        mostrarDatos();
        EstadoCuenta();
        txtmonto.setEditable(false);
        txtfec.setEnabled(false);
        txtfac.setEditable(false);
        if (Integer.parseInt(sacarPunto(txtsaldo.getText())) != 0) {
            txtmonto.setEditable(true);
            txtmonto.grabFocus();
            txtfec.setEnabled(true);
            txtfac.setEditable(true);
        }
        btnver.setEnabled(true);
        hiloClic=null;
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
        txtci = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnnue1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtnom1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtape1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtci1 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        lbl = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtsaldo = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        txtmonto = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        txtturno = new javax.swing.JFormattedTextField();
        txtcar = new javax.swing.JFormattedTextField();
        cbomes = new javax.swing.JComboBox();
        jLabel24 = new javax.swing.JLabel();
        btngra = new javax.swing.JButton();
        btncan = new javax.swing.JButton();
        btnsal = new javax.swing.JButton();
        txtfec = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "####/##/##", '_');
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtfac = new javax.swing.JTextField();
        btnver = new javax.swing.JButton();

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
                .addGap(46, 46, 46)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtci, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnnue1)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setText("Nombres");

        txtnom1.setEditable(false);
        txtnom1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setText("Apellidos");

        txtape1.setEditable(false);
        txtape1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel7.setText("C.I. Nro.");

        txtci1.setEditable(false);
        txtci1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel19.setText("Carrera");

        lbl.setFont(new java.awt.Font("DigifaceWide", 1, 36)); // NOI18N
        lbl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 0), 3, true));

        jLabel21.setText("Saldo");

        txtsaldo.setEditable(false);
        txtsaldo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtsaldo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        jLabel22.setText("Monto a pagar");

        txtmonto.setEditable(false);
        txtmonto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtmonto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        txtmonto.setNextFocusableComponent(btngra);
        txtmonto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtmontoFocusLost(evt);
            }
        });
        txtmonto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtmontoKeyPressed(evt);
            }
        });

        jLabel23.setText("Turno");

        txtturno.setEditable(false);
        txtturno.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtturno.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        txtcar.setEditable(false);
        txtcar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtcar.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        cbomes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        cbomes.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cbomes.setEnabled(false);

        jLabel24.setText("Mes");

        btngra.setText("Grabar");
        btngra.setEnabled(false);
        btngra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngraActionPerformed(evt);
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

        txtfec.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtfec.setEnabled(false);
        txtfec.setOpaque(false);

        jLabel6.setText("Fecha");

        jLabel8.setText("Nº Fac.");

        txtfac.setEditable(false);
        txtfac.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnver.setText("Ver Detalle");
        btnver.setEnabled(false);
        btnver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtfec, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtfac, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtape1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtci1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtcar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtsaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtmonto, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtturno, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbomes, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtnom1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnver)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btngra)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btncan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnsal, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(txtfec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtfac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtnom1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtape1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(txtci1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtcar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel23)
                    .addComponent(txtturno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbomes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel21)
                    .addComponent(txtsaldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel22)
                    .addComponent(txtmonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btngra)
                    .addComponent(btncan)
                    .addComponent(btnsal, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnver))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            if(hiloClic==null){
                hiloClic=new Thread(this);
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

    private void txtmontoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtmontoKeyPressed
        String keypres = KeyEvent.getKeyText(evt.getKeyCode());

        if (!(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE
                || evt.getKeyCode() == KeyEvent.VK_DELETE
                || evt.getKeyCode() == KeyEvent.VK_ENTER
                || FrmPrincipal.isNumeric(String.valueOf(evt.getKeyChar())) == true)) {
            try {
                Integer.parseInt(KeyEvent.getKeyText(evt.getKeyCode()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Solo puede Ingresar Numeros", "Error", JOptionPane.ERROR_MESSAGE);

            }
        }
    }//GEN-LAST:event_txtmontoKeyPressed

    private void btngraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngraActionPerformed
        int res = JOptionPane.showConfirmDialog(null, "Realmente, ¿Desea Grabar"
                + " los Datos?", "Mensaje", JOptionPane.OK_CANCEL_OPTION);
        SimpleDateFormat dat = new SimpleDateFormat("yyyyMMdd");
        if (res == JOptionPane.OK_OPTION) {
            boolean ban = false;
            String sql = "SELECT mes,pag_cod FROM pagos WHERE alu_cod="
                    + codAlumno + " AND mes=" + (cbomes.getSelectedIndex());
            conexion();
            rs = con.Consulta(sql);
            try {

                if (rs.next()) {
                    // sumamos saldo y monto pagado
                    int suma = Integer.parseInt(sacarPunto(txtsaldo.getText()))
                            - Integer.parseInt(sacarPunto(txtmonto.getText()));
                    // si cancela actualiza estado a 'C' cancelado
                    if (suma == 0) {
                        String updatePagos = "UPDATE pagos SET saldo=0, estado='C' WHERE pag_cod=" + rs.getInt(2);
                        ban = con.Guardar(updatePagos);
                        // si no cancela actualiza saldo y estado 'P' pendiente    
                    } else {
                        String updatePagos = "UPDATE pagos SET saldo=" + suma
                                + ", estado='P' WHERE pag_cod=" + rs.getInt(2);
                        ban = con.Guardar(updatePagos);
                    }
                    String codDetalle = "Select ifnull(max(det_cod),0)+1 codigo From detalle_pago";
                    ResultSet rsDet = con.Consulta(codDetalle);
                    rsDet.next();
                    String guardarDetalle = "INSERT INTO detalle_pago VALUES(" + rsDet.getInt(1)
                            + "," + rs.getInt(2) + "," + dat.format(txtfec.getDate()) + ",'"
                            + txtfac.getText() + "'," + sacarPunto(txtmonto.getText()) + ")";
                    ban = con.Guardar(guardarDetalle);
                } else {
                    String codPago = "Select ifnull(max(pag_cod),0)+1 codigo From pagos";
                    ResultSet rsPag = con.Consulta(codPago);
                    rsPag.next();
                    int suma = Integer.parseInt(sacarPunto(txtsaldo.getText()))
                            - Integer.parseInt(sacarPunto(txtmonto.getText()));
                    if (suma == 0) {
                        String guardaPago = "INSERT INTO pagos VALUES(" + rsPag.getInt(1)
                                + "," + codAlumno + "," + (cbomes.getSelectedIndex()) + ",0,'C')";
                        ban = con.Guardar(guardaPago);
                    } else {
                        String guardaPago = "INSERT INTO pagos VALUES(" + rsPag.getInt(1)
                                + "," + codAlumno + "," + (cbomes.getSelectedIndex()) + "," + suma
                                + ",'P')";
                        ban = con.Guardar(guardaPago);
                    }

                    String codDetalle = "Select ifnull(max(det_cod),0)+1 codigo From detalle_pago";
                    ResultSet rsDet = con.Consulta(codDetalle);
                    rsDet.next();
                    String guardarDetalle = "INSERT INTO detalle_pago VALUES(" + rsDet.getInt(1)
                            + "," + rsPag.getInt(1) + "," + dat.format(txtfec.getDate()) + ",'"
                            + txtfac.getText() + "'," + sacarPunto(txtmonto.getText()) + ")";
                    ban = con.Guardar(guardarDetalle);
                    con.CerrarConexion();
                }

                if (ban == true) {
                    JOptionPane.showMessageDialog(null, "Los datos se guardaron correctamente");
                }
            } catch (SQLException ex) {
                Logger.getLogger(FrmCuota.class.getName()).log(Level.SEVERE, null, ex);
            }
            btncan.doClick();
        }
    }//GEN-LAST:event_btngraActionPerformed

    private void btncanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncanActionPerformed
        cancelar = true;
        btngra.setEnabled(false);
        btnver.setEnabled(false);
        texto = "";
        codAlumno = "";
        foto = false;
        lbl.setIcon(null);
        botonagregar = false;
        botonmodificar = false;
        modelo.clear();
        lista.setModel(modelo);
        limpiar();
    }//GEN-LAST:event_btncanActionPerformed

    private void btnsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalActionPerformed
        //FrmPrincipal.CambiarPanel();
        //FrmAlumno f=new FrmAlumno(null, true);
        //f.dispose();
        cancelar = true;
        this.dispose();
    }//GEN-LAST:event_btnsalActionPerformed

    private void txtmontoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtmontoFocusLost
        if (!txtmonto.getText().isEmpty()) {
            int nro1 = Integer.parseInt(sacarPunto(txtsaldo.getText()));
            int nro2 = Integer.parseInt(sacarPunto(txtmonto.getText()));
            if (nro2 > nro1) {
                JOptionPane.showMessageDialog(null, "El monto a pagar no puede ser mayor al saldo", "Error", JOptionPane.ERROR_MESSAGE);
                txtmonto.setText("");
                txtmonto.grabFocus();
            } else {
                btngra.setEnabled(true);
                btngra.grabFocus();
                btncan.setEnabled(true);
            }
        }

    }//GEN-LAST:event_txtmontoFocusLost

    private void btnverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnverActionPerformed
        try {
            if (txtsaldo.getText().equals("0")) {
                ClassLoader cl = this.getClass().getClassLoader();
                InputStream fis = (cl.getResourceAsStream("paqInformes/DetallePagoC.jasper"));
                JasperReport reporte = (JasperReport) JRLoader.loadObject(fis);
                conexion();
                Map par = new HashMap();
                par.put("alumno", codAlumno);
                JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, par, con.getConexion());
                JasperViewer vista = new JasperViewer(jasperPrint, false);
                vista.setTitle("Detalle");
                vista.setVisible(true);
            } else {
                ClassLoader cl = this.getClass().getClassLoader();
                InputStream fis = (cl.getResourceAsStream("paqInformes/DetallePagoP.jasper"));
                JasperReport reporte = (JasperReport) JRLoader.loadObject(fis);
                conexion();
                Map par = new HashMap();
                par.put("alumno", codAlumno);
                JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, par, con.getConexion());
                JasperViewer vista = new JasperViewer(jasperPrint, false);
                vista.setTitle("Detalle");
                vista.setVisible(true);
            }
        } catch (JRException ex) {
            Logger.getLogger(FrmCuota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnverActionPerformed

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
            java.util.logging.Logger.getLogger(FrmCuota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmCuota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmCuota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmCuota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmCuota dialog = new FrmCuota(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnver;
    private javax.swing.JComboBox cbomes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl;
    private javax.swing.JList lista;
    private javax.swing.JTextField txtape;
    private javax.swing.JTextField txtape1;
    private javax.swing.JFormattedTextField txtcar;
    public static javax.swing.JTextField txtci;
    private javax.swing.JTextField txtci1;
    private javax.swing.JTextField txtfac;
    private com.toedter.calendar.JDateChooser txtfec;
    private javax.swing.JFormattedTextField txtmonto;
    private javax.swing.JTextField txtnom;
    private javax.swing.JTextField txtnom1;
    private javax.swing.JFormattedTextField txtsaldo;
    private javax.swing.JFormattedTextField txtturno;
    // End of variables declaration//GEN-END:variables
}
