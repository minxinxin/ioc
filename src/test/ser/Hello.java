package test.ser;

import test.dao.IDao;



class I implements IHello {

	IDao dao;
	
	
	public IDao getDao() {
		return dao;
	}

	
	public void setDao(IDao dao) {
		this.dao = dao;
	}

	public void say() {
		System.out.println("qq");
		
	}
	
	
}
public class Hello extends I{


	public void say() {
		System.out.println(dao.getString());
		
	}


}
