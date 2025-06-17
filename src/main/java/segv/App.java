package segv;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Model.CommandSpec;

@Command(name = "baragouin", synopsisSubcommandLabel = "COMMAND", subcommands = {}, version = "0.1.0")
class App implements Runnable {
    @Spec CommandSpec spec;

    public void run() {
        throw new ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new App()).execute(args));
    }
}
