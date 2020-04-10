package me.erikhennig.worktracks.ui.colordecider;

import android.content.Context;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

import me.erikhennig.worktracks.R;

public class RegexColorDeciderFactory {

    private static List<RegexColorDecider.PatternForColor> getDurationRedGreenMap(Context colorContext) {
        final int positiveColor = ContextCompat.getColor(colorContext, R.color.colorPositive);
        final int negativeColor = ContextCompat.getColor(colorContext, R.color.colorNegative);
        final int neutralColor = ContextCompat.getColor(colorContext, R.color.colorNeutral);

        return Arrays.asList(
                new RegexColorDecider.PatternForColor("^-?0?0:0?0$", neutralColor),
                new RegexColorDecider.PatternForColor("^-\\d?\\d:\\d?\\d$", negativeColor),
                new RegexColorDecider.PatternForColor("^\\d?\\d:\\d?\\d$", positiveColor),
                new RegexColorDecider.PatternForColor("^-?0[.,]0+$", neutralColor),
                new RegexColorDecider.PatternForColor("^-\\d+[.,]\\d+", negativeColor),
                new RegexColorDecider.PatternForColor("^\\d+[.,]\\d+", positiveColor),
                new RegexColorDecider.PatternForColor("^.*$", neutralColor));
    }

    public static void registerDurationPositiveNegativeDecider(TextView view) {
        new RegexColorDecider(view, getDurationRedGreenMap(view.getContext()));
    }
}
