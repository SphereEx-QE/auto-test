package com.sphereex.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBInfo {

    private String ip;

    private int port;

    private String user;

    private String password;

    private String dbName;
}
