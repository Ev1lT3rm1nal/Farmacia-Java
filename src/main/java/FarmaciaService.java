import farmacia.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class FarmaciaService {
    private final Farmacia.Builder farmacia;

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    static final String filePath = "farmacia_data.dat";

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

    public void guardar() {
        try {
            Files.write(Paths.get(filePath), farmacia.build().toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void agregarProductoInventario(Producto producto) {
        Inventario inventario = farmacia.getInventario();
        long id = inventario.getUltimoIdUsado() + 1;
        // Agrega el nuevo producto al inventario al cual se le asigna un id para poderlo indexar
        farmacia.getInventarioBuilder().addProductos(producto.toBuilder().setId(id)).setUltimoIdUsado(id);
    }

    public Producto obtenerProducto(int index) {
        return farmacia.getInventario().getProductos(index);
    }

    public void alterarProductoInventario(Producto producto) {
        Inventario inventario = farmacia.getInventario();
        List<Producto> productos = inventario.getProductosList();
        List<Producto> productos_nuevos = productos.parallelStream().filter(element -> element.getId() != producto.getId()).collect(Collectors.toList());
        productos_nuevos.add(producto);
        farmacia.setInventario(inventario.toBuilder().clearProductos().addAllProductos(productos_nuevos));
    }

    public void comprarProducto(List<ProductoOperacion> productos) {
        operacionProducto(productos, Operacion.TipoOperacion.TIPO_OPERACION_COMPRA);
    }

    public void venderProducto(List<ProductoOperacion> productos) {
        operacionProducto(productos, Operacion.TipoOperacion.TIPO_OPERACION_VENTA);
    }

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
            farmacia.getInventarioBuilder().putExistencias(existencias.get(productoOperacion.getIdProducto()), productoOperacion.getCantidad());
        });

        farmacia.addHistorialOperaciones(operacion);
    }

    public Map<Producto, Long> obtenerProductosExistencias() {
        Map<Producto, Long> productos = new HashMap<>();
        List<Producto> productosList = farmacia.getInventario().getProductosList();
        Map<Long, Long> existencias = farmacia.getInventario().getExistenciasMap();

        existencias.forEach((key, value) -> productosList.parallelStream().filter(elemento -> elemento.getId() == key).findFirst().ifPresent(producto1 -> productos.put(producto1, value)));
        return productos;
    }

    public List<Producto> obtenerProductos() {
        return farmacia.getInventario().getProductosList();
    }

    public ProductoOperacion agregarProducto(Producto producto, long cantidad) {
        return ProductoOperacion.newBuilder()
                .setCantidad(cantidad)
                .setIdProducto(producto.getId())
                .setPrecioEnOperacion(producto.getPrecio())
                .build();
    }
}
