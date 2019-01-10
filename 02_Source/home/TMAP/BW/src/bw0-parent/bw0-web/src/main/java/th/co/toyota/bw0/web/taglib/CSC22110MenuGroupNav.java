package th.co.toyota.bw0.web.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import th.co.toyota.sc2.client.model.simple.CSC22110AccessControlList;

public class CSC22110MenuGroupNav extends SimpleTagSupport{
	final Logger logger = LoggerFactory.getLogger(CSC22110MenuGroupNav.class);
    private String groupId;
    private String screenId;
    private String title;
    private String ariaLabelledby;
    private Boolean isSecured;
    private Boolean isHaveSubMenu; 
    private String href;

    public CSC22110MenuGroupNav()
    {
        setSecured(Boolean.TRUE);
    }

    public void doTag()
        throws JspException, IOException
    {
        Boolean isRender = Boolean.FALSE;
        if(isSecured.booleanValue())
        {
            logger.debug("Group Id {} is a secured menu group", groupId);
            Object objAcl = getJspContext().findAttribute("tscsysacl");
            if(objAcl != null)
            {
                CSC22110AccessControlList acl = (CSC22110AccessControlList)objAcl;
                if(acl.getMapGroupId().containsKey(groupId))
                {
                    logger.debug("ACL of user allowed to render the Menu Group for Group Id {}.", groupId);
                    isRender = Boolean.TRUE;
                } else
                {
                    logger.error("ACL of user not allowed to render the Menu Group for Group Id {}.", groupId);
                }
            } else
            {
                logger.error("Unable to retrieve the ACL for this system.");
            }
        } else
        {
            isRender = Boolean.TRUE;
            logger.debug("Group Id {} is a non-secured menu group", groupId);
        }
        if(isRender.booleanValue())
        {
            logger.debug("Start to render the menu group for Group id {}.", groupId);
            StringWriter html = new StringWriter();
            try
            {
            	html.append("<li id=\"").append(groupId).append("\" class=\"nav-item dropdown\">");            	
            	if(this.isHaveSubMenu){
            		html.append("<a class=\"nav-link dropdown-toggle\" href=\"#\" id=\"").append(ariaLabelledby).append("\" role=\"button\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">");
            	}else{
            		if(!Strings.isNullOrEmpty(href)){
                    	ServletContext servletContext = ((PageContext) getJspContext()).getServletContext();
                    	String appContext = servletContext.getContextPath();
                    	html.append("<a class=\"nav-link\" href=\"").append(appContext).append("/").append(href).append("\" id=\"").append(screenId).append("\">");
                    }else{
                    	html.append("<a class=\"nav-link\" href=\"#\" id=\"").append(screenId).append("\">");
                    }
            	}
            	html.append("<span class=\"px-1\">").append(title).append("</span>");
            	html.append("</a> ");
            	if(this.isHaveSubMenu == Boolean.FALSE){
	            	html.append("<ul class=\"dropdown-menu\" aria-labelledby=\"").append(ariaLabelledby).append("\">");
	            	html.append("</ul> ");
            	}
                getJspBody().invoke(html);
            	html.append("</li>");
                getJspContext().getOut().println(html.toString());
            }
            catch(IOException ie)
            {
                logger.error("Exception occured when writing the menu group to JSP", ie);
                throw ie;
            }
        } else
        {
            logger.debug("Menu Group for function id {} will not be rendered.", groupId);
        }
    }

    public String getAriaLabelledby() {
		return ariaLabelledby;
	}

	public void setAriaLabelledby(String ariaLabelledby) {
		this.ariaLabelledby = ariaLabelledby;
	}

	public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }
    
    public String getScreenId()
    {
        return screenId;
    }

    public void setScreenId(String screenId)
    {
        this.screenId = screenId;
    }

    public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Boolean getSecured()
    {
        return isSecured;
    }

    public void setSecured(Boolean isSecured)
    {
        this.isSecured = isSecured;
        if(this.isSecured == null)
            this.isSecured = Boolean.TRUE;
    }
    
    public Boolean getHaveSubMenu()
    {
        return isHaveSubMenu;
    }

    public void setHaveSubMenu(Boolean isHaveSubMenu)
    {
        this.isHaveSubMenu = isHaveSubMenu;
        if(this.isHaveSubMenu == null)
            this.isHaveSubMenu = Boolean.FALSE;
    }
}
