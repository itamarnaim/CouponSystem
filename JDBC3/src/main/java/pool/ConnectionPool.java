package pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConnectionPool {

    private Set<Connection> connections;

    private static ConnectionPool instance = null;

    private static final int MAX_CONNECTION = 15;

    private static final String URL ="jdbc:mysql://localhost:3306/coupon_system" +
                               "?user=root" +
                                "&password=root1987" +
                                "&useUnicode=true" +
                               "&useJDBCCompliantTimezoneShift=true" +
                              "&useLegacyDatetimeCode=false" +
                                "&serverTimezone= UTC";

    private ConnectionPool(){
        connections = new HashSet<>();
        for (int i = 0 ; i < MAX_CONNECTION ; i++){
            try {
                Connection connection = DriverManager.getConnection(URL);
                connections.add(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Connection getConnection(){
        while (connections.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Iterator<Connection> it = connections.iterator();
        Connection connection = it.next();
        it.remove();;
        return connection;
    }

    public synchronized void returnConnection(Connection connection){
        connections.add(connection);
        notifyAll();
    }

    public synchronized void closeAllConnections (){
//        counter for checking if all connections are close
        int counter = 0;
//        checking if the remove counter less the max connections.
        while (counter<MAX_CONNECTION){
//            while is empty wait...
            while (connections.isEmpty()){
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Someone interrupt waiting");
                }
            }
//            running over the available connections
//            Closing the connection and adding 1 to the counter
            Iterator<Connection> itCon = connections.iterator();
            while (itCon.hasNext()){
                Connection currentConnection = itCon.next();

                try {
                    currentConnection.close();
                    counter++;
                } catch (SQLException e) {
                    System.err.println("Couldnt close the current connection");
                }
            }
        }
    }

    public synchronized static  ConnectionPool getInstance(){
        if (instance == null){
            instance = new ConnectionPool();
        }
        return instance;
    }
}
