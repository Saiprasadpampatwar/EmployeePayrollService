import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import io.restassured.http.ContentType;

import java.time.LocalDate;
import java.util.Arrays;

import static io.restassured.RestAssured.given;

public class RestAssureTest {
    private int empId;
    static EmployeePayrollService employeePayrollService;

    @Before
    public void setUp(){
        RestAssured.baseURI="http://localhost";
        RestAssured.port=3000;
    }

    @BeforeClass
    public static void initializeConstructor() {
        employeePayrollService = new EmployeePayrollService();
    }


    public EmployeePayrollData[] getEmployeeList(){
        Response response = RestAssured.get("/EmployeePayrollData");
        System.out.println("Employee Payroll entires in JSON Server: \n"+response.asString());
        EmployeePayrollData[] arrayOfEmps = new Gson().fromJson(response.asString(),EmployeePayrollData[].class);
        return arrayOfEmps;
    }

    @Test
    public void givenEmployeeDataInJSONServer_whenRetrieved_shouldMatchTheCount(){
        EmployeePayrollData[] arrayOfEmps = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assert.assertEquals(9,entries);
    }

    @Test
    public void givenNeEmployeeWhenAddedShouldMatch201ResponseAndCount() throws PayrollServiceException {
        EmployeePayrollData[] arrayOfEmps = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        EmployeePayrollData employeePayrollData =null;
        employeePayrollData = new EmployeePayrollData(0, "Mukesh Ambani", 8000000.00, LocalDate.now(),'M');
        Response response = addEmployeeToJsonServer(employeePayrollData);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(201, statusCode);
        employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
        employeePayrollService.addEmployeeToPayroll(employeePayrollData, EmployeePayrollService.IOService.REST_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        System.out.println("Employee Payroll entires in JSON Server: \n"+response.asString());
        Assert.assertEquals(3,entries);
    }

    private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
        String employeeJson = new Gson().toJson(employeePayrollData);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(employeeJson);
        return request.post("/employees");
    }
}
