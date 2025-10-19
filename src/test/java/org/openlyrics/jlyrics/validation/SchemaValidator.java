package org.openlyrics.jlyrics.validation;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.Flag;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.prop.rng.RngProperty;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchemaValidator {

    public static void validateXmlToSchema(InputStream xmlStream, InputStream schemaStream) throws IOException, SAXException {
        ValidationHandler handler = new ValidationHandler();

        PropertyMapBuilder pmb = new PropertyMapBuilder();
        pmb.put(ValidateProperty.ERROR_HANDLER, handler);
        pmb.put(RngProperty.CHECK_ID_IDREF, Flag.PRESENT);

        ValidationDriver vd = new ValidationDriver(pmb.toPropertyMap());
        assertTrue(vd.loadSchema(new InputSource(schemaStream)));

        vd.validate(new InputSource(xmlStream));
        assertEquals(new ArrayList<>(), handler.getErrors());
    }

}
