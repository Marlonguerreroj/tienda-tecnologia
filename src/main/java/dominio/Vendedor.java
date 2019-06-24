package dominio;

import dominio.repositorio.RepositorioProducto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

	public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";

	public static final String EL_COD_PRODUCTO_TIENE_TRES_VOCALES = "Este producto no cuenta con garantia extendida";

	public static final String EXPRESION_REGULAR_VOCALES = "[AEIOU]";

	public static final int MONTO_FIJO = 500_000;

	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
		this.repositorioProducto = repositorioProducto;
		this.repositorioGarantia = repositorioGarantia;

	}

	public void generarGarantia(String codigo, String nombreCliente) {
		boolean isDuplicate = false;
		try {
			repositorioGarantia.obtener(codigo);
			isDuplicate = true;
		} catch (Exception e) {
			if (!codigoContieneTresVocales(codigo)) {
				Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
				double precioGarantia = calcularPrecioGarantiaExtendida(producto.getPrecio());
				Date fechaInicial = new Date();
				Date fechaFinal = calcularFechaFinGarantiaExtendida(producto.getPrecio(), fechaInicial);
				repositorioGarantia.agregar(
						new GarantiaExtendida(producto, fechaInicial, fechaFinal, precioGarantia, nombreCliente));
			} else {
				throw new GarantiaExtendidaException(EL_COD_PRODUCTO_TIENE_TRES_VOCALES);
			}

		} finally {
			if (isDuplicate)
				throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
		}

	}

	public boolean tieneGarantia(String codigo) {
		return repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo) != null;
	}

	public boolean codigoContieneTresVocales(String codigo) {
		int contadorVocales = 0;
		Pattern patron = Pattern.compile(EXPRESION_REGULAR_VOCALES);
		Matcher filtro = patron.matcher(codigo);
		while (filtro.find())
			contadorVocales++;
		return contadorVocales == 3;
	}

	public double calcularPrecioGarantiaExtendida(double costoProducto) {
		if (costoProducto > MONTO_FIJO)
			return costoProducto * 0.20;
		else
			return costoProducto * 0.10;
	}

	public Date calcularFechaFinGarantiaExtendida(double costoProducto, Date fechaIni) {
		Date fechaFin = null;
		int contador;
		LocalDate fechaInicial = fechaIni.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if (costoProducto > MONTO_FIJO) {
			contador = 200;
			while (contador > 0) {
				fechaInicial = fechaInicial.plusDays(1);
				if (fechaInicial.getDayOfWeek() != DayOfWeek.MONDAY)
					contador--;
			}
			if (fechaInicial.getDayOfWeek() == DayOfWeek.SUNDAY)
				fechaInicial = fechaInicial.plusDays(1);
			fechaFin = Date.from(fechaInicial.atStartOfDay(ZoneId.systemDefault()).toInstant());
		} else {
			contador = 100;
			fechaInicial = fechaInicial.plusDays(contador);
			fechaFin = Date.from(fechaInicial.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		return fechaFin;
	}

}
