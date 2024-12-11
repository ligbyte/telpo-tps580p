package com.stkj.cashier.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellCommandExecutor {
    public static String execute(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                Log.i("kaihuang","   line="+line);
                output.append(line).append("\n");

            }
            int exitCode = process.waitFor(); //等待进程结束并获取退出状态码
            Log.i("kaihuang","exitCode="+exitCode+"  ,cmd="+command);
            if (exitCode == 0) {
                String text= output.toString();
                Log.i("kaihuang","response="+text);
                return  text;
            }else{
                return "Failed to execute shell command with error code " + exitCode;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }
    public static boolean WriteDevFileMSG(String filepath,String cmd){
        File file=new File(filepath);
        if(file.exists())
        {
            FileOutputStream fileOutputStream=null;
            try {
                 fileOutputStream=new FileOutputStream(file);
                 fileOutputStream.write(cmd.getBytes());
                fileOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();

            }finally {
                if(fileOutputStream!=null)
                {
                    try {
                        fileOutputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return true;
    }
}
