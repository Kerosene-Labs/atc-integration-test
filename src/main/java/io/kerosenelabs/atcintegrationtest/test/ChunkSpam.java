package io.kerosenelabs.atcintegrationtest.test;

import io.kerosenelabs.atcintegrationtest.IntegrationTest;
import io.kerosenelabs.atcintegrationtest.TerminalUtil;
import io.kerosenelabs.atcintegrationtest.context.CurlResponseContext;
import lombok.SneakyThrows;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ChunkSpam implements IntegrationTest {
    private static final Integer ITERATIONS = 1_000;
    private static final Integer CHUNK_SIZE = 100;

    private static String getPropertyOrElse(String propertyName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        return propertyValue;
    }

    @SneakyThrows
    @Override
    public void execute() {
        // clear the terminal
        TerminalUtil.clear();

        // get properties
        String host = getPropertyOrElse("atc.integrationTest.host", "localhost:8443");

        // initialize our response contexts list
        List<CurlResponseContext> responseContexts = Collections.synchronizedList(new ArrayList<>());

        // initialize our inter-thread ui slots
        AtomicReference<Boolean> running = new AtomicReference<>(true);
        AtomicReference<Boolean> isSleeping = new AtomicReference<>(false);

        // start our UI thread
        Thread.ofVirtual().name("ui").start(() -> {
            while (running.get()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                TerminalUtil.printOnLine("Test: " + this.getClass().getSimpleName() + " : " + getDescription(), 1);
                TerminalUtil.printOnLine(responseContexts.size() + "/" + ITERATIONS, 2);
                TerminalUtil.printOnLine("Sleeping: " + isSleeping.get(), 3);
                if (responseContexts.size() >= ITERATIONS) {
                    running.set(false);
                }
            }
        });

        // do our iterations with curl as our backend
        int x = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            Thread.ofVirtual().start(() -> {
                Instant before = Instant.now();
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(
                            "curl", "--insecure", "-H", "Host: simple-echo",
                            "-H", "X-ATC-IdentityToken: N2UgODMgZTggMTMgZDYgZGIgYmQgYzIgMzMgOTMgMzYgMGUgZjMgMDIgNzEgNzI=",
                            "https://" + host);
                    Process process = processBuilder.start();

                    int exitCode = process.waitFor();
                    Instant after = Instant.now();
                    CurlResponseContext responseContext = CurlResponseContext.builder()
                            .duration(Duration.between(before, after).toNanos())
                            .curlExitCode(exitCode)
                            .build();
                    responseContexts.add(responseContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            x++;
            if (x >= CHUNK_SIZE && isSleeping.get().equals(false)) {
                isSleeping.set(true);
                Thread.sleep(500);
                x = 0;
                isSleeping.set(false);
            }
        }

        while (true) {
            if (responseContexts.size() == ITERATIONS) {
                break;
            }
        }

        // calculate average duration
        long avgNanosDuration = 0;
        for (CurlResponseContext responseContext : responseContexts) {
            avgNanosDuration += responseContext.getDuration();
        }
        avgNanosDuration = avgNanosDuration / ITERATIONS;
        System.out.println("\n\nAvg duration: " + avgNanosDuration / 1_000_000 + "ms");
    }

    @Override
    public String getDescription() {
        return "Spam an ATC service " + ITERATIONS + " times, in chunks of " + CHUNK_SIZE;
    }

}
