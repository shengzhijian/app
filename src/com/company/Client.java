package com.company;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * 聊天室客户端
 */

public class Client {
    private Socket socket;
    /*
    初始化客户端
     */
    public Client(){
        /*
        初始化Socket时需要传入ip地址和端口号
         */
        try {
            System.out.println("正在连接服务器。。。。");
            socket=new Socket("localhost",8088);
            System.out.println("与服务器成功建立联系！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    程序的启动方法
     */
    public void start(){

        try {
            //启动读取服务器消息的线程
            ServerHandler handler=new ServerHandler();
            Thread t=new Thread(handler);
            t.start();
            OutputStream out=socket.getOutputStream();
            OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
            PrintWriter printWriter=new PrintWriter(osw,true);
            System.out.println("请开始输入内容");
            Scanner scanner=new Scanner(System.in);
            String message=null;
            Long lastSend=System.currentTimeMillis()-1000;
            while (true){
                String line=scanner.nextLine();
                if (System.currentTimeMillis()-lastSend>=1000){
                    printWriter.println(line);
                    System.out.println("写入完毕");
                }else{
                    System.out.println("发送消息的频率应大于1秒");
                }
                lastSend=System.currentTimeMillis();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        Client client=new Client();
        client.start();
    }
    /**
     * 该线程用于读取服务器端发送过来的消息，并输出到控制台
     */
    private class  ServerHandler implements Runnable{

        @Override
        public void run() {
            try {
                BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                String line=null;
                while ((line=br.readLine())!=null){
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
