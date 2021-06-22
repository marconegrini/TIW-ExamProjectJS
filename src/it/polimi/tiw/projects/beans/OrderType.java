package it.polimi.tiw.projects.beans;

import java.util.HashMap;

import it.polimi.tiw.projects.enumerations.Order;

public class OrderType {
	
	private HashMap<String, Order> currOrder = new HashMap<String, Order>();
	
	public OrderType() {
		currOrder.put("studentId", Order.ASC);
		currOrder.put("surname", Order.ASC);
		currOrder.put("name", Order.ASC);
		currOrder.put("email", Order.ASC);
		currOrder.put("corsoDiLaurea", Order.ASC);
		currOrder.put("grade", Order.ASC);
		currOrder.put("status", Order.ASC);
	}
	
	public Order getOrder(String column) throws IllegalArgumentException{
		if(column.equals("studentId") || column.equals("surname") || column.equals("name") || column.equals("email") || column.equals("corsoDiLaurea") || column.equals("grade") || column.equals("status"))
			return currOrder.get(column);
		else throw new IllegalArgumentException();
	}
	
	public void updateOrder(String column) throws IllegalArgumentException{
		if(column.equals("studentId") || column.equals("surname") || column.equals("name") || column.equals("email") || column.equals("corsoDiLaurea") || column.equals("grade") || column.equals("status")) {
			if(currOrder.get(column).equals(Order.ASC))
				currOrder.put(column, Order.DESC);
				else currOrder.put(column, Order.ASC);
		} else throw new IllegalArgumentException();
	}
}
