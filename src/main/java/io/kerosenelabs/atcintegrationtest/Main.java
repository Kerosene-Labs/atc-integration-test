package io.kerosenelabs.atcintegrationtest;

import io.kerosenelabs.arg.ArgumentParser;
import io.kerosenelabs.arg.Command;
import io.kerosenelabs.arg.CommandModifier;
import io.kerosenelabs.arg.ExceptionHandler;
import io.kerosenelabs.arg.exception.*;
import lombok.SneakyThrows;

import java.util.List;

public class Main {
    public static void main(String[] args) throws CommandModifierNotFoundException, MalformedCommandModifierException, EapSubcommandNotFoundException, EapMissingSubcommandException, CommandNotSpecifiedException, CommandNotFoundException, DuplicateCommandException {
        ArgumentParser argumentParser = new ArgumentParser("atc-integration-test", "Integration tests for ATC");
        argumentParser.addExceptionHandler(new ExceptionHandler(Exception.class) {
            @Override
            public void execute(Exception e) {
                System.out.println(e.getMessage());
            }
        });
        argumentParser.addCommand(new Command("execute", "Execute the unit tests") {
            @SneakyThrows
            @Override
            public void execute(List<CommandModifier> list) {
                var classes = ClassUtil.getClasses("io.kerosenelabs.atcintegrationtest.test");
                var integrationTests = ClassUtil.filterClassesByImplementation(classes, IntegrationTest.class);
                for (Class<IntegrationTest> integrationTest : integrationTests) {
                    var constructor = integrationTest.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    var instance = constructor.newInstance();
                    System.out.println("Executing test: " + integrationTest.getSimpleName() + " || " + instance.getDescription());
                    instance.execute();
                }
            }
        });
        argumentParser.parse(args);
    }
}