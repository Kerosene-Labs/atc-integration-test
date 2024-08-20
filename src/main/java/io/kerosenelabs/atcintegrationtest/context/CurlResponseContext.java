package io.kerosenelabs.atcintegrationtest.context;

import lombok.*;

import java.util.Date;

/**
 * Represents the context of a cURL response
 */
@Getter
@ToString
@Builder
public class CurlResponseContext {
    private long duration;
    private Integer curlExitCode;
}
