package de.hahn.blog.uldl.model.facade;

import de.hahn.blog.uldl.model.dataaccess.ImageAccessViewImpl;

import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.ViewLinkImpl;
import oracle.jbo.server.ViewObjectImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Wed Nov 30 20:18:53 CET 2011
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class ULDLAppModuleImpl
    extends ApplicationModuleImpl
{
    /**
     * This is the default constructor (do not remove).
     */
    public ULDLAppModuleImpl()
    {
    }

    /**
     * Container's getter for CatalogView1.
     * @return CatalogView1
     */
    public ViewObjectImpl getCatalogView1()
    {
        return (ViewObjectImpl) findViewObject("CatalogView1");
    }

    /**
     * Container's getter for ImagesView1.
     * @return ImagesView1
     */
    public ViewObjectImpl getImagesView1()
    {
        return (ViewObjectImpl) findViewObject("ImagesView1");
    }

    /**
     * Container's getter for ImagesView2.
     * @return ImagesView2
     */
    public ViewObjectImpl getImagesView2()
    {
        return (ViewObjectImpl) findViewObject("ImagesView2");
    }

    /**
     * Container's getter for ImagesCatalogFkLink1.
     * @return ImagesCatalogFkLink1
     */
    public ViewLinkImpl getImagesCatalogFkLink1()
    {
        return (ViewLinkImpl) findViewLink("ImagesCatalogFkLink1");
    }

    /**
     * Container's getter for ImageAccessVie1.
     * @return ImageAccessVie1
     */
    public ImageAccessViewImpl getImageAccessView1()
    {
        return (ImageAccessViewImpl) findViewObject("ImageAccessView1");
    }
}