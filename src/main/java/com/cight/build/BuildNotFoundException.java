package com.cight.build;

import java.util.UUID;

public class BuildNotFoundException extends RuntimeException {
    public BuildNotFoundException(UUID id) {
        super("Build event not found: " + id);
    }
}
