package com.vero.compiler.common.error;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 19:06 2018/1/8.
 * @since face2face
 */

@Slf4j
public class PathUtils
{
    /**
     * 根据相对文件名得到绝对路径;
     * 
     * @param relativePath
     * @return 绝对路径
     */
    public static String getAbsolutePath(String relativePath)
    {
        try
        {
            String projectRoot = getProjectRoot();
            File file = buildFile(relativePath, projectRoot);
            return file.getAbsolutePath();
        }
        catch (FileNotFoundException e)
        {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static File buildFile(String relativePath, String projectRoot) {
        File file = new File(projectRoot, relativePath);
        if (!file.exists())
        {
            file.mkdirs();
        }
        return file;
    }

    public static String getProjectRoot()
        throws FileNotFoundException
    {
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if (!path.exists()) path = new File("");
        return path.getAbsolutePath();
    }

    public static String concat(String basePath, String... suffixes)
    {
        StringBuilder builder = new StringBuilder(basePath);
        for (String suffix : suffixes)
        {
            builder.append("\\").append(suffix);
        }
//        File file = new File(builder.toString());
//        if (!file.exists())
//        {
//            file.mkdirs();
//        }
        return builder.toString();
    }


    public static String getAbsolutePath(String relativePath, String... suffixes) {
        return concat(getAbsolutePath(relativePath), suffixes);
    }

    public static void handleFileTransfer(MultipartFile multipartFile, String absolutePath)
    {
        try
        {
            FileOutputStream outputStream = new FileOutputStream(absolutePath);
            outputStream.write(multipartFile.getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // try
        // {
        // FileUtils.copyInputStreamToFile(multipartFile.getInputStream(),
        // new File(absolutePath));
        // }
        // catch (IOException e)
        // {
        // e.printStackTrace();
        // }
    }

//    public static String makeDir(String realPath) {
//        File file = new File(realPath);
//        if(file)
//    }

}
