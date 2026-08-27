# ⚡ GridGuard Lite

**Preditor de risco de transformadores de rede elétrica com rede neural em Java.**

![Java](https://img.shields.io/badge/Java-17-red)
![TensorFlow](https://img.shields.io/badge/TensorFlow%20Java-1.0.0-orange)
![Licença](https://img.shields.io/badge/licen%C3%A7a-MIT-green)

GridGuard Lite é uma aplicação de console que estima se um transformador de energia está em **risco crítico** de falha, a partir de quatro medições operacionais. O modelo é uma rede neural pequena — com o **treinamento implementado do zero em Java puro** e a **classificação executada via TensorFlow**.

---

## 🎯 O que o projeto faz

A partir de quatro medições de um transformador...

| Medição | Unidade | Faixa esperada |
|---|---|---|
| Temperatura | °C | 30 – 120 |
| Idade | anos | 0 – 30 |
| Carga | % | 20 – 100 |
| Tensão | V | 180 – 240 |

...o sistema devolve uma classificação — **Risco Crítico** ou **Normal** — com um nível de confiança e uma recomendação de manutenção.

O menu do programa tem três opções:

1. **Treinar modelo** com os dados históricos (8 transformadores já rotulados por especialistas).
2. **Classificar** um novo transformador informado pelo usuário.
3. **Sair**.

---

## 🧠 Como funciona

O núcleo é uma rede neural **feedforward** com arquitetura **4 → 3 → 1**: quatro entradas (as medições), uma camada oculta de três neurônios e uma saída (probabilidade de risco crítico). A ativação usada é a **sigmoide**.

O projeto tem uma característica incomum e proposital: **treino e inferência são feitos por caminhos diferentes**, para explorar os dois lados do aprendizado de máquina.

- **Treinamento — Java puro (`Classificador.treinar`).** O ajuste dos pesos é implementado à mão: forward pass, cálculo do erro, **backpropagation** e **gradiente descendente**, ao longo de 500 épocas. Isso deixa explícito o que normalmente fica escondido dentro de uma biblioteca.
- **Classificação — TensorFlow Java (`Classificador.classificar`).** Um grafo de computação (`matMul` + `sigmoid`) é montado uma vez e executado numa sessão, alimentando os pesos já aprendidos através de *placeholders*.

Detalhes de implementação que valem nota:

- **Normalização** de todas as entradas para o intervalo [0, 1], evitando que a tensão (valores altos) domine numericamente sobre a idade (valores baixos).
- **Inicialização de Xavier** dos pesos, para o treino começar de forma estável.
- **Semente fixa (42)**, garantindo resultados reproduzíveis a cada execução.

---

## 🏗️ Estrutura do código

| Arquivo | Responsabilidade |
|---|---|
| `Principal.java` | Ponto de entrada e menu de console. |
| `Transformador.java` | Modelo de um transformador (4 medições + rótulo). Demonstra **encapsulamento**. |
| `RedeEletrica.java` | Conjunto de transformadores históricos. Demonstra **composição** (relação *has-a*). |
| `Classificador.java` | A rede neural: treino em Java e inferência em TensorFlow. |

---

## 🚀 Como rodar

### Pré-requisitos
- **JDK 17** (ou 11+).
- **Maven** para baixar as dependências.

### Passos
```bash
# clonar o repositório
git clone https://github.com/gabiburgel/GridGuardLite.git
cd GridGuardLite

# compilar e executar
mvn compile
mvn exec:java
```

No programa, escolha a **opção 1** para treinar o modelo e depois a **opção 2** para classificar um transformador.

> **⚠️ Sobre a plataforma:** o TensorFlow Java distribui binários para **Windows, Linux e macOS Intel (x86_64)**. Em **Mac com chip Apple Silicon (M1/M2/M3...)** não há binário oficial — nesse caso é preciso rodar num ambiente x86_64 ou compilar o TensorFlow do código-fonte.

---

## 📚 O que este projeto exercita

- **Programação Orientada a Objetos:** encapsulamento, composição, responsabilidade única por classe.
- **Fundamentos de redes neurais:** forward pass, função de ativação, backpropagation, gradiente descendente, normalização e inicialização de pesos — tudo implementado manualmente.
- **Integração com TensorFlow Java:** construção de grafo, sessão, *placeholders* e manipulação de tensores.

---

## 🔭 Limitações e próximos passos

Este é um projeto de estudo, e assumir seus limites faz parte:

- O conjunto de treino é pequeno (8 exemplos) e sem separação treino/teste — a acurácia reportada é sobre os próprios dados de treino.
- Treino e inferência usam caminhos distintos; uma evolução natural seria unificar tudo no TensorFlow.
- Ideias futuras: persistir os pesos treinados em arquivo, ampliar o conjunto de dados, adicionar validação cruzada e uma interface gráfica.

---

## 📄 Licença

Distribuído sob a licença MIT. Sinta-se à vontade para estudar e adaptar.

---

*Desenvolvido por [Gabriela Burgel](https://github.com/gabiburgel) — estudante de Sistemas de Informação na UTFPR.*
