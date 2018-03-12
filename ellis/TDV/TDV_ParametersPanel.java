import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


public class TDV_ParametersPanel extends JPanel implements ChangeListener{

	private TDV_DomainRendererPanel	renderer;
	private TDV_MapperPanel			mapper;
	
	private GridBagLayout			gridbag;
	private GridBagConstraints		gbc;

	//Controls
	private JSlider					phaseSlider;
		private	JLabel					phaseValueLabel;
	private JSlider					monolayerThicknessSlider;
		private	JLabel					monolayerThicknessValueLabel;
	private JSlider					tiltAngleSlider;
		private	JLabel					tiltAngleValueLabel;
	private JSlider					analyzerAngleSlider;
		private	JLabel					analyzerAngleValueLabel;
	private JSlider					laserWavelengthSlider;
		private	JLabel					laserWavelengthValueLabel;
		
	private JSlider					multiplierSlider;
		private	JLabel					multiplierValueLabel;
	
	public TDV_ParametersPanel(TDV_DomainRendererPanel renderer, TDV_MapperPanel mapper){
		this.super();
		this.renderer = renderer;
		this.mapper = mapper;

		gridbag = new GridBagLayout();
		gbc 	= new GridBagConstraints();
		gbc.insets = new Insets(10,5,10,5);			
		this.setLayout( gridbag );

		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.weightx		= 1;
//		gbc.gridwidth	= 1;
		JLabel phaseLabel = new JLabel("Tilt Azimuth Angle (degrees)");
		gridbag.setConstraints(phaseLabel, gbc);
		add(phaseLabel);

		gbc.gridx		= 0;
		gbc.gridy		= 1;
//		gbc.gridwidth	= 5;
		gbc.weightx		= 5;
		gbc.fill 		= GridBagConstraints.HORIZONTAL;
		phaseSlider = new JSlider(JSlider.HORIZONTAL,0,360,0);
		phaseSlider.setMajorTickSpacing(90);
		phaseSlider.setMinorTickSpacing(30);
		phaseSlider.setPaintTicks(true);
		phaseSlider.setPaintLabels(true);
		phaseSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		phaseSlider.addChangeListener(this);
		phaseSlider.addChangeListener(mapper);
		phaseSlider.addChangeListener(renderer);
		gridbag.setConstraints(phaseSlider, gbc);
		add(phaseSlider);

		gbc.gridx		= 1;
		gbc.gridy		= 0;
		gbc.weightx		= 1;
		phaseValueLabel = new JLabel( Integer.toString(phaseSlider.getValue()) );
		gridbag.setConstraints(phaseValueLabel, gbc);
		add(phaseValueLabel);

		gbc.gridx		= 0;
		gbc.gridy		= 2;
		gbc.weightx		= 1;
		JLabel thicknessLabel = new JLabel("Monolayer Thickness (A)");
		gridbag.setConstraints(thicknessLabel, gbc);
		add(thicknessLabel);
		
		
		gbc.gridx		= 0;
		gbc.gridy		= 3;
		gbc.weightx		= 5;
		gbc.fill 		= GridBagConstraints.HORIZONTAL;
		monolayerThicknessSlider = new JSlider(JSlider.HORIZONTAL,1,100,30);
		monolayerThicknessSlider.setMajorTickSpacing(25);
//		monolayerThicknessSlider.setMinorTickSpacing();
		monolayerThicknessSlider.setPaintTicks(true);
		monolayerThicknessSlider.setPaintLabels(true);
		monolayerThicknessSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		monolayerThicknessSlider.addChangeListener(this);
		monolayerThicknessSlider.addChangeListener(mapper);
		monolayerThicknessSlider.addChangeListener(renderer);
		gridbag.setConstraints(monolayerThicknessSlider, gbc);
		add(monolayerThicknessSlider);

		gbc.gridx		= 1;
		gbc.gridy		= 2;
		gbc.weightx		= 1;
		monolayerThicknessValueLabel = new JLabel( Integer.toString(monolayerThicknessSlider.getValue()) );
		gridbag.setConstraints(monolayerThicknessValueLabel, gbc);
		add(monolayerThicknessValueLabel);

		gbc.gridx		= 0;
		gbc.gridy		= 4;
		gbc.weightx		= 1;
		JLabel tiltAngleLabel = new JLabel("Tilt Angle (degrees)");
		gridbag.setConstraints(tiltAngleLabel, gbc);
		add(tiltAngleLabel);
		
		
		gbc.gridx		= 0;
		gbc.gridy		= 5;
		gbc.weightx		= 5;
		gbc.fill 		= GridBagConstraints.HORIZONTAL;
		tiltAngleSlider = new JSlider(JSlider.HORIZONTAL,0,90,20);
		tiltAngleSlider.setMajorTickSpacing(30);
		tiltAngleSlider.setMinorTickSpacing(10);
		tiltAngleSlider.setPaintTicks(true);
		tiltAngleSlider.setPaintLabels(true);
		tiltAngleSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		tiltAngleSlider.addChangeListener(this);
		tiltAngleSlider.addChangeListener(mapper);
		tiltAngleSlider.addChangeListener(renderer);
		gridbag.setConstraints(tiltAngleSlider, gbc);
		add(tiltAngleSlider);

		gbc.gridx		= 1;
		gbc.gridy		= 4;
		gbc.weightx		= 1;
		tiltAngleValueLabel = new JLabel( Integer.toString(tiltAngleSlider.getValue()) );
		gridbag.setConstraints(tiltAngleValueLabel, gbc);
		add(tiltAngleValueLabel);

		gbc.gridx		= 0;
		gbc.gridy		= 6;
		gbc.weightx		= 1;
		JLabel analyzerAngleLabel = new JLabel("Analyzer Angle (degrees)");
		gridbag.setConstraints(analyzerAngleLabel, gbc);
		add(analyzerAngleLabel);
		
		
		gbc.gridx		= 0;
		gbc.gridy		= 7;
		gbc.weightx		= 5;
		gbc.fill 		= GridBagConstraints.HORIZONTAL;
		analyzerAngleSlider = new JSlider(JSlider.HORIZONTAL,0,90,80);
		analyzerAngleSlider.setMajorTickSpacing(30);
		analyzerAngleSlider.setMinorTickSpacing(10);
		analyzerAngleSlider.setPaintTicks(true);
		analyzerAngleSlider.setPaintLabels(true);
		analyzerAngleSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		analyzerAngleSlider.addChangeListener(this);
		analyzerAngleSlider.addChangeListener(mapper);
		analyzerAngleSlider.addChangeListener(renderer);
		gridbag.setConstraints(analyzerAngleSlider, gbc);
		add(analyzerAngleSlider);

		gbc.gridx		= 1;
		gbc.gridy		= 6;
		gbc.weightx		= 1;
		analyzerAngleValueLabel = new JLabel( Integer.toString(analyzerAngleSlider.getValue()) );
		gridbag.setConstraints(analyzerAngleValueLabel, gbc);
		add(analyzerAngleValueLabel);

		gbc.gridx		= 0;
		gbc.gridy		= 8;
		gbc.weightx		= 1;
		JLabel laserWavelengthLabel = new JLabel("Laser Wavelength (nm)");
		gridbag.setConstraints(laserWavelengthLabel, gbc);
		add(laserWavelengthLabel);
		
		
		gbc.gridx		= 0;
		gbc.gridy		= 9;
		gbc.weightx		= 5;
		gbc.fill 		= GridBagConstraints.HORIZONTAL;
		laserWavelengthSlider = new JSlider(JSlider.HORIZONTAL,300,700,514);
		laserWavelengthSlider.setMajorTickSpacing(200);
		laserWavelengthSlider.setMinorTickSpacing(50);
		laserWavelengthSlider.setPaintTicks(true);
		laserWavelengthSlider.setPaintLabels(true);
		laserWavelengthSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		laserWavelengthSlider.addChangeListener(this);
		laserWavelengthSlider.addChangeListener(renderer);
		laserWavelengthSlider.addChangeListener(mapper);
		gridbag.setConstraints(laserWavelengthSlider, gbc);
		add(laserWavelengthSlider);

		gbc.gridx		= 1;
		gbc.gridy		= 8;
		gbc.weightx		= 1;
		laserWavelengthValueLabel = new JLabel( Integer.toString(laserWavelengthSlider.getValue()) );
		gridbag.setConstraints(laserWavelengthValueLabel, gbc);
		add(laserWavelengthValueLabel);
		
/*		gbc.gridx		= 0;
		gbc.gridy		= 5;
		gbc.weightx		= 1;
		JLabel multiplierLabel = new JLabel("Mapper Multiplier (x)");
		gridbag.setConstraints(multiplierLabel, gbc);
		add(multiplierLabel);
				
				
		gbc.gridx		= 1;
		gbc.gridy		= 5;
		gbc.weightx		= 5;
		gbc.fill 		= GridBagConstraints.HORIZONTAL;
		multiplierSlider = new JSlider(JSlider.HORIZONTAL,1,100000000,1);
		multiplierSlider.setMajorTickSpacing(50000000);
		multiplierSlider.setMinorTickSpacing(10000000);
		multiplierSlider.setPaintTicks(true);
		multiplierSlider.setPaintLabels(true);
		multiplierSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		multiplierSlider.addChangeListener(this);
		multiplierSlider.addChangeListener(mapper);
		multiplierSlider.addChangeListener(renderer);
		gridbag.setConstraints(multiplierSlider, gbc);
		add(multiplierSlider);

		gbc.gridx		= 2;
		gbc.gridy		= 5;
		gbc.weightx		= 1;
		multiplierValueLabel = new JLabel( Integer.toString(multiplierSlider.getValue()) );
		gridbag.setConstraints(multiplierValueLabel, gbc);
		add(multiplierValueLabel);
*/
}

