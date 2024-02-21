package io.ontablab;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

class SearchHandler {
    private DBManager dbManager;

    public SearchHandler(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    private String createRawSQLQueryFromSearch(String q) {

        if (q == null || q.isEmpty()) {
            return "SELECT * FROM traffic_logs;";
        }

        String whereClause = "";
        String[] qFilters = q.split(" ");

        for (String qFilter : qFilters) {
            String[] filterAttributes = qFilter.split("==");

            switch (filterAttributes[0]) {
                case "ip.src":
                    whereClause += "sourceIP='" + filterAttributes[1] + "'";
                    break;
                case "ip.dst":
                    whereClause += "destinationIP='" + filterAttributes[1] + "'";
                    break;
                case "tcp.port":
                    whereClause += "port='" + filterAttributes[1] + "'";
                    break;
                default:
                    whereClause += " " + qFilter + " ";
                    break;
            }
        }

        return "SELECT * FROM traffic_logs WHERE " + whereClause + ";";
    }

    public List<TrafficLog> getTrafficLogsFromSearch(String q) {
        String rawSQLQuery = createRawSQLQueryFromSearch(q);
        List<TrafficLog> trafficLogs = new ArrayList<>();

        try {
            Statement stmt = dbManager.createStatement();
            ResultSet resultSet = stmt.executeQuery(rawSQLQuery);

            while (resultSet.next()) {
                TrafficLog trafficLog = TrafficLog.getInstanceFromResultSet(resultSet);
                trafficLogs.add(trafficLog);
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trafficLogs;
    }
}

@WebServlet(name = "WebApp", urlPatterns = { "/search" })
public class WebApp extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");

        DBManager dbManager = new DBManager();
        SearchHandler searchHandler = new SearchHandler(dbManager);
        List<TrafficLog> trafficLogs = searchHandler.getTrafficLogsFromSearch(q);

        request.setAttribute("q", q);
        request.setAttribute("logs", trafficLogs);
        request.getRequestDispatcher("/WEB-INF/views/pages/search.jsp").forward(request, response);

        dbManager.close();
    }
}
