
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class CTHeadHistogram {
	public int min, max, range;
	public Cthead cthead;
	public int[] histogram;

	public CTHeadHistogram(int min, int max) {
		this.min = min;
		this.max = max;
		range = max - min + 1;
		this.histogram = new int[range];
		this.reset();
	}

	public CTHeadHistogram(Cthead cthead) {
		this.cthead = cthead;
		this.min = cthead.getMin();
		this.max = cthead.getMax();
		this.range = max - min + 1;
		this.histogram = new int[range];
		this.reset();
	}

	// You aske for the actual value
	public int getValue(int level) {
		//return this.histogram[level - this.min][0];
		return this.histogram[level];
	
	}

	public void reset() {
		for (int i = 0; i < range; i++) {
			this.histogram[i] = 0;
		}
	}

	public void fill() {
		for (int k = 0; k < 113; k++) {
			for (int j = 0; j < 256; j++) {
				for (int i = 0; i < 256; i++) {
					this.histogram[cthead.cthead[k][j][i] - this.min]++;
				}
			}
		}
	}

	public CTHeadHistogram histogramEqualization(int newMin, int newMax) {
		CTHeadHistogram r = new CTHeadHistogram(this.min, this.max);
		
		int t_i = 0;

		for (int i = 0; i < this.range; i++) {
			t_i += this.histogram[i];
			r.histogram[i] = (int)(((double)(newMax - newMin))*(((double)t_i)/(256.0d*256.0d*113.0d))) + newMin;
		}
		
		return r;
	}
}