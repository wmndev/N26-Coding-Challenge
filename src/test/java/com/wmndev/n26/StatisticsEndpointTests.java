package com.wmndev.n26;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

import com.wmndev.n26.entity.TransactionsContainer;

import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = N26Application.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatisticsEndpointTests extends AbstractEndpointTest{
	
	@Autowired
	private WebApplicationContext wac;
    private MockMvc mockMvc;
    
	@Autowired
	private TransactionsContainer container;
	
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        container.clear();
    }
    
	@Test
	public void contextLoads() {
		assertNotNull(wac);
	}
	
    @Test
    public void test1_TransactionStatisticsReturnEmptyResult() throws Exception{
    	this.mockMvc.perform(get("/statistics")
    		.accept(MediaType.APPLICATION_JSON))
    		.andExpect(status().isOk())
    		.andDo(print())
    		.andExpect(jsonPath("$.count", is(0)));                   
    }
    
    @Test
    public void test2_TransactionStatisticsReturnNotEmptyResult() throws Exception{
    	
    	 String jsonTask = createTransactionJson(Instant.now().toEpochMilli() - 1000);
         
         this.mockMvc.perform(post("/transactions")
        		 .contentType(MediaType.APPLICATION_JSON)
                 .content(jsonTask));
    	
    	this.mockMvc.perform(get("/statistics")
    		.accept(MediaType.APPLICATION_JSON))
    		.andExpect(status().isOk())
    		.andDo(print())
    		.andExpect(jsonPath("$.count", is(1)));                   
    }

}
