<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  Markdown 编辑器组件
  参数说明：
  - id: 编辑器容器ID (必填)
  - name: 表单字段名 (必填)
  - content: 初始内容 (可选)
  - width: 宽度 (默认: 100%)
  - height: 高度 (默认: 500px)
  - imageUploadURL: 图片上传地址 (可选)
  - placeholder: 占位文本 (可选)
--%>

<div id="${param.id}" class="markdown-editor">
    <textarea style="display:none;" name="${param.name}"><c:out value="${param.content}" escapeXml="true"/></textarea>
</div>

<script type="text/javascript">
//<![CDATA[
// 使用jQuery的noConflict模式
(function($) {
    // 检查依赖
    if (typeof editormd === 'undefined') {
        console.error('Editor.md is required for the markdown editor');
        return;
    }

    $(function() {
        var editorId = '${param.id}';
        
        // 基础配置
        var config = {};
        config.width = '${empty param.width ? "100%" : param.width}';
        config.height = '${empty param.height ? "500" : param.height}';
        config.path = '${pageContext.request.contextPath}/resources/lib/editor.md/lib/';
        config.saveHTMLToTextarea = ${empty param.saveHTMLToTextarea ? true : param.saveHTMLToTextarea};
        config.placeholder = '${empty param.placeholder ? "\u8bf7\u8f93\u5165\u5185\u5bb9..." : fn:escapeXml(param.placeholder)}';
        config.toolbarIcons = [
            'undo', 'redo', '|',
            'bold', 'del', 'italic', 'quote', '|',
            'h1', 'h2', 'h3', 'h4', 'h5', 'h6', '|',
            'list-ul', 'list-ol', 'hr', '|',
            'link', 'image', 'code', 'preformatted-text', 'code-block', 'table', '|',
            'watch', 'preview', 'fullscreen'
        ];
        
        // 图片上传配置
        var imageUploadURL = '${param.imageUploadURL}';
        if (imageUploadURL && imageUploadURL.trim() !== '') {
            config.imageUpload = true;
            config.imageFormats = ['jpg', 'jpeg', 'gif', 'png', 'bmp', 'webp'];
            config.imageUploadURL = imageUploadURL;
        }
        
        try {
            // 初始化编辑器
            window['editor_' + editorId] = editormd(editorId, config);
        } catch (e) {
            console.error('Failed to initialize markdown editor:', e);
        }
    });

})(jQuery);
//]]>
</script>
