package ellis.image.processing;
import java.io.*;


public class SuffixFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter{

	private String[]	suffixes;

	public SuffixFileFilter(String arg){
		suffixes = new String[1];
		suffixes[0]=arg;
	}

	public SuffixFileFilter(String[] arg){
		suffixes = arg;
	}

	public boolean accept(File arg){
		//if (arg.isDirectory()) return true;
		for (int i=0; i<suffixes.length; i++){
			if ( arg.getName().endsWith(suffixes[i]) ) {
				return true;
			}
		}
		return false;
	}

	public String getDescription(){
		return ".jpg and .gif";
	}

}