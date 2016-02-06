
/*
 * File: Tree.java
 *
 * Created: Fri Apr 23 09:30:05 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package calc;

/**
 * Класс для построения дервьев разбора
 */
public class Tree{
	public Tree(){
	}
	public Tree(Object left,Object right){
		this.left = left;
		this.right = right;
	}
	public Object left = null;
	public Object right = null;
}

