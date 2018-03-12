package ellis.image.processing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;


/**
 *
 *	<P>A <code>JScrollPane</code> subclass which displays image files of a predefined type in a <code>JList</code> for
 selection.</P>
 *  <P>Methods for returning selected images and names are provided.</P>
 *  <P>NOTE - As of JDK1.2.2 several bugs exist in the <code>JFileChooser</code> class.  As a temporary measure around
 being unable to select a file, the user of this class must select the directory which contains the files to add to the
 <code>JList</code>.  The directory will be scanned for files matching the correct file appendage and these files will
 be added to the <code>JList</code>.</P>
 *
 *  @author Ellis Teer 8/1/99
 *  @see ImageUtilities
 *  @see ImageViewArea
 *  @see JScrollPane
 */


public class ImageFileList extends JScrollPane{

	private static String[]		suffixes = {".jpg",".gif"};

	private JFileChooser		filechooser;
	private JList				list;
//	private ExtensionFileFilter	filter;
	private SuffixFileFilter	sfilter;
	private File[]				files;
	private File				directory;
	private Vector				filenames;
	private int					returnval;

/* Cant' seem to get access to ExtensionFileFilter object */

	public ImageFileList(){
		super();
		sfilter = new SuffixFileFilter(suffixes);
		filechooser = new JFileChooser(new File(System.getProperty("user.dir","")));
//		filter = new ExtensionFileFilter();
//		filter.addExtension("jpg");
//		filter.addExtension("gif");
//		filter.setDescription("JPG & GIF Images");
//		filechooser.setFileFilter(sfilter);
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		filechooser.setMultiSelectionEnabled(false);

		filenames = new Vector();

		initList();
	}

	private void initList() {
		list = new JList();
		directory = new File(System.getProperty("user.dir",""));
		if (directory!=null && directory.exists()) {
			filenames.clear();
			files=directory.listFiles(sfilter);				/********workaround*/
			if (files!=null && files.length>0) {
				for (int i=0; i<files.length; i++) {
					filenames.add(files[i].getName());
				}
				list.setListData(filenames);
			}
		}
		list.invalidate();
		this.getViewport().setView(list);
	}

/*	The JFileChooser will not return a array of selected files...Using workaround */

	public void populateList(){
		returnval = filechooser.showOpenDialog(this);
//		files = filechooser.getSelectedFiles();        ********this is buggy using workaround
		directory = filechooser.getSelectedFile();
		if (directory!=null && returnval==JFileChooser.APPROVE_OPTION && directory.exists()/* && files.length>0 */) {
			filenames.clear();
			files=directory.listFiles(sfilter);				/********workaround*/
			if (files!=null && files.length>0) {
				for (int i=0; i<files.length; i++) {
					filenames.add(files[i].getName());
				}
				list.setListData(filenames);
			}
		}
		list.invalidate();
	}

	public String getSelectedImageName(){
		return files[list.getSelectedIndex()].getName();
	}

	public static String removeAcceptableFileTypePostfix(String string){
		for(int i=0; i<suffixes.length; i++){
			if (string.endsWith( suffixes[i] )){
				string = string.substring( 0, string.length()-suffixes[i].length() );
			}
		}
		return string;
	}

	public Image getSelectedImage(){
		if (files!=null && files.length>0){
			return Toolkit.getDefaultToolkit().getImage( files[list.getSelectedIndex()].getPath() );
		} else {
			return null;
		}
	}

	public File[] getSelectedImageFiles(){
		File[] files = null;
		int[] indices = list.getSelectedIndices();
		if(indices!=null && indices.length>0){
			files = new File[indices.length];
			for(int i=0; i<indices.length; i++){
				files[i] = this.files[indices[i]];
			}
		}
		return files;
	}

	public String[] getSelectedImageNames(){
			String[] tempNames;
			int[] tempIndices = list.getSelectedIndices();
			if (tempIndices!=null && tempIndices.length>0){
				tempNames = new String[tempIndices.length];
				for (int i=0; i<tempIndices.length; i++) {
					tempNames[i] = files[tempIndices[i]].getPath();
				}
				return tempNames;
			} else {
				return null;
			}
	}

	public void addListSelectionListener(ListSelectionListener listener){
		list.addListSelectionListener(listener);
	}

	public void setEnabled(boolean enabled){
		list.setEnabled( enabled );
	}

	public boolean isMultiSelection(){
		int[] indices = list.getSelectedIndices();
		if(indices!=null && indices.length>1){
			return true;
		} else {
			return false;
		}

	}

}

