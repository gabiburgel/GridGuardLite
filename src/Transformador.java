public class Transformador {

    // Atributos privados: ninguém de fora consegue acessar diretamente
    // Isso chama encapsulamento, um dos pilares da POO
    private double  temperatura;   // em graus Celsius
    private int     idade;         // em anos
    private double  carga;         // em porcentagem (%)
    private double  tensao;        // em Volts
    private boolean riscoCritico;  // true = Crítico, false = Normal

    // Construtor: cria um transformador com todos os seus dados
    public Transformador(double temperatura, int idade,
                         double carga, double tensao,
                         boolean riscoCritico) {
        this.temperatura  = temperatura;
        this.idade        = idade;
        this.carga        = carga;
        this.tensao       = tensao;
        this.riscoCritico = riscoCritico;
    }

    // Getters: única forma de ler os atributos de fora da classe
    public double  getTemperatura() { return temperatura;  }
    public int     getIdade()       { return idade;        }
    public double  getCarga()       { return carga;        }
    public double  getTensao()      { return tensao;       }
    public boolean isRiscoCritico() { return riscoCritico; }
}