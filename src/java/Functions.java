
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

public class Functions {
    public static byte[] getImageData(BufferedImage image) {
        WritableRaster WR = image.getRaster();
        DataBuffer DB = WR.getDataBuffer();

        if (DB.getDataType() != DataBuffer.TYPE_BYTE)
            throw new IllegalStateException("That's not of type byte");

        return ((DataBufferByte) DB).getData();
    }

    // I want to estimate v (x, y) between v_1 (x_1, x_2) and v_2 (x_2, y_2) though I only need to pass one coordenate each time
    public static double linearInterpolation(double v_1, double v_2, double c_1, double c, double c_2) {
        return v_1 + ((v_2 - v_1) * ((c - c_1) / (c_2 - c_1)));
    }

    public static double equalizeColourTo255(Volume cthead, double colour) {
        return (255.0d * (colour - (double) cthead.getMin()) / ((double) (cthead.getMax() - cthead.getMin())));
    }
}
