import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Main {
	public static void main(String[] args) throws IOException {
		String fileLocation = "/Users/guilherme/Desktop/CThead";
		
		File file = new File(fileLocation);//"CThead"
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		Cthead cthead = new Cthead(in);
		
		Gui window = new Gui(cthead);
	}
}