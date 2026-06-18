package com.yan.crm_project.util;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import static org.springframework.util.StringUtils.*;

@Component
/**
 * Tom tat: Chuan hoa doan van ban tu do, gom khoang trang quanh dau cau,
 * ngoac va viet hoa sau dau ket thuc cau.
 */
public class TextUtil {
    private static final String[] BRACKET_PATTERNS = { "\\(", "\\)", "\\[", "\\]", "\\{", "\\}", "<", ">" };
    private static final String[][] BRACKET_SPACE_RULES = { { "\\(", " \\(" }, { "\\)", "\\) " }, { "\\[", " \\[" },
            { "\\]", "\\] " }, { "\\{", " \\{" }, { "\\}", "\\} " }, { "<", " <" }, { ">", "> " } };
    private static final String[] PUNCTUATION_TRIM_PATTERNS = { "\\.", ",", ";", ":", "\\?", "!" };
    private static final String[] PUNCTUATION_WITH_SPACE = { ".", ",", ";", ":", "?", "!" };
    private static final String[] SENTENCE_BOUNDARIES = { ".", "?", "!" };

    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private NumberUtil numberUtil;

    /**
     * Them mot khoang trang sau dau cau dau tien neu ky tu ke tiep khong phai so.
     */
    public String charWithSpace(String s, String character) {
        if (hasText(s)) {
            var index = s.indexOf(character);
            if (index != -1) {
                var iStart = index + 1;
                if (iStart < s.length() && !numberUtil.isNumeric(s.substring(iStart, iStart + 1))) {
                    s = s.substring(0, index) + character + " " + s.substring(index + 1);
                }
            }
        }
        return s;
    }

    /**
     * Chuan hoa text nghiep vu truoc khi luu role/project/task.
     */
    public String parseToLegalText(String s) {
        if (!hasText(s)) {
            return s;
        }

        var text = stringUtil.removeWhiteSpaceBeginAndEnd(s);
        for (var pattern : BRACKET_PATTERNS) {
            text = stringUtil.removeWhiteSpaceBeforeAndAfterChar(text, pattern);
        }
        for (var rule : BRACKET_SPACE_RULES) {
            text = stringUtil.replaceWordFoundByKeyword(text, rule[0], rule[1]);
        }
        for (var pattern : PUNCTUATION_TRIM_PATTERNS) {
            text = stringUtil.removeWhiteSpaceBeforeAndAfterChar(text, pattern);
        }
        for (var punctuation : PUNCTUATION_WITH_SPACE) {
            text = charWithSpace(text, punctuation);
        }
        for (var boundary : SENTENCE_BOUNDARIES) {
            text = stringUtil.capitalizeAfterChar(text, boundary);
        }

        return capitalize(stringUtil.replaceMultiBySingleWhitespace(stringUtil.removeWhiteSpaceBeginAndEnd(text)));
    }
}
