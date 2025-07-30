package pro.juego.Ironfall.pathfinding;

public class Nodo implements Comparable<Nodo> {
		public int x, y;
		public boolean accesible; // esto va a marcar si la celda es o no accesible 
		public Nodo padre;
		public int gCosto, hCosto; //El costo para llegar hasta ahí (g) y El costo estimado hasta el destino (h)
		
	public Nodo(int x, int y, boolean accesible) {
		this.x = x;
		this.y = y;
		this.accesible = accesible;
	}
	
	@Override
	public boolean equals(Object obj){
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false; 
		
		Nodo otro = (Nodo) obj; // lo convierto a tipo Nodo
		return this.x == otro.x && this.y == otro.y;
	}
	
	@Override
	public int hashCode(){
		return 31 * x + y;
	}
	

	
	@Override
	public int compareTo(Nodo otro) {
	    return Integer.compare(this.getFCosto(), otro.getFCosto());
	}
	
	public int getFCosto() {
		return gCosto + hCosto;
	}
}
