import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class EmployeePayrollServiceTest {
    static EmployeePayrollService employeePayrollService;

    @BeforeClass
    public static void initializeConstructor()
    {
        employeePayrollService = new EmployeePayrollService();
    }

    @Test
    public void CheckingClassCreatedAndPrintingWelComeMassage() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.printWelcomeMessage();
    }

    @Test
    public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
                new EmployeePayrollData(2, "Bill Gates", 200000.0),
                new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0)
        };
        employeePayrollService = new EmployeePayrollService(asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3, entries);

    }

    @Test
    public void givenFileOnReadingFileShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> entries = employeePayrollService.readPayrollData(EmployeePayrollService.IOService.FILE_IO);
    }

    @Test
    public void givenEmployeePayrollinDB_whenRetrieved_ShouldMatch_Employee_Count() throws PayrollServiceException {
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(5, employeePayrollData.size());
    }

    @Test
    public void givenNewSalary_ForEmployee_ShouldSinc_withDtabase() throws PayrollServiceException {
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.upDateEmployeeSalary("Mark",2000000.0);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
        Assert.assertTrue(result);
    }

    @Test
    public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() throws PayrollServiceException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        LocalDate startDate = LocalDate.of(2018, 01, 01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService
                .readEmployeePayrollForDateRange(EmployeePayrollService.IOService.DB_IO, startDate, endDate);
        Assert.assertEquals(5, employeePayrollData.size());
    }

    @Test
    public void givenPayrollData_whenAverageSalaryRetrievedByGender_shouldReturnProperValue()
            throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(averageSalaryByGender.get("M").equals(3666666.6666666665));
        Assert.assertTrue(averageSalaryByGender.get("F").equals(1500000.00) );
    }

    @Test
    public void givenPayrollData_whenAverageSalaryRetrievedByGender_shouldReturnProperCountValue()
            throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> countByGender = employeePayrollService.readCountByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(countByGender.get("M").equals(3.0) && countByGender.get("F").equals(2.0));
    }

    @Test
    public void givenPayrollData_whenAverageSalaryRetrievedByGender_shouldReturnProperMinimumValue()
            throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> countByGender = employeePayrollService.readMinumumSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(countByGender.get("M").equals(2000000.00) && countByGender.get("F").equals(1000000.00));
    }

    @Test
    public void givenPayrollData_whenAverageSalaryRetrievedByGender_shouldReturnProperMaximumValue()
            throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> countByGender = employeePayrollService.readMaximumSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(countByGender.get("M").equals(6000000.00) && countByGender.get("F").equals(2000000.00));
    }

    @Test
    public void givenPayrollData_whenAverageSalaryRetrievedByGender_shouldReturnProperSumValue()
            throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> sumSalaryByGender = employeePayrollService.readSumSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(sumSalaryByGender.get("M").equals(11000000.00) && sumSalaryByGender.get("F").equals(3000000.00));
    }
    
    @Test
    public void givenNewEmployee_whenAddedShouldSyncWithTheDatabase() throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployeeToPayroll(7,"Ansh", 5000000.00, LocalDate.now(), 'M');
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Ansh");
        Assert.assertTrue(result);
    }

    @Test
    public void retriveEntriesFromDifferentTables() throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        int noOfEntries = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO,"employee_payroll");
        Assert.assertEquals(6, noOfEntries);
        int noOfEntries1 = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO,"payroll_details");
        Assert.assertEquals(6, noOfEntries1);
    }

    @Test
    public void givenPayrollData_whenNet_PayRetrievedByGender_shouldReturnProperValue()
            throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> averageSalaryByGender = employeePayrollService.readNetSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(averageSalaryByGender.get("M").equals(3450000.00));
        Assert.assertTrue(averageSalaryByGender.get("F").equals(1380000.00) );
    }

    @Test
    public void givenNewEmployee_whenAddedShouldSyncWithTheDatabaseAccordingToER() throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployeeToPayroll(6,"Sai", 4000000.00, LocalDate.now(), 'M');
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Sai");
        Assert.assertTrue(result);
    }

    @Test
    public void whenEmployeeRemovedShouldReturn_TrueRecordsOfEmployee() throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.removeEmployeeFromDB("Mark");
        int noOfEmployee = employeePayrollService.getNoOfActiveEmployee();
        Assert.assertEquals(6,noOfEmployee);
    }

    @Test
    public void given6Employees_whenAddedToDB_shouldMatchEmployeeEntries() throws PayrollServiceException {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(8, "Bill Gates", 500000.0, LocalDate.now(), 'M'),
                new EmployeePayrollData(9, "Mark Zuckerberg", 400000.0, LocalDate.now(), 'M'),
                new EmployeePayrollData(10, "Sunder Pichai", 300000.0, LocalDate.now(), 'M'),
                new EmployeePayrollData(11, "Mukesh Ambani", 200000.0, LocalDate.now(), 'M'),
                new EmployeePayrollData(12, "Anil Ambani", 100000.0, LocalDate.now(), 'M') };
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without Thread; " + Duration.between(start, end));
        Instant threadStart = Instant.now();
        employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmps));
        Instant threadEnd = Instant.now();
        System.out.println("Duration with Thread; " + Duration.between(threadStart, threadEnd));
        Assert.assertEquals(13, employeePayrollService.countEntries(EmployeePayrollService.IOService.DB_IO));
    }

    @Test
    public void givenNewEmployee_whenAddedShouldSyncWithTheDatabaseAccordingToERWithThreads() throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployeeToPayroll(13,"Sainath", 5000000.00, LocalDate.now(), 'M');
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Sainath");
        Assert.assertTrue(result);
    }

    @Test
    public void updateSalaryOfMultipleEmployeeShouldMatchWithValueInDB() throws PayrollServiceException {
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String,Double> myMap = new HashMap<>();
        myMap.put("Anjali",3000000.00);
        myMap.put("Sai",6000000.00);
        employeePayrollService.updateMultipleEmployeeSalaryWithThreads(myMap);
        Assert.assertEquals(14, employeePayrollService.countEntries(EmployeePayrollService.IOService.DB_IO));
    }
}
