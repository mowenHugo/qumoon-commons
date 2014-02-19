package com.qumoon.commons;

import com.google.common.io.Files;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * @author kevin
 */
public class Files2 {
    /**
     * 解压缩zip文件。
     *
     * @param sourceFile 压缩文件。
     * @param desDir     目标目录
     * @param password   密码
     * @return 解压缩出来的的 {@link java.io.File}
     * @throws net.lingala.zip4j.exception.ZipException
     *          解压缩错误抛出异常
     */
    public static File[] unzip(String sourceFile, String desDir, String password) throws ZipException {
        ZipFile zipFile = new ZipFile(sourceFile);
        if (zipFile.isEncrypted() && null != password) {
            zipFile.setPassword(password);
        }
        zipFile.extractAll(desDir);
        return new File(desDir).listFiles();
    }

    /**
     * 解压缩zip文件。
     *
     * @param sourceFile 压缩文件。
     * @param desDir     目标目录
     * @return 解压缩出来的的 {@link java.io.File}
     * @throws ZipException 解压缩错误抛出异常
     */
    public static File[] unzip(String sourceFile, String desDir)
            throws ZipException {
        return unzip(sourceFile, desDir, null);
    }

    public static File[] unzip2TempDir(String sourceFile)
            throws ZipException {
        return unzip(sourceFile, Files.createTempDir().getAbsolutePath(), null);
    }

    public static void deleteQuietly(List<File> files){
        for(File file:files){
            FileUtils.deleteQuietly(file);
        }
    }
}
