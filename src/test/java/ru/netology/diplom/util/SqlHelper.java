package ru.netology.diplom.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@UtilityClass
public class SqlHelper {

    private final QueryRunner RUNNER = new QueryRunner();
    private String connUrl = System.getProperty("db.url");
    private final String[] tableNames = {"order_entity", "payment_entity", "credit_request_entity"};


    private Connection conn() throws SQLException {
        return DriverManager.getConnection(connUrl, "app", "pass");
    }

    @SneakyThrows
    public void cleaningDB() {
        String cleaningQueryTemplate = "DELETE FROM %s;";

        try (Connection conn = conn()) {

            for (String tableName : tableNames) {
                String cleaningQuery = String.format(cleaningQueryTemplate, tableName);
                RUNNER.execute(conn, cleaningQuery);
            }
        }
    }

    @SneakyThrows
    public String paymentStatus() {
        String result;
        String query =
                "SELECT payment_entity.status " +
                        "FROM order_entity " +
                        "JOIN payment_entity ON payment_entity.transaction_id = order_entity.payment_id;";

        try (Connection conn = conn()) {
            result = RUNNER.query(conn, query, new ScalarHandler<>());
        }
        return result;
    }

    @SneakyThrows
    public String creditStatus() {
        String result;
        String query =
                "SELECT credit_request_entity.status " +
                        "FROM order_entity " +
                        "JOIN credit_request_entity ON credit_request_entity.bank_id = order_entity.credit_id;";

        try (Connection conn = conn()) {
            result = RUNNER.query(conn, query, new ScalarHandler<>());
        }
        return result;
    }

    @SneakyThrows
    public int ordersCount() {
        Number count;
        String countQuery = "SELECT COUNT(*) FROM order_entity;";

        try (Connection conn = conn()) {
            count = RUNNER.query(conn, countQuery, new ScalarHandler<>());
        }
        return count.intValue();
    }

    @SneakyThrows
    public int paymentCount() {
        Number count;
        String countQuery = "SELECT COUNT(*) FROM payment_entity;";

        try (Connection conn = conn()) {
            count = RUNNER.query(conn, countQuery, new ScalarHandler<>());
        }
        return count.intValue();
    }

    @SneakyThrows
    public int creditCount() {
        Number count;
        String countQuery = "SELECT COUNT(*) FROM credit_request_entity;";

        try (Connection conn = conn()) {
            count = RUNNER.query(conn, countQuery, new ScalarHandler<>());
        }
        return count.intValue();
    }
}