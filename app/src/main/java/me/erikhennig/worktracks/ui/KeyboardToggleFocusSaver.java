package me.erikhennig.worktracks.ui;

import android.view.View;

/**
 * This class prevents an input field from loosing focus when the soft keyboard
 * becomes visible.
 * To use it, the input field must have this class as OnFocusChangeListener or
 * call the onFocusChange method in its listener.
 * A single instance of this class is to be shared per root view.
 *
 * The class is based on this StackOverflow answer: https://stackoverflow.com/a/22470233
 */
public class KeyboardToggleFocusSaver implements View.OnFocusChangeListener {
    private static final int MIN_DELTA = 300;           // threshold in ms

    /**
     * Time when the view gained focus.
     */
    private long focusTime = 0;

    /**
     * Currently focused view.
     */
    private View focusTarget = null;

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        long delta = this.getTimeSinceLastFocus();
        if (hasFocus) {
            if (delta > MIN_DELTA) {
                this.update(view);
            }
        } else {
            if (delta <= MIN_DELTA && view == this.focusTarget) {
                this.requestFocus();
            }
        }
    }

    private void update(View view) {
        this.focusTarget = view;
        this.focusTime = System.currentTimeMillis();
    }

    private long getTimeSinceLastFocus() {
        return System.currentTimeMillis() - this.focusTime;
    }

    private void requestFocus() {
        this.focusTarget.post(() -> this.focusTarget.requestFocus());
    }

}
