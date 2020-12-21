import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBService(){

    }

    public static EmployeePayrollDBService getInstance(){
        if(employeePayrollDBService==null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/employeepayroll?useSSl=false";
        String userName = "root";
        String password = "Sai@mysql";
        Connection connection;
        System.out.println("connecting to the database:" + jdbcURL);
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("connection is successful!!!!" + connection);
        return connection;
    }


    public List<EmployeePayrollData> readData() throws PayrollServiceException {
        String sql = "select * from employee_payroll";
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                Double salary = result.getDouble("salary");
                LocalDate startDate = result.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
            connection.close();
        } catch (SQLException e) {
            throw new PayrollServiceException(e.getMessage(), PayrollServiceException.ExceptionType.RETRIEVAL_PROBLEM);
        }
        return employeePayrollList;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if(this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try {
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                Double salary = result.getDouble("salary");
                LocalDate startDate = result.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
                return employeePayrollList;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void prepareStatementForEmployeeData() {
        try {
            Connection connection = this.getConnection();
            String sql = "select * from employee_payroll where name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDataUsingStatement(name, salary);
    }

    private int updateEmployeeDataUsingStatement(String name, double salary) {
        String sql = String.format("update employee_payroll set salary = %.2f where name = '%s';", salary, name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;

    }


    public List<EmployeePayrollData> getEmployeeForDateRange(LocalDate startDate, LocalDate endDate) throws PayrollServiceException {
        String sql = String.format("select * from employee_payroll where start between '%s' and '%s';",
                Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) throws PayrollServiceException {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try (Connection connection = this.getConnection();) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                Double salary = result.getDouble("salary");
                LocalDate startDate = result.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        } catch (SQLException e) {
            throw new PayrollServiceException(e.getMessage(), PayrollServiceException.ExceptionType.RETRIEVAL_PROBLEM);
        }
        return employeePayrollList;
    }

    public Map<String, Double> getAverageSalaryByGender() {
        String sql = "select gender,avg(salary) as avg_salary from employee_payroll group by gender";
        return getAggregateByGender("gender","avg_salary",sql);
    }

    private Map<String, Double> getAggregateByGender(String gender, String aggregate, String sql) {
        Map<String, Double> genderCountMap = new HashMap<>();
        try(Connection connection = this.getConnection();){
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                String getgender = result.getString(gender);
                Double count = result.getDouble(aggregate);
                genderCountMap.put(getgender, count);
            }
        }catch (SQLException e) {
            e.getMessage();
        }
        return genderCountMap;
    }

    public Map<String, Double> getCountByGender() {
        String sql = "select gender,count(salary) as count_gender from employee_payroll group by gender";
        return getAggregateByGender("gender","count_gender",sql);
    }

    public Map<String, Double> getMinimumByGender() {
        String sql = "select gender,min(salary) as minSalary_gender from employee_payroll group by gender";
        return getAggregateByGender("gender","minSalary_gender",sql);
    }

    public Map<String, Double> getMaximumByGender() {
        String sql = "select gender,max(salary) as maxSalary_gender from employee_payroll group by gender";
        return getAggregateByGender("gender","maxSalary_gender",sql);
    }

    public Map<String, Double> getSumOfSalaryByGender() {
        String sql = "select gender,sum(salary) as SumOfSalary_gender from employee_payroll group by gender";
        return getAggregateByGender("gender","SumOfSalary_gender",sql);
    }

    public EmployeePayrollData addEmployeeToPayroll(int i, String name, double salary, LocalDate startDate, String gender) throws PayrollServiceException {
       // int employeeId = -1;
        Connection connection = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new PayrollServiceException(e.getMessage(),
                    PayrollServiceException.ExceptionType.CONNECTION_PROBLEM);
        }
        EmployeePayrollData employeePayrollData = null;
        String sql = String.format("insert into employee_payroll (id,name,gender,salary,start)"+
                "values ('%s','%s', '%s', '%s', '%s')", i,name,gender,salary,Date.valueOf(startDate));
        try (Statement statement = connection.createStatement()){

            int rowAffected = statement.executeUpdate(sql);
            /*
            if(rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next())
                    employeeId = resultSet.getInt(1);
            }
            */
            employeePayrollData = new EmployeePayrollData(i, name, salary, startDate);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            throw new PayrollServiceException(e.getMessage(), PayrollServiceException.ExceptionType.INSERTION_PROBLEM);
        }

        try (Statement statement = connection.createStatement()) {
            double deductions = salary * 0.2;
            double taxablePay = salary - deductions;
            double tax = taxablePay * 0.1;
            double netPay = salary - tax;
            String sql1 = String.format(
                    "insert into payroll_details (employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) values "
                            + "('%s', '%s', '%s', '%s', '%s', '%s')",
                    i, salary, deductions, taxablePay, tax, netPay);
            int rowAffected = statement.executeUpdate(sql1);
            if (rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(i, name, salary, startDate);
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            throw new PayrollServiceException(e.getMessage(), PayrollServiceException.ExceptionType.INSERTION_PROBLEM);
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new PayrollServiceException(e.getMessage(),
                            PayrollServiceException.ExceptionType.CONNECTION_PROBLEM);
                }
        }
        return employeePayrollData;


    }

}
