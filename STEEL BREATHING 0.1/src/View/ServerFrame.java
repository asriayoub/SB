package View;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ServerFrame extends JFrame{

	public ServerFrame(){
		setSize(300, 500);
		setTitle("SERVER SB 0.1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		
		
		JPanel panel= new JPanel();
		panel.setBackground(Color.BLACK);

		JLabel launched = new JLabel();
		launched.setForeground(Color.WHITE);
		launched.setText("SERVER LAUNCHED...");
		

		JLabel exit = new JLabel();
		exit.setForeground(Color.WHITE);
		exit.setText("Close to exit");
		

		JLabel merci = new JLabel();
		merci.setForeground(Color.WHITE);
		merci.setText("A special thanks to the man who runs the Server!");
		
		
		setContentPane(panel);
		add(launched);
		add(merci);
		add(exit);
		
		
		
		setVisible(true);
		
		
	}
}
