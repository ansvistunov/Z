
/*
 * File: GLOBALPROP.java
 *
 * Created: Wed Apr  7 14:01:09 1999
 *
 * Copyright (c) by Almanex Technologes
 *
 *
 * Author: Alexey Chen */

package loader;

import java.util.*;
import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import sun.io.*;

public class GLOBALPROP{
	public final static String EXCEPTION_LOADER = "exception.loader";
	public final static String DEBUG_LOADER = "debug.loader";
	public final static String DEBUG_COLOR = "debug.color";
	public final static String DEBUG_PROTOCOL = "debug.protocol";
	public final static String DEBUG_RML = "debug.rml";
	public final static String DEBUG_VIEWS = "debug.views";
	public final static String DEBUG_LISP = "debug.lisp";
	public final static String DEBUG_DSTORE = "debug.dstore";
	public final static String DEBUG_PARSER = "debug.parser";
	public final static String DEBUG_CALCULATOR = "debug.calculator";
	public final static String CLASS_SERVER = "class.server";
	public final static String DOC_SERVER = "doc.server";
	public final static String DOC_BTN_CANCEL = "doc.cancel";
	public final static String DOC_BTN_DOK = "doc.dok";
	public final static String DOC_BTN_HOK = "doc.hok";
	public final static String DOC_BTN_SAVE = "doc.save";
	public final static String DOC_BTN_NEW = "doc.new";
	public final static String AWT_LOCALE = "awt.locale";
	public final static String DOC_SOMTHING = "doc.somthing";
	public final static String LOGIN_USER = "login.user";
	public final static String LOGIN_PASSWORD = "login.password";
	public final static String LOGIN_OK = "login.ok";
	public final static String LOGIN_LUSER = "login.user.label";
	public final static String LOGIN_LPASSWORD = "login.password.label";
	public final static String LOGIN_AUTO = "login.auto";
	public final static String DOC_START = "doc.start";
	public final static String DBS_PROTO = "dbs.proto";
	public final static String DBS_HOST = "dbs.host";
	public final static String DBS_REALLYCONNECTED = "dbs.connect";
	public final static String GIFNAME = "zeta.gif";
	public final static String PROPNAME = "zeta.propers";
	public final static String PROP_ENCODING = "file.encoding";
	public final static String OUT_ENCODING = "out.encoding";
	public final static int TEXTBG = 0;
	public final static int TEXTFG = 1;
	public final static int BTNBG = 2;
	public final static int BTNFG = 3;
	public final static int FLDBG = 4;
	public final static int FLDFG = 5;
	public final static String MSG_RUSUREEXIT = "msg.RUSureExit";
	public final static String MSG_OKBUTTON = "msg.OkButtonLabel";
	public final static String MSG_CANCELBUTTON = "msg.CanscelButtonLabel";
        public final static String MSG_USERALREADYEXIST = "msg.UserAlreadyExist";
        public final static String MSG_BADUSERSCOUNT1 = "msg.BadUsersCount1";
        public final static String MSG_BADUSERSCOUNT2 = "msg.BadUsersCount2";
        public final static String MSG_BADLICENSE = "msg.BadLicense";
	public final static String MSG_BADUSERORPASSWORD = "msg.BadUserOrPassword";
        public final static String MSG_BLOCKED = "msg.Blocked";
	public final static String MSG_CANTCONNECTTODS =
	"msg.CantConnnectToDocumentServer";
	public final static String MSG_CANTCONNECTTODBS =
	"msg.CantConnectToDataBaseServer";
	public final static String MSG_CANTLOADCLASS = "msg.CantLoadClass";
	public final static String MSG_PARSERERROR = "msg.ParsingError";
	public final static String MSG_UNKNOWNERROR = "msg.UnknownFatalError";
	public final static String MSG_ERRORREADNODE = "msg.ErrorReadNode";
	public final static String MSG_ERRORLOADNODE = "msg.ErrorLoadNode";
	public final static String MSG_CANTLOADDOCUMENT = "msg.CantLoadDocument";
	public final static String MSG_BADDOCUMENT = "msg.BadDocument";
	public final static String CLR_DEFAULT = "color.msg.default";
	public final static String CLR_DOCUMENT = "color.document";
	public final static String CLR_MSG_INFO = "color.msg.info";
	public final static String CLR_MSG_ERROR = "color.msg.error";
	public final static String CLR_MSG_QUEST = "color.msg.quest";
	public final static String CLR_LOGIN = "color.login";
	public final static String CLR_WORKPLACE = "color.workplace";
	public final static String CLR_TREEVIEW = "color.treeview";
	public final static String TITLE_MAINWINDOW = "title.mainwindow";
	public final static String TITLE_MESSAG = "title.messag";
	public final static String TITLE_SURE = "title.sure";
	public final static String TITLE_WORKPLACE = "title.workplace";
	public final static String TITLE_HELPER = "title.helper";
	public final static String TITLE_LOGIN = "title.login";
	public final static String TITLE_ERROR = "title.error";
	public final static String TITLE_NAFIGATOR = "title.nafigator";
	public final static String FONT_DEFAULT = "font.default";
	public final static String FONT_LOGIN_BTN = "font.login.btn";
	public final static String FONT_LOGIN_FIELD = "font.login.field";
	public final static String FONT_LOGIN_TEXT = "font.login.text";
	public final static String FONT_MSG_TEXT = "font.msg.text";
	public final static String FONT_MSG_BTN = "font.msg.btn";
	public final static String FONT_MSG_FIELD = "font.msg.field";
	public final static String FONT_WORKPLACE_TEXT = "font.workplace.text";
	public final static String FONT_WORKPLACE_BTN = "font.workplace.btn";
	public final static String FONT_WORKPLACE_FIELD = "font.workplace.field";
	public final static String FONT_TREEVIEW_POINT = "font.treeview.point";
	public final static String FONT_TREEVIEW_NODE = "font.treeview.node";
	public final static String FONT_DOCUMENT = "font.document";
	public final static String HILITING_TREEVIEW = "hiliting.treeview";
	public final static String OFFSET_TREEVIEW = "offset.treeview";
	public final static String NAFIGATOR_HEADER = "nafigator.header";
	public final static String NAFIGATOR_WIDTH = "nafigator.width";
	public final static String NAFIGATOR_HEIGHT = "nafigator.height";
	public final static String NAFIGATOR_X = "nafigator.x";
	public final static String NAFIGATOR_Y = "nafigator.y";
	public final static String LOADING_STRING = "loading.label";
	public final static String LOADING_WINDOW = "loading.window";
	public final static String LOADING_FOREGROUND = "loading.foreground";
	public final static String LOADING_BACKGROUND = "loading.background";
	public final static String LOADING_FONT = "loading.font";
	public final static String NEED_EXTRAH = "extrah.need";
	public final static String DEFAULT_EXTRAH = "extrah.default";
	public final static String SYSTEM_OUT = "system.out";
	public final static String SYSTEM_OUT_SIZE = "system.out.lines";
	public final static String SYSTEM_OUT_WIDTH = "system.out.width";
	public final static String SYSTEM_OUT_HEIGHT = "system.out.height";
	public final static String SYSTEM_OUT_X = "system.out.x";
	public final static String SYSTEM_OUT_Y = "system.out.y";
	public final static String SYSTEM_OUT_TITLE = "system.out.title";
	public final static String SYSTEM_OUT_BACKGROUND = "system.out.background";
	public final static String SYSTEM_OUT_FOREGROUND = "system.out.foreground";
	public final static String SYSTEM_OUT_FONT = "system.out.font";
	public final static String SYSTEM_EDITOR_WIDTH = "system.editor.width";
	public final static String SYSTEM_EDITOR_HEIGHT = "system.editor.height";
	public final static String SYSTEM_EDITOR_X = "system.editor.x";
	public final static String SYSTEM_EDITOR_Y = "system.editor.y";
	public final static String SYSTEM_EDITOR_TITLE = "system.editor.title";
	public final static String SYSTEM_EDITOR_BACKGROUND = "system.editor.background";
	public final static String SYSTEM_EDITOR_FOREGROUND = "system.editor.foreground";
	public final static String SYSTEM_EDITOR_FONT = "system.editor.font";
	public final static String SYSTEM_EDITOR_BROWSER = "system.editor.browser";
	public final static String SYSTEM_EDITOR_ALLTABLES = "system.editor.alltables";
	public final static String FILEDIALOG_BACKGROUND = "filedialog.background";
	public final static String FILEDIALOG_FOREGROUND = "filedialog.foreground";
	
