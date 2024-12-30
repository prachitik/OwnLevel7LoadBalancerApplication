package com.prachitik.own_level7_load_balancer.loadBalancer;

import com.prachitik.own_level7_load_balancer.Model.BackendServer;
import com.prachitik.own_level7_load_balancer.strategy.LoadBalancerStrategy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoadBalancer {
    private CopyOnWriteArrayList<BackendServer> servers; // todo: should be thread safe
    private LoadBalancerStrategy loadBalancerStrategy;

    public LoadBalancer(CopyOnWriteArrayList<BackendServer> servers, LoadBalancerStrategy loadBalancerStrategy){
        this.servers = servers;
        this.loadBalancerStrategy = loadBalancerStrategy;
    }

    public List<BackendServer> getServers() {
        return servers;
    }

    public BackendServer getServer(){
        return loadBalancerStrategy.selectNextServer(servers);
    }

}
