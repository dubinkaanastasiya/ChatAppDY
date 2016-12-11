import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;

public class IPSaving {

    private static String IP;

    public static void writeData(String RemoteLogin, String RemoteIP) throws IOException
    {
        PrintWriter out=new PrintWriter(new FileWriter("IP.txt", true));
        String s = RemoteLogin + " " + RemoteIP;
        out.println(s);
        out.close();
    }

    public static boolean isSaved(String RemoteLogin)
    {
        boolean flag=false;
        try
        {
            BufferedReader in=new BufferedReader(new FileReader("IP.txt"));
            String line;
            while (true)
            {
                line=in.readLine();
                if (line==null)
                    break;
                if (line.substring(0, RemoteLogin.length()+1).equals(RemoteLogin+" "))
                {
                    IP = line.substring(RemoteLogin.length() + 1);
                    flag=true;
                    break;
                }
                else
                {
                    flag=false;
                }
            }
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean isAble(String RemoteLogin, String RemoteIP)
    {
      if (isSaved(RemoteLogin))
      {
          if (RemoteIP.equals(IP))
          {
             return true;
          }
          else
          {
              return false;
          }
      }
      else
      {
          return true;
      }
    }

    public static String getIP()
    {
        return IP;
    }

    public static void main(String args[]) throws IOException
    {
        writeData("Nastya", "192.168.1.2");
        writeData("Artem", "123456789");
        System.out.println(isSaved("Artem"));
        System.out.println(IP);
    }
}
