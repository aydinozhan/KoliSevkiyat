package com.example.kolisevkiyat;


import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    Connection con;
    String uname, pass, ip, port,database;

    public Connection ConnectionClass()
    {
        ip ="172.16.0.25";
        database = "TEXTURETEST";
        uname = "sa";
        pass = "Axoft*2014";
        port = "1433";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionUrl= null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionUrl= "jdbc:jtds:sqlserver://"+ip+ ":"+ port+";"+"databasename="+database+";user="+uname+";password="+pass+";"+"characterEncoding=UTF-8;";
            connection= DriverManager.getConnection(ConnectionUrl);
        }
        catch (Exception ex){
            Log.e ("Error",ex.getMessage());
        }
        return connection;
    }
}
