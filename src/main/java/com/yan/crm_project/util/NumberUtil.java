package com.yan.crm_project.util;

import org.springframework.stereotype.*;

import static java.lang.Integer.*;

@Component
/**
 * Tom tat: Cung cap cac phep kiem tra so don gian cho cac util xu ly chuoi.
 */
public class NumberUtil {
    /**
     * Kiem tra chuoi co phai so nguyen hop le hay khong.
     */
    public boolean isNumeric(String s) {
        try {
            parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
