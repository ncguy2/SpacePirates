package net.game.spacepirates.input;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class InputAxis {

    public List<AxisEntry> entries = new ArrayList<>();
    public CompositionRule rule = CompositionRule.AdditiveClamped;

    public float resolve() {
        float value = 0;
        boolean first = true;
        for (AxisEntry entry : entries) {
            if (entry.action.test()) {
                if (rule.equals(CompositionRule.First)) {
                    return entry.scale;
                }

                if (first) {
                    value = entry.scale;
                    first = false;
                } else {
                    value = rule.resolve(value, entry.scale);
                }
            }
        }

        if(rule.shouldClamp) {
            value = Math.max(-1, Math.min(value, 1));
        }

        return value;
    }

    public InputAxis add(InputAction action, float scale) {
        AxisEntry entry = new AxisEntry();
        entry.action = action;
        entry.scale = scale;
        this.entries.add(entry);
        return this;
    }

    public static InputAxis create() {
        return create(CompositionRule.AdditiveClamped);
    }

    public static InputAxis create(CompositionRule rule) {
        InputAxis axis = new InputAxis();
        if(rule != null) {
            axis.rule = rule;
        }
        return axis;
    }

    public static enum CompositionRule {
        Additive(Float::sum),
        AdditiveClamped(Float::sum, true),
        Multiplicative((a, b) -> a * b),
        MultiplicativeClamped((a, b) -> a * b, true),
        First((a, b) -> b),
        ;

        final BiFunction<Float, Float, Float> function;
        final boolean shouldClamp;

        CompositionRule(BiFunction<Float, Float, Float> function) {
            this(function, false);
        }

        CompositionRule(BiFunction<Float, Float, Float> function, boolean shouldClamp) {
            this.function = function;
            this.shouldClamp = shouldClamp;
        }

        public float resolve(float a, float b) {
            return function.apply(a, b);
        }

    }

    public static class AxisEntry {
        public InputAction action;
        public float scale;
    }

}
