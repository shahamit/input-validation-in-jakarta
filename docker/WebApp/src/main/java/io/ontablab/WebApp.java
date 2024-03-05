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
                    if(isValidIPAddress(filterAttributes[1])) {
                        whereClause += "sourceIP='" + filterAttributes[1] + "'";
                    } else {
                        throw new IllegalStateException("Invalid source IP address : " + filterAttributes[1]);
                    }
                    break;
                case "ip.dst":
                    if(isValidIPAddress(filterAttributes[1])) {
                        whereClause += "destinationIP='" + filterAttributes[1] + "'";
                    } else {
                        throw new IllegalStateException("Invalid destination IP address : " + filterAttributes[1]);
                    }
                    break;
                case "tcp.port":
                    if(isValidPortNumber(filterAttributes[1])) {
                        whereClause += "port='" + filterAttributes[1] + "'";
                    } else {
                        throw new IllegalStateException("Invalid port number : " + filterAttributes[1]);
                    }
                    break;
                default:
                    throw new IllegalStateException("Invalid filter attribute : " + filterAttributes[0]);
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
            throw new RuntimeException("Error in executing query", e);
        }

        return trafficLogs;
    }

    private boolean isValidIPAddress(String ipAddress) {
        String ipv4Pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        return ipAddress.matches(ipv4Pattern);
    }

    private boolean isValidPortNumber(String portNumber) {
        String portPattern = "^([1-9]\\d{0,4}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";
        return portNumber.matches(portPattern);
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
