package genetico;

import java.util.*;

public class Main {

    //parametros
    static int numGenerations = 30000;
    static float mutationRate = 0.15f;
    static float selectionRate = 0.4f;
    static int numPopulacao = 130;
    static int numGenes = 100; //de 2 a 100

    public static ArrayList<ArrayList<Integer>> population = new ArrayList<>(); //população
    public static ArrayList<Integer> bestResult = new ArrayList<>();
    public static int graph[][] = null;
    

    public static void readGraphFile(){
        FileManager fileManager = new FileManager();
        ArrayList<String> text = fileManager.stringReader("CaixeiroViajante/data/test100.txt");
                
        for (int i = 0; i <= numGenes; i++) {
            String line = text.get(i);
            if (i == 0){
                //nVertex = Integer.parseInt(line.trim());
                graph = new int[numGenes][numGenes]; //numgenes
            }
            else {
                int oriVertex = Integer.parseInt(line.split(" ")[0]);
                String splits[] = line.substring(line.indexOf(" "), line.length()).split(";");
                for (String part : splits){
                    String edgeData[] = part.split("-");
                    int targetVertex = Integer.parseInt(edgeData[0].trim());
                    int weight = Integer.parseInt(edgeData[1]);
                    
                    /*
                        ADICIONAR A ARESTA À REPRESENTAÇÃO
                    */
                    if(oriVertex > numGenes-1 || targetVertex > numGenes-1){

                    }else{
                        graph[oriVertex][targetVertex] = weight; //
                        graph[targetVertex][oriVertex] = weight; //comentar para direcionar o grafo
                    }   
                }
            }
        }
    }


    public static ArrayList<Integer> createRandomIndi(){    //cria os genomas aleatorios

        ArrayList<Integer> individuo = new ArrayList<>();

        for(int i = 0; i < numGenes; i++){
            individuo.add(i);
        }

        Collections.shuffle(individuo);

        return individuo;
    }

    public static void showPopulation(){
        for(int i = 0; i < population.size(); i++){
            System.out.println(String.valueOf(i) + " "+ population.get(i));
        }
        System.out.println("==========================================");
    }

    public static void crossover(int size){ 
        //faz o cruzamento entre dois individuos
        //pega metade exato de cada genoma pai

        for(int i = 0; i < size; i+=2){
            ArrayList<Integer> individuoCruzado = new ArrayList<>();


            for(int j = 0; j < size/2; j++){
                int gene = population.get(i).get(j);
                individuoCruzado.add(gene);
            }

            for(int j = size/2; j < size; j++){
                int gene = population.get(i+1).get(j);
                individuoCruzado.add(gene);
            }

            population.add(individuoCruzado);

        }
    }

    public static void mutatePopulation(int size){ //numgenes


        Random rand = new Random();

        for(int i = 0; i < population.size(); i++){
            float mutate = rand.nextFloat();

            if(mutate < mutationRate){
                List<Integer> individuo = population.get(i);

                Collections.swap(individuo, rand.nextInt(size), rand.nextInt(size));
            }
        }
    }

    public static void fixUnableChild(int index){

        ArrayList<Integer> ind = population.get(index);

        Set<Integer> s = new LinkedHashSet<>();


        if(ind.size() > numGenes){
            ind.remove(ind.size()-1); //remove a coluna do fitness
        }

        s.addAll(ind);

        for(int i = 0; i < numGenes; i++){

            if(!s.contains(i)){
                s.add(i);
            }
        }

        ind.clear();
        ind.addAll(s);

        population.set(index, ind);

    }


    public static void clearUnablePop(){

        ArrayList<Integer> clearIndexes = new ArrayList<>();


        for(int i = numPopulacao; i < population.size(); i++){

            int auxPop[] = new int[numPopulacao];
            Arrays.fill(auxPop, -1);

            for(int j = 0; j < numGenes; j++){

                int gene = population.get(i).get(j);

                if(auxPop[gene] == -1){
                    auxPop[gene] = gene;
                }else{
                    clearIndexes.add(i);
                    break;
                }
            }
        }

        for(int i = 0; i < clearIndexes.size(); i++){
            population.remove(clearIndexes.get(i));            
        }

    }


