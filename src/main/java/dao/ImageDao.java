package dao;

import com.JavaserverException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ImageDao {
    /**
     *插入操作
     * 1. 获取连接
     * 2 .组装SQL语句
     * 3 .执行SQL语句
     *4 . 关闭连接
     * @param
     */
    public void insert(Image image){
        Connection connection=DBUtil.getConnection();
        String sql="insert into image_table values (null,?,?,?,?,?,?)";
        PreparedStatement statement=null;
        try {
            statement= connection.prepareStatement(sql);
            statement.setString(1,image.getImageName());
            statement.setInt(2,image.getSize());
            statement.setString(3,image.getUploadTime());
            statement.setString(4,image.getContentType());
            statement.setString(5,image.getPath());
            statement.setString(6,image.getMd5());
            int row= statement.executeUpdate();
            if(row!=1){
                throw new JavaserverException("插入数据库异常");
            }

        } catch (SQLException | JavaserverException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,null);
        }
    }

    /**
     *查找全部
     * 1.获取连接
     * 2 . 拼接sql
     * 2.执行SQL
     * 4。处理结果集
     * 5.关闭连接
     * @return
     */
    public List<Image> selectAll(){
        List<Image>list=new LinkedList<>();
        Connection connection=DBUtil.getConnection();
        String sql="select * from image_table" ;
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            statement=connection.prepareStatement(sql);
            resultSet =statement.executeQuery();
            while(resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                list.add(image);

            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

    /**
     *
     * @return
     */
    public Image  selectOne( int imageId){
        Connection connection=DBUtil.getConnection();
        String sql="select * from image_table where imageId=?";
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try {
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setInt(1,imageId);
            resultSet= preparedStatement.executeQuery();
            if(resultSet.next()) {
                Image image = new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
        return null;
    }

    /**
     * 删除图片
     * 1.连接数据库
     * 2，拼装SQL
     * 3，执行SQL
     * 4，关闭连接
     * @param imageId
     */
    public void delete(int imageId){
        Connection connection =DBUtil.getConnection();
        String sql="delete from image_table where imageId=?";
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try {
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setInt(1,imageId);
            int num= preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
    }
    public Image selectMD5(String md5){
        Connection connection=DBUtil.getConnection();
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        String sql="select*from image_table where md5=?";
        try{
            preparedStatement =connection.prepareStatement(sql);
            preparedStatement.setString(1,md5);
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                Image image = new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,resultSet );
        }
        return null;
    }


}
