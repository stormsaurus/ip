import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.math.*;

import javax.swing.*;
import javax.swing.event.*;

public class TDV_MapperPanel extends JPanel implements ItemListener, ChangeListener{

	private double phase = 0;
	
	private double monolayerThickness = 0.3;
	private double laserWavelength 	= 514;
	private double brewsterAngle 	= 53.12;
	private double brewsterRadians	= Math.toRadians(brewsterAngle);
	private double dielectricPerp 	= 2.31;
	private double dielectricSupp 	= 2.43 - dielectricPerp;
	private double tiltAngle		= 20;
	private double tiltRadians		= Math.toRadians(tiltAngle);
	private double analyzerAngle	= 80;
	private double analyzerRadians	= Math.toRadians(analyzerAngle);

	private Insets inset = this.getInsets();
	private int xoffset = inset.top+10;
	private int yoffset = inset.left+10;


	private double multiplier=1;
	private TDV_GraphX	graph;
	
	private JCheckBox	aBox;
		private JLabel		aValueLabel;
	private JCheckBox	bBox;
		private JLabel		bValueLabel;
	private JCheckBox	cBox;
		private JLabel		cValueLabel;
	private JCheckBox	dBox;
		private JLabel		dValueLabel;

	private	JLabel		multiplierValueLabel;

	public TDV_MapperPanel(){
		super();
		
		GridBagLayout 		gridbag = new GridBagLayout();
		GridBagConstraints 	gbc 	= new GridBagConstraints();
		gbc.insets = new Insets(10,5,10,5);			
		this.setLayout( gridbag );
		
		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.weightx		= 1;
		multiplierValueLabel = new JLabel( "x"+Double.toString(multiplier) );
		gridbag.setConstraints(multiplierValueLabel, gbc);
		add(multiplierValueLabel);

		aBox = new JCheckBox("A");
		aBox.setMnemonic('A');
		aBox.setSelected(true);
		aBox.addItemListener(this);
		gbc.gridx		= 1;
		gbc.gridy		= 0;
		gbc.weightx		= 1;		
		gridbag.setConstraints(aBox, gbc);
		this.add(aBox);
		
		gbc.gridx		= 1;
		gbc.gridy		= 1;
		gbc.weightx		= 1;
		aValueLabel = new JLabel( Double.toString(getA()*getPrefixNumber()) );
		gridbag.setConstraints(aValueLabel, gbc);
		add(aValueLabel);
		
		bBox = new JCheckBox("B");
		bBox.setMnemonic('B');
		bBox.setSelected(true);
		bBox.addItemListener(this);
		gbc.gridx		= 2;
		gbc.gridy		= 0;
		gbc.weightx		= 1;
		gridbag.setConstraints(bBox, gbc);
		this.add(bBox);

		gbc.gridx		= 2;
		gbc.gridy		= 1;
		gbc.weightx		= 1;
		bValueLabel = new JLabel( Double.toString(getB()*getPrefixNumber()) );
		gridbag.setConstraints(bValueLabel, gbc);
		add(bValueLabel);
		
		cBox = new JCheckBox("C");
		cBox.setMnemonic('C');
		cBox.setSelected(true);
		cBox.addItemListener(this);
		gbc.gridx		= 3;
		gbc.gridy		= 0;
		gbc.weightx		= 1;
		gridbag.setConstraints(cBox, gbc);
		this.add(cBox);

		gbc.gridx		= 3;
		gbc.gridy		= 1;
		gbc.weightx		= 1;
		cValueLabel = new JLabel( Double.toString(getC()*getPrefixNumber()) );
		gridbag.setConstraints(cValueLabel, gbc);
		add(cValueLabel);

		dBox = new JCheckBox("D");
		dBox.setMnemonic('D');
		dBox.setSelected(true);
		dBox.addItemListener(this);
		gbc.gridx		= 4;
		gbc.gridy		= 0;
		gbc.weightx		= 1;
		gridbag.setConstraints(dBox, gbc);
		this.add(dBox);

		gbc.gridx		= 4;
		gbc.gridy		= 1;
		gbc.weightx		= 1;
		dValueLabel = new JLabel( Double.toString(getD()*getPrefixNumber()) );
		gridbag.setConstraints(dValueLabel, gbc);
		add(dValueLabel);

		graph = new TDV_GraphX(new Dimension(400,400), new Rectangle(0,0,360,360), this);
		gbc.gridx		= 0;
		gbc.gridy		= 2;
		gbc.gridwidth	= 5;
		gridbag.setConstraints(graph, gbc);
		this.add(graph);
		
		calcMultiplierToFit(1000d);
	}


