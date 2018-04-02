package com.wmndev.n26;

public class AbstractEndpointTest {
	
    protected String createTransactionJson(long timestamp){
    	return String.format("{\"amount\": 15.5,\"timestamp\":  %s }", timestamp);
    }

}
