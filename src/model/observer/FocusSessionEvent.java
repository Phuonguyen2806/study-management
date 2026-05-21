package model.observer;

import model.entity.StudySession;
import model.entity.Task;

public class FocusSessionEvent {
    private Task task;
    private StudySession studySession;

    public FocusSessionEvent(Task task, StudySession studySession) {
        this.task = task;
        this.studySession = studySession;
    }

    public Task getTask() {
        return task;
    }

    public StudySession getStudySession() {
        return studySession;
    }
}
