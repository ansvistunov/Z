
/*
 * File: class_field.java
 *
 * Created: Wed May 12 08:10:43 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */


package calc.objects;

public interface class_field {
	public Object field(String field) throws Exception;
	public Object set_field(String field,Object value) throws Exception;
}
