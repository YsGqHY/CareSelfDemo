import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class JumpWindow {
    private final static Random RANDOM = new Random();
    private final static List<String> TIPS = Arrays.asList(
            "天天开心",
            "别熬夜",
            "早点休息",
            "天冷了，多穿衣服",
            "顺顺利利",
            "保持好心情",
            "梦想成真",
            "多喝水",
            "今天过得开心吗",
            "好好爱自己",
            "记得吃水果",
            "好好吃饭",
            "ABC"
    );

    public static void main(String[] args) throws InterruptedException {
        int count = 0;
        int max = 5;

        while (true) {
            for (String tip : TIPS) {
                count++;
                if (count > max) return;
                JFrame frame = new JFrame("温馨提示");

                frame.setSize(300,140);

                frame.getContentPane().setBackground(getRandomColor());

                randomFrameLocation(frame);

                JLabel label = new JLabel(tip, JLabel.CENTER);
                label.setFont(new Font("微软雅黑", Font.BOLD, 30));
                label.setForeground(Color.WHITE);

                frame.add(label);

                frame.setVisible(true);

                Thread.sleep(40L);
            }
        }

    }

    private static void randomFrameLocation(JFrame frame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        int width = toolkit.getScreenSize().width;
        int height = toolkit.getScreenSize().height;

        frame.setLocation(RANDOM.nextInt(width),RANDOM.nextInt(height));
    }

    private static Color getRandomColor() {
        int red = RANDOM.nextInt(255);
        int green = RANDOM.nextInt(255);
        int blue = RANDOM.nextInt(255);
        return new Color(red, green, blue);
    }
}
