package com.kristijanpeshevski.calculator.ui;

import com.kristijanpeshevski.calculator.theme.properties.Theme;
import com.kristijanpeshevski.calculator.theme.ThemeLoader;
import static com.kristijanpeshevski.calculator.util.ColorUtil.hex2Color;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.*;

public class CalculatorUI {
    private static final String FONT_NAME = "Comic Sans MS";
    private static final String DOUBLE_OR_NUMBER_REGEX = "([-]?\\d+[.]\\d*)|(\\d+)|(-\\d+)";
    private static final String APPLICATION_TITLE = "Calculator";
    private static final int WINDOW_WIDTH = 420;
    private static final int WINDOW_HEIGHT = 610;
    private static final int BUTTON_WIDTH = 81;
    private static final int BUTTON_HEIGHT = 71;
    private static final int MARGIN_X = 21;
    private static final int MARGIN_Y = 61;
    private final JFrame window;
    private JComboBox<String> comboCalculatorType;
    private JComboBox<String> comboTheme;
    private JTextField inputScreen;
    private JButton btnC;
    private JButton btnBack;
    private JButton btnMod;
    private JButton btnDiv;
    private JButton btnMul;
    private JButton btnSub;
    private JButton btnAdd;
    private JButton btn0;
    private JButton btn1;
    private JButton btn2;
    private JButton btn3;
    private JButton btn4;
    private JButton btn5;
    private JButton btn6;
    private JButton btn7;
    private JButton btn8;
    private JButton btn9;
    private JButton btnPoint;
    private JButton btnEqual;
    private JButton btnRoot;
    private JButton btnPower;
    private JButton btnLog;
    private JButton btnBin;
    private JButton btnHex;
    private JButton btnMean;
    private JButton btnVariance;
    private char selectedOperator = ' ';
    private boolean go = true;
    private boolean addToDisplay = true;
    private double typedValue = 0;
    private final Map<String, Theme> themesMap;

