import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * @author 161250078
 */
public class BoardFrame extends JFrame {
    private final Dimension Display = Toolkit.getDefaultToolkit().getScreenSize();
    private DrawPanel drawPanel = null;
    private JMenuBar menuBar = null;
    private JMenu fileMenu = new JMenu("文件");
    private JMenu settingMenu = new JMenu("设置");
    private JMenu operationMenu = new JMenu("操作");
    private JMenuItem newItem = new JMenuItem("新建");
    private JMenuItem saveItem = new JMenuItem("保存");
    private JMenuItem openItem = new JMenuItem("打开");
    private JMenuItem cancelItem = new JMenuItem("撤销");
    private JMenuItem colorItem = new JMenuItem("颜色选择");
    private JMenuItem lineItem = new JMenuItem("线条粗细");
    private JMenuItem paintItem = new JMenuItem("绘图");
    private JMenuItem selectItem = new JMenuItem("框选识别");
    private JButton identifyButton = new JButton("识别形状");

    private boolean saved = true;//判断是否保存过
    private BufferedImage temp;

    private Color color = Color.BLACK;//默认颜色为黑色
    private int width = 1;//默认线条粗细
    private Point start;//绘图初始点
    private Point end;//绘图终止点
    private boolean draw = false;
    private boolean identified = false;//是否处于识别模式
    private boolean isSelected = false;//是否框选

    public void initGUI() {
        menuBar = new JMenuBar();
        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(openItem);
        fileMenu.add(cancelItem);
        menuBar.add(fileMenu);
        settingMenu.add(colorItem);
        settingMenu.add(lineItem);
        menuBar.add(settingMenu);
        operationMenu.add(paintItem);
        operationMenu.add(selectItem);
        menuBar.add(operationMenu);
        identifyButton.setFocusPainted(false);
        menuBar.add(identifyButton);

        newItem.addActionListener(new ButtonActionListener());
        saveItem.addActionListener(new ButtonActionListener());
        openItem.addActionListener(new ButtonActionListener());
        cancelItem.addActionListener(new ButtonActionListener());
        colorItem.addActionListener(new ButtonActionListener());
        lineItem.addActionListener(new ButtonActionListener());
        paintItem.addActionListener(new ButtonActionListener());
        selectItem.addActionListener(new ButtonActionListener());
        identifyButton.addActionListener(new ButtonActionListener());

        this.setJMenuBar(menuBar);
        this.setTitle("简单画板");
        this.setLayout(null);
        this.setBounds(Display.width/6,Display.height/24, Display.width*2/3, Display.height*9/10);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        drawPanel = new DrawPanel(Display.width*2/3, Display.height*9/10);

        this.add(drawPanel);
        drawPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Press:" + e.getX() + " " + e.getY());
                if (!draw) {
                    if (isSelected) {
                        drawPanel.undo();
                        isSelected = false;
                    }
                    draw = true;
                    start = new Point(e.getX(), e.getY());
                    end = new Point(e.getX(), e.getY());
                    drawPanel.imgAdd(new BufferedImage(drawPanel.getWidth(), drawPanel.getHeight(), BufferedImage.TYPE_INT_ARGB));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("Release:" + e.getX() + " " + e.getY());
                if (draw) {
                    draw = false;
                    saved = false;
                }
                if (identified) {
                    isSelected = true;
                    BufferedImage bf = drawPanel.getImageList().get(drawPanel.getImageList().size() - 1);
                    Graphics2D g2d = bf.createGraphics();
                    end = new Point(e.getX(), e.getY());
                    int startX = Math.min(start.x, end.x);
                    int startY = Math.min(start.y, end.y);
                    Stroke s = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10, 5}, 0.0f);
                    g2d.setStroke(s);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(startX, startY, Math.abs(start.x - end.x), Math.abs(start.y - end.y));
                    g2d.dispose();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        drawPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draw && !identified) {
                    temp = drawPanel.getImageList().get(drawPanel.getImageList().size() - 1);
                    Graphics2D g2d = temp.createGraphics();
                    g2d.setStroke(new BasicStroke(width));
                    g2d.setColor(color);
                    g2d.drawLine(end.x, end.y, e.getX(), e.getY());
                    temp.setRGB(e.getX(), e.getY(), color.getRGB());
                    end = new Point(e.getX(), e.getY());
                    g2d.dispose();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
    }

    private class ButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            switch (cmd) {
                case "新建":
                    if (!saved) {
                        int n = JOptionPane.showConfirmDialog(null, "当前作画还未保存，是否保存？", "注意", JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (n) {
                            case 0:
                                drawPanel.savePic();
                                drawPanel.reset();
                                saved = true;
                                break;
                            case 1:
                                drawPanel.reset();
                                saved = true;
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case "保存":
                    drawPanel.savePic();
                    saved = true;
                    break;
                case "打开":
                    drawPanel.getPic();
                    saved = true;
                    break;
                case "撤销":
                    if (!drawPanel.undo()) {
                        saved = true;
                        JOptionPane.showMessageDialog(null, "无法撤销！", "提示", JOptionPane.PLAIN_MESSAGE);
                    }
                    break;
                case "颜色选择":
                    color = JColorChooser.showDialog(null, "请选择颜色", Color.BLACK);
                    break;
                case "线条粗细":
                    Object[] widthValues = new Object[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                    Object newWidth = JOptionPane.showInputDialog(null, "选择线条粗细", "线条粗细", JOptionPane.PLAIN_MESSAGE, null, widthValues, widthValues[0]);
                    width = (int)newWidth;
                    break;
                case "绘图":
                    identified = false;
                    break;
                case "框选识别":
                    identified = true;
                    break;
                case "识别形状":
                    if (identified && isSelected) {
                        int startX = Math.min(start.x, end.x);
                        int startY = Math.min(start.y, end.y);
                        //缩小裁剪区域，过滤虚线边框，以防影响识别，同时防止只点了一个点导致长宽为负
                        drawPanel.ctrlImg(startX + 1, startY + 1, Math.max(Math.abs(start.x - end.x) - 1, 1), Math.max(Math.abs(start.y - end.y) - 1, 1));
                        OpenCVShapeIdentify openCVShapeIdentify = new OpenCVShapeIdentify();
                        String shape = openCVShapeIdentify.identifyShape();
                        System.out.println(shape);
                        if (shape.equals("more")) {
                            JOptionPane.showMessageDialog(null, "请只选择一个图形！", "警告", JOptionPane.WARNING_MESSAGE);
                        } else if (shape.equals("none")) {
                            JOptionPane.showMessageDialog(null, "未选择图形！", "警告", JOptionPane.WARNING_MESSAGE);
                        } else {
                            drawPanel.undo();
                            isSelected = false;
                            BufferedImage bf = drawPanel.getImageList().get(drawPanel.getImageList().size() - 1);
                            Graphics2D g2d = bf.createGraphics();
                            g2d.setStroke(new BasicStroke(1.0f));
                            g2d.setColor(Color.BLACK);
                            g2d.drawRect(0, 0, 0, 0);
                            g2d.setFont(new Font("楷体", Font.BOLD, 30));
                            g2d.drawString(shape, startX + Math.abs(start.x - end.x) / 2, startY + Math.abs(start.y - end.y) / 2);
                            g2d.dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "还未选择识别的部分", "警告", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "出现了一些问题！", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        BoardFrame boardFrame = new BoardFrame();
        boardFrame.initGUI();
    }
}
