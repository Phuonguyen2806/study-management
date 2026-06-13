package model.repository;

import model.entity.Goal;
import model.entity.GoalStatus;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GoalRepositoryImpl
        implements IGoalRepository {

    private static final String FILE_PATH =
            "data/goals.txt";

    @Override
    public List<Goal> loadGoalsByUserId(
            String userId) {

        List<Goal> goalList =
                new ArrayList<>();

        try {

            Path path =
                    Paths.get(FILE_PATH);

            if (!Files.exists(path)) {
                return goalList;
            }

            List<String> lines =
                    Files.readAllLines(path);

            int lineNumber = 0;

            for (String line : lines) {

                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts =
                        line.split("\\|");

                if (parts.length >= 8) {

                    String fileUserId =
                            parts[0].trim();

                    if (fileUserId.equals(userId)) {

                        try {

                            LocalDate date =
                                    LocalDate.parse(
                                            parts[1].trim());

                            int id =
                                    Integer.parseInt(
                                            parts[2].trim());

                            String title =
                                    parts[3].trim();

                            double currentValue =
                                    Double.parseDouble(
                                            parts[4].trim());

                            double targetValue =
                                    Double.parseDouble(
                                            parts[5].trim());

                            String unit =
                                    parts[6].trim();

                            GoalStatus status =
                                    GoalStatus.valueOf(
                                            parts[7].trim());

                            Goal goal =
                                    new Goal(
                                            id,
                                            title,
                                            date,
                                            targetValue,
                                            unit
                                    );

                            goal.setCurrentValue(
                                    currentValue);

                            goal.setStatus(
                                    status);

                            goalList.add(goal);

                        } catch (Exception e) {

                            System.err.println(
                                    "Lỗi dòng "
                                            + lineNumber
                                            + ": "
                                            + e.getMessage()
                            );
                        }
                    }
                }
            }

        } catch (IOException e) {

            System.err.println(
                    "Lỗi đọc Goal File: "
                            + e.getMessage());
        }

        return goalList;
    }

    @Override
    public void saveGoalsByUserId(
            String userId,
            List<Goal> goals) {

        try {

            Path path =
                    Paths.get(FILE_PATH);

            List<String> allLines =
                    new ArrayList<>();

            if (Files.exists(path)) {

                List<String> currentLines =
                        Files.readAllLines(path);

                for (String line : currentLines) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts =
                            line.split("\\|");

                    if (parts.length >= 8) {

                        String fileUserId =
                                parts[0].trim();

                        if (!fileUserId.equals(userId)) {

                            allLines.add(line);
                        }
                    }
                }
            }

            for (Goal g : goals) {

                String line =
                        String.format(
                                "%s | %s | %d | %s | %s | %s | %s | %s",
                                userId,
                                g.getTargetDate(),
                                g.getGoalID(),
                                g.getTitle(),
                                g.getCurrentValue(),
                                g.getTargetValue(),
                                g.getUnit(),
                                g.getStatus().name()
                        );

                allLines.add(line);
            }

            Files.write(
                    path,
                    allLines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException e) {

            System.err.println(
                    "Lỗi lưu Goal File: "
                            + e.getMessage());
        }
    }
}