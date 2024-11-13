package br.eng.jonnes.jpa_large_data_handling.service;

import br.eng.jonnes.jpa_large_data_handling.entity.Employee;
import br.eng.jonnes.jpa_large_data_handling.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final int CHUNK_SIZE = 1000;

    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;

    public static <T> Consumer<T> withCounter(BiConsumer<Integer, T> biConsumer) {
        AtomicInteger counter = new AtomicInteger(0);
        return returnConsumer -> biConsumer.accept(counter.getAndIncrement(), returnConsumer);
    }

    @Transactional(readOnly = true, timeout = 1000) // !IMPORTANT
    public void processLargeChunkOfEmployees(String department) {

        try (Stream<Employee> employees = employeeRepository.findByDepartment(department)) {

            employees.forEach(withCounter((chunkCounter, employee) -> {

                System.out.println("Name: " + employee.getName());

                if (chunkCounter == CHUNK_SIZE) { // !IMPORTANT
                    entityManager.clear();
                }
            }));
        }
    }
}
