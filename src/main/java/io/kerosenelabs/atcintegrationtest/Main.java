package io.kerosenelabs.atcintegrationtest;

import io.kerosenelabs.arg.ArgumentParser;
import io.kerosenelabs.arg.Command;
import io.kerosenelabs.arg.CommandModifier;
import io.kerosenelabs.arg.exception.*;

import java.util.List;

public class Main {
    public static void main(String[] args) throws CommandModifierNotFoundException, MalformedCommandModifierException, EapSubcommandNotFoundException, EapMissingSubcommandException, CommandNotSpecifiedException, CommandNotFoundException, DuplicateCommandException {
        ArgumentParser argumentParser = new ArgumentParser("atc-integration-test", "Integration tests for ATC");
        argumentParser.addCommand(new Command("execute", "Execute the unit tests") {
            @Override
            public void execute(List<CommandModifier> list) {
                System.out.println("Test");
            }
        });
        argumentParser.parse(args);
    }
}