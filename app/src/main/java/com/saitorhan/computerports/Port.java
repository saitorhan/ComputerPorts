package com.saitorhan.computerports;

public class Port {
    String serviceName;
    String portNumber;
    String transport;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Port(String serviceName, String portNumber, String transport, String description) {
        this.serviceName = serviceName;
        this.portNumber = portNumber;
        this.transport = transport;
        this.description = description;
    }

    String description;
}
