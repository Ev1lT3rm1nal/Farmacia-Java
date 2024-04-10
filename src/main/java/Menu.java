
// Importaciones de clases necesarias para el funcionamiento del menú.
import farmacia.Operacion;
import farmacia.Producto;
import farmacia.ProductoOperacion;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

// Define la clase Menu que contiene toda la lógica para interactuar con el usuario.
public class Menu {
    // Instancia de FarmaciaService para acceder a las funcionalidades del sistema
    // de la farmacia.
    private final static FarmaciaService service = new FarmaciaService();
    // Scanner para leer la entrada del usuario desde la consola.
    private final static Scanner teclado = new Scanner(System.in);

    // Método principal que ejecuta el menú interactivo de la farmacia.
    public static void main(String[] args) throws Exception {
        int opcion = 8;
        System.out.println("\n======= Menú de la Farmacia =======");
        do {
            // Muestra las opciones disponibles del menú.
            System.out.println("\n1. Dar de alta producto");
            System.out.println("2. Modificar producto en inventario");
            System.out.println("3. Comprar productos");
            System.out.println("4. Vender productos");
            System.out.println("5. Mostrar existencias de productos");
            System.out.println("6. Mostrar todos los productos");
            System.out.println("7. Mostrar todo el historial de compra/ventas");
            System.out.println("0. Salir");
            System.out.print("\nSeleccione una opción: ");
            try {
                opcion = teclado.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Debes ingresar un número válido");
                continue;
            } finally {
                teclado.nextLine(); // Limpia el buffer del teclado.
            }
            switch (opcion) {
                case 1:
                    // Llama a la función para agregar un producto al inventario.
                    agregarProductoAlInventario();
                    break;
                case 2:
                    // Llama a la función para modificar un producto existente en el inventario.
                    alterarProductoEnInventario();
                    break;
                case 3:
                    // Llama a la función para realizar una compra de productos.
                    comprar();
                    break;
                case 4:
                    // Llama a la función para realizar una venta de productos.
                    vender();
                    break;
                case 5:
                    // Llama a la función para mostrar las existencias actuales de los productos.
                    mostrarExistenciasDeProductos();
                    break;
                case 6:
                    // Llama a la función para mostrar todos los productos en el inventario.
                    mostrarProductos();
                    break;
                case 7:
                    // Llama a la función para mostrar el historial de operaciones (compras y
                    // ventas).
                    mostrarHistorial();
                    break;
                case 0:
                    // Mensaje de salida del programa.
                    System.out.println("Saliendo...");
                    break;
                default:
                    // Manejo de opciones no válidas ingresadas por el usuario.
                    System.out.println("Opción no válida, por favor intente de nuevo.");
            }
        } while (opcion != 0); // Repite el menú hasta que el usuario decida salir.
        service.guardar(); // Guarda el estado actual de la farmacia al salir.
    }

