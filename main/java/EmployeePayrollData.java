import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
    public int id;
    public String name;
    public double salary;
    public LocalDate start;
    private String[] dept_name;
    public char gender;

    public EmployeePayrollData(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }
    public EmployeePayrollData(int id, String name, double salary, LocalDate start) {
        this(id,name,salary);
        this.start = start;
    }

    public EmployeePayrollData(int id, String name, double salary, LocalDate startDate, char gender, String[] dept_name) {
        this(id, name, salary, startDate);
        this.gender = gender;
        this.dept_name = dept_name;
    }

    public EmployeePayrollData(int id, String name, double salary, LocalDate startDate, char gender) {
        this(id, name, salary, startDate);
        this.gender = gender;
    }


    @Override
    public String toString() {
        return "EmployeePayrollData [id=" + id + ", name=" + name + ", salary=" + salary + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id &&
                Double.compare(that.salary, salary) == 0 &&
                Objects.equals(name, that.name) &&
                Objects.equals(start, that.start);
    }

}
