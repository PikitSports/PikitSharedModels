package com.pikit.shared.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHandler implements RequestHandler<TestHandlerRequest, TestHandlerResponse> {
    @Override
    public TestHandlerResponse handleRequest(TestHandlerRequest input, Context context) {
        log.info("Request received: {}", input);
        return TestHandlerResponse.builder()
                .echo(input.getEcho())
                .build();
    }
}
