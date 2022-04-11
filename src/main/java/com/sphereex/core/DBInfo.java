package com.sphereex.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DBInfo {

    private final String ip;

    private final int port;

    private final String user;

    private final String password;

    private final String dbName;
}
