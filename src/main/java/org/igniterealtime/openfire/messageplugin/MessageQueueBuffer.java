package org.igniterealtime.openfire.messageplugin;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.igniterealtime.openfire.messageplugin.utils.HazelcastUtils;
import org.igniterealtime.openfire.messageplugin.utils.JedisPoolUtils;

public class MessageQueueBuffer {
    private ExecutorService processorPool = Executors.newFixedThreadPool(10);
    // 缓存队列，避免瞬发的大量数据
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);

    /**
     * 开启缓冲
     */
    public void start() {
        for (int i = 0; i < 10; i++) {
            processorPool.execute(new BlockingMessageProcessor(queue));
        }
    }

    public BlockingQueue<String> getBufferQueue() {
        return queue;
    }

    private class BlockingMessageProcessor implements Runnable {

        private BlockingQueue<String> queue;

        public BlockingMessageProcessor(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                	String msg = queue.take();
                	if(JedisPoolUtils.checkRedis()) {
                		JedisPoolUtils.tryPush(msg);
                	}else {
                		HazelcastUtils.tryPush(msg);	
                	}
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 需要单例模式时使用
     *
     * @return
     */
    public static MessageQueueBuffer getSingleInstance() {
        return SingletonQueueBuffer.instance;
    }

    private static class SingletonQueueBuffer {
        private static final MessageQueueBuffer instance = new MessageQueueBuffer();
    }
}
