package autonomouscarproject.raspividj;

import autonomouscarproject.raspividj.attributes.BoolAttribute;
import autonomouscarproject.raspividj.attributes.IntAttribute;
import autonomouscarproject.raspividj.attributes.StrAttribute;
import autonomouscarproject.raspividj.attributes.enums.Exposure;
import autonomouscarproject.raspividj.attributes.enums.RawFormat;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.photonvision.common.logging.LogGroup;
import org.photonvision.common.logging.Logger;
import org.photonvision.common.util.TestUtils;

import java.io.IOException;

public class RaspiMain {
    private static Logger logger = new Logger(RaspiMain.class, LogGroup.General);

    @SuppressWarnings("UnusedAssignment")
    public static void main(String[] args) {
        try {

            TestUtils.loadLibraries();
            TestUtils.loadLibraries();

            // 3 bytes per pixel
            int frameSize = 1920 * 1080 * 3;
            byte[] rawFrameData = new byte[frameSize];
            int offset = 0;

            var process = Runtime.getRuntime().exec("raspivid -w 1920 -h 1080 -fps 30 -rf rgb -o -");

//            Streamer s = new Streamer(
//                BoolAttribute.NOPREVIEW,
//                IntAttribute.ISO.set(new Integer[]{1000}),
//                StrAttribute.OUTPUT.set(new String[]{"-"}),
//                Exposure.EXPOSURE,
//                IntAttribute.WIDTH,
//                IntAttribute.HEIGHT//,
////                RawFormat.RAW_FORMAT
//            ).start();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//            var buff = ByteBuffer.allocate(frameSize);
            Mat mat = new Mat(1920, 1080, CvType.CV_8UC1);
            int i = 0;
            var stdOut = process.getInputStream();

            while (true) {
                System.out.println("Avaliable: " + stdOut.available());
                if (stdOut.available() < frameSize) continue;

                //noinspection SpellCheckingInspection
                int retcode = stdOut.readNBytes(rawFrameData, offset, frameSize);

                if (retcode == 0 || retcode == -1) {
                    System.out.println("Read failed with code " + retcode);
                } else {
                    mat.put(0, 0, rawFrameData);
                    System.out.println("Got frame: " + mat);
                    Imgcodecs.imwrite("capture" + i + ".jpg", mat);
                    mat.release();
                }

                offset += frameSize;
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