	//accepts azimuthal angle in degrees and returns the mapper intensity response value
	public double getIntensityFromTilt(double azimuthAngle){

		
		double azimuthRadians = Math.toRadians(azimuthAngle+phase);

		return multiplier*sqr(   getA()*sqr(cos(azimuthRadians)) + getB() + getC()*sin(azimuthRadians) + getD()*sin(azimuthRadians)*cos(azimuthRadians)    );

	}

	public double getPrefixNumber(){
		return ( 2*Math.PI*monolayerThickness*cos(brewsterRadians) )/( laserWavelength*( dielectricPerp + dielectricSupp*sqr(cos(tiltRadians)) ) ) ;
	}
	
	private double sin(double value){
		return Math.sin(value);
	}

	private double cos(double value){
		return Math.cos(value);
	}
	
	private double sqr(double value){
		return Math.pow(value,2);
	}
	
	private double getA(){
		if (aBox.isSelected()){
			return -getPrefixNumber()*cos( analyzerRadians )*dielectricPerp*dielectricSupp*sqr( sin( tiltRadians ) );
		} else {
			return 0;
		}
	}

	private double getB(){
		if (bBox.isSelected()){
			return -getPrefixNumber()* (cos( analyzerRadians ))* (   (dielectricPerp-1)*( dielectricPerp-sqr(Math.tan(brewsterRadians)) )+ ( dielectricPerp-1-sqr(Math.tan(brewsterRadians)) )*dielectricSupp*sqr(cos(tiltRadians))   );
		} else {
			return 0;
		}
	}
	private double getC(){
		if (cBox.isSelected()){
			return -getPrefixNumber()*(2) * sin(analyzerRadians) * sin( brewsterRadians)*dielectricSupp  * cos(tiltRadians) * sin(tiltRadians);
		} else {
			return 0;
		}
	}
	private double getD(){
		if (dBox.isSelected()){
			return getPrefixNumber()* 2 * sin( analyzerRadians ) * cos( brewsterRadians)*dielectricPerp*dielectricSupp * sqr( sin(tiltRadians) );
		} else {
			return 0;
		}
	}

	public double getMaxIntensityFromTilt(){
		double max = 1;
		double temp = 0;
		for (int i=0; i<360; i++){
			temp = getIntensityFromTilt( (double)i );
			if ( max < temp ) max = temp;
		}
		return max;
	}

	public double getMinIntensityFromTilt(){
		double min = 0;
		double temp = 0;
		for (int i=0; i<360; i++){
			temp = getIntensityFromTilt( (double)i );
			if ( min > temp ) min = temp;
		}
		return min;
	}

	public void setMonolayerThickness(double monolayerThickness){
		this.monolayerThickness = monolayerThickness;
	}	

	public void setTiltAngle(double tiltAngle){
		this.tiltAngle = tiltAngle;
		this.tiltRadians = Math.toRadians(tiltAngle);
	}
	
	public void setAnalyzerAngle(double analyzerAngle){
		this.analyzerAngle = analyzerAngle;
		this.analyzerRadians = Math.toRadians(analyzerAngle);
	}
	
	public void setLaserWavelength(double laserWavelength){
		this.laserWavelength = laserWavelength;
	}

	public void setPhase(double phase){
		this.phase = phase;
	}

	public double getPhase(){
		return phase;
	}
	
	private void setMultiplier(double multiplier){
		if ( multiplier>0 ) {
			this.multiplier=multiplier;
			multiplierValueLabel.setText( "x"+Double.toString(multiplier) );
		}
	}
	
	private void calcMultiplierToFit(double fit){
		double max = getMaxIntensityFromTilt();
		if ( fit>0 ) {
			int count = 0;
			while ( max<fit && count++<5 ){
				setMultiplier( fit/(max/multiplier) );
				max = getMaxIntensityFromTilt();
			}
			if ( max != fit ) setMultiplier( fit/(max/multiplier) );
			//System.out.println( max + "-max, "+multiplier+"-mult, "+ fit +"-fit, "+fit/(max/multiplier));
		}
	}
	
	public void stateChanged(ChangeEvent e){
		calcMultiplierToFit(1000d);
		multiplierValueLabel.setText( "x"+Double.toString(multiplier) );
		aValueLabel.setText( Double.toString(getA()) );
		bValueLabel.setText( Double.toString(getB()) );
		cValueLabel.setText( Double.toString(getC()) );
		dValueLabel.setText( Double.toString(getD()) );
		graph.stateChanged(new ChangeEvent(this));
	}
	
	public void itemStateChanged(ItemEvent e) {

		Object 		source = e.getItemSelectable();
		
		if (e.getStateChange()==e.SELECTED || e.getStateChange()==e.DESELECTED) {
			stateChanged(new ChangeEvent(this));
		}
	}

}

class TDV_GraphX extends Canvas implements ChangeListener{

