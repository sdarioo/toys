package config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServletContainerInitializer
	extends AbstractAnnotationConfigDispatcherServletInitializer
{


    @Override
    protected String getServletName()
    {
        return "demo";
    }
	
    @Override
    protected Class<?>[] getRootConfigClasses()
    {
        return new Class[]
        {
            AppConfig.class
        };
    }

    /**
     * SpringMVC configuration.
     */
    @Override
    protected Class<?>[] getServletConfigClasses()
    {
        return new Class[]
        {
            MvcConfig.class
        };
    }

    @Override
    protected String[] getServletMappings()
    {
        return new String[]
        {
            "/"
        };
    }
	

}
