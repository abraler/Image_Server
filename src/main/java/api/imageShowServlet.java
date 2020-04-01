package api;

import dao.Image;
import dao.ImageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

public class imageShowServlet extends HttpServlet {
    private static HashSet<String>whiteList=new HashSet<>();
    static {
        whiteList.add("http://122.51.184.221:8080/Images/index.html");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        //解析imageId
        String imageId=req.getParameter("imageId");
        String referer=req.getHeader("Referer");
        if(!whiteList.contains(referer)){
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().print("{\"ok\":\"false\",\"reason\":\"没有权限\"}");
            return;
        }
        if(imageId==null||imageId.equals("")){
            resp.setContentType("application/json;charset=utf-8");
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":\"false\",\"reason\":\"解析请求失败\"}");
            return;
        }
        // 查找数据库
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        resp.setContentType(image.getContentType());
        File file=new File(image.getPath());

        OutputStream outputStream=resp.getOutputStream();
        FileInputStream fileInputStream=new FileInputStream(file);
        byte[]buffer=new byte[1024];
        while (true){
            int len =fileInputStream.read(buffer);
            if(len==-1){
                break;
            }
            outputStream.write(buffer);
        }
        fileInputStream.close();
        outputStream.close();





    }
}
