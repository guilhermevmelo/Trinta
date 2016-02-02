
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Histogram {
	public int min, max, range;
	private int[][] histogram;

	public static final int RED 	= 2;
	public static final int GREEN 	= 1;
	public static final int BLUE 	= 0;

	public Histogram (int min, int max) {
		this.min = min;
		this.max = max;
		this.range = max - min + 1;
		histogram = new int[range][3];
		this.reset();
	}

	public int getValue(int level, int color) {
		
		return this.histogram[level][color];
	}

	public void fill(Cthead cthead, int slice, int direction) {
		BufferedImage image = cthead.getImage(slice, direction);
		byte imageData[] = Functions.getImageData(image);

		int stop = (direction == 1) ? 256 : 113;
		stop *= 256*3;

		for (int i = 0; i < stop; i++) {
			this.histogram[imageData[i] - min][i%3]++;
		}

	}

	public void reset() {
		for (int i = 0; i < range; i++) {
			this.histogram[i][0] = 0;
			this.histogram[i][1] = 0;
			this.histogram[i][2] = 0;
		}
	}

	public static Histogram histogramEqualization(Histogram source, int newMin, int newMax) {
		Histogram r = new Histogram(newMin, newMax);

		return r;
	}
}