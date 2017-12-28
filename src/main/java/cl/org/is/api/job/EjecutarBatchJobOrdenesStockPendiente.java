/**
 *@name Channel.java
 * 
 *@version 1.0 
 * 
 *@date 07-03-2017
 * 
 *@author EA7129
 * 
 *@copyright Cencosud. All rights reserved.
 */
package cl.org.is.api.job;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import java.util.Properties;

//import com.ibm.as400.access.AS400;
//import com.ibm.as400.access.AS400JDBCDriver;

/**
 * 
 *  @description Clase que permite identificar stock pendientes enviados desde EOM hacias WMS
 *
 */
public class EjecutarBatchJobOrdenesStockPendiente {

	private static final int DIFF_HOY_FECHA_INI = 8;
	private static final int DIFF_HOY_FECHA_FIN = 1;
	
	//private static final int DIFF_HOY_FECHA_FIN = 19;
	
	
	private static final int FORMATO_FECHA_0 = 0;
	private static final int FORMATO_FECHA_1 = 1;
	private static final int FORMATO_FECHA_3 = 3;
	
	
	private static BufferedWriter bw;
	private static String path;
	private static final String RUTA_ENVIO = "C:/Share/Inbound/OrdenesStockPendientes";
	//private static final String RUTA_ENVIO = "\\\\Share\\Inbound\\OrdenesStockPendientes";// \\172.18.150.41\datamart windows
	//private static final String RUTA_ENVIO = "C:/Share/Inbound/OMNICANAL";
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Map <String, String> mapArguments = new HashMap<String, String>();
		String sKeyAux = null;

		for (int i = 0; i < args.length; i++) {

			if (i % 2 == 0) {

				sKeyAux = args[i];
			}
			else {

				mapArguments.put(sKeyAux, args[i]);
			}
		}

