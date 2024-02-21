package io.ontablab;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrafficLog {
    private int id;
    private String sourceIP;
    private String destinationIP;
    private int port;
    private Date date;

    public static TrafficLog getInstanceFromResultSet(ResultSet resultSet) throws SQLException {
        return new TrafficLog(
                resultSet.getInt("id"),
                resultSet.getString("sourceIP"),
                resultSet.getString("destinationIP"),
                resultSet.getInt("port"),
                Date.valueOf(resultSet.getString("date")));
    }

    public TrafficLog(int id, String sourceIP, String destinationIP, int port, Date date) {
        this.id = id;
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.port = port;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public int getPort() {
        return port;
    }

    public Date getDate() {
        return date;
    }
}
