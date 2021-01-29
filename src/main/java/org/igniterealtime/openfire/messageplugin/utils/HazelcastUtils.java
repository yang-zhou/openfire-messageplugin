package org.igniterealtime.openfire.messageplugin.utils;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

public class HazelcastUtils {
	private static final Logger Log = LoggerFactory.getLogger(HazelcastUtils.class);
	
    public static String CHAT_MESSAGE_TOPIC = "message/chat/one2one";
    public static String GROUP_CHAT_MESSAGE_TOPIC = "message/chat/group";

    /**
     * 单例模式创建Hazelcast实例
     *
     * @return HazelcastInstance
     */
    private volatile static HazelcastInstance singleton;
    public static HazelcastInstance getInstance() {
        if (singleton == null) {
            synchronized (HazelcastUtils.class) {
                if (singleton == null) {
                    singleton = Hazelcast.newHazelcastInstance(getHazelcastConfig());
                }
            }
        }
        return singleton;
    }

    public static void singletonStart() {
        getInstance();
    }
    
    private static Config getHazelcastConfig() {
        Config config = new Config();
        // 获取配置文件路径
        String configFilePath = HazelcastUtils.class.getResource("/hazelcast-default.xml").toString();
        InputStream inputStream = HazelcastUtils.class.getResourceAsStream("/hazelcast-default.xml");
        Log.info("=======================================================");
        if (inputStream != null) {
            XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder(inputStream);
            config = xmlConfigBuilder.build();
            Log.info("Get Hazelcast Config File From : " + configFilePath);
        }else {
        	Log.info("Get Hazelcast Config File From : " + configFilePath + " Failed, Use The Default Config. ");
        }
        Log.info("=======================================================");
        return config;
    }

    public static void tryPush(String msg) {
        ITopic<String> topic = getInstance().getTopic(CHAT_MESSAGE_TOPIC);
        topic.publish(msg);
    }
}
