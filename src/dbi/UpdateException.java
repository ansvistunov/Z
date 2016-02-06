package dbi;
import java.sql.*;
/**
*ьросается при проблемах с Update
*/
public class UpdateException extends SQLException{
	/**
	* ключ строки в которой произошла ошибка
	*/
	int badRow=-1;
	/**
	* обьект, в котором произошла ошибка
	*/
	int ds=-1;
	
	UpdateException(String msg,int br){
		super(msg);
		this.badRow = br;
	}
	UpdateException(String msg,int br,int DS){
		super(msg);
		this.badRow = br;
		this.ds = DS;
	}
	UpdateException(SQLException e,int br,int DS){
	    super(e.getMessage(),e.getSQLState(),e.getErrorCode());
	    this.badRow = br;
		this.ds = DS;
	    //this.error = e;
	    }
	 UpdateException(SQLException e,int br){
	    super(e.getMessage(),e.getSQLState(),e.getErrorCode());
	    this.badRow = br;
		
	    //this.error = e;
	    }
	/**
	* получить ключ строки в которой произошла ошибка
	*/
	public int getBadKey(){
		return badRow;
	}
	/**
	* получить номер обьекта, в котором произошла ошибка
	*/
	public int getBadDATASTORE(){
		return ds;
	} 

}
