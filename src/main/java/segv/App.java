package segv;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Model.CommandSpec;

// @formatter:off
@Command(
    name = "baragouin",
    description = "A text generator that can generate Markov chains from text and generate text from the generated Markov chains",
    synopsisSubcommandLabel = "COMMAND",
    subcommands = { HelpCommand.class, GenerateChain.class, GenerateText.class },
    version = "Baragouin 0.1.0-SNAPSHOT"
)
// @formatter:on
class App implements Runnable {
    @Spec
    CommandSpec spec;

    @Option(names = { "-V", "--version" }, versionHelp = true, description = "Print version information and exit")
    boolean versionRequested;

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Show usage help")
    boolean usageHelpRequested;

    public void run() {
        throw new ParameterException(this.spec.commandLine(), "Missing required subcommand");
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new App()).execute(args));
    }
}
