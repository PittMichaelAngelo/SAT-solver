package Sat_solver;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class SAT_SOLVER{
		
	private static char[][] randSymMatrix
	(int n, float neg, float pos, int edges, char[][] graphAdj) {
		// TODO Auto-generated method stub
		int posNum = (int) (pos * edges);
		int negNum = edges - posNum;
		int negCount = 0, posCount=0;
		
		char[][] SymMatrix=graphAdj;
		
		
		for (int i=0; i<n; i++) {
			for (int j=0; j<i ; j ++) {
				if(SymMatrix[i][j]=='1') {
					if (negCount==negNum) {
						SymMatrix[i][j]='+';
						SymMatrix[j][i]='+';
						posCount++;
					}
					else if (posCount==posNum) {
						SymMatrix[i][j]='-';
						SymMatrix[j][i]='-';
						negCount++;
					}
					else {
						Random rand=new Random();
						int x=rand.nextInt(2);
						if (x==1) {
							SymMatrix[i][j]='+';
							SymMatrix[j][i]='+';
							posCount++;
						}else {
							SymMatrix[i][j]='-';
							SymMatrix[j][i]='-';
							negCount++;
						}	
					}
				}
			}
		}		
		return SymMatrix;
	}

	private static char[][] randomAdjMatrix(int n, int e) {
		char[][] adjMatrix = new char[n][n];
		int c=0;
		for (int i=0; i<n; i++) {
			for (int j=0; j<n; j++) {
				adjMatrix[i][j]='0';
			}
		}
		
		Random rand = new Random();
		while (c<e) {
			int x=rand.nextInt(n-1) + 1 ; // from 1 to n-1
			int y=rand.nextInt(x); // from 0 to x (all values over the diagonal i:i)
			if (adjMatrix[x][y]!='1') {
				adjMatrix[x][y]='1';
				adjMatrix[y][x]='1';
				c++;
			}
		}
		
		return adjMatrix;
	}

	/*
	 * Creates a random graph
	 */
	private static Graph createGraph() {
		Graph g= null;
		PrintWriter writer;
		Scanner scan;
		int n=0;
		float density=0;
		
		try {
			writer = new PrintWriter("graph.txt", "UTF-8");
			scan = new Scanner(System.in);
			
			System.out.print("Number of Nodes: ");
			n=scan.nextInt();
			while(n<=0) {
				System.out.print("Please give correct input: ");
				n=scan.nextInt();
				
			}
			
			
			System.out.print("Negative Edges Percentage: ");
			float neg=scan.nextFloat();
			while(neg<0 || neg>1) {
				System.out.print("Please give correct input: ");
				neg=scan.nextFloat();
				
			}
			
			System.out.print("Positive Edges Percentage: ");
			float pos=scan.nextFloat();
			while(pos<0 || pos>1 || pos+neg!=1.0) {
				System.out.print("Please give correct input: ");
				pos=scan.nextFloat();
				
			}
			
			System.out.print("Density: ");
			density=scan.nextFloat();
			while(density<0 || density>1) {
				System.out.print("Please give correct input: ");
				density=scan.nextFloat();
			}
			
			
			scan.close();
			
			writer.println(n);
			writer.println(neg);
			writer.println(pos);
			writer.println(density);
			
			int edges= (int) (density * (n*(n-1)) / 2) ;
		
			char[][] graphAdj=randomAdjMatrix(n,edges);
			
			for (int i=0; i<n; i++) {
				for (int j=0; j<n ; j ++) {
					writer.print(graphAdj[i][j] + " ");
				}
				writer.println();
			}
			
			writer.println();
			
			char[][] symbolMatrix=randSymMatrix(n,neg,pos,edges,graphAdj);
			
			for (int i=0; i<n; i++) {
				for (int j=0; j<n ; j ++) {
					writer.print(symbolMatrix[i][j] + " ");
				}
				writer.println();
			}
			
			g = new Graph(n,pos,neg,density,graphAdj,symbolMatrix);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return g;
	}
	
	private static void generateCNF(Graph g) throws FileNotFoundException, UnsupportedEncodingException {
		char[][] sMatrix=g.getSymbolMatrix();
		int var=3*g.getN();
		int clauses=0;
		for (int j=0; j<g.getN(); j++) {
			for (int i=0; i<j; i++) {
				if(sMatrix[i][j]=='-') {
					clauses+=(3);
				}else if(sMatrix[i][j]=='+') {
					clauses+=(6);
				}
			}
		}
		clauses+=4*g.getN(); // make sure node is only in exactly one set
		clauses+=3; // make sure not empty
		
		PrintWriter writer;
		
		writer = new PrintWriter("PROBLEM.cnf", "UTF-8");
		
		writer.write("p cnf ");
		writer.write( var + " " +clauses + "\n");
		
				
		for (int j=0; j<g.getN(); j++) {
			for(int i=0; i<j; i++) {
				switch(sMatrix[i][j]) {
				case '-':
					for (int c=1; c<=3; c++) 
						writer.write("-"+(j*3+c) + " " + "-"+(i*3+c)+ " 0\n");
					break;
				case '+':
					for (int c=1; c<=3; c++) {
						writer.write("-"+(j*3+c) + " " + (i*3+c) + " 0\n");
						writer.write((j*3+c) + " " + "-"+(i*3+c) + " 0\n");
					}					
				}
			}
			writer.write("-"+(j*3+1)+" "+"-"+(j*3+2)+ " 0\n"); //-j1 V -j2
			writer.write("-"+(j*3+1)+" "+"-"+(j*3+3)+ " 0\n"); //-j1 V -j3
			writer.write("-"+(j*3+2)+" "+"-"+(j*3+3)+ " 0\n"); //-j2 V -j3
			writer.write((j*3+1)+" "+(j*3+2)+" "+(j*3+3)+ " 0\n"); //j1 V j2 V j3
		}
		
		for (int c=1; c<=3; c++) {
			for (int i=0; i<g.getN(); i++) {
				writer.write((i*3+c)+" ");
			}
			writer.write(" 0\n");
		}
		
		writer.close();
	}
	
	private static Graph createGraphFromTXT(String s) throws IOException {
		BufferedReader scan = new BufferedReader(new InputStreamReader(new FileInputStream(new File(s))));
		int n=Integer.parseInt(scan.readLine());
		float pos=Float.parseFloat(scan.readLine());
		float neg=Float.parseFloat(scan.readLine());
		float den=Float.parseFloat(scan.readLine());
		
		char[][] AdM= new char [n][n];
		String cbuf=null;
		for (int i=0; i<n; i++) {
			cbuf=scan.readLine();
			for(int j=0; j<n; j++) {
				AdM[i][j]=cbuf.charAt(2*j);
			}
		}
		
		scan.readLine();

		
		char[][] SyM= new char [n][n];
		for (int i=0; i<n; i++) {
			cbuf=scan.readLine();
			for(int j=0; j<n; j++) {
				SyM[i][j]=cbuf.charAt(2*j);
			}
		}
		
		scan.close();
		return (new Graph(n,pos,neg,den,AdM,SyM));
	}
	
	private static void printResults(String t) {
		ArrayList<Integer> Set1=new ArrayList<Integer>();
		ArrayList<Integer> Set2=new ArrayList<Integer>();
		ArrayList<Integer> Set3=new ArrayList<Integer>();
		
		String res[]=t.split(" ");
		int i=1; //skip the v
		
		while(i<res.length-1){
						
			int a=Integer.parseInt(res[i]);
			
			if (a<=0) {
				i++;
			}else{
				switch(a % 3){
					case 1: Set1.add((int)(a/3) + 1); i++; break;
					case 2: Set2.add((int)(a/3) + 1); i++; break;
					case 0: Set3.add((int)(a/3)    ); i++; break;
					default:i++;
				}
			}
		}
		
		System.out.println("The three (3) set that can be generated based"
				+ " on the matrix above are:");
		System.out.print("Set 1 = { ");
		System.out.print(Set1.get(0));
		for (int x=1; x<Set1.size(); x++)
			System.out.print(", " + Set1.get(x));
		System.out.println(" }");
		
		System.out.print("Set 2 = { ");
		System.out.print(Set2.get(0));
		for (int x=1; x<Set2.size(); x++)
			System.out.print(", " + Set2.get(x));
		System.out.println(" }");
		
		System.out.print("Set 3 = { ");
		System.out.print(Set3.get(0));
		for (int x=1; x<Set3.size(); x++)
			System.out.print(", " + Set3.get(x));
		System.out.println(" }");
	}
		
	public static void main(String[]args) {
		Scanner scan=new Scanner(System.in);
		Graph g = null;
		
		if(args[0].equals("0")) {
			System.out.print("Please give a Graph Text: ");
			String s=scan.nextLine();
			try {
				g=createGraphFromTXT(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else {
			g=createGraph(); // create random graph
		}
		scan.close();
		
		
		//generate the CNF file
		try {
			generateCNF(g);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//open terminal inside lingeling_solver
		ProcessBuilder pb=new ProcessBuilder("./lingeling", "PROBLEM.cnf");
				
		Process process=null;
		try {
			process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(process==null){
			System.out.println("Problem with lingeling");
			System.exit(1);
		}
		
		//get the result of lingeling from the process
		Scanner result=new Scanner(process.getInputStream());
		
		String line=null;
		boolean flag=false;
		while(result.hasNextLine()){
			line=result.nextLine();
			if (line.contains("UNSATISFIABLE")){
				System.out.println("The given graph cannot be devided into a total "
						+ "of (3) sets");
				flag=true;
			}
			if (line.contains("SATISFIABLE")){
				printResults(result.nextLine());
				flag=true;
			}
		}
		if(!flag)
			System.out.println("There is an error with the Lingeling result file");
		
		result.close();
	}
}
