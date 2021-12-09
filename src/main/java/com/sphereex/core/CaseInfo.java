package com.sphereex.core;

import lombok.Getter;
import lombok.Setter;

// Case meta data
@Setter
@Getter
public class CaseInfo {

    private String name;

//    feature which case belong to
    private String feature;

//    case tag
    private String tag;

//    describe of case
    private String message;

//    case status
    private boolean status;
}
