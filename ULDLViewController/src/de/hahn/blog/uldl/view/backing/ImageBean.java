package de.hahn.blog.uldl.view.backing;


import de.hahn.blog.uldl.view.util.ContentTypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.nav.RichButton;

import oracle.binding.AttributeBinding;
import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.domain.BlobDomain;

import org.apache.commons.io.IOUtils;
import org.apache.myfaces.trinidad.model.UploadedFile;
import org.apache.myfaces.trinidad.util.ComponentReference;


public class ImageBean {
    private ComponentReference downloadButton;
    private Integer randomVal = 0;

    public ImageBean() {
    }

    /**
     * @return
     */
    public String cancel_action() {
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();

        // get an ADF attributevalue from the ADF page definitions
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("CatalogId");
        Number catalogID = (Number) attr.getInputValue();
        OperationBinding opRollback = bindings.getOperationBinding("Rollback");
        opRollback.execute();
        if (!opRollback.getErrors().isEmpty()) {
            List<Throwable> lErrorList = opRollback.getErrors();
            for (Throwable lErr : lErrorList) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, lErr.getMessage(), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            return null;
        }

        OperationBinding opSetParent = bindings.getOperationBinding("setCurrentRowWithKeyValue");
        opSetParent.getParamsMap().put("rowKey", catalogID);
        opSetParent.execute();
        if (!opSetParent.getErrors().isEmpty()) {
            List<Throwable> lErrorList = opSetParent.getErrors();
            for (Throwable lErr : lErrorList) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, lErr.getMessage(), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            return null;
        }
        return "cancel";
    }

    /**
     * @param valueChangeEvent
     */
    public void uploadFileValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        // The event give access to an Uploade dFile which contains data about the file and its content
        UploadedFile file = (UploadedFile) valueChangeEvent.getNewValue();
        // Get the original file name
        String fileName = file.getFilename();
        // get the mime type
        String contentType = ContentTypes.get(fileName);
        // get the current roew from the ImagesView2Iterator via the binding
        DCBindingContainer lBindingContainer = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding lBinding = lBindingContainer.findIteratorBinding("ImagesView2Iterator");
        Row newRow = lBinding.getCurrentRow();
        // set the file name
        newRow.setAttribute("ImageName", fileName);
        // create the BlobDomain and set it into the row
        newRow.setAttribute("ImageData", createBlobDomain(file));
        // set the mime type
        newRow.setAttribute("ContentType", contentType);
    }

    /**
     * @return
     */
    public String getComputeImageUrl() {
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        // get an ADF attributevalue from the ADF page definitions
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("ImageId");
        Number ImageID = (Number) attr.getInputValue();
        String url = "/render_image?id=" + ImageID.toString() + "&x=" + getRandomVal().toString();
        return url;
    }

    private BlobDomain createBlobDomain(UploadedFile file) {
        // init the internal variables
        InputStream in = null;
        BlobDomain blobDomain = null;
        OutputStream out = null;

        try {
            // Get the input stream representing the data from the client
            in = file.getInputStream();
            // create the BlobDomain datatype to store the data in the db
            blobDomain = new BlobDomain();
            // get the outputStream for hte BlobDomain
            out = blobDomain.getBinaryOutputStream();
            // copy the input stream into the output stream
            /*
             * IOUtils is a class from the Apache Commons IO Package (http://www.apache.org/)
             * Here version 2.0.1 is used
             * please download it directly from http://projects.apache.org/projects/commons_io.html
             */
            IOUtils.copy(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }

        // return the filled BlobDomain
        return blobDomain;
    }


    /**
     * @param facesContext
     * @param outputStream
     */
    public void downloadImage(FacesContext facesContext, OutputStream outputStream) {
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();

        // get an ADF attributevalue from the ADF page definitions
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("ImageData");
        if (attr == null) {
            return;
        }

        // the value is a BlobDomain data type
        BlobDomain blob = (BlobDomain) attr.getInputValue();

        try { // copy hte data from the BlobDomain to the output stream
            IOUtils.copy(blob.getInputStream(), outputStream);
            // cloase the blob to release the recources
            blob.closeInputStream();
            // flush the outout stream
            outputStream.flush();
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            facesContext.addMessage(null, msg);
        }
    }

    /**
     * @param downloadButton
     */
    public void setDownloadButton(RichButton downloadButton) {

        this.downloadButton = ComponentReference.newUIComponentReference(downloadButton);
    }

    /**
     * @return
     */
    public RichButton getDownloadButton() {
        if (downloadButton != null) {
            return (RichButton) downloadButton.getComponent();
        } else {
            return null;
        }
    }

    /**
     * @param aRandomVal
     */
    public void setRandomVal(Integer aRandomVal) {
        this.randomVal = aRandomVal;
    }

    /**
     * @return
     */
    public Integer getRandomVal() {
        randomVal++;
        return randomVal;
    }
}
