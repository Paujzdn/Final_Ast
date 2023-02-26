package utils;

import ast.protocols.tcp.TCPSegment;

public interface Channel {

    public void send(TCPSegment seg);

    public TCPSegment receive();

    public int getMSS();
}
