//package router;
//
//import com.carrental.cloudgateway.handler.SwaggerInvalidationHandler;
//import com.carrental.cloudgateway.router.SwaggerInvalidationRouter;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithAnonymousUser;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = SwaggerInvalidationRouter.class)
//@WebFluxTest
//class SwaggerInvalidationRouterTest {
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @MockBean
//    private SwaggerInvalidationHandler swaggerInvalidationHandler;
//
//    @Test
//    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
//    void swaggerInvalidationRouteTest_success() {
//        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();
//
//        when(swaggerInvalidationHandler.invalidateSwaggerFolderCache(any(ServerRequest.class))).thenReturn(serverResponse);
//
//        Flux<Void> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
//                .delete()
//                .uri("/invalidate-swagger-cache")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .returnResult(Void.class)
//                .getResponseBody();
//
//        StepVerifier.create(responseBody)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    @WithAnonymousUser
//    void swaggerInvalidationRouteTest_unauthorized() {
//        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();
//
//        when(swaggerInvalidationHandler.invalidateSwaggerFolderCache(any(ServerRequest.class))).thenReturn(serverResponse);
//
//        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
//                .delete()
//                .uri("/invalidate-swagger-cache")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus()
//                .isUnauthorized();
//    }
//
//    @Test
//    @WithAnonymousUser
//    void swaggerInvalidationRouteTest_forbidden() {
//        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();
//
//        when(swaggerInvalidationHandler.invalidateSwaggerFolderCache(any(ServerRequest.class))).thenReturn(serverResponse);
//
//        webTestClient.delete()
//                .uri("/invalidate-swagger-cache")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus()
//                .isForbidden();
//    }
//
//}
