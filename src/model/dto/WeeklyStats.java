package model.dto;

import model.entity.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class WeeklyStats {
	private double averageFocusTime; // Thời gian tập trung trung bình/ngày (Giờ)
	private Map<LocalDate, Double> studyTimeByDay; // Biến động thời gian học qua các ngày (Thứ 2 -> CN)
	private double completionRate; // Tỉ lệ hoàn thành công việc (%)

	public WeeklyStats(double averageFocusTime, Map<LocalDate, Double> studyTimeByDay, double completionRate) {
		this.averageFocusTime = averageFocusTime;
		this.studyTimeByDay = studyTimeByDay;
		this.completionRate = completionRate;
	}

	// Getters và Setters
	public double getAverageFocusTime() {
		return averageFocusTime;
	}

	public void setAverageFocusTime(double averageFocusTime) {
		this.averageFocusTime = averageFocusTime;
	}

	public Map<LocalDate, Double> getStudyTimeByDay() {
		return studyTimeByDay;
	}

	public void setStudyTimeByDay(Map<LocalDate, Double> studyTimeByDay) {
		this.studyTimeByDay = studyTimeByDay;
	}

	public double getCompletionRate() {
		return completionRate;
	}

	public void setCompletionRate(double completionRate) {
		this.completionRate = completionRate;
	}

}