	public final static String PRINTING_REMOTE="printing.remote";
	public final static String PRINTING_HOST="printing.host";
	public final static String PRINTING_PRINTER_NAME="printing.printer_name";
	public final static String PRINTING_BUFFER_SIZE="printing.buffer_size";
	public final static String PRINTING_PORT="printing.port";
		public final static String HELP_BROWSER="help.browser";	public final static String HELP_START_PAGE="help.start_page";
	
	public final static String RML_EDITOR="rml.editor";

	public static String PRINT_RED = "\033[31m";
	public static String PRINT_NORMAL = "\033[0m";
	public static String PRINT_LIGHT = "\033[01m";
	public static String PRINT_GREEN = "\033[32m";
	public static String PRINT_BLUE = "\033[34m";
	public static String PRINT_YELLOW = "\033[33m";
	public static String PRINT_WHITE = "\033[37m";
	public static String PRINT_BLACK = "\033[30m";
	public final static String _DEFAULTFONT= "Courier,BOLD,14";
	public final static String _DEFAULTCOLOR= "#efefef,black,gray,black,gray,black";
	public final static String  FTITLE = "enc.ftitle";
	public final static String  DTITLE = "enc.dtitle";
	public final static String  BUTTON = "enc.button";
	public final static String  FIELD = "enc.field";
	public static String DEFAULTFONT=_DEFAULTFONT;
	public static String DEFAULTCOLOR=_DEFAULTCOLOR;
	public static Properties prop;
	public static Loader loader;
	public static ClassLoader cl;
	
	public static /*ByteToCharConverter*/ CharsetDecoder btc; 
	public static /*CharToByteConverter*/ CharsetEncoder ctb; 
	public static int loader_debug = 0; 
	public static int protocol_debug = 0;
	public static int rml_debug = 0;
	public static int views_debug = 0;
	public static int lisp_debug = 0;
	public static int dstore_debug = 0;
	public static int parser_debug = 0;
	public static int calc_debug = 0;
	public static boolean loader_exception = true;
        public static String needConnect = "needConnect";
        public static String usrSchema = "login.schema";
        public static String calcLanguage = "calc.language";
}









