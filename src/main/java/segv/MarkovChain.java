package segv;

import java.util.ArrayList;
import java.util.List;

public class MarkovChain {
    List<String> tokens;
    List<RootTransition> rootTransitions;

    public MarkovChain() {
        this.tokens = new ArrayList<String>();
        this.rootTransitions = new ArrayList<RootTransition>();
    }

    public void addText(String text) {
        var tokenizer = new Tokenizer(text);
        String lastToken = tokenizer.nextToken();
        String token = tokenizer.nextToken();

        if (lastToken == null) return;

        addToken(lastToken);

        while (token != null) {
            addToken(lastToken, token);
            lastToken = token;
            token = tokenizer.nextToken();
        }
    }

    private void addToken(String token) {
        int tokenIndex = getTokenIndex(token);

        RootTransition transition = this.rootTransitions.get(tokenIndex);
        transition.frequency++;
    }

    private void addToken(String lastToken, String token) {
        int tokenIndex = getTokenIndex(token);
        int lastTokenIndex = getTokenIndex(lastToken);

        RootTransition rootTransition = this.rootTransitions.get(lastTokenIndex);
        int transitionIndex = getTransitionIndex(rootTransition.otherTransitions, tokenIndex);

        Transition transition = rootTransition.otherTransitions.get(transitionIndex);
        transition.frequency++;
    }

    private int getTokenIndex(String token) {
        int tokenIndex = -1;

        int start = 0;
        int end = this.tokens.size() - 1;
        loop: while (start <= end) {
            int pivot = start + ((end - start) / 2);
            int difference = Integer.signum(token.compareTo(this.tokens.get(pivot)));

            switch (difference) {
                case -1:
                    end = pivot - 1;
                    break;
                case 0:
                    tokenIndex = pivot;
                    break loop;
                case 1:
                    start = pivot + 1;
                    break;
            }
        }

        if (tokenIndex == -1) {
            int difference = token.compareTo(this.tokens.get(start));
            tokenIndex = (difference > 0) ? (start + 1) : start;

            this.tokens.add(tokenIndex, token);
            this.rootTransitions.add(tokenIndex, new RootTransition());
        }

        return tokenIndex;
    }

    private int getTransitionIndex(List<Transition> transitions, int tokenIndex) {
        int transitionIndex = -1;

        int start = 0;
        int end = transitions.size() - 1;
        loop: while (start <= end) {
            int pivot = start + ((end - start) / 2);
            int difference = Integer.signum(tokenIndex - transitions.get(pivot).tokenIndex);

            switch (difference) {
                case -1:
                    end = pivot - 1;
                    break;
                case 0:
                    transitionIndex = pivot;
                    break loop;
                case 1:
                    start = pivot + 1;
                    break;
            }
        }

        if (transitionIndex == -1) {
            int difference = tokenIndex - transitions.get(start).tokenIndex;
            transitionIndex = (difference > 0) ? (start + 1) : start;

            transitions.add(transitionIndex, new Transition(tokenIndex));
        }

        return transitionIndex;
    }
}

class RootTransition {
    int frequency;
    List<Transition> otherTransitions;

    public RootTransition() {
        this.frequency = 0;
        this.otherTransitions = new ArrayList<Transition>();
    }
}

class Transition {
    int frequency;
    int tokenIndex;

    public Transition(int tokenIndex) {
        this.frequency = 0;
        this.tokenIndex = tokenIndex;
    }
}
