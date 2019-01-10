<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<link href="${pageContext.request.contextPath}<spring:message code="stylepath"/>menu_files/fontawesome.min.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}<spring:message code="stylepath"/>menu_files/fade-down.css" rel="stylesheet" type="text/css" media="all" id="effect"/>
<link href="${pageContext.request.contextPath}<spring:message code="stylepath"/>menu_files/webslidemenu.css" rel="stylesheet" type="text/css" media="all"/>
  
<link href="${pageContext.request.contextPath}<spring:message code="stylepath"/>menu_files/grd-orange.css" rel="stylesheet" type="text/css" media="all" id="theme"/>

<SCRIPT src="${pageContext.request.contextPath}<spring:message code="stylepath"/>menu_files/webslidemenu.js" type="text/javascript"></SCRIPT>
<div class="container">
 <div class="row">
	 <!-- Mobile Header -->   
	 <DIV class="wsmobileheader clearfix "><A class="wsanimated-arrow" id="wsnavtoggle"><SPAN></SPAN></A> 
		<div class="row">
			<div class="col-12 application-panel">
				<span class="application">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="BW0.lbl.Application"/></span> 
				<span class="userinfo"><c:out value="${sessionScope.tscuserinfo.firstName}" />&nbsp;&nbsp;</span>
			</div>
		</div>
		<div class="row">
			<div class="col-12 screenid-panel">
				<span class="screenid">&nbsp;&nbsp;<c:out value="${payload.screenId} : ${payload.screenDescription}" />
				</span>
				<span class="language">	
					<a href="${_mappingPath}?language=th_TH">
						<input tabindex="-1" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>icons/th_TH.png" width="17" title="TH"/>
					</a>
					<a href="${_mappingPath}?language=en_US">
						<input tabindex="-1" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>icons/en_US.png" width="17" title="EN"/>
					</a>
					<a href="${_mappingPath}?language=ja_JP">
						<input tabindex="-1" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>icons/ja_JP.png" width="17"  title="JP"/>
					</a>&nbsp;&nbsp;
				</span>
			</div>
		</div>
	</DIV> 
	
	<!--Menu HTML Code--> 
	<DIV class="wsmainfull clearfix col-12">
	   <DIV class="wsmainwp clearfix">
	      <!--Main Menu HTML Code-->      
	   <NAV class="wsmenu clearfix">
	      <UL class="wsmenu-list">
	         <LI>
	         	<A class="active menuhomeicon" href="${pageContext.request.contextPath}">
	         	<I class="fa fa-home"></I><SPAN class="hometext">&nbsp;&nbsp;Home</SPAN>
	         	</A>
	         </LI>
	         <LI>
	            <A href="${pageContext.request.contextPath}/NewCarInsurance">New Car Insurance <SPAN class="wsarrow"></SPAN></A>
	            <DIV class="wsmegamenu clearfix">
	               <DIV class="container" style="width: 60%;margin-left: 50px;">
	                  <DIV class="row">
	                     <DIV class="col-lg-6 col-md-12 col-xs-12">
	                        <H3 class="title">New Car Insurance Portfolio Management</H3>
	                        <DIV >
	                        	<A href="${pageContext.request.contextPath}/Kaikieng/kaikiengInput">
	                        		<input tabindex="1" id="BW0100" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/New_Car_Insurance_Portfolio_Management.png" title="New Car Insurance Portfolio Management"/>
	                        	</A>
	                        </DIV>
	                     </DIV>
	                     <DIV class="col-lg-6 col-md-12 col-xs-12">
	                        <H3 class="title">New Car Insurance Activation Management</H3>
	                        <DIV>
	                        	<A href="${pageContext.request.contextPath}/master/vehiclePlant">
	                        		<input tabindex="1" id="BW0100" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/New_Car_Insurance.png" title="New Car Insurance Activation Management"/>
	                        	</A>
	                        </DIV>
	                     </DIV>
	                  </DIV>
	               </DIV>
	            </DIV>
	         </LI>
	         <LI>
	            <A href="${pageContext.request.contextPath}/InsuranceRenewal">Insurance Renewal <SPAN class="wsarrow"></SPAN></A>
	            <DIV class="wsmegamenu clearfix ">
	               <DIV class="container">
	                  <DIV class="row">
	                     <DIV class="col-lg-4 col-md-12 col-xs-12">
	                        <H3 class="title">Standard Renewal SOP & PIC Setting</H3>
	                        <DIV class="fluid-width-video-wrapper">
	                        	<A href="${_mappingPath}">
	                        		<input tabindex="1" id="BW0100" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/Standard_Renewal_SOP_PIC_Setting.png" title="Standard Renewal SOP & PIC Setting"/>
	                        	</A>
	                        </DIV>
	                     </DIV>
	                     <DIV class="col-lg-4 col-md-12 col-xs-12">
	                        <H3 class="title">Special Contact Setting</H3>
	                        <DIV class="fluid-width-video-wrapper">
	                        	<A href="${_mappingPath}">
	                        		<input tabindex="1" id="BW0100" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/Special_Contact_Setting.png" title="Special Contact Setting"/>
	                        	</A>
	                        </DIV>
	                     </DIV>
	                     <DIV class="col-lg-4 col-md-12 col-xs-12">
	                        <H3 class="title">Activities Reminder</H3>
	                        <DIV class="fluid-width-video-wrapper">
	                        <A href="${_mappingPath}">
	                        		<input tabindex="1" id="BW0100" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/Activities_Reminder.png" title="Activities Reminder"/>
	                        </A> </DIV>
	                     </DIV>
	                  </DIV>
	               </DIV>
	            </DIV>
	         </LI>
	         <LI>
	            <A href="${pageContext.request.contextPath}/InsuranceCompany">Insurance Company <SPAN class="wsarrow"></SPAN></A>
	            <UL class="sub-menu">
	               <LI><A href="${_mappingPath}"><I 
	                  class="fa fa-angle-right"></I>Website Design </A></LI>
	               <LI><A href="${_mappingPath}"><I 
	                  class="fa fa-angle-right"></I>Ecommerce Solutions</A></LI>
	               <LI><A href="${_mappingPath}"><I 
	                  class="fa fa-angle-right"></I>Application Development</A></LI>
	               <LI><A href="${_mappingPath}"><I 
	                  class="fa fa-angle-right"></I>Website Development</A></LI>
	               <LI>
	                  <A href="${_mappingPath}"><I 
	                     class="fa fa-angle-right"></I>Open Source Development</A>
	                  <UL class="sub-menu">
	                     <LI><A href="${_mappingPath}"><I 
	                        class="fa fa-angle-right"></I>Submenu item 1</A></LI>
	                     <LI><A href="${_mappingPath}"><I 
	                        class="fa fa-angle-right"></I>Submenu item 2</A></LI>
	                     <LI><A href="${_mappingPath}"><I 
	                        class="fa fa-angle-right"></I>Submenu item 3</A></LI>
	                     <LI><A href="${_mappingPath}"><I 
	                        class="fa fa-angle-right"></I>Submenu item 4</A></LI>
	                  </UL>
	               </LI>
	            </UL>
	         </LI>
	         <LI>
	            <A href="${pageContext.request.contextPath}/Management">Management <SPAN class="wsarrow"></SPAN></A>
	            <DIV class="wsmegamenu clearfix">
	               <DIV class="typography-text clearfix">
	                  <DIV class="container">
	                     <DIV class="row">
	                        <DIV class="col-lg-6 col-sm-12">
	                           <H3 class="title">This is another title</H3>
	                           <P>Lorem Ipsum is simply du
	                           </P>
	                        </DIV>
	                        <DIV class="col-lg-3 col-sm-12">
	                           <H3 class="title">This is another title</H3>
	                           <P>Contrary ichard McClintock, a Latin professor at Hampden-Sydney College in  
	                              Virginia.                       
	                           </P>
	                        </DIV>
	                        <DIV class="col-lg-3 col-sm-12">
	                           <H3 class="title">This is another title</H3>
	                           <P>Contrary to po                
	                           </P>
	                        </DIV>
	                     </DIV>
	                     <DIV class="row">
	                        <DIV class="cl"></DIV>
	                        <DIV class="col-lg-3 col-sm-12">
	                           <H3 class="title">Other Services</H3>
	                           <UL>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-wordpress"></I>Wordpress Development</A></LI>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-drupal"></I>Drupal Development</A></LI>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-shopping-cart"></I>Shoping Cart Development</A></LI>
	                           </UL>
	                        </DIV>
	                        <DIV class="col-lg-3 col-sm-12">
	                           <H3 class="title">More Services</H3>
	                           <UL>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-android"></I> Android App Development</A></LI>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-apple"></I>iPhone App Development</A></LI>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-windows"></I>Windows App Development</A></LI>
	                           </UL>
	                        </DIV>
	                        <DIV class="col-lg-3 col-sm-12">
	                           <H3 class="title">Other Products</H3>
	                           <UL>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-wordpress"></I>Wordpress Development</A></LI>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-drupal"></I>Drupal Development</A></LI>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-joomla"></I>Joomla Development</A></LI>
	                           </UL>
	                        </DIV>
	                        <DIV class="col-lg-3 col-sm-12">
	                           <H3 class="title">More Services</H3>
	                           <UL>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-android"></I> Android App Development</A></LI>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-mobile"></I>HTML5 App Development</A></LI>
	                              <LI><A href="${_mappingPath}"><I 
	                                 class="fa fa-paypal"></I>Paypal Store 
	                                 Integration</A>
	                              </LI>
	                           </UL>
	                        </DIV>
	                     </DIV>
	                  </DIV>
	               </DIV>
	            </DIV>
	         </LI>
	         <LI>
	            <A href="#">Common<SPAN class="wsarrow"></SPAN></A>
	            <UL class="sub-menu">
	               <LI><A href="${_mappingPath}">
	               	<I class="fa fa-angle-right"></I>System Master</A>
	               </LI>
	               <LI>
	                  <A href="${_mappingPath}"><I 
	                     class="fa fa-angle-right"></I>Monitoring</A>
	                  <UL class="sub-menu">
	                     <LI><A href="${_mappingPath}"><I 
	                        class="fa fa-angle-right"></I>Log Monitor</A></LI>
	                  </UL>
	               </LI>
	            </UL>
	         </LI>
	      </UL>
	   </NAV>
	   <!--Menu HTML Code--> 
	   </DIV>
	</DIV>
</div>
</div>