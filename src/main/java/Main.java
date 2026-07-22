package main.java;

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

        // registrar el servlet de Jersey programáticamente
        String jerseyServletName = "jersey-container";
        var wrapper = Tomcat.addServlet(ctx, jerseyServletName, new ServletContainer());
        wrapper.addInitParameter("jersey.config.server.provider.packages", "recursos");
        wrapper.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/api/*", jerseyServletName);

        tomcat.start();
        System.out.println("Servidor arriba en http://localhost:8080/api/");
        tomcat.getServer().await();
    }
}