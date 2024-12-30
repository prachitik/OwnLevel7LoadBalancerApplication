package com.prachitik.own_level7_load_balancer.controller;

import com.prachitik.own_level7_load_balancer.service.LoadBalancerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.util.concurrent.Future;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadBalancerControllerTest {
    private LoadBalancerController loadBalancerController;
    private LoadBalancerService mockService;

    @BeforeEach
    void setUp(){
        mockService = mock(LoadBalancerService.class);
        loadBalancerController = new LoadBalancerController(mockService);
    }

    @Test
    void testRegisterServer(){
        String serverUrl = "http://localhost:8081";
        ResponseEntity<String> response = loadBalancerController.registerServer(serverUrl);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Registered server: "+serverUrl, response.getBody());
        verify(mockService, times(1)).registerServer(serverUrl);
    }

    @Test
    void testDeregisterServer(){
        String serverUrl = "http://localhost:8081";
        ResponseEntity<String> response = loadBalancerController.deregisterServer(serverUrl);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Deregistered the server: "+serverUrl, response.getBody());
        verify(mockService, times(1)).deregisterServer(serverUrl);
    }

    @Test
    void testRouteRequestSuccess() throws ExecutionException, InterruptedException {
        String expectedResponse = "Routing request to: http://localhost:8081";
        Future<String> mockFuture = mock(Future.class);

        when(mockService.routeRequest()).thenReturn(mockFuture);
        when(mockFuture.get()).thenReturn(expectedResponse);

        ResponseEntity<String> response = loadBalancerController.routeRequest();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(mockService, times(1)).routeRequest();
    }

//    @Test
//    void testRouteRequestFailure() throws ExecutionException, InterruptedException {
//        when(mockService.routeRequest()).thenThrow(new ExecutionException("Error", new RuntimeException()));
//
//        ResponseEntity<String> response = loadBalancerController.routeRequest();
//
//        assertEquals(500, response.getStatusCodeValue());
//        assertEquals("Failed to route request: Error", response.getBody());
//        verify(mockService, times(1)).routeRequest();
//    }
}