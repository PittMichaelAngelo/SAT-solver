package Sat_solver;

public class Graph {
	
	private int N;
	private float PosPerc;
	private float NegPerc;
	private float density;
	
	private char[][] AdjMatrix;
	private char[][] SymbolMatrix;
	
	public Graph(int n,float pos,float neg,float dens,char[][] AdM,char[][]symM) {
		this.N=n;
		this.PosPerc=pos;
		this.NegPerc=neg;
		this.density=dens;
		this.AdjMatrix=AdM;
		this.SymbolMatrix=symM;
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public float getPosPerc() {
		return PosPerc;
	}

	public void setPosPerc(float posPerc) {
		PosPerc = posPerc;
	}

	public float getNegPerc() {
		return NegPerc;
	}

	public void setNegPerc(float negPerc) {
		NegPerc = negPerc;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public char[][] getAdjMatrix() {
		return AdjMatrix;
	}

	public void setAdjMatrix(char[][] adjMatrix) {
		AdjMatrix = adjMatrix;
	}

	public char[][] getSymbolMatrix() {
		return SymbolMatrix;
	}

	public void setSymbolMatrix(char[][] symbolMatrix) {
		SymbolMatrix = symbolMatrix;
	}
	
	
}
