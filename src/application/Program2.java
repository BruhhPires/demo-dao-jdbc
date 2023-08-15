package application;

import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);

		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();  
		System.out.println("=== TEST 1: seller findByID ====");
		Department dep = departmentDao.findById(2);
		System.out.println(dep);
		
		System.out.println("\n=== TEST 3: Department findAll ====");
		List<Department> list = departmentDao.findAll();
		for(Department obj: list) {
			System.out.println(obj);
		}
		
		System.out.println("\n=== TEST 3: Department insert ====");
		Department newDep = new Department(null, "Bykes");
		departmentDao.inser(newDep);
		System.out.println("Departament Inserted!");
		
		System.out.println("\n=== TEST 4: Department update ====");
		Department dep2 = departmentDao.findById(5);
		dep2.setName("Cars");
		departmentDao.uptdate(dep2);
		System.out.println("Update Completed");

		
		System.out.println("\n=== TEST 5: seller delete ====");
		System.out.println("Enter id to delete test:");
		int id = sc.nextInt();
		departmentDao.deleteById(id);
		System.out.println("Delete completed");

	
		sc.close();
	}
	
}
