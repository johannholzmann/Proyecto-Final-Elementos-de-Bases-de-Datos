import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaPrincipal extends JFrame {

	private JPanel contentPane;
	private Empleado ventanaEmpleado;
	private JDesktopPane desktopPane;
	private Administrador ventanaAdministrador;
	private JMenu mnOpciones;
	private Image fondo;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal frame = new VentanaPrincipal();
					frame.setLocationRelativeTo(null);
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
	public VentanaPrincipal() {
		super();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ventanaAdministrador.desconectarExterno();
				ventanaEmpleado.desconectarExterno();
			}
		});
		setForeground(SystemColor.textHighlight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100,700,700);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnOpciones = new JMenu("Opciones");
		menuBar.add(mnOpciones);
		
		JMenuItem admin = new JMenuItem("Administrador");
		mnOpciones.add(admin);
		
		JMenuItem empleado = new JMenuItem("Empleado");
		mnOpciones.add(empleado);

		contentPane = new JPanel();
		contentPane.setForeground(SystemColor.textHighlight);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null); 
		
		desktopPane = new JDesktopPane();
		desktopPane.setBackground(SystemColor.textHighlight);
		desktopPane.setBounds(20, 0, 651, 639);
		desktopPane.setMaximumSize(getMaximumSize());
		contentPane.add(desktopPane);
		
		
		ventanaEmpleado = new Empleado();
		ventanaEmpleado.setBounds(0, 0, 640, 480);
		desktopPane.add(ventanaEmpleado);
		
		ventanaAdministrador = new Administrador();
		ventanaAdministrador.setBounds(34,55, 640, 480);
		desktopPane.add(ventanaAdministrador);
		admin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ventanaAdministrador.setVisible(true);
					ventanaAdministrador.setMaximum(true);
					ventanaEmpleado.setVisible(false);
				} catch (PropertyVetoException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		empleado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ventanaEmpleado.setVisible(true);
					ventanaEmpleado.setMaximum(true);
					ventanaAdministrador.setVisible(false);
				} catch (PropertyVetoException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		ventanaEmpleado.setVisible(false);
		ventanaAdministrador.setVisible(false);
		
		
	}	
}