	public void stateChanged(ChangeEvent e){
		JSlider source = (JSlider)e.getSource();
		if ( e.getSource() == laserWavelengthSlider ) {
			mapper.setLaserWavelength( laserWavelengthSlider.getValue() );
			laserWavelengthValueLabel.setText( Integer.toString(laserWavelengthSlider.getValue()) );
		}
		if ( e.getSource() == analyzerAngleSlider ) {
			mapper.setAnalyzerAngle( analyzerAngleSlider.getValue() );
			analyzerAngleValueLabel.setText( Integer.toString(analyzerAngleSlider.getValue()) );
		}
		if ( e.getSource() == tiltAngleSlider ) {
			mapper.setTiltAngle( tiltAngleSlider.getValue() );
			tiltAngleValueLabel.setText( Integer.toString(tiltAngleSlider.getValue()) );
		}
/*		if ( e.getSource() == multiplierSlider ) {
			mapper.setMultiplier( (double)multiplierSlider.getValue() );
			multiplierValueLabel.setText( Integer.toString(multiplierSlider.getValue()) );
		}
*/		if ( e.getSource() == phaseSlider ) {
			mapper.setPhase( (double)(phaseSlider.getValue()) );
			phaseValueLabel.setText( Integer.toString(phaseSlider.getValue()) );
		}
		if ( e.getSource() == monolayerThicknessSlider ) {
			mapper.setMonolayerThickness( (double)(monolayerThicknessSlider.getValue()/10) );
			monolayerThicknessValueLabel.setText( Integer.toString(monolayerThicknessSlider.getValue()) );
		}
	}

}