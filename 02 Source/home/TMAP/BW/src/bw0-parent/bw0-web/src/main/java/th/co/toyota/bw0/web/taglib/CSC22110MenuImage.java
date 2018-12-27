package th.co.toyota.bw0.web.taglib;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import th.co.toyota.sc2.client.model.simple.CSC22110AccessControlList;

public class CSC22110MenuImage extends SimpleTagSupport
{
	final Logger logger = LoggerFactory.getLogger(CSC22110MenuImage.class);
	
	private String type;
	private String groupId;
	private String subGroupId;
    private String screenId;
    private String title;
    private Boolean isSecured;
    private String style;
    private String styleColDiv;
    private String href;
    private String src;
    private String dataBadge;
    private String badgeStyle;
    
    private String id;
    
    public CSC22110MenuImage()
    {
        setSecured(Boolean.TRUE);
    }

    public void doTag()
        throws JspException, IOException
    {
        Boolean isRender = Boolean.FALSE;
        
        String label1 = "Screen";
        String label2 = "screen";
        String label3 = "Screen";
        id = this.screenId;
        
        if(type.equalsIgnoreCase("groupId")){
        	id = this.groupId;
        	label1 = "Group";
        	label2 = "group";
        	label3 = "Group";
        }else if(type.equalsIgnoreCase("subGroupId")){
        	id = this.subGroupId;
        	label1 = "Sub Group";
        	label2 = "subgroup";
        	label3 = "Sub Group";
        }
        
        
        if(isSecured.booleanValue())
        {
            logger.debug(label1+" Id {} is a secured menu "+label2+"", id);
            Object objAcl = getJspContext().findAttribute("tscsysacl");
            if(objAcl != null)
            {
                CSC22110AccessControlList acl = (CSC22110AccessControlList)objAcl;
                boolean allowGenMenu = false;
                if(type.equalsIgnoreCase("groupId")){
                	if(acl.getMapGroupId().containsKey(id))
                		allowGenMenu = true;
                }else if(type.equalsIgnoreCase("subGroupId")){
                	if(acl.getMapSubGroupId().containsKey(id))
                		allowGenMenu = true;
                }else{
                	if(acl.getMapScreenId().containsKey(id))
                		allowGenMenu = true;
                }
                
                if(allowGenMenu)
                {
                    logger.debug("ACL of user allowed to render the menu "+label2+" for "+label3+" Id {}.", id);
                    isRender = Boolean.TRUE;
                } else
                {
                    logger.error("ACL of user not allowed to render the menu "+label2+" for "+label3+" Id {}.", id);
                }
            } else
            {
                logger.error("Unable to retrieve the ACL for this system.");
            }
        } else
        {
            isRender = Boolean.TRUE;
            logger.debug(label1+" Id {} is a non-secured menu "+label2+"", id);
        }
        if(isRender.booleanValue())
        {
            logger.debug("Start to render the menu "+label2+" for "+label3+" Id {}.", id);
            StringWriter html = new StringWriter();
            try
            {
        		if(!Strings.isNullOrEmpty(styleColDiv)){
        			html.append("<div class=\"").append(styleColDiv).append("\">");
        		}else{
                    html.append("<div class=\"pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6\">");
        		}
        		ServletContext servletContext = ((PageContext) getJspContext()).getServletContext();
            	String appContext = servletContext.getContextPath();
            	
            	html.append("<a href=\"").append(appContext).append("/").append(href).append("\" ");
            	
            	if(!Strings.isNullOrEmpty(dataBadge))
                    html.append(" data-badge=\"").append(dataBadge).append("\" ");
            	
            	if(!Strings.isNullOrEmpty(badgeStyle))
                    html.append(" class=\"").append(badgeStyle).append("\" ");
            	
            	html.append(">");
            	
            	html.append("<input id=\"").append(id).append("\" type=\"image\" src=\"").append(appContext).append("/").append(src).append("\" title=\"").append(title).append("\"/>");
            	html.append("</a>");
            	html.append("<div>");
            	if(!Strings.isNullOrEmpty(style)){
            		html.append("<label class=\"").append(style).append("\">").append(title).append("</label>");
            	}else{
            		html.append("<label class=\"my-1 textMenuImage\">").append(title).append("</label>");
            	}
            	
            	html.append("</div>");
      
        		getJspBody().invoke(html);
                html.append("</div>");
                getJspContext().getOut().println(html.toString());

            }
            catch(IOException ie)
            {
                logger.error("Exception occured when writing the menu "+label2+" to JSP", ie);
                throw ie;
            }
        } else
        {
            logger.debug("Menu "+label2+" for "+label2+" Id {} will not be rendered.", id);
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

	public String getStyleColDiv() {
		return styleColDiv;
	}

	public void setStyleColDiv(String styleColDiv) {
		this.styleColDiv = styleColDiv;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getScreenId() {
		return screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDataBadge() {
		return dataBadge;
	}

	public void setDataBadge(String dataBadge) {
		this.dataBadge = dataBadge;
	}

	public String getBadgeStyle() {
		return badgeStyle;
	}

	public void setBadgeStyle(String badgeStyle) {
		this.badgeStyle = badgeStyle;
	}

}