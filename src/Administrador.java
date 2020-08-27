import quick.dbtable.DBTable;

import java.util.StringTokenizer;

import java.sql.*;

import javax.swing.*;
import java.awt.event.*;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

public class Administrador extends JInternalFrame {
	protected Connection ConexionBD;
	private JPanel panelTabla, panelConsultas,panelInformacionTabla,panelInicioSesion;
	private DBTable table,tablaDeTablas,tablaDescripcionTablas;
	private JButton btnIngresar,btnSeleccionarTabla;
	private String consulta= "", tablaSelec="";
	private JTextArea textArea;
	private JTextField textoUsuario;
	private JPasswordField textoPass;
	private JLayeredPane layeredPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Administrador frame = new Administrador();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Administrador() {
		setPreferredSize(new Dimension(640,480));
		setBounds(0,0,640,480);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setClosable(true);
		setResizable(true);
		setMaximizable(true);
		getContentPane().setLayout(null);		
		this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent evt) {
               desconectarBD();
            }
            public void componentShown(ComponentEvent evt) {
            	conectarBD();
            }
         });
		
		layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0,630,470);
		getContentPane().add(layeredPane);
		inicializarPanel();
	}
	
	/*
	 * Inicializa los paneles, creando sus componentes.
	 */
	private void inicializarPanel() {
		panelConsultas = new JPanel();
		layeredPane.setLayer(panelConsultas, 4);
		panelConsultas.setBounds(0, 0, 620, 445);
		layeredPane.add(panelConsultas);
		panelConsultas.setLayout(null);
		
		textArea = new JTextArea();
		textArea.setBounds(10, 23, 382, 178);
		panelConsultas.add(textArea);
		
		btnIngresar = new JButton("Ingresar");
		btnIngresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!computarConsulta())
					JOptionPane.showMessageDialog(null, "No corresponde a una sentencia SQL");
				actualizarTablas();
			}

		});
		btnIngresar.setBounds(33, 225, 89, 23);
		panelConsultas.add(btnIngresar);
		
		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.setBounds(225, 225, 89, 23);
		panelConsultas.add(btnEliminar);
		
		tablaDeTablas = new DBTable();
		tablaDeTablas.setEditable(false);
		tablaDeTablas.setLocation(412, 29);
		tablaDeTablas.setSize(183, 186);
		panelConsultas.add(tablaDeTablas);
		
		JTextPane txtpnSeleccioneUnaTabla = new JTextPane();
		txtpnSeleccioneUnaTabla.setEditable(false);
		txtpnSeleccioneUnaTabla.setOpaque(false);
		txtpnSeleccioneUnaTabla.setText("Seleccione una tabla para ver su formato");
		txtpnSeleccioneUnaTabla.setBounds(402, 11, 208, 20);
		panelConsultas.add(txtpnSeleccioneUnaTabla);
		
		btnSeleccionarTabla = new JButton("Seleccionar Tabla");
		btnSeleccionarTabla.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mostrarInformacionTabla();
			}
		});
		btnSeleccionarTabla.setBounds(412, 225, 183, 23);
		panelConsultas.add(btnSeleccionarTabla);
		
		panelInformacionTabla = new JPanel();
		panelInformacionTabla.setBounds(65, 259, 382, 186);
		panelConsultas.add(panelInformacionTabla);
		panelInformacionTabla.setLayout(null);
		
		tablaDescripcionTablas = new DBTable();
		tablaDeTablas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				clickShowTables(e);
			}
		});
		tablaDescripcionTablas.setBounds(10, 11, 362, 175);
		tablaDescripcionTablas.setEditable(false);
		panelInformacionTabla.add(tablaDescripcionTablas);
		tablaDeTablas.setVisible(true);
		
		btnEliminar.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});
		
		panelTabla = new JPanel();
		layeredPane.setLayer(panelTabla, 1);
		panelTabla.setBounds(0, 0, 620, 445);
		layeredPane.add(panelTabla);
		panelTabla.setLayout(null);
		
		table = new DBTable();
		table.setEditable(false);
		table.setBounds(10, 11, 600, 380);
		panelTabla.add(table);
		
		JButton btnVolver = new JButton("Volver");
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inicioMenu();
			}
		});
		btnVolver.setBounds(251, 411, 89, 23);
		
		panelTabla.add(btnVolver);
		
		panelInicioSesion = new JPanel();
		layeredPane.setLayer(panelInicioSesion, 3);
		panelInicioSesion.setBounds(100, 61, 417, 270);
		layeredPane.add(panelInicioSesion);
		panelInicioSesion.setLayout(null);
		
		textoUsuario = new JTextField();
		textoUsuario.setForeground(Color.BLACK);
		textoUsuario.setBounds(171, 76, 96, 20);
		panelInicioSesion.add(textoUsuario);
		textoUsuario.setColumns(10);
		
		JTextPane txtpnUsuario = new JTextPane();
		txtpnUsuario.setFont(new Font("Candara", Font.PLAIN, 12));
		txtpnUsuario.setEditable(false);
		txtpnUsuario.setOpaque(false);
		txtpnUsuario.setText("Usuario");
		txtpnUsuario.setBounds(99, 76, 68, 20);
		panelInicioSesion.add(txtpnUsuario);
		
		JTextPane txtpnContrasea = new JTextPane();
		txtpnContrasea.setFont(new Font("Candara", Font.PLAIN, 12));
		txtpnContrasea.setEditable(false);
		txtpnContrasea.setOpaque(false);
		txtpnContrasea.setText("Contrase\u00F1a");
		txtpnContrasea.setBounds(100, 119, 68, 20);
		panelInicioSesion.add(txtpnContrasea);
		
		JButton btnIniciarSesion = new JButton("Iniciar Sesion");
		btnIniciarSesion.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
		btnIniciarSesion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inicioSesion();
			}
		});
		btnIniciarSesion.setBounds(99, 163, 168, 23);
		panelInicioSesion.add(btnIniciarSesion);
		
		textoPass = new JPasswordField();
		textoPass.setBounds(171, 119, 96, 20);
		panelInicioSesion.add(textoPass);
		
		JTextPane txtpnAdministrador = new JTextPane();
		txtpnAdministrador.setAlignmentX(Component.RIGHT_ALIGNMENT);
		txtpnAdministrador.setFont(new Font("Nirmala UI Semilight", Font.PLAIN, 20));
		txtpnAdministrador.setOpaque(false);
		txtpnAdministrador.setEditable(false);
		txtpnAdministrador.setText("ADMINISTRADOR");
		txtpnAdministrador.setBounds(105, 15, 168, 38);
		panelInicioSesion.add(txtpnAdministrador);
		
		panelTabla.setVisible(false);
		panelConsultas.setVisible(false);
	}
	
	/*
	 * Verifica si el usuario y la contraseña ingresada es la correcta.
	 */
	private void inicioSesion() {
		if (textoUsuario.getText().compareTo("admin") != 0 || String.valueOf(textoPass.getPassword()).compareTo("admin") != 0)
				JOptionPane.showMessageDialog(null, "Usuario y/o contraseña incorrecta, por favor reingrese");
		else {
			if (conectarBD()){
				panelInicioSesion.setVisible(false);
				inicioMenu();
			}
		}
	}
	
	/*
	 * Accion para controlar la cantidad de clicks y la fila seleccionada.
	 */
	private void clickShowTables(MouseEvent evt) {
		int fila = tablaDeTablas.getSelectedRow();
		if (fila!=-1 && evt.getClickCount() == 1){
			btnSeleccionarTabla.setEnabled(true);
			tablaSelec = tablaDeTablas.getValueAt(fila, 0).toString();
		}
		
	}
	
	/*
	 * Se conecta con la base de datos para mostrar los atributos de la tabla seleccionada. 
	 */
	private void mostrarInformacionTabla() {
		try {
			String consulta = "select column_name as Atributos_Tabla_"+tablaSelec+"	 from information_schema.columns where table_name='"+tablaSelec+"'";
			Statement stmt = ConexionBD.createStatement();
			ResultSet rs = stmt.executeQuery(consulta);
			tablaDescripcionTablas.refresh(rs);
			rs.close();
			stmt.close();
			btnSeleccionarTabla.setEnabled(false);
		} catch (SQLException e) { 
			JOptionPane.showMessageDialog(this,
  	             "Se produjo un error al intentar conectarse a la base de datos.\n" + e.getMessage(),
  	              "Error", JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	/*
	 * Inicia el menu principal mostrando los paneles correspondientes.
	 */
	private void inicioMenu() {
		panelConsultas.setVisible(true);
		panelTabla.setVisible(false);
		textArea.setText("");
		btnSeleccionarTabla.setEnabled(false);
		actualizarTablas();
	}
	
	/*
	 * Realiza una consulta a la base de datos vuelos para mostrar las tablas que integran la base.
	 */
	private void actualizarTablas() {
		try {
			String consulta = "SHOW TABLES";
			Statement stmt = ConexionBD.createStatement();
			ResultSet rs = stmt.executeQuery(consulta);
			tablaDeTablas.refresh(rs);
			rs.close();
			stmt.close();
		} catch (SQLException e) { 
			JOptionPane.showMessageDialog(this,
  	             "Se produjo un error al intentar conectarse a la base de datos.\n" + e.getMessage(),
  	              "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * Sirve para verificar si la sentencia ingresada es una consulta o una modificacion de la tabla.
	 */
	private boolean computarConsulta() {
		consulta = textArea.getText();
		StringTokenizer token = new StringTokenizer(consulta);
		if (!token.hasMoreTokens())
			return false;
		String p = token.nextElement().toString().toUpperCase();
		System.out.println("el p "+p);
		if (p.compareTo("SELECT") == 0) { 
			consulta();
			return true;
		}
		else
			if (p.compareTo("INSERT") == 0||p.compareTo("CREATE") == 0||p.compareTo("DROP") == 0 ||p.compareTo("UPDATE") == 0 ) {
				modificacion();
				return true;
			}
			else
				return false;
	}
	
	/*
	 * Se conecta con la base de datos vuelos para realizar una consulta, en el caso afirmativo de la consulta, se muestra una tabla reflejando
	 * el resultado.
	 */
	private void consulta() {
		try {
			Statement stmt = ConexionBD.createStatement();
			ResultSet rs = stmt.executeQuery(consulta);
			panelTabla.setVisible(true);
			panelConsultas.setVisible(false);
			table.refresh(rs);
			//Para que muestre correctamente los valores de tipo TIME (hora)  
			for (int i = 0; i < table.getColumnCount(); i++)
	    		 if	 (table.getColumn(i).getType()==Types.TIME)  
	    			 table.getColumn(i).setType(Types.CHAR);  
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/*
	 * Se conecta con la base de datos vuelos para realizar una modificacion en ella.
	 */
	private void modificacion() {
		try {
			Statement stmt = ConexionBD.createStatement();
			stmt.execute(consulta);
			stmt.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/*
	 * Establece una conexion con la base de datos vuelos con todos los atributos correspondientes.
	 */
	private boolean conectarBD() {
		if (ConexionBD == null){               
	         try {
	            String servidor = "localhost:3306";
	            String baseDatos = "vuelos";
	            String usuario = "admin";
	            String clave = "admin";
	            String uriConexion = "jdbc:mysql://" + servidor + "/" + 
	                   baseDatos + "?serverTimezone=America/Argentina/Buenos_Aires";
	            ConexionBD = DriverManager.getConnection(uriConexion, usuario, clave);
	         }
	         catch (SQLException ex){
	            JOptionPane.showMessageDialog(this,
	             "Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(),
	              "Error", JOptionPane.ERROR_MESSAGE);
	            return false;
	         }
	      }
		return true;
	}
	
	/**
	 * Desconecta la base de datos en el caso que se haya inicializado.
	 */
	public void desconectarExterno() {
		desconectarBD();
	}
	
	/*
	 * Desconecta la base de datos vuelos.
	 */
	private void desconectarBD() {
		if (this.ConexionBD != null){
	         try {
	            this.ConexionBD.close();
	            this.ConexionBD = null;
	         }
	         catch (SQLException ex){
	            System.out.println("SQLException: " + ex.getMessage());
	            System.out.println("SQLState: " + ex.getSQLState());
	            System.out.println("VendorError: " + ex.getErrorCode());
	         }
	      }
	}
}