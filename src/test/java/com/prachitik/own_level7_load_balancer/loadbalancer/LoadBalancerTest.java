package com.prachitik.own_level7_load_balancer.loadbalancer;

import com.prachitik.own_level7_load_balancer.Model.BackendServer;
import com.prachitik.own_level7_load_balancer.loadBalancer.LoadBalancer;
import com.prachitik.own_level7_load_balancer.strategy.LoadBalancerStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadBalancerTest {
    private CopyOnWriteArrayList<BackendServer> servers;
    private LoadBalancerStrategy strategy;
    private LoadBalancer loadBalancer;

    @BeforeEach
    void setUp() {
        servers = new CopyOnWriteArrayList<BackendServer>();
        strategy = mock(LoadBalancerStrategy.class);
        loadBalancer = new LoadBalancer(servers, strategy);
    }

    @Test
    void testInitialization(){
        assertNotNull(loadBalancer.getServers());
        assertTrue(loadBalancer.getServers().isEmpty());
        assertEquals(servers, loadBalancer.getServers());
    }

    @Test
    void testGetServers() {
        BackendServer server1 = new BackendServer("http://localhost:8081");
        BackendServer server2 = new BackendServer("http://localhost:8082");
        servers.add(server1);
        servers.add(server2);

        assertEquals(2, loadBalancer.getServers().size());
        assertEquals("http://localhost:8081", loadBalancer.getServers().get(0).getUrl());
        assertEquals("http://localhost:8082", loadBalancer.getServers().get(1).getUrl());
    }

    @Test
    void testGetServerCallsToStrategy() {
        BackendServer server = new BackendServer("http://localhost:8081");
        servers.add(server);
        when(strategy.selectNextServer(servers)).thenReturn(server);
        BackendServer selectedServer = strategy.selectNextServer(servers);
        assertEquals(server, selectedServer);
        verify(strategy, times(1)).selectNextServer((servers));


        //verify(strategy, times(1)).selectNextServer(Servers);
    }

    @Test
    void testGetServerThrowsExceptionForNoServers(){
        when(strategy.selectNextServer(servers)).thenThrow(new IllegalStateException("No Backend servers available"));
        Exception ex = assertThrows(IllegalStateException.class, loadBalancer::getServer);
        assertEquals("No Backend servers available", ex.getMessage());
        verify(strategy, times(1)).selectNextServer(servers);
    }
}