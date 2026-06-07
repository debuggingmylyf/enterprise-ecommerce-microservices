package com.ecommerce.auth.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RefreshTokenSchemaMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenSchemaMigration.class);

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        List<UniqueConstraint> constraints = jdbcTemplate.query(
                """
                        select n.nspname as schema_name,
                               c.conname as constraint_name
                          from pg_constraint c
                          join pg_class t on t.oid = c.conrelid
                          join pg_namespace n on n.oid = t.relnamespace
                         where t.relname = 'refresh_tokens'
                           and c.contype = 'u'
                           and array_length(c.conkey, 1) = 1
                           and exists (
                               select 1
                                 from unnest(c.conkey) as constraint_column(attnum)
                                 join pg_attribute a on a.attrelid = t.oid
                                                     and a.attnum = constraint_column.attnum
                                where a.attname = 'user_id'
                           )
                        """,
                (rs, rowNum) -> new UniqueConstraint(
                        rs.getString("schema_name"),
                        rs.getString("constraint_name")
                )
        );

        constraints.forEach(this::dropConstraint);
    }

    private void dropConstraint(UniqueConstraint constraint) {
        String sql = "alter table " + quoteIdentifier(constraint.schemaName())
                + ".refresh_tokens drop constraint " + quoteIdentifier(constraint.constraintName());

        jdbcTemplate.execute(sql);
        log.info("Dropped stale refresh token user unique constraint: {}", constraint.constraintName());
    }

    private String quoteIdentifier(String identifier) {
        return '"' + identifier.replace("\"", "\"\"") + '"';
    }

    private record UniqueConstraint(String schemaName, String constraintName) {
    }
}
