package dominio.unitaria;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import dominio.Vendedor;
import dominio.Producto;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String FECHA_INICIAL = "16/08/2018";

	private static final String FECHA_FINAL_200_DIAS = "06/04/2019";

	private static final String FECHA_FINAL_100_DIAS = "24/11/2018";

	private static final double PRECIO_350_000 = 350_000;

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder();

		Producto producto = productoTestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		when(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo())).thenReturn(producto);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		boolean existeProducto = vendedor.tieneGarantia(producto.getCodigo());

		// assert
		assertTrue(existeProducto);
	}

	@Test
	public void productoNoTieneGarantiaTest() {

		// arrange
		ProductoTestDataBuilder productoestDataBuilder = new ProductoTestDataBuilder();

		Producto producto = productoestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		when(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo())).thenReturn(null);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		boolean existeProducto = vendedor.tieneGarantia(producto.getCodigo());

		// assert
		assertFalse(existeProducto);
	}

	@Test
	public void validarFechaFinalGarantiaExtendida200Dias() throws ParseException {

		// arrange
		ProductoTestDataBuilder productoestDataBuilder = new ProductoTestDataBuilder();

		Producto producto = productoestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		Date fechaIni = new SimpleDateFormat("dd/MM/yyyy").parse(FECHA_INICIAL);
		Date fechaFin = vendedor.calcularFechaFinGarantiaExtendida(producto.getPrecio(), fechaIni);
		String fechaFinGarantia = new SimpleDateFormat("dd/MM/yyyy").format(fechaFin);
		// assert
		assertEquals(FECHA_FINAL_200_DIAS, fechaFinGarantia);
	}

	@Test
	public void validarFechaFinalGarantiaExtendida100Dias() throws ParseException {

		// arrange
		ProductoTestDataBuilder productoestDataBuilder = new ProductoTestDataBuilder();

		Producto producto = productoestDataBuilder.conPrecio(PRECIO_350_000).build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		Date fechaIni = new SimpleDateFormat("dd/MM/yyyy").parse(FECHA_INICIAL);
		Date fechaFin = vendedor.calcularFechaFinGarantiaExtendida(producto.getPrecio(), fechaIni);
		String fechaFinGarantia = new SimpleDateFormat("dd/MM/yyyy").format(fechaFin);
		// assert
		assertEquals(FECHA_FINAL_100_DIAS, fechaFinGarantia);
	}

}
