package www.vaiyee.funds;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import www.vaiyee.funds.activity.MainActivity;


/**
 * Created by Administrator on 2018/6/12.
 */

public class ClientThread extends Thread {
    private OutputStream out =null;
    private InputStream in = null;
    private static Handler handler=null;
    public static Socket socket=null;
    //private String account,pwd;
    public ClientThread()
    {
        //this.handler = handler;
//        this.account = account;
//        this.pwd = pwd;
    }
    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }
    @Override
    public void run() {
        try {
            socket = new Socket("192.168.1.103",6666);
           // sendMessage("select***select * from student where s_id="+account+" and pwd='"+pwd+"'***获取服务器数据");
            Log.d("连接","服务器成功");
            MainActivity.isConnect = true;  //当连接服务器成功后将标志位设为true
            while (true) {
                    in = socket.getInputStream();  //这是一个阻塞函数，没有in时会一直阻塞在这里，当服务器断开时就不会阻塞在这里
                    //if (in.read()!=-1) {  //if判断是否有数据返回，防止死循环卡住UI界面,如果条件是>0，测试了条件!-1也一样读不到第一个字节的话，会读不到第一个字节
                    byte[] bytes = new byte[1024];
                    int len;
                    String s = "";
                    while ((len = in.read(bytes, 0, 1024)) != -1) {
                        s += new String(bytes, 0, len);
                        String[] ss = s.split("\\*\\*\\*\\*");
                        if (ss[ss.length - 1].equals("##end")) {
                            break;     //如果读到末尾的结束标记位就跳出循环，显示数据到界面
                        }
                    }
                     response(s);

                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void response(String s)  //将服务器返回结果返回UI界面
    {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("response", s);
        message.setData(bundle);
        message.what = 1;
        handler.sendMessage(message);
    }

    //这个方法是与发送命令给服务器的
    public void sendMessage(final String s)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out = socket.getOutputStream();
                    out.write(s.getBytes("utf8"));
                   // socket.shutdownOutput();//让另一端的read()不阻塞
                    if (s.equals("退出群聊"))
                    {
                        try {
                            in.close();
                            socket.close();
                            handler.sendEmptyMessage(2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void close()
    {
        Log.d("执行了","这句话");
        String ss = "退出群聊";
        try {
            //out = socket.getOutputStream();
            //out.write(ss.getBytes());
           // out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
