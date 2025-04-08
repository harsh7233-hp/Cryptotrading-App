import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;

public class CryptoTradingApp extends JFrame {
    // Main panels
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    
    // Market panel components
    private JTable marketTable;
    private JTextField searchField;
    
    // Portfolio panel components
    private JTable portfolioTable;
    private JLabel portfolioValueLabel;
    
    // Trading panel components
    private JComboBox<String> cryptoSelector;
    private JTextField amountField;
    private JLabel priceLabel;
    private JLabel totalLabel;
    private JRadioButton buyButton;
    private JRadioButton sellButton;
    
    // Sample data
    private String[] cryptos = {"Bitcoin (BTC)", "Ethereum (ETH)", "Binance Coin (BNB)", 
                               "Cardano (ADA)", "Solana (SOL)", "Ripple (XRP)"};
    private double[] prices = {42568.30, 2298.45, 312.78, 0.48, 102.35, 0.52};
    private double[] changes = {2.5, -1.2, 0.8, 3.2, -0.5, 1.7};
    
    // Portfolio data
    private HashMap<String, Double> portfolio = new HashMap<>();
    private double accountBalance = 10000.00;
    
    // Formatters
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    private DecimalFormat changeFormat = new DecimalFormat("+#,##0.00;-#,##0.00");
    
