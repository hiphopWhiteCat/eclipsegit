package com.beeagle.app.udpclient.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beeagle.app.udpclient.UdpService;
import com.beeagle.app.udpclient.bean.AlarmEvent;
import com.beeagle.app.udpclient.bean.ClientUpdateEvent;
import com.beeagle.app.udpclient.bean.Config;
import com.beeagle.app.udpclient.bean.Event;
import com.beeagle.app.udpclient.bean.ServerBuFangEvent;
import com.beeagle.app.udpclient.bean.ServerLedEvent;
import com.beeagle.app.udpclient.bean.ServerLedOneEvent;
import com.beeagle.app.udpclient.inf.IEventListener;

public class TestEventListener implements IEventListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static String hostIp = "192.168.100.252";
	private static String distIp = "192.168.100.10";
	private UdpService udpService;

	public void service(Event event) {
		logger.info("事件编码={}", event.getCommand());
		
		
		switch (event.getCommand()) {
		case Event.OFFLINE:
			// 设备下线
			logger.info("设备下线:ID={}", event.getId());
			break;
		case Event.ONLINE:
			// 设备上线
			logger.info("设备上线:ID={}", event.getId());
			break;

		case Event.SERVER_BUFANG_SERVICE_REPLY:
			// 布防成功消息
			logger.info("布防成功:ID={}", event.getId());
			break;
			
		case Event.SERVER_LED_SEND_ONE_REPLY:
			// 单个信号灯成功消息
			logger.info("单个LED信号灯更新成功:ID={}", event.getId());

			break;
		case Event.CLIENT_LED_SEND_REPLY:
			// 所有信号灯成功消息
			logger.info("所有LED信号灯更新成功:ID={}", event.getId());

			break;
		case Event.ALARM:
			AlarmEvent alarmEvent = (AlarmEvent) event;
			
			logger.info("报警信息,{},模块地址:{},防区:{},状态:{},时间:{}", event.getId(), event.getAlarmAddr(), alarmEvent.getIndex(),
					alarmEvent.getStatue() == 1 ? "报警" : "正常",event.getTimer());
			//udpService.sendMessage(builderLedOne(alarmEvent));
			
			break;
			
		default:
			logger.info("未知数据类型={}", event);
			break;
		}

	}
	
	private ServerLedOneEvent builderLedOne(AlarmEvent alarmEvent){
		ServerLedOneEvent event=new ServerLedOneEvent();
		event.setHostIp(hostIp);
		event.setDistIp("192.168.1.100");
		event.setDeviceType(0x3000000);
		event.setReportAddr(100);
		event.setAlarmAddr(3);
		event.setNumber(40);
		event.setStatus(1);
		return event;
	}

	public static void main(String[] args) {
		TestEventListener listener = new TestEventListener();
		listener.start();
		
	}

	public void start() {
		Config config = new Config();
		config.setIp(hostIp);// 设置主机IP
		config.setRecvPort(1036);// 设置接收数据UDP端口
		config.setSendPort(28971);// 设置发送端口
		config.setTimeout(1000 * 15);// 设置终端超时时间
		udpService = new UdpService(config);
		udpService.register(this);// 设置第三方系统的监听器
		udpService.start();// 启动服务

		this.sendMessage(udpService);// 发送消息
	}

	public void sendMessage(UdpService service) {
		// service.sendMessage(builderAllLEDEvent());
		// service.sendMessage(builderOneLEDEvent());
		//service.sendMessage(builderBufangEvent());
		
	}

	public Event builderAllLEDEvent() {
		ServerLedEvent event = new ServerLedEvent();

		event.setHostIp(hostIp);
		event.setDistIp(distIp);
		event.setDeviceType(0X00000000);
		event.setReportAddr(0);
		event.setAlarmAddr(0);
		event.setStatus(0);
		return event;
	}

	public Event builderOneLEDEvent() {
		ServerLedOneEvent event = new ServerLedOneEvent();

		event.setHostIp(hostIp);
		event.setDistIp(distIp);
		event.setDeviceType(0X00000000);
		event.setReportAddr(0);
		event.setAlarmAddr(0);
		event.setNumber(10);
		event.setStatus(0);
		return event;
	}

	/**
	 * 布防事件
	 * 
	 * @return
	 */
	public Event builderBufangEvent() {
		ServerBuFangEvent event = new ServerBuFangEvent();

		event.setHostIp(hostIp);
		event.setDistIp(distIp);
		event.setDeviceType(0X00000000);
		event.setReportAddr(0);
		event.setAlarmAddr(0);
		byte[] bs = new byte[3];
		bs[0] = 0x12;
		bs[1] = 0x34;
		bs[2] = 0x56;
		event.setCiphers(bs);
		event.setIndex(1);
		event.setStatue(0);
		return event;
	}

	//genggai hello  冲突 修改
}
