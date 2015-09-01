package jrds.starter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class SocketFactory extends Starter {

    public ServerSocket createServerSocket(int port) throws IOException {
        if(! isStarted())
            return null;

        ServerSocket s = new ServerSocket(port) {

            /* (non-Javadoc)
             * @see java.net.ServerSocket#accept()
             */
            @Override
            public Socket accept() throws IOException {
                Socket accepted = super.accept();
                accepted.setTcpNoDelay(true);
                return accepted;
            }

        };
        s.setSoTimeout(getTimeout() * 1000);
        return s;
    }

    public Socket createSocket(String host, int port) throws IOException {
        if(! isStarted())
            return null;

        Socket s = getSocket();
        s.connect(new InetSocketAddress(host, port), getTimeout());
        return s;
    }

    public Socket createSocket(StarterNode host, int port) throws IOException {
        if(! isStarted())
            return null;

        Resolver r = host.find(Resolver.class);
        if(r == null || ! r.isStarted())
            return null;

        Socket s = getSocket();
        s.connect(new InetSocketAddress(r.getInetAddress(), port), getTimeout());
        return s;
    }

    public Socket createSocket() throws IOException {
        if(! isStarted())
            return null;
        return getSocket();
    }

    private Socket getSocket() throws SocketException {
        Socket s = new Socket() {
            public void connect(SocketAddress endpoint) throws IOException {
                super.connect(endpoint, getTimeout() * 1000);
            }

            @Override
            public void connect(SocketAddress endpoint, int timeout)
                    throws IOException {
                super.connect(endpoint, getTimeout() * 1000);
            }
        };
        s.setSoTimeout(getTimeout() * 1000);
        s.setTcpNoDelay(true);
        return s;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return getLevel().getTimeout();
    }

}
