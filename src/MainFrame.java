

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JLabel lblUrl;
	private JTextField tfUrl;
	private JLabel lblID;
	private JTextField tfID;
	private JLabel lblPassword;
	private JTextField tfPassword;
	private JLabel lblDBName;
	private JTextField tfDBName;
	private JLabel lblPath;
	private JTextField tfPath;
	private JLabel lblffmpegPath;
	private JTextField tfffmpegPath;
	private JButton btSubmit;
	private JScrollPane scrollPane;
	private JTextArea taResult;
	private JLabel exeInfo;
	private List<Map<String,String>> fileList= new ArrayList<Map<String,String>>();
    //final private String driver = "oracle.jdbc.driver.OracleDriver";
	final private String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	final private String convert = "-i ${fromPath} -c:v libx264 -preset ultrafast -qp 18 ${toPath}";
	final private Runtime rt = Runtime.getRuntime();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
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
	public MainFrame() {
		setTitle("Ex4-3");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 530);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblDBName = new JLabel("DB");
		lblDBName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDBName.setBounds(10, 51, 46, 15);
		contentPane.add(lblDBName);
		
		tfDBName = new JTextField();
		tfDBName.setBounds(84, 38, 265, 21);
		contentPane.add(tfDBName);
		
		
		
		lblPassword = new JLabel("PWD");
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setBounds(10, 81, 46, 15);
		contentPane.add(lblPassword);
		
		tfPassword = new JTextField();
		tfPassword.setBounds(84, 78, 265, 21);
		contentPane.add(tfPassword);
		
		
		lblID = new JLabel("ID");
		lblID.setHorizontalAlignment(SwingConstants.RIGHT);
		lblID.setBounds(10, 111, 46, 15);
		contentPane.add(lblID);
		
		tfID = new JTextField();
		tfID.setBounds(84, 108, 265, 21);
		contentPane.add(tfID);
		
		
		lblUrl = new JLabel("IP");
		lblUrl.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUrl.setBounds(10, 141, 46, 15);
		contentPane.add(lblUrl);
		
		tfUrl = new JTextField();
		tfUrl.setBounds(84, 138, 265, 21);
		contentPane.add(tfUrl);
		
		lblPath = new JLabel("PATH");
		lblPath.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPath.setBounds(10, 171, 46, 15);
		contentPane.add(lblPath);
		
		tfPath = new JTextField();
		tfPath.setColumns(10);
		tfPath.setBounds(84, 168, 265, 21);
		contentPane.add(tfPath);
		
		lblffmpegPath = new JLabel("ffmpeg");
		lblffmpegPath.setHorizontalAlignment(SwingConstants.RIGHT);
		lblffmpegPath.setBounds(10, 201, 46, 15);
		contentPane.add(lblffmpegPath);
		
		tfffmpegPath = new JTextField();
		tfffmpegPath.setColumns(10);
		tfffmpegPath.setBounds(84, 198, 265, 21);
		contentPane.add(tfffmpegPath);
		
		btSubmit = new JButton("GO");
		btSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				fileList.clear();
				taResult.setText("");
				
				InputInfoVO input = new InputInfoVO();
				input.setUrl(tfUrl.getText());
				input.setId(tfID.getText());
				input.setPwd(tfPassword.getText());
				input.setDbName(tfDBName.getText());
				input.setPath(tfPath.getText());
				input.setFfmpeg(tfffmpegPath.getText());
				
				exeInfo.setText("°õ¦æ¤¤");
				BackgroundThread thread = new BackgroundThread(btSubmit,taResult,input,exeInfo);
				thread.start();
				
			}//btSubmit action 
		});
		btSubmit.setBounds(350, 197, 60, 23);
		contentPane.add(btSubmit);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(50, 230, 500, 200);
		contentPane.add(scrollPane);
		
		
		taResult = new JTextArea();
		scrollPane.setViewportView(taResult);
		
		exeInfo = new JLabel("");
		exeInfo.setHorizontalAlignment(SwingConstants.RIGHT);
		exeInfo.setBounds(50, 450, 60, 15);
		contentPane.add(exeInfo);
	} 
}
