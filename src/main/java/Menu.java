import farmacia.Producto;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Menu {
    private final static FarmaciaService service = new FarmaciaService();
    private final static Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        int opcion = 0;
        System.out.println("\n======= Menú de la Farmacia =======");
        do {
            System.out.println("\n1. Agregar un producto al inventario");
            System.out.println("2. Modificar producto en inventario");
            System.out.println("3. Comprar productos");
            System.out.println("4. Vender productos");
            System.out.println("5. Mostrar existencias de productos");
            System.out.println("6. Mostrar todos los productos");
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
        if (tipo == 6) {
            tipo = 0;
        }
        if (tipo <= 0 || tipo > 6) {
            System.out.println("Opcion no valida");
            return;
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
        System.out.print("\nIngrese el numero del producto que desea modificar: ");
        int index = teclado.nextInt();
        teclado.nextLine();
        if (index <= 0 || index > size) {
            System.out.println("Debes seleccionar un numero valido");
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
            System.out.print("Ingrese el numero de lo que desea modificar: ");
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
                    if (tipo == 6) {
                        tipo = 0;
                    }
                    if (tipo <= 0 || tipo > 6) {
                        System.out.println("Opcion no valida");
                        return;
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
        // Aquí va la lógica para comprar un producto
    }

    private static void vender() {
        // Aquí va la lógica para vender un producto
    }

    private static void mostrarExistenciasDeProductos() {
        // Aquí va la lógica para mostrar las existencias de los productos
    }

    private static int mostrarProductos() {
        List<Producto> productos = service.obtenerProductos();
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
}
