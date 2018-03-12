package ellis.image.processing;

import javax.swing.*;


public class StatusMessageArea extends JScrollPane{

	private static String		lineSeparator = System.getProperty("line.separator");

	private JTextArea			textArea	= new JTextArea();
	private int					indent		= 0;
	private String				prefix		= "";

	public StatusMessageArea(){
		super();
		textArea.setText("");
		textArea.setLineWrap(false);
		textArea.setEditable(false);
		textArea.setRows(10);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setViewportView(textArea);
	}

	public void addIndent(){
		indent++;
		prefix += "\t";
	}
	public void removeIndent(){
		if( indent>0 ){
			indent--;
			prefix = prefix.substring(0, prefix.length()-1);
		}
	}
	public void clearIndents(){
		indent = 0;
		prefix = "";
	}

	public String getText(){
		return textArea.getText();
	}

	public void clear(){
		textArea.setText("");
		this.clearIndents();
		textArea.revalidate();
	}

	public void printMessage(String temp){
		println(temp);
	}
	public void printErrorMessage(String temp){
		addIndent();
		println("!!! "+temp);
		removeIndent();
	}
	public void printCommentMessage(String temp){
		addIndent();
		println("--- "+temp);
		removeIndent();
	}
	public void printActionMessage(String temp){
		addIndent();
		println(">>> "+temp);
		removeIndent();
	}

	public void print(String temp){
		textArea.append( prefix+temp );
		textArea.invalidate();
		this.getVerticalScrollBar().setValue( this.getVerticalScrollBar().getMaximum() );
		this.revalidate();
	}

	public void println(String temp){
		print( temp+ lineSeparator);
	}

}