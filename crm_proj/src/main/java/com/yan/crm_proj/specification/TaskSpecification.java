package com.yan.crm_proj.specification;

import java.util.*;

import javax.persistence.criteria.*;

import org.springframework.data.jpa.domain.*;

import com.yan.crm_proj.model.*;

public class TaskSpecification {
    public static Specification<Task> findByDoerIdAndTaskStatusName(int doerId, String taskStatusName) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            var taskDoer = root.join("doer");
            predicates.add(cb.equal(taskDoer.get("id"), doerId));
            var taskTaskStatus = root.join("status");
            predicates.add(cb.equal(taskTaskStatus.get("name"), taskStatusName));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
