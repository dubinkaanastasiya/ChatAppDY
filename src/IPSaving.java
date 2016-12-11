import java.io.*;

class IPSaving {
    static String IP;

    static void writeData(String RemoteLogin, String RemoteIP) throws IOException
    {
        PrintWriter out = new PrintWriter(new FileWriter("IP.txt", true));
        String s = RemoteLogin + " " + RemoteIP;
        out.println(s);
        out.close();
    }

    static boolean isSaved(String RemoteLogin) {
        boolean flag = false;
        try {
            BufferedReader in = new BufferedReader(new FileReader("IP.txt"));
            String line;

            while (true) {
                line = in.readLine();
                if (line == null)
                    break;
                if (line.length()<RemoteLogin.length()) break;
                if (line.substring(0, RemoteLogin.length()+1).equals(RemoteLogin + " "))
                {
                    IP = line.substring(RemoteLogin.length() + 1);
                    flag = true;
                    break;
                }
                else
                    flag = false;
            }
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    static boolean isAble(String remoteLogin, String remoteIP) {
        return !isSaved(remoteLogin) || remoteIP.equals(IP);
    }

    static String getIP()
    {
        return IP;
    }
}