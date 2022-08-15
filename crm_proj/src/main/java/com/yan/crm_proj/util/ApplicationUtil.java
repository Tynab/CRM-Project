package com.yan.crm_proj.util;

import org.springframework.stereotype.*;

import static com.yan.crm_proj.constant.AttributeConstant.*;
import static java.net.URLEncoder.*;
import static java.nio.charset.StandardCharsets.*;
import static org.springframework.util.StringUtils.*;

@Component
public class ApplicationUtil {
    // Encode a string URL to UTF-8
    public String encodeUrlUtf8(String s) {
        return !hasText(s) ? s : encode(s, UTF_8);
    }

    // Send message to url
    public String sendMsgUrl(String s) {
        return MESSAGE_URL_QUERY + encodeUrlUtf8(s);
    }

    // Send invalid message to url
    public String urlMsgError() {
        return sendMsgUrl("Lỗi không tìm thấy dữ liệu!");
    }

    // Send success message to url
    public String urlMsgSuccess() {
        return sendMsgUrl("Thành công!");
    }
}
