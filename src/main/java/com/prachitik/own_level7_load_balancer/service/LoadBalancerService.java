package com.prachitik.own_level7_load_balancer.service;

import com.prachitik.own_level7_load_balancer.Model.BackendServer;
import com.prachitik.own_level7_load_balancer.loadBalancer.LoadBalancer;
import com.prachitik.own_level7_load_balancer.strategy.LoadBalancerStrategy;
import com.prachitik.own_level7_load_balancer.strategy.RoundRobinStrategy;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

@Service
public class LoadBalancerService {
    private final CopyOnWriteArrayList<BackendServer> servers = new CopyOnWriteArrayList<>();
    private final LoadBalancer loadBalancer;

    private final ExecutorService requestExecutor = Executors.newFixedThreadPool(10);
    private final ScheduledExecutorService healthCheckExecutor = Executors.newScheduledThreadPool(5);

    public LoadBalancerService() {
        LoadBalancerStrategy strategy = new RoundRobinStrategy();
        this.loadBalancer = new LoadBalancer(this.servers, strategy);
        // todo: health check - is it required here?
        startHealthChecks();
    }

    public Future<String> routeRequest(){
        return requestExecutor.submit(() -> {
            BackendServer server = loadBalancer.getServer();
            return "Routing request to: " + server.getUrl();
        });
    }

    public synchronized void registerServer(String url){

        if (servers.stream().noneMatch(server -> server.getUrl().equals(url))) {
            this.loadBalancer.getServers().add(new BackendServer(url));
            System.out.println("Server added: " + url);
        } else {
            System.out.println("Server already exists: " + url);
        }

    }

    public synchronized void deregisterServer(String url){
        servers.removeIf(server -> server.getUrl().equals(url));
    }

    private void startHealthChecks(){
        healthCheckExecutor.scheduleAtFixedRate(() -> {
            for (BackendServer server : servers) {
                boolean isHealthy = performHealthCheck(server);
                server.setHealthy(isHealthy);
                System.out.println("Health Check: " + server.getUrl() + " -> " +
                        (isHealthy ? "Healthy" : "Unhealthy"));
            }
        }, 0, 10, TimeUnit.SECONDS);
    }


    boolean performHealthCheck(BackendServer server){
        String healthUrl = server.getUrl() + "/actuator/health";
        System.out.println("Performing health check on: " + healthUrl);

        try {
            URL url = new URL(healthUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(10000);   // 10 seconds

            int status = connection.getResponseCode();
            System.out.println("Health check response for " + server.getUrl() + ": " + status);

            return status == 200;
        } catch (IOException ex) {
            System.err.println("Failed health check for server: " + server.getUrl());
            ex.printStackTrace();
            return false;
        }
    }

    @PreDestroy
    public void shutdown(){
        requestExecutor.shutdown();
        healthCheckExecutor.shutdown();
        try {
            if (!requestExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                requestExecutor.shutdownNow();
            }
            if (!healthCheckExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                requestExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            requestExecutor.shutdownNow();
            healthCheckExecutor.shutdown();
        }
    }

    public CopyOnWriteArrayList<BackendServer> getServers(){
        return servers;
    }
}
