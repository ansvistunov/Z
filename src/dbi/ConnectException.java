package dbi;
import java.sql.*;
public class ConnectException extends SQLException{
	ConnectException(String msg){
		super(msg);
	}

}
