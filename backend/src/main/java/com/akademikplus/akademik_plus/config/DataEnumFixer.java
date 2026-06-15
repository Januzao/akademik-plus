package com.akademikplus.akademik_plus.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class DataEnumFixer {

    @PersistenceContext
    private EntityManager em;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void fixData() {
        fixUsedIdColumn();
        fixEnumValues();
    }

    private void fixUsedIdColumn() {
        // Check if the stale typo column "used_id" still exists
        Long count = (Long) em.createNativeQuery(
                "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_name = 'maintenance_requests' AND column_name = 'used_id'"
        ).getSingleResult();

        if (count == 0) return;

        // Migrate FK values from used_id to user_id where user_id is still null
        int migrated = em.createNativeQuery(
                "UPDATE maintenance_requests SET user_id = used_id WHERE user_id IS NULL AND used_id IS NOT NULL"
        ).executeUpdate();

        em.createNativeQuery("ALTER TABLE maintenance_requests DROP COLUMN used_id").executeUpdate();

        log.info("DataEnumFixer: removed stale 'used_id' column, migrated {} rows to 'user_id'", migrated);
    }

    private void fixEnumValues() {
        int fixed = 0;

        fixed += em.createNativeQuery("UPDATE maintenance_requests SET status = 'PENDING'     WHERE status = 'Pending'").executeUpdate();
        fixed += em.createNativeQuery("UPDATE maintenance_requests SET status = 'IN_PROGRESS' WHERE status = 'In Progress'").executeUpdate();
        fixed += em.createNativeQuery("UPDATE maintenance_requests SET status = 'RESOLVED'    WHERE status = 'Resolved'").executeUpdate();
        fixed += em.createNativeQuery("UPDATE maintenance_requests SET status = 'CANCELLED'   WHERE status = 'Cancelled'").executeUpdate();

        fixed += em.createNativeQuery("UPDATE maintenance_requests SET priority = 'HIGH'   WHERE priority = 'High'").executeUpdate();
        fixed += em.createNativeQuery("UPDATE maintenance_requests SET priority = 'MEDIUM' WHERE priority = 'Medium'").executeUpdate();
        fixed += em.createNativeQuery("UPDATE maintenance_requests SET priority = 'LOW'    WHERE priority = 'Low'").executeUpdate();

        if (fixed > 0) {
            log.info("DataEnumFixer: corrected {} rows with wrong enum case", fixed);
        }
    }
}
