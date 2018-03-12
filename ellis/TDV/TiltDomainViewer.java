//package ellis.TDV;

//import ellis.TDV.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class TiltDomainViewer extends JFrame{

	//main container pane
	private JPanel				contentpane;

	//SplitPanes
	private JSplitPane			viewsAndParms;
	private JSplitPane			rendererAndMapper;

	//Panels
	private TDV_MapperPanel			mapper;
	private TDV_DomainRendererPanel	renderer;
	private TDV_ParametersPanel		parms;
	

	
	public TiltDomainViewer(){
		super("Tilt Domain Viewer");
		WindowListener wl = new WindowAdapter(){
			public void windowClosing(WindowEvent e) {System.exit(0);}
		};
		addWindowListener(wl);

	
		mapper		= new TDV_MapperPanel();
		renderer 	= new TDV_DomainRendererPanel(mapper);
		parms		= new TDV_ParametersPanel(renderer, mapper);
		
		rendererAndMapper 	= new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, renderer, mapper);
		viewsAndParms		= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, rendererAndMapper, parms);

		contentpane	= new JPanel();
		contentpane.setLayout(new BorderLayout());

		contentpane.add(viewsAndParms, BorderLayout.CENTER);		
		
		this.setContentPane(contentpane);
		
		pack();
//		setSize(600, 800);
		show();
	}
		
	public static void main(String[] args){
		new TiltDomainViewer();
	}

}

