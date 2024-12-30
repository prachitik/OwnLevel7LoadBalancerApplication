package com.prachitik.own_level7_load_balancer.strategy;

import com.prachitik.own_level7_load_balancer.Model.BackendServer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface LoadBalancerStrategy {
    BackendServer selectNextServer(CopyOnWriteArrayList<BackendServer> servers);
}
