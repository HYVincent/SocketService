package socketservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpSocketService {
	 // �˿ں�
    private final static int serverPort = 8888;
    // tcp�׽����б�
    private static List<Socket> mList = new ArrayList<Socket>();
    // �׽��ַ���
    private ServerSocket server = null;
    // �̳߳�
    private ExecutorService mExecutorService = null;

    /**
     * ���������
     * @author leibing
     * @createTime 2016/10/06
     * @lastModify 2016/10/06
     * @param args
     * @return
     */
    public static void main(String[] args) {
        // ����tcp�׽��ַ���
        new TcpSocketService();
    }

    /**
     * ����tcp�׽��ַ���
     * @author leibing
     * @createTime 2016/10/06
     * @lastModify 2016/10/06
     * @param
     * @return
     */
    public TcpSocketService() {
        try {
            server = new ServerSocket(serverPort);
            System.out.print("server start ...\n");
            Socket client = null;
            //create a thread pool
            mExecutorService = Executors.newCachedThreadPool();
            while(true) {
                client = server.accept();
                mList.add(client);
                //start a new thread to handle the connection
                mExecutorService.execute(new TcpSocketServic(client));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @className: TcpSocketService
     * @classDescription: tcp�׽��ַ���
     * @author: leibing
     * @createTime: 2016/10/06
     */
    static class TcpSocketServic implements Runnable {
            // �׽���
            private Socket socket;
            // ��������ȡ
            private BufferedReader in = null;
            // ��Ϣ
            public static String msg = "";

            /**
             * ���캯��
             * @author leibing
             * @createTime 2016/10/06
             * @lastModify 2016/10/06
             * @param socket �׽���
             * @return
             */
            public TcpSocketServic(Socket socket) {
                this.socket = socket;
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    msg = "tips: user" +this.socket.getInetAddress() + " come";  
                    this.sendmsg();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    while(true) {
                        if((msg = in.readLine())!= null) {
                            if(msg.equals("exit")) {
                                mList.remove(socket);
                                in.close();
                                msg = "tips: user" +this.socket.getInetAddress() + " exit";
                                socket.close();
                                this.sendmsg();
                                break;
                            } else {
                                msg = socket.getInetAddress() + ":" + msg;
                                this.sendmsg();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        /**
         * ������Ϣ
         * @author leibing
         * @createTime 2016/10/06
         * @lastModify 2016/10/06
         * @param
         * @return
         */
        public static void sendmsg() {
               System.out.println(msg);
               int num =mList.size();
               for (int index = 0; index < num; index ++) {
                   Socket mSocket = mList.get(index);
                   PrintWriter pout = null;
                   try {
                       pout = new PrintWriter(new BufferedWriter(
                               new OutputStreamWriter(mSocket.getOutputStream())),true);
                       pout.println(msg);
                   }catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
        }  
}
