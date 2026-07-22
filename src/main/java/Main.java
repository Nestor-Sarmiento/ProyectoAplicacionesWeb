

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        String webappDirLocation = "src/main/webapp/";

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir("target/tomcat-work");
        tomcat.getConnector();

        Context ctx = tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        ctx.setReloadable(true);

        // Disable manifest scanning to prevent warnings about missing jars referenced in MANIFEST.MF
        if (ctx.getJarScanner() instanceof org.apache.tomcat.util.scan.StandardJarScanner) {
            ((org.apache.tomcat.util.scan.StandardJarScanner) ctx.getJarScanner()).setScanManifest(false);
        }

        // Configurar WebResourceRoot para que Tomcat escanee las clases compiladas y encuentre los @WebServlet
        File additionWebInfClasses = new File("target/classes");
        org.apache.catalina.WebResourceRoot resources = new org.apache.catalina.webresources.StandardRoot(ctx);
        resources.addPreResources(new org.apache.catalina.webresources.DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        // registrar el servlet de Jersey programáticamente
        String jerseyServletName = "jersey-container";
        var wrapper = Tomcat.addServlet(ctx, jerseyServletName, new ServletContainer());
        wrapper.addInitParameter("jersey.config.server.provider.packages", "recursos");
        wrapper.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/api/*", jerseyServletName);

        tomcat.start();
        System.out.println("Servidor arriba en http://localhost:8080/login?ruta=ingresar");
        tomcat.getServer().await();
    }
}