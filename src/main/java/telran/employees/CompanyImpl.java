package telran.employees;

import java.util.*;

public class CompanyImpl implements Company{
   private TreeMap<Long, Employee> employees = new TreeMap<>();
   private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
   private TreeMap<Float, List<Manager>> managersFactor = new TreeMap<>();

    
   private class CompanyIterator implements Iterator<Employee> {
    private Iterator<Employee> iterator = employees.values().iterator();
    private Employee prev = null;

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Employee next() {
        return prev = iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
        removeDepartment(prev);
        removeManager(prev);
    }
}
   
   
   @Override
    public Iterator<Employee> iterator() {
        return new CompanyIterator();
    }

    @Override
    public void addEmployee(Employee empl) {
        if (employees.containsKey(empl.getId())) {
            throw new IllegalStateException();
        }
        employees.put(empl.getId(), empl);
        String department = empl.getDepartment();
        employeesDepartment.computeIfAbsent(department, k -> new ArrayList<>()).add(empl);
        if (empl instanceof Manager manager) {
            managersFactor.computeIfAbsent(manager.getFactor(), k -> new LinkedList<>()).add(manager);
        }
    }

    @Override
    public Employee getEmployee(long id) {
        return employees.get(id);
    }

    @Override
    public Employee removeEmployee(long id) {
        Employee removed = employees.remove(id);
        if (removed == null) {
            throw new NoSuchElementException("No employee with ID " + id);
        }
        removeDepartment(removed);
        if (removed instanceof Manager) {
            removeManager(removed);
        }
        return removed;
    }

    private void removeDepartment(Employee empl) {
        String department = empl.getDepartment();
        if (department != null) {
            List<Employee> employees = employeesDepartment.get(department);
            employees.remove(empl);
    
            if (employees.isEmpty()) {
                employeesDepartment.remove(department);
            }
        }
    }

    private void removeManager(Employee empl) {
        if (empl instanceof Manager manager) {
            Float factor = manager.getFactor();
            List<Manager> managers = managersFactor.get(factor);
            managers.remove(manager);

            if (managers.isEmpty()) {
                managersFactor.remove(factor);
            }
        }
    }




    @Override
    public int getDepartmentBudget(String department) {
        int sum = 0;
        List<Employee> employees = employeesDepartment.get(department);

        if (employees != null) {
            sum = employees.stream().mapToInt(i -> i.computeSalary()).sum();
        }
        return sum;
    }

    @Override
    public String[] getDepartments() {
        return employeesDepartment.keySet().stream().sorted().toArray(String[]::new);
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        Manager[] res = new Manager[0];
        if (!managersFactor.isEmpty()) {
            res = managersFactor.lastEntry().getValue().toArray(Manager[]::new);
        }
        return res;
    }

}