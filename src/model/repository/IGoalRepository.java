package model.repository;

import model.entity.Goal;

import java.util.List;

public interface IGoalRepository {

    List<Goal> loadGoalsByUserId(String userId);

    void saveGoalsByUserId(
            String userId,
            List<Goal> goals
    );
}