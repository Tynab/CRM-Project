package com.yan.crm_proj.spec;

import java.util.*;

import javax.persistence.criteria.*;

import org.springframework.data.jpa.domain.*;

import com.yan.crm_proj.model.*;

public class TaskSpec {
    // Find tasks by doer email and task status name
    public static Specification<Task> findByDoerEmailAndTaskStatusName(String email, String taskStatusName) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<>();
            predicates.add(cb.equal(root.join("doer").get("email"), email));
            predicates.add(cb.equal(root.join("status").get("name"), taskStatusName));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
