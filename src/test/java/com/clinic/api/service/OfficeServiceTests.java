package com.clinic.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import com.clinic.api.repository.OfficeRepository;

class OfficeServiceTests {
    @Test
    void returnsMessageWhenThereAreNoAppointments() {
        OfficeRepository officeRepository = mock(OfficeRepository.class);
        when(officeRepository.findOfficesByAppointmentCount(PageRequest.of(0, 1)))
                .thenReturn(List.of());
        OfficeService officeService = new OfficeService(officeRepository);

        assertEquals("No appointments yet", officeService.findBusiestOffice());
        verify(officeRepository).findOfficesByAppointmentCount(PageRequest.of(0, 1));
    }
}
