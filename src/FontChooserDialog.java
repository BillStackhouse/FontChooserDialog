package com.billsdesk.github.fontchooserdialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * <b>Description</b>
 * </p>
 * Dialog for selecting a Font (name, style, size). Lists font family, associated fonts, styles, and
 * sizes. Sizes maybe selected with a list, entering size in a field, or using a slider. Setting a
 * font will update all sliders and sample text.
 * <p>
 * Filters can be defines to select subsets of fonts, and on MacOS font collections defined in the
 * Font Book applications will be added as filters. Filters will be displaying in a JComboBox.
 * <p>
 * <b>Example</b> <pre>{@code
       final Font intialFont = new Font("Courier", Font.BOLD, 24);
       final FontChooserDialog dialog =  new FontChooserDialog(null, "Font", intialFont).showDialog();
       Font newFont = dialog.getSelectedFont();
     }</pre>
 * <p>
 * <b>Screenshot</b>
 * <p>
 * <img src="doc-files/FontChooserDialog.jpg" width="100%" alt="FontChooserDialog.jpg">
 * </p>
 * <p>
 * <b>Build Requirements</b>
 * </p>
 * Java 11 <pre>{@code
        <!-- https://mvnrepository.com/artifact/com.google.code.findbugs/jsr305 -->
        <dependency>
            <groupId>com.google.code.findbugs</groupId>
            <artifactId>jsr305</artifactId> <!--  javax.annotations -->
            <version>3.0.2</version>
        </dependency>
 * }</pre>
 * <p>
 * <b>Contact Information</b> Author: Bill Stackhouse<br>
 * email: billsdesk@gmail.com<br>
 *
 * @see <a href=
 *      "http://bhiggs.x10hosting.com/Courses/JavaProgramming/Swing/Dialogs/FontChooserDialog.htm">Derived
 *      from source from here</a>
 *
 * @author Bill
 * @version $Rev: 8233 $ $Date: 2020-07-19 16:03:39 -0700 (Sun, 19 Jul 2020) $
 */
