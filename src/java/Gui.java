
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Gui extends JFrame {
	Container container;
	JButton mip_button, resize_button, histogram_equalization_button; //an example button to switch to MIP mode
    JLabel image_icon1; // using JLabel to display an image (check online documentation)
    JLabel image_icon2; // using JLabel to display an image (check online documentation)
    JLabel image_icon3; // using JLabel to display an image (check online documentation)

	JSlider zslice_slider, yslice_slider, xslice_slider; //sliders to step through the slices (z, y and x directions) (remember 113 slices in z direction 0-112)
    BufferedImage image1, image2, image3; //storing the image in memory
	Cthead cthead;

	public Gui(Cthead cthead) {
		this.setSize(768, 500);
		//set default close mode
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("CTHead bolado");
		this.cthead = cthead;
		// Set up the simple GUI
        // First the container:
        /*image1 = new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
		image2 = new BufferedImage(256, 112, BufferedImage.TYPE_3BYTE_BGR);
		image3 = new BufferedImage(256, 112, BufferedImage.TYPE_3BYTE_BGR);*/


        container = getContentPane();
        container.setLayout(new BorderLayout());
        
        // First we create sliders so we can generate images as soon as the window loads
        zslice_slider = new JSlider(0,112);
        yslice_slider = new JSlider(0,255);
        xslice_slider = new JSlider(0,255);
		

        image_icon1 = new JLabel(new ImageIcon(cthead.getImage(zslice_slider.getValue(), 1)));
        container.add(image_icon1, BorderLayout.LINE_START);

        image_icon2 = new JLabel(new ImageIcon(cthead.getImage(yslice_slider.getValue(), 2)));
        container.add(image_icon2, BorderLayout.CENTER);

		image_icon3 = new JLabel(new ImageIcon(cthead.getImage(xslice_slider.getValue(), 3)));
        container.add(image_icon3, BorderLayout.LINE_END);

        Container c_start = new Container();
        c_start.setLayout(new FlowLayout());

        container.add(c_start, BorderLayout.PAGE_END);

        // Then the invert button
        mip_button = new JButton("MIP");
        c_start.add(mip_button); 

        resize_button = new JButton("Resize");
        c_start.add(resize_button);

        histogram_equalization_button = new JButton("Perform Histogram Equalization");
        c_start.add(histogram_equalization_button);
		

        Container c_end = new Container();
        c_end.setLayout(new FlowLayout());

        container.add(c_end, BorderLayout.PAGE_START);

		// Then we put the sliders after the images
		c_end.add(zslice_slider);
		c_end.add(yslice_slider);
		c_end.add(xslice_slider);
		
		//Add labels (y slider as example)
		yslice_slider.setMajorTickSpacing(50);
		yslice_slider.setMinorTickSpacing(10);
		yslice_slider.setPaintTicks(true);
		yslice_slider.setPaintLabels(true);

		xslice_slider.setMajorTickSpacing(50);
		xslice_slider.setMinorTickSpacing(10);
		xslice_slider.setPaintTicks(true);
		xslice_slider.setPaintLabels(true);

		// Now all the handlers class
        GUIEventHandler handler = new GUIEventHandler();

        // associate appropriate handlers
        mip_button.addActionListener(handler);
		resize_button.addActionListener(handler);
		histogram_equalization_button.addActionListener(handler);

		yslice_slider.addChangeListener(handler);
		zslice_slider.addChangeListener(handler);
		xslice_slider.addChangeListener(handler);
        
        // ... and display everything
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /*
        This is the event handler for the application
    */
    private class GUIEventHandler implements ActionListener, ChangeListener {
	
		//Change handler (e.g. for sliders)
        public void stateChanged(ChangeEvent e) {
         	//System.out.println(show_slice);

 			//image1 = Functions.getImage(cthead, zslice_slider.getValue(), 1); // 0 - 113 | z show the top
            image1 = cthead.getImage(zslice_slider.getValue(), 1); // 0 - 113 | z show the top
            image2 = cthead.getImage(yslice_slider.getValue(), 2); // 0 - 255 | y show the side
            image3 = cthead.getImage(xslice_slider.getValue(), 3); // 0 - 113 | x show the front
        
            // Update image
	        image_icon1.setIcon(new ImageIcon(image1));
            image_icon2.setIcon(new ImageIcon(image2));
            image_icon3.setIcon(new ImageIcon(image3));	
			//e.g. do something to change the image here
		}
		
		//action handlers (e.g. for buttons)
        public void actionPerformed(ActionEvent event) {
			if (event.getSource() == mip_button) {
				System.out.println("MIP is alive!");

				JFrame newWindow = new JFrame("Maximum Intensity Projection");
				newWindow.setLayout(new FlowLayout());

				JLabel mip_z = new JLabel(new ImageIcon(cthead.maximumIntensityProjection(1)));
				newWindow.add(mip_z);

				JLabel mip_y = new JLabel(new ImageIcon(cthead.maximumIntensityProjection(2)));
				newWindow.add(mip_y);

				JLabel mip_x = new JLabel(new ImageIcon(cthead.maximumIntensityProjection(3)));
				newWindow.add(mip_x);

				newWindow.pack();
				newWindow.setLocationRelativeTo(null);
    	    	newWindow.setVisible(true);
			   
			} else if (event.getSource() == resize_button) {
				System.out.println("Let's make it bigger! (or smaller)");

				Object[] possibilities = {"Top View", "Side View", "Front View"};
				String gui_view = (String) JOptionPane.showInputDialog(
				                    container,
				                    "Which view would you like to resize?",
				                    "Select the view you want to resize",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    "Top View");
				if (gui_view == null) return;

				int direction = 0;
				switch (gui_view) {
					case "Top View":
						direction = 1;
						break;

					case "Side View":
						direction = 2;
						break;

					case "Front View":
						direction = 3;
						break;
				}

				String sgui_width = (String) JOptionPane.showInputDialog(
				                    container,
				                    "Enter the new width",
				                    "Enter a positive integer for the amount\nof pixels in the new width:",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    null,
				                    "512");
				if (sgui_width == null) return;
				int gui_width = Integer.parseInt(sgui_width);

				String sgui_height = (String) JOptionPane.showInputDialog(
				                    container,
				                    "Enter the new height",
				                    "Enter a positive integer for the amount\nof pixels in the new height:",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    null,
				                    "512");
				if (sgui_height == null) return;
				int gui_height = Integer.parseInt(sgui_height);

				Object[] sampling_methods = {"Nearest Neighbour", "Bilinear Interpolation", "Both"};
				String gui_sampling_method = (String) JOptionPane.showInputDialog(
				                    container,
				                    "Which view would you like to resize?",
				                    "Select the view you want to resize",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    sampling_methods,
				                    "Nearest Neighbour");

				if (gui_sampling_method == null) return;
				int sampling_method = 5;
				switch (gui_sampling_method) {
					case "Nearest Neighbour":
						sampling_method = Cthead.NEAREST_NEIGHBOUR;
						break;

					case "Bilinear Interpolation":
						sampling_method = Cthead.BILINEAR_INTERPOLATION;
						break;

					case "Both":
						sampling_method = 5;
						break;
				}

				JFrame newWindow = new JFrame("Resized Image");
				newWindow.setLayout(new FlowLayout());

				int slice = 0;
				switch (direction) {
					case 1:
						slice = zslice_slider.getValue();
						break;

					case 2:
						slice = yslice_slider.getValue();
						break;

					case 3:
						slice = xslice_slider.getValue();
						break;
				}

				System.out.println(direction + ", " + slice + ", " + sampling_method);

				if (sampling_method == 101 || sampling_method == 5) {
					JLabel resizedImage = new JLabel(new ImageIcon(cthead.resizeImage(slice, direction, 256, direction == 1 ? 256 : 113, gui_width, gui_height, Cthead.NEAREST_NEIGHBOUR)));
					newWindow.add(resizedImage);
				}

				if (sampling_method == 102 || sampling_method == 5) {
					JLabel resizedImage2 = new JLabel(new ImageIcon(cthead.resizeImage(slice, direction, 256, direction == 1 ? 256 : 113, gui_width, gui_height, Cthead.BILINEAR_INTERPOLATION)));
					newWindow.add(resizedImage2);
				}

				newWindow.pack();
				newWindow.setLocationRelativeTo(null);
    	    	newWindow.setVisible(true);
			} else if (event.getSource() == histogram_equalization_button) {
				System.out.println("Also worked");


								Object[] possibilities = {"Top View", "Side View", "Front View"};
				String gui_view = (String) JOptionPane.showInputDialog(
				                    container,
				                    "Which view would you like to resize?",
				                    "Select the view you want to resize",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    "Top View");
				if (gui_view == null) return;

				int direction = 0;
				switch (gui_view) {
					case "Top View":
						direction = 1;
						break;

					case "Side View":
						direction = 2;
						break;

					case "Front View":
						direction = 3;
						break;
				}

				int slice = 0;
				switch (direction) {
					case 1:
						slice = zslice_slider.getValue();
						break;

					case 2:
						slice = yslice_slider.getValue();
						break;

					case 3:
						slice = xslice_slider.getValue();
						break;
				}


				JFrame newWindow = new JFrame("Histogram Equalization - slice " + (slice+1));
				newWindow.setLayout(new FlowLayout());

				CTHeadHistogram ctheadHistogram = new CTHeadHistogram(cthead);
				ctheadHistogram.fill();
				CTHeadHistogram mapping = ctheadHistogram.histogramEqualization(0, 255);
				JLabel equalizedImage = new JLabel(new ImageIcon(cthead.equalizeImage(mapping, slice, direction)));
				newWindow.add(equalizedImage);

				newWindow.pack();
				newWindow.setLocationRelativeTo(null);
    	    	newWindow.setVisible(true);
			}
        }
    }
}