import farmacia.Operacion;
import farmacia.Producto;
import farmacia.ProductoOperacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Menu {
    private final static FarmaciaService service = new FarmaciaService();
    private final static Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        int opcion = 0;
        System.out.println("\n======= Menú de la Farmacia =======");
        do {
            System.out.println("\n1. Dar de alta producto");
            System.out.println("2. Modificar producto en inventario");
            System.out.println("3. Comprar productos");
            System.out.println("4. Vender productos");
            System.out.println("5. Mostrar existencias de productos");
            System.out.println("6. Mostrar todos los productos");
            System.out.println("7. Mostrar todo el historial de compra/ventas");
            System.out.println("0. Salir");
            System.out.print("\nSeleccione una opción: ");
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    // Implementar lógica para agregar un producto al inventario
                    agregarProductoAlInventario();
                    break;
                case 2:
                    // Implementar lógica para alterar producto en inventario
                    alterarProductoEnInventario();
                    break;
                case 3:
                    // Implementar lógica para comprar producto
                    comprar();
                    break;
                case 4:
                    // Implementar lógica para vender producto
                    vender();
                    break;
                case 5:
                    // Implementar lógica para mostrar existencias de productos
                    mostrarExistenciasDeProductos();
                    break;
                case 6:
                    // Implementar lógica para mostrar existencias de productos
                    mostrarProductos();
                    break;
                case 7:
                    mostrarHistorial();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida, por favor intente de nuevo.");
            }
        } while (opcion != 0);
        service.guardar();
    }

    private static void agregarProductoAlInventario() throws Exception {
        System.out.print("Ingrese el nombre del producto: ");
        String nombre = teclado.nextLine();
        System.out.print("Ingrese la formula del producto: ");
        String formula = teclado.nextLine();
        System.out.print("Ingrese el precio del producto: ");
        double precio = teclado.nextDouble();
        teclado.nextLine();
        if (precio < 0) {
            System.out.println("El precio debe de ser mayor o igual a 0");
            return;
        }
        System.out.println("1. Tableta");
        System.out.println("2. Liquido");
        System.out.println("3. Crema");
        System.out.println("4. Capsula");
        System.out.println("5. Polvo");
        System.out.println("6. Otro");
        System.out.print("Ingrese el tipo del producto: ");
        int tipo = teclado.nextInt();
        teclado.nextLine();
        if (tipo <= 0 || tipo > 6) {
            System.out.println("Opción no válida");
            return;
        }
        if (tipo == 6) {
            tipo = 0;
        }
        System.out.print("Ingrese la cantidad del producto: ");
        double cantidad = teclado.nextDouble();
        if (cantidad <= 0) {
            System.out.println("La cantidad debe de ser mayor a 0");
            return;
        }
        Producto producto = Producto.newBuilder()
                .setNombre(nombre)
                .setFormula(formula)
                .setPrecio(precio)
                .setTipo(Producto.TipoMedicamento.forNumber(tipo))
                .setCantidad(cantidad)
                .build();
        service.agregarProductoInventario(producto);
    }

    private static void alterarProductoEnInventario() {
        int size = mostrarProductos();
        if (size == 0) {
            System.out.println("No hay productos que mostrar");
            return;
        }
        System.out.print("\nIngrese el número del producto que desea modificar: ");
        int index = teclado.nextInt();
        teclado.nextLine();
        if (index <= 0 || index > size) {
            System.out.println("Debes seleccionar un número válido");
            return;
        }
        Producto.Builder producto = service.obtenerProducto(index - 1).toBuilder();

        int opcion = 0;

        do {
            System.out.println("\n1. Nombre");
            System.out.println("2. Formula");
            System.out.println("3. Precio");
            System.out.println("4. Tipo");
            System.out.println("5. Cantidad");
            System.out.println("0. Guardar");
            System.out.print("Ingrese el número de lo que desea modificar: ");
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el nombre del producto: ");
                    String nombre = teclado.nextLine();
                    producto.setNombre(nombre);
                    break;
                case 2:
                    System.out.print("Ingrese la formula del producto: ");
                    String formula = teclado.nextLine();
                    producto.setFormula(formula);
                    break;
                case 3:
                    System.out.print("Ingrese el precio del producto: ");
                    double precio = teclado.nextDouble();
                    teclado.nextLine();
                    if (precio < 0) {
                        System.out.println("El precio debe de ser mayor o igual a 0");
                        return;
                    }
                    producto.setPrecio(precio);
                    break;
                case 4:
                    System.out.println("1. Tableta");
                    System.out.println("2. Liquido");
                    System.out.println("3. Crema");
                    System.out.println("4. Capsula");
                    System.out.println("5. Polvo");
                    System.out.println("6. Otro");
                    System.out.print("Ingrese el tipo del producto: ");
                    int tipo = teclado.nextInt();
                    teclado.nextLine();
                    if (tipo <= 0 || tipo > 6) {
                        System.out.println("Opción no válida");
                        return;
                    }
                    if (tipo == 6) {
                        tipo = 0;
                    }
                    producto.setTipo(Producto.TipoMedicamento.forNumber(tipo));
                    break;
                case 5:
                    System.out.print("Ingrese la cantidad del producto: ");
                    double cantidad = teclado.nextDouble();
                    if (cantidad <= 0) {
                        System.out.println("La cantidad debe de ser mayor a 0");
                        return;
                    }
                    producto.setCantidad(cantidad);
                    break;
                case 0:
                    service.alterarProductoInventario(producto.build());
                    break;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        } while (opcion != 0);

    }

    private static void comprar() {
        List<ProductoOperacion> productos = new ArrayList<>();
        int size = mostrarProductos();

        if (size == 0) {
            return;
        }

        int opcion = 0;

        labeled: do {
            System.out.println("\n1. Agregar al carrito");
            System.out.println("2. Cancelar");
            System.out.println("0. Comprar");
            System.out.print("Ingrese una opción: ");
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el índice el producto que desea comprar: ");
                    int indice = teclado.nextInt();
                    teclado.nextLine();
                    if (indice <= 0 || indice > size) {
                        System.out.println("Índice no válido");
                        continue;
                    }
                    Producto producto = service.obtenerProducto(indice - 1);
                    System.out.print("Ingrese la cantidad a comprar: ");
                    int cantidad = teclado.nextInt();
                    teclado.nextLine();
                    if (cantidad <= 0) {
                        System.out.println("La cantidad debe de ser mayor que 0");
                        continue;
                    }
                    productos.add(service.agregarProducto(producto, cantidad));
                    break;
                case 2:
                    System.out.print("¿Estás seguro que desea continuar? (S/N): ");
                    String respuesta = teclado.nextLine();
                    if (respuesta.equalsIgnoreCase("s")) {
                        break labeled;
                    }
                    break;
                case 0:
                    if (productos.isEmpty()) {
                        System.out.println("El carrito esta vacío");
                        continue;
                    }
                    System.out.print("¿Estás seguro que desea continuar? (S/N): ");
                    respuesta = teclado.nextLine();
                    if (respuesta.equalsIgnoreCase("s")) {
                        service.comprarProducto(productos);
                        break labeled;
                    }
                    break;
            }
        } while (opcion != 2);

    }

    private static void vender() {
        List<ProductoOperacion> productos = new ArrayList<>();
        List<Entry<Producto, Long>> products = mostrarExistenciasDeProductos();

        if (products.isEmpty()) {
            return;
        }

        int opcion = 0;

        labeled: do {
            System.out.println("\n1. Agregar al carrito");
            System.out.println("2. Cancelar");
            System.out.println("0. Vender");
            System.out.print("Ingrese una opción: ");
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el índice el producto que desea vender: ");
                    int indice = teclado.nextInt();
                    teclado.nextLine();
                    if (indice <= 0 || indice > products.size()) {
                        System.out.println("Índice no válido");
                        continue;
                    }
                    Producto producto = products.get(indice - 1).getKey();
                    System.out.print("Ingrese la cantidad a vender: ");
                    int cantidad = teclado.nextInt();
                    teclado.nextLine();
                    if (cantidad <= 0) {
                        System.out.println("La cantidad debe de ser mayor que 0");
                        continue;
                    }
                    if (cantidad > products.get(indice - 1).getValue()) {
                        System.out.println("Existencias insuficientes");
                        continue;
                    }
                    productos.add(service.agregarProducto(producto, cantidad));
                    break;
                case 2:
                    System.out.print("¿Estás seguro que desea continuar? (S/N): ");
                    String respuesta = teclado.nextLine();
                    if (respuesta.equalsIgnoreCase("s")) {
                        break labeled;
                    }
                    break;
                case 0:
                    if (productos.isEmpty()) {
                        System.out.println("El carrito esta vacío");
                        continue;
                    }
                    System.out.print("¿Estás seguro que desea continuar? (S/N): ");
                    respuesta = teclado.nextLine();
                    if (respuesta.equalsIgnoreCase("s")) {
                        service.venderProducto(productos);
                        break labeled;
                    }
                    break;
            }
        } while (opcion != 2);
    }

    private static List<Entry<Producto, Long>> mostrarExistenciasDeProductos() {
        List<Map.Entry<Producto, Long>> productos = new ArrayList<>(service.obtenerProductosExistencias().entrySet());
        if (productos.isEmpty()) {
            System.out.println("No hay productos que mostrar");
            return productos;
        }
        AtomicInteger index = new AtomicInteger(0);
        productos.forEach((element) -> {
            Producto product = element.getKey();

            System.out.println("\n--------------- Producto " + index.incrementAndGet() + " --------------");
            System.err.println("\tExistencias: " + element.getValue());
            System.err.println("\tNombre: " + product.getNombre());
            System.err.println("\tFormula: " + product.getFormula());
            System.err.println("\tPrecio: " + product.getPrecio());
            System.err.println("\tTipo: " + product.getTipo());
            System.err.println("\tCantidad: " + product.getCantidad());
            System.out.println("-----------------------------------------");
        });
        return productos;
    }

    private static int mostrarProductos() {
        List<Producto> productos = service.obtenerProductos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos que mostrar");
            return 0;
        }
        AtomicInteger index = new AtomicInteger(0);
        productos.forEach(element -> {
            System.out.println("\n--------------- Producto " + index.incrementAndGet() + " --------------");
            System.err.println("\tNombre: " + element.getNombre());
            System.err.println("\tFormula: " + element.getFormula());
            System.err.println("\tPrecio: " + element.getPrecio());
            System.err.println("\tTipo: " + element.getTipo());
            System.err.println("\tCantidad: " + element.getCantidad());
            System.out.println("-----------------------------------------");
        });
        return productos.size();
    }

    private static void mostrarHistorial() {
        List<Operacion> historial = service.obtenerHistorial();
        if (historial.isEmpty()) {
            System.out.println("No hay historial");
            return;
        }
        historial.forEach(operacion -> {
            System.out.println("\n----------------------------------------------------------");
            System.out.println("    Fecha: " + operacion.getFecha());
            System.out.println("    Tipo: " + operacion.getTipo());
            System.out.println("    Total: " + operacion.getTotal());
            System.out.println("    ================== Productos ==================");
            operacion.getProductosList().forEach(producto -> {
                System.out.println("\tNombre: " + producto.getNombre() + ", cantidad: " + producto.getCantidad());
            });
            System.out.println("    ===============================================");
            System.out.println("\n----------------------------------------------------------");
        });
    }
}
