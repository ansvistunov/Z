
/*
 * File: GlobalValuesObject.java
 *
 * Created: Mon Apr 26 13:05:28 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen
 */

package calc;

public interface GlobalValuesObject{
    public void setValue(Object obj) throws Exception;
    public Object getValue() throws Exception;
    public void setValueByName(String name, Object obj) throws Exception ;
    public Object getValueByName(String name) throws Exception;
}
