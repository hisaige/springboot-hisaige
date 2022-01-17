package com.hisaige.core.util;

import com.hisaige.core.entity.constant.CoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.net.util.IPAddressUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author chenyj
 * 2019/11/20 - 10:40.
 **/
public class IpUtils extends IPAddressUtil {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    private final static String UNKNOWN = "unknown";
    private final static int MAX_LENGTH = 15;

    /**
     * 获取所有网卡的IP信息
     * @return  List<String> ips
     * @throws SocketException SocketException
     */
    public static List<String> getLocalIps() throws SocketException {

        InetAddress ip;
        List<String> ipList = new ArrayList<>();

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) {
                ip = ips.nextElement();
                if (!ip.isLoopbackAddress() && ip.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                    ipList.add(ip.getHostAddress());
                }
            }
        }
        return ipList;
    }

    public static String getLocalIP() throws SocketException {
        String localIp = null;
        InetAddress inetAddress;
        boolean bFindIP = false;
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            if (null != localIp) {
                break;
            }
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) {
                inetAddress = ips.nextElement();

                // 非127.0.0.1且符合正则
                if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                    localIp = inetAddress.getHostAddress();
                    break;
                }
            }
        }
        return localIp;
    }


    public static String getMacAddr() throws SocketException {

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {

            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) {
                InetAddress inetAddress = ips.nextElement();
                // 非127.0.0.1且符合正则
                if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                    byte[] hardwareAddress = ni.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    int temp;
                    String str;
                    for(int i=0; i<hardwareAddress.length; i++){
                        if(i!=0) {
                            sb.append("-");
                        }
                        temp = hardwareAddress[i]&0xff;
                        str = Integer.toHexString(temp);
                        if(str.length()==1) {
                            sb.append("0").append(str);
                        }else {
                            sb.append(str);
                        }
                    }
                    return sb.toString().toUpperCase();
                }
            }
        }
        return null;
    }

    /**
     * 获取客户端真实IP
     * @return String IP
     */
    public static String getRequestIp(){

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            logger.error("getIpAddr error", e);
        }
        // 使用代理，则获取第一个IP地址
        if (!StringUtils.isEmpty(ip) && ip.length() > MAX_LENGTH) {
            if (ip.indexOf(CoreConstants.COMMA) > 0) {
                ip = ip.substring(0, ip.indexOf(CoreConstants.COMMA));
            }
        }
        return ip;
    }

    /**
     * 判断该IP是否为内网地址
     * @param ip IP地址
     * @return true 如果是内网IP
     */
    public static boolean isIntranetIP(String ip) {
        long number = ipStringToNumber(ip);
        // 该IP是否为127.0.0.1
        if (number == 2130706433L) {
            return true;
        }
        // 该IP是否属于 10.0.0.0 ~ 10.255.255.255 网段
        if (number >= 167772160L && number <= 184549375L) {
            return true;
        }
        // 该IP是否属于 172.16.0.0 ~ 172.31.255.255 网段
        if (number >= 2886729728L && number <= 2887778303L) {
            return true;
        }
        // 该IP是否属于 192.168.0.0 ~ 192.168.255.255 网段
        if (number >= 3232235520L && number <= 3232301055L) {
            return true;
        }
        return false;
    }

    /**
     * 将IP字符串转换成数字
     * @param ip ip
     * @return ip的数字
     */
    public static long ipStringToNumber(String ip) {
        String[] ipParts = ip.split("\\.");
        long number = 0;
        for (int i = 0; i < ipParts.length; i++) {
            int moveBit = (ipParts.length - 1 - i) * 8;
            number += Long.parseLong(ipParts[i]) << moveBit;
        }
        return number;
    }
}
