package com.yan.crm_proj.util;

import org.springframework.stereotype.*;
import org.thymeleaf.util.*;

import static org.springframework.util.StringUtils.*;

@Component
public class StringUtil {
    // Search word
    public boolean isWordContains(String s, String word) {
        return !hasText(s) ? false : s.toUpperCase().contains(word.toUpperCase());
    }

    // Replace word found by keyword
    public String replaceWordFoundByKeyword(String s, String word, String keyword) {
        return !hasText(s) ? s : s.replaceAll("(?i)" + word + "", keyword);
    }

    // Replace multi by single whitespace
    public String replaceMultiBySingleWhitespace(String s) {
        return !hasText(s) ? s : s.replaceAll("\\s+", " ");
    }

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
        return !hasText(s) ? s : capitalize(s.toLowerCase());
    }

    // Convert to title case advanced
    public String titleCase(String s) {
        return !hasText(s) ? s : StringUtils.capitalizeWords(s.toLowerCase());
    }

    // Capitalize after character
    public String capitalizeAfterChar(String s, String character) {
        if (hasText(s)) {
            var index = s.indexOf(character);
            if (index != -1) {
                s = s.substring(0, index) + character + StringUtils.capitalize(s.substring(index + 1));
            }
        }
        return s;
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

    // Remove all whitespace before character
    public String removeWhiteSpaceBeforeChar(String s, String character) {
        return !hasText(s) ? s : s.replaceAll("\\s+" + character, character);
    }

    // Remove all whitespace after character
    public String removeWhiteSpaceAfterChar(String s, String character) {
        return !hasText(s) ? s : s.replaceAll(character + "\\s+", character);
    }

    // Remove all whitespace before and after character
    public String removeWhiteSpaceBeforeAndAfterChar(String s, String character) {
        return !hasText(s) ? s : s.replaceAll("\\s*" + character + "\\s*", character);
    }
}
