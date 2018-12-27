package th.co.toyota.bw0.web.taglib;

import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import th.co.toyota.sc2.client.model.simple.CSC22110AccessControlList;

public class CSC22110MenuItemNav extends SimpleTagSupport{
	final Logger logger = LoggerFactory.getLogger(CSC22110MenuItemNav.class);
    private String screenId;
    private Boolean isSecured;
    private String style;
    private String href;
    private String onClick;
    private String target;
    private String dataBadge;

    public CSC22110MenuItemNav()
    {
        setSecured(Boolean.TRUE);
    }

    public void doTag()
        throws JspException, IOException
    {
        Boolean isRender = Boolean.FALSE;
        if(isSecured.booleanValue())
        {
            logger.debug("Screen Id {} is a secured menu", screenId);
            Object objAcl = getJspContext().findAttribute("tscsysacl");
            if(objAcl != null)
            {
                CSC22110AccessControlList acl = (CSC22110AccessControlList)objAcl;
                if(acl.getMapScreenId().containsKey(screenId))
                {
                    logger.debug("ACL of user allowed to render the Menu for Screen Id {}.", screenId);
                    isRender = Boolean.TRUE;
                } else
                {
                    logger.error("ACL of user not allowed to render the Menu for Screen Id {}.", screenId);
                }
            } else
            {
                logger.error("Unable to retrieve the ACL for this system.");
            }
        } else
        {
            isRender = Boolean.TRUE;
            logger.debug("Screen Id {} is a non-secured menu", screenId);
        }
        if(isRender.booleanValue())
        {
            logger.debug("Start to render the menu for Screen id {}.", screenId);
            StringWriter html = new StringWriter();
            try
            {
            	html.append("<li>");
            	html.append("<a id=\"").append(screenId).append("\"");
                html.append(" class=\"");
                if(!Strings.isNullOrEmpty(style))
                    html.append(style).append("\"");
                else
                    html.append("dropdown-item").append("\"");
                
                if(!Strings.isNullOrEmpty(dataBadge))
                    html.append(" data-badge=\"").append(dataBadge).append("\" ");
                
                if(!Strings.isNullOrEmpty(href)){
                	ServletContext servletContext = ((PageContext) getJspContext()).getServletContext();
                	String appContext = servletContext.getContextPath();
                	html.append("href=\"").append(appContext).append("/").append(href).append("\"");
                }
                if(!Strings.isNullOrEmpty(onClick))
                    html.append("onclick=\"").append(onClick).append("\"");
                if(!Strings.isNullOrEmpty(target))
                    html.append("target=\"").append(target).append("\"");
                html.append(">");
                getJspBody().invoke(html);
                html.append("</a>");
                html.append("</li>");
                getJspContext().getOut().println(html.toString());
            }
            catch(IOException ie)
            {
                logger.error("Exception occured when writing the button to JSP", ie);
                throw ie;
            }
        } else
        {
            logger.debug("Menu for function id {} will not be rendered.", screenId);
        }
    }

    public String getScreenId()
    {
        return screenId;
    }

    public void setScreenId(String screenId)
    {
        this.screenId = screenId;
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

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public String getDataBadge() {
		return dataBadge;
	}

	public void setDataBadge(String dataBadge) {
		this.dataBadge = dataBadge;
	}

	public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }

    public String getOnClick()
    {
        return onClick;
    }

    public void setOnClick(String onClick)
    {
        this.onClick = onClick;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

}
