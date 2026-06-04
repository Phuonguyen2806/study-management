package view;

import model.entity.Task;
import model.entity.TaskStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StatisticsPanel extends JPanel {
	private final Color COLOR_PRIMARY = new Color(0, 102, 204);
	private final Color COLOR_BG = new Color(245, 247, 251);
	private int doneP = 0, progressP = 0, overdueP = 0;
	// Thành phần Thống kê Ngày
	private JLabel lblDailyStudyTime, lblPomodoroCount, lblPendingTasks, lblInProgressTasks, lblDoneTasks,
			lblOverdueTasks, lblTotalTasks;
	private JTable overdueTable, upcomingTable;
	private DefaultTableModel overdueModel, upcomingModel;

	// Thành phần Thống kê Tuần
	private JLabel lblWeeklyAvgTime, lblWeeklyCompletionRate;
	private JPanel pnlBarChart, pnlPieChart;
	private JTable tblUpcomingTasks;
	private DefaultTableModel tableModel;

	// Biến lưu trữ dữ liệu thực tế để vẽ đồ thị động lên paintComponent
	private Map<LocalDate, Double> studyTimeData;
	private double weeklyCompletionRateData = 0.0;

	private final SimpleDateFormat tableDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public StatisticsPanel() {
		setLayout(new BorderLayout());
		setBackground(COLOR_BG);
		initComponents();
	}

	private void initComponents() {
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

		tabPane.addTab("Thống kê Ngày", createDailyView());
		tabPane.addTab("Thống kê Tuần", createWeeklyView());

		add(tabPane, BorderLayout.CENTER);

	}

	public void showNoDataMessage() {
		JOptionPane.showMessageDialog(this,
				"Bạn chưa có dữ liệu học tập trong khoảng thời gian này. Hãy bắt đầu phiên Pomodoro đầu tiên!",
				"Thông báo", JOptionPane.INFORMATION_MESSAGE);
	}

	public void showError(String message) {
		JOptionPane.showMessageDialog(this, "Lỗi: " + message, "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
	}

	public void refresh() {
		revalidate();
		repaint();
	}

	// --- CÁC PHƯƠNG THỨC HIỂN THỊ ĐỒNG BỘ DỮ LIỆU ---

	/** THỐNG KÊ NGÀY */
	public void displayDailyStudyTime(double time) {
		lblDailyStudyTime.setText(time + " giờ");
	}

	public void displayPomodoroCount(int count) {
		lblPomodoroCount.setText(String.valueOf(count));
	}

	public void displayTaskStatus(Map<TaskStatus, Integer> stats) {
		int pending = stats.getOrDefault(TaskStatus.PENDING, 0);
		int inProgress = stats.getOrDefault(TaskStatus.IN_PROGRESS, 0);
		int done = stats.getOrDefault(TaskStatus.DONE, 0);
		int overdue = stats.getOrDefault(TaskStatus.OVERDUE, 0);

		// Cập nhật "Công việc chưa hoàn thành" (Pending + In Progress + Overdue)
		if (lblTotalTasks != null) {
			lblTotalTasks.setText(String.valueOf(pending + inProgress + overdue));
		}

		// Cập nhật "Công việc đã hoàn thành"
		if (lblDoneTasks != null) {
			lblDoneTasks.setText(String.valueOf(done));
		}
	}

	public void displayDailyTables(List<Task> overdueData, List<Task> upcomingData) {
		overdueModel.setRowCount(0);
		for (Task task : overdueData) {
			overdueModel.addRow(new Object[] { task.getTitle(),
					task.getDeadline() != null ? tableDateFormat.format(task.getDeadline()) : "Không có",
					task.getPriority() });
		}

		upcomingModel.setRowCount(0);
		for (Task task : upcomingData) {
			upcomingModel.addRow(new Object[] { task.getTitle(),
					task.getDeadline() != null ? tableDateFormat.format(task.getDeadline()) : "Không có",
					task.getPriority() });
		}
	}

	/** THỐNG KÊ TUẦN */
	public void displayWeeklyStudyTime(double avgTime) {
		String text = String.format("%.1f h", avgTime);
		this.lblWeeklyAvgTime.setText(text);
		lblWeeklyAvgTime.setText(avgTime + " giờ/tuần");
	}

	public void showStudyTimeChart(Map<LocalDate, Double> data) {
		this.studyTimeData = data;
	    pnlBarChart.repaint();
	}

	
	// Trong StatisticsPanel.java
	public void showTaskCompletionPieChart(double completionRate) {
	    // 1. Cập nhật label (làm tròn để hiển thị cho đẹp)
	    String text = String.format("%.0f%%", completionRate);
	    this.lblWeeklyCompletionRate.setText(text);
	    
	    // 2. Cập nhật dữ liệu cho biểu đồ (Vẫn dùng giá trị gốc để vẽ chính xác)
	    this.weeklyCompletionRateData = completionRate; 
	    
	    // 3. Logic repaint
	    pnlPieChart.repaint();
	}

	// Sửa tham số từ List<Object[]> thành List<Task>
	public void displayUpcomingWeekTasks(List<Task> upcomingWeekTasks) {
		if (this.tableModel != null) {
	        tableModel.setRowCount(0); // Xóa dữ liệu cũ
	        for (Task task : upcomingWeekTasks) {
	            String deadlineStr = (task.getDeadline() != null) ? tableDateFormat.format(task.getDeadline()) : "Không có";
	            tableModel.addRow(new Object[] { 
	                task.getTitle(), 
	                deadlineStr, 
	                task.getPriority() 
	            });
	        }
	    }
	}

	// --- GIAO DIỆN CHI TIẾT ---

	private JPanel createDailyView() {
		JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
		mainPanel.setBackground(COLOR_BG);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

		// --- PHẦN TRÊN: 4 Ô THỐNG KÊ CƠ BẢN ---
		JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
		statsPanel.setOpaque(false);
		statsPanel.add(createStatCard(
				"<html>Tổng giờ tập<br/> trung</html>", // Thêm HTML và thẻ <br/>
				lblDailyStudyTime = new JLabel("0.0 giờ"),
				"",
				new Color(0, 102, 204)
		));
		statsPanel.add(createStatCard(
				"<html>Pomodoro<br/>hoàn thành</html>",
				lblPomodoroCount = new JLabel("0 Pomo"),
				"",
				new Color(255, 87, 34)
		));
		statsPanel.add(createStatCard(
				"<html>Công việc chưa<br/>hoàn thành</html>",
				lblTotalTasks = new JLabel("0"),
				"",
				new Color(0, 150, 136)
		));
		statsPanel.add(createStatCard(
				"<html>Công việc<br/>đã hoàn thành</html>",
				lblDoneTasks = new JLabel("0"),
				"",
				new Color(40, 167, 69)
		));

		// --- PHẦN DƯỚI: 2 DANH SÁCH BẢNG TÁC VỤ ---
		JPanel listsContainer = new JPanel(new GridLayout(2, 1, 0, 20));
		listsContainer.setOpaque(false);

		// Bảng 1: Trễ hạn trong ngày
		overdueModel = new DefaultTableModel(new String[] { "Tên công việc", "Hạn chót", "Mức độ" }, 0);
		overdueTable = new JTable(overdueModel);
		listsContainer.add(createListSection("Công việc trễ hạn trong ngày", overdueTable, new Color(220, 53, 69)));

		// Bảng 2: Sắp đến hạn trong ngày
		upcomingModel = new DefaultTableModel(new String[] { "Tên công việc", "Hạn chót", "Mức độ" }, 0);
		upcomingTable = new JTable(upcomingModel);
		listsContainer
				.add(createListSection("Công việc sắp đến hạn trong ngày", upcomingTable, new Color(0, 102, 204)));

		mainPanel.add(statsPanel, BorderLayout.NORTH);
		mainPanel.add(listsContainer, BorderLayout.CENTER);

		return mainPanel;
	}

	private JPanel createListSection(String title, JTable table, Color titleColor) {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setOpaque(false);

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblTitle.setForeground(titleColor);

		// Cấu hình bảng
		table.setRowHeight(25);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		table.getTableHeader().setBackground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

		panel.add(lblTitle, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	// Thêm hàm này để Test hoặc gọi từ Controller
	public void updateDailyTables(List<Object[]> overdueData, List<Object[]> upcomingData) {
		overdueModel.setRowCount(0);
		for (Object[] row : overdueData)
			overdueModel.addRow(row);

		upcomingModel.setRowCount(0);
		for (Object[] row : upcomingData)
			upcomingModel.addRow(row);
	}

	private JPanel createWeeklyView() {
		JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
		mainPanel.setBackground(COLOR_BG);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

		// 1. Top Cards: Tóm tắt tuần
		JPanel topCards = new JPanel(new GridLayout(1, 2, 20, 0));
		topCards.setOpaque(false);
		topCards.add(createStatCard("Trung bình thời gian tập trung trong tuần", lblWeeklyAvgTime = new JLabel("0.0"), "",
				new Color(0, 102, 204)));
		topCards.add(createStatCard("Tỷ lệ hoàn thành công việc trong tuần", lblWeeklyCompletionRate = new JLabel("0%"), "",
				new Color(0, 102, 204)));
		mainPanel.add(topCards, BorderLayout.NORTH);

		// 2. Center: Biểu đồ - SỬ DỤNG GRIDLAYOUT
		JPanel chartPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		chartPanel.setOpaque(false);
		// Tăng chiều cao tối thiểu cho khu vực biểu đồ để hình tròn không bị dẹt
		chartPanel.setPreferredSize(new Dimension(0, 300));

		// BIỂU ĐỒ CỘT
		pnlBarChart = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        if (studyTimeData == null) return;

		        Graphics2D g2 = (Graphics2D) g;
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		        int n = studyTimeData.size();
		        int margin = 30;
		        int spacing = 15;

				int availableWidth = Math.max(0, getWidth() - (2 * margin));
				int barWidth = (n > 0) ? (availableWidth - (spacing * (n - 1))) / n : 0;
		        int baseLineY = getHeight() - 40;
				int maxHeight = Math.max(0, getHeight() - 100);
		        double maxHours = 10.0; // Đặt mốc trần cho biểu đồ

		        int i = 0;
		        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Font cho số giờ
		        FontMetrics fm = g2.getFontMetrics();

		        for (Map.Entry<LocalDate, Double> entry : studyTimeData.entrySet()) {
		            double hours = entry.getValue();
		            int x = margin + i * (barWidth + spacing);

					int barHeight = (maxHours > 0) ? (int) ((hours / maxHours) * maxHeight) : 0;
		            // 1. Vẽ cột
					g2.setPaint(new GradientPaint(x, baseLineY - barHeight, COLOR_PRIMARY, x, baseLineY, new Color(100, 180, 255)));
					// Chỉ vẽ nếu barWidth và barHeight > 0 để tránh lỗi fillRect
					if (barWidth > 0 && barHeight > 0) {
						g2.fillRoundRect(x, baseLineY - barHeight, barWidth, barHeight, 8, 8);
					}
		            // 2. Vẽ nhãn Thứ (Tiếng Việt)
		            g2.setColor(Color.GRAY);
		            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
		            String dayLabel = switch (entry.getKey().getDayOfWeek()) {
		                case MONDAY -> "T2"; case TUESDAY -> "T3"; case WEDNESDAY -> "T4";
		                case THURSDAY -> "T5"; case FRIDAY -> "T6"; case SATURDAY -> "T7";
		                case SUNDAY -> "CN";
		            };
		            int labelX = x + (barWidth - g2.getFontMetrics().stringWidth(dayLabel)) / 2;
		            g2.drawString(dayLabel, labelX, baseLineY + 20);

		            // 3. Vẽ số giờ trên đỉnh cột
		            g2.setColor(Color.DARK_GRAY);
		            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		            String hourText = String.format("%.1f", hours) + "h";
		            int hourX = x + (barWidth - fm.stringWidth(hourText)) / 2;
		            g2.drawString(hourText, hourX, baseLineY - barHeight - 5);

		            i++;
		        }
		    }
		};
		setupChartStyle(pnlBarChart, "Thời gian tập trung các ngày trong tuần");

		pnlPieChart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// 1. Cấu hình thông số
				this.setBackground(Color.WHITE);
				int paddingBottom = 60;
				int size = Math.min(getWidth(), getHeight() - paddingBottom) - 60;
				int x = (getWidth() - size) / 2;
				int y = 30;
				int donePercent = StatisticsPanel.this.doneP;
				int progressPercent = StatisticsPanel.this.progressP;
				int overduePercent = StatisticsPanel.this.overdueP;
				if (donePercent == 0 && progressPercent == 0 && overduePercent == 0) {
					// Vẽ một hình tròn xám nếu không có dữ liệu
					g2.setColor(Color.LIGHT_GRAY);
					g2.fillOval(x, y, size, size);
					return;
				}
				// Chuyển sang độ (tổng 360)
				int angleDone = (int) (donePercent * 3.6);
				int angleProgress = (int) (progressPercent * 3.6);
				int angleOverdue = 360 - angleDone - angleProgress;
				// 2. Vẽ biểu đồ Pie
				g2.setColor(new Color(40, 167, 69)); // Xanh lá
				g2.fillArc(x, y, size, size, 0, angleDone);
				g2.setColor(new Color(255, 193, 7)); // Vàng
				g2.fillArc(x, y, size, size, angleDone, angleProgress);
				g2.setColor(new Color(220, 53, 69)); // Đỏ
				g2.fillArc(x, y, size, size, angleDone + angleProgress, angleOverdue);
				// 3. Vẽ chữ % lên từng phần bánh
				g2.setColor(Color.WHITE);
				g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
				drawPercentText(g2, x, y, size, 0, angleDone, donePercent + "%");
				drawPercentText(g2, x, y, size, angleDone, angleProgress, progressPercent + "%");
				drawPercentText(g2, x, y, size, angleDone + angleProgress, angleOverdue, overduePercent + "%");
				// 4. Vẽ lỗ trắng ở giữa (Donut)
				g2.setColor(Color.WHITE);
				int innerSize = size / 2;
				int innerX = x + (size - innerSize) / 2;
				int innerY = y + (size - innerSize) / 2;
				g2.fillOval(innerX, innerY, innerSize, innerSize);
				// 5. Chú thích bên dưới (Legend) - Giống image_e64875.png
				drawLegend(g2, x + size / 2, y + size + 40);
			}

			// Hàm phụ để tính toán vị trí và vẽ text %
			private void drawPercentText(Graphics2D g2, int x, int y, int size, int startAngle, int arcAngle,
					String text) {
				if (arcAngle < 15)
					return; // Nếu phần bánh quá nhỏ thì không vẽ chữ để tránh lem

				double midAngle = Math.toRadians(startAngle + arcAngle / 2.0);
				double radius = size / 2.8; // Vị trí chữ (nằm giữa tâm và viền ngoài)

				int centerX = x + size / 2;
				int centerY = y + size / 2;

				int drawX = (int) (centerX + radius * Math.cos(midAngle));
				int drawY = (int) (centerY - radius * Math.sin(midAngle)); // Trục Y trong Java ngược với toán học

				FontMetrics fm = g2.getFontMetrics();
				g2.drawString(text, drawX - fm.stringWidth(text) / 2, drawY + fm.getAscent() / 2);
			}

			private void drawLegend(Graphics2D g2, int centerX, int y) {
				String[] labels = { "Hoàn thành", "Đang làm", "Trễ hạn" };
				Color[] colors = { new Color(40, 167, 69), new Color(255, 193, 7), new Color(220, 53, 69) };
				int xOffset = -120;

				g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				for (int i = 0; i < 3; i++) {
					g2.setColor(colors[i]);
					g2.fillRoundRect(centerX + xOffset, y, 12, 12, 4, 4);
					g2.setColor(Color.DARK_GRAY);
					g2.drawString(labels[i], centerX + xOffset + 18, y + 10);
					xOffset += 90;
				}
			}
		};
		setupChartStyle(pnlPieChart, "Tỉ lệ công việc trong tuần");

		chartPanel.add(pnlBarChart);
		chartPanel.add(pnlPieChart);
		mainPanel.add(chartPanel, BorderLayout.CENTER);

		return mainPanel;
	}

	/**
	 * Hàm phụ trợ để tránh lặp code setup style
	 */
	private void setupChartStyle(JPanel panel, String title) {
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)), title),
				BorderFactory.createEmptyBorder(20, 20, 20, 20)));
	}

	private JPanel createStatCard(String title, JLabel valueLabel, String icon, Color accentColor) {
		JPanel card = new JPanel(new BorderLayout(10, 5));
		card.setBackground(Color.WHITE);

		// Tạo viền bo góc và đổ bóng nhẹ bằng LineBorder màu nhạt
		card.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(235, 238, 242), 1, true),
						BorderFactory.createEmptyBorder(15, 15, 15, 15)));

		// Tiêu đề nhỏ bên trên
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblTitle.setForeground(new Color(120, 130, 140));

		// Giá trị số lớn ở giữa - Sử dụng màu accent Color để làm nổi bật
		valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
		valueLabel.setForeground(accentColor);

		// Biểu tượng icon bên phải
		JLabel lblIcon = new JLabel(icon);
		lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 35));

		card.add(lblTitle, BorderLayout.NORTH);
		card.add(valueLabel, BorderLayout.CENTER);
		card.add(lblIcon, BorderLayout.EAST);

		return card;
	}
	    // Hàm để Controller gọi cập nhật
	    public void updatePieChartData(int done, int progress, int overdue) {
	        this.doneP = done;
	        this.progressP = progress;
	        this.overdueP = overdue;
	        pnlPieChart.repaint(); // Yêu cầu vẽ lại với dữ liệu mới
	    }
	
}
