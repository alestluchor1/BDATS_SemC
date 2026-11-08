package javafx.bdats_sema_tluchor.obyvatele;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.bdats_sema_tluchor.abstrdoublelist.AbstrTable;
import javafx.bdats_sema_tluchor.abstrdoublelist.AbstrFIFO;
import javafx.bdats_sema_tluchor.abstrdoublelist.AbstrHeap;
import javafx.bdats_sema_tluchor.abstrdoublelist.IAbstrTable;
import javafx.bdats_sema_tluchor.abstrdoublelist.eTypPriority;

/**
 *
 * @author atluc
 * @param <K>
 * @param <V>
 */
public class AgendaKraj<K extends Comparable<K>, V> {

    private final AbstrTable<K, V> binStrom;
    private final AbstrHeap<Obec> heap;

    // Konstruktor pro inicializaci tabulky
    public AgendaKraj() {
        this.binStrom = new AbstrTable<>();
        this.heap = new AbstrHeap<>(vytvorComparator(eTypPriority.POCET_OBYVATEL));
    }

    /// ABSTRHEAP
    public List<Obec> vypisHaldu(IAbstrTable.eTypProhl typProhlidky) {
        return heap.vypis(typProhlidky);
    }

    public final Comparator<Obec> vytvorComparator(eTypPriority typPriority) {
        if (null == typPriority) {
            throw new IllegalArgumentException("Neznámý typ priority.");
        } else {
            switch (typPriority) {
                case NAZEV_OBCE -> {
                    return Comparator.comparing(Obec::getNazev);
                }
                case POCET_OBYVATEL -> {
                    return Comparator.comparing(Obec::getCelkemObyvatel);
                }
                default ->
                    throw new IllegalArgumentException("Neznámý typ priority.");
            }
        }
    }

    public void reorganizujHaldu(eTypPriority typPriority) {
        // Vytvoř nový komparátor na základě zadaného typu priority
        Comparator<Obec> newComparator = vytvorComparator(typPriority);

        // Nastav nový komparátor pro haldu
        heap.setComparator(newComparator);

        // Získání aktuálních prvků z haldy
        List<Obec> hodnotyList = heap.vypis(IAbstrTable.eTypProhl.DO_SIRKY);

        // Převod seznamu na pole
        Obec[] hodnotyArray = hodnotyList.toArray(new Obec[0]);

        // Vybudování nové haldy s novým komparátorem
        heap.vybuduj(hodnotyArray);
    }

    public void vlozDoHaldy(Obec data) {
        heap.vloz(data);
    }

    public Obec odeberMaxZHaldy() {
        Obec obec = heap.odeberMax();

        return obec;
    }

    public Obec zpristupniMaxZHaldy() {
        Obec obec = heap.zpristupniMax();

        return obec;
    }

    public boolean jePrazdna() {
        return heap.jePrazdny();
    }

    public void zrusHeap() {
        heap.zrus();
    }

    ///// ABSTRTABLE
    public V najdi(K key) throws Exception {

        // Vyhledáme v tabulce dle klíče (název obce)
        if (key == null) {
            throw new Exception("Klíč nesmí být null.");
        }

        V value = binStrom.najdi(key);

        if (value == null) {
            throw new Exception("Obec s tímto názvem nebyla nalezena.");
        }
        return value;
    }

    public void vloz(K key, V value) throws Exception {
        if (key == null || value == null) {
            throw new Exception("Klíč nebo hodnota nemohou být null.");
        }
        binStrom.vloz(key, value);
    }

    public V odeber(K key) throws Exception {
        if (key == null) {
            throw new Exception("Klíč nemůže být null.");
        }

        V removedValue = binStrom.odeber(key);

        if (removedValue == null) {
            throw new Exception("Obec s tímto názvem nebyla nalezena pro odstranění.");
        }
        return removedValue;
    }

    //Metoda pro vybudování vyváženého binárního stromu
    public void vybuduj() {

        // Naplňuje FIFO s daty pro vyvážený binární strom
        AbstrFIFO<Obec> fifoZasobnik = fifoNaplneniObciProVybuduj();
        Iterator<Obec> it = fifoZasobnik.iterator();

        // Vkládá každou obec do stromu
        while (it.hasNext()) {
            Obec obec = it.next();
            try {
                vloz((K) obec.getNazev(), (V) obec);
            } catch (Exception e) {
            }
        }
    }

// Metoda pro naplnění FIFO s daty pro vyvážený binární strom
    private AbstrFIFO<Obec> fifoNaplneniObciProVybuduj() {
        AbstrFIFO<Obec> fifoZasobnik = new AbstrFIFO<>();

        // Získá seznam všech obcí z tabulky a převede na pole
        Obec[] poleObci = ziskatPoleVsechObciProVybuduj();
        binStrom.zrus();
        // Pokud má strom více než 1 prvek, sestavíme vyvážený binární strom
        if (poleObci.length > 1) {
            vlozObceProVyvazenyBinStrom(poleObci, fifoZasobnik, 0, poleObci.length - 1);
        } else if (poleObci.length == 1) {
            fifoZasobnik.vloz(poleObci[0]);
        }

        return fifoZasobnik;
    }

// Pomocná metoda pro získání seznamu všech obcí z tabulky
    private Obec[] ziskatPoleVsechObciProVybuduj() {
        LinkedList<Obec> seznamObci = new LinkedList<>();
        Iterator<Obec> it = (Iterator<Obec>) binStrom.vytvorIterator(AbstrTable.eTypProhl.DO_HLOUBKY);

        // Projde všechny obce v tabulce
        while (it.hasNext()) {
            seznamObci.add(it.next());
        }

        // Vytvoří pole všech obcí a vrátí ho
        Obec[] poleVsechObci = new Obec[seznamObci.size()];
        return seznamObci.toArray(poleVsechObci);
    }

// Rekurzivní metoda pro vložení obcí do FIFO podle principu vyváženého binárního stromu
    private void vlozObceProVyvazenyBinStrom(Obec[] pole, AbstrFIFO<Obec> fifo, int start, int end) {
        if (start <= end) {
            // Vypočítáme střední index
            int mid = (start + end) / 2;

            // Vložení obce do FIFO
            Obec vkladanaObec = pole[mid];
            fifo.vloz(vkladanaObec);

            // Rekurzivní volání pro levou a pravou část pole
            vlozObceProVyvazenyBinStrom(pole, fifo, start, mid - 1); // Levá část
            vlozObceProVyvazenyBinStrom(pole, fifo, mid + 1, end);   // Pravá část
        }
    }

    public void zrus() {
        binStrom.zrus();
    }

    public void stromJePrazdny() {
        binStrom.jePrazdny();
    }

    // Metoda pro získání iterátoru do šířky (level-order)
    public Iterator<V> vytvorIteratorDoSirky() {
        return binStrom.vytvorIterator(AbstrTable.eTypProhl.DO_SIRKY);
    }

    // Metoda pro získání iterátoru do hloubky (pre-order)
    public Iterator<V> vytvorIteratorDoHloubky() {
        return binStrom.vytvorIterator(AbstrTable.eTypProhl.DO_HLOUBKY);
    }
}
