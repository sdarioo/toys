package config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServletContainerInitializer
	extends AbstractAnnotationConfigDispatcherServletInitializer
{


    @Override
    protected String getServletName()
    {
        return "spring-mvc";
    }
	
    @Override
    protected Class<?>[] getRootConfigClasses()
    {
        return new Class[]
        {
            ApplicationContext.class
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
            SpringMVCServletContext.class
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
