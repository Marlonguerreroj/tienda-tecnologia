package dominio.integracion;

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Vendedor;
import dominio.Producto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";

	private static final String CODIGO_TRES_VOCALES = "F01TSA0E5I";

	private static final String CODIGO_CUATRO_VOCALES = "FO1TSA0E5I";

	private static final String NOMBRE_CLIENTE = "Marlon Guerrero";

	private static final double PRECIO_GARANTIA_COSTO_PRODUCTO_780_000 = 156_000;

	private static final double PRECIO = 400_000;

	private static final double PRECIO_GARANTIA_COSTO_PRODUCTO_400_000 = 40_000;

	/**
	 * Modificar el valor de la fecha. Este valor se realiza teniendo en cuenta
	 * que se calcularon los 100 dias a partir de la fecha 24/06/2019
	 */
	private static final String FECHA_FINAL_100_DIAS = "02/10/2019";

	private SistemaDePersistencia sistemaPersistencia;

	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {

		sistemaPersistencia = new SistemaDePersistencia();

		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();

		sistemaPersistencia.iniciar();
	}

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtener(producto.getCodigo()));
		Assert.assertEquals(PRECIO_GARANTIA_COSTO_PRODUCTO_780_000,
				repositorioGarantia.obtener(producto.getCodigo()).getPrecioGarantia(), 0);
		Assert.assertEquals(NOMBRE_CLIENTE, repositorioGarantia.obtener(producto.getCodigo()).getNombreCliente());
	}

	@Test
	public void generarGarantiaCostoProductoInferior500_000Test() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).conPrecio(PRECIO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtener(producto.getCodigo()));
		Assert.assertEquals(PRECIO_GARANTIA_COSTO_PRODUCTO_400_000,
				repositorioGarantia.obtener(producto.getCodigo()).getPrecioGarantia(), 0);
		Assert.assertEquals(FECHA_FINAL_100_DIAS, new SimpleDateFormat("dd/MM/yyyy")
				.format(repositorioGarantia.obtener(producto.getCodigo()).getFechaFinGarantia()));
		Assert.assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(new Date()), new SimpleDateFormat("dd/MM/yyyy")
				.format(repositorioGarantia.obtener(producto.getCodigo()).getFechaSolicitudGarantia()));
		Assert.assertEquals(NOMBRE_CLIENTE, repositorioGarantia.obtener(producto.getCodigo()).getNombreCliente());
	}

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();

		repositorioProducto.agregar(producto);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
		;
		try {

			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
			fail();

		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}

	@Test
	public void codigoProductoTieneTresVocales() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conCodigo(CODIGO_TRES_VOCALES).build();

		repositorioProducto.agregar(producto);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		try {
			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
			fail();

		} catch (Exception e) {
			// assert
			Assert.assertEquals(Vendedor.EL_COD_PRODUCTO_TIENE_TRES_VOCALES, e.getMessage());
			Assert.assertNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
		}

	}

	@Test
	public void codigoProductoNoTieneTresVocales() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(CODIGO_CUATRO_VOCALES).build();

		repositorioProducto.agregar(producto);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
	}

	@Test
	public void validarNombreClienteGarantiaExtendida() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().build();

		repositorioProducto.agregar(producto);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
		Assert.assertEquals(NOMBRE_CLIENTE, repositorioGarantia.obtener(producto.getCodigo()).getNombreCliente());
	}

	@Test
	public void costoProductoSuperaElValorDe500_000() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().build();

		repositorioProducto.agregar(producto);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtener(producto.getCodigo()));
		Assert.assertEquals(PRECIO_GARANTIA_COSTO_PRODUCTO_780_000,
				repositorioGarantia.obtener(producto.getCodigo()).getPrecioGarantia(), 0);

	}

	@Test
	public void costoProductoNoSuperaElValorDe500_000() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO).build();

		repositorioProducto.agregar(producto);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtener(producto.getCodigo()));
		Assert.assertEquals(PRECIO_GARANTIA_COSTO_PRODUCTO_400_000,
				repositorioGarantia.obtener(producto.getCodigo()).getPrecioGarantia(), 0);

	}

}
