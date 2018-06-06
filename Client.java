/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author koala
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.util.*;

import java.io.File;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFrame;
public class Client extends JFrame {
   private DataInputStream reader;
   private ObjectOutputStream writer;
   private Socket socket;
   
   public static void main(String[] args) {
      // TODO Auto-generated method stub
      Client client = new Client();
      client.connect();
   }
   
   
   
   public void connect() {
      try {
          System.out.println("HER~~E");
         socket = new Socket("127.0.0.1", 12345);
         writer = new ObjectOutputStream(socket.getOutputStream());
//         System.out.println("HEREasdsadas");
         
         System.out.println("HEREasdsadas1");
         DirScanner dirScanner = new DirScanner();
         System.out.println("HEREasdsadas2");
         String[] files = dirScanner.scanAll();
         System.out.println("HERE");
         writer.writeObject(files);
         System.out.println("HEREWWWW");
         
         ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//         while (true) {
            HashMap<String, String> gfbl = (HashMap<String, String>)ois.readObject();
            System.out.println(gfbl);
            
//         }
//         Scanner scan = new Scanner(System.in);
//         String input = scan.nextLine();
//         writer.writeUTF(input);
//         String result = reader.readUTF();
//         System.out.println(result);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
  
}


class DirScanner {
    public String[] scanAll() {
        File file = new File("/Users/koala/Desktop/test1/");
        return file.list();
    }
}