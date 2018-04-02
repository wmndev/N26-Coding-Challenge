package com.wmndev.n26;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = N26Application.class)
@WebAppConfiguration
public class TransactionEndpointTests extends AbstractEndpointTest{
	
	@Autowired
	private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
	@Test
	public void contextLoads() {
		assertNotNull(wac);
	}
	 
    @Test
    public void testInvalidTimestampRequestReturns204() throws Exception {
    	String jsonTask = createTransactionJson(123456);
        
    	this.mockMvc.perform(post("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonTask))
                    .andDo(print())
                    .andExpect(status().isNoContent()); 
    }
    
    @Test
    public void testValidTimestampRequestReturns201() throws Exception { 	
        String jsonTask = createTransactionJson(Instant.now().toEpochMilli() - 1000);
        
        this.mockMvc.perform(post("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonTask))
                    .andDo(print())
                    .andExpect(status().isCreated());
    }
}
