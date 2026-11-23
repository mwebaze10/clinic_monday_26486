package com.clinic.api.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import com.clinic.api.model.Office;
import com.clinic.api.service.OfficeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class OfficeControllerTest {

    private OfficeService officeService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        officeService = mock(OfficeService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new OfficeController(officeService)).build();
    }

    @Test
    void getOfficeByIdReturnsNotFoundWhenOfficeDoesNotExist() throws Exception {
        when(officeService.findById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/offices/42"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOfficeReturnsNotFoundWhenOfficeDoesNotExist() throws Exception {
        when(officeService.update(eq(42L), any(Office.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/offices/42")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"North Wing\",\"officeNumber\":2}"))
                .andExpect(status().isNotFound());
    }
}
