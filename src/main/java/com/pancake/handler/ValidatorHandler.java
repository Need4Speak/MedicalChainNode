package com.pancake.handler;

/**
 * Created by chao on 2017/11/21.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancake.entity.util.Const;
import com.pancake.entity.util.NetAddress;
import com.pancake.service.message.impl.*;
import com.pancake.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;


/**
 * 用于解析 socket 中 msg 的类型，并根据不同的类型，交由不同的其他 ValidatorHandler 去处理
 */
public class ValidatorHandler implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(ValidatorHandler.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final Socket socket;
    private CommitMessageService cmtms = new CommitMessageService();
    private NetAddress validatorAddr;

    public ValidatorHandler(Socket socket, NetAddress validatorAddr) {
        this.socket = socket;
        this.validatorAddr = validatorAddr;
    }

    public void run() {
        // read and service request on socket
        try {
            logger.info("远程主机地址：" + socket.getRemoteSocketAddress());

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String rcvMsg = NetUtil.read(dataInputStream);
            String msgType = (String) objectMapper.readValue(rcvMsg, Map.class).get("msgType");
            logger.debug("接收到的 Msg 类型为： [" + msgType + "]");
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
//            out.writeUTF("接收到你发来的消息");
//            out.flush();
//            socket.close();

            // 如果socket中接受到的消息为 blockMsg 类型
            if (msgType.equals(Const.BM)) {
                NetUtil.write(dataOutputStream, "接收到你发送 Block 消息");
//                socket.close();
                try {
                    BlockMessageService.procBlockMsg(rcvMsg, validatorAddr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(msgType.equals(Const.TXM)) {
                NetUtil.write(dataOutputStream, "接收到你发送的 Transaction 消息");
//                socket.close();
                TransactionMessageService.procTxMsg(rcvMsg, validatorAddr);
            }
            // 如果socket中接受到的消息为 PrePrepare 类型
            else if (msgType.equals(Const.PPM)) {
                NetUtil.write(dataOutputStream, "接收到你发送的 PrePrepare 消息");
//                socket.close();
                PrePrepareMessageService.procPPMsg(rcvMsg, validatorAddr);
            }
            // 如果socket中接受到的消息为 Prepare 类型
            else if (msgType.equals(Const.PM)) {
                NetUtil.write(dataOutputStream, "接收到你发送的 Prepare 消息");
//                socket.close();
                logger.info("接收到准备消息");
                PrepareMessageService.procPMsg(rcvMsg, validatorAddr);
            }
            // 如果socket中接受到的消息为 commit 类型
            else if (msgType.equals(Const.CMTM)) {
                NetUtil.write(dataOutputStream, "接收到你发送的 Commit 消息");
//                socket.close();
                logger.info("接收到commit消息");
                cmtms.procCMTM(rcvMsg, validatorAddr);
            }
            else {
                NetUtil.write(dataOutputStream, "未知的 msgType 类型");
//                socket.close();
                logger.error("未知的 msgType 类型");
            }

//            out.writeUTF("\n连接结束");
//            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(MessageService.getSeqNum("seq"));
            MessageService.updateSeqNum("seq");
            System.out.println(MessageService.getSeqNum("seq"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
