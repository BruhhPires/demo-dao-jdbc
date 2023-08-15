package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Statement;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{
	
	private Connection conn; // MANTER A CONEXÃO ABERTA PARA UTILIZAÇÃO DOS METODOS
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void inser(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+"(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+"VALUES "
					+"(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
				
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected >0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! no rows affected! ");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void uptdate(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
					
			st.setInt(1, id);
			rs = st.executeQuery();
			if(rs.next()) { // SE O NEXT NÃO FOR NULO, SERÁ CRIADO O OBJETO
				Department dep = instantiateDepartment(rs);
				Seller obj = instantiateSeller(rs, dep);
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			// A CONEXÃO DEVERÁ SER MANTIDA ABERTA
		}

	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getNString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep); // DEVE SER INCLUIDO O DEPARTMENT POIS ESSE OBJETO ESTÁ SETADO PELO SELLER
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentID"));
		dep.setName(rs.getNString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
			PreparedStatement st = null;
			ResultSet rs = null;
			try {
				st = conn.prepareStatement(
						"SELECT seller.*,department.Name as DepName "
						+ "FROM seller INNER JOIN department "
						+ "ON seller.DepartmentId = department.Id "
						+ "ORDER BY Name ");
						
				rs = st.executeQuery();
				
				List<Seller> list = new ArrayList<>();          // DEVE INSTANCIAR UMA LIST POIS É UMA LISTA DE DEPARTAMENTOS 
				Map<Integer, Department> map = new HashMap<>(); // CRIA UM MAP POIS ELE NÃO ACEITA VALOR DUPLICADO
				
				while (rs.next()) { 			                // ENQUANTO O NEXT NÃO FOR NULO, SERÁ CRIADO O OBJETO
					Department dep = map.get(rs.getInt("DepartmentId")); // O DEPARTMENT RECEBERA O VALOR QUE MAP VAI BUSCAR
					
					if(dep == null) {				            // SE O DEP ESTIVER NULO AI SIM SERÁ INSTANCIADO O DEPARTAMENTO SE JÁ EXISTE SERÁ USADO ELE MESMO
						dep = instantiateDepartment(rs);		// O DEP SERÁINSTANCIADO COM O VALOR DO RS, CASO O MAP NÃO FOR NULO
						map.put(rs.getInt("DepartmentId"), dep); // O MAP PEGA O VALOR DO RS.INT E NA PROXIMA NÃO DARÁ MAIS NULO
					}
			
					Seller obj = instantiateSeller(rs, dep);
					list.add(obj);

				}
				return list;
			}
			catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
			finally {
				DB.closeStatement(st);
				DB.closeResultSet(rs);
				// A CONEXÃO DEVERÁ SER MANTIDA ABERTA
			}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name ");
					
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();          // DEVE INSTANCIAR UMA LIST POIS É UMA LISTA DE DEPARTAMENTOS 
			Map<Integer, Department> map = new HashMap<>(); // CRIA UM MAP POIS ELE NÃO ACEITA VALOR DUPLICADO
			
			while (rs.next()) { 			                // ENQUANTO O NEXT NÃO FOR NULO, SERÁ CRIADO O OBJETO
				Department dep = map.get(rs.getInt("DepartmentId")); // O DEPARTMENT RECEBERA O VALOR QUE MAP VAI BUSCAR
				
				if(dep == null) {				            // SE O DEP ESTIVER NULO AI SIM SERÁ INSTANCIADO O DEPARTAMENTO SE JÁ EXISTE SERÁ USADO ELE MESMO
					dep = instantiateDepartment(rs);		// O DEP SERÁINSTANCIADO COM O VALOR DO RS, CASO O MAP NÃO FOR NULO
					map.put(rs.getInt("DepartmentId"), dep); // O MAP PEGA O VALOR DO RS.INT E NA PROXIMA NÃO DARÁ MAIS NULO
				}
		
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);

			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			// A CONEXÃO DEVERÁ SER MANTIDA ABERTA
		}
	}

}
