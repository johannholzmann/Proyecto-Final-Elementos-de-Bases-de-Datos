import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import java.awt.event.*;

import javax.swing.text.MaskFormatter;

import quick.dbtable.DBTable;

import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.sql.Date;
import javax.swing.JTable;
import javax.swing.JPasswordField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JSeparator;

public class Empleado extends JInternalFrame {

	private Connection ConexionBD = null;
	private JFormattedTextField textoFechaIda;
	private JFormattedTextField textoFechaVuelta;
	private JPanel panelBuscarVuelos;
	private JRadioButton rdbtnIda,rdbtnIdaYVuelta;
	private JComboBox comboBoxOrigen,comboBoxDestino;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton botonBuscarVuelos;
	private boolean viajeVuelta;
	private Date dateIda,dateVuelta;
	private JCheckBox confirmarIda,confirmarVuelta;
	private boolean activarBoton [];
	private DBTable tablaAeropuertos;
	private String aeropuerto_salida,aeropuerto_llegada;
	private JPanel panelInformacionVuelos;
	private DBTable tablaVuelos,tablaClases,tablaVuelosVuelta,tablaClasesVuelta;
	private JButton btnMenuPrincipal;
	private JPanel panelIniciarSesion;
	private JPasswordField password;
	private JTextField usuario;
	private DBTable tablaUsuarios;
	private JTextPane txtpnVuelosDeIda;
	private JTextPane txtpnInformacionDelVuelo;
	private JButton btnIniciarSesion;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Empleado frame = new Empleado();
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
	public Empleado() {

		setBounds(100, 100, 640, 497);
		getContentPane().setLayout(null);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		setClosable(true);
		addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				System.out.println("y");
				desconectarBD();
			}
			public void componentShown(ComponentEvent e) {
				if (!conectarBD())
					btnIniciarSesion.setEnabled(false);
			}
		});
		
		
				panelIniciarSesion = new JPanel();
				panelIniciarSesion.setBounds(0, 0, 507, 275);
				getContentPane().add(panelIniciarSesion);
				panelIniciarSesion.setLayout(null);
				
				password = new JPasswordField();
				password.setBounds(174, 139, 143, 20);
				panelIniciarSesion.add(password);
				
				usuario = new JTextField();
				usuario.setBounds(174, 97, 143, 20);
				panelIniciarSesion.add(usuario);
				usuario.setColumns(10);
				
				JTextPane txtpnUsuario = new JTextPane();
				txtpnUsuario.setOpaque(false);
				txtpnUsuario.setEditable(false);
				txtpnUsuario.setText("Usuario");
				txtpnUsuario.setBounds(79, 97, 85, 20);
				panelIniciarSesion.add(txtpnUsuario);
				
				JTextPane txtpnContrasea = new JTextPane();
				txtpnContrasea.setOpaque(false);
				txtpnContrasea.setEditable(false);
				txtpnContrasea.setText("Contrase\u00F1a");
				txtpnContrasea.setBounds(79, 139, 85, 20);
				panelIniciarSesion.add(txtpnContrasea);
				
				btnIniciarSesion = new JButton("Iniciar Sesion");
				btnIniciarSesion.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						verificarUsuario();
					}
				});
				btnIniciarSesion.setBounds(79, 189, 238, 23);
				panelIniciarSesion.add(btnIniciarSesion);
		iniciarPanelInicioSesion();

		
	}

	private void iniciarPanelInicioSesion() {
		tablaUsuarios = new DBTable();
		
	}
	
	
	private void iniciarEmpleado() {
		
		activarBoton = new boolean [3];
		iniciarPanelBuscarVuelos();
		iniciarPanelInformacionVuelos();
		iniciarTablasAeropuertos();
		
		panelBuscarVuelos.setVisible(true);
		panelInformacionVuelos.setVisible(false);
		inicio();
		
	}
	
	private void verificarUsuario() {

		String user,pass;
		user = usuario.getText();
		pass = String.copyValueOf(password.getPassword());
		
		Statement stmt ;
		try {
			stmt = ConexionBD.createStatement();
			String sql = "SELECT * FROM empleados ";
			sql+="where legajo='"+user+"' and password = md5('"+pass+"')";
			System.out.println("CONSULTA "+sql);
			ResultSet rs = stmt.executeQuery(sql);
			tablaUsuarios.refresh(rs);
			int fila = tablaUsuarios.getRowCount();
			if (fila > 0)
				iniciarEmpleado();
			else
		        JOptionPane.showMessageDialog(null,"Legajo y/o contraseña incorrecta, reingrese por favor","Error de Inicio de Sesion",JOptionPane.ERROR_MESSAGE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
	}

	private void iniciarPanelBuscarVuelos() {
		
		tablaAeropuertos = new DBTable();
		panelBuscarVuelos = new JPanel();
		panelBuscarVuelos.setBounds(0, 0, 624, 442);
		getContentPane().add(panelBuscarVuelos);
		panelBuscarVuelos.setLayout(null);
		
		panelBuscarVuelos.setVisible(true); //CAMBIAR POR FALSO 
		
		rdbtnIda = new JRadioButton("Ida");		
		buttonGroup.add(rdbtnIda);
		rdbtnIda.setBounds(54, 44, 65, 23);
		panelBuscarVuelos.add(rdbtnIda);
		
		rdbtnIdaYVuelta = new JRadioButton("Ida y Vuelta");
		buttonGroup.add(rdbtnIdaYVuelta);
		rdbtnIdaYVuelta.setBounds(185, 44, 130, 23);
		panelBuscarVuelos.add(rdbtnIdaYVuelta);
		
		comboBoxOrigen = new JComboBox();
		comboBoxOrigen.setToolTipText("Origen");
		comboBoxOrigen.setBounds(54, 144, 216, 22);
		panelBuscarVuelos.add(comboBoxOrigen);
		
		comboBoxDestino = new JComboBox();
		comboBoxDestino.setBounds(322, 144, 216, 22);
		panelBuscarVuelos.add(comboBoxDestino);
		
		try {
			textoFechaIda = new JFormattedTextField(new MaskFormatter("##'/##'/####"));
			textoFechaIda.setBackground(new Color(135, 206, 235));
			textoFechaIda.setToolTipText("Fecha Ida");
			textoFechaVuelta = new JFormattedTextField(new MaskFormatter("##'/##'/####"));
			textoFechaVuelta.setBackground(new Color(135, 206, 235));
			textoFechaVuelta.setToolTipText("Fecha Vuelta");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		textoFechaIda.setBounds(54, 97, 96, 20);
		panelBuscarVuelos.add(textoFechaIda);
		//textoFechaIda.setColumns(10);
		
		textoFechaVuelta.setBounds(318, 97, 96, 20);
		panelBuscarVuelos.add(textoFechaVuelta);
		textoFechaVuelta.setColumns(10);
		//textoFechaVuelta.setEnabled(false);
		
		botonBuscarVuelos = new JButton("Buscar Vuelos");
		botonBuscarVuelos.setFont(new Font("Tahoma", Font.PLAIN, 14));
		botonBuscarVuelos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accionBuscarVuelos();					
			}
		});
		botonBuscarVuelos.setBounds(185, 211, 216, 41);
		panelBuscarVuelos.add(botonBuscarVuelos);
		
		confirmarIda = new JCheckBox("");
		confirmarIda.setBounds(159, 96, 45, 23);
		panelBuscarVuelos.add(confirmarIda);
		
		confirmarVuelta = new JCheckBox("");
		confirmarVuelta.setBounds(420, 96, 97, 23);
		panelBuscarVuelos.add(confirmarVuelta);
		

		confirmarIda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fecha = textoFechaIda.getText().trim();
				System.out.println("mostrar j "+fecha);
				 dateIda = null;
				if (!Fechas.validar(fecha)) {
					confirmarIda.setSelected(false);
					JOptionPane.showMessageDialog(null,
							"En el campo fecha debe ingresar dd/mm/aaaa",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
				else {
					dateIda = Fechas.convertirStringADateSQL(fecha);
					textoFechaIda.setEnabled(false);
					confirmarIda.setEnabled(false);
					if (rdbtnIdaYVuelta.isSelected()) {
						textoFechaVuelta.setEnabled(true);
						confirmarVuelta.setEnabled(true);
					}
					else
						activaryCheck(0);
				}
			}
		});
		
		

		confirmarVuelta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fecha = textoFechaVuelta.getText().trim();
				 dateVuelta = null;
				if (!Fechas.validar(fecha)) {
					confirmarVuelta.setSelected(false);
					textoFechaVuelta.setText("");
					JOptionPane.showMessageDialog(null,
							"En el campo fecha debe ingresar dd/mm/aaaa",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
				else {
					dateVuelta = Fechas.convertirStringADateSQL(fecha);
					if (dateVuelta.before(dateIda)) {
						JOptionPane.showMessageDialog(null,
								"La fecha de vuelta debe ser posterior a la fecha de ida",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						confirmarVuelta.setSelected(false);
						textoFechaVuelta.setText("");
					}
					else {
						textoFechaVuelta.setEnabled(false);
						confirmarVuelta.setEnabled(false);
						rdbtnIda.setEnabled(false);
						rdbtnIdaYVuelta.setEnabled(false);
						activaryCheck(0);
					}
				}
			}
		});
		


		//ESTO VA EN METODO ACCIONBUSCARVUELOS
		/////////////
	}
	
	private void iniciarPanelInformacionVuelos() {
		
		btnMenuPrincipal = new JButton("Reiniciar Busqueda");
		btnMenuPrincipal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inicio();
			}
		});
		btnMenuPrincipal.setBounds(10, 444, 604, 23);
		getContentPane().add(btnMenuPrincipal);
		btnMenuPrincipal.setVisible(false);
		
		panelInformacionVuelos = new JPanel();
		panelInformacionVuelos.setBounds(0, 0, 624, 442);
		getContentPane().add(panelInformacionVuelos);
		panelInformacionVuelos.setLayout(null);

		tablaVuelos = new DBTable();
		tablaVuelos.setEditable(false);
		tablaVuelos.setBounds(10, 25, 553, 94);
		panelInformacionVuelos.add(tablaVuelos);
		
		tablaClases = new DBTable();
		tablaClases.setEditable(false);
		tablaClases.setBounds(10, 142, 553, 77);
		panelInformacionVuelos.add(tablaClases);
		

		tablaVuelosVuelta = new DBTable();
		tablaVuelosVuelta.setEditable(false);
		tablaVuelosVuelta.setBounds(10, 248, 553, 94);
		panelInformacionVuelos.add(tablaVuelosVuelta);
		
		tablaClasesVuelta = new DBTable();
		tablaClasesVuelta.setEditable(false);
		tablaClasesVuelta.setBounds(10, 358, 553, 82);
		panelInformacionVuelos.add(tablaClasesVuelta);
		
		txtpnVuelosDeIda = new JTextPane();
		txtpnVuelosDeIda.setFont(new Font("Sitka Heading", Font.PLAIN, 13));
		txtpnVuelosDeIda.setOpaque(false);
		txtpnVuelosDeIda.setEditable(false);
		txtpnVuelosDeIda.setText("Vuelos de Ida (Doble click para seleccionar un vuelo y ver informacion del mismo)");
		txtpnVuelosDeIda.setBounds(10, 7, 500, 20);
		panelInformacionVuelos.add(txtpnVuelosDeIda);
		
		txtpnInformacionDelVuelo = new JTextPane();
		txtpnInformacionDelVuelo.setText("Informacion del Vuelo seleccionado");
		txtpnInformacionDelVuelo.setOpaque(false);
		txtpnInformacionDelVuelo.setFont(new Font("Sitka Heading", Font.PLAIN, 13));
		txtpnInformacionDelVuelo.setEditable(false);
		txtpnInformacionDelVuelo.setBounds(10, 121, 218, 20);
		panelInformacionVuelos.add(txtpnInformacionDelVuelo);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 228, 624, 2);
		panelInformacionVuelos.add(separator);
		
		JTextPane txtpnVuelosDeVuelta = new JTextPane();
		txtpnVuelosDeVuelta.setText("Vuelos de Vuelta (Doble click para seleccionar un vuelo y ver informacion del mismo)");
		txtpnVuelosDeVuelta.setOpaque(false);
		txtpnVuelosDeVuelta.setFont(new Font("Sitka Heading", Font.PLAIN, 13));
		txtpnVuelosDeVuelta.setEditable(false);
		txtpnVuelosDeVuelta.setBounds(10, 230, 500, 20);
		panelInformacionVuelos.add(txtpnVuelosDeVuelta);
		
		JTextPane textPane = new JTextPane();
		textPane.setText("Informacion del Vuelo seleccionado");
		textPane.setOpaque(false);
		textPane.setFont(new Font("Sitka Heading", Font.PLAIN, 13));
		textPane.setEditable(false);
		textPane.setBounds(10, 340, 218, 20);
		panelInformacionVuelos.add(textPane);
		
		
		
	}
	
	private void inicio() {
		
		btnMenuPrincipal.setVisible(true);
		aeropuerto_llegada = aeropuerto_salida = "";
		viajeVuelta = false;
		for (int j = 0 ; j < 3; j++)
			activarBoton[j] = false;
		panelIniciarSesion.setVisible(false);
		panelBuscarVuelos.setVisible(true);
		panelInformacionVuelos.setVisible(false);
		botonBuscarVuelos.setEnabled(false);
		textoFechaIda.setEnabled(true);
		textoFechaIda.setText("");
		textoFechaVuelta.setText("");
		rdbtnIda.setSelected(false);
		rdbtnIdaYVuelta.setSelected(false);
		rdbtnIda.setEnabled(true);
		rdbtnIdaYVuelta.setEnabled(true);
		comboBoxOrigen.setEnabled(true);
		comboBoxDestino.setEnabled(true);
		comboBoxOrigen.setSelectedItem(comboBoxOrigen.getItemAt(0));
		comboBoxDestino.setSelectedItem(comboBoxDestino.getItemAt(0));
		panelBuscarVuelos.setVisible(true);
		textoFechaVuelta.setEnabled(false);
		confirmarIda.setEnabled(true);
		confirmarVuelta.setEnabled(false);
		confirmarIda.setSelected(false);
		confirmarVuelta.setSelected(false);

		
		
		
		rdbtnIda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnIda.setEnabled(false);
				rdbtnIdaYVuelta.setEnabled(false);
				textoFechaVuelta.setEnabled(false);
				confirmarVuelta.setEnabled(false);
				if (confirmarIda.isSelected())
					activaryCheck(0);
			}
		});
		
		
		
		rdbtnIdaYVuelta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viajeVuelta = true;
				rdbtnIda.setEnabled(false);
				rdbtnIdaYVuelta.setEnabled(false);
				textoFechaVuelta.setEnabled(true);
				if (confirmarIda.isSelected())
					confirmarVuelta.setEnabled(true);
				}
				
		});
		

		
		
	}

	private void activaryCheck(int i) {
		activarBoton[i] = true;
		boolean es = true;
		for (int j =0 ; j < 3 ; j++)
			if (!activarBoton[j])
				es = false;
		if (es)
			botonBuscarVuelos.setEnabled(true);
		
	}
	
	private void iniciarTablasAeropuertos() {
		
		Statement stmt;
		
		try {
			stmt = ConexionBD.createStatement();
			String consulta = "SELECT nombre FROM aeropuertos";
			
			ResultSet rs = stmt.executeQuery(consulta);
			tablaAeropuertos.refresh(rs);
			rs.close();
			stmt.close();
			
			
			informacionTablasAeropuertos();
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,"Error : "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
		
		
		
	}
	
	private void informacionTablasAeropuertos() {	
		int filas = tablaAeropuertos.getRowCount();
		comboBoxOrigen.addItem("ORIGEN");
		comboBoxDestino.addItem("DESTINO");
		for (int i = 0 ; i < filas ; i++) {
			String aero = tablaAeropuertos.getValueAt(i, 0).toString();
			comboBoxOrigen.addItem(aero);
			comboBoxDestino.addItem(aero);				
		}

		comboBoxOrigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String aero = comboBoxOrigen.getSelectedItem().toString();
				if (aero.compareTo("ORIGEN")!= 0 ) {
					comboBoxOrigen.setEnabled(false);
					aeropuerto_salida = aero;
					activaryCheck(1);
				}
			}
		});
		
		comboBoxDestino.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String aero = comboBoxDestino.getSelectedItem().toString();
				if (aero.compareTo("DESTINO")!= 0 ) {
					comboBoxDestino.setEnabled(false);
					aeropuerto_llegada = aero;
					activaryCheck(2);
				}
			}
		});
	}
	
	private void accionBuscarVuelos() {


		panelInformacionVuelos.setVisible(true);
		panelBuscarVuelos.setVisible(false);
		tablaClases.setVisible(false);
		tablaClasesVuelta.setVisible(false);
		actualizarTablaVuelos(aeropuerto_salida,aeropuerto_llegada,dateIda,0);
		if (viajeVuelta)
			actualizarTablaVuelos(aeropuerto_llegada,aeropuerto_salida,dateVuelta,1);
		
		tablaVuelos.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent evt) {
				int fila = tablaVuelos.getSelectedRow();
				if (fila != -1 && evt.getClickCount() == 2)
					accionMostrarClases(fila,false);
			}
		});
		
		
		tablaVuelosVuelta.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent evt) {
				int fila = tablaVuelosVuelta.getSelectedRow();
				if (fila != -1 && evt.getClickCount() == 2 && viajeVuelta)
					accionMostrarClases(fila,true);
			}
		});
	}
	
	
	private void actualizarTablaVuelos(String as,String al,Date fecha,int i) {

		Statement stmt;
		
		try {
			
			stmt = ConexionBD.createStatement();
			String consulta = "SELECT DISTINCT vuelo as Vuelo, fecha as Fecha, aer_sal as Origen, hs as Hora_Salida, aer_lle as Destino, hl as Hora_Llegada";
			consulta+=", mod_av as Modelo_Avion, time_est as Tiempo_Estimado FROM vuelos_disponibles ";
			consulta+="where aer_sal='"+as+"' and aer_lle='"+al+"'";
			consulta+=" and fecha='"+fecha+"'";
			ResultSet rs = stmt.executeQuery(consulta);
			if (i == 0)
				tablaVuelos.refresh(rs);
			else
				tablaVuelosVuelta.refresh(rs);
			refrescarTabla(i);
			int fila = tablaVuelosVuelta.getRowCount();
			System.out.println("MOSTRANDO i y fila"+i+" f "+fila);
			if (!viajeVuelta) 
				tablaVuelosVuelta.setVisible(false);
			else
				tablaVuelosVuelta.setVisible(true);
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void refrescarTabla(int j) {
		
		if (j ==0) {
			 for (int i = 0; i < tablaVuelos.getColumnCount(); i++) // para que muestre correctamente los valores de tipo TIME (hora)  
	   		 if	 (tablaVuelos.getColumn(i).getType()==Types.TIME)  
	   		    tablaVuelos.getColumn(i).setType(Types.CHAR);  
		}
		else
			 for (int i = 0; i < tablaVuelosVuelta.getColumnCount(); i++) // para que muestre correctamente los valores de tipo TIME (hora)  
		   		 if	 (tablaVuelosVuelta.getColumn(i).getType()==Types.TIME)  
		   		    tablaVuelosVuelta.getColumn(i).setType(Types.CHAR);  
			
         }
		
	private void accionMostrarClases(int fila,boolean vuelta) {
		
		Statement stmt;
		
		try {
			stmt = ConexionBD.createStatement();
			String vuelo;
			Date fecha;
			if (!vuelta) {
				vuelo = tablaVuelos.getValueAt(fila, 0).toString();
				fecha = dateIda;
			}
			else
			{
				vuelo = tablaVuelosVuelta.getValueAt(fila, 0).toString();
				fecha = dateVuelta;
			}
			
			String consulta = "SELECT vuelo as Vuelo, clase as Clase, cant_disp as Asientos_Disponibles, precio as Precio ";
			consulta+="FROM vuelos_disponibles ";
			consulta+="where vuelo='"+vuelo+"' and fecha ='"+fecha+"'";
			
			ResultSet rs = stmt.executeQuery(consulta);
			if (!vuelta) {
				tablaClases.setVisible(true);
				tablaClases.refresh(rs);
			}
			else {
				tablaClasesVuelta.setVisible(true);
				tablaClasesVuelta.refresh(rs);
			}

			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	

	private boolean conectarBD() {
		System.out.println("CONECTANDO EMPLEADO");
		if (ConexionBD == null)
	      {               
	         try
	         {
	            String servidor = "localhost:3306";
	            String baseDatos = "vuelos";
	            String usuario = "admin";
	            String clave = "admin";
	            String uriConexion = "jdbc:mysql://" + servidor + "/" + 
	                   baseDatos + "?serverTimezone=America/Argentina/Buenos_Aires";
	            
	            ConexionBD = DriverManager.getConnection(uriConexion, usuario, clave);
	         }
	         catch (SQLException ex)
	         {
	            JOptionPane.showMessageDialog(this,
	             "Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(),
	              "Error", JOptionPane.ERROR_MESSAGE);
	            return false;
	         }
	      }
		return true;
		
	}
	
	public void desconectarExterno() {
		desconectarBD();
	}
	
	private void desconectarBD() {
		System.out.println("DESCONECTANDO EMPLEADO");
		if (this.ConexionBD != null)
	      {
	         try
	         {
	            this.ConexionBD.close();
	            this.ConexionBD = null;
	         }
	         catch (SQLException ex)
	         {
	            System.out.println("SQLException: " + ex.getMessage());
	            System.out.println("SQLState: " + ex.getSQLState());
	            System.out.println("VendorError: " + ex.getErrorCode());
	         }
	      }
	}
}
