/**
 * 
 */
package cl.org.is.api.job;

/**
 * @author EA7129
 *
 */
public class Test {
	
	
	/**
	 * Metodo que compara si dos campos son iguales o no 
	 * 
	 * @param String, campo shpdQty
	 * @return String campo cantidadDespachada
	 */
	private static String obtenerCamposIguales(String shpdQty, String cantidadDespachada) {
		String result = null;
		if (shpdQty.equals(cantidadDespachada)) {
			result = "ok";
		} else {
			
			result = "no";
		}
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(""+obtenerCamposIguales("1","1"));
		
		

	}

}
