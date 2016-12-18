import java.io.*;

class IPSaving {
    private static String IP;

    static void writeData(String remoteLogin, String remoteIP) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter("IP.txt", true));
        String s = remoteLogin + " " + remoteIP;
        out.println(s);
        out.close();
    }

    static boolean isSaved(String RemoteLogin) {
        boolean flag = false;
        try {
            BufferedReader in = new BufferedReader(new FileReader("IP.txt"));
            while (true) {
                String line = in.readLine();
                if (line == null)
                    break;

                if (line.length() < RemoteLogin.length())
                    break;

                if (line.substring(0, RemoteLogin.length() + 1).equals(RemoteLogin + " ")) {
                    IP = line.substring(RemoteLogin.length() + 1);
                    flag = true;
                    break;
                } else
                    flag = false;
            }
            in.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return flag;
    }

    static boolean isAble(String myLogin, String myIP) {
        return !isSaved(myLogin) || myIP.equals(IP);
    }

    static String getIP() {
        return IP;
    }
}