<#ftl strip_whitespace=true>
<#macro csrf>
    <#if csrfKey?has_content>
    <input type="hidden" name="${csrfKey}" value="${csrfValue}"/>
    </#if>
</#macro>