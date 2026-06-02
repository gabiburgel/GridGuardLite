import java.util.ArrayList;

public class RedeEletrica {
    private ArrayList<Transformador> transformadores;

    public RedeEletrica()
    {
        transformadores = new ArrayList<>();

        carregarDados();
    }
    private void carregarDados()
    {
        transformadores.add(
                new Transformador(95,20,90,195,true));

        transformadores.add(
                new Transformador(45,3,40,220,false));

        transformadores.add(
                new Transformador(102,25,95,188,true));

        transformadores.add(
                new Transformador(38,2,35,225,false));

        transformadores.add(
                new Transformador(88,15,82,200,true));

        transformadores.add(
                new Transformador(52,5,55,215,false));

        transformadores.add(
                new Transformador(78,12,70,205,false));

        transformadores.add(
                new Transformador(98,18,88,192,true));
    }
    public ArrayList<Transformador> getTransformadores()
    {
        return transformadores;
    }
}
