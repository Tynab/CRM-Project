package com.yan.crm_proj.util;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.thymeleaf.util.*;

import lombok.*;

import static org.springframework.util.StringUtils.*;

@Component
@RequiredArgsConstructor
public class AddressUtil {
    @Autowired
    private final StringUtil stringUtil;

    // Re-format violate rule one minus
    private String reformatViolateOneMinus(String s) {
        return s.length() - s.replaceAll("-", "").length() > 1 ? s.replaceAll("-", ",") : s;
    }

    // Re-format violate rule single dot
    private String reformatViolateSingleDot(String s) {
        return s.replaceAll("\\.{2,}", ".");
    }

    // Insert keyword "tỉnh" or "thành phố" before city name
    private String insertKeywordTinhThanh(String s) {
        return !hasText(s) ? s : s.replaceAll(",(?!.*,)", ",TP.");
    }

    // Search word
    public boolean isWordContains(String s, String word) {
        return !hasText(s) ? false : s.toUpperCase().contains(word.toUpperCase());
    }

    // Replace word found by keyword
    public String replaceWordFoundByKeyword(String s, String word, String keyword) {
        return !hasText(s) ? s : s.replaceAll("(?i)" + word + "", keyword);
    }

    // Reparse string to legal address
    // Ex: " 6C Đường số 8,phường.linh Tây- Quận Thủ đức-HCM "
    public String reparseToLegalAddress(String s) {
        if (hasText(s)) {
            s = stringUtil
                    .removeSpCharsBeforeAndAfterSlash(stringUtil
                            .removeSpCharsBeforeAndAfterComma(StringUtils.capitalizeWords(replaceWordFoundByKeyword(
                                    replaceWordFoundByKeyword(
                                            reformatViolateOneMinus(stringUtil.removeSpCharsBeginAndEnd(s)), ",", ", "),
                                    "\\.", "\\. "))));
            s = stringUtil.removeSpCharsBeforeAndAfterDot(reformatViolateSingleDot(replaceWordFoundByKeyword(
                    replaceWordFoundByKeyword(replaceWordFoundByKeyword(replaceWordFoundByKeyword(
                            replaceWordFoundByKeyword(replaceWordFoundByKeyword(replaceWordFoundByKeyword(
                                    replaceWordFoundByKeyword(replaceWordFoundByKeyword(replaceWordFoundByKeyword(
                                            replaceWordFoundByKeyword(replaceWordFoundByKeyword(
                                                    replaceWordFoundByKeyword(replaceWordFoundByKeyword(
                                                            replaceWordFoundByKeyword(replaceWordFoundByKeyword(
                                                                    replaceWordFoundByKeyword(replaceWordFoundByKeyword(
                                                                            replaceWordFoundByKeyword(
                                                                                    replaceWordFoundByKeyword(
                                                                                            replaceWordFoundByKeyword(
                                                                                                    replaceWordFoundByKeyword(
                                                                                                            replaceWordFoundByKeyword(
                                                                                                                    replaceWordFoundByKeyword(
                                                                                                                            replaceWordFoundByKeyword(
                                                                                                                                    replaceWordFoundByKeyword(
                                                                                                                                            replaceWordFoundByKeyword(
                                                                                                                                                    replaceWordFoundByKeyword(
                                                                                                                                                            replaceWordFoundByKeyword(
                                                                                                                                                                    replaceWordFoundByKeyword(
                                                                                                                                                                            replaceWordFoundByKeyword(
                                                                                                                                                                                    replaceWordFoundByKeyword(
                                                                                                                                                                                            replaceWordFoundByKeyword(
                                                                                                                                                                                                    isWordContains(
                                                                                                                                                                                                            s,
                                                                                                                                                                                                            ",t.")
                                                                                                                                                                                                            || isWordContains(
                                                                                                                                                                                                                    s,
                                                                                                                                                                                                                    ",tp") ? s
                                                                                                                                                                                                                            : insertKeywordTinhThanh(
                                                                                                                                                                                                                                    s),
                                                                                                                                                                                                    ",",
                                                                                                                                                                                                    ", "),
                                                                                                                                                                                            " Số ",
                                                                                                                                                                                            " "),
                                                                                                                                                                                    ", Phòng",
                                                                                                                                                                                    ", phòng"),
                                                                                                                                                                            ", Lầu",
                                                                                                                                                                            ", lầu"),
                                                                                                                                                                    ", Căn",
                                                                                                                                                                    ", căn"),
                                                                                                                                                            "Tòa Nhà",
                                                                                                                                                            "tòa nhà"),
                                                                                                                                                    "Cc",
                                                                                                                                                    "CC"),
                                                                                                                                            "Chung Cư",
                                                                                                                                            "CC"),
                                                                                                                                    ", Hẻm",
                                                                                                                                    ", hẻm"),
                                                                                                                            " Đường",
                                                                                                                            " đường"),
                                                                                                                    ", Tổ",
                                                                                                                    ", tổ"),
                                                                                                            "Tndtq",
                                                                                                            "TNDTQ"),
                                                                                                    "Tổ Nhân Dân Tự Quản",
                                                                                                    "TNDTQ"),
                                                                                            ", Làng", ", làng"),
                                                                                    ", Thôn", ", thôn"),
                                                                            ", Xóm", ", xóm"), ", Bản", ", bản"),
                                                                    ", Buôn", ", buôn"), ", Ấp", ", ấp"),
                                                            "Kp", "KP."), "Khu Phố", "KP."),
                                                    ", Xã", ", X."), ", Phường", ", P."),
                                            "Tt", "TT."), "Thị Trấn", "TT."), ", Huyện", ", H."),
                                    ", Quận", ", Q."), "Tx", "TX."), "Thị Xã", "TX."),
                            ", Tỉnh", ", T."), "Tp", "TP."), "Thành Phố", "TP."),
                    "Hcm", "Hồ Chí Minh")));
        }
        return s;
    }
}