		try {

			File info              = null;
			File miDir             = new File(".");
			path                   =  miDir.getCanonicalPath();
			info                   = new File(path+"/info.txt");
			bw = new BufferedWriter(new FileWriter(info));
			info("El programa se esta ejecutando...");
			crearTxt(mapArguments);
			System.out.println("El programa finalizo.");
			info("El programa finalizo.");
			bw.close();
		}
		catch (Exception e) {

			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Metodo que hace commit en la base de datos
	 * 
	 * @param Connection,
	 *            conexion a la base de datos
	 * @return si valor de retorno
	 
*/
	private static void commit(Connection dbconnection, String sql) {
		PreparedStatement pstmt = null;
		try {
			pstmt = dbconnection.prepareStatement(sql);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cerrarTodo(null, pstmt, null);
		}
	}

	private static void crearTxt(Map <String, String> mapArguments) {

		//Connection dbconnection = crearConexion();
		Connection dbconnOracle = crearConexionOracle();
		Connection dbconnOracleWms = crearConexionWms();
		Connection dbconnOracle2 = crearConexionOracle2();

		File file1              = null;
		BufferedWriter bw       = null;
		BufferedWriter bw2      = null;
		PreparedStatement pstmt = null;
		StringBuffer sb         = null;
		String sFechaIni           = null;
		String sFechaFin           = null;
		String iFechaIni           	 = null;
		String iFechaFin           	 = null;
		
		String dFechaIni           	 = null;
		String dFechaFin           	 = null;
		
		
		PreparedStatement pstmtInsert 						= null;

		try {

			try {
				
				
				Date now2 = new Date();
				SimpleDateFormat ft2 = new SimpleDateFormat ("dd/MM/YYYY");
				String currentDate = ft2.format(now2);

				sFechaIni = restarDiasTxt(mapArguments.get("-fi"), DIFF_HOY_FECHA_INI);
				sFechaFin = restarDiasTxt(mapArguments.get("-ft"), DIFF_HOY_FECHA_FIN);
				
				iFechaIni = restarDias(mapArguments.get("-fi"), DIFF_HOY_FECHA_INI);
				iFechaFin = restarDias(mapArguments.get("-ft"), DIFF_HOY_FECHA_FIN);
				
				dFechaIni = restarDiasEliminar(mapArguments.get("-fi"), DIFF_HOY_FECHA_INI);
				dFechaFin = restarDiasEliminar(mapArguments.get("-ft"), DIFF_HOY_FECHA_FIN);
				
				info("[currentDate]:"+currentDate);
				
				info("[sFechaIni]:"+sFechaIni);
				info("[sFechaFin]:"+sFechaFin);
				
				info("[iFechaIni]:"+iFechaIni);
				info("[iFechaFin]:"+iFechaFin);
				
				info("[dFechaIni]:"+dFechaIni);
				info("[dFechaFin]:"+dFechaFin);
			}
			catch (Exception e) {

				e.printStackTrace();
			}

			info("[FORMATO_FECHA_1]:"+FORMATO_FECHA_0);
			info("[FORMATO_FECHA_1]:"+FORMATO_FECHA_1);
			info("[FORMATO_FECHA_3]:"+FORMATO_FECHA_3);
			info("[sFechaFin]:"+sFechaFin);
			info("[RUTA_ENVIO]:"+RUTA_ENVIO);
			//file1                   = new File(path + "/-" + iFechaIni + ".txt");
			file1                   = new File(path + "/" + sFechaIni + "_"+sFechaFin+ ".txt");
			
			
			
			Thread.sleep(60);
			info("Pausa para eliminar  CUMPLIMIENTO sleep(60 seg)");
			info("DELETE FROM ORDENES_STOCK_PENDIENTES WHERE 1 = 1 AND FECHA_CREACION > = '"+dFechaIni+" 00:00:00' AND FECHA_CREACION <= '"+dFechaFin+" 23:59:59'");
			eliminar(dbconnOracle2,"DELETE FROM ORDENES_STOCK_PENDIENTES WHERE 1 = 1 AND FECHA_CREACION > = '"+dFechaIni+" 00:00:00' AND FECHA_CREACION <= '"+dFechaFin+" 23:59:59'");

			commit(dbconnOracle2,"COMMIT"); 
			
			
			Thread.sleep(60);
			info("Pausa para insertar  CUMPLIMIENTO sleep(60 seg)");
			String sql = "Insert into ORDENES_STOCK_PENDIENTES (DISTRO_NBR, PO_NBR, SIZE_DESC, SHPD_QTY, fecha_Creacion, ORDERID, ORDERLINEID, orden,nro_de_linea,do_dtl_status,tipoorden,pedido,sku,cantidad_pendiente,bodega,stat_code,estado, TC_ORDER_ID, CANTIDAD_DESPACHADA, CANTIDAD_ORDENADA, EQUALS_QHPD_DESPACHA) values (?,?,?,?,? ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmtInsert = dbconnOracle2.prepareStatement(sql);
			Thread.sleep(60);
			
			sb = new StringBuffer();
			//Query Ordernes pendiente de envio
			//sb.append("SELECT a.DISTRO_NBR, SUBSTR(a.DISTRO_NBR, 0, 9) as orderId,  SUBSTR(a.DISTRO_NBR, 10, 12) as orderLineId,  a.PO_NBR, b.size_desc, a.SHPD_QTY, a.create_date_time FROM outpt_pkt_DTL a, item_master b WHERE a.sku_id=b.sku_id AND  a.SHPD_QTY>0 AND  To_Char (a.create_date_time,'dd/mm/yyyy')>='"+iFechaIni+"'  AND To_Char (a.create_date_time,'dd/mm/yyyy')<='"+iFechaFin+"' AND a.distro_nbr IS NOT NULL ");
			//sb.append("SELECT a.DISTRO_NBR, SUBSTR(a.DISTRO_NBR, 0, 9) as orderId,  SUBSTR(a.DISTRO_NBR, 10, 12) as orderLineId,  a.PO_NBR, b.size_desc, a.SHPD_QTY, a.create_date_time FROM outpt_pkt_DTL a, item_master b WHERE a.sku_id=b.sku_id AND  a.SHPD_QTY>0 AND  To_Char (a.create_date_time,'dd/mm/yyyy HH24:MI:SS')>='"+iFechaIni+" 00:00:00'  AND To_Char (a.create_date_time,'dd/mm/yyyy HH24:MI:SS')<='"+iFechaFin+" 23:59:59' AND a.distro_nbr IS NOT NULL ");
			sb.append("SELECT a.DISTRO_NBR, SUBSTR(a.DISTRO_NBR, 0, 9) as orderId,  SUBSTR(a.DISTRO_NBR, 10, 12) as orderLineId,  a.PO_NBR, b.size_desc, sum (a.SHPD_QTY) as SHPD_QTY, a.create_date_time FROM outpt_pkt_DTL a, item_master b WHERE a.sku_id=b.sku_id AND  a.SHPD_QTY>0 AND  To_Char (a.create_date_time,'dd/mm/yyyy HH24:MI:SS')>='"+iFechaIni+" 00:00:00'  AND To_Char (a.create_date_time,'dd/mm/yyyy HH24:MI:SS')<='"+iFechaFin+" 23:59:59' AND a.distro_nbr IS NOT NULL group by a.DISTRO_NBR,  a.PO_NBR,  b.size_desc, a.create_date_time");

			info("[sb1]:"+sb);
			pstmt         = dbconnOracleWms.prepareStatement(sb.toString());
			//pstmt.setInt(1, iFechaIni);
			//pstmt.setInt(2, iFechaFin);
			sb = new StringBuffer();
			ResultSet rs = pstmt.executeQuery();
			bw  = new BufferedWriter(new FileWriter(file1));
			bw.write("DISTRO_NBR;");
			bw.write("PO_NBR;");
			bw.write("size_desc;");
			bw.write("SHPD_QTY;");
			bw.write("create_date_time;");


			bw.write("orderId;");
			bw.write("orderLineId;");

			 
			bw.write("orden" + ";");
			bw.write("nro_de_linea" + ";");
			bw.write("do_dtl_status" + ";");
			bw.write("tipoorden" + ";");
			bw.write("pedido" + ";");
			bw.write("sku" + ";");
			bw.write("cantidad_pendiente" + ";");
			bw.write("bodega" + ";");
			bw.write("stat_code" + ";");
			bw.write("estado" + ";");
			
			
			bw.write("tc_order_id" + ";");
			bw.write("cantidad_Despachada" + ";");
			bw.write("cantidad_ordenada" + ";");
			bw.write("equals_qhpd_despacha" + "\n");
			
			

			while (rs.next()) {
				//bw.write(rs.getString("Distro_WMS") + ";");
				bw.write(rs.getString("DISTRO_NBR") + ";");
				bw.write(rs.getString("PO_NBR") + ";");
				bw.write(rs.getString("size_desc") + ";");
				bw.write(rs.getString("SHPD_QTY") + ";");
				//bw.write(rs.getString("create_date_time") + ";");
				bw.write(formatDate(rs.getTimestamp("create_date_time"), FORMATO_FECHA_3)+ ";");
				
				bw.write(rs.getInt("orderId") + ";");
				bw.write(rs.getInt("orderLineId") + ";");
				
				pstmtInsert.setString(1, rs.getString("DISTRO_NBR"));
				pstmtInsert.setString(2, rs.getString("PO_NBR"));
				pstmtInsert.setString(3, rs.getString("size_desc"));
				pstmtInsert.setString(4, rs.getString("SHPD_QTY"));
				pstmtInsert.setString(5, formatDate(rs.getTimestamp("create_date_time"), FORMATO_FECHA_3));
				
				pstmtInsert.setInt(6, rs.getInt("orderId"));
				pstmtInsert.setInt(7, rs.getInt("orderLineId"));
				
				
				if (rs.getString("DISTRO_NBR") != null) {

					//bw.write(ejecutarQuery2(limpiarCeros(rs.getString("SolicitudPOS")), rs.getString("SKU"), dbconnOracle));
					bw.write(ejecutarQuery2( rs.getString("orderId"), rs.getString("orderLineId"), rs.getInt("SHPD_QTY"),  dbconnOracle, pstmtInsert));
					pstmtInsert.executeUpdate();
					commit(dbconnOracle2,"COMMIT");	

				} else {
					
				}
				
				
				
				//bw.write("===="+ejecutarQuery3( rs.getString("Distro_WMS"), dbconnOracleWms));
				
				//info("Archivos creados."+ejecutarQuery3( rs.getString("Distro_WMS"), dbconnOracleWms));
				
			}
			info("Archivos creados.");
			
		}
		catch (Exception e) {

			System.out.println(e.getMessage());
			info("[crearTxt1]Exception:"+e.getMessage());
		}
		finally {

			cerrarTodo(dbconnOracle,pstmt,bw);
			cerrarTodo(dbconnOracleWms, null, bw2);
		}
	}
	
	
	/**
	 * Metodo que ejecuta la eliminacion de registros en una tabla 
	 * 
	 * @param Connection, conexion de las base de datos
	 * @param String, query para la eliminacion  
	 * @return 
	 */
	private static void eliminar(Connection dbconnection,  String sql) {
		info("Inicio elimnar Cumplimiento.");
		PreparedStatement pstmt = null;
		try {
			pstmt = dbconnection.prepareStatement(sql);
			info("registros elimnados Cumplimient : " + pstmt.executeUpdate());
			
		}
		catch (Exception e) {
			e.printStackTrace();
			info("[elimnarCumplimiento]Exception:" + e.getMessage());
		}
		finally {
			cerrarTodo(null, pstmt, null);
		}
		info("Fin elimnar Cumplimiento.");

	}
	/**
	 * Metodo que formatea una fecha 
	 * 
	 * @param String, fecha a formatear
	 * @param String, formato de fecha
	 * @return String retorna el formato de fecha a un String
	 * 
	 */
	private static String formatDate(Date fecha, int iOptFormat) {

		String sFormatedDate = null;
		String sFormat = null;

		try {

			SimpleDateFormat df = null;

			switch (iOptFormat) {

			case 0:
				sFormat = "dd/MM/yy HH:mm:ss,SSS";
				break;
			case 1:
				sFormat = "dd/MM/yy";
				break;
			case 2:
				sFormat = "dd/MM/yy HH:mm:ss";
				break;
			case 3:
				sFormat = "yyyy-MM-dd HH:mm:ss";
				break;
			}
			df = new SimpleDateFormat(sFormat);
			sFormatedDate = df.format(fecha != null ? fecha:new Date(0));

			if (iOptFormat == 0 && sFormatedDate != null) {

				sFormatedDate = sFormatedDate + "000000";
			}
		}
		catch (Exception e) {

			info("[formatDate]Exception:"+e.getMessage());
		}
		return sFormatedDate;
	}
	private static String ejecutarQuery2(String orderId, String orderLineId, int shpdQty, Connection dbconnection, PreparedStatement pstmtInsert) {
		info("[Inicio]:"+"");
		StringBuffer sb         = new StringBuffer();
		PreparedStatement pstmt = null;

		try {

			sb = new StringBuffer();
			//Enviados a tiendas despachados
			sb.append("select  o.Order_Type as tipoorden,o.purchase_order_number as Pedido,o.tc_order_id,oli.tc_order_line_id,oli.item_name as SKU, nvl(oli.shipped_qty,0) cantidad_Despachada,oli.order_qty as cantidad_ordenada, (oli.order_qty - nvl(oli.shipped_qty,0))  as Cantidad_PENDIENTE, o.O_FACILITY_ALIAS_ID as bodega from orders o join order_line_item oli on oli.order_id = o.order_id where (oli.order_qty - nvl(oli.shipped_qty,0)) > 0          and oli.do_dtl_status > 100         and oli.do_dtl_status <= 185  and o.do_status <> 190 and o.IS_CANCELLED <> 1 and TC_ORDER_ID='"+orderId+"' and TC_ORDER_LINE_ID='"+orderLineId+"' ");
			sb.append("");
			sb.append("");// 90 enviados a tiendas despachados
			info("[sb2]:"+sb);
			pstmt = dbconnection.prepareStatement(sb.toString());
			ResultSet rs = pstmt.executeQuery();
			sb = new StringBuffer();

			boolean reg = false;
			do{
				reg = rs.next();
				if (reg){
					
					
					
					sb.append("-" + ";");//Orden
					sb.append("-" + ";");//Numero de Linea
					sb.append("-" + ";");//estatus
					sb.append(rs.getString("tipoorden") + ";");
					sb.append(rs.getString("Pedido") + ";");
					sb.append(rs.getString("SKU") + ";");
					sb.append(rs.getString("Cantidad_PENDIENTE") + ";");
					sb.append(rs.getString("bodega") + ";");//bodega
					sb.append("-" + ";");//stat_code
					sb.append("0" + ";"); // Estado No despachado
					
					sb.append(rs.getInt("tc_order_id") + ";");
					sb.append(rs.getInt("cantidad_Despachada") + ";");
					sb.append(rs.getInt("cantidad_ordenada") + ";");
					sb.append(obtenerCamposIguales(shpdQty, rs.getInt("cantidad_Despachada"))+ "\n");
					
					pstmtInsert.setInt(8, 1);
					pstmtInsert.setString(9, "-");
					pstmtInsert.setString(10, "-");
					pstmtInsert.setString(11, rs.getString("tipoorden"));
					pstmtInsert.setString(12, rs.getString("Pedido") );
					pstmtInsert.setString(13, rs.getString("SKU"));
					pstmtInsert.setString(14, rs.getString("Cantidad_PENDIENTE"));
					pstmtInsert.setString(15, rs.getString("bodega"));//Bodega
					pstmtInsert.setInt(16, 0);
					pstmtInsert.setInt(17, 0);
					
					pstmtInsert.setInt(18, rs.getInt("tc_order_id"));
					pstmtInsert.setInt(19, rs.getInt("cantidad_Despachada"));
					pstmtInsert.setInt(20, rs.getInt("cantidad_ordenada"));
					
					pstmtInsert.setString(21, obtenerCamposIguales(shpdQty, rs.getInt("cantidad_Despachada")));
					
				
					break;
				}else{
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append("1" + ";");// despachado
					
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append("-" + ";");
					sb.append(obtenerCamposIguales(0, 0) + "\n");
					
					
					
					
					pstmtInsert.setInt(8, 1);
					pstmtInsert.setString(9, "-");
					pstmtInsert.setString(10, "-");
					pstmtInsert.setString(11, "-");
					pstmtInsert.setString(12, "-");
					pstmtInsert.setString(13, "-");
					pstmtInsert.setString(14, "-");
					pstmtInsert.setString(15, "-");
					pstmtInsert.setInt(16, 1);
					pstmtInsert.setInt(17, 1);
					pstmtInsert.setInt(18, 0);
					pstmtInsert.setInt(19, 0);
					pstmtInsert.setInt(20, 0);
					pstmtInsert.setString(21, "-");
					
				}
			}
			while (reg);
		}
		catch (Exception e) {

			e.printStackTrace();
			info("[crearTxt2]Exception:"+e.getMessage());
		}
		finally {

			cerrarTodo(null,pstmt,null);
		}
		return sb.toString();
	}
	
	/*
	private static String ejecutarQuery3(String Distro_WMS, Connection dbconnection) {
		
		StringBuffer sb         = new StringBuffer();
		PreparedStatement pstmt = null;
		String result = "";
		try {
			sb = new StringBuffer();
			sb.append("SELECT DISTINCT(STAT_CODE) FROM CARTON_HDR WHERE PKT_CTRL_NBR IN (SELECT PKT_CTRL_NBR From store_distro ");
			sb.append("WHERE distro_nbr = '");
			sb.append(Distro_WMS);
			sb.append("') AND STAT_CODE = 90 and CREATE_DATE_TIME>SYSDATE-30");// 90 enviados a tiendas estos son despachos      bus ac de eom pendiente de despacho 
			info("[sb2]:"+sb);
			pstmt = dbconnection.prepareStatement(sb.toString());
			ResultSet rs = pstmt.executeQuery();
			sb = new StringBuffer();

			boolean reg = false;
			do{
				reg = rs.next();
				if (reg){
					result = rs.getString("STAT_CODE");
					break;
				}else{
					result = "0";
				}
			}
			while (reg);
		}
		catch (Exception e) {

			e.printStackTrace();
			info("[crearTxt2]Exception:"+e.getMessage());
		}
		finally {

			cerrarTodo(null,pstmt,null);
		}
		return result;
	}
	*/
	/*
	private static String ejecutarQuery3(String Distro_WMS, Connection dbconnection) {
		
		StringBuffer sb         = new StringBuffer();
		PreparedStatement pstmt = null;
		String result = null;
		try {

			sb = new StringBuffer();
			sb.append("SELECT DISTINCT(STAT_CODE) FROM CARTON_HDR WHERE PKT_CTRL_NBR IN (SELECT PKT_CTRL_NBR From store_distro ");
			sb.append("WHERE distro_nbr = '");
			sb.append(Distro_WMS);
			sb.append("')");
			info("[sb3]:"+sb);
			pstmt = dbconnection.prepareStatement(sb.toString());
			ResultSet rs = pstmt.executeQuery();
			sb = new StringBuffer();
			
			while (rs.next()) {
				return result = rs.getString("STAT_CODE")+"\n";
			}
			
		}
		catch (Exception e) {

			e.printStackTrace();
			info("[crearTxt3]Exception:"+e.getMessage());
		}
		finally {

			cerrarTodo(null,pstmt,null);
		}
		return result;
	}
	*/
	
	/**
	 * Metodo que compara si dos campos son iguales o no 
	 * 
	 * @param String, campo shpdQty
	 * @return String campo cantidadDespachada
	 */
	private static String obtenerCamposIguales(int shpdQty, int cantidadDespachada) {
		String result = null;
		if (shpdQty == cantidadDespachada) {
			result = "SI";
		} else {
			
			result = "NO";
		}
		return result;
	}
	private static String restarDias(String sDia, int iCantDias) {

		String sFormatoIn = "yyyyMMdd";
		String sFormatoOut = "dd/MM/yyyy";
		Calendar diaAux = null;
		String sDiaAux = null;
		SimpleDateFormat df = null;

		try {

			diaAux = Calendar.getInstance();
			df = new SimpleDateFormat(sFormatoIn);
			diaAux.setTime(df.parse(sDia));
			diaAux.add(Calendar.DAY_OF_MONTH, -iCantDias);
			df.applyPattern(sFormatoOut);
			sDiaAux = df.format(diaAux.getTime());
		}
		catch (Exception e) {

			info("[restarDias]error: " + e);
		}
		return sDiaAux;
	}
	
	
	
	private static String restarDiasTxt(String sDia, int iCantDias) {

		String sFormatoIn = "yyyyMMdd";
		String sFormatoOut = "dd-MM-yyyy";
		Calendar diaAux = null;
		String sDiaAux = null;
		SimpleDateFormat df = null;

		try {

			diaAux = Calendar.getInstance();
			df = new SimpleDateFormat(sFormatoIn);
			diaAux.setTime(df.parse(sDia));
			diaAux.add(Calendar.DAY_OF_MONTH, -iCantDias);
			df.applyPattern(sFormatoOut);
			sDiaAux = df.format(diaAux.getTime());
		}
		catch (Exception e) {

			info("[restarDias]error: " + e);
		}
		return sDiaAux;
	}
	
	private static String restarDiasEliminar(String sDia, int iCantDias) {

		String sFormatoIn = "yyyyMMdd";
		String sFormatoOut = "yyyy-MM-dd";
		Calendar diaAux = null;
		String sDiaAux = null;
		SimpleDateFormat df = null;

		try {

			diaAux = Calendar.getInstance();
			df = new SimpleDateFormat(sFormatoIn);
			diaAux.setTime(df.parse(sDia));
			diaAux.add(Calendar.DAY_OF_MONTH, -iCantDias);
			df.applyPattern(sFormatoOut);
			sDiaAux = df.format(diaAux.getTime());
		}
		catch (Exception e) {

			info("[restarDias]error: " + e);
		}
		return sDiaAux;
	}
/*
	private static Connection crearConexion() {

		System.out.println("Creado conexion a ROBLE.");
		AS400JDBCDriver d = new AS400JDBCDriver();
		String mySchema = "RDBPARIS2";
		Properties p = new Properties();
		AS400 o = new AS400("roble.cencosud.corp","USRCOM", "USRCOM");
		Connection dbconnection = null;

		try {

			System.out.println("AuthenticationScheme: "+o.getVersion());
			dbconnection = d.connect (o, p, mySchema);
			System.out.println("Conexion a ROBLE CREADA.");
		}
		catch (Exception e) {

			System.out.println(e.getMessage());
		}
		return dbconnection;
	}
*/
	private static Connection crearConexionOracle() {

		Connection dbconnection = null;

		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			
			//Shareplex
			//dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@g500603svcr9.cencosud.corp:1521:MEOMCLP","REPORTER","RptCyber2015");
			
			dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@g500603sv0zt.cencosud.corp:1521:MEOMCLP", "ca14", "Manhattan1234");


		}
		catch (Exception e) {

			e.printStackTrace();
		}
		return dbconnection;
	}
	
	
	private static Connection crearConexionWms() {

		Connection dbconnection = null;

		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			
			//Wms
			dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@172.18.162.115:1521:reportmhn","usr_repl_wmos_ecomm","cenco2017");
			
			//El servidor g500603sv0zt corresponde a Produccion. Por el momento
			//dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@g500603sv0zt.cencosud.corp:1521:MEOMCLP","ca14","Manhattan1234");


			//Comentado por cambio de base de datos. El servidor g500603svcr9 corresponde shareplex.
			//dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@g500603svcr9:1521:MEOMCLP","REPORTER","RptCyber2015");
			
			//El servidor g500603sv0zt corresponde a ProducciÃ³n.
			//dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@g500603sv0zt:1521:MEOMCLP","ca14","Manhattan1234");
			//dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@g500603sv0zt.cencosud.corp:1521:MEOMCLP","ca14","Manhattan1234");

		}
		catch (Exception e) {

			e.printStackTrace();
		}
		return dbconnection;
	}
	

	/*
	private static String limpiarCeros(String str) {

		int iCont = 0;

		while (str.charAt(iCont) == '0') {

			iCont++;
		}
		return str.substring(iCont, str.length());
	}
	*/
	
	/**
	 * Metodo que crea la conexion a la base de datos a KPI
	 * 
	 * @param Connection,  Objeto que representa una conexion a la base de datos
	 * @return 
	 * 
	*/
	private static Connection crearConexionOracle2() {

		Connection dbconnection = null;

		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");

			// Comentado por cambio de base de datos. El servidor g500603svcr9
			// corresponde shareplex.
			// dbconnection =
			// DriverManager.getConnection("jdbc:oracle:thin:@g500603svcr9:1521:MEOMCLP","REPORTER","RptCyber2015");

			// El servidor g500603sv0zt corresponde a Producción.
			dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@172.18.163.15:1521/XE", "kpiweb", "kpiweb");
		} catch (Exception e) {

			e.printStackTrace();
		}
		return dbconnection;
	}
	
 
	private static void cerrarTodo(Connection cnn, PreparedStatement pstmt, BufferedWriter bw){

		try {

			if (cnn != null) {

				cnn.close();
				cnn = null;
			}
		}
		catch (Exception e) {

			System.out.println(e.getMessage());
			info("[cerrarTodo]Exception:"+e.getMessage());
		}
		try {

			if (pstmt != null) {

				pstmt.close();
				pstmt = null;
			}
		}
		catch (Exception e) {

			System.out.println(e.getMessage());
			info("[cerrarTodo]Exception:"+e.getMessage());
		}
		try {

			if (bw != null) {

				bw.flush();
				bw.close();
				bw = null;
			}
		}
		catch (Exception e) {

			System.out.println(e.getMessage());
			info("[cerrarTodo]Exception:"+e.getMessage());
		}
	}

	private static void info(String texto){

		try {

			bw.write(texto+"\n");
			bw.flush();
		}
		catch (Exception e) {

			System.out.println("Exception:"+e.getMessage());
		}
	}
	/*
	private static int restarDia(String sDia) {

		int dia = 0;
		String sFormato = "yyyyMMdd";
		Calendar diaAux = null;
		String sDiaAux = null;
		SimpleDateFormat df = null;

		try {

			diaAux = Calendar.getInstance();
			df = new SimpleDateFormat(sFormato);
			diaAux.setTime(df.parse(sDia));
			diaAux.add(Calendar.DAY_OF_MONTH, -1);
			sDiaAux = df.format(diaAux.getTime());
			dia = Integer.parseInt(sDiaAux);
		}
		catch (Exception e) {

			info("[restarDia]Exception:"+e.getMessage());
		}
		return dia;
	}
	*/
}
