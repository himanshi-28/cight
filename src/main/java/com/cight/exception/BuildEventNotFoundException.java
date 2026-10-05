package com.cight.exception;

public class BuildEventNotFoundException extends RuntimeException {
    public BuildEventNotFoundException(String id) {
        super("Build event not found: " + id);
    }
}
