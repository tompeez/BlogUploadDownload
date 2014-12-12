package de.hahn.blog.uldl.view.frkext;


import de.hahn.blog.uldl.model.dataaccess.common.ImageAccessViewRow;
import de.hahn.blog.uldl.model.facade.ULDLAppModuleImpl;
import de.hahn.blog.uldl.view.util.ContentTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.domain.BlobDomain;
import oracle.jbo.uicli.binding.JUCtrlActionBinding;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


public class ImageServlet extends HttpServlet {
    @SuppressWarnings("compatibility:-1452118953754413705")
    private static final long serialVersionUID = 1L;
    protected transient ADFLogger mLogger = ADFLogger.createADFLogger(ImageServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    protected ULDLAppModuleImpl findULDLAppModule() {
        BindingContext bindingContext = BindingContext.getCurrent();
        DCBindingContainer amx = bindingContext.findBindingContainer("de_hahn_blog_uldl_view_image_dummyPageDef");
        ULDLAppModuleImpl am = (ULDLAppModuleImpl) amx.getDataControl().getApplicationModule();
        return am;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder(100);
        String appModuleName = "de.hahn.blog.uldl.model.facade.ULDLAppModule";

        sb.append("ImageServlet ").append(appModuleName);

        try {
            // get parameter from request
            Map paramMap = request.getParameterMap();
            oracle.jbo.domain.Number id = null;
            String tmporaryFilePath = "";
            if (paramMap.containsKey("id")) {
                String[] pVal = (String[]) paramMap.get("id");
                id = new oracle.jbo.domain.Number(pVal[0]);
                sb.append(" id=").append(pVal[0]);
            }
            // check if we find a temporary file name. In this case we allways use this!
            if (paramMap.containsKey("tmp")) {
                String[] pVal = (String[]) paramMap.get("tmp");
                tmporaryFilePath = pVal[0];
                sb.append(" tmp=").append(pVal[0]);
            }


            // get method action from pagedef
            BindingContext bindingContext = BindingContext.getCurrent();
            DCBindingContainer amx = bindingContext.findBindingContainer("de_hahn_blog_uldl_view_image_dummyPageDef");
            JUCtrlActionBinding lBinding = (JUCtrlActionBinding) amx.findCtrlBinding("getImageById");
            // set parameter
            lBinding.getParamsMap().put("aId", id);
            // execute method
            lBinding.invoke();
            // get result
            Object obj = lBinding.getResult();
            ImageAccessViewRow imageRow = (ImageAccessViewRow) obj;
            BlobDomain image = null;
            String mimeType = null;

            // Check if a row has been found
            if (imageRow != null) {
                // Get the blob data
                image = imageRow.getImageData();
                mimeType = imageRow.getContentType();
                // if no image data can be found and no temporary file is present then return and do nothing
                if (image == null && tmporaryFilePath.isEmpty()) {
                    mLogger.info("No data found !!! (id = " + id + ")");
                    return;
                }
            } else {
                if (tmporaryFilePath.isEmpty()) {
                    mLogger.warning("No row found to get image from !!! (id = " + id + ")");
                    return;
                }
                mimeType = ContentTypes.get(tmporaryFilePath);
            }
            sb.append(" ").append(mimeType).append(" ...");
            mLogger.info(sb.toString());

            // Set the content-type. Only images are taken into account
            response.setContentType(mimeType + "; charset=utf8");
            OutputStream outputStream = response.getOutputStream();
            InputStream inputStream = null;
            if (!tmporaryFilePath.isEmpty()) {
                File file = FileUtils.getFile(tmporaryFilePath);
                FileInputStream fileInputStream = FileUtils.openInputStream(file);
                inputStream = fileInputStream;
            } else {
                inputStream = image.getInputStream();
            }
            IOUtils.copy(inputStream, outputStream);
            // cloase the blob to release the recources
            image.closeInputStream();
            // flush the outout stream
            outputStream.flush();
        } catch (Exception e) {
            mLogger.log(Level.WARNING, "Fehler bei der Ausführung: " + e.getMessage(), e);
        } finally {


            mLogger.info("...done!");
        }
    }
}
