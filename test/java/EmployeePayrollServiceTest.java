import org.junit.Test;

public class EmployeePayrollServiceTest {

    @Test
    public void CheckingClassCreatedAndPrintingWelComeMassage() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.printWelcomeMessage();
    }
}
