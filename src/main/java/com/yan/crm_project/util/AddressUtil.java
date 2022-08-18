package com.yan.crm_project.util;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.thymeleaf.util.*;

import static org.springframework.util.StringUtils.*;

// Ex: "  6C Đường số 8,phường.linh Tây- Quận  Thủ đức-HCM   "
@Component
public class AddressUtil {
    @Autowired
    private StringUtil stringUtil;

    // Re-format violate rule one "-"
    private String reformatViolateOneHyphen(String s) {
        return s.length() - s.replaceAll("-", "").length() > 1 ? s.replaceAll("-", ",") : s;
    }

    // Re-format violate rule single "."
    private String reformatViolateSingleDot(String s) {
        return s.replaceAll("\\.{2,}", ".");
    }

    // Insert keyword "tỉnh" or "thành phố" before city name
    private String insertKeywordTinhThanh(String s) {
        return hasText(s) ? s.replaceAll(",(?!.*,)", ",TP.") : s;
    }

    // Parse string to legal address
    public String parseToLegalAddress(String s) {
        if (hasText(s)) {
            s = stringUtil.removeWhiteSpaceBeforeAndAfterChar(stringUtil.removeWhiteSpaceBeforeAndAfterChar(
                    StringUtils.capitalizeWords(stringUtil.replaceWordFoundByKeyword(
                            stringUtil.replaceWordFoundByKeyword(
                                    reformatViolateOneHyphen(stringUtil.removeWhiteSpaceBeforeAndAfterChar(
                                            stringUtil.removeWhiteSpaceBeginAndEnd(s), "-")),
                                    ",", ", "),
                            "\\.", "\\. ")),
                    ","), "/"); // para para for condition
            s = stringUtil.removeWhiteSpaceBeforeAndAfterChar(reformatViolateSingleDot(stringUtil
                    .replaceWordFoundByKeyword(stringUtil.replaceWordFoundByKeyword(
                            stringUtil.replaceWordFoundByKeyword(stringUtil.replaceWordFoundByKeyword(
                                    stringUtil.replaceWordFoundByKeyword(stringUtil.replaceWordFoundByKeyword(stringUtil
                                            .replaceWordFoundByKeyword(stringUtil.replaceWordFoundByKeyword(stringUtil
                                                    .replaceWordFoundByKeyword(stringUtil.replaceWordFoundByKeyword(
                                                            stringUtil.replaceWordFoundByKeyword(
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
                                                                                                                                                                                                                                                                                                                                                                                    .replaceWordFoundByKeyword(
                                                                                                                                                                                                                                                                                                                                                                                            stringUtil
                                                                                                                                                                                                                                                                                                                                                                                                    .replaceWordFoundByKeyword(
                                                                                                                                                                                                                                                                                                                                                                                                            stringUtil
                                                                                                                                                                                                                                                                                                                                                                                                                    .isWordContains(
                                                                                                                                                                                                                                                                                                                                                                                                                            s,
                                                                                                                                                                                                                                                                                                                                                                                                                            ",t.")
                                                                                                                                                                                                                                                                                                                                                                                                                    || stringUtil
                                                                                                                                                                                                                                                                                                                                                                                                                            .isWordContains(
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
                                                                                                                                                                                            ", Làng",
                                                                                                                                                                                            ", làng"),
                                                                                                                                                                            ", Thôn",
                                                                                                                                                                            ", thôn"),
                                                                                                                                                            ", Xóm",
                                                                                                                                                            ", xóm"),
                                                                                                                                            ", Bản",
                                                                                                                                            ", bản"),
                                                                                                                            ", Buôn",
                                                                                                                            ", buôn"),
                                                                                                            ", Ấp",
                                                                                                            ", ấp"),
                                                                                            "Kp", "KP."),
                                                                                    "Khu Phố", "KP."),
                                                                            ", Xã", ", X."),
                                                                    ", Phường", ", P."),
                                                            "Tt", "TT."), "Thị Trấn", "TT."),
                                                    ", Huyện", ", H."), ", Quận", ", Q."),
                                            "Tx", "TX."), "Thị Xã", "TX."),
                                    ", Tỉnh", ", T."), "Tp", "TP."),
                            "Thành Phố", "TP."), "Hcm", "Hồ Chí Minh")),
                    "\\.");
        }
        return s;
    }
}
