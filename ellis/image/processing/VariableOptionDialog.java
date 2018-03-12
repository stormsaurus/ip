package ellis.image.processing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 *
 *  @author Ellis Teer 7/07/99
 *  @see ImageUtilities
 */
public class VariableOptionDialog extends JDialog implements ActionListener{

	private Vector			variableNames;
	private Vector			variableDefaultValues;
	private Vector			variableJTextFields;

	private Vector			optionNames;
	private Vector			optionDefaultValues;
	private Vector			optionJCheckBoxes;

	private Container		contentPane;

	private JPanel			variablesPanel;
	private JPanel			optionsPanel;
	private JPanel			flowOptionsPanel;
	private JPanel			buttonRestoreDefaultsPanel;
	private JPanel			buttonsPanel;
	private JButton			restoreDefaultsButton;
	private JButton			okButton;
	private JButton			cancelButton;

	private boolean			canceled;

	public VariableOptionDialog(Frame owner, String title){
		super(owner, title, true);
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		canceled = true;
		this.addWindowListener( new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				canceled = true;
				setVisible(false);
			}
		});
		contentPane = getContentPane();

		okButton = new JButton("OK");
		okButton.addActionListener( this );
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener( this );
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout( new FlowLayout(FlowLayout.CENTER) );
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);

		restoreDefaultsButton = new JButton("Restore Defaults");
		restoreDefaultsButton.addActionListener( this );
		buttonRestoreDefaultsPanel = new JPanel();
		buttonRestoreDefaultsPanel.setLayout( new FlowLayout(FlowLayout.CENTER) );
		buttonRestoreDefaultsPanel.add(restoreDefaultsButton);

		variableNames 			= new Vector();
		variableDefaultValues 	= new Vector();
		variableJTextFields 	= new Vector();
		optionNames 			= new Vector();
		optionDefaultValues 	= new Vector();
		optionJCheckBoxes 		= new Vector();

		variablesPanel = new JPanel();
		variablesPanel.setLayout( new BoxLayout( variablesPanel, BoxLayout.Y_AXIS) );
		variablesPanel.setBorder( new TitledBorder("Variables") );

		flowOptionsPanel = new JPanel();
		flowOptionsPanel.setLayout( new FlowLayout(FlowLayout.CENTER) );
		flowOptionsPanel.setBorder( new TitledBorder("Options") );
		optionsPanel = new JPanel();
		optionsPanel.setLayout( new BoxLayout( optionsPanel, BoxLayout.Y_AXIS) );
		flowOptionsPanel.add( optionsPanel );

		contentPane.setLayout( new BoxLayout(contentPane, BoxLayout.Y_AXIS) );
		contentPane.add( variablesPanel );
		contentPane.add( flowOptionsPanel );
		contentPane.add( buttonRestoreDefaultsPanel );
		contentPane.add( buttonsPanel );

		flowOptionsPanel.setVisible(false);
		variablesPanel.setVisible(false);
	}

	public void addVariable(String name, String defaultValue){

		if( variableNames.size()==0 ) variablesPanel.setVisible(true);

		JTextField field = new JTextField( defaultValue.toString(), 10 );

		variableNames.add( name );
		variableDefaultValues.add( defaultValue );
		variableJTextFields.add( field);

		variablesPanel.add( Box.createVerticalStrut(3) );
		variablesPanel.add( new JLabel( name ) );
		variablesPanel.add( field );
		variablesPanel.add( Box.createVerticalStrut(3) );
	}

	public void addOption( String name, boolean defaultValue ){

		if( optionNames.size()==0 ) flowOptionsPanel.setVisible(true);

		JCheckBox box = new JCheckBox( name, defaultValue);

		optionNames.add( name );
		optionDefaultValues.add( new Boolean(defaultValue) );
		optionJCheckBoxes.add( box );

		optionsPanel.add( box );
	}

	public String getValue( String varName ){

		int index = variableNames.indexOf( varName );

		return ( (JTextField)variableJTextFields.get(index) ).getText();

	}

	public boolean isOptionSelected( String optionName ){

		int index = optionNames.indexOf( optionName );
		return ( (JCheckBox)optionJCheckBoxes.get(index) ).isSelected();
	}

	public boolean isCanceled(){
		return canceled;
	}

	public void centerDialog(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( screenSize.width/2 - this.getWidth()/2, screenSize.height/2 - this.getHeight()/2);
	}

	public void actionPerformed( ActionEvent e ){

		Object source = e.getSource();

		if ( source==okButton ){
			canceled = false;
			this.setVisible( false );
		}

		if ( source==restoreDefaultsButton ){
			for(int i=0; i<variableJTextFields.size(); i++){
		 		( (JTextField)variableJTextFields.get(i) ).setText( (String)variableDefaultValues.get(i) );
			}
			this.invalidate();
		}

		if ( source==cancelButton ){
			canceled = true;
			this.setVisible( false );
		}

	}

}