    // Método para agregar un producto nuevo al inventario.
    private static void agregarProductoAlInventario() {
        // Solicita al usuario los detalles del nuevo producto.
        System.out.print("Ingrese el nombre del producto: ");
        String nombre = teclado.nextLine();
        System.out.print("Ingrese la formula del producto: ");
        String formula = teclado.nextLine();
        System.out.print("Ingrese el precio del producto: ");
        double precio = 0;
        try {
            precio = teclado.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Debes ingresar un número válido");
            return;
        } finally {
            teclado.nextLine(); // Limpia el buffer del teclado.
        }
        // Verifica que el precio sea válido.
        if (precio < 0) {
            System.out.println("El precio debe de ser mayor o igual a 0");
            return;
        }
        // Muestra las opciones de tipos de medicamento y solicita la selección.
        System.out.println("1. Tableta");
        System.out.println("2. Liquido");
        System.out.println("3. Crema");
        System.out.println("4. Capsula");
        System.out.println("5. Polvo");
        System.out.println("6. Otro");
        System.out.print("Ingrese el tipo del producto: ");
        int tipo = 0;
        try {
            tipo = teclado.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Debes ingresar un número válido");
            return;
        } finally {
            teclado.nextLine(); // Limpia el buffer del teclado.
        }
        // Verifica que el tipo seleccionado sea válido.
        if (tipo <= 0 || tipo > 6) {
            System.out.println("Opción no válida");
            return;
        }
        if (tipo == 6) {
            tipo = 0; // Ajusta el tipo a un valor neutro si se selecciona "Otro".
        }
        System.out.print("Ingrese la cantidad del producto: ");
        double cantidad = 0;
        try {
            cantidad = teclado.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Debes ingresar un número válido");
            return;
        } finally {
            teclado.nextLine(); // Limpia el buffer del teclado.
        }
        // Verifica que la cantidad sea válida.
        if (cantidad <= 0) {
            System.out.println("La cantidad debe de ser mayor a 0");
            return;
        }
        // Construye el producto con los datos ingresados y lo agrega al inventario.
        Producto producto = Producto.newBuilder()
                .setNombre(nombre)
                .setFormula(formula)
                .setPrecio(precio)
                .setTipo(Producto.TipoMedicamento.forNumber(tipo))
                .setCantidad(cantidad)
                .build();
        service.agregarProductoInventario(producto);
    }

