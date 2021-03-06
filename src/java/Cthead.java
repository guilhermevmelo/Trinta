
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Cthead {
	public static final int NEAREST_NEIGHBOUR	= 101;
	public static final int BILINEAR_INTERPOLATION	= 102;
	public static final int BICUBIC_INTERPOLATION	= 103;

	public short cthead[][][];	// store the 3D volume data set
	private short min, max;		// min/max colour value in the 3D volume data set

	public Cthead(DataInputStream file) throws IOException {
		int i, j, k; //loop through the 3D data set
		
		//set to extreme values
		min = Short.MAX_VALUE;
		max = Short.MIN_VALUE; 
		short read; //value read in
		int b1, b2; //data is wrong Endian (check wikipedia) for Java so we need to swap the bytes around
		
		cthead = new short[113][256][256]; //allocate the memory - note this is fixed for this data set
		//loop through the data reading it in
		for (k=0; k<113; k++) {
			for (j=0; j<256; j++) {
				for (i=0; i<256; i++) {
					//because the Endianess is wrong, it needs to be read byte at a time and swapped
					b1=((int)file.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types (C++ is so much easier!)
					b2=((int)file.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types (C++ is so much easier!)
					read=(short)((b2<<8) | b1); //and swizzle the bytes around
					if (read<min) min=read; //update the minimum
					if (read>max) max=read; //update the maximum
					cthead[k][j][i]=read; //put the short into memory (in C++ you can replace all this code with one fread)
				} // i
			} // j
		} // k
	} // constructor

	public BufferedImage getImage(int slice, int direction) {
		int w = 256, h = (direction == 1) ? 256 : 113;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

		int i, j, c, k;
        //Obtain pointer to data for fast processing
        byte[] data = FunctionsCt.getImageData(image);

        //System.out.println(data);

		double col;
		short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (j=0; j<h; j++) {
            for (i=0; i<w; i++) {
				switch (direction) {
					case 1:
					datum = cthead[slice][j][i];
					break;

					case 2:
					datum = cthead[j][i][slice];
					break;

					case 3:
					datum = cthead[j][slice][i];
					break;

					default:
					datum = 0;
				}

				
				//calculate the colour by performing a mapping from [min,max] -> [0,255]
				col=(255.0f*((double)datum-(double)min)/((double)(max-min)));
                for (c=0; c<3; c++) {
					//and now we are looping through the bgr components of the pixel
					//set the colour component c of pixel (i,j)
					data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            } // column loop
        } // row loop

        return image;
	}

	public BufferedImage equalizeImage(CTHeadHistogram mapping, int slice, int direction) {
		int w = 256, h = (direction == 1) ? 256 : 113;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

		int i, j, c, k;
        //Obtain pointer to data for fast processing
        byte[] data = FunctionsCt.getImageData(image);

        //System.out.println(data);

		double col;
		short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (j=0; j<h; j++) {
            for (i=0; i<w; i++) {
				switch (direction) {
					case 1:
					datum = cthead[slice][j][i];
					break;

					case 2:
					datum = cthead[j][i][slice];
					break;

					case 3:
					datum = cthead[j][slice][i];
					break;

					default:
					datum = 0;
				}

				
				//calculate the colour by performing a mapping from [min,max] -> [0,255]
				col = mapping.getValue((int)datum - this.min);
                for (c=0; c<3; c++) {
					//and now we are looping through the bgr components of the pixel
					//set the colour component c of pixel (i,j)
					data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            } // column loop
        } // row loop

        return image;
	}

	public BufferedImage resizeImage(int slice, int direction, int originalWidth, int originalHeight, int newWidth, int newHeight, int samplingMethod) {
		BufferedImage r;
		switch(samplingMethod) {
			case 101:
				r = this.nearestNeighbourSampling(originalWidth, originalHeight, newWidth, newHeight, slice, direction);
				break;

			case 102:
				r = this.bilinearInterpolationSampling(originalWidth, originalHeight, newWidth, newHeight, slice, direction);
				break;

			default:
				r = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);
		}
		return r;
	}

	/* Resizing sampling modes */
	private BufferedImage nearestNeighbourSampling(int originalWidth, int originalHeight, int newWidth, int newHeight, int slice, int direction) {
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);
		int i, j, c, k;

		byte[] newData = FunctionsCt.getImageData(newImage);
		double col;
		short datum;
		int I, J;
		
		for (j = 0; j < newHeight; j++) {
		    for (i = 0; i < newWidth; i++) {
				J = (int)(j * (double)originalHeight / (double)newHeight);
				I = (int)(i * (double)originalWidth / (double)newWidth);
				
				switch (direction) {
					case 1:
					datum = cthead[slice][J][I];
					break;

					case 2:
					datum = cthead[J][I][slice];
					break;

					case 3:
					datum = cthead[J][slice][I];
					break;

					default:
					datum = 0;
				}
				
				//calculate the colour by performing a mapping from [min,max] -> [0,255]
				col = FunctionsCt.equalizeColourTo255(this, datum);
				for (c=0; c<3; c++) {
					//and now we are looping through the bgr components of the pixel
					//set the colour component c of pixel (i,j)
					newData[c+3*i+3*j*newWidth] = (byte) col;
		        } // colour loop
		    } // column loop
		} // row loop

		return newImage;
    }

	private BufferedImage bilinearInterpolationSampling(int originalWidth, int originalHeight, int newWidth, int newHeight, int slice, int direction) {
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);
		int i, j, c, k;

		byte[] newData = FunctionsCt.getImageData(newImage);
		double col, interpo1, interpo2, interpo3;
		short v_1, v_2, v_3, v_4;
		int I, J;
		
		//exactly how man times the new image is bigger/smaller than the original
		double stepW = (double)newWidth / (double)originalWidth;
		double stepH = (double)newHeight / (double)originalHeight;

		//System.out.println(newHeight+", "+newWidth+", "+originalHeight+", "+originalWidth+", "+stepH+", "+stepW); /*

		for (j = 0; j < newHeight; j++) {
			J = (j < newHeight - stepH) ? (int)((double)(j - (j % (int)stepH)) / stepH) : originalHeight - 2;

		    for (i = 0; i < newWidth; i++) {
				/*J_1 = (int)(j * (double)originalHeight / (double)newHeight);
				I_1 = (int)(i * (double)originalWidth / (double)newWidth);*/
				I = (i < newWidth - stepW) ? (int)((double)(i - (i % (int)stepW)) / stepW) : originalWidth - 2;

				//System.out.println(i+" "+I+"; "+j+" "+J);

				switch (direction) {
					case 1:
					v_1 = cthead[slice][J][I];
					v_2 = cthead[slice][J][I+1];
					v_3 = cthead[slice][J+1][I];
					v_4 = cthead[slice][J+1][I+1];
					break;

					case 2:
					v_1 = cthead[J][I][slice];
					v_2 = cthead[J][I+1][slice];
					v_3 = cthead[J+1][I][slice];
					v_4 = cthead[J+1][I+1][slice];
					break;

					case 3:
					v_1 = cthead[J][slice][I];
					v_2 = cthead[J][slice][I+1];
					v_3 = cthead[J+1][slice][I];
					v_4 = cthead[J+1][slice][I+1];
					break;

					default:
					v_1 = 0;
					v_2 = 0;
					v_3 = 0;
					v_4 = 0;
				}
				
				interpo1 = FunctionsCt.linearInterpolation((double)v_1, (double)v_2, I, (double)i * (double)originalWidth / (double)newWidth, I+1);
				interpo2 = FunctionsCt.linearInterpolation((double)v_3, (double)v_4, I, (double)i * (double)originalWidth / (double)newWidth, I+1);
				interpo3 = FunctionsCt.linearInterpolation(interpo1, interpo2, J, (double)j * (double)originalHeight / (double)newHeight, J+1);

				//calculate the colour by performing a mapping from [min,max] -> [0,255]
				col = FunctionsCt.equalizeColourTo255(this, interpo3);
				if (col > 250)
					System.out.println(interpo3 + " -- " +i + ": " + I + " / " + j + ": " + J + " | " + newData[3*i+3*j*newWidth] + ": " + col + " $ " + v_1+ " $ " + v_2+ " $ " + v_3+ " $ " + v_4);

				for (c=0; c<3; c++) {
					//and now we are looping through the bgr components of the pixel
					//set the colour component c of pixel (i,j)
					newData[c+3*i+3*j*newWidth]=(byte) col;
		        } // colour loop
		    } // column loop
		} // row loop
		
		return newImage;
	}

	private BufferedImage bicubicInterpolationSampling(int originalWidth, int originalHeight, int newWidth, int newHeight, int slice, int direction) {
		return new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);
	}

	public BufferedImage maximumIntensityProjection(int direction) {
		int w = 256, h = (direction == 1) ? 256 : 113;
		int z = (direction == 1) ? 113 : 256;
		short maximum;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

		int i, j, c, k;
        //Obtain pointer to data for fast processing
        byte[] data = FunctionsCt.getImageData(image);

        //System.out.println(data);

		double col;
		short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (j=0; j<h; j++) {
            for (i=0; i<w; i++) {
				maximum = this.min;

				for (k = 0; k < z; k++)
					switch (direction) {
						case 1:
						maximum = (cthead[k][j][i] > maximum) ? cthead[k][j][i] : maximum;
						break;

						case 2:
						maximum = (cthead[j][i][k] > maximum) ? cthead[j][i][k] : maximum;
						break;

						case 3:
						maximum = (cthead[j][k][i] > maximum) ? cthead[j][k][i] : maximum;
						break;

						default:
						maximum = 0;
					}
				
				//calculate the colour by performing a mapping from [min,max] -> [0,255]
				col=(255.0f*((double)maximum-(double)min)/((double)(max-min)));
                for (c=0; c<3; c++) {
					//and now we are looping through the bgr components of the pixel
					//set the colour component c of pixel (i,j)
					data[c+3*i+3*j*w]=(byte) col;
                } // colour loop
            } // column loop
        } // row loop

        return image;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
} // class