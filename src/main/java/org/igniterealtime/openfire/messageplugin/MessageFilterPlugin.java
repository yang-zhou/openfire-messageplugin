package org.igniterealtime.openfire.messageplugin;

import org.igniterealtime.openfire.messageplugin.utils.HazelcastUtils;
import org.igniterealtime.openfire.messageplugin.utils.JedisPoolUtils;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.cluster.ClusterManager;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MessageFilterPlugin implements Plugin {
	private static final Logger Log = LoggerFactory.getLogger(MessageFilterPlugin.class);
	
    private InterceptorManager interceptorManager;
    private MessageFilter messageFilter = new MessageFilter();

    public void initializePlugin(PluginManager pluginManager, File file) {
    	Log.info("Initting Message Filter Plugin ... ");
    	if(JedisPoolUtils.checkRedis()) {
    		// 1. 启动Redis实例
    		JedisPoolUtils.singletonStart();
    	}else {
    		// 1. 启动Hazelcast实例
    		HazelcastUtils.singletonStart();
    	}
        // 2. 启动消息接收缓冲器
        MessageQueueBuffer.getSingleInstance().start();
        // 3. 注册消息过滤器
        interceptorManager = InterceptorManager.getInstance();
        interceptorManager.addInterceptor(messageFilter);
        Log.info("Message Filter Plugin Loaded Successfully");
    }

    public void destroyPlugin() {
        if(JedisPoolUtils.checkRedis()) {
        	JedisPoolUtils.getInstance().destroy();
        }else {
        	HazelcastUtils.getInstance().shutdown();
        }
        if (interceptorManager != null) {
            interceptorManager.removeInterceptor(messageFilter);
        }
        // Shutdown is initiated by XMPPServer before unloading plugins
        if (!XMPPServer.getInstance().isShuttingDown()) {
            ClusterManager.shutdown();
        }
    }


}
