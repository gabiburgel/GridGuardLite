import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        RedeEletrica rede = new RedeEletrica();

        Classificador classificador = new Classificador();

        int opcao = 0;

        while(opcao != 3) {

            System.out.println();
            System.out.println("=== GRIDGUARD LITE ===");
            System.out.println("1 - Treinar modelo");
            System.out.println("2 - Classificar transformador");
            System.out.println("3 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = teclado.nextInt();

            switch(opcao) {

                case 1:

                    classificador.treinar(rede);

                    break;

                case 2:

                    double temperatura;
                    int idade;
                    double carga;
                    double tensao;

                    System.out.println();
                    System.out.println("Informe os dados do transformador:");

                    System.out.print("Temperatura: ");
                    temperatura = teclado.nextDouble();

                    System.out.print("Idade: ");
                    idade = teclado.nextInt();

                    System.out.print("Carga: ");
                    carga = teclado.nextDouble();

                    System.out.print("Tensão: ");
                    tensao = teclado.nextDouble();

                    classificador.classificar(
                            temperatura,
                            idade,
                            carga,
                            tensao
                    );

                    break;

                case 3:

                    System.out.println("Programa encerrado.");

                    break;

                default:

                    System.out.println("Opção inválida.");
            }
        }

        teclado.close();
    }
}
