package pro.juego.Ironfall.pathfinding;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Pathfinding {
	private Nodo[][] grilla;
	private int alto;
	private int ancho;
	
	public Pathfinding(Nodo[][] grilla) {
		this.grilla = grilla;
		this.ancho = grilla.length;
		this.alto = grilla[0].length;
	}
	
	public List<Nodo> encontrarCamino(Nodo inicio, Nodo destino){
		
		PriorityQueue<Nodo> abiertos = new PriorityQueue<>(); // Nodos no explorado, saca el nodo con menor fCosto, en criollo el que esta mas cerca del destino
	    Set<Nodo> cerrados = new HashSet<>();//Se refieren a lo nodos que no sirven, para no volver a revisarlos, no deverian repetirse 
	    
	    inicio.gCosto = 0;
	    inicio.hCosto = calcularHeuristica(inicio, destino); // 
	    inicio.padre = null;
	    abiertos.add(inicio);
	    while(!abiertos.isEmpty()) {
	    	Nodo actual = abiertos.poll();// saca el mejor nodo (menor fCosto)
	    	if(actual.equals(destino)) {
	    		return reconstruirCamino(actual);// encontro el camino
	    	}
	    	 cerrados.add(actual);

	         for (Nodo vecino : getVecinos(actual)) {
	             if (!vecino.accesible || cerrados.contains(vecino)) continue;

	             int nuevoCostoG = actual.gCosto + 1;

	             if (nuevoCostoG < vecino.gCosto || !abiertos.contains(vecino)) {
	                 vecino.gCosto = nuevoCostoG;
	                 vecino.hCosto = calcularHeuristica(vecino, destino);
	                 vecino.padre = actual;

	                 if (!abiertos.contains(vecino)) {
	                     abiertos.add(vecino);
	                 }
	             }
	         }
	     }

	     return new ArrayList<>(); // sin camino
		}
	
	private List<Nodo> getVecinos(Nodo nodo) {
        List<Nodo> vecinos = new ArrayList<>();

        int[][] direcciones = {
            {0, 1},   // arriba
            {0, -1},  // abajo
            {1, 0},   // derecha
            {-1, 0}   // izquierda
        };

        for (int[] dir : direcciones) {
            int nx = nodo.x + dir[0];
            int ny = nodo.y + dir[1];

            if (nx >= 0 && nx < ancho && ny >= 0 && ny < alto) {
                vecinos.add(grilla[nx][ny]);
            }
        }

        return vecinos;
    }
	  private int calcularHeuristica(Nodo a, Nodo b) {
	        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	    }
	  private List<Nodo> reconstruirCamino(Nodo destino) {
	        List<Nodo> camino = new ArrayList<>();
	        Nodo actual = destino;

	        while (actual != null) {
	            camino.add(0, actual); // lo meto al principio para que quede en orden
	            actual = actual.padre;
	        }

	        return camino;
	    }
	  public List<Nodo> suavizarCamino(List<Nodo> camino) {
		    if (camino == null || camino.size() < 3) return camino;

		    List<Nodo> suavizado = new ArrayList<>();
		    suavizado.add(camino.get(0)); // siempre agregamos el inicio

		    int indiceActual = 0;

		    for (int i = 2; i < camino.size(); i++) {
		        Nodo desde = camino.get(indiceActual);
		        Nodo hasta = camino.get(i);

		        if (!hayObstaculo(desde, hasta)) {
		            continue; // si no hay obstáculo, seguimos buscando
		        }

		        // si hay obstáculo, el nodo anterior era el último visible
		        suavizado.add(camino.get(i - 1));
		        indiceActual = i - 1;
		    }

		    suavizado.add(camino.get(camino.size() - 1)); // final

		    return suavizado;
		}
	  private boolean hayObstaculo(Nodo desde, Nodo hasta) {
		    int dx = hasta.x - desde.x;
		    int dy = hasta.y - desde.y;
		    int pasos = Math.max(Math.abs(dx), Math.abs(dy));

		    for (int i = 1; i < pasos; i++) {
		        int x = desde.x + i * dx / pasos;
		        int y = desde.y + i * dy / pasos;

		        if (!grilla[x][y].accesible) {
		            return true;
		        }
		    }
		    return false;
		}
	
}
