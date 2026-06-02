public class Transformador {
    private double temperatura;
    private int idade;
    private double carga;
    private double tensao;
    private boolean riscoCritico;

    public Transformador(double temperatura,
                         int idade,
                         double carga,
                         double tensao,
                         boolean riscoCritico){

        this.temperatura = temperatura;
        this.idade = idade;
        this.carga = carga;
        this.tensao = tensao;
        this.riscoCritico = riscoCritico;

    }

    public double getTemperatura(){
        return temperatura;
    }
    public int getIdade(){
        return idade;
    }
    public double getCarga(){
        return carga;
    }
    public double getTensao(){
        return tensao;
    }
    public boolean isRiscoCritico(){
        return riscoCritico;
    }
}
