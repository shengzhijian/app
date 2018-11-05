package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class Server {
    private ServerSocket server;
    /*
    存放所有客户端输出流，用于广播消息
     */
    private Collection<PrintWriter> allout;
    public Server(){
        System.out.println("正在启动服务端。。。");
        try {
            server=new ServerSocket(8088);
            allout=new ArrayList <PrintWriter>();
            System.out.println("服务器启动完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        try {
            while (true){
                System.out.println("等待客户端连接。。。。");
                Socket socket=server.accept();
                System.out.println("一个客户端连接了！");
                ClientHandler clientHandler=new ClientHandler(socket);
                Thread t=new Thread(clientHandler);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args){
        Server server=new Server();
        server.start();
    }
    /**
     * 处理与一个客户端的交互
     */
    private class ClientHandler implements Runnable{
        private Socket socket;
        //记录档期客户端的地址信息
        private String host;
        public ClientHandler(Socket socket){
            this.socket=socket;
            InetAddress address=socket.getInetAddress();
            host=address.getHostAddress();
        }
        @Override
        public void run() {
            PrintWriter printWriter=null;
            try {
                InputStream in=socket.getInputStream();
                InputStreamReader inputStreamReader=new InputStreamReader(in,"UTF-8");
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                OutputStream outputStream=socket.getOutputStream();
                OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream,"UTF-8");
                printWriter=new PrintWriter(outputStreamWriter,true);
                //将该客户端的输入流存入allOut
                allout.add(printWriter);

                String line=null;
                while ((line=bufferedReader.readLine())!=null){
                    System.out.println("客户端说："+line);
                    //回复所有客户端
                    for(PrintWriter pw:allout){
                        pw.println(host+"说："+line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                /*
                处理客户端断开连接后的操作
                 */
                System.out.println("一个客户端断线了");
                //将该客户端的输入流从共享集合中删除
                allout.remove(printWriter);
                //关闭Socket，同时输入流和输出流也同时关闭了。
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
