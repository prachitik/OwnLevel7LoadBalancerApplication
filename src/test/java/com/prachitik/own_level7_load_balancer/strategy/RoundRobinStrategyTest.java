package com.prachitik.own_level7_load_balancer.strategy;

import com.prachitik.own_level7_load_balancer.Model.BackendServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CopyOnWriteArrayList;


import static org.junit.jupiter.api.Assertions.*;

class RoundRobinStrategyTest {
    private CopyOnWriteArrayList<BackendServer> servers;
    private RoundRobinStrategy strategy;

    @BeforeEach
    void setUp(){
        strategy = new RoundRobinStrategy();
        servers = new CopyOnWriteArrayList<>();
    }

    @Test
    void testInitialiazation(){
        assertNotNull(strategy);
    }

    @Test
    void selectNextServerWithEmptyListThrowsExceptionTest(){
        Exception ex = assertThrows(IllegalStateException.class, () -> strategy.selectNextServer(servers));
        assertEquals("No backend servers available.", ex.getMessage());
    }


    @Test
    void testSelectNextServer(){
        servers.add(new BackendServer("http://localhost:8081"));
        servers.add(new BackendServer("http://localhost:8082"));

        servers.get(0).setHealthy(true);
        servers.get(1).setHealthy(true);
        assertEquals("http://localhost:8081", strategy.selectNextServer(servers).getUrl());
        assertEquals("http://localhost:8082", strategy.selectNextServer(servers).getUrl());
        assertEquals("http://localhost:8081", strategy.selectNextServer(servers).getUrl());

    }

    @Test
    void testSelectNextServerSkipsUnhealthyServers() {
        BackendServer server1 = new BackendServer("http://localhost:8081");
        BackendServer server2 = new BackendServer("http://localhost:8082");
        servers.add(server1);
        servers.add(server2);

        // Mark only the second server healthy
        server1.setHealthy(false);
        server2.setHealthy(true);

        BackendServer selected = strategy.selectNextServer(servers);
        assertEquals(server2, selected);

        // Should keep returning the healthy server
        BackendServer nextSelected = strategy.selectNextServer(servers);
        assertEquals(server2, nextSelected);
    }

    @Test
    void testSelectNextServerThrowsExceptionIfNoHealthyServers() {
        BackendServer server1 = new BackendServer("http://localhost:8081");
        BackendServer server2 = new BackendServer("http://localhost:8082");
        servers.add(server1);
        servers.add(server2);

        // Mark all servers unhealthy
        server1.setHealthy(false);
        server2.setHealthy(false);

        Exception exception = assertThrows(RuntimeException.class, () -> strategy.selectNextServer(servers));
        assertEquals("No healthy servers available", exception.getMessage());
    }

    @Test
    void testThreadSafetyForRoundRobinLogic() throws InterruptedException {
        BackendServer server1 = new BackendServer("http://localhost:8081");
        BackendServer server2 = new BackendServer("http://localhost:8082");
        BackendServer server3 = new BackendServer("http://localhost:8083");

        servers.add(server1);
        servers.add(server2);
        servers.add(server3);

        // Mark all servers healthy
        server1.setHealthy(true);
        server2.setHealthy(true);
        server3.setHealthy(true);

        Runnable task = () -> {
            for (int i = 0; i < 10; i++) {
                BackendServer server = strategy.selectNextServer(servers);
                assertNotNull(server);
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

}