    public CalculatorUI() {
        themesMap = ThemeLoader.loadThemes();
        window = new JFrame(APPLICATION_TITLE);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);
        int[] columns = { MARGIN_X, MARGIN_X + 90, MARGIN_X + 90 * 2, MARGIN_X + 90 * 3, MARGIN_X + 90 * 4 };
        int[] rows = { MARGIN_Y, MARGIN_Y + 100, MARGIN_Y + 180, MARGIN_Y + 260, MARGIN_Y + 340, MARGIN_Y + 420 };
        initInputScreen(columns, rows);
        initButtons(columns, rows);
        initCalculatorTypeSelector();
        initThemeSelector();
        window.setLayout(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public double calculate(double firstNumber, double secondNumber, char operator) {
        switch (operator) {
            case '+': return firstNumber + secondNumber;
            case '-': return firstNumber - secondNumber;
            case '*': return firstNumber * secondNumber;
            case '/': return firstNumber / secondNumber;
            case '%': return firstNumber % secondNumber;
            case '^': return Math.pow(firstNumber, secondNumber);
            default: return secondNumber;
        }
    }

    private void initThemeSelector() {
        comboTheme = createComboBox(themesMap.keySet().toArray(new String[0]), 230, 30, "Theme");
        comboTheme.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED) return;
            String selectedTheme = (String) event.getItem();
            applyTheme(themesMap.get(selectedTheme));
        });
        if (themesMap.entrySet().iterator().hasNext()) {
            applyTheme(themesMap.entrySet().iterator().next().getValue());
        }
    }

    private void initInputScreen(int[] columns, int[] rows) {
        inputScreen = new JTextField("0");
        inputScreen.setBounds(columns[0], rows[0], 350, 70);
        inputScreen.setEditable(false);
        inputScreen.setBackground(Color.WHITE);
        inputScreen.setFont(new Font(FONT_NAME, Font.PLAIN, 33));
        window.add(inputScreen);
    }

    private void initCalculatorTypeSelector() {
        comboCalculatorType = createComboBox(new String[]{"Standard", "Scientific", "Programmer", "Statistics"},
                20, 30, "Calculator type");
        comboCalculatorType.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED) return;
            String selectedItem = (String) event.getItem();
            switch (selectedItem) {
                case "Standard":
                    window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
                    setScientificButtonsVisible(false);
                    setProgrammerButtonsVisible(false);
                    setStatisticsButtonsVisible(false);
                    break;
                case "Scientific":
                    window.setSize(WINDOW_WIDTH + 80, WINDOW_HEIGHT);
                    setScientificButtonsVisible(true);
                    setProgrammerButtonsVisible(false);
                    setStatisticsButtonsVisible(false);
                    break;
                case "Programmer":
                    window.setSize(WINDOW_WIDTH + 120, WINDOW_HEIGHT);
                    setScientificButtonsVisible(false);
                    setProgrammerButtonsVisible(true);
                    setStatisticsButtonsVisible(false);
                    break;
                case "Statistics":
                    window.setSize(WINDOW_WIDTH + 120, WINDOW_HEIGHT + 80);
                    setScientificButtonsVisible(false);
                    setProgrammerButtonsVisible(false);
                    setStatisticsButtonsVisible(true);
                    break;
            }
        });
    }

    private void setScientificButtonsVisible(boolean visible) {
        btnRoot.setVisible(visible);
        btnPower.setVisible(visible);
        btnLog.setVisible(visible);
    }

    private void setProgrammerButtonsVisible(boolean visible) {
        btnBin.setVisible(visible);
        btnHex.setVisible(visible);
    }

    private void setStatisticsButtonsVisible(boolean visible) {
        btnMean.setVisible(visible);
        btnVariance.setVisible(visible);
    }

    private void initButtons(int[] columns, int[] rows) {
        btnC = createButton("C", columns[0], rows[1]);
        btnC.addActionListener(event -> {
            inputScreen.setText("0");
            selectedOperator = ' ';
            typedValue = 0;
        });
        btnBack = createButton("<-", columns[1], rows[1]);
        btnBack.addActionListener(event -> {
            String str = inputScreen.getText();
            if (str.length() > 1) {
                inputScreen.setText(str.substring(0, str.length() - 1));
            } else {
                inputScreen.setText("0");
            }
        });
        btnMod = createButton("%", columns[2], rows[1]);
        btnMod.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()) || !go) return;
            typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
            displayTypedValue();
            selectedOperator = '%';
            go = false;
            addToDisplay = false;
        });
        btnDiv = createButton("/", columns[3], rows[1]);
        btnDiv.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText())) return;
            processOperator('/');
        });
        btn7 = createButton("7", columns[0], rows[2]);
        btn7.addActionListener(event -> appendDigit("7"));
        btn8 = createButton("8", columns[1], rows[2]);
        btn8.addActionListener(event -> appendDigit("8"));
        btn9 = createButton("9", columns[2], rows[2]);
        btn9.addActionListener(event -> appendDigit("9"));
        btnMul = createButton("*", columns[3], rows[2]);
        btnMul.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText())) return;
            processOperator('*');
        });
        btn4 = createButton("4", columns[0], rows[3]);
        btn4.addActionListener(event -> appendDigit("4"));
        btn5 = createButton("5", columns[1], rows[3]);
        btn5.addActionListener(event -> appendDigit("5"));
        btn6 = createButton("6", columns[2], rows[3]);
        btn6.addActionListener(event -> appendDigit("6"));
        btnSub = createButton("-", columns[3], rows[3]);
        btnSub.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText())) return;
            processOperator('-');
        });
        btn1 = createButton("1", columns[0], rows[4]);
        btn1.addActionListener(event -> appendDigit("1"));
        btn2 = createButton("2", columns[1], rows[4]);
        btn2.addActionListener(event -> appendDigit("2"));
        btn3 = createButton("3", columns[2], rows[4]);
        btn3.addActionListener(event -> appendDigit("3"));
        btnAdd = createButton("+", columns[3], rows[4]);
        btnAdd.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText())) return;
            processOperator('+');
        });
        btnPoint = createButton(".", columns[0], rows[5]);
        btnPoint.addActionListener(event -> {
            if (addToDisplay) {
                if (!inputScreen.getText().contains(".")) {
                    inputScreen.setText(inputScreen.getText() + ".");
                }
            } else {
                inputScreen.setText("0.");
                addToDisplay = true;
            }
            go = true;
        });
        btn0 = createButton("0", columns[1], rows[5]);
        btn0.addActionListener(event -> appendDigit("0"));
        btnEqual = createButton("=", columns[2], rows[5]);
        btnEqual.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText())) return;
            if (go) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                displayTypedValue();
                selectedOperator = '=';
                addToDisplay = false;
            }
        });
        btnEqual.setSize(2 * BUTTON_WIDTH + 10, BUTTON_HEIGHT);
        btnRoot = createButton("√", columns[4], rows[1]);
        btnRoot.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText())) return;
            if (go) {
                typedValue = Math.sqrt(Double.parseDouble(inputScreen.getText()));
                displayTypedValue();
                selectedOperator = '√';
                addToDisplay = false;
            }
        });
        btnRoot.setVisible(false);
        btnPower = createButton("pow", columns[4], rows[2]);
        btnPower.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText())) return;
            processOperator('^');
        });
        btnPower.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        btnPower.setVisible(false);
        btnLog = createButton("ln", columns[4], rows[3]);
        btnLog.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText())) return;
            if (go) {
                typedValue = Math.log(Double.parseDouble(inputScreen.getText()));
                displayTypedValue();
                selectedOperator = 'l';
                addToDisplay = false;
            }
        });
        btnLog.setVisible(false);
        btnBin = createButton("BIN", columns[4], rows[4]);
        btnBin.addActionListener(e -> {
            try {
                int val = Integer.parseInt(inputScreen.getText());
                inputScreen.setText(Integer.toBinaryString(val));
                go = false;
                addToDisplay = false;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(window, "Invalid integer for binary conversion!");
            }
        });
        btnBin.setVisible(false);
        btnHex = createButton("HEX", columns[4], rows[5]);
        btnHex.addActionListener(e -> {
            try {
                int val = Integer.parseInt(inputScreen.getText());
                inputScreen.setText(Integer.toHexString(val).toUpperCase());
                go = false;
                addToDisplay = false;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(window, "Invalid integer for hex conversion!");
            }
        });
        btnHex.setVisible(false);
        btnMean = createButton("Mean", columns[3], rows[5] + 80);
        btnMean.addActionListener(e -> {
            String text = inputScreen.getText();
            String[] parts = text.split(",");
            try {
                double sum = 0;
                for (String p : parts) {
                    sum += Double.parseDouble(p.trim());
                }
                double mean = sum / parts.length;
                inputScreen.setText(String.valueOf(mean));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(window, "Invalid format! Enter numbers like: 10,20,30");
            }
        });
        btnMean.setVisible(false);
        btnVariance = createButton("Var", columns[4], rows[5] + 80);
        btnVariance.addActionListener(e -> {
            String text = inputScreen.getText();
            String[] parts = text.split(",");
            try {
                double sum = 0;
                for (String p : parts) {
                    sum += Double.parseDouble(p.trim());
                }
                double mean = sum / parts.length;
                double varianceSum = 0;
                for (String p : parts) {
                    double val = Double.parseDouble(p.trim());
                    varianceSum += (val - mean) * (val - mean);
                }
                double variance = varianceSum / parts.length;
                inputScreen.setText(String.valueOf(variance));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(window, "Invalid format! Enter numbers like: 10,20,30");
            }
        });
        btnVariance.setVisible(false);
    }

    private void processOperator(char operator) {
        if (go) {
            typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
            displayTypedValue();
            selectedOperator = operator;
            go = false;
            addToDisplay = false;
        } else {
            selectedOperator = operator;
        }
    }

    private void displayTypedValue() {
        if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
            inputScreen.setText(String.valueOf((int) typedValue));
        } else {
            inputScreen.setText(String.valueOf(typedValue));
        }
    }

    private void appendDigit(String digit) {
        if (addToDisplay) {
            if (Pattern.matches("[0]*", inputScreen.getText())) {
                inputScreen.setText(digit);
            } else {
                inputScreen.setText(inputScreen.getText() + digit);
            }
        } else {
            inputScreen.setText(digit);
            addToDisplay = true;
        }
        go = true;
    }

    private JComboBox<String> createComboBox(String[] items, int x, int y, String toolTip) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBounds(x, y, 140, 25);
        combo.setToolTipText(toolTip);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        window.add(combo);
        return combo;
    }

    private JButton createButton(String label, int x, int y) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        btn.setFont(new Font("Comic Sans MS", Font.PLAIN, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);
        window.add(btn);
        return btn;
    }

    private void applyTheme(Theme theme) {
        window.getContentPane().setBackground(hex2Color(theme.getApplicationBackground()));
        comboCalculatorType.setForeground(hex2Color(theme.getTextColor()));
        comboTheme.setForeground(hex2Color(theme.getTextColor()));
        inputScreen.setForeground(hex2Color(theme.getTextColor()));
        btn0.setForeground(hex2Color(theme.getTextColor()));
        btn1.setForeground(hex2Color(theme.getTextColor()));
        btn2.setForeground(hex2Color(theme.getTextColor()));
        btn3.setForeground(hex2Color(theme.getTextColor()));
        btn4.setForeground(hex2Color(theme.getTextColor()));
        btn5.setForeground(hex2Color(theme.getTextColor()));
        btn6.setForeground(hex2Color(theme.getTextColor()));
        btn7.setForeground(hex2Color(theme.getTextColor()));
        btn8.setForeground(hex2Color(theme.getTextColor()));
        btn9.setForeground(hex2Color(theme.getTextColor()));
        btnPoint.setForeground(hex2Color(theme.getTextColor()));
        btnC.setForeground(hex2Color(theme.getTextColor()));
        btnBack.setForeground(hex2Color(theme.getTextColor()));
        btnMod.setForeground(hex2Color(theme.getTextColor()));
        btnDiv.setForeground(hex2Color(theme.getTextColor()));
        btnMul.setForeground(hex2Color(theme.getTextColor()));
        btnSub.setForeground(hex2Color(theme.getTextColor()));
        btnAdd.setForeground(hex2Color(theme.getTextColor()));
        btnRoot.setForeground(hex2Color(theme.getTextColor()));
        btnLog.setForeground(hex2Color(theme.getTextColor()));
        btnPower.setForeground(hex2Color(theme.getTextColor()));
        btnEqual.setForeground(hex2Color(theme.getBtnEqualTextColor()));
        btnBin.setForeground(hex2Color(theme.getTextColor()));
        btnHex.setForeground(hex2Color(theme.getTextColor()));
        btnMean.setForeground(hex2Color(theme.getTextColor()));
        btnVariance.setForeground(hex2Color(theme.getTextColor()));
        comboCalculatorType.setBackground(hex2Color(theme.getApplicationBackground()));
        comboTheme.setBackground(hex2Color(theme.getApplicationBackground()));
        inputScreen.setBackground(hex2Color(theme.getApplicationBackground()));
        btn0.setBackground(hex2Color(theme.getNumbersBackground()));
        btn1.setBackground(hex2Color(theme.getNumbersBackground()));
        btn2.setBackground(hex2Color(theme.getNumbersBackground()));
        btn3.setBackground(hex2Color(theme.getNumbersBackground()));
        btn4.setBackground(hex2Color(theme.getNumbersBackground()));
        btn5.setBackground(hex2Color(theme.getNumbersBackground()));
        btn6.setBackground(hex2Color(theme.getNumbersBackground()));
        btn7.setBackground(hex2Color(theme.getNumbersBackground()));
        btn8.setBackground(hex2Color(theme.getNumbersBackground()));
        btn9.setBackground(hex2Color(theme.getNumbersBackground()));
        btnPoint.setBackground(hex2Color(theme.getNumbersBackground()));
        btnC.setBackground(hex2Color(theme.getOperatorBackground()));
        btnBack.setBackground(hex2Color(theme.getOperatorBackground()));
        btnMod.setBackground(hex2Color(theme.getOperatorBackground()));
        btnDiv.setBackground(hex2Color(theme.getOperatorBackground()));
        btnMul.setBackground(hex2Color(theme.getOperatorBackground()));
        btnSub.setBackground(hex2Color(theme.getOperatorBackground()));
        btnAdd.setBackground(hex2Color(theme.getOperatorBackground()));
        btnRoot.setBackground(hex2Color(theme.getOperatorBackground()));
        btnLog.setBackground(hex2Color(theme.getOperatorBackground()));
        btnPower.setBackground(hex2Color(theme.getOperatorBackground()));
        btnEqual.setBackground(hex2Color(theme.getBtnEqualBackground()));
        btnBin.setBackground(hex2Color(theme.getOperatorBackground()));
        btnHex.setBackground(hex2Color(theme.getOperatorBackground()));
        btnMean.setBackground(hex2Color(theme.getOperatorBackground()));
        btnVariance.setBackground(hex2Color(theme.getOperatorBackground()));
    }
}
