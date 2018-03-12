import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class TDV_DomainRendererPanel extends JPanel implements ChangeListener{

	private int slices = 8;

	private TDV_MapperPanel	mapper;
	
	private TDV_StripCanvas	stripview;
	private TDV_StarCanvas	starview;
	
	private GridBagLayout			gridbag;
	private GridBagConstraints		gbc;

	private JSlider			sliceSlider;
	
	public TDV_DomainRendererPanel(TDV_MapperPanel mapper){
		super();
		this.mapper = mapper;

		gridbag = new GridBagLayout();
		gbc 	= new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);			
		this.setLayout( gridbag );

		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.weightx		= 1;
		gbc.gridwidth	= 1;
//		gbc.fill 		= GridBagConstraints.HORIZONTAL;
		stripview = new TDV_StripCanvas(new Dimension(100,100), this);
		gridbag.setConstraints(stripview, gbc);
		this.add(stripview);

		gbc.gridx		= 1;
		gbc.gridy		= 0;
		gbc.weightx		= 1;
		gbc.gridwidth	= 1;
//		gbc.fill 		= GridBagConstraints.HORIZONTAL;
		starview  = new TDV_StarCanvas(new Dimension(150,150), this);
		gridbag.setConstraints(starview, gbc);
		this.add(starview);

		gbc.gridx		= 0;
		gbc.gridy		= 1;
		gbc.weightx		= 1;
		gbc.gridwidth	= 2;
		gbc.fill 		= GridBagConstraints.HORIZONTAL;

		sliceSlider = new JSlider(JSlider.HORIZONTAL,0,36,6);
		sliceSlider.setMajorTickSpacing(6);
		sliceSlider.setMinorTickSpacing(1);
		sliceSlider.setPaintTicks(true);
		sliceSlider.setPaintLabels(true);
		sliceSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		sliceSlider.addChangeListener(this);
		gridbag.setConstraints(sliceSlider, gbc);
		add(sliceSlider);

	}

	public void setSlices(int slices){
		if (slices!=0) this.slices = slices;
		else this.slices=1;
	}
	
	public int getSlices(){
		return slices;
	}

	public double getIntensityFromTilt(double azimuthalAngle){
		return mapper.getIntensityFromTilt(azimuthalAngle);
	}

	public double getMaxIntensityFromTilt(){
		return mapper.getMaxIntensityFromTilt();
	}

	public double getMinIntensityFromTilt(){
		return mapper.getMinIntensityFromTilt();
	}

	public void stateChanged(ChangeEvent e){

		if ( e.getSource() == sliceSlider ) {
			setSlices( (int)(sliceSlider.getValue()) );
		}	
		stripview.update(stripview.getGraphics());
		starview.update(starview.getGraphics());
		this.invalidate();
		this.revalidate();
	}
	
}

class TDV_StarCanvas extends Canvas{

	private Dimension				drawingDimension = new Dimension(150,150);
	private TDV_DomainRendererPanel	renderer;


	public TDV_StarCanvas(Dimension dimension, TDV_DomainRendererPanel renderer){
		this.drawingDimension = dimension;
		this.renderer = renderer;
		setSize(dimension);
	}

	public void paint(Graphics g){
		int slices 	= renderer.getSlices();
		double mIntensityToColor	= 255/( renderer.getMaxIntensityFromTilt()-renderer.getMinIntensityFromTilt() );
		double bIntensityToColor	= renderer.getMinIntensityFromTilt();
		for (int i=0; i<=slices; i++){
			int intensity = (int)(renderer.getIntensityFromTilt(i*((int)360/slices))*mIntensityToColor+bIntensityToColor);
			Color icolor = new Color ( intensity, intensity, intensity );
			g.setColor(icolor);
			g.fillArc( 0,0,drawingDimension.width,drawingDimension.width,i*((int)360/slices),(int)360/slices );
		}
	}

}


class TDV_StripCanvas extends Canvas{

	private Dimension				drawingDimension = new Dimension(100,100);
	private TDV_DomainRendererPanel	renderer;

	public TDV_StripCanvas(Dimension dimension, TDV_DomainRendererPanel renderer){
		this.drawingDimension = dimension;
		this.renderer = renderer;
		setSize(dimension);
	}

	public void paint(Graphics g){
		int slices 	= renderer.getSlices();
		double mIntensityToColor	= 255/( renderer.getMaxIntensityFromTilt()-renderer.getMinIntensityFromTilt() );
		double bIntensityToColor	= renderer.getMinIntensityFromTilt();
		for (int i=0; i<slices; i++){
			int intensity = (int)(renderer.getIntensityFromTilt(i*((int)360/slices))*mIntensityToColor+bIntensityToColor);
			Color icolor = new Color ( intensity, intensity, intensity );
			g.setColor(icolor);
			g.fillRect( (int)(i*drawingDimension.width/slices), 0, (int)(drawingDimension.width/slices)+1, drawingDimension.height );
		}
	}
	
}
