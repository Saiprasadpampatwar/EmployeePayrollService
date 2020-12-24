import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import io.restassured.http.ContentType;

import java.util.Arrays;

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


}
