package com.prachitik.own_level7_load_balancer.Model;

public class BackendServer {
    private String url;
    private boolean isHealthy;

    public BackendServer(String url){
        this.url = url;
        this.isHealthy = true;
    }

    public String getUrl() {
        return url;
    }

    public boolean isHealthy() {
        return isHealthy;
    }

    public void setHealthy(boolean healthy) {
        isHealthy = healthy;
    }

}
