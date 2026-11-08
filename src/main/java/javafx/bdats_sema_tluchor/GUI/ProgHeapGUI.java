package javafx.bdats_sema_tluchor.GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.bdats_sema_tluchor.abstrdoublelist.IAbstrTable.eTypProhl;
import javafx.bdats_sema_tluchor.obyvatele.Obec;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.bdats_sema_tluchor.obyvatele.AgendaKraj;
import javafx.bdats_sema_tluchor.obyvatele.enumKraj;
import javafx.bdats_sema_tluchor.abstrdoublelist.eTypPriority;
import javafx.bdats_sema_tluchor.generator.Generator;

import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class ProgHeapGUI extends Application {

    private static final int VYSKA_OKNO = 500;
    private static final int SIRKA_OKNO = 800;

    private final Generator generator = new Generator();

    private AgendaKraj<String, Obec> agenda;

    private static final Button btnVloz = new Button("Vlož");
    private static final Button btnPreorganizuj = new Button("Preorganizuj");
    private static final Button btnZrus = new Button("Zruš");
    private static final Button btnOdeberMax = new Button("Odeber Max");
    private static final Button btnZpristupniMax = new Button("Zpristupni max");
    private static final Button btnGeneruj = new Button("Generovat");
    private static final Button btnImport = new Button("Importuj ze souboru");
    private static final Button btnImportZKrajeCSV = new Button("Import 'kraje.csv'");
    private static final Button btnUlozDoSouboru = new Button("Ulož");

    private final ObservableList<Obec> oltObce = FXCollections.observableArrayList();
    private final ListView<Obec> listViewObce = new ListView<>();

    private final TextField txtNovaObec = new TextField();

    // ComboBox pro výběr kraje
    private final ComboBox<enumKraj> comboKraj = new ComboBox<>();

    // ComboBox pro výběr kritéria přeorganizování POČET OBYVATEL NEBO POČET OBCÍ
    private final ComboBox<eTypPriority> comboKriterium = new ComboBox<>();

    // ComboBox pro výběr Iteratoru do šířky nebo hloubky
    private final ComboBox<eTypProhl> comboProhl = new ComboBox<>();

    //Spinner
    private final Spinner<Integer> spPocetGenerovani = new Spinner<>(1, 100, 1);

    @Override
    public void start(Stage stage) throws Exception {
        agenda = new AgendaKraj<>();

        // Nastavení seznamu obcí pro ListView
        listViewObce.setItems(oltObce);

        // Nastavení ComboBox pro výběr kraje
        comboKraj.getItems().setAll(enumKraj.values());
        comboKraj.setPromptText("Vyberte kraj");

        // Nastavení ComboBox pro výběr kritéria (Počet obyvatel nebo Název obce)
        comboKriterium.getItems().setAll(eTypPriority.NAZEV_OBCE, eTypPriority.POCET_OBYVATEL);
        comboKriterium.setPromptText("Vyberte kritérium");

        // Nastavení ComboBox pro výběr iteratoru
        comboProhl.getItems().setAll(eTypProhl.DO_HLOUBKY, eTypProhl.DO_SIRKY);
        comboProhl.setPromptText("Vyber iterator");

        // Nastavení TextField pro zadání nové obce
        txtNovaObec.setPromptText("Zadejte název obce...");

        // Akce pro tlačítka
        btnUlozDoSouboru.setOnAction(e -> ulozDoSouboru(listViewObce));
        btnVloz.setOnAction(e -> vlozObec());
        btnImportZKrajeCSV.setOnAction(e -> {
            try {
                importZSouboruKraje(stage);
            } catch (Exception ex) {
                Logger.getLogger(ProgHeapGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnPreorganizuj.setOnAction(e -> preorganizujHaldu());
        btnZrus.setOnAction(e -> zrusHaldu());
        btnOdeberMax.setOnAction(e -> odeberMaxZHaldy());
        btnZpristupniMax.setOnAction(e -> zpristupniMax());
        btnGeneruj.setOnAction(e -> generujObec());
        btnImport.setOnAction(e -> {
            try {
                importData(stage);
            } catch (Exception ex) {
                Logger.getLogger(ProgHeapGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // Hlavní nadpis aplikace
        Label titleLabel = new Label("Správa obcí");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setAlignment(Pos.CENTER);

        // Rozložení ovládacích prvků
        HBox hboxControls = new HBox(10, btnVloz, comboKraj, comboKriterium, btnPreorganizuj, comboProhl, btnImportZKrajeCSV);
        hboxControls.setAlignment(Pos.CENTER);

        HBox hboxControls2 = new HBox(10, btnOdeberMax, btnZpristupniMax, spPocetGenerovani, btnGeneruj, btnImport, btnZrus, btnUlozDoSouboru);
        hboxControls2.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(15, titleLabel, listViewObce, hboxControls, hboxControls2);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.TOP_CENTER);

        // Nastavení scény
        Scene scene = new Scene(vbox, SIRKA_OKNO, VYSKA_OKNO);
        vbox.setStyle("-fx-background-color: lightblue;");
        stage.setScene(scene);
        stage.setTitle("Správa obcí (Tlučhoř)");
        stage.show();
    }

    // Metoda pro reorganizování haldy
    private void preorganizujHaldu() {
        eTypPriority selectedPriority = comboKriterium.getValue();
        if (selectedPriority != null) {
            // Reorganizace haldy podle vybraného kritéria
            agenda.reorganizujHaldu(selectedPriority);

            refreshList();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Halda byla přeorganizována podle: " + selectedPriority);
            alert.show();
        } else {

            Alert alert = new Alert(Alert.AlertType.WARNING, "Vyberte kritérium pro reorganizaci.");
            alert.show();
        }
    }

    private void refreshList() {
        // Aktualizuj ObservableList s novými daty
        eTypProhl selectedProhl = comboProhl.getSelectionModel().getSelectedItem();

        if (selectedProhl == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vyberte iterator pro prohlížení.");
            alert.show();
            return;
        }

        // Získáme seznam obcí podle zvoleného prohlížeče (iteratoru)
        List<Obec> listObci = agenda.vypisHaldu(selectedProhl);

        if (listObci.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seznam obcí je prázdný.");
            alert.show();
        } else {

            oltObce.setAll(listObci);
            listViewObce.setItems(oltObce);
        }
    }

    // Metoda pro vložení nové obce
    private void vlozObec() {
        // Získání vybraného kraje z ComboBox
        enumKraj vybranyKraj = comboKraj.getValue();
        if (vybranyKraj == null) {
            // Pokud není vybrán kraj, zobrazí se varování
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vyberte kraj.");
            alert.show();
            return;
        }

        // Dialog pro zadání informací o nové obci
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Vložení nové obce");
        dialog.setHeaderText("Zadejte informace o nové obci (PSC;název;počet mužů;počet žen):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] parts = input.split(";");
            if (parts.length == 4) {
                try {
                    // Získání jednotlivých hodnot pro novou obec
                    String PSC = parts[0].trim();
                    String nazev = parts[1].trim();
                    int pocetMuz = Integer.parseInt(parts[2].trim());
                    int pocetZen = Integer.parseInt(parts[3].trim());

                    Obec novaObec = new Obec(PSC, nazev, pocetMuz, pocetZen);

                    agenda.vlozDoHaldy(novaObec);

                    oltObce.add(novaObec);

                    // Informování uživatele
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Obec byla úspěšně vložena.");
                    alert.show();
                } catch (NumberFormatException e) {

                    Alert alert = new Alert(Alert.AlertType.ERROR, "Chyba při převodu čísel: " + e.getMessage());
                    alert.show();
                } catch (Exception ex) {

                    Alert alert = new Alert(Alert.AlertType.ERROR, "Chyba při vložení obce.");
                    alert.show();
                }
            } else {

                Alert alert = new Alert(Alert.AlertType.WARNING, "Špatný formát. Použijte formát PSC;název;počet mužů;počet žen.");
                alert.show();
            }
        });
    }

    private void zrusHaldu() {

        agenda.zrusHeap();

        oltObce.clear();

        listViewObce.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Halda a seznam obcí byly úspěšně smazány.");
        alert.show();
    }

    // Metoda pro odebrání maximu z haldy
    private void odeberMaxZHaldy() {
        Obec maxObec = agenda.odeberMaxZHaldy(); // Odebere max prvek
        if (maxObec != null) {
            oltObce.remove(maxObec);
            refreshList();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Byla odebrána obec: " + maxObec.getNazev());
            alert.show();
        }
    }

    private void zpristupniMax() {
        Obec maxObec = agenda.zpristupniMaxZHaldy();
        if (maxObec != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Maximální obec: " + maxObec.getNazev());
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Halda je prázdná.");
            alert.show();
        }
    }

    private void generujObec() {
        Integer pocetGenerovani = spPocetGenerovani.getValue();

        if (pocetGenerovani == null || pocetGenerovani <= 0) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Varování");
            alert.setHeaderText(null);
            alert.setContentText("Zadejte platný počet obcí pro generování.");
            alert.showAndWait();
            return;
        }

        try {
            enumKraj vybranyKraj = comboKraj.getValue();
            if (vybranyKraj == null) {

                showAlert("Varování", "Vyberte kraj před generováním obcí.");
                return;
            }

            // Generování obcí
            for (int i = 0; i < pocetGenerovani; i++) {
                Obec novaObec = generator.generateData(listViewObce, spPocetGenerovani, vybranyKraj);

                if (novaObec != null) {
                    agenda.vlozDoHaldy(novaObec);
                }
            }

            showAlert("Úspěch", "Obce byly úspěšně vygenerovány a přidány.");

        } catch (Exception e) {

            showAlert("Chyba", "Došlo k chybě při generování obce: " + e.getMessage());
        }
    }

    // Metoda pro import dat z csv souboru v určitém formátu
    private void importData(Stage stage) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV soubory", "*.csv"));
        File soubor = fileChooser.showOpenDialog(stage);

        if (soubor != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(soubor))) {
                String line;
                while ((line = br.readLine()) != null) {

                    if (line.startsWith("Obec{") && line.endsWith(", }")) {
                        line = line.substring(5, line.length() - 3);

                        String[] detailsParts = line.split(", ");

                        String psc = detailsParts[0].split("=")[1].trim();
                        String nazev = detailsParts[1].split("=")[1].trim();
                        int pocetMuz = Integer.parseInt(detailsParts[2].split("=")[1].trim());
                        int pocetZen = Integer.parseInt(detailsParts[3].split("=")[1].trim());

                        Obec novaObec = new Obec(psc, nazev, pocetMuz, pocetZen);

                        agenda.vlozDoHaldy(novaObec);

                        oltObce.add(novaObec);
                    }
                }
            } catch (IOException e) {
                showAlert("Chyba", "Chyba " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert("Chyba", "Neplatný formát dat ve vstupním souboru.");
            }
        }
    }

    // Metoda pro načítání obcí z CSV souboru "kraje"
    private void importZSouboruKraje(Stage stage) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV soubory", "*.csv"));
        File soubor = fileChooser.showOpenDialog(stage);

        if (soubor != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(soubor))) {
                String line;
                ObservableList<Obec> noveObce = FXCollections.observableArrayList();

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length >= 7) {
                        try {
                            String nazev = parts[3].trim();
                            String PSC = parts[2].trim();
                            int pocetMuz = Integer.parseInt(parts[4].trim());
                            int pocetZen = Integer.parseInt(parts[5].trim());
                            int kodKraje = Integer.parseInt(parts[0].trim()) - 1;

                            if (kodKraje >= 0 && kodKraje < enumKraj.values().length) {

                                Obec obec = new Obec(PSC, nazev, pocetMuz, pocetZen);
                                noveObce.add(obec);
                                agenda.vlozDoHaldy(obec);
                            }
                        } catch (NumberFormatException e) {

                        }
                    }
                }

                if (!noveObce.isEmpty()) {
                    oltObce.addAll(noveObce);
                }
            } catch (IOException e) {
                showAlert("Chyba", "Chyba při čtení souboru: " + e.getMessage());
            }
        }
    }

    // Metoda pro uložení obcí do csv souboru
    private void ulozDoSouboru(ListView<Obec> listView) {

        if (listView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Žádné položky k uložení.");
            alert.show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Uložit CSV soubor");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV soubory", "*.csv"));

        // Otevře dialog pro uložení souboru
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            // podmínka, jestli má soubor příponu .csv
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            try (FileWriter writer = new FileWriter(file)) {
                for (Obec item : listView.getItems()) {
                    writer.write(item + "\n");
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Soubor byl úspěšně uložen na: " + file.getAbsolutePath());
                alert.show();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Chyba při ukládání souboru: " + e.getMessage());
                alert.show();

            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Uložení souboru bylo zrušeno.");
            alert.show();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
