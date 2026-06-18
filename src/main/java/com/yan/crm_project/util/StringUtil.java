package com.yan.crm_project.util;

import org.springframework.stereotype.*;
import org.thymeleaf.util.*;

import static org.springframework.util.StringUtils.*;

@Component
/**
 * Tom tat: Chuan hoa chuoi dau vao cho ten, email va cac thao tac replace/trim
 * dung lai o service va util khac.
 */
public class StringUtil {
    /**
     * Kiem tra chuoi co chua tu khoa, khong phan biet hoa thuong.
     */
    public boolean isWordContains(String s, String word) {
        return hasText(s) ? s.toUpperCase().contains(word.toUpperCase()) : false;
    }

    /**
     * Thay the theo regex khong phan biet hoa thuong.
     */
    public String replaceWordFoundByKeyword(String s, String word, String keyword) {
        return hasText(s) ? s.replaceAll("(?i)" + word + "", keyword) : s;
    }

    /**
     * Rut gon nhieu khoang trang lien tiep thanh mot khoang trang.
     */
    public String replaceMultiBySingleWhitespace(String s) {
        return hasText(s) ? s.replaceAll("\\s+", " ") : s;
    }

    /**
     * Chuan hoa ho ten tieng Viet o dang title case.
     */
    public String parseName(String s) {
        return hasText(s) ? titleCase(replaceMultiBySingleWhitespace(removeNumAndWhiteSpaceBeginAndEnd(s))) : s;
    }

    /**
     * Chuan hoa ten quoc te bang cach bo ky tu dac biet o hai dau.
     */
    public String parseNameInternational(String s) {
        return hasText(s)
                ? titleCase(
                        replaceMultiBySingleWhitespace(removeSpCharsBeginAndEnd(removeNumAndWhiteSpaceBeginAndEnd(s))))
                : s;
    }

    /**
     * Chuan hoa ten in tren the theo dang viet hoa.
     */
    public String parseNameOnCard(String s) {
        return hasText(s)
                ? upperCase(
                        replaceMultiBySingleWhitespace(removeSpCharsBeginAndEnd(removeNumAndWhiteSpaceBeginAndEnd(s))))
                : s;
    }

    /**
     * Chuan hoa email ve lowercase va cat ky tu thua o hai dau.
     */
    public String parseEmail(String s) {
        return hasText(s) ? lowerCase(removeSpCharsBeginAndEnd(removeNumAndWhiteSpaceEnd(s))) : s;
    }

    // Convert to lower case advanced
    public String lowerCase(String s) {
        return hasText(s) ? s.toLowerCase() : s;
    }

    // Convert to upper case advanced
    public String upperCase(String s) {
        return hasText(s) ? s.toUpperCase() : s;
    }

    // Convert to capitalize case advanced
    public String capitalizeCase(String s) {
        return hasText(s) ? capitalize(s) : s;
    }

    // Convert to sentence case advanced
    public String sentenceCase(String s) {
        return hasText(s) ? capitalize(s.toLowerCase()) : s;
    }

    // Convert to title case advanced
    public String titleCase(String s) {
        return hasText(s) ? StringUtils.capitalizeWords(s.toLowerCase()) : s;
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
        return hasText(s) ? s.replaceAll("^\\s+", "") : s;
    }

    // Remove all whitespace at end
    public String removeWhiteSpaceEnd(String s) {
        return hasText(s) ? s.replaceAll("\\s+$", "") : s;
    }

    // Remove all whitespace at begin and end
    public String removeWhiteSpaceBeginAndEnd(String s) {
        return hasText(s) ? s.replaceAll("^\\s+|\\s+$", "") : s;
    }

    // Remove all special characters at begin
    public String removeSpCharsBegin(String s) {
        return hasText(s) ? s.replaceAll("^[^a-zA-Z0-9]+", "") : s;
    }

    // Remove all special characters at end
    public String removeSpCharsEnd(String s) {
        return hasText(s) ? s.replaceAll("[^a-zA-Z0-9]+$", "") : s;
    }

    // Remove all special characters at begin and end
    public String removeSpCharsBeginAndEnd(String s) {
        return hasText(s) ? s.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "") : s;
    }

    // Remove all number and whitespace at begin
    public String removeNumAndWhiteSpaceBegin(String s) {
        return hasText(s) ? s.replaceAll("^[0-9\\s]+", "") : s;
    }

    // Remove all number and whitespace at end
    public String removeNumAndWhiteSpaceEnd(String s) {
        return hasText(s) ? s.replaceAll("[0-9\\s]+$", "") : s;
    }

    // Remove all number and whitespace at begin and end
    public String removeNumAndWhiteSpaceBeginAndEnd(String s) {
        return hasText(s) ? s.replaceAll("^[0-9\\s]+|[0-9\\s]+$", "") : s;
    }

    // Remove all whitespace before character
    public String removeWhiteSpaceBeforeChar(String s, String character) {
        return hasText(s) ? s.replaceAll("\\s+" + character, character) : s;
    }

    // Remove all whitespace after character
    public String removeWhiteSpaceAfterChar(String s, String character) {
        return hasText(s) ? s.replaceAll(character + "\\s+", character) : s;
    }

    // Remove all whitespace before and after character
    public String removeWhiteSpaceBeforeAndAfterChar(String s, String character) {
        return hasText(s) ? s.replaceAll("\\s*" + character + "\\s*", character) : s;
    }
}
