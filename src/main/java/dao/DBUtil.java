package dao;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private  static final  String URL="jdbc:mysql://127.0.0.1:3306/image_Server?characterEncoding=utf8&useSSL=true";
    private  static final String USEName="root";
    private  static final  String PassWord="";
    private  static  volatile DataSource dataSource=null;
    private static DataSource getDataSource(){
        if(dataSource==null){
            synchronized (DBUtil.class) {
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();
                    MysqlDataSource mysqlDataSource = (MysqlDataSource) dataSource;
                    mysqlDataSource.setURL(URL);
                    mysqlDataSource.setUser(USEName);
                    mysqlDataSource.setPassword(PassWord);
                }
            }
        }
        return dataSource;
    }

    /**
     * 获取连接
     * @return
     */
    public static java.sql.Connection getConnection(){
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭连接
     * @param connection
     * @param preparedStatement
     * @param resultSet
     */
    public static void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        try {
            if(resultSet!=null){
                resultSet.close();
            }
            if(preparedStatement!=null){
                preparedStatement.close();
            }
            if(connection!=null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
