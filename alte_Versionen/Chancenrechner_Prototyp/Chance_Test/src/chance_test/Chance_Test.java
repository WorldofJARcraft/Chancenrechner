/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chance_test;

import java.util.Scanner;

/**
 *
 * @author Eric
 */
public class Chance_Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner scanner = new Scanner(System.in);
        int[] zahl = new int[11];
        for(int i=0;i<11;i++){
            System.out.println("zahl["+i+"]=");
            zahl[i]=scanner.nextInt();
            
        }
        System.out.println("Optimum:");
        int optimum = scanner.nextInt();
        System.out.println("Rest");
        int Rest = scanner.nextInt();
        zahl= sortieren (zahl, Rest, optimum);
        
        for(int i=0; i<11;i++)
            System.out.println("Zahl["+i+"]="+zahl[i]);
        
            
    }
    static private int[] sortieren(int[] zahl, int Rest, int optimum) {
        //Sortieren des Arrays
        //1. Fall: Ausgleichende Ziele
        for(int i=0;i<optimum;i++){
        while(zahl[i]>0&&zahl[optimum+(optimum-i)]>0){
            System.out.println(i+";"+(optimum+(optimum-i)));
            zahl[i]--;
            zahl[optimum+(optimum-i)]--;
            zahl[optimum]+=2;
        }
        }
        //2. Idee: Ziele mitteln, d.h. statt 1*0 und 1*8 2*4
        for(int i=0;i<10;i++){
            System.out.println("1");
            for(int j=0;j<10;j++){
                            System.out.println("2");
                if((i+j)%2==0&&i!=j){
                                System.out.println("3");
                    while(zahl[i]>0&&zahl[j]>0){
                        zahl[i]--;
                        zahl[j]--;
                        zahl[(i+j)/2]+=2;
                    }
                }
                
            }
        
        }
        return zahl;
    }
    
}
