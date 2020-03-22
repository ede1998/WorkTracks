package me.erikhennig.worktracks.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class RegexColorDecider implements TextWatcher {

    private TextView element;
    private List<PatternForColor> regexToColor;

    public RegexColorDecider(TextView element, List<PatternForColor> regexToColor) {
        this.element = element;
        this.element.addTextChangedListener(this);
        this.regexToColor = regexToColor;
    }

    private void updateColor(String newText) {
        Optional<PatternForColor> matchedPattern = this.regexToColor.stream().filter(x -> x.matches(newText)).findFirst();

        matchedPattern.ifPresent(patternForColor -> this.element.setTextColor(patternForColor.getColor()));
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
