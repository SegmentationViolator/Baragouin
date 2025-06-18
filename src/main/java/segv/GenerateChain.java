package segv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Model.CommandSpec;

@Command(name = "generate-chain", description = "Generate a Markov chain from provided text sample(s)")
class GenerateChain implements Callable<Integer> {
    @Spec
    CommandSpec spec;

    // @formatter:off
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Show usage help for the generate-chain command and exit")
    // @formatter:on
    boolean usageHelpRequested;

    @ArgGroup(multiplicity = "1")
    InputFormat inputFormat;

    static class InputFormat {
        @Option(names = { "-t", "--text" }, description = "<inputFile> contains a different text sample on each line")
        boolean text;
        @Option(names = { "-j", "--json" }, description = "<inputFile> contains a JSON array of text samples")
        boolean JSON;
    }

    @Parameters(index = "0", description = "File containing text sample(s)")
    File inputFile;

    @Parameters(index = "1", arity = "0..1", description = "File for the generated Markov chain. Print the output to the STDOUT if this parameter is not provided")
    File ouputFile = null;

    public Integer call() {
        List<String> textSamples;

        if (inputFormat.text) {
            Scanner scanner;

            try {
                scanner = new Scanner(inputFile);
            } catch (FileNotFoundException e) {
                throw new ParameterException(this.spec.commandLine(),
                        String.format("\"%s\" does not exist, is not accessible, or is not a file", this.inputFile, e));
            }

            textSamples = new ArrayList<String>();

            while (scanner.hasNextLine()) {
                textSamples.add(scanner.nextLine());
            }
            scanner.close();
        } else if (inputFormat.JSON) {
            try {
                textSamples = JSON.std.listOfFrom(String.class, this.inputFile);
            } catch (JSONObjectException e) {
                throw new ParameterException(this.spec.commandLine(),
                        String.format("\"%s\" does not contain a JSON array of text samples", this.inputFile, e));
            } catch (IOException e) {
                throw new ParameterException(this.spec.commandLine(),
                        String.format("\"%s\" does not exist, is not accessible, or is not a file", this.inputFile, e));
            }
        } else {
            // unreachable since exactly one option must be true
            throw new AssertionError("all input format options are set to false");
        }

        var markovChain = new MarkovChain();

        for (String textSample : textSamples) {
            markovChain.addText(textSample);
        }

        var frozenMarkovChain = new FrozenMarkovChain(markovChain);

        if (this.ouputFile == null) {
            String output;

            try {
                output = JSON.std.asString(frozenMarkovChain);
            } catch (JSONObjectException e) {
                System.err.println(Ansi.ON.string("@|bold,red Couldn't convert the generated Markov chain to JSON|@"));
                return CommandLine.ExitCode.SOFTWARE;
            } catch (IOException e) {
                // unreachable since the code doesn't deal with IO
                throw new AssertionError("an IOException has occurred");
            }

            System.out.println(output);
            return CommandLine.ExitCode.OK;
        }

        try {
            JSON.std.write(frozenMarkovChain, this.ouputFile);
        } catch (JSONObjectException e) {
            System.err.println(Ansi.ON.string("@|bold,red Couldn't convert the generated Markov chain to JSON|@"));
            return CommandLine.ExitCode.SOFTWARE;
        } catch (IOException e) {
            throw new ParameterException(this.spec.commandLine(),
                    String.format("\"%s\" does not exist, is not accessible, or is not a file", this.inputFile, e));
        }

        return CommandLine.ExitCode.OK;
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new GenerateChain()).execute(args));
    }
}
