package segv;

public class FrozenMarkovChain {
    String[] tokens;
    FrozenRootTransition[] rootTransitions;

    public FrozenMarkovChain(MarkovChain markovChain) {
        int tokenCount = markovChain.tokens.size();
        this.tokens = new String[tokenCount];
        markovChain.tokens.toArray(this.tokens);

        int transitionCount = markovChain.rootTransitions.size();
        this.rootTransitions = new FrozenRootTransition[transitionCount];

        int cumulativeFrequency = 0;

        for (int i = 0; i < transitionCount; i++) {
            RootTransition transition = markovChain.rootTransitions.get(i);

            this.rootTransitions[i] = new FrozenRootTransition(cumulativeFrequency, transition);
            cumulativeFrequency += transition.frequency;
        }
    }
}

class FrozenRootTransition {
    int cumulativeFrequency;
    FrozenTransition[] otherTransitions;

    public FrozenRootTransition(int cumulativeFrequency, RootTransition rootTransition) {
        this.cumulativeFrequency = cumulativeFrequency + rootTransition.frequency;

        int transitionCount = rootTransition.otherTransitions.size();
        this.otherTransitions = new FrozenTransition[transitionCount];

        int otherCumulativeFrequency = 0;

        for (int i = 0; i < transitionCount; i++) {
            Transition transition = rootTransition.otherTransitions.get(i);

            this.otherTransitions[i] = new FrozenTransition(otherCumulativeFrequency, transition);
            otherCumulativeFrequency += transition.frequency;
        }
    }
}

class FrozenTransition {
    int cumulativeFrequency;
    int tokenIndex;

    public FrozenTransition(int cumulativeFrequency, Transition transition) {
        this.cumulativeFrequency = cumulativeFrequency + transition.frequency;
        this.tokenIndex = transition.tokenIndex;
    }
}
