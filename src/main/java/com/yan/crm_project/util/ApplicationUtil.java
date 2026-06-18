package com.yan.crm_project.util;

import java.util.*;

import org.springframework.stereotype.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.support.*;

import com.yan.crm_project.model.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.util.stream.Collectors.*;

@Component
/**
 * Tom tat: Tap hop cac phep tinh phu tro cho dashboard, thong ke task va thong
 * bao flash sau redirect.
 */
public class ApplicationUtil {
    /**
     * Loc danh sach cong viec theo trang thai, dong thoi tranh loi null khi
     * quan he JPA chua co du lieu.
     */
    public List<Task> splitTasksByStatus(List<Task> tasks, int statusId) {
        return Optional.ofNullable(tasks).orElseGet(Collections::emptyList).stream()
                .filter(task -> task.getStatus() != null && task.getStatus().getId() == statusId).collect(toList());
    }

    /**
     * Tinh phan tram dang so nguyen de dung truc tiep cho progress bar.
     */
    public int calculatePercent(int count, int total) {
        return total == 0 ? 0 : count * 100 / total;
    }

    /**
     * Gan thong bao truc tiep vao model cua request hien tai.
     */
    public void addMessage(ModelAndView mav, String message) {
        mav.addObject(FLAG_MSG_PARAM, true);
        mav.addObject(MSG_PARAM, message);
    }

    /**
     * Gan thong bao flash de ton tai dung mot lan sau redirect.
     */
    public void addFlashMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(FLAG_MSG_PARAM, true);
        redirectAttributes.addFlashAttribute(MSG_PARAM, message);
    }
}
