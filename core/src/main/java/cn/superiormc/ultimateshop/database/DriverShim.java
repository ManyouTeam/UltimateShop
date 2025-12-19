package cn.superiormc.ultimateshop.database;

import java.sql.*;

public class DriverShim implements Driver {
    private final Driver driver;
    public DriverShim(Driver d) { this.driver = d; }
    @Override public Connection connect(String url, java.util.Properties info) throws SQLException { return driver.connect(url, info); }
    @Override public boolean acceptsURL(String url) throws SQLException { return driver.acceptsURL(url); }
    @Override public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) throws SQLException { return driver.getPropertyInfo(url, info); }
    @Override public int getMajorVersion() { return driver.getMajorVersion(); }
    @Override public int getMinorVersion() { return driver.getMinorVersion(); }
    @Override public boolean jdbcCompliant() { return driver.jdbcCompliant(); }
    @Override public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException { return driver.getParentLogger(); }
}