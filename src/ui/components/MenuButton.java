package ui.components;

import utils.AppIcons;
import utils.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuButton extends JPanel {
    private String cardName;
    private boolean isActive = false;
    private boolean isHovered = false;
    private JLabel textLabel;
    private MenuClickListener listener;

    public interface MenuClickListener {
        void onMenuClicked(String cardName);
    }

    public MenuButton(String text, String iconType, String cardName, MenuClickListener listener) {
        this.cardName = cardName;
        this.listener = listener;

        setLayout(new BorderLayout());
        setOpaque(false); // Để tự vẽ nền trong paintComponent
        setMaximumSize(new Dimension(280, 50));
        setPreferredSize(new Dimension(280, 50));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(0, 25, 0, 0));

        textLabel = new JLabel(text);
        // Giả định AppIcons trả về Icon, nếu chưa có hãy kiểm tra lại class đó
        textLabel.setIcon(AppIcons.getIcon(iconType, 20));
        textLabel.setIconTextGap(15);
        textLabel.setFont(AppTheme.FONT_REGULAR);
        textLabel.setForeground(AppTheme.TEXT_MAIN);
        add(textLabel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (listener != null) {
                    listener.onMenuClicked(cardName);
                }
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
        // Đổi màu chữ hoặc font khi active
        textLabel.setForeground(active ? AppTheme.PRIMARY_BLUE : AppTheme.TEXT_MAIN);
        textLabel.setFont(active ? AppTheme.FONT_BOLD : AppTheme.FONT_REGULAR);
        repaint();
    }

    public String getCardName() {
        return cardName;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isActive) {
            // Vẽ nền nhẹ cho nút đang chọn
            g2.setColor(new Color(AppTheme.PRIMARY_BLUE.getRed(), AppTheme.PRIMARY_BLUE.getGreen(), AppTheme.PRIMARY_BLUE.getBlue(), 30));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Vẽ vạch xanh bên trái để đánh dấu
            g2.setColor(AppTheme.PRIMARY_BLUE);
            g2.fillRect(0, 0, 5, getHeight());
        } else if (isHovered) {
            // Vẽ nền xám cực nhẹ khi hover
            g2.setColor(new Color(245, 245, 245));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        super.paintComponent(g);
        g2.dispose();
    }
}