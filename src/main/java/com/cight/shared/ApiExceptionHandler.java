package com.cight.shared;

import com.cight.build.BuildNotFoundException;
import com.cight.analysis.AnalysisNotFoundException;
import com.cight.webhook.DuplicateWebhookDeliveryException;
import com.cight.webhook.InvalidWebhookSignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> validation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = problem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more request fields are invalid.",
                request
        );
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ProblemDetail> malformed(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = problem(
                HttpStatus.BAD_REQUEST,
                "Malformed request",
                "The request body could not be parsed.",
                request
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler({BuildNotFoundException.class, AnalysisNotFoundException.class})
    ResponseEntity<ProblemDetail> notFound(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = problem(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                exception.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(InvalidWebhookSignatureException.class)
    ResponseEntity<ProblemDetail> invalidSignature(
            InvalidWebhookSignatureException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = problem(
                HttpStatus.UNAUTHORIZED,
                "Invalid webhook signature",
                exception.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> illegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = problem(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                exception.getMessage(),
                request
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ProblemDetail> illegalState(
            IllegalStateException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = problem(
                HttpStatus.CONFLICT,
                "Request cannot be completed",
                exception.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(DuplicateWebhookDeliveryException.class)
    ResponseEntity<ProblemDetail> conflict(
            DuplicateWebhookDeliveryException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = problem(
                HttpStatus.CONFLICT,
                "Duplicate webhook delivery",
                exception.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    private ProblemDetail problem(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://cight.dev/problems/" + status.value()));
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}