    public CryptoTradingApp() {
        // Initialize sample portfolio
        portfolio.put("Bitcoin (BTC)", 0.05);
        portfolio.put("Ethereum (ETH)", 1.2);
        portfolio.put("Cardano (ADA)", 500.0);
        
        // Set up the frame
        setTitle("CryptoTrader - Simple Trading Platform");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with tabs
        mainPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();
        
        // Create and add the three main panels
        tabbedPane.addTab("Market", createMarketPanel());
        tabbedPane.addTab("Portfolio", createPortfolioPanel());
        tabbedPane.addTab("Trade", createTradePanel());
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(32, 43, 61));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("CryptoTrader");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel balanceLabel = new JLabel("Balance: $" + df.format(accountBalance));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMarketPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.setToolTipText("Search cryptocurrencies");
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(59, 89, 152));
        searchButton.setForeground(Color.WHITE);
        
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Market table
        String[] columnNames = {"Cryptocurrency", "Price (USD)", "24h Change", "Market Cap", "Action"};
        Object[][] data = new Object[cryptos.length][5];
        
        for (int i = 0; i < cryptos.length; i++) {
            data[i][0] = cryptos[i];
            data[i][1] = "$" + df.format(prices[i]);
            data[i][2] = changeFormat.format(changes[i]) + "%";
            data[i][3] = "$" + df.format(prices[i] * (Math.random() * 1000000000 + 1000000));
            data[i][4] = "Trade";
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only action column is editable
            }
        };
        
        marketTable = new JTable(model);
        marketTable.setRowHeight(30);
        
        // Custom renderer for the change column to show colors
        marketTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                String text = value.toString();
                if (text.startsWith("+")) {
                    c.setForeground(new Color(0, 150, 0)); // Green for positive
                } else if (text.startsWith("-")) {
                    c.setForeground(new Color(200, 0, 0)); // Red for negative
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        
        // Button renderer for action column
        marketTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        marketTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(marketTable);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPortfolioPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Portfolio summary panel
        JPanel summaryPanel = new JPanel(new BorderLayout());
        double totalValue = calculatePortfolioValue();
        
        portfolioValueLabel = new JLabel("Total Portfolio Value: $" + df.format(totalValue));
        portfolioValueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.add(createStatPanel("Assets", portfolio.size() + ""));
        statsPanel.add(createStatPanel("24h Change", changeFormat.format(Math.random() * 5 - 2) + "%"));
        statsPanel.add(createStatPanel("Profit/Loss", "$" + changeFormat.format(totalValue - 8500)));
        
        summaryPanel.add(portfolioValueLabel, BorderLayout.NORTH);
        summaryPanel.add(statsPanel, BorderLayout.CENTER);
        summaryPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Portfolio table
        String[] columnNames = {"Cryptocurrency", "Holdings", "Value (USD)", "Avg. Buy Price", "Profit/Loss"};
        Object[][] data = new Object[portfolio.size()][5];
        
        int i = 0;
        for (Map.Entry<String, Double> entry : portfolio.entrySet()) {
            String crypto = entry.getKey();
            double amount = entry.getValue();
            int index = Arrays.asList(cryptos).indexOf(crypto);
            double price = prices[index];
            double value = amount * price;
            double avgBuyPrice = price * (0.9 + Math.random() * 0.2); // Random avg buy price
            double profitLoss = value - (amount * avgBuyPrice);
            
            data[i][0] = crypto;
            data[i][1] = df.format(amount);
            data[i][2] = "$" + df.format(value);
            data[i][3] = "$" + df.format(avgBuyPrice);
            data[i][4] = "$" + changeFormat.format(profitLoss);
            i++;
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        portfolioTable = new JTable(model);
        portfolioTable.setRowHeight(30);
        
        // Custom renderer for profit/loss column
        portfolioTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                String text = value.toString();
                if (text.startsWith("$+")) {
                    c.setForeground(new Color(0, 150, 0)); // Green for positive
                } else if (text.startsWith("$-")) {
                    c.setForeground(new Color(200, 0, 0)); // Red for negative
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(portfolioTable);
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTradePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Trade type selection
        JPanel tradeTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buyButton = new JRadioButton("Buy");
        sellButton = new JRadioButton("Sell");
        ButtonGroup tradeGroup = new ButtonGroup();
        tradeGroup.add(buyButton);
        tradeGroup.add(sellButton);
        buyButton.setSelected(true);
        
        tradeTypePanel.add(new JLabel("Trade Type:"));
        tradeTypePanel.add(buyButton);
        tradeTypePanel.add(sellButton);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(tradeTypePanel, gbc);
        
        // Cryptocurrency selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Select Cryptocurrency:"), gbc);
        
        cryptoSelector = new JComboBox<>(cryptos);
        gbc.gridx = 1;
        formPanel.add(cryptoSelector, gbc);
        
        // Current price display
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Current Price:"), gbc);
        
        priceLabel = new JLabel("$" + df.format(prices[0]));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        formPanel.add(priceLabel, gbc);
        
        // Amount input
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Amount:"), gbc);
        
        amountField = new JTextField("1.0");
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        
        // Total calculation
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Total Cost:"), gbc);
        
        totalLabel = new JLabel("$" + df.format(prices[0]));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        formPanel.add(totalLabel, gbc);
        
        // Add event listeners
        cryptoSelector.addActionListener(e -> updatePriceAndTotal());
        amountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updatePriceAndTotal();
            }
        });
        
        // Execute trade button
        JButton executeButton = new JButton("Execute Trade");
        executeButton.setBackground(new Color(59, 89, 152));
        executeButton.setForeground(Color.WHITE);
        executeButton.setFont(new Font("Arial", Font.BOLD, 14));
        executeButton.addActionListener(e -> executeTrade());
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(executeButton, gbc);
        
        // Market information panel
        JPanel marketInfoPanel = new JPanel(new BorderLayout());
        marketInfoPanel.setBorder(BorderFactory.createTitledBorder("Market Information"));
        
        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 10, 10));
        infoGrid.add(createInfoPanel("24h High", "$" + df.format(prices[0] * 1.05)));
        infoGrid.add(createInfoPanel("24h Low", "$" + df.format(prices[0] * 0.95)));
        infoGrid.add(createInfoPanel("24h Volume", "$" + df.format(prices[0] * 1000000)));
        infoGrid.add(createInfoPanel("Market Cap", "$" + df.format(prices[0] * 19000000)));
        infoGrid.add(createInfoPanel("Circulating Supply", df.format(19000000) + " BTC"));
        infoGrid.add(createInfoPanel("All-Time High", "$" + df.format(69000)));
        
        marketInfoPanel.add(infoGrid, BorderLayout.CENTER);
        
        // Chart placeholder
        JPanel chartPanel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(400, 200));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Price Chart"));
        chartPanel.setLayout(new BorderLayout());
        
        JLabel chartPlaceholder = new JLabel("Price chart would be displayed here", JLabel.CENTER);
        chartPlaceholder.setFont(new Font("Arial", Font.ITALIC, 14));
        chartPanel.add(chartPlaceholder, BorderLayout.CENTER);
        
        // Combine panels
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(marketInfoPanel, BorderLayout.NORTH);
        rightPanel.add(chartPanel, BorderLayout.CENTER);
        
        panel.add(formPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(32, 43, 61));
        footerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel copyrightLabel = new JLabel("© 2023 CryptoTrader - Educational Project");
        copyrightLabel.setForeground(Color.WHITE);
        
        JLabel designedByLabel = new JLabel("Designed by WebSparks AI");
        designedByLabel.setForeground(Color.WHITE);
        
        footerPanel.add(copyrightLabel, BorderLayout.WEST);
        footerPanel.add(designedByLabel, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    private JPanel createStatPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private double calculatePortfolioValue() {
        double total = 0;
        for (Map.Entry<String, Double> entry : portfolio.entrySet()) {
            String crypto = entry.getKey();
            double amount = entry.getValue();
            int index = Arrays.asList(cryptos).indexOf(crypto);
            if (index >= 0) {
                total += amount * prices[index];
            }
        }
        return total;
    }
    
    private void updatePriceAndTotal() {
        try {
            int selectedIndex = cryptoSelector.getSelectedIndex();
            double price = prices[selectedIndex];
            priceLabel.setText("$" + df.format(price));
            
            double amount = Double.parseDouble(amountField.getText());
            totalLabel.setText("$" + df.format(price * amount));
        } catch (NumberFormatException e) {
            totalLabel.setText("Invalid amount");
        }
    }
    
    private void executeTrade() {
        try {
            String selectedCrypto = (String) cryptoSelector.getSelectedItem();
            int selectedIndex = cryptoSelector.getSelectedIndex();
            double price = prices[selectedIndex];
            double amount = Double.parseDouble(amountField.getText());
            double total = price * amount;
            
            if (buyButton.isSelected()) {
                // Buy logic
                if (total > accountBalance) {
                    JOptionPane.showMessageDialog(this, 
                            "Insufficient funds. Your balance: $" + df.format(accountBalance),
                            "Trade Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update portfolio
                portfolio.put(selectedCrypto, portfolio.getOrDefault(selectedCrypto, 0.0) + amount);
                accountBalance -= total;
                
                JOptionPane.showMessageDialog(this, 
                        "Successfully bought " + df.format(amount) + " " + selectedCrypto + 
                        " for $" + df.format(total),
                        "Trade Executed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Sell logic
                double currentHolding = portfolio.getOrDefault(selectedCrypto, 0.0);
                if (amount > currentHolding) {
                    JOptionPane.showMessageDialog(this, 
                            "Insufficient holdings. You have: " + df.format(currentHolding) + " " + selectedCrypto,
                            "Trade Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update portfolio
                if (currentHolding - amount <= 0.00001) {
                    portfolio.remove(selectedCrypto);
                } else {
                    portfolio.put(selectedCrypto, currentHolding - amount);
                }
                accountBalance += total;
                
                JOptionPane.showMessageDialog(this, 
                        "Successfully sold " + df.format(amount) + " " + selectedCrypto + 
                        " for $" + df.format(total),
                        "Trade Executed", JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Update UI
            tabbedPane.setSelectedIndex(1); // Switch to portfolio tab
            tabbedPane.setComponentAt(1, createPortfolioPanel());
            
            // Update header balance
            mainPanel.remove(0);
            mainPanel.add(createHeaderPanel(), BorderLayout.NORTH, 0);
            mainPanel.revalidate();
            mainPanel.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Please enter a valid amount",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Custom button renderer for the market table
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(new Color(59, 89, 152));
            setForeground(Color.WHITE);
            return this;
        }
    }
    
    // Custom button editor for the market table
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setBackground(new Color(59, 89, 152));
            button.setForeground(Color.WHITE);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Switch to trade tab and select the crypto
                tabbedPane.setSelectedIndex(2);
                cryptoSelector.setSelectedIndex(marketTable.getSelectedRow());
                updatePriceAndTotal();
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            CryptoTradingApp app = new CryptoTradingApp();
            app.setVisible(true);
        });
    }
}
