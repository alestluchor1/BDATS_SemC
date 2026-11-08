package javafx.bdats_sema_tluchor.abstrdoublelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.bdats_sema_tluchor.abstrdoublelist.IAbstrTable.eTypProhl;

public class AbstrHeap<T> implements IAbstrHeap<T> {

    private T[] heapPole;
    int heapSize;
    private Comparator<T> comparator;

    public AbstrHeap(Comparator<T> comparator) {
        
        this.comparator = comparator;
        this.heapSize = 0;
        
    }

    @Override
    public void vybuduj(T[] hodnoty) {
        
        zrus();
        heapPole = Arrays.copyOf(hodnoty, hodnoty.length);
        heapSize = hodnoty.length;
        
        prebuduj();
    }

    @Override
    public void prebuduj() {
        // Vybudování maxHeap pomocí heapify
        for (int i = heapSize / 2 - 1; i >=  0; i--) {
            heapify(heapPole, heapSize, i);
        }
    }

    private void heapify(T[] arr, int pocetPrvku, int i) {
        int largest = i; // Největší prvek (počátečně kořen)
        int left = 2 * i + 1; // Levý potomek
        int right = 2 * i + 2; // Pravý potomek

        // Porovnej levého potomka s rodičem
        if (left < pocetPrvku && (comparator.compare(arr[left], arr[largest]) > 0)) {
            largest = left;
        }

        // Porovnej pravého potomka s největším nalezeným
        if (right < pocetPrvku && (comparator.compare(arr[right], arr[largest]) > 0)) {
            largest = right;
        }

        // Pokud se největší prvek změnil, proveď výměnu a rekurzivně oprav podstrom
        if (largest != i) {
            T swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            heapify(arr, pocetPrvku, largest);
        }
    }
    
    // prohození swap
    private void swap(int index1, int index2) {
        T temp = heapPole[index1];
        heapPole[index1] = heapPole[index2];
        heapPole[index2] = temp;
    }

    @Override
    public T[] getPole() {
        return this.heapPole;
    }

    private void heapUp(int child) {
        int parent;
        while (child > 0 && jeMensi((parent = getParent(child)), child)) {
            swap(parent, child);
            child = parent;
        }
    }
    
    // Levý potomek rodiče
    private int getLeftChild(int parrent) {
        return (parrent * 2) + 1;
    }
    
    //Pravý potomek rodiče
    private int getRightChild(int parrent) {
        return (parrent * 2) + 2;
    }
    
    // Rodič
    private int getParent(int child) {
        return (child - 1) / 2;
    }

    @Override
    public int getHeapSize() {
        return heapSize;
    }

    @Override
    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void zrus() {
        this.heapPole = (T[]) new Object[0];
        heapSize = 0;
    }

    @Override
    public boolean jePrazdny() {
        return heapSize == 0 || heapSize < 1;
    }

    @Override
    public void vloz(T data) {
        if (heapPole == null || heapPole.length == 0) {
            heapPole = (T[]) new Object[1]; // Inicializace pole, pokud není nastaveno
        }
        zkontrolujHeapSize(); // Zkontroluje, zda je třeba zvětšit pole
        
        heapPole[heapSize] = data; // Vloží prvek na konec haldy
        heapUp(heapSize); // Upraví haldu, aby dodržovala pravidla
        heapSize++; // Zvýší velikost haldy
    }

    @Override
    public T odeberMax() {
        if (jePrazdny()) {

            return null;
        }
        T max = heapPole[0];
        heapPole[0] = heapPole[heapSize - 1];
        heapPole[heapSize - 1] = null;
        heapSize--;

        heapDown(0); // Zajišťuje, že halda zůstane správně uspořádána

        zkontrolujHeapSize();
        
        return max;
    }

    private void heapDown(int parent) {
        
        int maxChild;
        
        while ((maxChild = getMaxChild(parent)) != -1 && jeMensi(parent, maxChild)) {
            
            swap(parent, maxChild);
            parent = maxChild;
        }
    }

