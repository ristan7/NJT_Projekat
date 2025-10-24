package rs.ac.bg.fon.e_learning_platforma_njt.service.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.e_learning_platforma_njt.service.StatusIds;

@Service
public class StatusIdsJdbc implements StatusIds {

    private final JdbcTemplate jdbc;

    private volatile Long REQUESTED_ID;
    private volatile Long ACTIVE_ID;
    private volatile Long COMPLETED_ID;
    private volatile Long CANCELLED_ID;
    private volatile Long SUSPENDED_ID;

    public StatusIdsJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Long fetch(String name) {
        return jdbc.queryForObject(
                "select enrollment_status_id from enrollment_status where enrollment_status_name = ?",
                Long.class, name
        );
    }

    @Override
    public Long requested() {
        if (REQUESTED_ID == null) synchronized (this) {
            if (REQUESTED_ID == null) {
                REQUESTED_ID = fetch("REQUESTED");
            }
        }
        return REQUESTED_ID;
    }

    @Override
    public Long active() {
        if (ACTIVE_ID == null) synchronized (this) {
            if (ACTIVE_ID == null) {
                ACTIVE_ID = fetch("ACTIVE");
            }
        }
        return ACTIVE_ID;
    }

    @Override
    public Long completed() {
        if (COMPLETED_ID == null) synchronized (this) {
            if (COMPLETED_ID == null) {
                COMPLETED_ID = fetch("COMPLETED");
            }
        }
        return COMPLETED_ID;
    }

    @Override
    public Long cancelled() {
        if (CANCELLED_ID == null) synchronized (this) {
            if (CANCELLED_ID == null) {
                CANCELLED_ID = fetch("CANCELLED");
            }
        }
        return CANCELLED_ID;
    }

    @Override
    public Long suspended() {
        if (SUSPENDED_ID == null) synchronized (this) {
            if (SUSPENDED_ID == null) {
                SUSPENDED_ID = fetch("SUSPENDED");
            }
        }
        return SUSPENDED_ID;
    }
}
