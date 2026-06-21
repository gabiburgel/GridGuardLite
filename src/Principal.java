import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        // Cria a rede elétrica (já carrega os 8 transformadores de exemplo)
        RedeEletrica rede = new RedeEletrica();

        // Cria o classificador (inicia o TensorFlow internamente)
        Classificador classificador = new Classificador();

        int opcao = 0;

        while (opcao != 3) {

            System.out.println();
            System.out.println("=== GRIDGUARD LITE - Preditor de Risco ===");
            System.out.println("1 - Treinar modelo com dados históricos");
            System.out.println("2 - Classificar novo transformador");
            System.out.println("3 - Sair");
            System.out.print("Opção: ");

            opcao = teclado.nextInt();

            switch (opcao) {

                case 1:
                    classificador.treinar(rede);
                    break;

                case 2:
                    System.out.println();
                    System.out.println("Informe os dados do transformador:");

                    System.out.print("Temperatura (°C) [30-120]: ");
                    double temperatura = teclado.nextDouble();

                    System.out.print("Idade (anos) [0-30]: ");
                    int idade = teclado.nextInt();

                    System.out.print("Carga (%) [20-100]: ");
                    double carga = teclado.nextDouble();

                    System.out.print("Tensão (V) [180-240]: ");
                    double tensao = teclado.nextDouble();

                    classificador.classificar(temperatura, idade, carga, tensao);
                    break;

                case 3:
                    // Libera os recursos do TensorFlow antes de sair
                    classificador.fechar();
                    System.out.println("Programa encerrado.");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }

        teclado.close();
    }
}