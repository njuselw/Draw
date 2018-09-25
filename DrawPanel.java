import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DrawPanel extends JPanel {
    private ArrayList<BufferedImage> imageList = new ArrayList<>();
    private BufferedImage baseImage;
    private Graphics2D g2d;

    public DrawPanel(int width, int height) {
        this.setBackground(Color.white);
        this.setVisible(true);
        this.setBounds(0, 0, width, height);
        baseImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) baseImage.getGraphics();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(baseImage, 0, 0, this);
        for (int i = 0; i < imageList.size(); i++) {
            BufferedImage bi = imageList.get(i);
            g.drawImage(bi, 0, 0, this);
        }
        repaint();
    }

    public void imgAdd(BufferedImage img){
        if (imageList.size() >= 20) {
            g2d.drawImage(imageList.get(0), 0, 0, null);
            imageList.remove(0);
        }
        imageList.add(img);
        System.out.println(imageList.size());
    }

    /**
     * 将画板内容重置的方法，用于新建
     */
    public void reset() {
        baseImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D)baseImage.getGraphics();
        imageList = new ArrayList<>();
    }


    /**
     * 保存图片的方法，用户选择保存路径，图片名以当前时间命名格式为"yyyy_MM_dd_HH_mm_ss"
     */
    public void savePic() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择路径");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int res = fileChooser.showOpenDialog(this);
        if (res == 0) {
            String path = fileChooser.getSelectedFile().toString();
            System.out.println(path);
            BufferedImage saveImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = saveImg.createGraphics();
            this.paint(graphics2D);
            File f = new File(path+"\\"+new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())+".png");
            try {
                ImageIO.write(saveImg, "png", f);
                if (f.exists()) {
                    JOptionPane.showMessageDialog(null, "保存成功", "提示", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "保存失败请重试！", "警告", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "保存失败请重试！", "警告", JOptionPane.WARNING_MESSAGE);
                e.printStackTrace();
            }
        }

    }

    /**
     * 打开图片方法
     */
    public void getPic() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择路径");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*jpg, *png or *jpeg", "jpg", "png", "jpeg");
        fileChooser.addChoosableFileFilter(filter);
        int res = fileChooser.showOpenDialog(this);
        if (res == 0) {
            String path = fileChooser.getSelectedFile().toString();
            System.out.println(path);
            try {
                baseImage = ImageIO.read(new FileInputStream(path));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "不存在这张图片！", "警告", JOptionPane.WARNING_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * 撤销方法
     * @return 判断能否撤销
     */
    public boolean undo() {
        if (imageList.size() > 0) {
            imageList.remove(imageList.size() - 1);
            return true;
        }
        return false;
    }

    /**
     * 切割图片进行保存，以便进行识别
     * @param x 子图片在原图上的坐标x
     * @param y 子图片在原图上的坐标y
     * @param width 子图片的宽
     * @param height 子图片的高
     */
    public void ctrlImg(int x, int y, int width, int height) {
        BufferedImage saveImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = saveImg.createGraphics();
        this.paint(graphics2D);
        BufferedImage selectImg = saveImg.getSubimage(x, y, width, height);
        File file = new File("select.png");
        try {
            ImageIO.write(selectImg, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<BufferedImage> getImageList() {
        return this.imageList;
    }


}
