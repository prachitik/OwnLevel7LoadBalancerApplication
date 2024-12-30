package com.prachitik.own_level7_load_balancer.strategy;

import com.prachitik.own_level7_load_balancer.Model.BackendServer;
import com.prachitik.own_level7_load_balancer.strategy.LoadBalancerStrategy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements LoadBalancerStrategy {
    private final AtomicInteger currentServerIndex = new AtomicInteger(0);

    @Override
    public BackendServer selectNextServer(CopyOnWriteArrayList<BackendServer> servers){
        if(servers == null || servers.isEmpty()){
            throw new IllegalStateException("No backend servers available.");
        }

        int startIndex = currentServerIndex.get();

        for (int i = 0; i < servers.size(); i++) {
            // Update the index atomically and wrap it around using modulo
            int currentIndex = currentServerIndex.getAndUpdate(index -> (index + 1) % servers.size());
            BackendServer server = servers.get(currentIndex);

            if (server.isHealthy()) {
                return server;
            }
        }
        throw new RuntimeException("No healthy servers available");
    }

}
