//package com.swiftwheelshub.agency.repository;
//
//import com.swiftwheelshub.entity.Branch;
//import org.junit.ClassRule;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.context.annotation.Profile;
//import org.testcontainers.consul.ConsulContainer;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Profile("test")
//@Testcontainers
//@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class BranchRepositoryTest {
//
//    @Container
//    @ServiceConnection
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
//
//    @ClassRule
//    public static ConsulContainer consulContainer = new ConsulContainer("hashicorp/consul:latest")
//            .withConsulCommand("kv put config/testing1 value123");
//
//    @Autowired
//    private BranchRepository branchRepository;
//
//    @Test
//    void checkIfConnectionEstablished() {
//        assertTrue(postgres.isCreated());
//    }
//
//    @Test
//    void findByFilterTest_success() {
//        List<Branch> branches = branchRepository.findByFilter("Branch");
//        assertEquals(2, branches.size());
//    }
//
//}
