package com.hisaige.websocket.support;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension;

import java.net.SocketAddress;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public class WebsocketSession {

	// 即channelId
	private String Id;
	private Channel channel;
	private List<WebSocketExtension> extensions;
    private String path;
    private HttpHeaders handshakeHeaders;
    private Map<String, List<String>> parameters;
    private Map<String, Object> attributes;
    private String token;
    private String clientIp;
    //用户ID，可能为null
	private String userId;
	/**
	 * AbstractActionPushEvent中actionType关联的用户标识，应该与userId一致，
	 * 用于调用com.infinova.venus.service.SysUserVEventConfigService中的getUserIdByToken方法获取值
	 * 可能为null
	 */
	private String actionUserId;
	private String clientId;
	/**
	 * 用户针对某事件的订阅，来源于websocket请求的url，
	 * 由于url长度的限制，不支持太多个事件类型订阅
	 * 传空表示订阅所有事件
	 */
	private List<String> subscribeEvents;
    private String userName;
    private Principal principal;
    private SocketAddress localAddress;
    private SocketAddress remoteAddress;
    private String acceptedProtocol;
    private int textMessageSizeLimit;
    private int binaryMessageSizeLimit;
    private WebSocketServerHandshaker webSocketServerHandshaker;
    private HttpHeaders httpHeaders;
    private String subscribeKey;
    private Long subscribeId;
    
    /**
     * 允许发送数据失败的最大次数，超过该次数，后台将断开此客户端的连接，
     * -1表示最大限度允许发送失败后端不会因此断开连接
     */
    private int maxFailedNum = -1;
    
    private long createTime = System.currentTimeMillis();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}
	public void setHttpHeaders(HttpHeaders httpHeaders) {
		this.httpHeaders = httpHeaders;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public List<WebSocketExtension> getExtensions() {
		return extensions;
	}
	public void setExtensions(List<WebSocketExtension> extensions) {
		this.extensions = extensions;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public HttpHeaders getHandshakeHeaders() {
		return handshakeHeaders;
	}
	public void setHandshakeHeaders(HttpHeaders handshakeHeaders) {
		this.handshakeHeaders = handshakeHeaders;
	}
	public Map<String, List<String>> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, List<String>> parameters) {
		this.parameters = parameters;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public Principal getPrincipal() {
		return principal;
	}
	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public SocketAddress getLocalAddress() {
		return localAddress;
	}
	public void setLocalAddress(SocketAddress localAddress) {
		this.localAddress = localAddress;
	}
	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(SocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	public String getAcceptedProtocol() {
		return acceptedProtocol;
	}
	public void setAcceptedProtocol(String acceptedProtocol) {
		this.acceptedProtocol = acceptedProtocol;
	}
	public int getTextMessageSizeLimit() {
		return textMessageSizeLimit;
	}
	public void setTextMessageSizeLimit(int textMessageSizeLimit) {
		this.textMessageSizeLimit = textMessageSizeLimit;
	}
	public int getBinaryMessageSizeLimit() {
		return binaryMessageSizeLimit;
	}
	public void setBinaryMessageSizeLimit(int binaryMessageSizeLimit) {
		this.binaryMessageSizeLimit = binaryMessageSizeLimit;
	}
	public WebSocketServerHandshaker getWebSocketServerHandshaker() {
		return webSocketServerHandshaker;
	}
	public void setWebSocketServerHandshaker(WebSocketServerHandshaker webSocketServerHandshaker) {
		this.webSocketServerHandshaker = webSocketServerHandshaker;
	}
	public String getSubscribeKey() {
		return subscribeKey;
	}
	public void setSubscribeKey(String subscribeKey) {
		this.subscribeKey = subscribeKey;
	}
	public Long getSubscribeId() {
		return subscribeId;
	}
	public void setSubscribeId(Long subscribeId) {
		this.subscribeId = subscribeId;
	}

	public void send(String msg){
		channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public List<String> getSubscribeEvents() {
		return subscribeEvents;
	}

	public void setSubscribeEvents(List<String> subscribeEvents) {
		this.subscribeEvents = subscribeEvents;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getMaxFailedNum() {
		return maxFailedNum;
	}

	public void setMaxFailedNum(int maxFailedNum) {
		this.maxFailedNum = maxFailedNum;
	}

	public String getActionUserId() {
		return actionUserId;
	}

	public void setActionUserId(String actionUserId) {
		this.actionUserId = actionUserId;
	}

	@Override
	public String toString() {
		return "WebsocketSession [Id=" + Id + ", channel=" + channel + ", token=" + token + ", clientIp=" + clientIp
				+ ", userId=" + userId + ", clientId=" + clientId + "]";
	}
}
