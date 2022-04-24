package com.sphereex.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Status {
    
    private final boolean success;
    
    private final String message;
}
