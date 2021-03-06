package me.erikhennig.worktracks.ui.colordecider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class RegexColorDecider implements TextWatcher {

    private static final String TAG = RegexColorDecider.class.getName();

    private TextView element;
    private List<PatternForColor> regexToColor;

    public RegexColorDecider(TextView element, List<PatternForColor> regexToColor) {
        this.element = element;
        this.element.addTextChangedListener(this);
        this.regexToColor = regexToColor;
    }

    private void updateColor(String newText) {
        Log.d(TAG, String.format("Checking if color must be updated for text [%s]", newText));
        Optional<PatternForColor> matchedPattern = this.regexToColor.stream().filter(x -> x.matches(newText)).findFirst();

        if (matchedPattern.isPresent()) {
            PatternForColor pattern = matchedPattern.get();
            Log.d(TAG, String.format("Pattern [%s] matched. Setting new color [%s].", pattern.pattern, pattern.color));
            this.element.setTextColor(pattern.getColor());
        }
        else{
            Log.d(TAG, "No pattern matched to change the color.");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        this.updateColor(editable.toString());
    }

    public static class PatternForColor {
        private Pattern pattern;
        private int color;

        public PatternForColor(String pattern, int color) {
            this.pattern = Pattern.compile(pattern);
            this.color = color;
        }

        @SuppressWarnings("unused") // might be useful later
        public PatternForColor(Pattern pattern, int color) {
            this.pattern = pattern;
            this.color = color;
        }

        public int getColor() {
            return this.color;
        }

        public boolean matches(String input) {
            return this.pattern.matcher(input).matches();
        }
    }
}
