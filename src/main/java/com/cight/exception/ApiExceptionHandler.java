package com.cight.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BuildEventNotFoundException.class)
    public ProblemDetail handleBuildNotFound(BuildEventNotFoundException exception,
                                             HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, "The requested build does not exist.");
        problem.setType(URI.create("urn:cight:problem:build-not-found"));
        problem.setTitle("Build not found");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableBody(HttpMessageNotReadableException exception,
                                              HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "The request body is invalid or contains an unsupported value.");
        problem.setType(URI.create("urn:cight:problem:invalid-request"));
        problem.setTitle("Invalid request");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}