    // Método para modificar un producto existente en el inventario.
    private static void alterarProductoEnInventario() {
        // Muestra los productos y verifica si hay productos para mostrar.
        int size = mostrarProductos();
        if (size == 0) {
            System.out.println("No hay productos que mostrar");
            return;
        }
        // Solicita al usuario seleccionar el producto a modificar.
        System.out.print("\nIngrese el número del producto que desea modificar: ");
        int index = 0;
        try {
            index = teclado.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Debes ingresar un número válido");
            return;
        } finally {
            teclado.nextLine(); // Limpia el buffer del teclado.
        }
        // Verifica que el índice seleccionado sea válido.
        if (index <= 0 || index > size) {
            System.out.println("Debes seleccionar un número válido");
            return;
        }
        // Obtiene el producto a modificar.
        Producto.Builder producto = service.obtenerProducto(index).toBuilder();

        int opcion = 0;
        // Muestra las opciones de modificación disponibles para el producto
        // seleccionado.
        do {
            System.out.println("\n1. Nombre");
            System.out.println("2. Formula");
            System.out.println("3. Precio");
            System.out.println("4. Tipo");
            System.out.println("5. Cantidad");
            System.out.println("0. Guardar");
            System.out.print("Ingrese el número de lo que desea modificar: ");
            try {
                opcion = teclado.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Debes ingresar un número válido");
                continue;
            } finally {
                teclado.nextLine(); // Limpia el buffer del teclado.
            }
            switch (opcion) {
                case 1:
                    // Modifica el nombre del producto.
                    System.out.print("Ingrese el nombre del producto: ");
                    String nombre = teclado.nextLine();
                    producto.setNombre(nombre);
                    break;
                case 2:
                    // Modifica la fórmula del producto.
                    System.out.print("Ingrese la formula del producto: ");
                    String formula = teclado.nextLine();
                    producto.setFormula(formula);
                    break;
                case 3:
                    // Modifica el precio del producto.
                    System.out.print("Ingrese el precio del producto: ");
                    double precio = 0;
                    try {
                        precio = teclado.nextDouble();
                    } catch (InputMismatchException e) {
                        System.out.println("Debes ingresar un número válido");
                        return;
                    } finally {
                        teclado.nextLine(); // Limpia el buffer del teclado.
                    }
                    if (precio < 0) {
                        System.out.println("El precio debe de ser mayor o igual a 0");
                        return;
                    }
                    producto.setPrecio(precio);
                    break;
                case 4:
                    // Modifica el tipo de medicamento del producto.
                    System.out.println("1. Tableta");
                    System.out.println("2. Liquido");
                    System.out.println("3. Crema");
                    System.out.println("4. Capsula");
                    System.out.println("5. Polvo");
                    System.out.println("6. Otro");
                    System.out.print("Ingrese el tipo del producto: ");
                    int tipo = 0;
                    try {
                        tipo = teclado.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Debes ingresar un número válido");
                        continue;
                    } finally {
                        teclado.nextLine(); // Limpia el buffer del teclado.
                    }
                    if (tipo <= 0 || tipo > 6) {
                        System.out.println("Opción no válida");
                        return;
                    }
                    if (tipo == 6) {
                        tipo = 0; // Ajusta el tipo a un valor neutro si se selecciona "Otro".
                    }
                    producto.setTipo(Producto.TipoMedicamento.forNumber(tipo));
                    break;
                case 5:
                    // Modifica la cantidad disponible del producto.
                    System.out.print("Ingrese la cantidad del producto: ");
                    double cantidad = 0;
                    try {
                        cantidad = teclado.nextDouble();
                    } catch (InputMismatchException e) {
                        System.out.println("Debes ingresar un número válido");
                        return;
                    } finally {
                        teclado.nextLine(); // Limpia el buffer del teclado.
                    }
                    if (cantidad <= 0) {
                        System.out.println("La cantidad debe de ser mayor a 0");
                        return;
                    }
                    producto.setCantidad(cantidad);
                    break;
                case 0:
                    // Guarda los cambios realizados al producto en el inventario.
                    service.alterarProductoInventario(producto.build());
                    break;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        } while (opcion != 0); // Repite hasta que el usuario decida guardar los cambios.

    }

    // Método para simular la compra de productos.
    private static void comprar() {
        List<ProductoOperacion> productos = new ArrayList<>();
        // Muestra los productos disponibles para la compra.
        int size = mostrarProductos();

        if (size == 0) {
            return; // Sale si no hay productos disponibles.
        }

        int opcion = 0;

        labeled: do {
            System.out.println("\n1. Agregar al carrito");
            System.out.println("2. Buscar");
            System.out.println("3. Cancelar");
            System.out.println("0. Comprar");
            System.out.print("Ingrese una opción: ");
            try {
                opcion = teclado.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Debes ingresar un número válido");
                continue;
            } finally {
                teclado.nextLine(); // Limpia el buffer del teclado.
            }
            switch (opcion) {
                case 1:
                    // Permite al usuario agregar un producto al carrito de compras.
                    System.out.print("Ingrese el id el producto que desea comprar: ");
                    int id = 0;
                    try {
                        id = teclado.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Debes ingresar un número válido");
                        continue;
                    } finally {
                        teclado.nextLine(); // Limpia el buffer del teclado.
                    }
                    if (id <= 0 || id > size) {
                        System.out.println("Id no válido");
                        continue; // Repite el ciclo si el índice no es válido.
                    }
                    // Obtiene el producto seleccionado.
                    Producto producto = service.obtenerProducto(id);
                    System.out.print("Ingrese la cantidad a comprar: ");
                    Long cantidad = 0L;
                    try {
                        cantidad = teclado.nextLong();
                    } catch (InputMismatchException e) {
                        System.out.println("Debes ingresar un número válido");
                        continue;
                    } finally {
                        teclado.nextLine(); // Limpia el buffer del teclado.
                    }
                    if (cantidad <= 0) {
                        System.out.println("La cantidad debe de ser mayor que 0");
                        continue; // Repite el ciclo si la cantidad no es válida.
                    }
                    // Agrega el producto seleccionado al carrito de compras.
                    productos.add(service.agregarProducto(producto, cantidad));
                    break;
                case 2:
                    producto = buscarProducto();
                    if (producto != null) {
                        System.out.print("Ingrese la cantidad a comprar: ");
                        cantidad = 0L;
                        try {
                            cantidad = teclado.nextLong();
                        } catch (InputMismatchException e) {
                            System.out.println("Debes ingresar un número válido");
                            continue;
                        } finally {
                            teclado.nextLine(); // Limpia el buffer del teclado.
                        }
                        if (cantidad <= 0) {
                            System.out.println("La cantidad debe de ser mayor que 0");
                            continue; // Repite el ciclo si la cantidad no es válida.
                        }
                        productos.add(service.agregarProducto(producto, cantidad));
                    }
                    break;
                case 3:
                    // Permite al usuario cancelar la compra.
                    System.out.print("¿Estás seguro que desea continuar? (S/N): ");
                    String respuesta = teclado.nextLine();
                    if (respuesta.equalsIgnoreCase("s")) {
                        break labeled; // Sale del ciclo si el usuario confirma la cancelación.
                    }
                    break;
                case 0:
                    // Finaliza la compra si el carrito no está vacío y el usuario confirma.
                    if (productos.isEmpty()) {
                        System.out.println("El carrito esta vacío");
                        continue; // Repite el ciclo si el carrito está vacío.
                    }
                    System.out.print("¿Estás seguro que desea continuar? (S/N): ");
                    respuesta = teclado.nextLine();
                    if (respuesta.equalsIgnoreCase("s")) {
                        service.comprarProducto(productos); // Realiza la compra de los productos en el carrito.
                        break labeled; // Sale del ciclo una vez completada la compra.
                    }
                    break;
            }
        } while (opcion != 3); // Repite hasta que el usuario decida cancelar o completar la compra.

    }

    // Método para simular la venta de productos.
    private static void vender() {
        List<ProductoOperacion> productos = new ArrayList<>();
        // Muestra los productos con sus existencias para la venta.
        List<Entry<Producto, Long>> products = mostrarExistenciasDeProductos();

        List<Long> keys = products.parallelStream().map(element -> element.getKey().getId())
                .collect(Collectors.toList());

        if (products.isEmpty()) {
            return; // Sale si no hay productos disponibles para la venta.
        }

        int opcion = 0;

        labeled: do {
            System.out.println("\n1. Agregar al carrito");
            System.out.println("2. Buscar");
            System.out.println("3. Cancelar");
            System.out.println("0. Vender");
            System.out.print("Ingrese una opción: ");
            try {
                opcion = teclado.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Debes ingresar un número válido");
                continue;
            } finally {
                teclado.nextLine(); // Limpia el buffer del teclado.
            }
            switch (opcion) {
                case 1:
                    // Permite al usuario agregar un producto al carrito de ventas.
                    System.out.print("Ingrese el id el producto que desea vender: ");
                    Long id = 0L;
                    try {
                        id = teclado.nextLong();
                    } catch (InputMismatchException e) {
                        System.out.println("Debes ingresar un número válido");
                        continue;
                    } finally {
                        teclado.nextLine(); // Limpia el buffer del teclado.
                    }
                    if (keys.contains(id)) {
                        System.out.println("Id no válido");
                        continue; // Repite el ciclo si el índice no es válido.
                    }
                    // Obtiene el producto seleccionado.
                    Long finalId = id;
                    Entry<Producto, Long> producto_map = products.stream()
                            .filter(element -> element.getKey().getId() == finalId).findFirst().get();
                    System.out.print("Ingrese la cantidad a vender: ");
                    Long cantidad = 0L;
                    try {
                        cantidad = teclado.nextLong();
                    } catch (InputMismatchException e) {
                        System.out.println("Debes ingresar un número válido");
                        continue;
                    } finally {
                        teclado.nextLine(); // Limpia el buffer del teclado.
                    }
                    if (cantidad <= 0) {
                        System.out.println("La cantidad debe de ser mayor que 0");
                        continue; // Repite el ciclo si la cantidad no es válida.
                    }
                    if (cantidad > producto_map.getValue()) {
                        System.out.println("Existencias insuficientes");
                        continue; // Repite el ciclo si no hay suficientes existencias para la venta.
                    }
                    // Agrega el producto seleccionado al carrito de ventas.
                    productos.add(service.agregarProducto(producto_map.getKey(), cantidad));
                    break;
                case 2:
                    Producto producto = buscarProducto();
                    if (producto != null) {
                        System.out.print("Ingrese la cantidad a vender: ");
                        cantidad = 0L;
                        try {
                            cantidad = teclado.nextLong();
                        } catch (InputMismatchException e) {
                            System.out.println("Debes ingresar un número válido");
                            continue;
                        } finally {
                            teclado.nextLine(); // Limpia el buffer del teclado.
                        }
                        if (cantidad <= 0) {
                            System.out.println("La cantidad debe de ser mayor que 0");
                            continue; // Repite el ciclo si la cantidad no es válida.
                        }
                        if (cantidad > service.obtenerExistenciasProducto(producto.getId())) {
                            System.out.println("Existencias insuficientes");
                            continue; // Repite el ciclo si no hay suficientes existencias para la venta.
                        }
                        productos.add(service.agregarProducto(producto, cantidad));
                    }
                    break;
                case 3:
                    // Permite al usuario cancelar la venta.
                    System.out.print("¿Estás seguro que desea continuar? (S/N): ");
                    String respuesta = teclado.nextLine();
                    if (respuesta.equalsIgnoreCase("s")) {
                        break labeled; // Sale del ciclo si el usuario confirma la cancelación.
                    }
                    break;
                case 0:
                    // Finaliza la venta si el carrito no está vacío y el usuario confirma.
                    if (productos.isEmpty()) {
                        System.out.println("El carrito esta vacío");
                        continue; // Repite el ciclo si el carrito está vacío.
                    }
                    System.out.print("¿Estás seguro que desea continuar? (S/N): ");
                    respuesta = teclado.nextLine();
                    if (respuesta.equalsIgnoreCase("s")) {
                        System.out.print("¿Deseas generar factura? (S/N): ");
                        String factura_respuesta = teclado.nextLine();
                        boolean factura = factura_respuesta.equalsIgnoreCase("s");
                        service.venderProducto(productos, factura); // Realiza la venta de los productos en el carrito.
                        break labeled; // Sale del ciclo una vez completada la venta.
                    }

                    break;
            }
        } while (opcion != 3); // Repite hasta que el usuario decida cancelar o completar la venta.

    }

    private static Producto buscarProducto() {
        System.out.println("\n1. Buscar por nombre");
        System.out.println("2. Buscar por formula");
        System.out.println("3. Buscar por id");
        System.out.print("Ingrese método de búsqueda: ");
        int metodo = 0;
        try {
            metodo = teclado.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Debes ingresar un número válido");
            return null;
        } finally {
            teclado.nextLine(); // Limpia el buffer del teclado.
        }
        if (metodo <= 0 || metodo > 3) {
            System.out.println("Debes ingresar un número dentro del rango de selección");
            return null;
        }

        List<Producto> productos = List.of();

        switch (metodo) {
            case 1:
                System.out.print("Ingrese el nombre del producto: ");
                String nombre = teclado.nextLine();
                productos = service.buscarProductosNombre(nombre);
                break;

            case 2:
                System.out.print("Ingrese la formula del producto: ");
                String formula = teclado.nextLine();
                productos = service.buscarProductosFormula(formula);
                break;

            case 3:
                System.out.print("Ingrese el id del producto: ");
                Long id = 0L;
                try {
                    id = teclado.nextLong();
                } catch (InputMismatchException e) {
                    System.out.println("Debes ingresar un número válido");
                    return null;
                } finally {
                    teclado.nextLine(); // Limpia el buffer del teclado.
                }
                Optional<Producto> producto = service.buscarProductosId(id);
                if (producto.isPresent()) {
                    return producto.get();
                } else {
                    System.out.println("No se encontró ningún producto");
                    return null;
                }
        }
        if (productos.isEmpty()) {
            System.out.println("No se encontró ningún producto");
            return null;
        } else {
            imprimirProductos(productos);
            System.out.print("Ingrese el id del producto: ");
            Long id = 0L;
            try {
                id = teclado.nextLong();
            } catch (InputMismatchException e) {
                System.out.println("Debes ingresar un número válido");
                return null;
            } finally {
                teclado.nextLine(); // Limpia el buffer del teclado.
            }
            Optional<Producto> producto = service.buscarProductosId(id);
            if (producto.isPresent()) {
                return producto.get();
            } else {
                System.out.println("No se encontró ningún producto");
                return null;
            }
        }
    }

    // Método para mostrar las existencias de los productos disponibles para la
    // venta.
    private static List<Entry<Producto, Long>> mostrarExistenciasDeProductos() {
        // Obtiene la lista de productos con sus existencias.
        List<Entry<Producto, Long>> productos = new ArrayList<>(service.obtenerProductosExistencias().entrySet());
        if (productos.isEmpty()) {
            System.out.println("No hay productos que mostrar");
            return productos; // Retorna la lista vacía si no hay productos.
        }
        // Muestra los detalles de cada producto junto con sus existencias.
        productos.forEach((element) -> {
            Producto product = element.getKey();

            System.out.println("\n--------------- Producto " + product.getId() + " --------------");
            System.out.println("\tExistencias: " + element.getValue());
            System.out.println("\tNombre: " + product.getNombre());
            System.out.println("\tFormula: " + product.getFormula());
            System.out.println("\tPrecio: " + product.getPrecio());
            System.out.println("\tTipo: " + product.getTipo().toString().split("_")[2]);
            System.out.println("\tCantidad: " + product.getCantidad());
            System.out.println("-----------------------------------------");
        });
        return productos; // Retorna la lista de productos con sus existencias.
    }

    // Método para mostrar todos los productos en el inventario.
    private static int mostrarProductos() {
        // Obtiene la lista de todos los productos en el inventario.
        List<Producto> productos = service.obtenerProductos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos que mostrar");
            return 0; // Retorna cero si no hay productos.
        }
        // Muestra los detalles de cada producto.
        imprimirProductos(productos);

        return productos.size(); // Retorna el número de productos mostrados.
    }

