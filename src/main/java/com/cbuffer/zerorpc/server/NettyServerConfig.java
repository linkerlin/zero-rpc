package com.cbuffer.zerorpc.server;

import io.netty.channel.ChannelHandler;

/**
 * fireflyc@icloud.com
 */
public class NettyServerConfig {
    private int serverThreads = 2;
    private int listenPort = 3000;
    private int executeThread = 8;
    private ChannelHandler handler;

    public int getServerThreads() {
        return serverThreads;
    }

    public int getListenPort() {
        return listenPort;
    }

    public int getExecuteThread() {
        return executeThread;
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    public static NettyServerConfigBuilder builder() {
        return new NettyServerConfigBuilder();
    }

    public static class NettyServerConfigBuilder {
        private NettyServerConfig config;

        public NettyServerConfigBuilder() {
            this.config = new NettyServerConfig();
        }

        public NettyServerConfigBuilder handler(ChannelHandler handler) {
            config.handler = handler;
            return this;
        }

        public NettyServerConfigBuilder serverThreads(int serverThreads) {
            config.serverThreads = serverThreads;
            return this;
        }


        public NettyServerConfigBuilder listenPort(int listenPort) {
            config.listenPort = listenPort;
            return this;
        }

        public NettyServerConfigBuilder executeThread(int executeThread) {
            config.executeThread = executeThread;
            return this;
        }

        public NettyServerConfig build() {
            return config;
        }
    }
}
