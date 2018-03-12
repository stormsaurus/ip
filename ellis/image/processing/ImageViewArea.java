package ellis.image.processing;

import java.util.*;
import java.awt.image.*;

import javax.swing.*;
import java.awt.*;

/**
 *
 *	<P>A JScrollPane subclass which supports the display of named images.  The images with names underneath are
 displayed in a left to right fashion.</P>
 *  <P>Various methods for adding, clearing, and retrieving the most recently added image are provided.</P>
 *
 *  @author Ellis Teer 8/1/99
 *  @see ImageUtilities
 *  @see ImageFileList
 *  @see JScrollPane
 */

public class ImageViewArea extends JScrollPane{

	private JPanel		displayPanel;
	private double		zoomRatio = 1;
	private Vector		images = new Vector();
	private Vector		imageNames = new Vector();


	public ImageViewArea(){
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		displayPanel = new JPanel();
		getViewport().add(displayPanel);
	}

    /**
     * Clears all images and names from the view area.
     */
	public void clear(){
		images.clear();
		imageNames.clear();
		displayPanel.removeAll();
		displayPanel.invalidate();
		displayPanel.repaint();
	}

    /**
     * Returns the most recently added image.
     *
     * @return	Image - the image
     */
	public Image getLastImage(){
		return (Image)images.lastElement();
	}

    /**
     * Returns the name of the most recently added image.
     *
     * @return	String - the name
     */
	public String getLastImageName(){
		return (String)imageNames.lastElement();
	}

    /**
     * Returns a specific image indexed by the order the images were added.
     *
     * @return	Image - the image indexed by the order it was added to the view area
     */
	public Image getImage(int index){
		return (Image)images.get(index);
	}

    /**
     * Returns a specific image name indexed by the order the images were added.
     *
     * @return	String - the name of the image indexed by the order it was added to the view area
     */
	public String getImageName(int index){
		return (String)imageNames.get(index);
	}

    /**
     * Returns the current number of images in the view area.
     *
     * @return    int - the number of images
     */
	public int getImageCount(){
		return images.size();
	}

    /**
     * Adds a single unnamed image to the view area.  A default name is assigned.
     *
     * @param     imageToAdd - the Image to be added to the view area.
     */
	public void addImage(Image imageToAdd){
		this.addImage( "default", imageToAdd );
	}

    /**
     * Adds a single named image to the view area.
     *
     * @param     imageToAdd - the image to be added to the view area
     * @param	  nameToAdd - The name of the image.  The name is descriptive only but should be short.
     */
	public void addImage(String nameToAdd, Image imageToAdd){
		if (imageToAdd!=null){
			if( nameToAdd==null ) nameToAdd="default";
			images.add( imageToAdd );
			imageNames.add( nameToAdd );
			Box box = Box.createVerticalBox();
			box.add( new JLabel( new ImageIcon(imageToAdd) ) );
			box.add( new JLabel( nameToAdd ) );
			//displayPanel.add( new JLabel(nameToAdd, new ImageIcon(imageToAdd), JLabel.LEADING) );
			displayPanel.add( box );
			displayPanel.invalidate();
		} else {
			clear();
			throw new IllegalArgumentException("ImageViewArea cannot add a null image.");
		}
		this.validate();
		getHorizontalScrollBar().setValue( getHorizontalScrollBar().getMaximum() );
	}

	public Dimension getPreferredSize(){
		return new Dimension( 625, 350 );
	}

}