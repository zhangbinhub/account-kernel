package pers.acp.communications.server.socket.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import pers.acp.communications.server.socket.base.BaseSocketHandle;
import pers.acp.communications.server.socket.config.ListenConfig;
import pers.acp.tools.common.CommonTools;

/**
 * 报文处理类
 *
 * @author zhang
 */
public final class TcpServerHandle implements Runnable {

    private Logger log = Logger.getLogger(this.getClass());

    private Socket clientSocket;

    private ListenConfig listenConfig;

    private BaseSocketHandle socketResponse;

    public TcpServerHandle(Socket clientSocket, ListenConfig listenConfig, BaseSocketHandle socketResponse) {
        super();
        this.clientSocket = clientSocket;
        this.listenConfig = listenConfig;
        this.socketResponse = socketResponse;
    }

    public void run() {
        InputStream in = null;
        OutputStream out = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            /* 接收数据 start */
            String recvStr = "";
            if (listenConfig.isHex()) {
                byte[] revB = new byte[2048];
                in = clientSocket.getInputStream();
                out = clientSocket.getOutputStream();
                int size = in.read(revB);
                for (int i = 0; i < size; i++) {
                    byte[] tmp = {revB[i]};
                    recvStr += CommonTools.byte2hex(tmp);
                }
            } else {
                char[] revC = new char[2048];
                String defaultCharset = CommonTools.getDefaultCharset();
                String charset = listenConfig.getCharset();
                if (!CommonTools.isNullStr(charset)) {
                    br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), charset));
                    bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), charset));
                } else if (!CommonTools.isNullStr(defaultCharset)) {
                    br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), defaultCharset));
                    bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), defaultCharset));
                } else {
                    br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                }
                int size = br.read(revC);
                for (int i = 0; i < size; i++) {
                    recvStr += revC[i];
                }
            }
            log.debug("tcp receive:" + recvStr);
            /* 接收数据 end */
            String responseStr = this.socketResponse.doResponse(recvStr);
            log.debug("tcp return:" + responseStr);
            /* 返回数据 start */
            if (listenConfig.isHex()) {
                byte[] returnBytes = CommonTools.hex2byte(responseStr);
                out.write(returnBytes);
                out.flush();
            } else {
                bw.write(responseStr);
                bw.flush();
            }
            /* 返回数据 end */
            clientSocket.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
