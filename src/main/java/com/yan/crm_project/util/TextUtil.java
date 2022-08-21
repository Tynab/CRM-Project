package com.yan.crm_project.util;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import static org.springframework.util.StringUtils.*;

//Ex: " ai?đứng như bóng dừa!tóc(dài )bay( trong gió).  có phải,người còn đó:là con gái của Bến Tre thời 4.0. "
@Component
public class TextUtil {
    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private NumberUtil numberUtil;

    // Add whitespace after character
    public String charWithSpace(String s, String character) {
        // If string is not empty
        if (hasText(s)) {
            var index = s.indexOf(character);
            // Found character
            if (index != -1) {
                var iStart = index + 1;
                // Next character is not number
                if (iStart < s.length() && !numberUtil.isNumeric(s.substring(iStart, iStart + 1))) {
                    s = s.substring(0, index) + character + " " + s.substring(index + 1);
                }
            }
        }
        return s;
    }

    // Parse string to legal text
    public String parseToLegalText(String s) {
        return capitalize(stringUtil.replaceMultiBySingleWhitespace(stringUtil.removeWhiteSpaceBeginAndEnd(
                charWithSpace(charWithSpace(charWithSpace(charWithSpace(charWithSpace(charWithSpace(stringUtil
                        .capitalizeAfterChar(stringUtil.capitalizeAfterChar(stringUtil.capitalizeAfterChar(stringUtil
                                .removeWhiteSpaceBeforeAndAfterChar(stringUtil.removeWhiteSpaceBeforeAndAfterChar(
                                        stringUtil.removeWhiteSpaceBeforeAndAfterChar(
                                                stringUtil.removeWhiteSpaceBeforeAndAfterChar(
                                                        stringUtil.removeWhiteSpaceBeforeAndAfterChar(
                                                                stringUtil.removeWhiteSpaceBeforeAndAfterChar(
                                                                        stringUtil.replaceWordFoundByKeyword(stringUtil
                                                                                .replaceWordFoundByKeyword(stringUtil
                                                                                        .replaceWordFoundByKeyword(
                                                                                                stringUtil
                                                                                                        .replaceWordFoundByKeyword(
                                                                                                                stringUtil
                                                                                                                        .replaceWordFoundByKeyword(
                                                                                                                                stringUtil
                                                                                                                                        .replaceWordFoundByKeyword(
                                                                                                                                                stringUtil
                                                                                                                                                        .replaceWordFoundByKeyword(
                                                                                                                                                                stringUtil
                                                                                                                                                                        .replaceWordFoundByKeyword(
                                                                                                                                                                                stringUtil
                                                                                                                                                                                        .removeWhiteSpaceBeforeAndAfterChar(
                                                                                                                                                                                                stringUtil
                                                                                                                                                                                                        .removeWhiteSpaceBeforeAndAfterChar(
                                                                                                                                                                                                                stringUtil
                                                                                                                                                                                                                        .removeWhiteSpaceBeforeAndAfterChar(
                                                                                                                                                                                                                                stringUtil
                                                                                                                                                                                                                                        .removeWhiteSpaceBeforeAndAfterChar(
                                                                                                                                                                                                                                                stringUtil
                                                                                                                                                                                                                                                        .removeWhiteSpaceBeforeAndAfterChar(
                                                                                                                                                                                                                                                                stringUtil
                                                                                                                                                                                                                                                                        .removeWhiteSpaceBeforeAndAfterChar(
                                                                                                                                                                                                                                                                                stringUtil
                                                                                                                                                                                                                                                                                        .removeWhiteSpaceBeforeAndAfterChar(
                                                                                                                                                                                                                                                                                                stringUtil
                                                                                                                                                                                                                                                                                                        .removeWhiteSpaceBeforeAndAfterChar(
                                                                                                                                                                                                                                                                                                                stringUtil
                                                                                                                                                                                                                                                                                                                        .removeWhiteSpaceBeginAndEnd(
                                                                                                                                                                                                                                                                                                                                s),
                                                                                                                                                                                                                                                                                                                "\\("),
                                                                                                                                                                                                                                                                                                "\\)"),
                                                                                                                                                                                                                                                                                "\\["),
                                                                                                                                                                                                                                                                "\\]"),
                                                                                                                                                                                                                                                "\\{"),
                                                                                                                                                                                                                                "\\}"),
                                                                                                                                                                                                                "<"),
                                                                                                                                                                                                ">"),
                                                                                                                                                                                "\\(",
                                                                                                                                                                                " \\("),
                                                                                                                                                                "\\)",
                                                                                                                                                                "\\) "),
                                                                                                                                                "\\[",
                                                                                                                                                " \\["),
                                                                                                                                "\\]",
                                                                                                                                "\\] "),
                                                                                                                "\\{",
                                                                                                                " \\{"),
                                                                                                "\\}", "\\} "),
                                                                                        "<", " <"),
                                                                                ">", "> "),
                                                                        "\\."),
                                                                ","),
                                                        ";"),
                                                ":"),
                                        "\\?"), "!"),
                                "."), "?"), "!"),
                        "."), ","), ";"), ":"), "?"), "!"))));
    }
}
