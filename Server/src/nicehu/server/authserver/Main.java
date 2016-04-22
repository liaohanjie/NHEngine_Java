package nicehu.server.authserver;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;

import nicehu.nhsdk.candy.log.LogBackMgr;
import nicehu.nhsdk.core.data.SD;
import nicehu.nhsdk.core.type.ServerType;
import nicehu.server.authserver.core.AuthHandler;
import nicehu.server.common.handler.ShutdownReqHandler;
import nicehu.server.manageserver.config.core.ConfigPath;
import nicehu.server.manageserver.config.serverconfig.ServerConfigMgr;

public class Main
{
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args)
	{
		LogBackMgr.load(ConfigPath.file_logback);

		String serverName = "AuthServer";
		SD.init(ServerType.AUTH, serverName);
		logger.warn("Server Name: {}", SD.serverName);

		ServerConfigMgr.instance.reload();

		SD.serverInitHandler = new AuthInitHandler();
		SD.socketServerForS.initialize(16);
		SD.socketServerForC.initialize(16);
		SD.httpCServer.initialize(16);
		AuthHandler.init();

		String manageIp = ServerConfigMgr.instance.getManageIp();
		int managePort = ServerConfigMgr.instance.getManagePort();
		logger.warn("Authserver connecting manager server  ip={} port={}", manageIp, managePort);
		SD.socketServerForS.connectTo(new InetSocketAddress(manageIp, managePort));

		Signal.handle(new Signal("TERM"), new ShutdownReqHandler());
	}

}
