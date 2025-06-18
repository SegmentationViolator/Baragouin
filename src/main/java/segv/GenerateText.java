package segv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

@Command(name = "generate-text", description = "Generate text from a previously generated Markov chain")
class GenerateText implements Callable<Integer> {
    @Spec
    CommandSpec spec;

    // @formatter:off
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Show usage help for the generate-text command and exit")
    // @formatter:on
    boolean usageHelpRequested;

    @Parameters(index = "0", description = "File containing the Markov chain")
    File inputFile;

    @Parameters(index = "1", arity = "0..1", description = "Initial prompt for text generation")
    String prompt = null;

    public Integer call() {
        String lastToken = Tokenizer.NULL_TOKEN;

        if (this.prompt != null) {
            var tokenizer = new Tokenizer(this.prompt);
            String token = tokenizer.nextToken();

            while (token != Tokenizer.NULL_TOKEN) {
                lastToken = token;
                token = tokenizer.nextToken();
            }
        }

        FrozenMarkovChain markovChain;

        try {
            markovChain = JSON.std.beanFrom(FrozenMarkovChain.class, this.inputFile);
        } catch (JSONObjectException e) {
            throw new RuntimeException(e);
            // throw new ParameterException(this.spec.commandLine(),
            //         String.format("\"%s\" does not contain a Markov chain", this.inputFile, e));
        } catch (IOException e) {
            throw new ParameterException(this.spec.commandLine(),
                    String.format("\"%s\" does not exist, is not accessible, or is not a file", this.inputFile, e));
        }

        int tokenIndex = FrozenMarkovChain.getTokenIndex(markovChain.tokens, lastToken);
        var rng = new Random();

        if (tokenIndex == -1) {
            if (lastToken != Tokenizer.NULL_TOKEN) {
                System.err.println("Error: Can't generate text with that prompt");
                return CommandLine.ExitCode.SOFTWARE;
            }

            if (markovChain.tokens.length == 0) {
                System.err.println("Error: Can't generate text with this Markov chain");
                return CommandLine.ExitCode.SOFTWARE;
            }

            int randomNumber = rng
                    .nextInt(markovChain.rootTransitions[markovChain.tokens.length - 1].cumulativeFrequency);
            for (int i = 0; i < markovChain.rootTransitions.length; i++) {
                if (randomNumber < markovChain.rootTransitions[i].cumulativeFrequency) {
                    tokenIndex = i;
                    break;
                }
            }
        }

        var generatedTokens = new ArrayList<String>();

        while (tokenIndex != -1) {
            generatedTokens.add(markovChain.tokens[tokenIndex]);

            FrozenTransition[] transitions = markovChain.rootTransitions[tokenIndex].otherTransitions;

            if (transitions.length == 0) break;

            int randomNumber = rng.nextInt(transitions[transitions.length - 1].cumulativeFrequency);

            for (int i = 0; i < transitions.length; i++) {
                if (randomNumber < transitions[i].cumulativeFrequency) {
                    tokenIndex = transitions[i].tokenIndex;
                    break;
                }
            }
        }

        System.out.println(String.join(" ", generatedTokens));

        return CommandLine.ExitCode.OK;
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new GenerateChain()).execute(args));
    }
}
