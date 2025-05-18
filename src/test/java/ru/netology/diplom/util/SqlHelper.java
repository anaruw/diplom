package ru.netology.diplom.util;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class SqlHelper {
    private String connUrl = "jdbc:postgresql://localhost:5432/app";
    private String connUser = "app";
    private String connPass = "pass";

    @Data
    public class TableName {
        String table_name;
    }

    @SneakyThrows
    public void cleaningDB() {
        List<String> tableNames = new ArrayList<>();
        String tableNamesQuery =
                "SELECT table_name " +
                "FROM information_schema.tables " +
                "WHERE table_schema = 'public';";

        try (Connection conn = DriverManager.getConnection(connUrl, connUser, connPass)) {
            QueryRunner runner = new QueryRunner();

            List<TableName> reply = runner.query(conn, tableNamesQuery, new BeanListHandler<>(TableName.class));

            for (TableName name : reply) {
                tableNames.add(name.getTable_name());
            }

            String foreignKeySet = "SET session_replication_role = 'replica';";
            runner.update(conn, foreignKeySet);

            for (String tableName : tableNames) {
                String removeQuery = "TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE;";
                runner.update(conn, removeQuery);
            }
            foreignKeySet = "SET session_replication_role = 'origin';";
            runner.update(conn, foreignKeySet);
        }
    }

    @SneakyThrows
    public String paymentStatus(Timestamp testCreated) {
        String result;
        String query =
                "SELECT payment_entity.status " +
                        "FROM order_entity " +
                        "JOIN payment_entity ON payment_entity.transaction_id = order_entity.payment_id " +
                        "WHERE order_entity.created > ?;";
        QueryRunner runner = new QueryRunner();

        try (Connection conn = DriverManager.getConnection(connUrl, connUser, connPass)) {
            result = runner.query(conn, query, new ScalarHandler<>(), testCreated);
        }

        return result;
    }

    @SneakyThrows
    public String creditStatus(Timestamp testCreated) {
        String result;
        String query =
                "SELECT credit_request_entity.status " +
                        "FROM order_entity " +
                        "JOIN credit_request_entity ON credit_request_entity.bank_id = order_entity.credit_id " +
                        "WHERE order_entity.created > ?;";
        QueryRunner runner = new QueryRunner();

        try (Connection conn = DriverManager.getConnection(connUrl, connUser, connPass)) {
            result = runner.query(conn, query, new ScalarHandler<>(), testCreated);
        }

        return result;
    }
}