    private static void imprimirProductos(List<Producto> productos) {
        productos.forEach(element -> {
            System.out.println("\n--------------- Producto " + element.getId() + " --------------");
            System.out.println("\tNombre: " + element.getNombre());
            System.out.println("\tFormula: " + element.getFormula());
            System.out.println("\tPrecio: " + element.getPrecio());
            System.out.println("\tTipo: " + element.getTipo().toString().split("_")[2]);
            System.out.println("\tCantidad: " + element.getCantidad());
            System.out.println("-----------------------------------------");
        });
    }

    // Método para mostrar el historial de compras y ventas realizadas.
    private static void mostrarHistorial() {
        // Obtiene el historial de operaciones.
        List<Operacion> historial = service.obtenerHistorial();
        if (historial.isEmpty()) {
            System.out.println("No hay historial");
            return; // Sale si no hay historial.
        }
        // Muestra los detalles de cada operación en el historial.
        historial.forEach(operacion -> {
            System.out.println("\n----------------------------------------------------------");
            System.out.println("    Fecha: " + operacion.getFecha());
            System.out.println("    Tipo: " + operacion.getTipo().toString().split("_")[2]);
            System.out.print("    Total: " + operacion.getTotal());
            if (operacion.getFactura()) {
                System.out.print(" + IVA = " + operacion.getTotal() * 1.16);
            }
            System.out.println("\n    Factura: " + (operacion.getFactura() ? "Si" : "No"));
            System.out.println("    ================== Productos ==================");
            operacion.getProductosList().forEach(producto -> {
                System.out.println("\tNombre: " + producto.getNombre() + ", cantidad: " + producto.getCantidad() + " = "
                        + producto.getCantidad() * producto.getPrecioEnOperacion());
            });
            System.out.println("    ===============================================");
            System.out.println("\n----------------------------------------------------------");
        });
    }
}
