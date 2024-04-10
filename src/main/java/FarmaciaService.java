// Importa todas las clases y interfaces del paquete farmacia.
import farmacia.*;
// Importa específicamente la enumeración TipoOperacion dentro de la clase Operacion.
import farmacia.Operacion.TipoOperacion;

// Importaciones relacionadas con la manipulación de archivos y directorios.
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.StandardOpenOption;

// Importaciones para el manejo de fechas y colecciones.
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

// Define la clase FarmaciaService que proporciona funcionalidades para gestionar una farmacia.
public class FarmaciaService {
    // Referencia mutable a una instancia de Farmacia, permitiendo construir o modificar una farmacia.
    private final Farmacia.Builder farmacia;

    // Formato para las fechas usadas en las operaciones de la farmacia.
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    // Ruta del archivo donde se guarda el estado de la farmacia.
    static final String filePath = "farmacia_data.dat";

    // Constructor que intenta cargar el estado previo de la farmacia desde un archivo o crear una nueva si no existe.
    public FarmaciaService() {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                byte[] bytes = Files.readAllBytes(path);
                farmacia = Farmacia.parseFrom(bytes).toBuilder();
            } else {
                farmacia = Farmacia.newBuilder();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Guarda el estado actual de la farmacia en el archivo especificado.
    public void guardar() {
        try {
            Files.write(Paths.get(filePath), farmacia.build().toByteArray(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Agrega un producto al inventario de la farmacia, asignándole un ID único.
    public void agregarProductoInventario(Producto producto) {
        Inventario inventario = farmacia.getInventario();
        long id = inventario.getUltimoIdUsado() + 1;
        farmacia.getInventarioBuilder().addProductos(producto.toBuilder().setId(id)).setUltimoIdUsado(id);
    }

    // Obtiene un producto del inventario por su índice.
    public Producto obtenerProducto(int index) {
        return farmacia.getInventario().getProductos(index);
    }

    // Actualiza un producto en el inventario, reemplazando el existente con el mismo ID.
    public void alterarProductoInventario(Producto producto) {
        Inventario inventario = farmacia.getInventario();
        List<Producto> productos = inventario.getProductosList();
        List<Producto> productos_nuevos = productos.parallelStream()
                .filter(element -> element.getId() != producto.getId()).collect(Collectors.toList());
        productos_nuevos.add(producto);
        farmacia.setInventario(inventario.toBuilder().clearProductos().addAllProductos(productos_nuevos));
    }

    // Registra la compra de uno o más productos, actualizando el inventario y el historial de operaciones.
    public void comprarProducto(List<ProductoOperacion> productos) {
        operacionProducto(productos, Operacion.TipoOperacion.TIPO_OPERACION_COMPRA);
    }

    // Registra la venta de uno o más productos, actualizando el inventario y el historial de operaciones.
    public void venderProducto(List<ProductoOperacion> productos) {
        operacionProducto(productos, Operacion.TipoOperacion.TIPO_OPERACION_VENTA);
    }

    // Método auxiliar para registrar operaciones de compra o venta de productos.
    private void operacionProducto(List<ProductoOperacion> productos, Operacion.TipoOperacion tipo) {
        Double total = productos.parallelStream().map(ProductoOperacion::getPrecioEnOperacion).reduce(0.0, Double::sum);
        Inventario inventario = farmacia.getInventario();
        Operacion operacion = Operacion.newBuilder()
                .addAllProductos(productos)
                .setTipo(tipo)
                .setFecha(dateFormat.format(new Date()))
                .setTotal(total)
                .build();
        Map<Long, Long> existencias = inventario.getExistenciasMap();
        productos.forEach(productoOperacion -> {
            Long num = existencias.getOrDefault(productoOperacion.getIdProducto(), 0L);
            if (tipo == TipoOperacion.TIPO_OPERACION_COMPRA) {
                num += productoOperacion.getCantidad();
            } else {
                num -= productoOperacion.getCantidad();
            }

            farmacia.getInventarioBuilder().putExistencias(productoOperacion.getIdProducto(), num);
        });

        farmacia.addHistorialOperaciones(operacion);
    }

    // Obtiene un mapa de productos a sus cantidades existentes en el inventario.
    public Map<Producto, Long> obtenerProductosExistencias() {
        Map<Producto, Long> productos = new HashMap<>();
        List<Producto> productosList = farmacia.getInventario().getProductosList();
        Map<Long, Long> existencias = farmacia.getInventario().getExistenciasMap();

        existencias.forEach((key, value) -> productosList.parallelStream().filter(elemento -> elemento.getId() == key)
                .findFirst().ifPresent(producto1 -> productos.put(producto1, value)));
        return productos;
    }

    // Devuelve una lista de todos los productos en el inventario.
    public List<Producto> obtenerProductos() {
        return farmacia.getInventario().getProductosList();
    }

    // Crea un objeto ProductoOperacion para su uso en operaciones de compra o venta.
    public ProductoOperacion agregarProducto(Producto producto, long cantidad) {
        return ProductoOperacion.newBuilder()
                .setCantidad(cantidad)
                .setNombre(producto.getNombre())
                .setIdProducto(producto.getId())
                .setPrecioEnOperacion(producto.getPrecio())
                .build();
    }

    // Obtiene el historial completo de operaciones realizadas en la farmacia.
    public List<Operacion> obtenerHistorial() {
        return farmacia.getHistorialOperacionesList();
    }
}
