package dominio.unitaria;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import dominio.Producto;
import dominio.Vendedor;
import dominio.repositorio.RepositorioGarantiaExtendida;
import dominio.repositorio.RepositorioProducto;
import testdatabuilder.ProductoTestDataBuilder;

public class ProductoTest {

	private static final String CODIGO = "S01H1AT51";
	private static final String NOMBRE_PRODUCTO = "Impresora";
	private static final int PRECIO = 550000;

	private static final String CODIGO_TRES_VOCALES = "G20A12H3EBPI";

	private static final String CODIGO_CUATRO_VOCALES = "A1TSO0E5I";

	private static final String CODIGO_DOS_VOCALES = "JK192ME2A";

	private static final double PRECIO_GARANTIA_COSTO_PRODUCTO_780_000 = 156_000;

	private static final double PRECIO2 = 450_000;

	private static final double PRECIO_GARANTIA_COSTO_PRODUCTO_450_000 = 45_000;

	@Test
	public void crearProductoTest() {

		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder().conNombre(NOMBRE_PRODUCTO)
				.conCodigo(CODIGO).conPrecio(PRECIO);

		// act
		Producto producto = productoTestDataBuilder.build();

		// assert
		assertEquals(NOMBRE_PRODUCTO, producto.getNombre());
		assertEquals(CODIGO, producto.getCodigo());
		assertEquals(PRECIO, producto.getPrecio(), 0);
	}

	@Test
	public void codigoProductoTieneTresVocales() {

		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder().conCodigo(CODIGO_TRES_VOCALES);

		Producto producto = productoTestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		boolean contieneTresVocales = vendedor.codigoContieneTresVocales(producto.getCodigo());

		// assert
		assertTrue(contieneTresVocales);
	}

	@Test
	public void codigoProductoNoTieneTresVocales() {

		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder()
				.conCodigo(CODIGO_CUATRO_VOCALES);

		Producto producto = productoTestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		boolean contieneTresVocales = vendedor.codigoContieneTresVocales(producto.getCodigo());

		// assert
		assertFalse(contieneTresVocales);
	}

	@Test
	public void codigoProductoNoTieneTresVocalesVersion2() {

		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder().conCodigo(CODIGO_DOS_VOCALES);

		Producto producto = productoTestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		boolean contieneTresVocales = vendedor.codigoContieneTresVocales(producto.getCodigo());

		// assert
		assertFalse(contieneTresVocales);
	}

	@Test
	public void costoProductoSuperaElValorDe500_000() {

		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder();

		Producto producto = productoTestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		double valorGarantiaExtendida = vendedor.calcularPrecioGarantiaExtendida(producto.getPrecio());

		// assert
		assertEquals(PRECIO_GARANTIA_COSTO_PRODUCTO_780_000, valorGarantiaExtendida, 0);
	}

	@Test
	public void costoProductoNoSuperaElValorDe500_000() {

		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder().conPrecio(PRECIO2);

		Producto producto = productoTestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		double valorGarantiaExtendida = vendedor.calcularPrecioGarantiaExtendida(producto.getPrecio());

		// assert
		assertEquals(PRECIO_GARANTIA_COSTO_PRODUCTO_450_000, valorGarantiaExtendida, 0);
	}

}
