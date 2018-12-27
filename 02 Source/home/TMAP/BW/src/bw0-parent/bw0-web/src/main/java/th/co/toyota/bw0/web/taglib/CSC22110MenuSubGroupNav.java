package th.co.toyota.bw0.web.taglib;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import th.co.toyota.sc2.client.model.simple.CSC22110AccessControlList;

public class CSC22110MenuSubGroupNav extends SimpleTagSupport
{
	final Logger logger = LoggerFactory.getLogger(CSC22110MenuSubGroupNav.class);
    private String subGroupId;
    private String title;
    private Boolean isSecured;
    private String style;

    public CSC22110MenuSubGroupNav()
    {
        setSecured(Boolean.TRUE);
    }

    public void doTag()
        throws JspException, IOException
    {
        Boolean isRender = Boolean.FALSE;
        if(isSecured.booleanValue())
        {
            logger.debug("Sub Group Id {} is a secured menu subgroup", subGroupId);
            Object objAcl = getJspContext().findAttribute("tscsysacl");
            if(objAcl != null)
            {
                CSC22110AccessControlList acl = (CSC22110AccessControlList)objAcl;
                if(acl.getMapSubGroupId().containsKey(subGroupId))
                {
                    logger.debug("ACL of user allowed to render the menu subgroup for Sub Group Id {}.", subGroupId);
                    isRender = Boolean.TRUE;
                } else
                {
                    logger.error("ACL of user not allowed to render the menu subgroup for Sub Group Id {}.", subGroupId);
                }
            } else
            {
                logger.error("Unable to retrieve the ACL for this system.");
            }
        } else
        {
            isRender = Boolean.TRUE;
            logger.debug("Sub Group Id {} is a non-secured menu subgroup", subGroupId);
        }
        if(isRender.booleanValue())
        {
            logger.debug("Start to render the menu subgroup for Sub Group Id {}.", subGroupId);
            StringWriter html = new StringWriter();
            try
            {
            	html.append("<li id=\"").append(subGroupId).append("\" class=\"dropdown-submenu\">");
            	html.append("<a class=\"");
            	if(!Strings.isNullOrEmpty(style))
                    html.append(style);
                else
                    html.append("dropdown-item dropdown-toggle");
            	
            	html.append("\" href=\"#\">").append(title).append("</a>");
            	
            	getJspBody().invoke(html);
                html.append("</li>");
                getJspContext().getOut().println(html.toString());
            }
            catch(IOException ie)
            {
                logger.error("Exception occured when writing the menu subgroup to JSP", ie);
                throw ie;
            }
        } else
        {
            logger.debug("Menu subgroup for Sub Group Id {} will not be rendered.", subGroupId);
        }
    }

    public String getSubGroupId()
    {
        return subGroupId;
    }

    public void setSubGroupId(String subGroupId)
    {
        this.subGroupId = subGroupId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Boolean getSecured()
    {
        return isSecured;
    }

    public void setSecured(Boolean isSecured)
    {
        this.isSecured = isSecured;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }


}