    public static void getFitness(){

        for(int i = 0; i < population.size(); i++){
            if(population.get(i).size() == numGenes){
                population.get(i).add(0);
            }
        }
        

        for(int i = 0; i < population.size(); i++){
            int finalValue = 0;
            int aux = 0;

            for(int j = 0; j < numGenes - 1; j++){ //numgenes
                // System.out.println(population.get(i).get(j) + " " + population.get(i).get(j + 1) + "=" + graph[population.get(i).get(j)][population.get(i).get(j + 1)]);
                aux = aux + graph[population.get(i).get(j)][population.get(i).get(j + 1)];
            }


            finalValue = aux + graph[population.get(i).get(numGenes - 1)][population.get(i).get(0)];

            // System.out.println("final: " + graph[population.get(i).get(numPopulacao - 1)][population.get(i).get(0)]);
            // System.out.println("total: " + finalValue);
            // System.out.println("==");


            population.get(i).set(numGenes, finalValue);

        }
    }

    public static void sortByFitness(){
        //utiliza bubble sort no arraylist interno comparando o valor do fitness de cada individuo

        for(int i = 0; i < population.size(); i++){ 
            for(int j = 0; j < population.size(); j++){

                if(population.get(i).get(numGenes) <= population.get(j).get(numGenes)){

                    ArrayList<Integer> aux = population.get(i);
                    population.set(i, population.get(j));
                    population.set(j, aux);
                }
            }
        }
    }

    public static void verifyBestResult(){
        if(bestResult.size() == 0){
            bestResult.addAll(population.get(0));
        }else if(population.get(0).get(numGenes) < bestResult.get(numGenes)){
            bestResult.clear();
            bestResult.addAll(population.get(0));
        }
    }

    public static void selection(){
        //deixa um numero x dos melhores individuos defino por selectionRate

        int popSelected = (int) Math.round(selectionRate * population.size());

        for(int i = population.size() - 1; i >= popSelected; i--){
            population.remove(i);
        }
    }

    public static void rePop(){
        //repopula o numero inicial da população
        int rePopNum = numPopulacao - population.size();

        for(int i = 0; i < rePopNum; i++){
            population.add(createRandomIndi());
        }
    }

    public static void showBestPath(){
        System.out.println("Best path: " + bestResult);
        System.out.println("Best fit: " + bestResult.get(numGenes));
        System.out.println("Generations num: " + numGenerations);
        System.out.println("Population size: " + numPopulacao);
    }

    public static void main(String[] args) {

        int dataSize = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        long startTime = System.currentTimeMillis();
 
        readGraphFile();

        for(int i = 0; i < numPopulacao; i++ ){
            population.add(createRandomIndi());
        }
        


        for(int  i = 0; i < numGenerations; i++){ // aqui corre as gerações

            if(i % 1000 == 2){
                System.out.println(i);
            }

            crossover(numGenes);

            if(numPopulacao % 2 == 1){
                population.remove(population.size()-1); //remove ultimo filho em caso de numero impar de cidades
            }
  
            mutatePopulation(numGenes);

            for(int j = 0; j < population.size(); j++){
                fixUnableChild(j);
            }            

            getFitness();          
            sortByFitness();
            verifyBestResult();
            selection();
            rePop();
        }

        showBestPath();
        long endTime = System.currentTimeMillis();
        double finalTime = (double) (endTime - startTime)/1000;
        System.out.println("Tempo de execução: " + finalTime + "s");
        System.out.println("Memória usada: " + (rt.totalMemory() - rt.freeMemory())/dataSize + " MB");
    }
}

