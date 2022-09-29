package com.yan.crm_project.util;

import java.util.*;

import org.springframework.stereotype.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;

import static com.yan.crm_project.common.Bean.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.util.stream.Collectors.*;

@Component
public class ApplicationUtil {
    // Get all tasks with status not started
    public List<Task> splitTasksByStatus(List<Task> tasks, int statusId) {
        return tasks.stream().filter(task -> task.getStatus().getId() == statusId).collect(toList());
    }

    // Show message
    public boolean showMessageBox(ModelAndView mav) {
        // check flag
        if (_isMsgShow) {
            mav.addObject(FLAG_MSG_PARAM, true);
            mav.addObject(MSG_PARAM, _msg);
        }
        return false;
    }
}
