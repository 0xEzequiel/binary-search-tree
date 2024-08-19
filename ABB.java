package aed;

import java.util.*;

// elem1.compareTo(elem2) devuelve un entero. Si es mayor a 0, entonces elem1 > elem2
public class ABB<T extends Comparable<T>> implements Conjunto<T> {
    // Atributos privados del Conjunto
    private Nodo _raiz;
    private int _cardinal;

    private class Nodo {
        // Atributos privados del Nodo
        T valor;
        Nodo izq;
        Nodo der;
        Nodo padre;

        // Constructor del nodo
        Nodo(T v){
            valor = v;
            izq = null;
            der = null;
            padre = null;
        }

    }

    public ABB() {
        _raiz = null;
        _cardinal = 0;
    }

    public int cardinal() {
        return _cardinal;
    }

    public T minimo(){
        Nodo actual = _raiz;
        while(actual.izq!=null){
            actual = actual.izq;
        }
        return actual.valor;
    }

    public T maximo(){
        Nodo actual = _raiz;

        while(actual.der!=null){
            actual = actual.der;
        }
        return actual.valor;
    }

    private Nodo buscar_nodo(Nodo raiz, T elem){
        int cmp = elem.compareTo(raiz.valor);

        if(cmp<0){ // elem < raiz.valor
            if(raiz.izq != null) return buscar_nodo(raiz.izq, elem);
        } else if(cmp>0){
            if(raiz.der != null) return buscar_nodo(raiz.der, elem);
        }
        return raiz;
    }

    public void insertar(T elem){
        Nodo nuevo = new Nodo(elem);

        if(cardinal() == 0){
            _raiz = nuevo;
            _cardinal++;
        } else if(!pertenece(elem)){
            Nodo ultimo_nodo_buscado = buscar_nodo(_raiz, elem);
            if(ultimo_nodo_buscado.valor.compareTo(elem) > 0){
                ultimo_nodo_buscado.izq = nuevo;
            } else {
                ultimo_nodo_buscado.der = nuevo;
            }
            nuevo.padre = ultimo_nodo_buscado;
            _cardinal++;
        }
    }

    private boolean busqueda_recursiva(Nodo actual, T elem){
        while(actual != null){
            int cmp = actual.valor.compareTo(elem);
            // actual.valor > elem
            if(cmp>0){ return busqueda_recursiva(actual.izq, elem);}
            else if(cmp==0){ return true; }
            else if(cmp<0) { return busqueda_recursiva(actual.der, elem);}
        }
        return false;
    }

    public boolean pertenece(T elem){
        return busqueda_recursiva(_raiz, elem);
    }

    private boolean esHoja(Nodo n){
        return (n.der == null) && (n.izq == null);
    }

    // Precondicion: que elem pertenezca al conjunto.
    private Nodo encontrarNodo(Nodo actual, T elem){
        int cmp = elem.compareTo(actual.valor);

        if(cmp<0){
            return encontrarNodo(actual.izq, elem);
        } else if(cmp>0){
            return encontrarNodo(actual.der, elem);
        } else {
            return actual;
        }
    }

    private boolean tieneSoloUnHijo(Nodo n){
        return ((n.der==null) && (n.izq!=null)) ||
               ((n.der!=null) && (n.izq==null));
    }

    private boolean tieneDosHijos(Nodo n){
        return n.der != null && n.izq != null;
    }

    // Devuelve el minimo del subarbol derecho
    private Nodo sucesorInmediato(Nodo n){
        Nodo actual = n.der;
        while(actual.izq!=null){
            actual = actual.izq;
        }
        return actual;
    }

    public void eliminar(T elem){
        if(pertenece(elem)){
            Nodo aEliminar = encontrarNodo(_raiz, elem);

            /* CASO NODO HOJA */
            if(esHoja(aEliminar)){
                if(_raiz == aEliminar){ _raiz = null; }
                else {
                    Nodo padre = aEliminar.padre;
                    if(padre.der == aEliminar){
                        padre.der = null;
                    } else {
                        padre.izq = null;
                    }
                }

            /* CASO NODO CON UN HIJO */
            } else if(tieneSoloUnHijo(aEliminar)){
                Nodo padre = aEliminar.padre;
                Nodo hijo = (aEliminar.der!=null) ? aEliminar.der : aEliminar.izq;
                if(aEliminar == _raiz){
                    _raiz = hijo;
                } else {
                    hijo.padre = padre;
                    if(padre.der == aEliminar){
                        padre.der = hijo;
                    } else {
                        padre.izq = hijo;
                    }
                }
                aEliminar = null;

            /* CASO NODO CON DOS HIJOS */
            } else if(tieneDosHijos(aEliminar)){
                Nodo sucesorInmediato = sucesorInmediato(aEliminar);

                aEliminar.valor = sucesorInmediato.valor;
                Nodo padreSucesor = sucesorInmediato.padre;

                if(esHoja(sucesorInmediato)){
                    if(padreSucesor.der == sucesorInmediato){
                        padreSucesor.der = null;
                    } else {
                        padreSucesor.izq = null;
                    }
                } else if (tieneSoloUnHijo(sucesorInmediato)){
                    Nodo hijo = (sucesorInmediato.der != null)? sucesorInmediato.der : sucesorInmediato.izq;
                    if(sucesorInmediato == _raiz){
                        _raiz = hijo;
                    } else {
                        hijo.padre = padreSucesor;
                        if(padreSucesor.der == sucesorInmediato){
                            padreSucesor.der = hijo;
                        } else {
                            padreSucesor.izq = hijo;
                        }
                    }
                sucesorInmediato = null;
                }
            }
            _cardinal--;
        }
    }


    public String toString(){
        StringBuffer res =  new StringBuffer();
        if(_cardinal == 0){
            return "{}";
        } else {
            Iterador<T> it = this.iterador();
            res.append("{");
            while(it.haySiguiente()){
                T elemento = it.siguiente();
                res.append(elemento);
                if(elemento != maximo()) res.append(",");
            }

            res.append("}");
        }

        return res.toString();
    }

    private class ABB_Iterador implements Iterador<T> {
        private int posicion;
        private Nodo actual;

        ABB_Iterador(){
            posicion = 0;
        }

        public boolean haySiguiente() {
            return posicion < cardinal();
        }

        private Nodo sucesor(T elem){
            Nodo res = encontrarNodo(_raiz, elem);
            if(res.der != null){
                res = res.der;
                while(res.izq!=null){
                    res = res.izq;
                }
            } else if(res.der==null && res.padre.der == res){
                res = res.padre;
                while(elem.compareTo(res.valor)>0){
                    res = res.padre;
                }
            } else if(res.padre.izq == res){
                res = res.padre;
            }

            return res;
        }

        private T encontrarNodoPorOrden(Nodo raiz, int posicion){
            // Precondicion: que el conjunto no este vacio;
            T res = minimo();

            while(posicion!=0){
                res = sucesor(res).valor;
                // La idea de esto es hacer:
                // sucesor(sucesor(sucesor...(sucesor(res))))
                posicion--;
            }
            return res;
        }

        public T siguiente() {
            posicion++;
            return encontrarNodoPorOrden(_raiz, posicion-1);
        }

    }

    public Iterador<T> iterador() {
        return new ABB_Iterador();
    }
}
