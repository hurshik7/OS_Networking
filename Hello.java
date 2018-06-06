/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author koala
 */
import java.io.BufferedReader;
import java.io.*;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
public class Hello {
   private DataInputStream reader;
   private DataOutputStream writer;
   private ServerSocket server;
   private Socket socket;
   private FileManager fm;
   private HashMap<String, Socket> socketList;
   
   public static void main(String[] args) {
      // TODO Auto-generated method stub
      Hello server = new Hello();
      server.start();
   }
   
   public Hello() {
       socketList = new HashMap<String, Socket>();
       fm = new FileManager();
   }
   
   private void registerClient(Socket clientSocket) {
       
       try {
        String addr = clientSocket.getInetAddress().toString() + ":" + String.valueOf(clientSocket.getPort());
            
        System.out.println("클라이언트가 접속했습니다.");
        socketList.put(addr, clientSocket); // on disconnect??
        
        reader = new DataInputStream(clientSocket.getInputStream());
        writer = new DataOutputStream(clientSocket.getOutputStream());

        ThreadServerHandler handler = new ThreadServerHandler(clientSocket, fm, socketList);
        handler.start();
        handler.sleep(1000);

        System.out.println(fm.getFileTable());

       } catch (Exception err) {
           err.printStackTrace();
       }
       
   }
   
   public void start() {
      try {
         server = new ServerSocket(12345);
         System.out.println("서버가 활성화되었습니다.");
         while (true) {
            
            Socket clientSocket = server.accept();
            registerClient(clientSocket);
            
            
//            String[] fileNames = (String[])ois.readObject();
//            String addr = clientSocket.getInetAddress().toString() + ":" + String.valueOf(clientSocket.getPort());
// 
//            this.fm.putFiles(addr, fileNames);
//            HashMap<String, String> ftb = fm.getFileTable();
//            
//            System.out.println(ftb);
                    
                    
//                sendFile();
         }
      } catch(Exception e) {
         e.printStackTrace();
      } 
   }
   
    public void sendFile() {
        try {
            while(true) {
                String fileName = reader.readUTF();
                writer.writeUTF(    findFile(fileName)   );
                System.out.println(fileName + "을(를) 전송 완료했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("클라이언트가 나갔습니다.");
            try {
                if(reader != null) reader.close();
                if(writer != null) writer.close();
                if(socket != null) socket.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public String findFile(String fileName) {
      String result = "";
      try {
         FileReader fr = new FileReader(fileName);
         BufferedReader br = new BufferedReader(fr);
         String temp;
         while ((temp = br.readLine()) != null) {
            result += temp + "\n";
         }
         br.close();
      } catch(Exception e) {
         e.printStackTrace();
      }
      return result;
   }
}



class FileManager {
    
    HashMap<String, String> fileTable = new HashMap<String, String>();
    public void putFiles(String addr, String[] files) {
        for (String file : files) {
            this.fileTable.put(file, addr);
        }
    }
    
    public String getAddr(String fileName) {
        return this.fileTable.get(fileName);
    }
    
    public HashMap<String, String> getFileTable() {
        return this.fileTable;
    }

}

// 클라이언트로 데이터를 전송할 스레드 클래스
class ThreadServerHandler extends Thread {
    //멤버변수
    private Socket connectedClientSocket;
    private FileManager fm;
    private HashMap<String, Socket> clientList;
    //생성자
    public ThreadServerHandler(Socket connectedClientSocket, FileManager fm, HashMap<String, Socket> clientList) {
     //Client와 통신할 객체를 초기값으로 받아 할당합니다.
        this.clientList = clientList;
        this.connectedClientSocket = connectedClientSocket;
        this.fm = fm;
    }
    //start() 메소드 호출 시 실행됩니다.
    
    // do encapsulation
    
    private void updateFileTable(String addr, String[] fileNames) {
        fm.putFiles(addr, fileNames);
    }
    
    private void syncClients() {
        
        try {
            for (String addr : clientList.keySet()) {
                Socket clientSocket = clientList.get(addr);
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.writeObject(fm.getFileTable());
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    
    public void run() {
        try {
            //클라이언트로 내용을 출력 할 객체 생성
            
            ObjectInputStream ois = new ObjectInputStream(connectedClientSocket.getInputStream());
//            ObjectOutputStream oos = new ObjectOutputStream(connectedClientSocket.getOutputStream());
            
            System.out.println("Hello>");
            String[] fileNames = (String[])ois.readObject();
            System.out.println("Hello>!!!");
            String addr = connectedClientSocket.getInetAddress().toString() + ":" + String.valueOf(connectedClientSocket.getPort());
            
            System.out.println(1);
            updateFileTable(addr, fileNames);
            System.out.println(2);
            syncClients();      
            System.out.println(3);      
            
//            this.fm.putFiles(addr, fileNames);
//            HashMap<String, String> ftb = fm.getFileTable();
            
//            System.out.println(ftb);
                    

//버퍼에 문자열을 기록함
//            writer.write("강사용 스레드 다중접속 서버 접속");
//            writer.newLine();  //줄 변경
//            writer.write("\n★ 등산했던 산들 ★\n");
//            writer.write("----------------------------------------\n");
//            writer.write("[관악산], 도봉산, 북한산, 사패산");
//            writer.newLine();
//            writer.write("\n★ 기억에남는 영화 ★\n");
//            writer.write("----------------------------------------\n");
//            writer.write("[트로이], 아마갯돈, 스타워즈, 반지의제왕");
//            writer.write("\n\n\n");
//            //실제로 Client로 전송함  
//            writer.flush();
        } catch(Exception ignored) {
            ignored.printStackTrace();
        } finally {
            try {
                connectedClientSocket.close(); // 클라이언트 접속 종료
            } catch(IOException ignored) {}
        }
    }
}
