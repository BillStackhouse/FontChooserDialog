package com.billsdesk.github.fontchooserdialog;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Font;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.billsdesk.github.fontchooserdialog.FontChooserDialog.FilterStandard;
import com.billsdesk.github.fontchooserdialog.FontChooserDialog.FontFamilyList;
import com.billsdesk.github.fontchooserdialog.FontChooserDialog.FontList;
import com.billsdesk.github.fontchooserdialog.FontChooserDialog.FontSizeList;
import com.billsdesk.github.fontchooserdialog.FontChooserDialog.FontStyleList;

/**
 * @author Bill
 * @version $Rev: 8244 $ $Date: 2020-07-21 14:08:55 -0700 (Tue, 21 Jul 2020) $
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class FontChooserDialogTest {

    // @Disabled("visual check only")
    @Test
    public void displayDefault() {
        final FontChooserDialog dialog = //
                GuiActionRunner.execute(() -> new FontChooserDialog().showDialog());
        while (dialog.isVisible()) {
            pause(200);
        }
    }

    @Disabled("visual check only")
    @Test
    public void display() {
        final Font intialFont = new Font("Courier", Font.BOLD, 24);
        final FontChooserDialog dialog = //
                GuiActionRunner.execute(() -> new FontChooserDialog(null,
                        "Font",
                        intialFont).showDialog());
        assertEquals(intialFont, dialog.getSelectedFont());
        while (dialog.isVisible()) {
            pause(200);
        }
    }

    @Disabled("visual check only")
    @Test
    public void displayStandard() {
        final Font intialFont = new Font("Courier", Font.BOLD, 24);
        final FontChooserDialog dialog = //
                GuiActionRunner.execute(() -> new FontChooserDialog(null,
                        "Font",
                        intialFont,
                        new FilterStandard()).showDialog());
        assertEquals(intialFont, dialog.getSelectedFont());
//      while (dialog.isVisible()) {
//          TestUtils.pause(200);
//      }
    }

    @ParameterizedTest
    @CsvSource(value = {
                        "Courier, Bold, 24",
    })
    public void setModeDialog(final String fontName, final String styleName, final int size) {
        final int style = FontChooserDialog.FontStyle.fromName(styleName).get().getStyle();
        final Font font = new Font(fontName, style, size);
        final FontChooserDialog dialog = GuiActionRunner.execute(() -> new FontChooserDialog());
        final DialogFixture fixture = new DialogFixture(dialog);
        fixture.robot().showWindow(fixture.target(), null, false);
        fixture.robot().waitForIdle();
        fixture.list(FontFamilyList.NAME).clickItem(font.getFamily());
        fixture.list(FontList.NAME)
               .clickItem(FontChooserDialog.FontRegistry.getInstance().getFontNameForList(font));
        fixture.list(FontStyleList.NAME).clickItem(styleName);
        fixture.list(FontSizeList.NAME).clickItem(String.valueOf(size));
        fixture.button("okay").click();
        assertEquals(font, dialog.getSelectedFont());
        // fixture.close();
        fixture.cleanUp();
    }

    public static void pause(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException error) {
            // Ignore
        }
    }
}
