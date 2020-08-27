import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;





public class Clase extends JFrame {
	

	
	private static Logica logica;
	private JPanel contentPane;
	private JTextField textUsuario;
	private JTextPane txtpnContrasea;
	private JPanel panelInicioSesion;
	protected static Connection conexionBD = null;
	private JPasswordField passwordIngresado;
	private JOptionPane mensajeSesion;
	private JPanel panelAdmin;




	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				
				try {
					Clase frame = new Clase();
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
	
	public Clase() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 572, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panelInicioSesion = new JPanel();
		panelInicioSesion.setBounds(0,0,556,261);
		panelInicioSesion.setLayout(null);

		contentPane.add(panelInicioSesion);
		
		panelAdmin = new JPanel();
		panelAdmin.setBounds(0, 0, 556, 261);
		panelAdmin.setLayout(null);


		contentPane.add(panelAdmin);
		panelAdmin.setVisible(false);
		
		
		textUsuario = new JTextField();
		textUsuario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});

		JButton btnIniciarSesion = new JButton("Iniciar Sesion");
		btnIniciarSesion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String usua = textUsuario.getText();
				String pass = String.copyValueOf(passwordIngresado.getPassword());
				mensajeSesion = new JOptionPane();
				mensajeSesion.setOptionType(JOptionPane.OK_CANCEL_OPTION);
				mensajeSesion.setVisible(true);
				
				if (!conectarBD(usua, pass)) {
					mensajeSesion.showMessageDialog(null,"Error en el ingreso al sistema, reingrese los datos");
					textUsuario.setText(null);
					passwordIngresado.setText(null);
				}
				else {
					mensajeSesion.showMessageDialog(null,"Inicio de sesion correcto para: '"+usua+"'");
					contentPane.removeAll();
					panelAdmin.setVisible(true);
					contentPane.add(panelAdmin);
					contentPane.repaint();
					}
			}
				
		});
		
		
				
				btnIniciarSesion.setBounds(99, 142, 205, 23);
				panelInicioSesion.add(btnIniciarSesion);
		
		
		textUsuario.setBounds(203, 80, 96, 20);
		panelInicioSesion.add(textUsuario);
		textUsuario.setColumns(10);
		
		
		JTextPane txtpnUsuario = new JTextPane();
		txtpnUsuario.setEditable(false);
		txtpnUsuario.setText("Usuario");
		txtpnUsuario.setBounds(99, 80, 79, 20);
		panelInicioSesion.add(txtpnUsuario);
		txtpnUsuario.setOpaque(false);
		
		txtpnContrasea = new JTextPane();
		txtpnContrasea.setEditable(false);
		txtpnContrasea.setText("Contrase\u00F1a");
		txtpnContrasea.setBounds(99, 111, 79, 20);
		panelInicioSesion.add(txtpnContrasea);
		txtpnContrasea.setOpaque(false);
		
		passwordIngresado = new JPasswordField();
		passwordIngresado.setBounds(203, 111, 96, 20);
		panelInicioSesion.add(passwordIngresado);
		
		

	}

	
	
	
	
	
	
	
	
	
	

	public void iniciar(String usuario) {
		
		System.out.println("holaasdas");
		desconectarBD();
		
	}

	
	private static boolean conectarBD(String us,String p) {
		
		if (conexionBD == null)
		{
			
			
			try {
				String servidor,BD,user,pass,conexion;
				servidor = "localhost:3306";
				BD = "vuelos";
				user = us;
				pass = p;
				conexion = "jdbc:mysql://" + servidor + "/" + BD + "?serverTimezone=America/Argentina/Buenos_Aires";
				
				conexionBD = DriverManager.getConnection(conexion,user,pass);
				return true;
			}
			
			catch(SQLException excep) {
				System.out.println("SQL Exception "+excep.getMessage());
				System.out.println("SQL Code Error "+excep.getErrorCode());
				System.out.println("SQL State "+excep.getSQLState());
			}
			
		}
		return false;
	}
	
	private static void desconectarBD() {
		
		
		if (conexionBD != null) {
			
			
			try {
				conexionBD.close();
				conexionBD = null;		
			}
			
			catch(SQLException excep) {
				System.out.println("SQL Exception "+excep.getMessage());
				System.out.println("SQL Code Error "+excep.getErrorCode());
				System.out.println("SQL State "+excep.getSQLState());
			}
		}
	}
}