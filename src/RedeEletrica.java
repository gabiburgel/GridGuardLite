import java.util.ArrayList;

public class RedeEletrica {

    // COMPOSIÇÃO: RedeEletrica "tem" uma lista de objetos Transformador
    // Esse é o relacionamento "has-a" (tem um) da POO
    private ArrayList<Transformador> transformadores;

    // Construtor: ao criar a rede, já carrega os dados históricos
    public RedeEletrica() {
        transformadores = new ArrayList<>();
        carregarDados();
    }

    // Método privado: carrega os 8 transformadores já classificados por especialistas
    // Esses são os exemplos que a rede neural vai usar para aprender
    private void carregarDados() {
        // Formato: temperatura, idade, carga, tensao, riscoCritico
        transformadores.add(new Transformador(95,  20, 90, 195, true));   // T1 - Crítico
        transformadores.add(new Transformador(45,   3, 40, 220, false));  // T2 - Normal
        transformadores.add(new Transformador(102, 25, 95, 188, true));   // T3 - Crítico
        transformadores.add(new Transformador(38,   2, 35, 225, false));  // T4 - Normal
        transformadores.add(new Transformador(88,  15, 82, 200, true));   // T5 - Crítico
        transformadores.add(new Transformador(52,   5, 55, 215, false));  // T6 - Normal
        transformadores.add(new Transformador(78,  12, 70, 205, false));  // T7 - Normal
        transformadores.add(new Transformador(98,  18, 88, 192, true));   // T8 - Crítico
    }

    // Getter: permite que outras classes leiam a lista de transformadores
    public ArrayList<Transformador> getTransformadores() {
        return transformadores;
    }
}