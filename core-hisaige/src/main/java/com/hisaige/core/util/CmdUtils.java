package com.hisaige.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author chenyj
 * 2019/9/20 - 16:19.
 **/
public class CmdUtils {

    /**
     * 运行 控制台指令
     * @param cmdStr 控制台指令
     * @return 如果运行命令行后，控制台有返回结果，会将其返回
     * @throws IOException 运行失败会抛IO异常
     */
    public static String runCmd(String cmdStr) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        //这种方式可能不支持重定向到文件，linux未知
        Process process = runtime.exec(cmdStr);
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        String line;
        StringBuilder build = new StringBuilder();
        while ((line = br.readLine()) != null) {
            build.append(line);
            build.append("\r\n");
        }
        process.destroy();
        return new String(build.toString().getBytes(), StandardCharsets.UTF_8);
    }

    /**
     *运行 控制台指令，将文件当作参数运行
     * @param cmdStr 控制台指令
     * @param inputStream 文件流，解析成字符串到控制台中运行
     * @return  如果运行命令行后，控制台有返回结果，会将其返回
     * @throws IOException 运行失败会抛异常
     */
    public static String runCmd(String cmdStr, InputStream inputStream) throws IOException {

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmdStr);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            OutputStream out = process.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            InputStream info = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(info, StandardCharsets.UTF_8);
            BufferedReader infoReader = new BufferedReader(reader)){

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }

            String outStr = sb.toString();
            writer.write(outStr);
            writer.flush();

            out.close();
            br.close();
            writer.close();

            String retStr;
            StringBuilder retSb = new StringBuilder();
            while ((retStr=infoReader.readLine()) != null) {
                retSb.append(retStr)
                        .append("\r\n");
            }
            return retSb.toString();
        } finally {
            process.destroy();
        }
    }
}
