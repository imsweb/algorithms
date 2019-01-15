/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package lab;

import com.imsweb.algorithms.surgery.SiteSpecificSurgeryUtils;
import com.imsweb.algorithms.surgery.SurgeryRowDto;
import com.imsweb.algorithms.surgery.SurgeryTableDto;
import com.imsweb.algorithms.surgery.SurgeryTablesDto;
import com.imsweb.seerutilsgui.SeerGuiUtils;
import com.imsweb.seerutilsgui.SeerList;
import com.imsweb.seerutilsgui.table.SeerColumn;
import com.imsweb.seerutilsgui.table.SeerTable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.View;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@SuppressWarnings("ConstantConditions")
public class SurgeryTablesViewerLab extends JFrame {

    private SeerList<SurgeryTableDto> _tablesList;
    private JSplitPane _pane;

    public SurgeryTablesViewerLab() {
        JPanel contentPnl = SeerGuiUtils.createContentPanel(this, 5);

        SurgeryTablesDto data = SiteSpecificSurgeryUtils.getInstance().getTables(2018);

        // WEST - list of tables
        JPanel westPnl = SeerGuiUtils.createPanel();
        westPnl.setBorder(new EmptyBorder(0, 0, 0, 5));

        JPanel filterPnl = SeerGuiUtils.createPanel();
        filterPnl.setBorder(new EmptyBorder(0, 0, 5, 0));
        JTextField filterField = new JTextField();
        filterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                _tablesList.filter(filterField.getText());
            }
        });
        filterPnl.add(filterField, BorderLayout.CENTER);
        westPnl.add(filterPnl, BorderLayout.NORTH);

        _tablesList = new SeerList<>(data.getTables(), SeerList.DISPLAY_MODE_DOTTED_LINES, SeerList.FILTERING_MODE_STARTS_WITH, false, null);
        _tablesList.setBorder(new LineBorder(SeerGuiUtils.COLOR_COMP_FOCUS_OUT));
        _tablesList.setPreferredSize(new Dimension(250, _tablesList.getPreferredSize().height));
        _tablesList.addListSelectionListener(e -> {
            SurgeryTableDto table = (SurgeryTableDto)_tablesList.getSelectedValue();
            if (table != null) {
                _pane.setLeftComponent(createTablePanel(table));
                _pane.setRightComponent(buildDetailsPanel(table));
                _pane.setDividerLocation(0.75);
            }
        });
        westPnl.add(_tablesList, BorderLayout.CENTER);
        contentPnl.add(westPnl, BorderLayout.WEST);

        // CENTER - tables
        JPanel centerPnl = SeerGuiUtils.createPanel();
        _pane = new JSplitPane();
        _pane.setBackground(SeerGuiUtils.COLOR_APPLICATION_BACKGROUND);
        _pane.setOpaque(false);
        _pane.setBorder(null);
        _pane.setDividerSize(5);
        if (_pane.getUI() instanceof BasicSplitPaneUI) {
            ((BasicSplitPaneUI)_pane.getUI()).getDivider().setBorder(null);
            ((BasicSplitPaneUI)_pane.getUI()).getDivider().setBackground(SeerGuiUtils.COLOR_APPLICATION_BACKGROUND);
        }
        centerPnl.add(_pane, BorderLayout.CENTER);
        contentPnl.add(centerPnl, BorderLayout.CENTER);

        pack();
        _pane.setDividerLocation(0.75);

        // by default show first value selected in the list, and set the divider to the middle...
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                _tablesList.setSelectedIndex(0);
                SurgeryTablesViewerLab.this.removeComponentListener(this);
            }
        });
    }

    private JPanel createTablePanel(SurgeryTableDto table) {
        JPanel tablePnl = SeerGuiUtils.createPanel();

        // columns
        List<SeerColumn> cols = new Vector<>();
        cols.add(new SeerColumn("Code").setWidth(SeerColumn.SeerColumnWidthType.MIN).setVisible(false));
        cols.add(new SeerColumn("Code/Description"));
        cols.add(new SeerColumn("Level").setContentType(Integer.class).setWidth(SeerColumn.SeerColumnWidthType.MIN).setVisible(false));

        // data
        Vector<Vector<Object>> data = new Vector<>();
        for (SurgeryRowDto dto : table.getRow()) {

            Vector<Object> row = new Vector<>();
            row.add(dto.getCode());
            if (dto.getDescription() != null) {
                StringBuilder buf = new StringBuilder();
                buf.append("<html>");
                try {
                    BufferedReader reader = new BufferedReader(new StringReader(dto.getDescription()));
                    String line = reader.readLine();
                    while (line != null) {
                        List<String> subLines = new ArrayList<>();
                        subLines.add(line);

                        for (String subLine : subLines) {
                            if (dto.getCode() != null && !dto.getCode().isEmpty() && subLines.indexOf(subLine) == 0)
                                buf.append(dto.getCode()).append("&nbsp;&nbsp;&nbsp;");
                            buf.append(subLine.replace("<html>", "")).append("\n");
                        }

                        line = reader.readLine();
                    }
                    if (buf.length() > 0)
                        buf.setLength(buf.length() - 1);
                    if (!buf.toString().endsWith("</html>"))
                        buf.append("</html>");
                    row.add(buf.toString());
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else
                row.add("");

            row.add(dto.getLevel());

            data.add(row);
        }

        // create table
        SeerTable seerTable = new SeerTable(cols, data, false, false, false, null);
        seerTable.getColumnModel().getColumn(0).setCellRenderer(new SurgeryTableCellRenderer());
        tablePnl.add(new JScrollPane(seerTable), BorderLayout.CENTER);

        if (table.getPostNote() != null) {
            JPanel southPnl = SeerGuiUtils.createPanel();
            southPnl.setLayout(new BoxLayout(southPnl, BoxLayout.Y_AXIS));
            JEditorPane noteEditorPane = new JEditorPane();
            noteEditorPane.setEditorKit(new HTMLEditorKit());
            noteEditorPane.setEditable(false);
            noteEditorPane.setBackground(SeerGuiUtils.COLOR_APPLICATION_BACKGROUND);
            noteEditorPane.setFont(new Font("Monospaced", Font.PLAIN, 11));
            noteEditorPane.setText(table.getPostNote());
            JScrollPane notePane = new JScrollPane(noteEditorPane);
            notePane.setBorder(null);
            southPnl.add(notePane);
            tablePnl.add(southPnl, BorderLayout.SOUTH);
        }

        return tablePnl;
    }

    private JPanel buildDetailsPanel(SurgeryTableDto table) {
        JPanel detailsPnl = SeerGuiUtils.createPanel();

        StringBuilder buf = new StringBuilder();
        if (table.getPreNote() != null)
            buf.append("<b>Notes</b><br/><br/>").append(table.getPreNote());

        JEditorPane detailPane = new JEditorPane();
        detailPane.setEditorKit(new HTMLEditorKit());
        detailPane.setEditable(false);
        detailPane.setFont(new Font("Monospaced", Font.PLAIN, 11));
        detailPane.setBackground(new Color(240, 240, 240));
        detailPane.setText(buf.toString());
        JScrollPane pane = new JScrollPane(detailPane);
        pane.setBorder(SeerGuiUtils.BORDER_TEXT_FIELD_OUT);
        detailsPnl.add(pane, BorderLayout.CENTER);

        return detailsPnl;
    }

    private static class SurgeryTableCellRenderer extends JLabel implements TableCellRenderer {

        private final Map<JTable, Map<Integer, Map<Integer, Integer>>> _cellSizes = new HashMap<>();

        private final JLabel _lbl = new JLabel();

        public SurgeryTableCellRenderer() {
            setVerticalAlignment(JLabel.TOP);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(obj == null ? "" : obj.toString());

            TableColumnModel columnModel = table.getColumnModel();

            if (columnModel != null) {
                setSize(columnModel.getColumn(column).getWidth(), Integer.MAX_VALUE);

                int heightWanted = calculateHeight(columnModel.getColumn(column).getWidth(), table.getModel().getValueAt(row, 2));

                addSize(table, row, column, heightWanted);
                heightWanted = findTotalMaximumRowSize(table, row);
                if (heightWanted != table.getRowHeight(row))
                    table.setRowHeight(row, heightWanted);
            }

            Border border = BorderFactory.createEmptyBorder(0, Math.max(5, (Integer)table.getModel().getValueAt(row, 2) * 25), 0, 0);
            if (isSelected) {
                if (hasFocus) {
                    this.setBackground(SeerTable.COLOR_TABLE_ROW_SELECTED);
                    this.setForeground(SeerTable.COLOR_TABLE_CELL_FOCUSED_LBL);
                    this.setBorder(BorderFactory.createCompoundBorder(SeerTable.TABLE_FOCUSED_CELL_BORDER, border));
                }
                else {
                    this.setBackground(SeerTable.COLOR_TABLE_ROW_SELECTED);
                    this.setForeground(SeerTable.COLOR_TABLE_ROW_SELECTED_LBL);
                    this.setBorder(BorderFactory.createCompoundBorder(SeerTable.TABLE_DEFAULT_CELL_BORDER, border));
                }
            }
            else {
                if (!(table instanceof SeerTable))
                    throw new RuntimeException("Was expecting a SeerTable");

                this.setBackground(((row % 2) == 0 || !((SeerTable)table).getAlternateRowColors()) ? SeerTable.COLOR_TABLE_ROW_ODD : SeerTable.COLOR_TABLE_ROW_EVEN);
                this.setForeground(SeerTable.COLOR_TABLE_ROW_LBL);
                this.setBorder(BorderFactory.createCompoundBorder(SeerTable.TABLE_DEFAULT_CELL_BORDER, border));
            }

            return this;
        }

        private void addSize(JTable table, int row, int column, int height) {
            Map<Integer, Map<Integer, Integer>> rows = _cellSizes.computeIfAbsent(table, k -> new HashMap<>());
            Map<Integer, Integer> rowheights = rows.computeIfAbsent(row, k -> new HashMap<>());
            rowheights.put(column, height);
        }

        private int findTotalMaximumRowSize(JTable table, int row) {
            int maximumHeight = 16;
            Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {
                TableColumn tc = columns.nextElement();
                TableCellRenderer cellRenderer = tc.getCellRenderer();
                if (cellRenderer instanceof SurgeryTableCellRenderer) {
                    SurgeryTableCellRenderer tar = (SurgeryTableCellRenderer)cellRenderer;
                    maximumHeight = Math.max(maximumHeight, tar.findMaximumRowSize(table, row));
                }
            }
            return maximumHeight;
        }

        private int findMaximumRowSize(JTable table, int row) {
            Map<Integer, Map<Integer, Integer>> rows = _cellSizes.get(table);
            if (rows == null)
                return 0;
            Map<Integer, Integer> rowheights = rows.get(row);
            if (rowheights == null)
                return 0;
            int maximumHeight = 0;
            for (Map.Entry<Integer, Integer> entry : rowheights.entrySet())
                maximumHeight = Math.max(maximumHeight, entry.getValue());
            return maximumHeight;
        }

        private int calculateHeight(int colWidth, Object level) {
            int border = Integer.parseInt(level.toString()) * 25;

            _lbl.setText(getText());
            View view = (View)_lbl.getClientProperty(BasicHTML.propertyKey);
            int height = 0;
            if (view != null) {
                view.setSize(colWidth - border, Integer.MAX_VALUE);

                height = ((int)view.getPreferredSpan(View.Y_AXIS)) + 2;
            }

            return height;
        }
    }

    public static void main(String[] args) throws IOException {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        SurgeryTablesViewerLab viewer = new SurgeryTablesViewerLab();
        viewer.setTitle("Site-specific Surgery Table Viewer");
        viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewer.setPreferredSize(new Dimension(1600, 800));

        SeerGuiUtils.showAndPosition(viewer, null);
    }
}

