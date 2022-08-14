package com.yan.crm_proj.util;

import org.springframework.stereotype.*;
import org.thymeleaf.util.*;

import static org.springframework.util.StringUtils.*;

@Component
public class StringUtil {
    // Convert to lower case advanced
    public String lowerCase(String s) {
        return !hasText(s) ? s : s.toLowerCase();
    }

    // Convert to upper case advanced
    public String upperCase(String s) {
        return !hasText(s) ? s : s.toUpperCase();
    }

    // Convert to upper case advanced
    public String sentenceCase(String s) {
        return !hasText(s) ? s : capitalize(s);
    }

    // Convert to title case advanced
    public String titleCase(String s) {
        return !hasText(s) ? s : StringUtils.capitalizeWords(s.toLowerCase());
    }

    // Remove all whitespace at begin
    public String removeWhiteSpaceBegin(String s) {
        return !hasText(s) ? s : s.replaceAll("^\\s+", "");
    }

    // Remove all whitespace at end
    public String removeWhiteSpaceEnd(String s) {
        return !hasText(s) ? s : s.replaceAll("\\s+$", "");
    }

    // Remove all whitespace at begin and end
    public String removeWhiteSpaceBeginAndEnd(String s) {
        return !hasText(s) ? s : s.replaceAll("^\\s+|\\s+$", "");
    }

    // Remove all special characters at begin
    public String removeSpCharsBegin(String s) {
        return !hasText(s) ? s : s.replaceAll("^[^a-zA-Z0-9]+", "");
    }

    // Remove all special characters at end
    public String removeSpCharsEnd(String s) {
        return !hasText(s) ? s : s.replaceAll("[^a-zA-Z0-9]+$", "");
    }

    // Remove all special characters at begin and end
    public String removeSpCharsBeginAndEnd(String s) {
        return !hasText(s) ? s : s.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
    }

    // Remove all number and whitespace at begin
    public String removeNumAndWhiteSpaceBegin(String s) {
        return !hasText(s) ? s : s.replaceAll("^[0-9\\s]+", "");
    }

    // Remove all number and whitespace at end
    public String removeNumAndWhiteSpaceEnd(String s) {
        return !hasText(s) ? s : s.replaceAll("[0-9\\s]+$", "");
    }

    // Remove all number and whitespace at begin and end
    public String removeNumAndWhiteSpaceBeginAndEnd(String s) {
        return !hasText(s) ? s : s.replaceAll("^[0-9\\s]+|[0-9\\s]+$", "");
    }

    // Remove all special characters before comma
    public String removeSpCharsBeforeComma(String s) {
        return !hasText(s) ? s : s.replaceAll("[^a-zA-Z0-9]+,", ",");
    }

    // Remove all special characters after comma
    public String removeSpCharsAfterComma(String s) {
        return !hasText(s) ? s : s.replaceAll(",[^a-zA-Z0-9]+", ",");
    }

    // Remove all special characters before and after comma
    public String removeSpCharsBeforeAndAfterComma(String s) {
        return !hasText(s) ? s : removeSpCharsBeforeComma(removeSpCharsAfterComma(s)); // query warning
    }

    // Remove all special characters before dot
    public String removeSpCharsBeforeDot(String s) {
        return !hasText(s) ? s : s.replaceAll("[^a-zA-Z0-9]+\\.", ".");
    }

    // Remove all special characters after dot
    public String removeSpCharsAfterDot(String s) {
        return !hasText(s) ? s : s.replaceAll("\\.[^a-zA-Z0-9]+", ".");
    }

    // Remove all special characters before and after dot
    public String removeSpCharsBeforeAndAfterDot(String s) {
        return !hasText(s) ? s : removeSpCharsBeforeDot(removeSpCharsAfterDot(s)); // query warning
    }

    // Remove all special characters before slash
    public String removeSpCharsBeforeSlash(String s) {
        return !hasText(s) ? s : s.replaceAll("[^a-zA-Z0-9]+/", "/");
    }

    // Remove all special characters after slash
    public String removeSpCharsAfterSlash(String s) {
        return !hasText(s) ? s : s.replaceAll("/[^a-zA-Z0-9]+", "/");
    }

    // Remove all special characters before and after slash
    public String removeSpCharsBeforeAndAfterSlash(String s) {
        return !hasText(s) ? s : removeSpCharsBeforeSlash(removeSpCharsAfterSlash(s)); // query warning
    }

    // Replace multi by single whitespace
    public String replaceMultiBySingleWhitespace(String s) {
        return !hasText(s) ? s : s.replaceAll("\\s+", " ");
    }
}
