# GridGuard Lite

**Preditor de risco de transformadores de rede elétrica com rede neural em Java.**

![Java](https://img.shields.io/badge/Java-17-red)
![TensorFlow](https://img.shields.io/badge/TensorFlow%20Java-1.0.0-orange)
![Licença](https://img.shields.io/badge/licen%C3%A7a-MIT-green)

GridGuard Lite é uma aplicação de console que estima se um transformador de energia está em **risco crítico** de falha, a partir de quatro medições operacionais. O modelo é uma rede neural pequena com o **treinamento implementado do zero em Java puro** e a **classificação executada via TensorFlow**.

---

## O que o projeto faz:

A partir de quatro medições de um transformador...

| Medição | Unidade | Faixa esperada |
|---|---|---|
| Temperatura | °C | 30 – 120 |
| Idade | anos | 0 – 30 |
| Carga | % | 20 – 100 |
| Tensão | V | 180 – 240 |

...o sistema devolve uma classificação entre **Risco Crítico** ou **Normal** com um nível de confiança e uma recomendação de manutenção.

O menu do programa tem três opções:

1. **Treinar modelo** com os dados históricos (8 transformadores já rotulados por especialistas).
2. **Classificar** um novo transformador informado pelo usuário.
3. **Sair**.

---

## Como funciona

O núcleo é uma rede neural **feedforward** com arquitetura **4 → 3 → 1**: quatro entradas (as medições), uma camada oculta de três neurônios e uma saída (probabilidade de risco crítico). A ativação usada é a **sigmoide**.

O projeto tem uma característica incomum e proposital: **treino e inferência são feitos por caminhos diferentes**, para explorar os dois lados do aprendizado de máquina.

- **Treinamento: Java (`Classificador.treinar`).** O ajuste dos pesos é implementado à mão: forward pass, cálculo do erro, **backpropagation** e **gradiente descendente**, ao longo de 500 épocas.
- **Classificação: TensorFlow Java (`Classificador.classificar`).** Um grafo de computação (`matMul` + `sigmoid`) é montado uma vez e executado numa sessão, alimentando os pesos já aprendidos através de *placeholders*.

---

## Estrutura do código

| Arquivo | Responsabilidade |
|---|---|
| `Principal.java` | Ponto de entrada e menu de console. |
| `Transformador.java` | Modelo de um transformador (4 medições + rótulo). Demonstra **encapsulamento**. |
| `RedeEletrica.java` | Conjunto de transformadores históricos. Demonstra **composição** (relação *has-a*). |
| `Classificador.java` | A rede neural: treino em Java e inferência em TensorFlow. |

---

## O que este projeto exercita

- **Programação Orientada a Objetos:** encapsulamento, composição, responsabilidade única por classe.
- **Fundamentos de redes neurais:** forward pass, função de ativação, backpropagation, gradiente descendente, normalização e inicialização de pesos — tudo implementado manualmente.
- **Integração com TensorFlow Java:** construção de grafo, sessão, *placeholders* e manipulação de tensores.
