package br.eng.jonnes.jpa_large_data_handling.repository;

import br.eng.jonnes.jpa_large_data_handling.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.department = :department")
    Stream<Employee> findByDepartment(@Param("department") String department);

}
