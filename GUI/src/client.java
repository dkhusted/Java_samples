import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.net.*;
class client {
public static void main(String args[]) throws Exception    
{
    String sockin;
    try
    {
        Socket csock = new Socket(InetAddress.getLocalHost(),4444);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader br_sock = new BufferedReader(new InputStreamReader(csock.getInputStream()));
        PrintStream ps = new PrintStream(csock.getOutputStream());
        System.out.println("Start echoing... type 'end' to terminate");
        while((sockin=br.readLine())!=null)
        {
            ps.println(sockin);
            if(sockin.equals("end"))
                break;
            else 
                System.out.println("echoed from server:"+br_sock.readLine());
            
        }
    }
catch(UnknownHostException e)
{
    System.out.println(e.toString());
    
}
catch(IOException ioe)
{
    System.out.println(ioe);
}
}
}