package segv;

public class FrozenMarkovChain {
    public String[] tokens;
    public FrozenRootTransition[] rootTransitions;

    public FrozenMarkovChain() {
        this.tokens = new String[0];
        this.rootTransitions = new FrozenRootTransition[0];
    }

    public FrozenMarkovChain(MarkovChain markovChain) {
        int tokenCount = markovChain.tokens.size();
        this.tokens = new String[tokenCount];
        markovChain.tokens.toArray(this.tokens);

        int transitionCount = markovChain.rootTransitions.size();
        this.rootTransitions = new FrozenRootTransition[transitionCount];

        int cumulativeFrequency = 0;

        for (int i = 0; i < transitionCount; i++) {
            RootTransition transition = markovChain.rootTransitions.get(i);

            this.rootTransitions[i] = new FrozenRootTransition(cumulativeFrequency, transition, this.tokens);
            cumulativeFrequency += transition.frequency;
        }
    }

    public static int getTokenIndex(String[] tokens, String token) {
        int tokenIndex = -1;
        int tokenCount = tokens.length;

        if (tokenCount == 0 || token == Tokenizer.NULL_TOKEN)
            return -1;

        int start = 0;
        int end = tokenCount - 1;
        loop: while (start <= end) {
            int pivot = start + ((end - start) / 2);
            int difference = Integer.signum(token.compareTo(tokens[pivot]));

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

        return tokenIndex;
    }
}

class FrozenRootTransition {
    public int cumulativeFrequency;
    public FrozenTransition[] otherTransitions;

    public FrozenRootTransition() {
        this.cumulativeFrequency = 0;
        this.otherTransitions = new FrozenTransition[0];
    }

    public FrozenRootTransition(int cumulativeFrequency, RootTransition rootTransition, String[] tokens) {
        this.cumulativeFrequency = cumulativeFrequency + rootTransition.frequency;

        int transitionCount = rootTransition.otherTransitions.size();
        this.otherTransitions = new FrozenTransition[transitionCount];

        int otherCumulativeFrequency = 0;

        for (int i = 0; i < transitionCount; i++) {
            Transition transition = rootTransition.otherTransitions.get(i);

            this.otherTransitions[i] = new FrozenTransition(otherCumulativeFrequency, transition, tokens);
            otherCumulativeFrequency += transition.frequency;
        }
    }
}

class FrozenTransition {
    public int cumulativeFrequency;
    public int tokenIndex;

    public FrozenTransition() {
        this.cumulativeFrequency = 0;
        this.tokenIndex = -1;
    }

    public FrozenTransition(int cumulativeFrequency, Transition transition, String[] tokens) {
        this.cumulativeFrequency = cumulativeFrequency + transition.frequency;
        this.tokenIndex = FrozenMarkovChain.getTokenIndex(tokens, transition.token);
    }
}
