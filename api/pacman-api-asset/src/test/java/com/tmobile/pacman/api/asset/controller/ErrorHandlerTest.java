package com.tmobile.pacman.api.asset.controller;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class })
public class ErrorHandlerTest {

    ErrorHandler handler = new ErrorHandler();

    @Test
    public void testprocessValidationError() throws Exception {
        handler.processValidationError(new IllegalArgumentException());

        ResponseEntity<Object> response = handler.processValidationError(new Exception());
        assertTrue(response.getStatusCode() == HttpStatus.EXPECTATION_FAILED);

    }

    
}
