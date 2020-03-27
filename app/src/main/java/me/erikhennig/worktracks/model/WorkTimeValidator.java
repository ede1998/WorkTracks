package me.erikhennig.worktracks.model;

public class WorkTimeValidator {
    private WorkTimeValidator() {
    }

    public static boolean validate(IWorkTime wt) {
        return wt != null &&
               wt.getDate() != null &&
               wt.getStartingTime() != null &&
               wt.getEndingTime() != null &&
               wt.getBreakDuration() != null;
    }
}
