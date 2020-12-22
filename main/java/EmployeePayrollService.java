import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {

    public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}

    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBServiceERD employeePayrollDBServiceERD;


    public EmployeePayrollService(){
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
        employeePayrollDBServiceERD = EmployeePayrollDBServiceERD.getInstance();
    }

    /* Welcome Message */
    public void printWelcomeMessage() {
        System.out.println("Welcome to the Employee PayRoll Service Program");
    }


    private  static List<EmployeePayrollData> employeePayrollList;

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
        this();
        this.employeePayrollList = employeePayrollList;
    }


    public static void main(String[] args) {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader = new Scanner(System.in);
        employeePayrollService.readEmployeePayrollData(consoleInputReader);
        employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
    }

    /* Read Employee Payroll data from console */
    public void readEmployeePayrollData(Scanner consoleInputReader) {
        System.out.println("Enter Employee ID: ");
        int id = consoleInputReader.nextInt();
        System.out.println("Enter Employee Name ");
        String name = consoleInputReader.next();
        System.out.println("Enter Employee Salary ");
        double salary = consoleInputReader.nextDouble();
        employeePayrollList.add(new EmployeePayrollData(id, name, salary));
    }

    /*read data from database*/
    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) throws PayrollServiceException {
        if(ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return employeePayrollList;
    }

    public int readEmployeePayrollData(IOService ioService, String employee_payroll) throws PayrollServiceException {
        int noOfEntries = 0;
        if(ioService.equals(IOService.DB_IO))
            noOfEntries =  employeePayrollDBService.readData(employee_payroll);
        return noOfEntries;
    }


    public void upDateEmployeeSalary(String name, double salary) throws PayrollServiceException {
        int result = employeePayrollDBService.updateEmployeeData(name,salary);
        if(result==0)return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if(employeePayrollData != null)
            employeePayrollData.salary = salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        return employeePayrollList.stream()
                .filter(employeePayrollDataItem ->employeePayrollDataItem.name.equals(name) )
                .findFirst()
                .orElse(null);
    }

    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        employeePayrollList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollList.get(0).equals(getEmployeePayrollData(name));
    }


    public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) throws PayrollServiceException {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeeForDateRange(startDate, endDate);
        return null;
    }

    public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }

    public Map<String, Double> readNetSalaryByGender(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getNetSalaryByGender();
        return null;
    }

    public Map<String, Double> readCountByGender(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getCountByGender();
        return null;
    }

    public Map<String, Double> readMinumumSalaryByGender(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getMinimumByGender();
        return null;
    }


    public Map<String, Double> readMaximumSalaryByGender(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getMaximumByGender();
        return null;
    }


    public Map<String, Double> readSumSalaryByGender(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getSumOfSalaryByGender();
        return null;
    }


    public void addEmployeeToPayroll(int i, String name, double salary, LocalDate startDate, char gender) throws PayrollServiceException {
        employeePayrollList.add(employeePayrollDBServiceERD.addEmployeeToPayroll(i,name, salary, startDate, gender));
    }


    public void addEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData -> {
            System.out.println("Employee Being Added: "+employeePayrollData.name);
            try {
                this.addEmployeeToPayroll(employeePayrollData.id,employeePayrollData.name, employeePayrollData.salary,employeePayrollData.start,employeePayrollData.gender);
                System.out.println("Employee Added:"+employeePayrollData.name);
            } catch (PayrollServiceException e) {
                e.printStackTrace();
            }
            System.out.println(this.employeePayrollList);
        });
    }


    public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            Runnable task = () -> {
                employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
                System.out.println("Employee Being Added: " + Thread.currentThread().getName());
                try {
                    this.addEmployeeToPayroll(employeePayrollData.id,employeePayrollData.name, employeePayrollData.salary,
                            employeePayrollData.start, employeePayrollData.gender);
                } catch (PayrollServiceException e) {
                    e.printStackTrace();
                }
                employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
                System.out.println("Employee Added " + Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();
        });
        while(employeeAdditionStatus.containsValue(false)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(employeePayrollDataList);
    }


    public void removeEmployeeFromDB(String employee_name) throws PayrollServiceException {
        employeePayrollDBService.removeEmployeeFromDB(employee_name);
    }


    public int getNoOfActiveEmployee() throws PayrollServiceException {
        return employeePayrollDBService.getNoOfActiveEmployee();
    }

    /* Write Employee Payroll data to console */
    public void writeEmployeePayrollData(IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO))
            System.out.println("\nWriting Employee Payroll Roaster to Console\n" + employeePayrollList);
        else if(ioService.equals(IOService.FILE_IO)) {
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
        }
    }

    /* Print Employee Payroll */
    public void printData(IOService fileIo) {
        if(fileIo.equals(IOService.FILE_IO)) {
            new EmployeePayrollFileIOService().printData();
        }

    }

    public long countEntries(IOService fileIo) {
        if(fileIo.equals(IOService.FILE_IO)) {
            return new EmployeePayrollFileIOService().countEntries();
        }else if(fileIo.equals(IOService.DB_IO)){
            return employeePayrollList.size();
        }
        return 0;
    }

    public List<EmployeePayrollData> readPayrollData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            this.employeePayrollList = new EmployeePayrollFileIOService().readData();
        return employeePayrollList;
    }
}