public class FontChooserDialog
    extends
        JDialog {

    private static final long serialVersionUID = 1L;

    private static Properties properties       = new Properties();
    static {
        // if file not found then strings will have default values
        setProperties(FontChooserDialog.class, "messages.properties");
    }

    /**
     * Allow User to specify a different properties files for the external strings. Must be called
     * prior to creating the dialog.
     *
     * @param aClass
     *            Class for the anchor of the location
     * @param name
     *            name of the properties, may include relative path
     */
    public static void setProperties(final Class< ? > aClass, final String name) {
        try {
            properties.load(aClass.getResourceAsStream(name));
        } catch (final NullPointerException | IOException error) {
            // not found, leave empty and use default values.
        }
    }

    // External Strings
    private static final String STR_ALL_FONTS     =                                 //
            properties.getProperty("FontChooserDialog.all_fonts", "All Fonts");
    private static final String STR_BOLD          =                                 //
            properties.getProperty("FontChooserDialog.bold", "Bold");
    private static final String STR_BOLD_ITALIC   =                                 //
            properties.getProperty("FontChooserDialog.bold_italic", "Bold+Italic");
    private static final String STR_CANCEL        =                                 //
            properties.getProperty("FontChooserDialog.cancel", "Cancel");
    private static final String STR_FAMILY        =                                 //
            properties.getProperty("FontChooserDialog.family", "Family");
    private static final String STR_FONT          =                                 //
            properties.getProperty("FontChooserDialog.font", "Font");
    private static final String STR_FONT_STANDARD =                                 //
            properties.getProperty("FontChooserDialog.font_standard",
                                   "Arial,Courier,Garamond,Helvetica,Monaco,Times");
    private static final String STR_ITALIC        =                                 //
            properties.getProperty("FontChooserDialog.italic", "Italic");
    private static final String STR_OK            =                                 //
            properties.getProperty("FontChooserDialog.ok", "OK");
    private static final String STR_PREVIEW       =                                 //
            properties.getProperty("FontChooserDialog.preview",
                                   "The quick brown fox jumps over the lazy dog");
    private static final String STR_REGULAR       =                                 //
            properties.getProperty("FontChooserDialog.regular", "Regular");
    private static final String STR_SIZE          =                                 //
            properties.getProperty("FontChooserDialog.size", "Size");
    private static final String STR_SIZE_LIST     =                                 //
            properties.getProperty("FontChooserDialog.size_list",
                                   "10,12,14,16,18,20,22,24,36,48,72");
    private static final String STR_SIZE_DEFAULT  =                                 //
            properties.getProperty("FontChooserDialog.size_default", "12");
    private static final String STR_STANDARD      =                                 //
            properties.getProperty("FontChooserDialog.standard", "Standard");
    private static final String STR_STYLE         =                                 //
            properties.getProperty("FontChooserDialog.style", "Style");

    // JComponent names used with AspectJ JUnit testing.
    public static final String  NAME_OK           = "okay";                         //$NON-NLS-1$ AspectJ name
    public static final String  NAME_CANCEL       = "cancel";                       //$NON-NLS-1$ AspectJ name

    // Default window title;
    private static final String DEFAULT_TITLE     = "Font";                         //$NON-NLS-1$

    private final JPanel        mChooserPane;
    private final SettingsPanel mSettingsPanel;
    private int                 mResult;

    /**
     * Create the Dialog with the window title set to "Font", and the 1st item of each list (family,
     * font, style, size) selected.
     */
    public FontChooserDialog() {
        this(null, null, null, (AbstractFontFilter[]) null);
    }

    /**
     * Create the Dialog with the window title defined.
     *
     * @param frame
     *            the parent component of the dialog, maybe null
     * @param title
     *            the dialog title (for the title bar)
     */
    public FontChooserDialog(@Nullable final JFrame frame, final String title) {
        this(frame, title, null, (AbstractFontFilter[]) null);
    }

    /**
     * Create the Dialog with the window title defined and the initial font to select.
     *
     * @param frame
     *            the parent component of the dialog
     * @param title
     *            the dialog title (for the title bar)
     * @param selectedFont
     *            Initially selected font. If null then select first of each list.
     */
    public FontChooserDialog(@Nullable final JFrame frame,
                             @Nullable final String title,
                             @Nullable final Font selectedFont) {
        this(frame, title, selectedFont, (AbstractFontFilter[]) null);
    }

    /**
     * Create the Dialog with the window title defined, the initial font to select, and filters to
     * add to the filter registry.
     *
     * @param frame
     *            the parent component of the dialog
     * @param title
     *            the dialog title (for the title bar)
     * @param selectedFont
     *            the initially selected font. If null then select first of each list.
     * @param filters
     *            list of filters to add, if null do not add any
     */
    public FontChooserDialog(@Nullable final JFrame frame,
                             @Nullable final String title,
                             @Nullable final Font selectedFont,
                             @Nullable final AbstractFontFilter... filters) {
        super(frame, title == null ? DEFAULT_TITLE : title, ModalityType.APPLICATION_MODAL);
        setName("FontChooserDialog");

        // Order of the next 2 lines is important.
        FontRegistry.getInstance(); // load all font information
        FilterRegistry.getInstance(); // load internal filters
        if (filters != null) {
            FilterRegistry.getInstance().addFilter(filters);
        }
        // if MacOS then load files in <user.home>/Library/FontCollections and create filters
        FilterRegistry.processCollections();

        mChooserPane = new JPanel();
        mChooserPane.setLayout(new BorderLayout());

        final PreviewPanel previewPane = new PreviewPanel(this);
        mSettingsPanel = new SettingsPanel(previewPane);
        mChooserPane.add(mSettingsPanel, BorderLayout.CENTER);
        mChooserPane.add(previewPane, BorderLayout.SOUTH);

        // Set contents of dialog
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mChooserPane, BorderLayout.CENTER);

        // Cancel button
        final JButton cancelButton = new JButton(STR_CANCEL);
        cancelButton.setName(NAME_CANCEL);
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                setVisible(false); // just hide the dialog
                mResult = JOptionPane.CANCEL_OPTION;
            }
        });

        // OK Button
        final JButton okButton = new JButton(STR_OK);
        okButton.setName(NAME_OK);
        getRootPane().setDefaultButton(okButton);
        okButton.setActionCommand("ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                setVisible(false); // just hide the dialog
                mResult = JOptionPane.YES_OPTION;
            }
        });

        // Create lower button panel
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPane.add(cancelButton);
        buttonPane.add(okButton);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        pack();
        if (selectedFont != null) {
            setSelectFont(selectedFont);
        }
        setLocationRelativeTo(null); // center position of screen
    }

    /**
     * Display the dialog.
     *
     * @return this
     */
    public FontChooserDialog showDialog() {
        setVisible(true);
        return this;
    }

    /**
     * @return JOptionPane.YES_OPTION or JOptionPane.CANCEL_OPTION
     */
    public int getResult() {
        return mResult;
    }

    public Font getSelectedFont() throws IllegalArgumentException {
        return mSettingsPanel.getSelectedFont();
    }

    public void setSelectFont(final Font font) {
        mSettingsPanel.setSelectedFont(font);
    }

    /**
     * The area of the dialog with the lists of font families, fonts, styles, sizes, size text
     * field, size slider and filter JComboBox.
     */
    private static class SettingsPanel
        extends
            JPanel {

        private static final long       serialVersionUID = 1L;
        public static final String      FILTER_NAME      = "filterSelection"; // AspectJ name

        private final FontFamilyList    mFontFamilyList;
        private final FontList          mFontList;
        private final FontStyleList     mFontStyleList;
        private final FontSizeList      mFontSizeList;
        private final FontSizeText      mFontSizeText;
        private final FontSizeSlider    mFontSizeSlider;
        private final JComboBox<String> mComboBox;

        /**
         * @param previewPanel
         *            listener for updating the sample text as inputs are changed
         */
        public SettingsPanel(final PreviewPanel previewPanel) {
            super();
            setLayout(new BorderLayout());
            mFontFamilyList = new FontFamilyList();
            mFontList = new FontList();
            mFontList.setPreferredSize(new Dimension(
                    ((int) (mFontFamilyList.getPreferredSize().width * 0.6)),
                    mFontFamilyList.getPreferredSize().height));
            mFontStyleList = new FontStyleList();
            mFontSizeList = new FontSizeList();
            mFontSizeText = new FontSizeText();
            mFontSizeSlider = new FontSizeSlider();

            mFontSizeSlider.addChangeListener(mFontSizeText);
            mFontSizeSlider.addChangeListener(mFontSizeList);

            mFontFamilyList.addListSelectionListener(mFontList);

            // update values after leaving JTextField
            mFontSizeText.addFocusListener(mFontSizeSlider);
            mFontSizeText.addFocusListener(mFontSizeList);

            mFontSizeList.addListSelectionListener(mFontSizeText);
            mFontSizeList.addListSelectionListener(mFontSizeSlider);

            mFontFamilyList.setSelectedIndex(0);
            mFontSizeList.setSelectedIndex(0);

            mComboBox = FilterRegistry.getInstance().getComboBox();
            mComboBox.setName(FILTER_NAME);
            mComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent event) {
                    final String selectedName = (String) mComboBox.getSelectedItem();
                    mFontFamilyList.setFamilyNames(FilterRegistry.getInstance()
                                                                 .getByName(selectedName));
                }
            });

            // Family name
            final Box familyBox = Box.createVerticalBox();
            familyBox.add(Box.createVerticalStrut(10));
            final JLabel familyNameLabel = new JLabel(STR_FAMILY);
            familyBox.add(familyNameLabel);
            // mFontFamilyList.addListSelectionListener(listener);
            final JScrollPane familyPane = new JScrollPane(mFontFamilyList);
            familyBox.add(familyPane);
            familyBox.add(Box.createVerticalStrut(10));

            // Font name
            final Box fontBox = Box.createVerticalBox();
            fontBox.add(Box.createVerticalStrut(10));
            final JLabel fontNameLabel = new JLabel(STR_FONT);
            fontBox.add(fontNameLabel);
            mFontList.addListSelectionListener(previewPanel);
            final JScrollPane fontPane = new JScrollPane(mFontList);
            fontBox.add(fontPane);
            fontBox.add(Box.createVerticalStrut(10));

            // Font style
            final Box styleBox = Box.createVerticalBox();
            styleBox.add(Box.createVerticalStrut(10));
            final JLabel fontStyleLabel = new JLabel(STR_STYLE);
            styleBox.add(fontStyleLabel);
            mFontStyleList.addListSelectionListener(previewPanel);
            final JScrollPane stylePane = new JScrollPane(mFontStyleList);
            styleBox.add(stylePane);
            styleBox.add(Box.createVerticalStrut(10));

            // Font size (size list, size text, and size slider)
            final Box sizeBox = Box.createVerticalBox();
            sizeBox.add(Box.createVerticalStrut(10));
            final JLabel fontSizeLabel = new JLabel(STR_SIZE);
            sizeBox.add(fontSizeLabel);
            final JPanel tempPane = new JPanel();
            tempPane.setLayout(new BorderLayout());
            mFontSizeList.addListSelectionListener(previewPanel);
            mFontSizeText.addFocusListener(mFontSizeList);
            mFontSizeText.addFocusListener(previewPanel);
            tempPane.add(mFontSizeText, BorderLayout.NORTH);
            tempPane.add(mFontSizeSlider, BorderLayout.EAST);
            final JScrollPane sizePane = new JScrollPane(mFontSizeList);
            tempPane.add(sizePane, BorderLayout.CENTER);
            sizeBox.add(tempPane);
            sizeBox.add(Box.createVerticalStrut(10));

            // filter comboBox
            final JPanel filterNames = new JPanel();
            filterNames.setLayout(new BorderLayout());
            filterNames.add(mComboBox, BorderLayout.WEST);
            filterNames.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

            final Box mainBox = Box.createHorizontalBox();
            mainBox.add(Box.createHorizontalStrut(10)); // space between
            mainBox.add(familyBox);
            mainBox.add(Box.createHorizontalStrut(10));
            mainBox.add(fontBox);
            mainBox.add(Box.createHorizontalStrut(10));
            mainBox.add(styleBox);
            mainBox.add(Box.createHorizontalStrut(10));
            mainBox.add(sizeBox);
            mainBox.add(Box.createHorizontalStrut(10));

            add(mainBox, BorderLayout.CENTER);
            add(filterNames, BorderLayout.SOUTH);
        }

        /**
         * @return Returns the selected font, derived from the user's list choices.
         */
        public Font getSelectedFont() {
            final Font result = new Font(
                    FontRegistry.getInstance()
                                .getFontName(mFontFamilyList.getFamilyName(),
                                             mFontList.getFontName()),
                    mFontStyleList.getFontStyle(),
                    mFontSizeText.getTextSize());
            return result;
        }

        /**
         * Set all lists and text to match this font.
         *
         * @param font
         *            font to set dialog with
         */
        public void setSelectedFont(final Font font) {
            mFontFamilyList.setSelectedValue(font.getFamily(), true);
            mFontList.setSelectedValue(FontRegistry.stripFamily(font), true);
            mFontStyleList.setSelectedValue(FontStyle.fromInt(font.getStyle()).get().getName(),
                                            true);
            mFontSizeList.setSelectedValue(String.valueOf(font.getSize()), true);
        }
    }

    /**
     * List of all installed font families or subset based on selected filter.
     */
    public static class FontFamilyList
        extends
            AbstractJList {

        private static final long  serialVersionUID = 1L;
        public static final String NAME             = "fontFamilyName"; //$NON-NLS-1$ AspectJ name

        public FontFamilyList() {
            super(makeModel());
            setName(NAME);
        }

        public String getFamilyName() {
            final String name = getSelectedValue();
            return name;
        }

        public void setFamilyNames(final Predicate<String> filter) {
            final String selection = getSelectedValue();
            final DefaultListModel<String> model = (DefaultListModel<String>) getModel();
            model.removeAllElements();
            model.addAll(Arrays.asList(FontRegistry.getInstance().getFamilyNames())
                               .stream()
                               .filter(filter)
                               .sorted()
                               .collect(Collectors.toList()));
            setModel(model);
            if (selection == null) {
                setSelectedIndex(0);
                ensureIndexIsVisible(0);
            } else {
                setSelectedValue(selection, true);
                if (getSelectedIndex() == -1) {
                    setSelectedIndex(0);
                    ensureIndexIsVisible(0);
                }
            }
        }

        private static DefaultListModel<String> makeModel() {
            final DefaultListModel<String> model = new DefaultListModel<>();
            model.addAll(Arrays.asList(FontRegistry.getInstance().getFamilyNames())
                               .stream()
                               .sorted()
                               .collect(Collectors.toList()));
            return model;
        }
    }

    /**
     * List of all installed fonts or subset based on selected filter.
     */
    public static class FontList
        extends
            AbstractJList
        implements
            ListSelectionListener {

        private static final long  serialVersionUID = 1L;
        public static final String NAME             = "fontName"; //$NON-NLS-1$ AspectJ name

        public FontList() {
            super(makeModel());
            setName(FontList.NAME);
        }

        public String getFontName() throws IllegalArgumentException {
            final String result = getSelectedValue();
            if (result == null) {
                throw new IllegalArgumentException();
            }
            return result;
        }

        public void setFontNames(final String familyName) {
            final String selection = getSelectedValue();
            final DefaultListModel<String> model = (DefaultListModel<String>) getModel();
            model.removeAllElements();
            model.addAll(Arrays.asList(FontRegistry.getInstance().getFontNames(familyName))
                               .stream()
                               .collect(Collectors.toList()));
            setModel(model);
            if (selection == null) {
                setSelectedIndex(0);
                ensureIndexIsVisible(0);
            } else {
                setSelectedValue(selection, true);
                if (getSelectedIndex() == -1) {
                    setSelectedIndex(0);
                    ensureIndexIsVisible(0);
                }
            }
        }

        @Override
        public void valueChanged(final ListSelectionEvent event) {
            @SuppressWarnings("unchecked")
            final JList<String> list = (JList<String>) event.getSource();
            final ListSelectionModel model = list.getSelectionModel();
            if (!model.isSelectionEmpty()) {
                setFontNames(list.getSelectedValue());
            }
        }

        private static DefaultListModel<String> makeModel() {
            final DefaultListModel<String> model = new DefaultListModel<>();
            model.addAll(Arrays.asList(FontRegistry.getInstance().getFontNames("Courier")) //$NON-NLS-1$
                               .stream()
                               .collect(Collectors.toList()));
            return model;
        }
    }

    /**
     * List of all installed font styles.
     */
    static class FontStyleList
        extends
            AbstractJList {

        private static final long  serialVersionUID = 1L;
        public static final String NAME             = "styleName"; //$NON-NLS-1$ AspectJ name

        public FontStyleList() {
            super(makeModel());
            setName(NAME);
            setSelectedIndex(0);
            setVisibleRowCount(5);
        }

        public int getFontStyle() {
            return FontStyle.fromName(getSelectedValue()).get().getStyle();
        }

        private static DefaultListModel<String> makeModel() {
            final DefaultListModel<String> model = new DefaultListModel<>();
            model.addAll(Arrays.asList(FontStyle.getNames()).stream().collect(Collectors.toList()));
            return model;
        }
    }

    /**
     * List of all installed font sizes based upon FontChooserDialog.size_list in .properties file.
     */
    public static class FontSizeList
        extends
            AbstractJList
        implements
            FocusListener,
            ChangeListener {

        private static final long  serialVersionUID = 1L;
        public static final String NAME             = "sizeName"; //$NON-NLS-1$ AspectJ name

        FontSizeList() {
            super(makeModel());
            setName(NAME);
            setSelectedValue(STR_SIZE_DEFAULT, true);
            setVisibleRowCount(5);
            final FontMetrics fontMetrics = new JLabel().getFontMetrics(getFont());
            setPreferredSize(new Dimension(fontMetrics.stringWidth("9999"), //$NON-NLS-1$
                    getPreferredSize().height));
        }

        /**
         * @return the selected font size.
         */
        public int getFontSize() throws IllegalArgumentException {
            try {
                final int size = Integer.parseInt(getSelectedValue());
                return size;
            } catch (final NumberFormatException error) {
                throw new IllegalArgumentException(error);
            }
        }

        /**
         * Update based on the new slider value.
         *
         * @param event
         *            event
         *
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
         */
        @Override
        public void stateChanged(final ChangeEvent event) {
            final FontSizeSlider slider = (FontSizeSlider) event.getSource();
            setSelectedValue(String.valueOf(slider.getValue()), true);
        }

        @Override
        public void focusGained(final FocusEvent event) {
            // do nothing
        };

        /**
         * Update with the new value in the size text box when it loses focus.
         *
         * @param event
         *            event
         *
         * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
         */
        @Override
        public void focusLost(final FocusEvent event) {
            final FontSizeText text = (FontSizeText) event.getSource();
            try {
                setSelectedValue(text.getTextSize(), true);
            } catch (final IllegalArgumentException error) {
                // ignore
            }
        }

        private static DefaultListModel<String> makeModel() {
            final DefaultListModel<String> model = new DefaultListModel<>();
            model.addAll(Arrays.asList(STR_SIZE_LIST.split(",", Integer.MAX_VALUE))
                               .stream()
                               .collect(Collectors.toList()));
            return model;
        }
    }

    /**
     * Base class for the font family, font, style, and size list to have a common way to set the
     * preferred size, initial selection, and visible number of rows.
     */
    private abstract static class AbstractJList
        extends
            JList<String> {

        private static final long serialVersionUID = 1L;

        public AbstractJList(final DefaultListModel<String> model) {
            super(model);
            setSelectedIndex(0);
            setVisibleRowCount(10);
            /*
             * get the max width of all font names so the list will be the same width regardless of
             * the filtered list.
             */
            final FontMetrics fontMetrics = getFontMetrics(getFont());
            final int maxWidth = Arrays.asList(model.toArray())
                                       .stream()
                                       .map(s -> s.toString())
                                       .mapToInt(s -> fontMetrics.stringWidth(s))
                                       .max()
                                       .getAsInt();
            setPreferredSize(new Dimension(maxWidth + 20, getPreferredSize().height));
        }
    }

    /**
     * A text field to type in a new size. When the focus is lost, the font size list and slider
     * will adjust.
     */
    public static class FontSizeText
        extends
            JTextField
        implements
            ListSelectionListener,
            ChangeListener {

        private static final long  serialVersionUID = 1L;
        public static final String NAME             = "sizeText"; //$NON-NLS-1$ AspectJ name

        public FontSizeText() {
            setName(NAME);
        }

        public int getTextSize() {
            return Integer.parseInt(getText());
        }

        @Override
        public void stateChanged(final ChangeEvent event) {
            final FontSizeSlider slider = (FontSizeSlider) event.getSource();
            setText(String.valueOf(slider.getValue()));
        }

        @Override
        public void valueChanged(final ListSelectionEvent event) {
            final FontSizeList list = (FontSizeList) event.getSource();
            final ListSelectionModel model = list.getSelectionModel();
            if (!model.isSelectionEmpty()) {
                setText(list.getSelectedValue());
            }
        }
    }

    /**
     * A slider to adjust the font size.
     */
    public static class FontSizeSlider
        extends
            JSlider
        implements
            ListSelectionListener,
            FocusListener {

        private static final long  serialVersionUID = 1L;
        public static final String NAME             = "sizeSzlider"; //$NON-NLS-1$ AspectJ name

        public FontSizeSlider() {
            super(SwingConstants.VERTICAL, 0, 144, 50);
            setName(NAME);
            setMinorTickSpacing(5);
            setMajorTickSpacing(20);
            setPaintTicks(true);
            setPaintLabels(true);

        }

        @Override
        public void valueChanged(final ListSelectionEvent event) {
            @SuppressWarnings("unchecked")
            final JList<String> list = (JList<String>) event.getSource();
            final ListSelectionModel model = list.getSelectionModel();
            if (!model.isSelectionEmpty()) {
                setValue(Integer.parseInt(list.getSelectedValue()));
            }
        }

        @Override
        public void focusGained(final FocusEvent event) {
            // do nothing
        };

        @Override
        public void focusLost(final FocusEvent event) {
            final FontSizeText text = (FontSizeText) event.getSource();
            try {
                setValue(text.getTextSize());
            } catch (final IllegalArgumentException error) {
                // ignore
            }
        }
    }

    /**
     * Display sample text using selected font.
     */
    public static class PreviewPanel
        extends
            JPanel
        implements
            ListSelectionListener,
            FocusListener {

        private static final long       serialVersionUID = 1L;
        public static final String      TEXTFIELD_NAME   = "textField";                //$NON-NLS-1$ AspectJ name

        private final JTextField        mTextField       = new JTextField(STR_PREVIEW);
        private final FontChooserDialog mDialog;

        public PreviewPanel(final FontChooserDialog dialog) {
            super();
            mDialog = dialog;
            setLayout(new FlowLayout());

            mTextField.setName(PreviewPanel.TEXTFIELD_NAME);
            mTextField.setEditable(true);
            mTextField.setBackground(Color.WHITE);
            mTextField.setForeground(Color.BLACK);

            final JScrollPane pane = new JScrollPane(mTextField);
            pane.setPreferredSize(new Dimension(500, 100));

            add(pane);
        }

        @Override
        public void valueChanged(final ListSelectionEvent event) {
            final ListSelectionModel model = ((JList< ? >) event.getSource()).getSelectionModel();
            if (!model.isSelectionEmpty()) {
                try {
                    mTextField.setFont(mDialog.getSelectedFont());
                } catch (final IllegalArgumentException error) {
                    // Ignore
                }
            }
        }

        @Override
        public void focusGained(final FocusEvent event) {
            // do nothing
        };

        @Override
        public void focusLost(final FocusEvent event) {
            mTextField.setFont(mDialog.getSelectedFont());
        }
    }

    /**
     * Enum to support converting between Font.style and Style names.
     */
    public enum FontStyle {
        // @formatter:off
            PLAIN(STR_REGULAR,            Font.PLAIN),
            BOLD(STR_BOLD,                Font.BOLD),
            ITALIC(STR_ITALIC,            Font.ITALIC),
            BOLD_ITALIC(STR_BOLD_ITALIC,  Font.BOLD + Font.ITALIC);
        // @formatter:on

        private final String mName;
        private final int    mStyle;

        private FontStyle(final String name, final int style) {
            mName = name;
            mStyle = style;
        }

        public String getName() {
            return mName;
        }

        public int getStyle() {
            return mStyle;
        }

        public static String[] getNames() {
            return Arrays.asList(FontStyle.values())
                         .stream()
                         .map(FontStyle::getName)
                         .toArray(String[]::new);
        }

        public static Optional<FontStyle> fromName(final String name) {
            return Arrays.asList(FontStyle.values())
                         .stream()
                         .filter(e -> e.getName().equals(name))
                         .findFirst();
        }

        public static Optional<FontStyle> fromInt(final int value) {
            return Arrays.asList(FontStyle.values())
                         .stream()
                         .filter(e -> e.getStyle() == value)
                         .findFirst();
        }
    };

    /**
     * Map of all installed font family names and fonts for each.
     */
    public static final class FontRegistry
        extends
            HashMap<String, List<Font>> {

        private static final long   serialVersionUID = 1L;

        private static FontRegistry sInstance;

        public static FontRegistry getInstance() {
            if (sInstance == null) {
                sInstance = new FontRegistry();
            }
            return sInstance;
        }

        /**
         * Create a map of all installed family names and fonts for each.
         */
        private FontRegistry() {
            super();
            Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                                             .getAvailableFontFamilyNames())
                  .stream()
                  .forEach(f -> addFamily(f));
            Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts())
                  .stream()
                  .forEach(f -> addFont(f));
            // dump();
        }

        /**
         * Strip family name from a font name.
         *
         * @param font
         *            font to use
         * @return if name contains a '-', then remove all before plus the '-'
         */
        public static String stripFamily(final Font font) {
            String result = font.getName();
            final int offset = result.indexOf('-');
            if (offset != -1) {
                result = result.substring(offset + 1);
            }
            return result;
        }

        /**
         * Get all family names.
         *
         * @return sorted array of all family names
         */
        public String[] getFamilyNames() {
            return this.keySet().stream().sorted().toArray(String[]::new);
        }

        public boolean isFamilyName(final String name) {
            return get(name) != null;
        }

        /**
         * Get all stripped font names. Move the one similar to the family name to the top (same or
         * same with spaces removed).
         *
         * @param familyName
         *            family name to look up
         * @return sorted array of all font names stripped of any thing before '-'
         */
        public String[] getFontNames(final String familyName) {
            final List<String> result = get(familyName).stream()
                                                       .map(f -> stripFamily(f))
                                                       .sorted()
                                                       .collect(Collectors.toList());
            for (final String name : Arrays.asList(familyName, familyName.replace(" ", ""))) {
                final int index = result.indexOf(name);
                if (index != -1) {
                    result.remove(index);
                    result.add(0, STR_REGULAR); // Regular
                    break;
                }
            }
            return result.stream().toArray(String[]::new);
        }

        /**
         * Get the actual font name for a stripped name.
         *
         * @param familyName
         *            family name to look up
         * @param strippedName
         *            a stripped font name
         * @return actual font name
         * @throws IllegalArgumentException
         *             no font found
         */
        public String getFontName(final String familyName,
                                  final String strippedName) throws IllegalArgumentException {
            final Optional<Font> font = get(familyName).stream()
                                                       .filter(f -> stripFamily(f).equals(strippedName))
                                                       .findFirst();
            if (font.isPresent()) {
                return font.get().getName();
            } else if (strippedName.equals(STR_REGULAR)) {
                return familyName;
            } else {
                throw new IllegalArgumentException(familyName + " " + strippedName);
            }
        }

        /**
         * Strip family name from font to match name in list. Used in JUnit tests to drive robot.
         *
         * @param font
         *            font
         * @return stripped font name
         */
        public String getFontNameForList(final Font font) {
            String result = stripFamily(font);
            if (font.getFamily().equals(result)) {
                result = STR_REGULAR;
            }
            return result;
        }

        private void addFamily(final String familyName) {
            put(familyName, new ArrayList<Font>());
        }

        private void addFont(final Font font) {
            get(font.getFamily()).add(font);
        }

        private void dump() {
            this.keySet().stream().sorted().forEach(f -> dump(f));
        }

        private void dump(final String familyName) {
            System.out.println(String.format("%-25s  %s",
                                             familyName,
                                             Arrays.asList(getFontNames(familyName))
                                                   .stream()
                                                   .collect(Collectors.joining(", "))));
        }

    }

    /**
     * Filter to accept a list of names. Use to create sublists of fonts.
     */
    public abstract static class AbstractFontFilter
        implements
            Predicate<String> {

        private String             mFilterName;
        private final List<String> mNames = new ArrayList<>();

        /**
         * @return filter name
         */
        public String getFilterName() {
            return mFilterName;
        }

        /**
         * Set name of filter.
         *
         * @param filterName
         *            new name
         */
        public void setFilterName(final String filterName) {
            mFilterName = filterName;
        }

        /**
         * Add names to filter. Names already in list are ignored.
         *
         * @param names
         *            names
         * @return this
         */
        public AbstractFontFilter addFontName(final String... names) {
            Arrays.asList(names).stream().filter(this.negate()).forEach(name -> mNames.add(name));
            return this;
        }

        /**
         * @param value
         *            value to check if in list
         * @return true if in list
         *
         * @see java.util.function.Predicate#test(java.lang.Object)
         */
        @Override
        public boolean test(final String value) {
            return mNames.contains(value);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mFilterName == null) ? 0 : mFilterName.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AbstractFontFilter other = (AbstractFontFilter) obj;
            if (mFilterName == null) {
                if (other.mFilterName != null) {
                    return false;
                }
            } else if (!mFilterName.equals(other.mFilterName)) {
                return false;
            }
            return true;
        }

        /**
         * @return filter name: font1, font2, ...
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return String.format("%s: %s",
                                 mFilterName,
                                 mNames.stream().sorted().collect(Collectors.joining(", ")));
        }
    }

    /**
     * Filter to accept all font names.
     */
    public static class FilterAll
        extends
            AbstractFontFilter {

        public FilterAll() {
            super();
            setFilterName(STR_ALL_FONTS);
        }

        @Override
        public boolean test(final String value) {
            return true;
        }
    }

    /**
     * Filter to accept a standard short list of names.
     */
    public static class FilterStandard
        extends
            AbstractFontFilter {

        public FilterStandard() {
            super();
            setFilterName(STR_STANDARD);
            addFontName(STR_FONT_STANDARD.split(",", Integer.MAX_VALUE));
        }
    }

    /**
     * Used to create custom filters, especially ones created from MacOS .collections from the Font
     * Book.
     */
    public static class FilterCustom
        extends
            AbstractFontFilter {

        public FilterCustom(final String name) {
            super();
            setFilterName(name);
        }
    }

    /**
     * A list of all filters. Also processes MacOS .collections files from the Font Book
     * application.
     */
    public static final class FilterRegistry
        extends
            ArrayList<AbstractFontFilter> {

        private static final long     serialVersionUID     = 1L;

        private static FilterRegistry sInstance;
        private static boolean        mProcesedCollections = false;

        public static FilterRegistry getInstance() {
            if (sInstance == null) {
                sInstance = new FilterRegistry();
            }
            return sInstance;
        }

        /**
         * Process all files in ~user/Library/FontCollections directory and create FilterUser for
         * each. Only in MacOS.
         */
        private static void processCollections() {
            if (System.getProperty("os.name").equals("Mac OS X") && !mProcesedCollections) { //$NON-NLS-1$
                final List<String> mIgnore = new ArrayList<>(
                        Arrays.asList("com.apple.Favorites.collection", //$NON-NLS-1$
                                      "com.apple.Recents.collection")); //$NON-NLS-1$
                final File file = new File(System.getProperty("user.home"), //$NON-NLS-1$
                        "/Library/FontCollections"); //$NON-NLS-1$
                Arrays.asList(file.listFiles())
                      .stream()
                      .filter(f -> f.getName().endsWith(".collection")) //$NON-NLS-1$
                      .filter(f -> !mIgnore.contains(f.getName()))
                      .map(f -> processCollection(f))
                      .forEach(f -> FilterRegistry.getInstance().addFilter(f));
                mProcesedCollections = true;
            }
        }

        /**
         * Process one font collections file. A bit of a hack, but good enough for now.
         *
         * @param file
         *            file to read.
         * @return FilterUser built for this collection
         * @throws IllegalArgumentException
         *             file or xml errors
         */
        private static FilterCustom processCollection(final File file) throws IllegalArgumentException {
            // !Improve find a better way of reading a collections file, maybe Apache commons.
            /*
             * Make a copy of file and run plutil to convert it to plain xml in case it might a
             * binary collection.
             */
            final File directory = //
                    new File(System.getProperty("java.io.tmpdir"), "FilterRegistry"); //$NON-NLS-1$
            directory.mkdirs();
            final File copy = new File(directory, file.getName());
            try {
                Files.copy(file.toPath(), copy.toPath());
                runScript(new String[]{
                                       "/usr/bin/plutil", //$NON-NLS-1$
                                       "-convert", //$NON-NLS-1$
                                       "xml1", //$NON-NLS-1$
                                       copy.getAbsolutePath()
                });
                /*
                 * Read all <string> entries and save values.
                 */
                final FilterRegistry.PList plist = new PList();
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder builder = factory.newDocumentBuilder();
                final Document document = builder.parse(copy);
                document.getDocumentElement().normalize();
                final NodeList nodeList = document.getElementsByTagName("string"); //$NON-NLS-1$
                for (int i = 0; i < nodeList.getLength(); i++) {
                    final Node node = nodeList.item(i);
                    switch (node.getNodeType()) {
                    case Node.ELEMENT_NODE:
                        // final String name = node.getNodeName();
                        final String value = node.getTextContent();
                        plist.add(value);
                        break;
                    default:
                        break;
                    }
                }

                /*
                 * For each entry in PList, look for NSFontCollectionName and NSFontFamilyAttribute.
                 * Some entries are discarded by next(), others might be font family names and are
                 * checked. Build a FilterUser and return.
                 */
                String name = "?";
                final List<String> fonts = new ArrayList<>();
                while (plist.hasNext()) {
                    final String next = plist.next();
                    if (next.equals("NSFontCollectionName")) {
                        name = plist.next();
                    } else if (next.equals("NSFontFamilyAttribute")) {
                        fonts.add(plist.next());
                    } else {
                        if (FontRegistry.getInstance().isFamilyName(next)) {
                            fonts.add(next);
                        } else {
                            // System.out.println(next);
                        }
                    }
                }
                final FilterCustom filter = new FilterCustom(name);
                fonts.stream().forEach(f -> filter.addFontName(f));
                return filter;
            } catch (final IOException | InterruptedException | ParserConfigurationException
                    | org.xml.sax.SAXException error) {
                throw new IllegalArgumentException(error);
            } finally {
                copy.delete();
            }
        }

        /**
         * Create a list of all filters defined in FontChooseDialog.
         */
        private FilterRegistry() {
            super();
            Arrays.asList(FontChooserDialog.class.getDeclaredClasses())
                  .stream()
                  .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                  .filter(c -> AbstractFontFilter.class.isAssignableFrom(c))
                  .filter(c -> !c.getSimpleName().equals(FilterCustom.class.getSimpleName()))
                  .map(c -> createFilter(c))
                  .sorted(Comparator.comparing(f -> f.getFilterName()))
                  .forEach(f -> addFilter(f));
        }

        /**
         * Add a filter to the list.
         *
         * @param filters
         *            filter to add
         * @return this
         */
        public FilterRegistry addFilter(final AbstractFontFilter... filters) {
            Arrays.asList(filters).stream().filter(f -> !contains(f)).forEach(f -> add(f));
            return this;
        }

        /**
         * Get a sorted array of filter names.
         *
         * @return sorted array of filter names
         */
        public String[] getFilterNames() {
            return stream().map(e -> e.getFilterName()).sorted().toArray(String[]::new);
        }

        /**
         * Get filter by name.
         *
         * @param name
         *            filter name to lookup
         * @return filter
         */
        public AbstractFontFilter getByName(final String name) {
            return this.stream().filter(f -> f.getFilterName().equals(name)).findFirst().get();
        }

        /**
         * Make a JComboBox with all filter names.
         *
         * @return JComboBox
         */
        public JComboBox<String> getComboBox() {
            final JComboBox<String> result = new JComboBox<>(getFilterNames());
            result.setMaximumRowCount(size());
            final FontMetrics fontMetrics = result.getFontMetrics(result.getFont());
            final OptionalInt width = this.stream()
                                          .mapToInt(f -> fontMetrics.stringWidth(f.getFilterName()))
                                          .max();
            result.setPreferredSize(new Dimension(width.getAsInt(), 20));
            return result;
        }

        /**
         * Create a filter object for list, and handle any errors.
         *
         * @param aClass
         *            filter class to create
         * @return filter object
         * @throws IllegalArgumentException
         *             any error thrown
         */
        private AbstractFontFilter createFilter(final Class< ? > aClass) throws IllegalArgumentException {
            try {
                final AbstractFontFilter filter = (AbstractFontFilter) aClass.getDeclaredConstructor()
                                                                             .newInstance();
                return filter;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                    | SecurityException | InvocationTargetException error) {
                throw new IllegalArgumentException(error);
            }
        }

        private static String[] runScript(final String[] command) throws InterruptedException,
                                                                  IOException {
            final List<String> result = new ArrayList<String>();
            final Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            readResults(result, process);
            process.waitFor();
            return result.toArray(new String[result.size()]);
        }

        private static void readResults(final List<String> result,
                                        final Process process) throws IOException {
            try (final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));) {
                String text;
                while ((text = reader.readLine()) != null) {
                    result.add(text);
                }
            }
        }

        /**
         * List of {@code <String>} entries in PList with methods to skip over ones to ignore.
         */
        private static class PList
            extends
                ArrayList<String> {

            private static final long  serialVersionUID = 1L;
            private int                mIndex;

            private final List<String> mIgnore          = new ArrayList<>();

            public PList() {
                super();
                mIgnore.addAll(Arrays.asList(//
                                             "$null",
                                             "NSArray",
                                             "NSDictionary",
                                             "NSFontCollectionAttributes",
                                             "NSFontCollectionFileName",
                                             "NSFontCollectionFontDescriptors",
                                             "NSFontDescriptor",
                                             "NSFontDescriptorAttributes",
                                             "NSFontFaceAttribute",
                                             "NSFontNameAttribute",
                                             "NSFontNameAttribute",
                                             "NSKeyedArchiver",
                                             "NSMutableArray",
                                             "NSMutableDictionary",
                                             "NSObject" //
                ));
            }

            /**
             * start at beaconing of list
             */
            public void start() {
                mIndex = 0;
            }

            /**
             * @return next string, ignoring all in the ignore list
             */
            public String next() {
                String result = "";
                while (hasNext()) {
                    result = get(mIndex++);
                    if (!mIgnore.contains(result)) {
                        break;
                    }
                }
                return result;
            }

            /**
             * @return true if has more entries
             */
            public boolean hasNext() {
                return mIndex < size();
            }
        }
    }
}