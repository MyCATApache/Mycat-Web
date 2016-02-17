package jrds.webapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jrds.factories.xml.EntityResolver;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.*;
import org.json.JSONWriter;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Servlet implementation class Upload
 */
public class Upload extends JrdsServlet {
    static final private Logger logger = LogManager.getLogger(Upload.class);

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
            instance.setIgnoringComments(true);
            instance.setValidating(true);
            DocumentBuilder dbuilder = instance.newDocumentBuilder();
            dbuilder.setEntityResolver(new EntityResolver());
            dbuilder.setErrorHandler(new ErrorHandler() {
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }
                public void warning(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });

            List<FileItem> items = extracted(request, upload);

            response.setContentType("text/html");

            PrintWriter outputWriter = response.getWriter();
            outputWriter.println("<textarea>");

            JSONWriter w = new JSONWriter(outputWriter);
            w.array();

            for(FileItem item: items) {
                logger.debug(jrds.Util.delayedFormatString("Item send: %s", item));

                // Process a file upload
                if (!item.isFormField()) {
                    w.object();
                    String fileName = item.getName();
                    w.key("name").value(fileName);
                    InputStream uploadedStream = item.getInputStream();
                    try {
                        dbuilder.parse(uploadedStream);
                        File destination = new File(getPropertiesManager().configdir, fileName);
                        if(! destination.exists()) {
                            item.write(destination);
                            w.key("parsed").value(true);
                        }
                        else {
                            w.key("error").value("file existe");
                            w.key("parsed").value(false);
                        }
                    } catch (Exception e) {
                        w.key("error").value(e.getMessage());
                        w.key("parsed").value(false);
                    }
                    uploadedStream.close();
                    w.endObject();
                }
            }
            w.endArray();
            outputWriter.println("</textarea>");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("upload file failed: " + e);
        }
        response.flushBuffer();
    }

    @SuppressWarnings("unchecked")
    private List<FileItem> extracted(HttpServletRequest request, ServletFileUpload upload)
            throws FileUploadException {
        return upload.parseRequest(request);
    }

}
