package net.game.spacepirates.render.buffer;

import java.util.Optional;
import java.util.Stack;

public class FBOStack {

    protected static Stack<IStackableFBO> stack = new Stack<>();

    public static Optional<IStackableFBO> current() {
        if(stack.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(stack.peek());
    }

    public static IStackableFBO push(IStackableFBO fbo) {
        current().ifPresent(IStackableFBO::endFBO);
        fbo.beginFBO();
        return stack.push(fbo);
    }

    public static Optional<IStackableFBO> pop() {
        if(stack.isEmpty()) {
            return Optional.empty();
        }

        IStackableFBO pop = stack.pop();
        pop.endFBO();

        current().ifPresent(IStackableFBO::beginFBO);
        return Optional.of(pop);
    }

}
