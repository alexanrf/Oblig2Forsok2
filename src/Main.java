import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        RunSekv runsekv = new RunSekv();
        runsekv.run();
    }
}


class RunSekv{
    void run(){
        //2 milliarder: 2000000000
        int maxPrime = 2000000000;
        EratosthenesSil sil = new EratosthenesSil(maxPrime);

        long t = System.nanoTime(); // start klokke
        sil.generatePrimesByEratosthenesSeq(); //Oppretter alle primtallene


        double tid = (System.nanoTime()-t)/1000000.0; //Teller opp tiden brukt på å generere primtall
        System.out.println("Genererte alle primtall under < " + maxPrime + " på " +  tid + " millisec");
        System.out.println("Antall primtall: " + sil.howManyPrimes());


        long factorize = (long)maxPrime*maxPrime - 100; //Regner ut av tallene som skal faktoriseres

        t = System.nanoTime(); // start klokke igjen
        for(int i = 0; i < 100; i++){
            ArrayList<Long> list = sil.factorize(factorize + i); //Faktoriserer og skriver ut tallene
            prettyPrintFactors(list, factorize + i);
        }

        tid = (System.nanoTime()-t)/1000000.0; //Skriver ut resultat etter sekvensiell kjøring
        System.out.println("100 faktoriseringer med utskrift beregnet på: " + tid + "\n" + tid/100 + "ms. per faktorisering");
    }

    void prettyPrintFactors(ArrayList<Long> list, long num){ //Skriver ut faktoriseringsresultatet pent.
        System.out.print(num + " = ");
        for(int i = 0; i<list.size(); i++){
            if(i == list.size()-1){
                System.out.print(list.get(i) +"\n");
            }
            else{
                System.out.print(list.get(i) + " * ");
            }
        }
    }
}



class EratosthenesSil {
    byte[] bitArr;           // bitArr[0] represents the 8 integers:  1,3,5,...,15, and so on
    int maxNum;               // all primes in this bit-array is <= maxNum
    final int[] bitMask = {1, 2, 4, 8, 16, 32, 64, 128};  // kanskje trenger du denne - 0 starter fra starten.
    final int[] bitMask2 = {255 - 1, 255 - 2, 255 - 4, 255 - 8, 255 - 16, 255 - 32, 255 - 64, 255 - 128}; // kanskje trenger du denne - 0 starter fra slutten
    ArrayList<Integer> primes = new ArrayList<>();


    EratosthenesSil (int maxNum) {
        this.maxNum = maxNum;
        bitArr = new byte [(maxNum/16)+1]; // 16 pga 8 tall i en byte, og kun oddetall
        setAllPrime();
    } // end konstruktor ErathostenesSil

    void setAllPrime() {
        for (int i = 0; i < bitArr.length; i++) {
            bitArr[i] = (byte)255; // bitarray[0] = 11111111
        }

    } //Sets all bits to 1

    void crossOut(int i) {
        if(i % 2 == 0){
            return;
        }

        int bitIndex = (i-1) / 2;
        int arrayIndex = bitIndex / 8; //bitArr[arrayIndex] - Where the number is stored
        bitIndex = bitIndex % 8; //which bit in bitArr[arrayIndex] corresponds to the number

        bitArr[arrayIndex] = (byte) (bitArr[arrayIndex] & bitMask2[bitIndex]); //Uses the correct mask over the array index to single out our value.
    } //Crosses out the integer (sets it to 0) aka not a prime


    boolean isPrime (int i) { //isCrossedOut()
        if(i == 2){
            return true;
        }
        if(i % 2 == 0 || i == 1){
            return false;
        }

        //Finn position i arrayet
        int bitIndex = (i-1) / 2;
        int arrayIndex = bitIndex / 8;
        bitIndex = bitIndex % 8;
        //System.out.println("isPrime("+ i +")");

        if((bitArr[arrayIndex] & bitMask[bitIndex]) == 0){
            return false;
        }
        return true;
    } //Checks if a value is a prime


    int nextPrime(int input) { //Ikke særlig optimalt dette. Hadde problemer med å iterere over binærArrayet direkte
        if(input < 2)
            return 2;
        if(input % 2 == 0){ //Unødvendig å sjekke partall.
            input++;
        }
        while(input < maxNum){
            if(isPrime(input)){
                return input;
            }
            input += 2;
        }
        return -1;
    }

    void generatePrimesByEratosthenesSeq() {
        crossOut(1);      // 1 er ikke et primtall
        long prime = 0;
        int sqrtMaxNum = (int)Math.sqrt(maxNum)+1; //About 5-10% faster with an int than a double based on observations.

        while((prime = nextPrime((int)prime+1)) <= sqrtMaxNum){ //SqrtMaxNum fordi
            long primeTemp = prime*prime;

            while(primeTemp < maxNum){
                crossOut((int)primeTemp);
                primeTemp += prime*2;
            }
        }
        //Setter alle primtall i en egen liste.
        setPrimesList();

    } // end generatePrimesByEratosthenes


    int howManyPrimes() { //Puts all primes in a list.
        return primes.size();
    }

    void setPrimesList(){
        primes.add(2); //2 is a prime
        int input = 3; //Starts at 3, and checks all numbers above.
        while(input < maxNum){
            if(isPrime(input)){
                primes.add(input);
            }
            input += 2;
        }

    }


    ArrayList<Long> factorize (long num) { //Sekvensiell faktorisering
        ArrayList <Long> fakt = new ArrayList <>(); //Arraylist som holder styr på faktorene våre. Trengs ikke long her. de er under 2 milliard

        int primeCount = 0;
        int prime = primes.get(primeCount);
        while(true){ // != -1 fordi nextPrime returnerer -1 om det ikke er flere primtall
            if(num % prime == 0){ // == 0, da er det en faktor.
                fakt.add((long)prime);
                num = num / prime;
            }
            else{
                try{
                    prime = primes.get(++primeCount); //Ellers, gå til neste primtall
                }
                catch(Exception e){
                    break; //Ferdig med primtall
                }
            }
        }
        if(num != 1){
            fakt.add(num);
        }
        return fakt;
    }

}