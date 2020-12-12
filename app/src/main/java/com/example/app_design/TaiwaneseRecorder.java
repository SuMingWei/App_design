package com.example.app_design;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;


public class TaiwaneseRecorder extends AsyncTask<String, Void, String> {

    private String taiwaneseResult;
    private JSONObject return_result;

    @Override
    protected String doInBackground(String... params) {
        //由SERVER端提供之token
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJpZCI6NzgsInVzZXJfaWQiOiIwIiwic2VydmljZV9pZCI6IjMiLCJzY29wZXMiOiI5OTk5OTk5OTkiLCJzdWIiOiIiLCJpYXQiOjE1NDEwNjUwNzEsIm5iZiI6MTU0MTA2NTA3MSwiZXhwIjoxNjk4NzQ1MDcxLCJpc3MiOiJKV1QiLCJhdWQiOiJ3bW1rcy5jc2llLmVkdS50dyIsInZlciI6MC4xfQ.K4bNyZ0vlT8lpU4Vm9YhvDbjrfu_xuPx8ygoKsmovRxCCUbj4OBX4PzYLZxeyVF-Bvdi2-wphGVEjz8PsU6YGRSh5SDUoHjjukFesUr8itMmGfZr4BsmEf9bheDm65zzbmbk7EBA9pn1TRimRmNG3XsfuDZvceg6_k6vMWfhQBA";
        //@@@ + SET名(EX:S07) + (8-length(set名))個空白字元
        String s = "@@@main\u0000\u0000\u0000\u0000";
        //若透過Android手機傳送，則設為"A"；若透過網頁傳送，則設為"W"
        String label = "A";
        String model_name = "Minnan\u0000\u0000";
        String serviceId = "0001";


        String outmsg = token + "@@@" + model_name + label + serviceId;

        //連接socket
        Socket socket = new Socket();
        InetSocketAddress isa = new InetSocketAddress("140.116.245.149", 2804);
        try {
            //將outmsg轉成byte[]
            byte[] token_et_s = outmsg.getBytes();
            //將語音檔案轉成byte[]，使用下方convert(String path) function
            byte[] samples = convert(params[0]);
            //將outmsg以及語音檔案兩個陣列串接，使用下方 byteconcate(byte[] a, byte[] b) function
            byte[] outbyte = byteconcate(token_et_s, samples);
            //用於計算outmsg和語音檔案串接後的byte數
            byte[] g = new byte[4];

            g[0] = (byte) ((outbyte.length & 0xff000000) >>> 24);
            g[1] = (byte) ((outbyte.length & 0x00ff0000) >>> 16);
            g[2] = (byte) ((outbyte.length & 0x0000ff00) >>> 8);
            g[3] = (byte) ((outbyte.length & 0x000000ff));

            socket.connect(isa, 10000);

            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            // 送出字串
            out.write(byteconcate(g, outbyte));
            out.flush();

//                        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
//                        String path = "/storage/emulated/0/DCIM/output1.wav";//getCacheDir() + "/output.wav";
//                        FileOutputStream fos = new FileOutputStream(path);
//                        byte[] b = new byte[1024];
//                        int count = 0 ;
//                        while ((count = in.read(b)) > 0)// <=0的話就是結束了
//                        {
//                                Log.d("byte length : ", Integer.toString(b.length));
//                                fos.write(b, 0, count);
//
//                        }


            // 接收字串
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            byte[] b = new byte[1024];
            while (in.read(b) > 0)// <=0的話就是結束了
                taiwaneseResult = new String(b, Charset.forName("UTF-8"));
            out.close();
            in.close();
            //    out = null;
            socket.close();
            //    socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

//                taiwaneseResult = taiwaneseResult.split(" ")[0];
//                taiwaneseResult = taiwaneseResult.split("\\.")[1];
        try {
            return_result = new JSONObject(taiwaneseResult);
//                        taiwaneseResult = return_result.getString("wav_name");
            JSONArray results = return_result.getJSONArray("rec_result");
            taiwaneseResult = String.valueOf(results.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return taiwaneseResult;
    }


//        @Override
//        protected void onPostExecute(String s)
//        {
//                super.onPostExecute(s);
//
//        }

    //用於串接兩個byte[]
    private byte[] byteconcate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    //用於將檔案轉換成byte，輸入為檔案路徑，輸出為byte[]
    private byte[] convert(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1; ) {
            bos.write(b, 0, readNum);
        }

        return bos.toByteArray();
    }
}
