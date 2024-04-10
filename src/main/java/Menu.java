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
            System.out.println("3. Comprar producto");
            System.out.println("4. Vender producto");
            System.out.println("5. Mostrar existencias de productos");
            System.out.println("6. Mostrar todos los productos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
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
                    comprarProducto();
                    break;
                case 4:
                    // Implementar lógica para vender producto
                    venderProducto();
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
        System.out.println("Ingrese el nombre del producto");
        String nombre = teclado.nextLine();
        System.out.println("Ingrese la formula del producto");
        String formula = teclado.nextLine();
        System.out.println("Ingrese el precio del producto");
        double precio = teclado.nextDouble();
        if (precio < 0) {
            throw new Exception("El precio debe de ser mayor a 0");
        }
        teclado.nextLine();
        System.out.println("Ingrese el tipo del producto");
        System.out.println("1. Tableta");
        System.out.println("2. Liquido");
        System.out.println("3. Crema");
        System.out.println("4. Capsula");
        System.out.println("5. Polvo");
        System.out.println("6. Otro");
        int tipo = teclado.nextInt();
        teclado.nextLine();
        if (tipo == 6) {
            tipo = 0;
        }
        if (tipo < 0 || tipo > 6) {
            throw new Exception("Opcion no valida");
        }
        System.out.println("Ingrese la cantidad del producto");
        double cantidad = teclado.nextDouble();
        if (cantidad < 0) {
            throw new Exception("La cantidad debe de ser mayor a 0");
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
        mostrarProductos();
        System.out.println("Seleccione el indice");
    }

    private static void comprarProducto() {
        // Aquí va la lógica para comprar un producto
    }

    private static void venderProducto() {
        // Aquí va la lógica para vender un producto
    }

    private static void mostrarExistenciasDeProductos() {
        // Aquí va la lógica para mostrar las existencias de los productos
    }

    private static void mostrarProductos() {
        List<Producto> productos = service.getProductos();
        AtomicInteger index = new AtomicInteger(1);
        productos.forEach(element -> {
            System.out.println("--------------- Producto " + index + " --------------");
            System.err.println("\tNombre: " + element.getNombre());
            System.err.println("\tFormula: " + element.getFormula());
            System.err.println("\tPrecio: " + element.getPrecio());
            System.err.println("\tTipo: " + element.getTipo());
            System.err.println("\tCantidad: " + element.getCantidad());
            System.out.println("-----------------------------------------");
            index.getAndIncrement();
        });
    }
}
