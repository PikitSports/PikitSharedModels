package com.pikit.shared.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestHandler implements RequestHandler<TestHandlerRequest, TestHandlerResponse> {
    @Override
    public TestHandlerResponse handleRequest(TestHandlerRequest input, Context context) {
        log.info("Request received: {}", input);
        return TestHandlerResponse.builder()
                .echo(input.getEcho())
                .build();
    }
}
