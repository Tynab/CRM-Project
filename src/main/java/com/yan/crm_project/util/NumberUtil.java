package com.yan.crm_project.util;

import org.springframework.stereotype.*;

import static java.lang.Integer.*;

@Component
public class NumberUtil {
    // Check string is number or not
    public boolean isNumeric(String s) {
        try {
            parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
