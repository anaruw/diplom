package ru.netology.diplom.util;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class SqlHelper {
    private String connUrl = "jdbc:mysql://localhost:3307/app";
    private String connUser = "app";
    private String connPass = "pass";

    @Data
    public class TableName {
        String tables_in_app;
    }

    @SneakyThrows
    public void cleaningDB() {
        List<String> tableNames = new ArrayList<>();
        String tableNamesQuery = "SHOW tables;";

        try (Connection conn = DriverManager.getConnection(connUrl, connUser, connPass)) {
            QueryRunner runner = new QueryRunner();

            List<TableName> reply = runner.query(conn, tableNamesQuery, new BeanListHandler<>(TableName.class));

            for (TableName name : reply) {
                tableNames.add(name.getTables_in_app());
            }

            String foreignKeySet = "SET FOREIGN_KEY_CHECKS = 0;";
            runner.update(conn, foreignKeySet);

            for (String tableName : tableNames) {
                String removeQuery = "TRUNCATE TABLE " + tableName + ";";
                runner.update(conn, removeQuery);
            }
            foreignKeySet = "SET FOREIGN_KEY_CHECKS = 1;";
            runner.update(conn, foreignKeySet);
        }
    }

    @SneakyThrows
    public String paymentStatus(String testCreated) {
        String result;
        String query =
                "SELECT payment_entity.status " +
                        "FROM order_entity " +
                        "JOIN payment_entity ON payment_entity.transaction_id = order_entity.payment_id " +
                        "WHERE CONVERT_TZ(order_entity.created, '+00:00', '+3:00') > ?;";
        QueryRunner runner = new QueryRunner();

        try (Connection conn = DriverManager.getConnection(connUrl, connUser, connPass)) {
            result = runner.query(conn, query, new ScalarHandler<>(), testCreated);
        }

        return result;
    }

    @SneakyThrows
    public String creditStatus(String testCreated) {
        String result;
        String query =
                "SELECT credit_request_entity.status " +
                        "FROM order_entity " +
                        "JOIN credit_request_entity ON credit_request_entity.bank_id = order_entity.credit_id " +
                        "WHERE CONVERT_TZ(order_entity.created, '+00:00', '+3:00') > ?;";
        QueryRunner runner = new QueryRunner();

        try (Connection conn = DriverManager.getConnection(connUrl, connUser, connPass)) {
            result = runner.query(conn, query, new ScalarHandler<>(), testCreated);
        }

        return result;
    }
}