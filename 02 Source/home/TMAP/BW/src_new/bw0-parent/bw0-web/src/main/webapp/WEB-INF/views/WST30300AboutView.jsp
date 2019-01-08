<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>

<views:script src="json2.js"/>
<views:script src="jquery-ui-1.9.1/jquery.dataTables.js"/>
<views:style src="jquery.dataTables.css"/>
<style type="text/css">
#enhancementDiv{
	overflow: auto;
	overflow-x: hidden;
	height:140px; 
	border-color: #EAEAEA;
	border-width: .2em; 
	border-style: solid; 
	padding: 5px;
}

</style>
<script>
function getEnhancement(versionId) {
	$.post( "${_mappingPath}/listEnhancement", { code:versionId}, function( data ) {
		$('#enhancementDiv').html(data.enhancement.replace(/\\n/g,'<br/>'));
	});
}
</script>
<div id="modal-detail" style="padding: 0em;">
	<div id="modal-body" style="padding: 0 5px 0 5px;">
		<input type="hidden" id="hddDetailFileName" name="hddDetailFileName" value="" />
		<input type="hidden" id="hddDetailFileNo" name="hddDetailFileNo" value="" />
		<input type="hidden" id="hddDetailDocId" name="hddDetailDocId" value="" />
		
		<table style="table-layout: fixed; width: 60%;font-size: small;">
			<tr>
				<td height="20px"></td>
			</tr>
			<tr>
				<td align="left"><span id="applicationWording">${fn:replace(about.appWording,"\\n","<br/>")}</span></td>
			</tr>
			<tr>
				<td height="20px"></td>
			</tr>
			<tr>
				<td align="left"><span id="applicationHelpDesk">${fn:replace(about.helpDeskInfo,"\\n","<br/>")}</span></td>
			</tr>
			<c:if test="${about.currentAppVer != null}">
			<tr>
				<td height="8px"></td>
			</tr>
			<tr>
				<td height="2px" style="background-color: #EAEAEA;"></td>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
			<tr>
				<td align="left">
					<span id="applicationVersion"><spring:message code="ST3.WST30300.Label.CurrentVersion" /> : ${about.currentAppVer}</span>
				</td>
			</tr>
			<tr>
				<td height="8px"></td>
			</tr>
			<tr>
				<td height="2px" style="background-color: #EAEAEA;"></td>
			</tr>
			</c:if>
			<tr>
				<td height="10px"></td>
			</tr>
			<tr>
				<td align="left"><span id="applicationStdVersion">
				<c:if test="${about.currentStdLibVer != null}">
					<spring:message code="ST3.WST30300.Label.CurrentStdVersion" /> : ${about.currentStdLibVer}<br/>
				</c:if>
				<c:if test="${about.currentStdBatchVer != null}">
					<spring:message code="ST3.WST30300.Label.CurrentBatchVersion" /> : ${about.currentStdBatchVer}<br/>
				</c:if>
				<c:if test="${about.currentSecCenterVer != null}">
					<spring:message code="ST3.WST30300.Label.CurrentSecCenterVersion" /> : ${about.currentSecCenterVer}</span></td>
				</c:if>
			</tr>
			<c:if test="${not empty about.versions}">
			<tr>
				<td height="8px"></td>
			</tr>
			<tr>
				<td height="2px" style="background-color: #EAEAEA;"></td>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
			<tr>
				<td>
					<table style="width: 100%;">
						<tr>
							<td colspan="4" style="background-color: #EAEAEA;padding-left: 5px;" align="left" height="20px">
								<b><spring:message code="ST3.WST30300.Label.listEnhancement" /></b>
							</td>
						</tr>
						<tr>
							<td colspan="4" height="20px"></td>
						</tr>
						<tr valign="top">
							<td width="7%"></td>
							<td width="20%">
								<b><spring:message code="ST3.WST30300.Label.Version" /></b>&nbsp;&nbsp;
								<select id="versions" name="versions" style="width: 50px;" onchange="getEnhancement(this.value)">
									<c:forEach items="${about.versions}" var="version">
										<option value="${version}" ${fn:contains(about.currentAppVer, version) ? 'selected' : ''}>${version}</option>
									</c:forEach>
								</select>
							</td>
							<td width="*"><div id="enhancementDiv">${fn:replace(about.currentEnhancement,"\\n","<br/>")}</div></td>
							<td width="7%"></td>
						</tr>
					</table>
				</td>
			</tr>
			</c:if>
			<tr>
				<td height="20px"></td>
			</tr>
			<!-- 
			<tr>
				<td align="right" style="padding-top: 5px;">
					<spring:message code="ST3.WST30300.Label.BtnOk" var="WST30300Ok"/>	
					<sc2:button functionId="ST3302" 
										screenId="WST30300" 
										buttonId="WST30300Ok" 
										type="button" 
										value="${WST30300Ok}" 
										style="width:80px;" 
										secured="false"
										onClick="doCancelDetail();" />
				</td>
			</tr>
			-->
		</table>
		
		
	</div>
</div>