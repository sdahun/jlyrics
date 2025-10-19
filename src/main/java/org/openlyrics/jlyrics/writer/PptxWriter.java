package org.openlyrics.jlyrics.writer;

import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.*;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.ILyricsEntry;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.util.SongUtils;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStopList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLinearShadeProperties;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMaster;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTransition;
import org.openxmlformats.schemas.presentationml.x2006.main.STTransitionSpeed;

import java.awt.*;
import java.io.OutputStream;

import static org.openlyrics.jlyrics.song.SongConstants.LIB_VERSION;

public class PptxWriter implements ILyricsWriter {

    private static final int BOX_MARGIN = 30;

    @Override
    public String getFileExtension() {
        return ".pptx";
    }

    @Override
    public void write(Song song, OutputStream outputStream) throws Exception {
        String title = song.getProperties().getTitles().get(0).getTitle();
        XMLSlideShow pptx = getConfiguredPptx(title);
        XSLFSlideMaster master = pptx.getSlideMasters().get(0);

        for (ILyricsEntry entry : song.getLyrics()) {
            if (entry instanceof Verse) {
                Verse verse = (Verse) entry;

                //new slide
                XSLFSlide slide = pptx.createSlide(master.getLayout(SlideLayout.BLANK));

                //pull transition
                CTSlideTransition trans = slide.getXmlObject().addNewTransition();
                trans.setSpd(STTransitionSpeed.MED);
                trans.addNewPull().setDir("u");

                //textbox for lyrics
                XSLFTextShape textShape = slide.createTextBox();
                textShape.setAnchor(new Rectangle(BOX_MARGIN, BOX_MARGIN, 960 - BOX_MARGIN - BOX_MARGIN, 540 - BOX_MARGIN - BOX_MARGIN));
                textShape.setTextAutofit(TextShape.TextAutofit.NORMAL);
                textShape.setVerticalAlignment(VerticalAlignment.MIDDLE);

                //paragraph for textbox
                XSLFTextParagraph paragraph = textShape.getTextParagraphs().get(0);
                paragraph.setTextAlign(TextParagraph.TextAlign.CENTER);

                //textrun for paragraph
                XSLFTextRun textRun = paragraph.addNewTextRun();
                textRun.getRPr(false).setLang("hu-HU");
                textRun.setFontFamily("Tahoma");
                textRun.setBold(true);
                textRun.setFontSize(40d);
                textRun.setFontColor(Color.WHITE);
                textRun.setText(SongUtils.getVerseTextContent(verse, false).replaceAll("\r\n", "\n"));

                //verse name to notes
                XSLFNotes notes = pptx.getNotesSlide(slide);
                for (XSLFTextShape shape : notes.getPlaceholders()) {
                    if (shape.getTextType() == Placeholder.BODY) {
                        shape.setText(verse.getFormattedName());
                    }
                }
            }
        }

        pptx.write(outputStream);
        pptx.close();
    }

    private static XMLSlideShow getConfiguredPptx(String title) {
        XMLSlideShow pptx = new XMLSlideShow();

        //page properties
        POIXMLProperties.CoreProperties coreProperties = pptx.getProperties().getCoreProperties();
        coreProperties.setTitle(title);
        coreProperties.setCreator(LIB_VERSION);
        coreProperties.setLastModifiedByUser(LIB_VERSION);

        //page layout 16x9
        Dimension dimension = new Dimension();
        dimension.setSize(960, 540);
        pptx.setPageSize(dimension);

        //master background
        XSLFSlideMaster master = pptx.getSlideMasters().get(0);
        CTSlideMaster ctSlide = master.getXmlObject();
        ctSlide.getCSld().getBg().unsetBgRef(); //clear previous background
        CTGradientFillProperties gFill = ctSlide.getCSld().getBg().addNewBgPr().addNewGradFill();

        CTLinearShadeProperties gFillProps = gFill.addNewLin();
        gFillProps.setAng(5400000);
        gFillProps.setScaled(true);

        CTGradientStopList stopList = gFill.addNewGsLst();

        //gradient fill bottom pos
        CTGradientStop stop = stopList.addNewGs();
        stop.setPos(0);
        stop.addNewSrgbClr().setVal(new byte[]{(byte) 0x00, (byte) 0x20, (byte) 0x60});

        //gradient fill top pos
        stop = stopList.addNewGs();
        stop.setPos(100000);
        stop.addNewSrgbClr().setVal(new byte[]{0, 0, 0});

        return pptx;
    }
}
