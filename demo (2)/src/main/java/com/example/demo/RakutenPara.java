package com.example.demo;

import java.util.HashMap;

public class RakutenPara {
    private HashMap<String, String> parameter;

    // Constructor
    public RakutenPara() {
        this.parameter = new HashMap<>();
    }

    // Getter for the parameter
    public HashMap<String, String> getParameter() {
        return parameter;
    }

    // Setter for the parameter
    public void setParameter(HashMap<String, String> parameter) {
        this.parameter = parameter;
    }
}
