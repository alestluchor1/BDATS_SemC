package javafx.bdats_sema_tluchor.abstrdoublelist;

import java.util.Comparator;
import java.util.List;
import java.util.Iterator;

/**
 * Rozhraní pro implementaci haldy.
 *
 * @param <T> Typ prvků v haldě.
 */
public interface IAbstrHeap<T> {

    /**
     * Vloží prvek do haldy.
     *
     * @param data Prvek, který má být přidán.
     */
    void vloz(T data);

    /**
     * Odebere maximální prvek z haldy.
     *
     * @return Maximální prvek, nebo null, pokud je halda prázdná.
     */
    T odeberMax();

    /**
     * Vrátí maximální prvek bez jeho odstranění.
     *
     * @return Maximální prvek, nebo null, pokud je halda prázdná.
     */
    T zpristupniMax();

    /**
     * Zjistí, zda je halda prázdná.
     *
     * @return True, pokud je halda prázdná, jinak false.
     */
    boolean jePrazdny();

    /**
     * Získá velikost haldy.
     *
     * @return Velikost haldy.
     */
    int getHeapSize();

    /**
     * Vybuduje haldu z pole hodnot.
     *
     * @param hodnoty Pole hodnot, které se mají vložit do haldy.
     */
    void vybuduj(T[] hodnoty);

    /**
     * Přestaví haldu na základě jejího stávajícího stavu.
     */
    void prebuduj();

    /**
     * Nastaví komparátor pro porovnávání prvků v haldě.
     *
     * @param comparator Komparátor pro porovnání prvků.
     */
    void setComparator(Comparator<T> comparator);

    /**
     * Vytiskne prvky haldy podle specifikovaného typu prohlídky.
     *
     * @param typProhlidky Typ prohlídky (např. do šířky nebo do hloubky).
     * @return Seznam prvků ve správném pořadí.
     */
    List<T> vypis(IAbstrTable.eTypProhl typProhlidky);

    /**
     * Vytvoří iterátor pro procházení haldy podle zadaného typu prohlídky.
     *
     * @param typProhlidky Typ prohlídky (např. do šířky nebo do hloubky).
     * @return Iterátor pro procházení haldy.
     */
    Iterator<Integer> vytvorIterator(IAbstrTable.eTypProhl typProhlidky);

    /**
     * získá pole
     *
     * @return
     */
    T[] getPole();

    /* 
    * Smaže pole
     */
    void zrus();

}
