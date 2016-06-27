package org.xbase.android.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.xbase.android.log.Logger;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;


/**
 * <p>
 * 处理Assets目录下的文件操作
 * </p>
 */
public class AssetUtils {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger  LOG   = Logger.getLogger(AssetUtils.class);
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 获取assets中指定路径下的文件及文件夹列表
     * 
     * @param pContext
     * @param pPath
     *            文件路径，如路径 "assets/fonts" 则传入 "fonts"
     * @return 该路径下的文件及文件夹列表
     */
    public static final String[] getAssetsFilePaths(Context pContext, String pPath) {
        try {
            return pContext.getAssets().list(pPath);
        } catch (IOException e) {
            LOG.e("getAssetsFilePaths.error " + e.toString());
        }
        return null;
    }

    /**
     * 判断assets下是否存在文件
     * 
     * @param pContext
     * @param pFilePath
     *            文件路径, 如"assets/fonts/zh.ttf"传入"fonts/zh.ttf"
     * @return true为存在此文件
     */
    public static final boolean exists(Context pContext, String pFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = pContext.getAssets().open(pFilePath);
            return (inputStream != null);
        } catch (IOException e) {
            LOG.e("exists.error " + e.toString());
            return false;
        } finally {
            close(inputStream);
        }
    }

    /**
     * 获取assets下文件内容长度
     * 
     * @param pContext
     * @param pFilePath
     *            文件路径, 如"assets/fonts/zh.ttf"传入"fonts/zh.ttf"
     * @return 文件长度
     */
    public static final long getFileSize(Context pContext, String pFilePath) {
        AssetFileDescriptor fileDescriptor = getAssetFileDescriptor(pContext, pFilePath);
        return fileDescriptor.getLength();
    }

    /**
     * 获取assets下文件 {@link InputStream}
     * 
     * @param pContext
     * @param pFilePath
     *            文件路径, 如"assets/fonts/zh.ttf"传入"fonts/zh.ttf"
     * @return 文件 {@link InputStream}
     */
    public static final InputStream getInputStream(Context pContext, String pFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = pContext.getAssets().open(pFilePath);
        } catch (IOException e) {
            LOG.e("exists.error " + e.toString());
        }
        return inputStream;
    }

    /**
     * 获取assets下文件 {@link AssetFileDescriptor}
     * 
     * @param pContext
     * @param pFilePath
     *            文件路径, 如"assets/fonts/zh.ttf"传入"fonts/zh.ttf"
     * @return 文件 {@link AssetFileDescriptor}
     */
    public static final AssetFileDescriptor getAssetFileDescriptor(Context pContext, String pFilePath) {
        try {
            return pContext.getAssets().openFd(pFilePath);
        } catch (IOException e) {
            LOG.e("exists.error " + e.toString());
        }
        return null;
    }

    /**
     * 获取assets下的配置对象，要求此文件符合Properties文件规则
     * 
     * @param pContext
     * @param pFilePath
     *            文件路径, 如"assets/fonts/zh.ttf"传入"fonts/zh.ttf"
     * @return 配置对象 {@link Properties}
     */
    public static final Properties getProperties(Context pContext, String pFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = pContext.getAssets().open(pFilePath);
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            LOG.e("getProperties.error " + e.toString());
        } finally {
            close(inputStream);
        }
        return null;
    }

    /**
     * 获取assets文件内容
     * 
     * @param pContext
     * @param pFilePath
     *            文件路径, 如"assets/fonts/zh.ttf"传入"fonts/zh.ttf"
     * @return 文件内容:byte[]
     */
    public static final byte[] getBytes(Context pContext, String pFilePath) {
        try {
            InputStream inputStream = pContext.getAssets().open(pFilePath);
            return readData(inputStream);
        } catch (IOException e) {
            LOG.e("getBytes.error " + e.toString());
        }
        return null;
    }

    /**
     * 获取assets文件内容
     * 
     * @param pContext
     * @param pFilePath
     *            文件路径, 如"assets/fonts/zh.ttf"传入"fonts/zh.ttf"
     * @return 文件内容:String
     */
    public static final String getString(Context pContext, String pFilePath) {
        try {
            InputStream inputStream = pContext.getAssets().open(pFilePath);
            return readString(inputStream);
        } catch (IOException e) {
            LOG.e("getBytes.error " + e.toString());
        }
        return null;
    }

    /**
     * 获取assets文件内容
     * 
     * @param pContext
     * @param pFilePath
     *            文件路径, 如"assets/fonts/zh.ttf"传入"fonts/zh.ttf"
     * @return 文件内容:Bitmap
     */
    public static final Bitmap getBitmap(Context pContext, String pFilePath) {
        try {
            InputStream inputStream = pContext.getAssets().open(pFilePath);
            return readBitmap(inputStream);
        } catch (IOException e) {
            LOG.e("getBytes.error " + e.toString());
        }
        return null;
    }

    private static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            LOG.e("close.error " + e.toString());
        }
    }

    private static byte[] readData(InputStream pInputStream) {
        if (pInputStream == null) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;

        try {
            while ((len = pInputStream.read(buf)) != -1) {
                output.write(buf, 0, len);
            }
            return output.toByteArray();
        } catch (IOException e) {
            LOG.e("readData.error " + e.toString());
            return null;
        } finally {
            close(pInputStream);
        }
    }

    private static String readString(InputStream pInputStream) {
        if (pInputStream == null) {
            return null;
        }
        return readString(new InputStreamReader(pInputStream, UTF_8));
    }

    private static String readString(Reader pReader) {
        if (pReader == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        char[] buf = new char[1024];
        int len = -1;

        try {
            while ((len = pReader.read(buf)) != -1) {
                builder.append(buf, 0, len);
            }
            return builder.toString();
        } catch (IOException e) {
            LOG.e("readString.error " + e.toString());
            return null;
        } finally {
            close(pReader);
        }
    }

    private static Bitmap readBitmap(InputStream pInputStream) {
        if (pInputStream == null) {
            return null;
        }
        try {
            return BitmapFactory.decodeStream(pInputStream);
        } catch (Exception e) {
            LOG.e("readBitmap.error " + e.toString());
            return null;
        } finally {
            close(pInputStream);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
