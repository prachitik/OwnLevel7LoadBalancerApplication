package com.prachitik.own_level7_load_balancer.service;

import com.prachitik.own_level7_load_balancer.Model.BackendServer;
import com.prachitik.own_level7_load_balancer.loadBalancer.LoadBalancer;
import com.prachitik.own_level7_load_balancer.strategy.LoadBalancerStrategy;
import com.prachitik.own_level7_load_balancer.strategy.RoundRobinStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadBalancerServiceTest {
   // private final CopyOnWriteArrayList<BackendServer> servers = new CopyOnWriteArrayList<>();
    //private LoadBalancer loadBalancer;

    //private final ExecutorService requestExecutor = Executors.newFixedThreadPool(10);
    //private final ScheduledExecutorService healthCheckExecutor = Executors.newScheduledThreadPool(5);
    private LoadBalancerService loadBalancerService;

    @BeforeEach
    void setUp(){
        //LoadBalancerStrategy strategy = new RoundRobinStrategy();
        //loadBalancer = new LoadBalancer(servers, strategy);
        loadBalancerService = new LoadBalancerService();
    }

    @Test
    void testRouteRequest() throws Exception {
        String url = "http://localhost:8081";
        loadBalancerService.registerServer(url);

        // Set server to healthy
        loadBalancerService.getServers().get(0).setHealthy(true);

        Future<String> future = loadBalancerService.routeRequest();

        assertEquals("Routing request to: " + url, future.get());
    }

//    @Test
//    void testRouteRequestThrowsExceptionWhenNoServers() {
//        assertThrows(IllegalStateException.class, () -> {
//            loadBalancerService.routeRequest().get();
//        });
//    }

    @Test
    void testRegisterServer() {
        String url = "http://localhost:8081";
        loadBalancerService.registerServer(url);
        assertEquals(1, loadBalancerService.getServers().size());
        assertEquals(url, loadBalancerService.getServers().get(0).getUrl());
    }

    @Test
    void testDeregisterServer() {
        String url = "http://localhost:8081";
        loadBalancerService.registerServer(url);
        assertEquals(1, loadBalancerService.getServers().size());

        loadBalancerService.deregisterServer(url);
        assertEquals(0, loadBalancerService.getServers().size());
    }

    @Test
    void testPerformHealthCheck() {
        String url = "http://localhost:8081";
        loadBalancerService.registerServer(url);

        BackendServer server = loadBalancerService.getServers().get(0);

        // Simulate healthy server
        LoadBalancerService spyService = Mockito.spy(loadBalancerService);
        doReturn(true).when(spyService).performHealthCheck(server);

        spyService.getServers().get(0).setHealthy(true);
        assertTrue(spyService.getServers().get(0).isHealthy());

        // Simulate unhealthy server
        doReturn(false).when(spyService).performHealthCheck(server);
        spyService.getServers().get(0).setHealthy(false);
        assertFalse(spyService.getServers().get(0).isHealthy());
    }

    @Test
    void testShutdown() {
        loadBalancerService.shutdown();

        assertTrue(loadBalancerService.getServers().isEmpty());
        assertTrue(loadBalancerService.getServers().isEmpty());
    }

}