import org.tensorflow.*;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Placeholder;
import org.tensorflow.types.TFloat32;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Classificador {

    // =====================================================
    // PESOS DA REDE NEURAL (guardados como arrays Java)
    // O TensorFlow usa esses pesos na hora de classificar
    // =====================================================
    private float[][] w1; // pesos: 4 entradas → 3 neurônios ocultos
    private float[]   b1; // vieses dos 3 neurônios ocultos
    private float[]   w2; // pesos: 3 neurônios ocultos → 1 saída
    private float     b2; // vies da saída

    // =====================================================
    // TENSORFLOW: grafo e sessão para a classificação
    //
    // Grafo = a receita das operações (matMul, sigmoid...)
    // Sessão = quem executa a receita com dados reais
    // =====================================================
    private Graph   grafo;
    private Session sessao;

    // Placeholders: "portas de entrada" do grafo TF
    // Recebem os valores reais só quando a sessão executa
    private Placeholder<TFloat32> phX;  // dados de entrada:      shape [1, 4]
    private Placeholder<TFloat32> phW1; // pesos camada oculta:   shape [4, 3]
    private Placeholder<TFloat32> phB1; // vieses camada oculta:  shape [1, 3]
    private Placeholder<TFloat32> phW2; // pesos camada de saída: shape [3, 1]
    private Placeholder<TFloat32> phB2; // vies de saída:         shape [1, 1]

    // Saída do grafo: probabilidade de ser Risco Crítico (valor entre 0 e 1)
    private Operand<TFloat32> saidaTF;

    private boolean modeloTreinado = false;

    // Número de vezes que o modelo vê todos os dados de treino
    private static final int   EPOCAS = 500;
    // Tamanho do passo de ajuste dos pesos a cada época
    private static final float TAXA   = 0.5f;

    // Limites para normalização: transforma tudo para o intervalo [0, 1]
    // Sem isso, a tensão (180-240) dominaria numericamente sobre a idade (0-30)
    private static final float TEMP_MIN   = 30f;   private static final float TEMP_MAX   = 120f;
    private static final float IDADE_MIN  = 0f;    private static final float IDADE_MAX  = 30f;
    private static final float CARGA_MIN  = 20f;   private static final float CARGA_MAX  = 100f;
    private static final float TENSAO_MIN = 180f;  private static final float TENSAO_MAX = 240f;

    public Classificador() {
        inicializarPesos();   // 1. pesos aleatórios iniciais
        construirGrafoTF();   // 2. monta a receita do TF para classificação
    }

    // =====================================================
    // INICIALIZAÇÃO DOS PESOS (técnica de Xavier)
    // Valores pequenos e aleatórios para o treino começar
    // sem que a sigmoide sature logo de início.
    // Semente 42: garante os mesmos valores toda execução.
    // =====================================================
    private void inicializarPesos() {
        Random rand = new Random(42);
        w1 = new float[4][3];
        b1 = new float[3]; // Java já inicializa arrays float com 0.0
        w2 = new float[3];
        b2 = 0f;

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 3; j++)
                w1[i][j] = (float)((rand.nextDouble() * 2 - 1) * Math.sqrt(2.0 / 4));

        for (int j = 0; j < 3; j++)
            w2[j] = (float)((rand.nextDouble() * 2 - 1) * Math.sqrt(2.0 / 3));
    }

    // Normaliza um valor para o intervalo [0, 1]
    private float normalizar(float valor, float min, float max) {
        return (valor - min) / (max - min);
    }

    // Função sigmoide: transforma qualquer número em valor entre 0 e 1
    // Usada como "ativação" dos neurônios no treinamento
    private float sigmoid(float x) {
        return (float)(1.0 / (1.0 + Math.exp(-x)));
    }

    // =====================================================
    // GRAFO TENSORFLOW — só para classificação
    //
    // Monta a estrutura das operações TF uma única vez.
    // Usa matMul (multiplicação de matrizes) e sigmoid,
    // que são as operações centrais de uma rede neural.
    // =====================================================
    private void construirGrafoTF() {
        grafo = new Graph();
        Ops tf = Ops.create(grafo);

        // Placeholders recebem os dados reais só quando classificar() roda
        phX  = tf.placeholder(TFloat32.class, Placeholder.shape(Shape.of(1, 4)));
        phW1 = tf.placeholder(TFloat32.class, Placeholder.shape(Shape.of(4, 3)));
        phB1 = tf.placeholder(TFloat32.class, Placeholder.shape(Shape.of(1, 3)));
        phW2 = tf.placeholder(TFloat32.class, Placeholder.shape(Shape.of(3, 1)));
        phB2 = tf.placeholder(TFloat32.class, Placeholder.shape(Shape.of(1, 1)));

        // Camada oculta: sigmoid( X × W1 + b1 )
        // TF faz a multiplicação de matrizes e a sigmoid automaticamente
        Operand<TFloat32> oculta = tf.math.sigmoid(
                tf.math.add(
                        tf.linalg.matMul(phX, phW1),
                        phB1
                )
        );

        // Camada de saída: sigmoid( H × W2 + b2 )
        // Resultado: número entre 0 (Normal) e 1 (Crítico)
        saidaTF = tf.math.sigmoid(
                tf.math.add(
                        tf.linalg.matMul(oculta, phW2),
                        phB2
                )
        );

        sessao = new Session(grafo);
    }

    // =====================================================
    // FORWARD PASS EM JAVA — usado no treinamento
    //
    // Calcula a saída da rede para uma entrada x[].
    // Retorna float[4] = { h[0], h[1], h[2], yPred }
    // onde h[] são as ativações ocultas e yPred é a saída.
    // =====================================================
    private float[] forwardJava(float[] x) {
        // Camada oculta: para cada neurônio j, soma entradas × pesos + vies
        float[] h = new float[3];
        for (int j = 0; j < 3; j++) {
            float soma = b1[j];
            for (int i = 0; i < 4; i++) soma += x[i] * w1[i][j];
            h[j] = sigmoid(soma);
        }

        // Camada de saída
        float soma = b2;
        for (int j = 0; j < 3; j++) soma += h[j] * w2[j];
        float yPred = sigmoid(soma);

        return new float[]{h[0], h[1], h[2], yPred};
    }

    // =====================================================
    // TREINAMENTO
    //
    // O modelo aprende ajustando os pesos para que suas
    // previsões fiquem cada vez mais próximas dos rótulos
    // corretos dos 8 transformadores de exemplo.
    // =====================================================
    public void treinar(RedeEletrica rede) {
        ArrayList<Transformador> dados = rede.getTransformadores();
        int n = dados.size();

        System.out.println("Treinando modelo com " + n + " transformadores...");

        // Normaliza todos os dados para o intervalo [0, 1]
        float[][] X = new float[n][4]; // as 4 medições normalizadas
        float[]   Y = new float[n];   // rótulos: 0.0 = Normal, 1.0 = Crítico

        for (int i = 0; i < n; i++) {
            Transformador t = dados.get(i);
            X[i][0] = normalizar((float) t.getTemperatura(), TEMP_MIN,   TEMP_MAX);
            X[i][1] = normalizar((float) t.getIdade(),       IDADE_MIN,  IDADE_MAX);
            X[i][2] = normalizar((float) t.getCarga(),       CARGA_MIN,  CARGA_MAX);
            X[i][3] = normalizar((float) t.getTensao(),      TENSAO_MIN, TENSAO_MAX);
            Y[i]    = t.isRiscoCritico() ? 1.0f : 0.0f;
        }

        // Loop de treinamento: repete por EPOCAS vezes
        for (int epoca = 0; epoca < EPOCAS; epoca++) {

            // Acumuladores de gradiente: zerados a cada época
            float[][] dw1 = new float[4][3];
            float[]   db1 = new float[3];
            float[]   dw2 = new float[3];
            float     db2 = 0f;

            // Para cada transformador de treino...
            for (int i = 0; i < n; i++) {

                // PASSO 1: Forward pass — calcula a previsão atual
                float[] res   = forwardJava(X[i]);
                float[] h     = {res[0], res[1], res[2]};
                float   yPred = res[3];

                // PASSO 2: Backpropagation — calcula o quanto cada peso errou
                // dz2 = diferença entre o que previu e o que era certo
                float dz2 = yPred - Y[i];

                for (int j = 0; j < 3; j++) {
                    dw2[j] += dz2 * h[j];
                    // Propaga o erro de volta para a camada oculta
                    float dz1 = dz2 * w2[j] * h[j] * (1 - h[j]);
                    for (int k = 0; k < 4; k++) dw1[k][j] += dz1 * X[i][k];
                    db1[j] += dz1;
                }
                db2 += dz2;
            }

            // PASSO 3: Atualiza os pesos na direção que reduz o erro
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 4; k++) w1[k][j] -= TAXA * dw1[k][j] / n;
                b1[j] -= TAXA * db1[j] / n;
                w2[j] -= TAXA * dw2[j] / n;
            }
            b2 -= TAXA * db2 / n;

            // Exibe barra de progresso a cada 10% das épocas
            if ((epoca + 1) % (EPOCAS / 10) == 0) {
                int pct = (epoca + 1) * 20 / EPOCAS;
                StringBuilder barra = new StringBuilder("[");
                for (int b = 0; b < 20; b++) barra.append(b < pct ? "█" : " ");
                barra.append("] ").append(epoca + 1).append("/").append(EPOCAS);
                System.out.println(barra);
            }
        }

        // Calcula a acurácia: quantos dos 8 o modelo acerta agora
        int corretos = 0;
        for (int i = 0; i < n; i++) {
            float[] res = forwardJava(X[i]);
            boolean predicao = res[3] >= 0.5f;
            boolean real     = Y[i] == 1.0f;
            if (predicao == real) corretos++;
        }

        modeloTreinado = true;
        System.out.printf("%nModelo treinado! Acurácia: %.1f%%%n",
                (float) corretos / n * 100.0f);
    }

    // =====================================================
    // CLASSIFICAÇÃO — TensorFlow executa o forward pass
    //
    // Os pesos já foram aprendidos no treinamento.
    // O TF recebe esses pesos e os dados do novo
    // transformador e calcula a probabilidade de risco.
    // =====================================================
    public void classificar(double temperatura, int idade,
                            double carga, double tensao) {
        if (!modeloTreinado) {
            System.out.println("Erro: primeiro treine o modelo (opção 1).");
            return;
        }

        // Cria os tensores TF com os dados normalizados
        TFloat32 xT  = criarTensor1x4(new float[]{
                normalizar((float) temperatura, TEMP_MIN,   TEMP_MAX),
                normalizar((float) idade,       IDADE_MIN,  IDADE_MAX),
                normalizar((float) carga,       CARGA_MIN,  CARGA_MAX),
                normalizar((float) tensao,      TENSAO_MIN, TENSAO_MAX)
        });

        // Cria os tensores TF com os pesos já treinados
        TFloat32 w1T = criarTensorW1();
        TFloat32 b1T = criarTensorB1();
        TFloat32 w2T = criarTensorW2();
        TFloat32 b2T = criarTensorB2();

        // TensorFlow executa o grafo: matMul + sigmoid
        // feed() = alimenta os placeholders com os dados reais
        // fetch() = pede o resultado da saída
        Result resultado = sessao.runner()
                .fetch(saidaTF.asOutput())
                .feed(phX.asOutput(),  xT)
                .feed(phW1.asOutput(), w1T)
                .feed(phB1.asOutput(), b1T)
                .feed(phW2.asOutput(), w2T)
                .feed(phB2.asOutput(), b2T)
                .run();

        // Extrai o valor do tensor de saída de volta para Java
        float confianca;
        try (TFloat32 pred = (TFloat32) resultado.get(0)) {
            confianca = pred.getFloat(0, 0); // tensor [1,1] → float simples
        }

        // Libera os tensores da memória do TensorFlow
        xT.close(); w1T.close(); b1T.close(); w2T.close(); b2T.close();

        System.out.println();
        if (confianca >= 0.5f) {
            System.out.printf(">>> RESULTADO: RISCO CRÍTICO (confiança: %.2f)%n", confianca);
            System.out.println("Recomendação: Agendar manutenção preventiva.");
        } else {
            System.out.printf(">>> RESULTADO: RISCO NORMAL (confiança: %.2f)%n", 1.0f - confianca);
            System.out.println("Recomendação: Transformador operando dentro dos parâmetros.");
        }
    }

    // =====================================================
    // MÉTODOS AUXILIARES: convertem arrays Java em tensores TF
    // =====================================================

    private TFloat32 criarTensor1x4(float[] vals) {
        FloatNdArray nd = NdArrays.ofFloats(Shape.of(1, 4));
        for (int i = 0; i < 4; i++) nd.setFloat(vals[i], 0, i);
        return TFloat32.tensorOf(nd);
    }

    private TFloat32 criarTensorW1() {
        FloatNdArray nd = NdArrays.ofFloats(Shape.of(4, 3));
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 3; j++) nd.setFloat(w1[i][j], i, j);
        return TFloat32.tensorOf(nd);
    }

    private TFloat32 criarTensorB1() {
        FloatNdArray nd = NdArrays.ofFloats(Shape.of(1, 3));
        for (int j = 0; j < 3; j++) nd.setFloat(b1[j], 0, j);
        return TFloat32.tensorOf(nd);
    }

    private TFloat32 criarTensorW2() {
        FloatNdArray nd = NdArrays.ofFloats(Shape.of(3, 1));
        for (int j = 0; j < 3; j++) nd.setFloat(w2[j], j, 0);
        return TFloat32.tensorOf(nd);
    }

    private TFloat32 criarTensorB2() {
        FloatNdArray nd = NdArrays.ofFloats(Shape.of(1, 1));
        nd.setFloat(b2, 0, 0);
        return TFloat32.tensorOf(nd);
    }

    // Libera o grafo e a sessão do TensorFlow ao encerrar
    public void fechar() {
        if (sessao != null) sessao.close();
        if (grafo  != null) grafo.close();
    }

    public boolean isModeloTreinado() {
        return modeloTreinado;
    }
}