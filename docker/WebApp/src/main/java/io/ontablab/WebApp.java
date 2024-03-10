package io.ontablab;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.logging.Logger;

class SearchHandler {
    private DBManager dbManager;
    private final Logger logger = Logger.getLogger(SearchHandler.class.getName());

    public SearchHandler(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    private String createRawSQLQueryFromSearch(String q) {

        if (q == null || q.isEmpty()) {
            return "SELECT * FROM traffic_logs;";
        }
        q = Normalizer.normalize(q, Normalizer.Form.NFKC);
        StringBuilder whereClause = new StringBuilder();
        String[] qFilters = q.split(" AND ");

        for (String qFilter : qFilters) {
            String[] filterAttributes = qFilter.split("==");
            if (filterAttributes.length == 2) {
                switch (filterAttributes[0].trim()) {
                    case "ip.src":
                        if (isValidIPAddress(filterAttributes[1].trim())) {
                            whereClause.append("sourceIP='").append(filterAttributes[1].trim()).append("'");
                        } else {
                            throw new IllegalStateException("Invalid source IP address : " + filterAttributes[1]);
                        }
                        break;
                    case "ip.dst":
                        if (isValidIPAddress(filterAttributes[1].trim())) {
                            whereClause.append("destinationIP='").append(filterAttributes[1].trim()).append("'");
                        } else {
                            throw new IllegalStateException("Invalid destination IP address : " + filterAttributes[1]);
                        }
                        break;
                    case "tcp.port":
                        if (isValidPortNumber(filterAttributes[1].trim())) {
                            whereClause.append("port='").append(filterAttributes[1].trim()).append("'");
                        } else {
                            throw new IllegalStateException("Invalid port number : " + filterAttributes[1]);
                        }
                        break;
                    default:
                        throw new IllegalStateException("Invalid filter attribute : " + filterAttributes[0]);
                }
                whereClause.append(" AND ");
            }
        }
        whereClause.delete(whereClause.length() - 5, whereClause.length()); //Remove the trailing " AND "
        return "SELECT * FROM traffic_logs WHERE " + whereClause + ";";
    }

    public List<TrafficLog> getTrafficLogsFromSearch(String q) {
        String rawSQLQuery = createRawSQLQueryFromSearch(q);
        logger.info("SQL query to be executed : " + rawSQLQuery);
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
        String ipv4Pattern = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        return ipAddress.matches(ipv4Pattern); //better would be to use apache commons InetAddressValidator
    }

    private boolean isValidPortNumber(String portNumber) {
        int port = Integer.parseInt(portNumber);
        return port > 0 && port < 65535;
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