	private Dimension 		drawSize = new Dimension(150,150);
	private Rectangle		axisScaling = new Rectangle(0,0,360,360 );
	private TDV_MapperPanel	mapper;
	private boolean			autoScale = true;

	private int ddx;
	private int ddy;
	private int dax;
	private int day;
	
	public TDV_GraphX(Dimension size, Rectangle axisScaling, TDV_MapperPanel mapper){
		this.drawSize = size;					//sets the real size of the graph component in pixels
		ddx=drawSize.width-0;					//delta x for drawSize
		ddy=drawSize.height-0;					//delta y for drawSize
		setSize(size);							
		
		this.axisScaling = axisScaling;			//sets how the graph values map to pixels

 		this.mapper = mapper;					//handle on the mapper
	}

	public void stateChanged(ChangeEvent e){
		this.update(this.getGraphics());	
	}

	public void paint(Graphics g){
		int y;
		int yplus;
		Rectangle scaling;
		if (autoScale) {
			scaling = new Rectangle(axisScaling.x, (int)(mapper.getMinIntensityFromTilt()), axisScaling.width, (int)(mapper.getMaxIntensityFromTilt()-mapper.getMinIntensityFromTilt()) );
			dax=scaling.width-scaling.x;
			day=scaling.height-scaling.y;
		} else {
			scaling = axisScaling;
			dax=scaling.width-scaling.x;
			day=scaling.height-scaling.y;
		}
		g.setColor(new Color(0,0,0));
		g.drawLine(0,0,0,drawSize.height-1);
		g.drawLine(0,drawSize.height-1,drawSize.width-1,drawSize.height-1);
		g.drawString( Integer.toString((int)(scaling.height)),0,20 );
		g.drawString( Integer.toString((int)(scaling.y)),0,drawSize.height-20);

		for (int x=0; x<drawSize.width; x+=2){
			y = (int)(mapper.getIntensityFromTilt(x*dax/ddx+scaling.x)*ddy/day);
			y = ( (y>drawSize.height )? drawSize.height : ((y<0)? 0 : y)  );
			yplus = (int)(mapper.getIntensityFromTilt((x+2)*dax/ddx+scaling.x)*ddy/day);
			yplus = ( (yplus>drawSize.height )? drawSize.height : ((yplus<0)? 0 : yplus)  );
			g.drawLine(x,drawSize.height-y,x+2,drawSize.height-yplus);
		}
	}

}

class TDV_Graph extends Canvas implements ChangeListener{

	private Dimension 		drawSize = new Dimension(150,150);
	private Rectangle		axisScaling = new Rectangle(0,0,360,360 );
	private TDV_MapperPanel	mapper;
	private boolean			autoScale = true;

	private int ddx;
	private int dax;
	
	public TDV_Graph(Dimension size, Rectangle axisScaling, TDV_MapperPanel mapper){
		this.drawSize = size;
		this.axisScaling = axisScaling;
		this.mapper = mapper;
		setSize(size);
		ddx=drawSize.width;
		dax=axisScaling.width-axisScaling.x;
	}

	public void setMultiplier(int multiplier){
//		this.multiplier = multiplier;
	}
	
	public void stateChanged(ChangeEvent e){
		this.update(this.getGraphics());	
	}

	public void paint(Graphics g){
		int yvalue;
		int yvalueplus;
		g.setColor(new Color(0,0,0));
		g.drawLine(0,0,0,drawSize.height-1);
		g.drawLine(0,drawSize.height-1,drawSize.width-1,drawSize.height-1);
		g.drawString( Integer.toString((int)(axisScaling.height)),0,20 );
		g.drawString( Integer.toString((int)(axisScaling.y)),0,drawSize.height-20);
/*		if (autoScale){
			int value;
			for (int x=0; x<drawSize.width; x+=5){
				value = (int)(mapper.getIntensityFromTilt(x*dax/ddx+axisScaling.x));
				if (value>axisScaling.height) axisScaling.height=value;
				if (value<axisScaling.height) axisScaling.height=value;
			}
		}
*/
		for (int x=0; x<drawSize.width; x+=2){
			yvalue = (int)(mapper.getIntensityFromTilt(x*dax/ddx+axisScaling.x));
			yvalue = ( (yvalue>axisScaling.height )? axisScaling.height : ((yvalue<axisScaling.y)? axisScaling.y : yvalue)  );
			yvalueplus = (int)(mapper.getIntensityFromTilt((x+2)*dax/ddx+axisScaling.x));
			yvalueplus = ( (yvalueplus>axisScaling.height )? axisScaling.height : ((yvalueplus<axisScaling.y)? axisScaling.y : yvalueplus)  );
			g.drawLine(x,drawSize.height-yvalue,x+2,drawSize.height-yvalueplus);
		}
	}

}