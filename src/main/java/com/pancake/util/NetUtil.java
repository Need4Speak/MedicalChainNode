package com.pancake.util;


import com.pancake.entity.util.Const;
import com.pancake.entity.util.NetAddress;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;


/**
 * Created by chao on 2017/11/10.
 */
public class NetUtil {
    private static String readIp;
    private final static List<NetAddress> validatorList = JsonUtil.getValidatorAddressList(Const.BlockChainConfigFile);
    static {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP

        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress ip = null;
        boolean finded = false;// 是否找到外网IP
        assert netInterfaces != null;
        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {// 外网IP
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                        && !ip.getHostAddress().contains(":")) {// 内网IP
                    localip = ip.getHostAddress();
                }
            }
        }

        if (netip != null && !"".equals(netip)) {
            readIp = netip;
        } else {
            readIp = localip;
        }
    }

    public static String getRealIp() {
        return readIp;
    }

    /**
     * 以 map 的形式返回 ip 与 port
     * @return
     */
    public static NetAddress getPrimaryNode(){
        return validatorList.get(0);
    }

    /**
     * 以 *.*.*.*:**** 的形式返回主节点url
     * @return
     */
    public static String getPrimaryNodUrl(){
        return getPrimaryNode().getIp() + ":" + getPrimaryNode().getPort();
    }

    /**
     * 发送数据
     * @param dataOutputStream
     * @param msg
     * @throws IOException
     */
    public static void write(DataOutputStream dataOutputStream, String msg) throws IOException {
        byte[] msgBytes = msg.getBytes("UTF-8");
        dataOutputStream.writeInt(msgBytes.length);
        dataOutputStream.write(msgBytes);
        dataOutputStream.flush();
    }

    /**
     * 获取数据
     * @param dataInputStream
     * @return 以string 的方式返回
     * @throws IOException
     */
    public static String read(DataInputStream dataInputStream) throws IOException {
        int length = dataInputStream.readInt();
        byte[] data = new byte[length];
        dataInputStream.readFully(data);
        return new String(data, "UTF-8");
    }
}
