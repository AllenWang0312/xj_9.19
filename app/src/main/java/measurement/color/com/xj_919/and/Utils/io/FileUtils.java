package measurement.color.com.xj_919.and.Utils.io;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import measurement.color.com.xj_919.and.Utils.FileOpener.ChoseFileDialog;
import measurement.color.com.xj_919.and.Utils.FileOpener.FileInfo;
import measurement.color.com.xj_919.and.Utils.StringUtils;

public class FileUtils {

    public static final String packagename = "measurement.color.com.xj_919";
    public static final String SYSTEM_DATA_PATH = "/data/data/" + packagename;

    public static final String SDCARD_PATH = getSDcardPath();
public static final String SD_xj=SDCARD_PATH+"/xj_919";
    //    public static final String IMG=SYSTEM_DATA_PATH+"/img";
    public static final String IMG = SD_xj + "/img";
    public static final String Excel = SD_xj + "/excel";

    public static final String IMG_NATIVE_DATA = SD_xj + "/imgCache";
    public static final String FILE_PATH = SYSTEM_DATA_PATH + "/file";

    public static boolean deleteFileIfExist(String absPath) {
        File file = new File(absPath);
        if (file.exists()) {
            file.delete();
            Log.i("deleteFileIfExist:", absPath);
            return true;
        }
        return false;
    }

    public static void playFileWithSystemSeveice(Activity context, String path) {
        // TODO Auto-generated method stub
        Log.i("filepath", path);
        if (path.endsWith(".mp4")) {
            Uri uri = Uri.parse("file://" + path);
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setDataAndType(uri, "video/*");
            context.startActivity(it);
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClassName("com.tencent.mobileqq", "QQAct");
        Uri uri = Uri.parse(path);

        if (path.endsWith(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if (path.endsWith(".ppt")) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (path.endsWith(".xls")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        }
        try {
            context.startActivity(intent);
            return;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Application Available to View ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    public static void showChoseFileToPlayDialog(String dirPath,
                                                 String fileType, final Activity context) {
        if (new File(dirPath).exists()) {
            Log.i("目录存在", dirPath);
            final ArrayList<FileInfo> items = getFileInfoListWithDirPathAndEnd(
                    dirPath, fileType);
            if (items.size() == 0) {
                Toast.makeText(context, "文件夹为空", Toast.LENGTH_SHORT).show();
            } else {
                ChoseFileDialog dialog = new ChoseFileDialog(context, items,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                // TODO Auto-generated method stub
                                FileInfo fi = items.get(arg2);
                                String path = fi.getDirPath() + fi.getName();
                                playFileWithSystemSeveice(context, path);
                            }
                        }, null);
                dialog.show(context.getFragmentManager(), "chosefiledialog");
            }

            // if (MainActivity.videodatas == null
            // || MainActivity.videodatas.size() <= 0) {
            // Toast.makeText(SoftUpdateActivity.this, "路径下不存在视频",
            // Toast.LENGTH_SHORT).show();
            // } else {
            // MainActivity.videolist_layout
            // .setVisibility(View.VISIBLE);
            // }
        } else {
            Toast.makeText(context, "目录不存在", Toast.LENGTH_SHORT).show();
        }
    }

    public static ArrayList<FileInfo> getFileInfoListWithDirPathAndEnd(
            String path, String endwith) {
        ArrayList<FileInfo> vediolist = new ArrayList<FileInfo>();

        File file = new File(path);
        File[] subFile = file.listFiles();
        if (subFile != null) {
            if (subFile.length != 0) {
                for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                    if (!subFile[iFileLength].isDirectory()) {
                        String filename = subFile[iFileLength].getName();
                        if (filename.trim().toLowerCase().endsWith(endwith)) {
                            vediolist.add(new FileInfo(filename, path, ""));
                        }
                    } else {
                        Log.i("getvediofilename", "文件目录有错");
                    }
                }
            }
        }
        return vediolist;
    }

    /**
     * @param path
     * @param endwith
     * @return 文件绝对路径列表;
     */

    private ArrayList<String> getFileNameListWithPathAndEnd(String path,
                                                            String endwith) {
        ArrayList<String> vediolist = new ArrayList<String>();

        File file = new File(path);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();

                // 判断是否为MP4结尾
                if (filename.trim().toLowerCase().endsWith(endwith)) {
                    // String strVideoName = filename.substring(0,
                    // filename.length() - 4);
                    // System.out.println("读取到的视频名称："+strVideoName);
                    vediolist.add(filename);
                    // System.out.println("读取到的视频名称1："+strVideoName);
                }
            } else {
                Log.i("getvediofilename", "文件目录有错");
            }
        }
        return vediolist;
    }

    public static String getSDcardPath() {
        String str = Environment.getExternalStorageDirectory().getPath();
        Log.i("getSDcardPath", str);
        return str;
    }

    /**
     * 获取外置SD卡路径
     *
     * @return 应该就一条记录或空
     */
    public static List<String> getExtSDCardPath() {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file 文件
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(File file) {
        if (file == null) return false;
        // 文件存在并且删除失败返回false
        if (file.exists() && file.isFile() && !file.delete()) return false;
        // 创建目录失败返回false
        if (!createOrExistsDir(file.getParentFile().getPath())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(String filePath) {
        return StringUtils.isSpace(filePath) ? null : new File(filePath);
    }

    public static boolean writeByteArrayToFile(byte[] bytes, String dirPath, String filename) {
        FileUtils.createOrExistsDir(dirPath);
        File file = new File(dirPath, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.i("create file", "faild");
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fos.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static byte[] decodeFileToByteArray(String dirPath,String filename) {

        File f = new File(dirPath,filename);
        if (!f.exists()) {
            Log.i(filename,"not exist");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos.toByteArray();
    }


}
