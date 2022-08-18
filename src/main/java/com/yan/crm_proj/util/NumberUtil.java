package com.yan.crm_proj.util;

import org.springframework.stereotype.*;

import static java.lang.Integer.*;

@Component
public class NumberUtil {
    // Is numeric
    public boolean isNumeric(String s) {
        try {
            parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
