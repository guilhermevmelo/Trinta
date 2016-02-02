
public class VolumeHistogram {
    public int min, max, range;
    public Volume volume;
    public int[] histogram;

    public VolumeHistogram(int min, int max) {
        this.min = min;
        this.max = max;
        range = max - min + 1;
        this.histogram = new int[range];
        this.reset();
    }

    public VolumeHistogram(Volume volume) {
        this.volume = volume;
        this.min = volume.getMin();
        this.max = volume.getMax();
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
        for (int k = 0; k < volume.Z(); k++) {
            for (int j = 0; j < volume.Y(); j++) {
                for (int i = 0; i < volume.X(); i++) {
                    this.histogram[volume.volume[k][j][i] - this.min]++;
                }
            }
        }
    }

    public VolumeHistogram histogramEqualization(int newMin, int newMax) {
        VolumeHistogram r = new VolumeHistogram(this.min, this.max);

        int t_i = 0;

        for (int i = 0; i < this.range; i++) {
            t_i += this.histogram[i];
            r.histogram[i] = (int) (((double) (newMax - newMin)) * (((double) t_i) / ((double)volume.X() * (double) volume.Y() * (double) volume.Z()))) + newMin;
        }

        return r;
    }
}