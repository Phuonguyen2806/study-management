package utils;

import javax.swing.Icon;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AppIcons {
    // Sử dụng HashMap để cache (lưu trữ) các icon đã tạo
    private static final Map<String, Icon> iconCache = new HashMap<>();

    public static Icon getIcon(String type, int size) {
        String cacheKey = type + "_" + size;

        // Nếu icon đã tồn tại trong bộ nhớ, lấy ra dùng luôn
        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }

        // Nếu chưa có, tiến hành tạo mới
        Icon newIcon = new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);

                Color color = c.getForeground();
                g2.setColor(color);

                int stroke = Math.max(2, size / 12);
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                switch(type) {
                    case "assignment":
                        g2.drawRect(size/5, size/8, size*3/5, size*6/8);
                        g2.drawLine(size*2/5, size*3/8, size*3/5, size*3/8);
                        g2.drawLine(size*2/5, size*4/8, size*3/5, size*4/8);
                        g2.drawLine(size*2/5, size*5/8, size*3/5, size*5/8);
                        break;
                    case "calendar":
                        g2.drawRect(size/6, size/6, size*4/6, size*4/6);
                        g2.drawLine(size/6, size*2/6, size*5/6, size*2/6);
                        g2.drawLine(size/3+size/16, size/12, size/3+size/16, size/4);
                        g2.drawLine(size*2/3-size/16, size/12, size*2/3-size/16, size/4);
                        break;
                    case "book":
                        g2.drawLine(size/2, size/4, size/2, size*3/4);
                        g2.drawRect(size/6, size/4, size*2/6, size*2/4);
                        g2.drawRect(size/2, size/4, size*2/6, size*2/4);
                        g2.drawArc(size/6, size/5, size*2/6, size/5, 0, 180);
                        g2.drawArc(size/2, size/5, size*2/6, size/5, 0, 180);
                        break;
                    case "target":
                        g2.drawOval(size/6, size/6, size*4/6, size*4/6);
                        g2.drawOval(size/3, size/3, size/3, size/3);
                        g2.fillOval(size/2-size/16, size/2-size/16, size/8, size/8);
                        g2.drawLine(size/2, size/2, size*5/6+2, size/6-2);
                        g2.drawLine(size*5/6+2, size/6-2, size*4/6, size/6-2);
                        g2.drawLine(size*5/6+2, size/6-2, size*5/6+2, size/3);
                        break;
                    case "chart":
                        g2.drawLine(size/6, size/8, size/6, size*5/6);
                        g2.drawLine(size/6, size*5/6, size*5/6, size*5/6);
                        g2.fillRect(size*2/6, size*4/6, size/8, size/6);
                        g2.fillRect(size*3/6+size/24, size*3/6, size/8, size*2/6);
                        g2.fillRect(size*5/6-size/12, size*2/6, size/8, size*3/6);
                        break;
                    case "timer":
                        g2.drawOval(size/6, size/6, size*4/6, size*4/6);
                        g2.drawLine(size/2, size/2, size/2, size/3);
                        g2.drawLine(size/2, size/2, size*2/3, size*2/3);
                        g2.drawLine(size/2, size/12, size/2, size/6);
                        g2.drawLine(size*3/8, size/12, size*5/8, size/12);
                        break;
                }
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };

        // Lưu vào cache trước khi trả về
        iconCache.put(cacheKey, newIcon);
        return newIcon;
    }
}