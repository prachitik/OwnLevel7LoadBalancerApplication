package com.prachitik.own_level7_load_balancer.controller;

import com.prachitik.own_level7_load_balancer.loadBalancer.LoadBalancer;
import com.prachitik.own_level7_load_balancer.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/")
public class LoadBalancerController {

    private final LoadBalancerService loadBalancerService;

    public LoadBalancerController(LoadBalancerService loadBalancerService){
        this.loadBalancerService = loadBalancerService;
    }

    @PostMapping("servers/register")
    public ResponseEntity<String> registerServer(@RequestParam String serverUrl){
        loadBalancerService.registerServer(serverUrl);
        return ResponseEntity.ok("Registered server: " + serverUrl);
    }

    @DeleteMapping("servers/deregister")
    public ResponseEntity<String> deregisterServer(@RequestParam String serverUrl){
        loadBalancerService.deregisterServer(serverUrl);
        return ResponseEntity.ok("Deregistered the server: " + serverUrl);
    }

    @GetMapping("route")
    public ResponseEntity<String> routeRequest() throws ExecutionException, InterruptedException {
        //todo: add routerequest method in lb service and call here
        try{
            Future<String> response = loadBalancerService.routeRequest();
            return ResponseEntity.ok(response.get()); // wait for the request to be processed
        }catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body("Failed to route request: " + e.getMessage());
        }
    }

}
