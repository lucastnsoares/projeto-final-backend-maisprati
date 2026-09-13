package br.com.maisprati.projeto.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestControllerTest {

    private TestController testController;

    @BeforeEach
    void setUp() {
        testController = new TestController();
    }

    @Test
    void shouldReturnPublicRouteMessage() {
        assertNotNull(testController.publicRoute());
    }

    @Test
    void shouldReturnAuthenticatedRouteMessage() {
        assertNotNull(testController.authenticatedRoute());
    }

    @Test
    void shouldReturnAdminRouteMessage() {
        assertNotNull(testController.onlyAdminRoute());
    }
}