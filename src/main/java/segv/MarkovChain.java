package segv;

import java.util.ArrayList;
import java.util.List;

public class MarkovChain {
    public List<String> tokens;
    public List<RootTransition> rootTransitions;

    public MarkovChain() {
        this.tokens = new ArrayList<String>();
        this.rootTransitions = new ArrayList<RootTransition>();
    }

    public void addText(String text) {
        var tokenizer = new Tokenizer(text);
        String lastToken = tokenizer.nextToken();
        String token = tokenizer.nextToken();

        if (lastToken == Tokenizer.NULL_TOKEN)
            return;

        addToken(lastToken);

        while (token != Tokenizer.NULL_TOKEN) {
            addToken(lastToken, token);
            lastToken = token;
            token = tokenizer.nextToken();
        }

        addToken(lastToken, token);
    }

    private void addToken(String token) {
        int tokenIndex = getTokenIndex(token);

        RootTransition transition = this.rootTransitions.get(tokenIndex);
        transition.frequency++;
    }

    private void addToken(String lastToken, String token) {
        int lastTokenIndex = getTokenIndex(lastToken);

        RootTransition rootTransition = this.rootTransitions.get(lastTokenIndex);
        int transitionIndex = getTransitionIndex(rootTransition.otherTransitions, token);

        Transition transition = rootTransition.otherTransitions.get(transitionIndex);
        transition.frequency++;
    }

    private int getTokenIndex(String token) {
        int tokenIndex = -1;
        int tokenCount = this.tokens.size();

        if (tokenCount == 0) {
            this.tokens.add(0, token);
            this.rootTransitions.add(0, new RootTransition());

            return 0;
        }

        int start = 0;
        int end = tokenCount - 1;
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
            int difference = (start < tokenCount) ? token.compareTo(this.tokens.get(start)) : 0;
            tokenIndex = (difference > 0) ? (start + 1) : start;

            this.tokens.add(tokenIndex, token);
            this.rootTransitions.add(tokenIndex, new RootTransition());
        }

        return tokenIndex;
    }

    private int getTransitionIndex(List<Transition> transitions, String token) {
        int transitionIndex = -1;
        int transitionCount = transitions.size();

        if (transitionCount == 0) {
            transitions.add(0, new Transition(token));
            return 0;
        }

        int start = 0;
        int end = transitionCount - 1;
        loop: while (start <= end) {
            int pivot = start + ((end - start) / 2);
            int difference = Integer.signum(token.compareTo(transitions.get(pivot).token));

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
            int difference = (start < transitionCount) ? token.compareTo(transitions.get(start).token) : -1;
            transitionIndex = (difference > 0) ? (start + 1) : start;

            transitions.add(transitionIndex, new Transition(token));
        }

        return transitionIndex;
    }
}

class RootTransition {
    public int frequency;
    public List<Transition> otherTransitions;

    public RootTransition() {
        this.frequency = 0;
        this.otherTransitions = new ArrayList<Transition>();
    }
}

class Transition {
    public int frequency;
    public String token;

    public Transition(String token) {
        this.frequency = 0;
        this.token = token;
    }
}