    private int getMaxChild(int parent) {
        
        int noChild = -1;   // Žádný potomek neexistuje
        int rightChild = getRightChild(parent);
        int leftChild = getLeftChild(parent);    

        if (jePrvekVHalde(rightChild) && jeMensi(leftChild, rightChild)) {
            return rightChild;
        } else if (jePrvekVHalde(leftChild)) {
            return leftChild;
        } else {
            return noChild; // Žádný potomek neexistuje
        }
    }

    @Override
    public T zpristupniMax() {
        if (!jePrazdny()) {
            return heapPole[0];
        } else {
            return null;
        }
    }

    @Override
    public List<T> vypis(eTypProhl typProhlidky) {
        List<T> list = new ArrayList<>();
        Iterator<Integer> iterator = vytvorIterator(typProhlidky);
        if (iterator != null) {
            while (iterator.hasNext()) {
                int index = iterator.next();
                list.add(heapPole[index]);
            }
        }
        return list;
    }

    // Vytvoření Iteratoru pro různé typy prohlídek
    @Override
    public Iterator<Integer> vytvorIterator(eTypProhl typProhlidky) {
        switch (typProhlidky) {
            case DO_SIRKY -> {
                return new levelOrderIterator();
            }
            case DO_HLOUBKY -> {
                return new inOrderIterator();
            }
            default ->
                throw new IllegalArgumentException("Neznámý typ prohlídky.");
        }
    }

    // Do šířky
    private class levelOrderIterator implements Iterator<Integer> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < heapSize;
        }

        @Override
        public Integer next() {
            return index++;
        }
    }

    // Do hloubky in-order
    private class inOrderIterator implements Iterator<Integer> {

        private final AbstrLIFO<Integer> zasobnik = new AbstrLIFO<>();  // Zásobník pro uchování uzlů
        private int aktualniIndex = 0;  // Ukazatel na aktuální prvek

        public inOrderIterator() {
            // Inicializace: Projdeme levý podstrom kořenu a přidáme uzly do zásobníku
            pushLevyPodstrom(0);
        }

        // Funkce pro průchod levým podstromem a přidání všech uzlů do zásobníku
        private void pushLevyPodstrom(int index) {
            while (index < heapSize) {
                zasobnik.vloz(index);  // Přidáme aktuální uzel na zásobník
                index = getLeftChild(index);  // Přejdeme na levého potomka
            }
        }

        @Override
        public boolean hasNext() {
            return !zasobnik.jePrazdny();  // Pokud zásobník není prázdný, máme další prvek
        }

        @Override
        public Integer next() {
            try {
                if (!hasNext()) {
                    throw new NoSuchElementException("Žádný další prvek.");
                }

                // Vyjmeme uzel z vrcholu zásobníku
                int aktualniIndex = zasobnik.odeber();

                // Pokud má tento uzel pravého potomka, prozkoumáme podstrom pravého potomka
                if (getRightChild(aktualniIndex) < heapSize) {
                    pushLevyPodstrom(getRightChild(aktualniIndex));
                }

                return aktualniIndex;
            } catch (Exception ex) {
                Logger.getLogger(AbstrHeap.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    private boolean jeMensi(int parent, int child) {
        if (heapPole[child] == null) {
            return false;
        }
        return comparator.compare(heapPole[parent], heapPole[child]) < 0;
    }

    private void zkontrolujHeapSize() {
        if (heapSize * 2 < heapPole.length) {
            zmensiVelikostHeap();
        } else if (heapSize == heapPole.length) {
            zvetsiVelikostHeap();
        }
    }

    private void zvetsiVelikostHeap() {
        heapPole = Arrays.copyOf(heapPole, (heapPole.length * 2) + 1);
    }

    private void zmensiVelikostHeap() {
        heapPole = Arrays.copyOf(heapPole, (heapPole.length / 2) + 1);
    }

    private boolean jePrvekVHalde(int prvek) {
        return prvek < heapSize;
    }

}
