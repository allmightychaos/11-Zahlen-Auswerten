import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NumberAnalyzerGUI extends JFrame {
    private JTextArea textArea;
    private JButton analyzeButton;
    private JTable numbersTable;
    private DefaultTableModel tableModel;
    private JList<String> frequentNumbersList;
    private DefaultListModel<String> listModel;
    private JSlider minSlider, maxSlider;
    private JRadioButton integerButton, doubleButton;
    private JCheckBoxMenuItem sortMenuItem;

    public NumberAnalyzerGUI() {
        setTitle("Zahlen Analyzer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createMenuBar();
        createMainPanel();
        createBottomPanel();
        createControlPanel();

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Datei");
        JMenuItem menuItemNew = new JMenuItem("Neu");
        menuItemNew.addActionListener(e -> resetApplication());
        menu.add(menuItemNew);

        sortMenuItem = new JCheckBoxMenuItem("Sortieren");
        menu.add(sortMenuItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        textArea = new JTextArea();
        mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        analyzeButton = new JButton("Auswerten");
        analyzeButton.addActionListener(e -> analyzeText());
        mainPanel.add(analyzeButton, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));

        tableModel = new DefaultTableModel(new Object[]{"Zahl", "Anzahl"}, 0);
        numbersTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(numbersTable);
        bottomPanel.add(tableScrollPane);

        listModel = new DefaultListModel<>();
        frequentNumbersList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(frequentNumbersList);
        bottomPanel.add(listScrollPane);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        minSlider = new JSlider(0, 40, 0);
        minSlider.setMajorTickSpacing(10);
        minSlider.setPaintTicks(true);
        minSlider.setPaintLabels(true);
        controlPanel.add(new JLabel("Min Wert:"));
        controlPanel.add(minSlider);

        maxSlider = new JSlider(60, 200, 200);
        maxSlider.setMajorTickSpacing(20);
        maxSlider.setPaintTicks(true);
        maxSlider.setPaintLabels(true);
        controlPanel.add(new JLabel("Max Wert:"));
        controlPanel.add(maxSlider);

        integerButton = new JRadioButton("Integer", true);
        doubleButton = new JRadioButton("Double");
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(integerButton);
        typeGroup.add(doubleButton);
        controlPanel.add(integerButton);
        controlPanel.add(doubleButton);

        add(controlPanel, BorderLayout.NORTH);
    }

    private void analyzeText() {
        String text = textArea.getText().replaceAll("([^\\d\\.]+|\\s{2,}|\\s\\.)", " ").trim();
        if (text.isEmpty()) {
            return;
        }

        Map<String, Integer> numbersCount = new HashMap<>();
        Pattern pattern = Pattern.compile(integerButton.isSelected() ? "\\d+" : "\\d*\\.\\d+|\\d+");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String number = matcher.group();
            double value = Double.parseDouble(number);
            if (value >= minSlider.getValue() && value <= maxSlider.getValue()) {
                numbersCount.put(number, numbersCount.getOrDefault(number, 0) + 1);
            }
        }

        updateTable(numbersCount);
        updateList(numbersCount);
    }

    private void updateTable(Map<String, Integer> numbersCount) {
        tableModel.setRowCount(0);
        numbersCount.forEach((number, count) -> tableModel.addRow(new Object[]{number, count}));
    }

    private void updateList(Map<String, Integer> numbersCount) {
        listModel.clear();
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(numbersCount.entrySet());
        sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        if (sortMenuItem.isSelected()) {
            sortedEntries = sortedEntries.stream()
                    .sorted(Comparator.comparing(e -> -Integer.parseInt(e.getKey()) * e.getValue()))
                    .collect(Collectors.toList());
        }

        for (int i = 0; i < Math.min(sortedEntries.size(), 10); i++) {
            Map.Entry<String, Integer> entry = sortedEntries.get(i);
            listModel.addElement(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void resetApplication() {
        textArea.setText("");
        tableModel.setRowCount(0);
        listModel.clear();
        minSlider.setValue(0);
        maxSlider.setValue(200);
        integerButton.setSelected(true);
        sortMenuItem.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NumberAnalyzerGUI::new);
    }
}
