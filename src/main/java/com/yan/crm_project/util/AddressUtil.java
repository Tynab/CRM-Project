package com.yan.crm_project.util;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.thymeleaf.util.*;

import static org.springframework.util.StringUtils.*;

@Component
/**
 * Tom tat: Chuan hoa dia chi tieng Viet truoc khi luu, bao gom viet hoa,
 * khoang trang, dau phan cach va cac tien to hanh chinh pho bien.
 */
public class AddressUtil {
    private static final String[][] ADMINISTRATIVE_REPLACE_RULES = { { ",", ", " }, { " Số ", " " },
            { ", Phòng", ", phòng" }, { ", Lầu", ", lầu" }, { ", Căn", ", căn" },
            { "Tòa Nhà", "tòa nhà" }, { "Cc", "CC" }, { "Chung Cư", "CC" }, { ", Hẻm", ", hẻm" },
            { " Đường", " đường" }, { ", Tổ", ", tổ" }, { "Tndtq", "TNDTQ" },
            { "Tổ Nhân Dân Tự Quản", "TNDTQ" }, { ", Làng", ", làng" }, { ", Thôn", ", thôn" },
            { ", Xóm", ", xóm" }, { ", Bản", ", bản" }, { ", Buôn", ", buôn" }, { ", Ấp", ", ấp" },
            { "Kp", "KP." }, { "Khu Phố", "KP." }, { ", Xã", ", X." }, { ", Phường", ", P." },
            { "Tt", "TT." }, { "Thị Trấn", "TT." }, { ", Huyện", ", H." }, { ", Quận", ", Q." },
            { "Tx", "TX." }, { "Thị Xã", "TX." }, { ", Tỉnh", ", T." }, { "Tp", "TP." },
            { "Thành Phố", "TP." }, { "Hcm", "Hồ Chí Minh" } };

    @Autowired
    private StringUtil stringUtil;

    /**
     * Doi dau "-" thanh "," neu dia chi co qua nhieu dau gach ngang.
     */
    private String reformatViolateOneHyphen(String s) {
        return s.length() - s.replaceAll("-", "").length() > 1 ? s.replaceAll("-", ",") : s;
    }

    /**
     * Gom nhieu dau cham lien tiep thanh mot dau cham.
     */
    private String reformatViolateSingleDot(String s) {
        return s.replaceAll("\\.{2,}", ".");
    }

    /**
     * Chen tien to thanh pho truoc phan tinh/thanh neu nguoi dung chua nhap.
     */
    private String insertKeywordTinhThanh(String s) {
        return hasText(s) ? s.replaceAll(",(?!.*,)", ",TP.") : s;
    }

    /**
     * Chuan hoa dia chi theo cac quy uoc rut gon dang dung trong UI CRM.
     */
    public String parseToLegalAddress(String s) {
        if (!hasText(s)) {
            return s;
        }

        var address = stringUtil.removeWhiteSpaceBeginAndEnd(s);
        address = stringUtil.removeWhiteSpaceBeforeAndAfterChar(address, "-");
        address = reformatViolateOneHyphen(address);
        address = stringUtil.replaceWordFoundByKeyword(address, ",", ", ");
        address = stringUtil.replaceWordFoundByKeyword(address, "\\.", "\\. ");
        address = StringUtils.capitalizeWords(address);
        address = stringUtil.removeWhiteSpaceBeforeAndAfterChar(address, ",");
        address = stringUtil.removeWhiteSpaceBeforeAndAfterChar(address, "/");

        if (!stringUtil.isWordContains(address, ",t.") && !stringUtil.isWordContains(address, ",tp")) {
            address = insertKeywordTinhThanh(address);
        }

        for (var rule : ADMINISTRATIVE_REPLACE_RULES) {
            address = stringUtil.replaceWordFoundByKeyword(address, rule[0], rule[1]);
        }

        address = reformatViolateSingleDot(address);
        return stringUtil.removeWhiteSpaceBeforeAndAfterChar(address, "\\.");
    }
}
