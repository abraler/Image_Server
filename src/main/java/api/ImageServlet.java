package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.ImageDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageServlet extends HttpServlet {
    /**
     * 查看图片 所以和指定
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String imageId=req.getParameter("imageId");
        if(imageId==null||imageId.equals("")){
            selectAll(req,resp);
        }else{
            selectOne(imageId,resp);
        }
    }

    private void selectOne(String imageId, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        ImageDao imageDao=new ImageDao();
        Image image= imageDao.selectOne(Integer.parseInt(imageId));
        Gson gson=new GsonBuilder().create();
        String jsonData=gson.toJson(image);
        resp.getWriter().write(jsonData);
    }

    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        ImageDao imageDao=new ImageDao();
        List<Image>images= imageDao.selectAll();
        Gson gson=new GsonBuilder().create();
        String jsonData=gson.toJson(images);
        resp.getWriter().write(jsonData);
    }

    /**
     * 上传图片
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取图片的属性信息，并出入数据库
        //创建一个factory对象和upload对象

        FileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);
        List<FileItem> items=null;
        try {
            items=upload.parseRequest(req);
        } catch (FileUploadException e) {

            e.printStackTrace();
            //告诉客户端具体出现了什么错误
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write("{\"ok\":\"false\n\"reason:\"请求解析失败\"}");
            return;
        }
        FileItem fileItem=items.get(0);
        Image image=new Image();
        image.setImageName(fileItem.getName());
        image.setSize((int)fileItem.getSize());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
        image.setUploadTime(simpleDateFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());


        image.setMd5(DigestUtils.md5Hex(fileItem.get()));
        //通过upload对象来进一步解析请求(解析)
        //获取图片内容，病写入磁盘文件
        //想客户返回结果数据
        image.setPath("./image/"+image.getMd5());
        ImageDao imageDao=new ImageDao();
        Image image1=imageDao.selectMD5(image.getMd5());
        imageDao.insert(image);

        if(image1==null) {

            File file = new File(image.getPath());
            try {
                fileItem.write(file);
            } catch (Exception e) {
                e.printStackTrace();
                resp.setContentType("application/json;charset=utf-8");
                resp.getWriter().write("{\"ok\":\"false\n\"reason:\"写磁盘失败\"}");
                return;
            }
        }

        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write("{\"ok\":\"true\"}");
        resp.sendRedirect("index.html");
    }

    /**
     * 删除图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");
        String imageID=req.getParameter("imageId");
        if(imageID==null||imageID.equals("")){
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":\"false\",\"reason\":\"解析请求失败\"}");
            return;
        }
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageID));
        if(image==null) {
            resp.setStatus(200);
            resp.getWriter().write("{\"ok\":\"false\",\"reason\":\"没有指定图片\"}");
            return;
        }
        imageDao.delete(Integer.parseInt(imageID));
        Image image1=imageDao.selectMD5(image.getMd5());
        if(image1==null){
            File file = new File(image.getPath());
            file.delete();
        }
        resp.setStatus(200);
        resp.getWriter().write("{\"ok\":\"true\"}");
        return;

    }
}
