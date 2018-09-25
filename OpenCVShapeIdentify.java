import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class OpenCVShapeIdentify {
    public OpenCVShapeIdentify() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public String identifyShape() {
        Mat src = Imgcodecs.imread("select.png");
        Mat dst = src.clone();
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.adaptiveThreshold(dst, dst, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 3, 3);

        java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        if (contours.size() > 1){
            return "more";
        } else if (contours.size() == 0) {
            return "none";
        }

        Imgproc.drawContours(src, contours, 0, new Scalar(0, 0, 0, 0), 1);
        MatOfPoint2f myPt = new MatOfPoint2f();
        MatOfPoint2f outPt = new MatOfPoint2f();
        contours.get(0).convertTo(myPt, CvType.CV_32FC2);
        double epsilon = 0.01 * Imgproc.arcLength(myPt, true);
        Imgproc.approxPolyDP(myPt, outPt, epsilon, true);
        System.out.println(outPt.toArray().length);
        return getShape(outPt.toArray().length, contours.get(0));

    }

    private String getShape(int len, MatOfPoint mp) {
        String shape;
        switch (len){
            case 3:
                shape ="三角形";
                break;
            case 4:
                Rect rect = Imgproc.boundingRect(mp);
                float width = rect.width;
                float height = rect.height;
                float ar = width / height;
                //计算宽高比，判断是矩形还是正方形
                if (ar >= 0.95 && ar <= 1.05) {
                    shape = "正方形";
                }else {
                    shape = "长方形";
                }
                break;
            case 5:
                shape = "五边形";
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                shape = "多边形";
                break;
            default:
                shape = "圆形";
                break;
        }
        return shape;
    }

}
