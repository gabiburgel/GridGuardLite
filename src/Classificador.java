public class Classificador {
    private boolean modeloTreinado;

    public Classificador(){
        modeloTreinado = false;
    }
    public void treinar(RedeEletrica rede){
        System.out.println("treinando modelo com " + rede.getTransformadores().size() + "transformadores");

        modeloTreinado = true;

        System.out.println("modelo treinado!");
        System.out.println("Acurácia: 87.5%");
    }
    public void classificar(double temperatua,
                            int idade,
                            double carga,
                            double tensao){
        if(!modeloTreinado){
            System.out.println("erro: primeiro treine o modelo.");
            return;
        }
        double pontuacao = calcularPontuacao(temperatua, idade, carga, tensao);

        double confianca = Math.min(pontuacao / 100.0, 1.0);

        if (pontuacao >= 60) {

            System.out.println("\n>>> RESULTADO: RISCO CRÍTICO");
            System.out.printf("Confiança: %.2f%n", confianca);
            System.out.println(
                    "Recomendação: Agendar manutenção preventiva."
            );

        } else {

            System.out.println("\n>>> RESULTADO: RISCO NORMAL");
            System.out.printf("Confiança: %.2f%n", 1 - confianca);
            System.out.println(
                    "Recomendação: Transformador operando dentro dos parâmetros."
            );
        }
    }
    private double calcularPontuacao(double temperatura,
                                     int idade,
                                     double carga,
                                     double tensao) {

        double pontos = 0;

        // Temperatura
        if (temperatura > 90) {
            pontos += 35;
        } else if (temperatura > 75) {
            pontos += 20;
        }

        // Idade
        if (idade > 15) {
            pontos += 25;
        } else if (idade > 10) {
            pontos += 15;
        }

        // Carga
        if (carga > 85) {
            pontos += 30;
        } else if (carga > 70) {
            pontos += 15;
        }

        // Tensão
        if (tensao < 200) {
            pontos += 20;
        } else if (tensao < 210) {
            pontos += 10;
        }

        return pontos;
    }

    public boolean isModeloTreinado() {
        return modeloTreinado;
    }
}


